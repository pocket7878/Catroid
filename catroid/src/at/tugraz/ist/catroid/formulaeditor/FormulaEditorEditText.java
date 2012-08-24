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
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import at.tugraz.ist.catroid.ui.fragment.FormulaEditorFragment;

public class FormulaEditorEditText extends EditText implements OnTouchListener {

	private static final BackgroundColorSpan COLOR_ERROR = new BackgroundColorSpan(0xFFF00000);
	private static final BackgroundColorSpan COLOR_HIGHLIGHT = new BackgroundColorSpan(0xFFFFFF00);

	public static final int NUMBER = 0;
	public static final int OPERATOR = 1;
	public static final int FUNCTION_SEPERATOR = 2;
	public static final int FUNCTION = 3;
	public static final int BRACKET_CLOSE = 4;
	public static final int BRACKET_OPEN = 5;
	public static final int SENSOR_VALUE = 6;
	public static boolean autoWhitespaceDeletion = true;

	private int selectionStartIndex = 0;
	private int selectionEndIndex = 0;
	private int absoluteCursorPosition = 0;

	private boolean editMode = false;
	private Spannable highlightSpan = null;
	private float lineHeight = 0;

	public CatKeyboardView catKeyboardView;
	private static FormulaEditorHistory history = null;
	private Context context;

	FormulaEditorFragment formulaEditorDialog = null;

	public FormulaEditorEditText(Context context) {
		super(context);
		this.context = context;
	}

	public FormulaEditorEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public FormulaEditorEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public void init(FormulaEditorFragment dialog, int brickHeight, CatKeyboardView ckv) {
		this.formulaEditorDialog = dialog;
		this.setOnTouchListener(this);
		this.setLongClickable(false);
		this.setSelectAllOnFocus(false);
		this.catKeyboardView = ckv;
		this.setCursorVisible(false);
	}

	public void enterNewFormula(String formulaAsText) {
		setText(formulaAsText);
		quickSelect();

		if (history == null) {
			history = new FormulaEditorHistory(formulaAsText, absoluteCursorPosition, selectionStartIndex,
					selectionEndIndex);
		} else {
			history.init(formulaAsText, absoluteCursorPosition, selectionStartIndex, selectionEndIndex);
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

		return true;
	}

	public void updateSelectionIndices() {
		clearSelectionHighlighting();
		editMode = false;

		selectionStartIndex = absoluteCursorPosition;
		selectionEndIndex = absoluteCursorPosition;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Layout layout = getLayout();
		if (layout != null) {
			lineHeight = getTextSize() + 5;

			int line = layout.getLineForOffset(absoluteCursorPosition);
			int baseline = layout.getLineBaseline(line);
			int ascent = layout.getLineAscent(line);

			float xCoordinate = layout.getPrimaryHorizontal(absoluteCursorPosition);
			float startYCoordinate = baseline + ascent;
			float endYCoordinate = baseline + ascent + lineHeight;

			canvas.drawLine(xCoordinate, startYCoordinate, xCoordinate, endYCoordinate, getPaint());
		}
	}

	public void doSelectionAndHighlighting() {
		clearSelectionHighlighting();
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
			extendSelectionForSensorValue();
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
		} else if (Operators.isOperator(currentlySelectedElement)) {
			return OPERATOR;
		} else if (Functions.isFunction(currentlySelectedElement)) {
			return FUNCTION;
		}

		return NUMBER;

	}

	private void extendSelectionBetweenBracketsFromOpenBracket() {
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
		if (selectionEndIndex > textLength) {
			selectionEndIndex = textLength;
			editMode = false;
		}
	}

