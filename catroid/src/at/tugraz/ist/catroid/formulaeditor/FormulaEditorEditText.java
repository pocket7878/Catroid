/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.formulaeditor;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import at.tugraz.ist.catroid.ui.dialogs.FormulaEditorDialog;

public class FormulaEditorEditText extends EditText implements OnTouchListener {

	//	private static final BackgroundColorSpan COLOR_EDITING = new BackgroundColorSpan(0xFF00FFFF);
	private static final BackgroundColorSpan COLOR_ERROR = new BackgroundColorSpan(0xFFF00000);
	private static final BackgroundColorSpan COLOR_HIGHLIGHT = new BackgroundColorSpan(0xFFFFFF00);

	public static final String[] GROUP_NUMBERS = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "." };
	public static final String[] GROUP_OPERATORS = new String[] { "+", "-", "*", "/", "^" };
	public static final String[] GROUP_FUNCTIONS = new String[] { "sin", "cos", "tan", "ln", "log", "sqrt", "rand",
			"abs", "round" }; //only functions with brackets in here plz!!!

	public static final int NUMBER = 0;
	public static final int OPERATOR = 1;
	public static final int FUNCTION_SEPERATOR = 2;
	public static final int FUNCTION = 3;
	public static final int BRACKET_CLOSE = 4;
	public static final int BRACKET_OPEN = 5;
	public static final int SENSOR_VALUE = 6;

	public CatKeyboardView catKeyboardView;
	private int selectionStartIndex = 0;
	private int selectionEndIndex = 0;

	private boolean editMode = false;
	private Spannable highlightSpan = null;
	private Spannable errorSpan = null;
	private int numberOfVisibleLines = 0;
	private float lineHeight = 0;
	private int absoluteCursorPosition = 0;
	private static FormulaEditorHistory history = null;

	FormulaEditorDialog formulaEditorDialog = null;

	public FormulaEditorEditText(Context context) {
		super(context);
	}

	public FormulaEditorEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FormulaEditorEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void init(FormulaEditorDialog dialog, int brickHeight, CatKeyboardView ckv, Context context) {
		this.formulaEditorDialog = dialog;
		this.setOnTouchListener(this);
		this.setLongClickable(false);
		this.setSelectAllOnFocus(false);
		//this.setEnabled(false);
		//this.setBackgroundColor(getResources().getColor(R.color.transparent));
		this.catKeyboardView = ckv;
		this.setCursorVisible(false);

		//setText(getText(), selectable ? BufferType.SPANNABLE : BufferType.NORMAL);
		if (brickHeight < 100) { //this height seems buggy for some high bricks, still need it...
			numberOfVisibleLines = 7;
			this.setLines(numberOfVisibleLines);
		} else if (brickHeight < 200) {
			numberOfVisibleLines = 6;
			this.setLines(numberOfVisibleLines);
		} else {
			numberOfVisibleLines = 4;
			this.setLines(numberOfVisibleLines);
		}

	}

	public void enterNewFormula(String formulaAsText) {
		//setEnabled(true);
		setText(formulaAsText);
		//formulaSaved();
		//		absoluteCursorPosition = formulaAsText.length();
		//		setSelection(absoluteCursorPosition - 1);
		//		selectionStartIndex = 0;
		//		selectionEndIndex = formulaAsText.length();
		//		highlightSelection();
		quickSelect();
		editMode = true;

		if (history == null) {
			history = new FormulaEditorHistory(formulaAsText, absoluteCursorPosition, selectionStartIndex,
					selectionEndIndex);
		} else {
			history.clear();
		}
	}

	public void setInputTextAndPosition(String formulaAsText, int cursorPosition, int selectionStart, int selectionEnd) {
		setText(formulaAsText);
		absoluteCursorPosition = cursorPosition;
		setSelection(cursorPosition);
		selectionStartIndex = selectionStart;
		selectionEndIndex = selectionEnd;
		if (selectionStartIndex < selectionEndIndex) {
			highlightSelection();
			editMode = true;
		}
	}

	public boolean restoreFieldFromPreviousHistory() {
		FormulaEditorHistoryElement currentState = history.getCurrentState();
		if (currentState == null) {
			return false;
		}
		setInputTextAndPosition(currentState.text, currentState.cursorPosition, currentState.selectionStart,
				currentState.selectionEnd);
		//setEnabled(true);
		formulaEditorDialog.makeUndoButtonClickable(history.undoIsPossible());
		formulaEditorDialog.makeRedoButtonClickable(history.redoIsPossible());
		if (history.hasUnsavedChanges()) {
			formulaEditorDialog.makeOkButtonSaveButton();
		}
		return true;
	}

	public synchronized void updateSelectionIndices() {
		clearSelectionHighlighting();
		editMode = false;

		selectionStartIndex = absoluteCursorPosition;
		selectionEndIndex = absoluteCursorPosition;
		//setSelection(selectionStartIndex);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Layout layout = this.getLayout();
		float scrollOffset = this.getScrollY();
		float horizontalOffset = 0;
		int verticalOffset = 0;
		float betweenLineOffset = 0;

		if (layout != null && getText().length() > 0) {

			if (absoluteCursorPosition > getText().length()) { // fix for landscape switches
				absoluteCursorPosition = getText().length() - 1;
			}

			lineHeight = layout.getSpacingMultiplier() * this.getTextSize() + 5;
			horizontalOffset = layout.getPrimaryHorizontal(absoluteCursorPosition);
			verticalOffset = layout.getLineForOffset(absoluteCursorPosition);
			verticalOffset -= (int) (scrollOffset / lineHeight);
			betweenLineOffset = scrollOffset % lineHeight;
		}

		float startX = horizontalOffset;
		float endX = horizontalOffset;
		float startY = (5 + scrollOffset + lineHeight * verticalOffset) - betweenLineOffset;
		float endY = (5 + scrollOffset + lineHeight * (verticalOffset + 1)) - betweenLineOffset;
		canvas.drawLine(startX, startY, endX, endY, getPaint());

	}

	public synchronized void doSelectionAndHighlighting() {
		//Log.i("info", "do Selection and highlighting, cursor position: " + cursor pos);
		Editable currentInput = this.getText();

		if (currentInput.length() < 1) {
			return;
		}

		if (absoluteCursorPosition <= 0) {
			selectionStartIndex = 1;
			selectionEndIndex = 1;
		} else {
			selectionStartIndex = absoluteCursorPosition;
			selectionEndIndex = absoluteCursorPosition;
		}

		char currentChar = currentInput.charAt(selectionStartIndex - 1); //always selecting the char before the current cursor position!
		if (currentChar == '_') {
			selectionStartIndex--;
		}
		if ((currentChar == '(') || (currentChar == ',') || (currentChar == ')')) {
			selectionStartIndex--;
		} else {
			while (selectionStartIndex > 0) {
				currentChar = currentInput.charAt(selectionStartIndex - 1);
				if (!charIsLowerCaseLetter(currentChar) && !charIsCapitalLetter(currentChar)) {
					break;
				}
				selectionStartIndex--;
			}
		}

		while (selectionEndIndex < currentInput.length()) {
			currentChar = currentInput.charAt(selectionEndIndex - 1);
			if (!charIsLowerCaseLetter(currentChar) && !charIsCapitalLetter(currentChar)) {
				break;
			}
			selectionEndIndex++;
		}

		editMode = true;
		checkSelectedTextType();
		highlightSelection();

	}

	private void checkSelectedTextType() {

		int currentlySelectedElementType = getSelectedType(getText()
				.subSequence(selectionStartIndex, selectionEndIndex).toString());
		//Log.i("info", "FEEditText: check selected Type "
		//		+ getText().subSequence(selectionStartIndex, selectionEndIndex).toString() + " "
		//		+ currentlySelectedElementType);
		if (currentlySelectedElementType == FUNCTION) {
			extendSelectionBetweenBracketsFromOpenBracket();
		} else if (currentlySelectedElementType == BRACKET_CLOSE) {
			extendSelectionBetweenBracketsFromCloseBracket();
			extendSelectionForFunctionName();
		} else if (currentlySelectedElementType == BRACKET_OPEN) {
			extendSelectionForFunctionName();
			extendSelectionBetweenBracketsFromOpenBracket();
		} else if (currentlySelectedElementType == FUNCTION_SEPERATOR) {
			extendSelectionForFunctionOnSeperator();
		} else if (currentlySelectedElementType == SENSOR_VALUE) {
		} else {

			extendSelectionForNumber();
		}
	}

	public int getSelectedType(String currentlySelectedElement) {
		//Log.i("info", currentlySelectedElement + " start: " + selectionStartIndex + " end: " + selectionEndIndex);

		if (currentlySelectedElement.contains(",")) {
			return FUNCTION_SEPERATOR;

		} else if (currentlySelectedElement.contains(")")) {
			return BRACKET_CLOSE;
		} else if (currentlySelectedElement.contains("(")) {
			return BRACKET_OPEN;
		} else if (currentlySelectedElement.contains("_")) {
			return SENSOR_VALUE;
		}
		for (String item : GROUP_FUNCTIONS) {
			if (currentlySelectedElement.startsWith(item)) {
				return FUNCTION;
			}
		}
		for (String item : GROUP_OPERATORS) {
			if (currentlySelectedElement.contains(item)) {
				return OPERATOR;
			}
		}
		return NUMBER;

	}

	public void extendSelectionBetweenBracketsFromOpenBracket() {
		int bracketCount = 1;
		Editable text = getText();
		selectionEndIndex++;
		int textLength = text.length();

		while (selectionEndIndex < textLength && bracketCount > 0) {
			if (text.charAt(selectionEndIndex) == '(') {
				bracketCount++;
			} else if (text.charAt(selectionEndIndex) == ')') {
				bracketCount--;
			}
			selectionEndIndex++;
		}
	}

	public void extendSelectionBetweenBracketsFromCloseBracket() {
		int bracketCount = 1;
		Editable text = getText();
		selectionStartIndex--;

		while (selectionStartIndex > 0 && bracketCount > 0) {
			if (text.charAt(selectionStartIndex) == '(') {
				bracketCount--;
			} else if (text.charAt(selectionStartIndex) == ')') {
				bracketCount++;
			}
			selectionStartIndex--;
		}
	}

	public void extendSelectionForFunctionOnSeperator() {
		extendSelectionBetweenBracketsFromCloseBracket();
		extendSelectionForFunctionName();
		extendSelectionBetweenBracketsFromOpenBracket();
	}

	public void extendSelectionForFunctionName() {
		Editable currentInput = getText();
		char currentChar;

		while (selectionStartIndex > 0) {
			currentChar = currentInput.charAt(selectionStartIndex - 1);
			if (!charIsLowerCaseLetter(currentChar)) {
				break;
			}
			selectionStartIndex--;
		}
	}

	public void extendSelectionForNumber() {
		String currentInput = getText().toString();
		char currentChar;

		while (selectionStartIndex > 0) {
			currentChar = currentInput.charAt(selectionStartIndex - 1);
			if (!((charIsNumber(currentChar)) || (currentChar) == '.')) {
				break;
			}
			selectionStartIndex--;
		}

		while (selectionEndIndex < currentInput.length()) {
			currentChar = currentInput.charAt(selectionEndIndex);
			if (!((charIsNumber(currentChar)) || (currentChar) == '.')) {
				break;
			}
			selectionEndIndex++;
		}
	}

	public boolean charIsLowerCaseLetter(char letter) {
		if (letter >= 97 && letter <= 123) { //ASCII a...z
			return true;
		}
		return false;
	}

	public boolean charIsCapitalLetter(char letter) {
		if (letter >= 65 && letter <= 91) { //ASCII A...Z
			return true;
		}
		return false;
	}

	public boolean charIsNumber(char letter) {
		if (letter >= 48 && letter <= 58) { //ASCII 0...9
			return true;
		}
		return false;
	}

	public void highlightSelection() {
		highlightSpan = this.getText();
		highlightSpan.removeSpan(COLOR_HIGHLIGHT);

		if (selectionStartIndex < 0) {
			selectionStartIndex = 0;
		}

		if (selectionEndIndex == selectionStartIndex || selectionEndIndex > highlightSpan.length()) {
			return;
		}

		highlightSpan.setSpan(COLOR_HIGHLIGHT, selectionStartIndex, selectionEndIndex,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	public void clearSelectionHighlighting() {
		highlightSpan = this.getText();
		highlightSpan.removeSpan(COLOR_HIGHLIGHT);
		highlightSpan.removeSpan(COLOR_ERROR);
	}

	public void highlightParseError(int firstError) {
		clearSelectionHighlighting();
		errorSpan = this.getText();
		//Log.i("info", "First error: " + firstError);
		if (errorSpan.length() <= 1 || firstError == 0) {
			if (errorSpan.length() == 0) {
				append(" ");
			}
			errorSpan.setSpan(COLOR_ERROR, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			absoluteCursorPosition = 0;
			selectionStartIndex = 0;
			selectionEndIndex = 1;
			editMode = true;
			return;
		}

		if (firstError < errorSpan.length()) {
			editMode = (charIsLowerCaseLetter(errorSpan.charAt(firstError)) || charIsCapitalLetter(errorSpan
					.charAt(firstError))) ? false : true;
			selectionStartIndex = firstError;
			errorSpan.setSpan(COLOR_ERROR, firstError, ++firstError, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			selectionEndIndex = firstError;
		} else {
			Log.i("info", "ELSE!!!");
			editMode = (charIsLowerCaseLetter(errorSpan.charAt(firstError - 1)) || charIsCapitalLetter(errorSpan
					.charAt(firstError - 1))) ? false : true;
			errorSpan.setSpan(COLOR_ERROR, firstError - 1, firstError, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			selectionStartIndex = firstError - 1;
			selectionEndIndex = firstError;
		}

		setSelection(firstError);
		absoluteCursorPosition = firstError;
		//		if (errorSpan.length() > firstError) {
		//			char firstLetter = errorSpan.charAt(firstError);
		//
		//			//selection for characters always selects character before current cursor position!
		//			if (charIsCapitalLetter(firstLetter) || charIsLowerCaseLetter(firstLetter)) {
		//				absoluteCursorPosition++;
		//			}
		//			if (firstLetter == ')') {
		//				absoluteCursorPosition++;
		//			}
		//
		//		}
		//
		//		doSelectionAndHighlighting();
		//
		//		if (selectionEndIndex == selectionStartIndex) {
		//			if (selectionEndIndex == errorSpan.length()) {
		//				selectionStartIndex--;
		//			} else {
		//				selectionEndIndex++;
		//			}
		//
		//		}
		//errorSpan.setSpan(COLOR_ERROR, selectionStartIndex, selectionEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	public void checkAndModifyKeyInput(CatKeyEvent catKey) {

		String newElement = null;
		if (catKey.getKeyCode() == CatKeyEvent.KEYCODE_COMMA) {
			newElement = ".";
		} else {
			newElement = "" + catKey.getDisplayLabelString();
		}

		clearSelectionHighlighting();

		String text = getText().toString();
		if (absoluteCursorPosition > 0 && absoluteCursorPosition < text.length() && !editMode) {

			char currentChar = text.charAt(absoluteCursorPosition);
			char charBefore = text.charAt(absoluteCursorPosition - 1);

			//when the user tries to write into a function/variable/sensor, we delete it!
			if ((((charIsCapitalLetter(currentChar) || charIsLowerCaseLetter(currentChar) || (currentChar == '_') || (currentChar == '('))) && ((charIsCapitalLetter(charBefore) || charIsLowerCaseLetter(charBefore)
					&& charBefore != '_')))) {
				doSelectionAndHighlighting();
				editMode = true;
				if (!(catKey.getKeyCode() == KeyEvent.KEYCODE_DEL)) {
					return;
				}
			}
		}

		if (catKey.getKeyCode() == KeyEvent.KEYCODE_DEL) {
			deleteOneCharAtCurrentPosition();
		} else {
			appendToTextFieldAtCurrentPosition(newElement);
		}

		formulaEditorDialog.makeOkButtonSaveButton();

		//absoluteCursorPosition = selectionEndIndex;
		//updateSelectionIndices();

		history.push(getText().toString(), absoluteCursorPosition, absoluteCursorPosition, absoluteCursorPosition);
		formulaEditorDialog.makeUndoButtonClickable(true);

		//Log.i("info", "Cursor Pos: " + absoluteCursorPosition);

	}

	public void deleteOneCharAtCurrentPosition() {
		Editable text = getText();

		if (selectionEndIndex < 1) {
			return;
		}

		if (editMode) {
			text.replace(selectionStartIndex, selectionEndIndex, "");
			selectionEndIndex = selectionStartIndex;
			editMode = false;
		} else {
			char currentChar = text.charAt(selectionEndIndex - 1);
			if (currentChar == ',' || currentChar == ')' || currentChar == '(' || currentChar == '_') {
				doSelectionAndHighlighting();
				text.replace(selectionStartIndex, selectionEndIndex, "");
				selectionEndIndex = selectionStartIndex;
				//return;
				//			} else if () {
				//				doSelectionAndHighlighting();
				//				return;
				//			} else if () {
				//				doSelectionAndHighlighting();
				//				return;
				//			} else if () {
				//				doSelectionAndHighlighting();
				//				return;
				//			} else if (charIsCapitalLetter(currentChar) || charIsLowerCaseLetter(currentChar)) {
				//				doSelectionAndHighlighting();
				//				return;
			} else {
				text.replace(selectionEndIndex - 1, selectionEndIndex, "");
				selectionEndIndex--;
				selectionStartIndex = selectionEndIndex;
			}
		}

		setText(text);
		setSelection(selectionEndIndex);
		absoluteCursorPosition = selectionEndIndex;
	}

	private void appendToTextFieldAtCurrentPosition(String newElement) {
		Editable text = getText();

		if (newElement.equals("null")) { //Spacebar!
			newElement = " ";
		}

		if (editMode) {
			text.replace(selectionStartIndex, selectionEndIndex, newElement);
			selectionEndIndex = selectionStartIndex + newElement.length();
			editMode = false;
		} else {
			text.insert(selectionEndIndex, newElement);
			selectionEndIndex += newElement.length();
			selectionStartIndex = selectionEndIndex - newElement.length();
		}
		setText(text);

		if (newElement.length() > 1 && newElement.contains("(")) {
			absoluteCursorPosition = selectionStartIndex + newElement.indexOf("(") + 3;
			selectionStartIndex = absoluteCursorPosition - 1;
			selectionEndIndex = absoluteCursorPosition;
			editMode = true;
			highlightSelection();
		} else {
			absoluteCursorPosition = selectionEndIndex;
		}

		setSelection(selectionEndIndex);
	}

	public boolean hasChanges() {
		return history == null ? false : history.hasUnsavedChanges();
	}

	public void formulaSaved() {
		history.changesSaved();
		formulaEditorDialog.makeOkButtonBackButton();
		//		errorSpan = this.getText();
		//		errorSpan.removeSpan(COLOR_ERROR);
	}

	public void endEdit() {
		history.clear();
	}

	public void quickSelect() {
		selectionStartIndex = 0;
		selectionEndIndex = getText().length();
		absoluteCursorPosition = selectionEndIndex;
		setSelection(absoluteCursorPosition - 1);
		highlightSelection();
		editMode = true;
	}

	public boolean undo() {
		FormulaEditorHistoryElement lastStep = history.backward();
		setInputTextAndPosition(lastStep.text, lastStep.cursorPosition, lastStep.selectionStart, lastStep.selectionEnd);

		return history.undoIsPossible();
	}

	public boolean redo() {
		FormulaEditorHistoryElement nextStep = history.forward();
		setInputTextAndPosition(nextStep.text, nextStep.cursorPosition, nextStep.selectionStart, nextStep.selectionEnd);

		return history.redoIsPossible();
	}

	@Override
	public void setSelection(int index) {
		//This is only used to get the scrollbar to the right position easily
		super.setSelection(index);

	}

	@Override
	public void setSelection(int start, int end) {
		//Do not use!
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		// dont want it!
	}

	@Override
	public boolean onTouch(View v, MotionEvent motion) {
		return gestureDetector.onTouchEvent(motion);
	}

	final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			//Log.i("info", "double tap ");
			doSelectionAndHighlighting();
			history.updateCurrentSelection(absoluteCursorPosition, selectionStartIndex, selectionEndIndex);
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent motion) {
			Layout layout = getLayout();
			if (layout != null) {

				int yCoordinate = (int) motion.getY();
				int cursorY = 0;
				int cursorXOffset = (int) motion.getX();
				int initialScrollY = getScrollY();
				int firstLineSize = (int) (initialScrollY % lineHeight);

				if (yCoordinate <= lineHeight - firstLineSize) {

					scrollBy(0, (int) (initialScrollY > lineHeight ? -1 * (firstLineSize + lineHeight / 2) : -1
							* firstLineSize));
					cursorY = 0;
				} else if (yCoordinate >= numberOfVisibleLines * lineHeight - firstLineSize) {
					if (!(yCoordinate > layout.getLineCount() * lineHeight - getScrollY())) {
						scrollBy(0, (int) (lineHeight - firstLineSize + lineHeight / 2));
						cursorY = numberOfVisibleLines;
					}
				} else {
					for (int i = 1; i <= numberOfVisibleLines; i++) {
						if (yCoordinate <= ((lineHeight - firstLineSize) + i * lineHeight)) {
							cursorY = i;
							break;
						}
					}
				}

				int linesDown = (int) (initialScrollY / lineHeight);

				while (cursorY + linesDown >= layout.getLineCount()) {
					linesDown--;
				}

				int tempCursorPosition = layout.getOffsetForHorizontal(cursorY + linesDown, cursorXOffset);

				while (tempCursorPosition > getText().length()) {
					tempCursorPosition--;
				}

				absoluteCursorPosition = tempCursorPosition;

				postInvalidate();

				//				Log.i("info", "clicked on y: " + motion.getY() + "x: " + motion.getX() + " lines down: " + linesDown
				//						+ " cursor: " + tempCursorPosition);
				updateSelectionIndices();
				history.updateCurrentSelection(absoluteCursorPosition, selectionStartIndex, selectionEndIndex);
			}
			return true;

		}

	});

	@Override
	public boolean onCheckIsTextEditor() {
		return false;
	}
}
