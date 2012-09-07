/**
+ *  Catroid: An on-device graphical programming language for Android devices
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

import java.util.Stack;

public class FormulaEditorHistory {

	//TODO: History should contain InternFormula member to generate extern representation

	private static final int MAXIMUM_HISTORY_LENGTH = 15;
	private Stack<FormulaEditorHistoryElement> undoStack = null;
	private Stack<FormulaEditorHistoryElement> redoStack = null;
	private FormulaEditorHistoryElement current = null;
	private boolean hasUnsavedChanges = false;

	public FormulaEditorHistory(String text, int cursorPosition, int selectionStart, int selectionEnd) {
		current = new FormulaEditorHistoryElement(text, cursorPosition, selectionStart, selectionEnd);
		undoStack = new Stack<FormulaEditorHistoryElement>();
		redoStack = new Stack<FormulaEditorHistoryElement>();
	}

	public void push(String text, int cursorPosition, int selectionStart, int selectionEnd) {

		if (current != null && current.text.equals(text)) {
			return;
		}

		if (current != null) {
			undoStack.push(current);
		}
		current = new FormulaEditorHistoryElement(text, cursorPosition, selectionStart, selectionEnd);
		redoStack.clear();
		hasUnsavedChanges = true;
		//Log.i("info", "history size: " + undoStack.size());
		if (undoStack.size() > MAXIMUM_HISTORY_LENGTH) {
			undoStack.removeElementAt(0);
		}

	}

	public FormulaEditorHistoryElement backward() {
		redoStack.push(current);
		hasUnsavedChanges = true;
		if (!undoStack.empty()) {
			current = undoStack.pop();
		}
		return current;
	}

	public FormulaEditorHistoryElement forward() {
		undoStack.push(current);
		hasUnsavedChanges = true;
		if (!redoStack.empty()) {
			current = redoStack.pop();
		}
		return current;
	}

	public void updateCurrentSelection(int cursorPosition, int selectionStart, int selectionEnd) {
		current.cursorPosition = cursorPosition;
		current.selectionStart = selectionStart;
		current.selectionEnd = selectionEnd;
	}

	public void init(String text, int cursorPosition, int selectionStart, int selectionEnd) {
		current = new FormulaEditorHistoryElement(text, cursorPosition, selectionStart, selectionEnd);
	}

	public void clear() {
		undoStack.clear();
		redoStack.clear();
		current = null;
		hasUnsavedChanges = false;
	}

	public FormulaEditorHistoryElement getCurrentState() {
		return current;
	}

	public void updateCurrentCursor(int cursorPosition) {
		current.cursorPosition = cursorPosition;
	}

	public boolean undoIsPossible() {
		return !undoStack.empty();
	}

	public boolean redoIsPossible() {
		return !redoStack.empty();
	}

	public boolean hasUnsavedChanges() {
		return hasUnsavedChanges;
	}

	public void changesSaved() {
		hasUnsavedChanges = false;
	}

}