	private void extendSelectionBetweenBracketsFromCloseBracket() {
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

	private void extendSelectionForFunctionOnSeperator() {
		extendSelectionBetweenBracketsFromCloseBracket();
		extendSelectionForFunctionName();
		extendSelectionBetweenBracketsFromOpenBracket();
	}

	private void extendSelectionForFunctionName() {
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

	private void extendSelectionForNumber() {
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

	private void extendSelectionForSensorValue() {
		String temp = getText().toString().substring(selectionStartIndex, selectionEndIndex);
		if (Sensors.isAmbiguousName(temp)) {
			String forwardDisambiguation = disambiguateNameForSensors();

			if (Sensors.isStartOfSensorName(temp, forwardDisambiguation)) {
				extendSelectionForSensorValueFromFront();
			} else if (Sensors.isEndOfSensorName(temp)) {
				extendSelectionForSensorValueFromEnd();
			}
		} else if (Sensors.isStartOfSensorName(temp)) {
			extendSelectionForSensorValueFromFront();
		} else if (Sensors.isEndOfSensorName(temp)) {
			extendSelectionForSensorValueFromEnd();
		}

	}

	private String disambiguateNameForSensors() {
		String searchForward = "";
		Editable text = getText();
		int searchPos = selectionEndIndex;
		while (searchPos < text.length()) {
			searchForward += text.charAt(searchPos);
			searchPos++;
			if (searchForward.endsWith("_")) {
				break;
			}
		}

		return searchForward;
	}

	private void extendSelectionForSensorValueFromEnd() {
		Editable text = getText();
		selectionStartIndex--;
		while (selectionStartIndex > 0 && charIsCapitalLetter(text.charAt(selectionStartIndex - 1))) {
			selectionStartIndex--;
		}
	}

	private void extendSelectionForSensorValueFromFront() {
		Editable text = getText();
		selectionEndIndex++;
		while (selectionEndIndex < text.length() && text.charAt(selectionEndIndex) != '_') {
			selectionEndIndex++;
		}
		selectionEndIndex++;
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

	public boolean charIsWhitespace(char letter) {
		if (letter == 32) { //ASCII 0x00...0x20
			return true;
		}
		return false;
	}

	public void highlightSelection() {
		highlightSpan = this.getText();
		highlightSpan.removeSpan(COLOR_HIGHLIGHT);

		//		if (selectionStartIndex < 0) {
		//			selectionStartIndex = 0;
		//		}

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

	public void setParseErrorCursor(int firstError) {
		clearSelectionHighlighting();
		highlightSpan = this.getText();

		if (highlightSpan.length() <= 1 || firstError == 0) {
			if (highlightSpan.length() == 0) {
				append(" ");
			}
			highlightSpan.setSpan(COLOR_ERROR, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			absoluteCursorPosition = 0;
			selectionStartIndex = 0;
			selectionEndIndex = 1;
			editMode = true;
			return;
		}

		if (firstError < highlightSpan.length()) {
			editMode = (charIsLowerCaseLetter(highlightSpan.charAt(firstError))
					|| charIsCapitalLetter(highlightSpan.charAt(firstError)) || highlightSpan.charAt(firstError) == ')' || highlightSpan
					.charAt(firstError) == ',') ? false : true;
			selectionStartIndex = firstError;
			highlightSpan.setSpan(COLOR_ERROR, firstError, ++firstError, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			selectionEndIndex = firstError;
		} else {
			editMode = (charIsLowerCaseLetter(highlightSpan.charAt(firstError - 1))
					|| charIsCapitalLetter(highlightSpan.charAt(firstError - 1)) || highlightSpan
					.charAt(firstError - 1) == ')') ? false : true;
			highlightSpan.setSpan(COLOR_ERROR, firstError - 1, firstError, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			selectionStartIndex = firstError - 1;
			selectionEndIndex = firstError;
		}

		setSelection(firstError);
		absoluteCursorPosition = firstError;
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

		history.push(getText().toString(), absoluteCursorPosition, absoluteCursorPosition, absoluteCursorPosition);

		formulaEditorDialog.refreshFormulaPreviewString(this.getText().toString());
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
			//remove 1 whitespace char if there is one
			if (autoWhitespaceDeletion && charIsWhitespace(currentChar) && selectionEndIndex >= 2) {
				text.replace(selectionEndIndex - 1, selectionEndIndex, "");
				selectionEndIndex--;
				absoluteCursorPosition--;
				selectionStartIndex = selectionEndIndex;
				currentChar = text.charAt(selectionEndIndex - 1);
			}
			if (currentChar == ',' || currentChar == ')' || currentChar == '(' || currentChar == '_'
					|| charIsLowerCaseLetter(currentChar)) { //isLowerCaseLetter possible for parameterless functions, the others get treated in checkAndModifyKeyInput!
				doSelectionAndHighlighting();
				text.replace(selectionStartIndex, selectionEndIndex, "");
				selectionEndIndex = selectionStartIndex;
			} else {
				text.replace(selectionEndIndex - 1, selectionEndIndex, "");
				selectionEndIndex--;
				selectionStartIndex = selectionEndIndex;
			}
		}

		if (text.length() > 0 && !charIsWhitespace(text.charAt(text.length() - 1))) {
			text.append(" ");
		}

		setText(text);
		setSelection(selectionEndIndex);
		absoluteCursorPosition = selectionEndIndex;
	}

	private void appendToTextFieldAtCurrentPosition(String newElement) {
		Editable text = getText();

		//		if (newElement.equals("null")) { //Spacebar, removed!
		//			newElement = " ";
		//		}

		if (editMode) {
			text.replace(selectionStartIndex, selectionEndIndex, newElement);
			selectionEndIndex = selectionStartIndex + newElement.length();
			editMode = false;
		} else {
			text.insert(selectionEndIndex, newElement);
			selectionEndIndex += newElement.length();
			selectionStartIndex = selectionEndIndex - newElement.length();
		}

		//insert whitespace if none exists before/after new element
		if (newElement.length() > 1 || Operators.isOperator(newElement)) {
			if (selectionStartIndex > 0 && !charIsWhitespace(text.charAt(selectionStartIndex - 1))) {
				text.insert(selectionStartIndex++, " ");
				selectionEndIndex++;
				absoluteCursorPosition++;
			}
			if (selectionEndIndex < text.length() && !charIsWhitespace(text.charAt(selectionEndIndex))) {
				text.insert(selectionEndIndex++, " ");
			}
		} else if (selectionStartIndex > 0 && !charIsWhitespace(text.charAt(selectionStartIndex - 1))
				&& !charIsNumber(text.charAt(selectionStartIndex - 1))
				&& !(text.charAt(selectionStartIndex - 1) == '.')) {
			text.insert(selectionStartIndex++, " ");
			selectionEndIndex++;
			absoluteCursorPosition++;
		}
		if (text.length() > 0 && !charIsWhitespace(text.charAt(text.length() - 1))) {
			text.append(" ");
		}
		setText(text);

		//move cursor to first function parameter
		if (newElement.length() > 1 && (Functions.isFunction(newElement) || newElement.equals("( 0 )"))) {
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
	}

	public void endEdit() {
		history.clear();
	}

	public void quickSelect() {
		if (getText().length() < 1) {
			return;
		}
		selectionStartIndex = 0;
		selectionEndIndex = getText().length();
		absoluteCursorPosition = selectionEndIndex;
		setSelection(absoluteCursorPosition - 1);
		highlightSelection();
		editMode = true;
	}

	//	public boolean getUndoIsPossible() {
	//		return history.undoIsPossible();
	//	}
	//
	//	public boolean getRedoIsPossible() {
	//		return history.redoIsPossible();
	//	}

	public boolean undo() {
		if (!history.undoIsPossible()) {
			return false;
		}
		FormulaEditorHistoryElement lastStep = history.backward();
		if (lastStep != null) {
			setInputTextAndPosition(lastStep.text, lastStep.cursorPosition, lastStep.selectionStart,
					lastStep.selectionEnd);
		}

		formulaEditorDialog.refreshFormulaPreviewString(this.getText().toString());
		return true;
	}

	public boolean redo() {
		if (!history.redoIsPossible()) {
			return false;
		}
		FormulaEditorHistoryElement nextStep = history.forward();
		if (nextStep != null) {
			setInputTextAndPosition(nextStep.text, nextStep.cursorPosition, nextStep.selectionStart,
					nextStep.selectionEnd);
		}
		formulaEditorDialog.refreshFormulaPreviewString(this.getText().toString());
		return true;
	}

	@Override
	public void setSelection(int index) {
		//This is only used to get the scrollbar to the right position easily
		super.setSelection(index);

	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		// dont want it!
	}

	@Override
	public boolean onTouch(View v, MotionEvent motion) {
		return gestureDetector.onTouchEvent(motion);
	}

	final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			doSelectionAndHighlighting();
			history.updateCurrentSelection(absoluteCursorPosition, selectionStartIndex, selectionEndIndex);
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent motion) {
			Layout layout = getLayout();
			if (layout != null) {

				lineHeight = getTextSize() + 5;
				int yCoordinate = (int) motion.getY();
				int cursorY = 0;
				int cursorXOffset = (int) motion.getX();
				int initialScrollY = getScrollY();
				int firstLineSize = (int) (initialScrollY % lineHeight);
				int numberOfVisbleLines = (int) (getHeight() / lineHeight);

				if (yCoordinate <= lineHeight - firstLineSize) {

					scrollBy(0, (int) (initialScrollY > lineHeight ? -1 * (firstLineSize + lineHeight / 2) : -1
							* firstLineSize));
					cursorY = 0;
				} else if (yCoordinate >= numberOfVisbleLines * lineHeight - lineHeight / 2) {
					if (!(yCoordinate > layout.getLineCount() * lineHeight - getScrollY())) {
						scrollBy(0, (int) (lineHeight - firstLineSize + lineHeight / 2));
						cursorY = numberOfVisbleLines;
					}
				} else {
					for (int i = 1; i <= numberOfVisbleLines; i++) {
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
