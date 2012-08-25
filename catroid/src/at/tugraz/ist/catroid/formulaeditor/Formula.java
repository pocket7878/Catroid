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

import java.io.Serializable;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import at.tugraz.ist.catroid.formulaeditor.FormulaElement.ElementType;

public class Formula implements Serializable {

	private static final long serialVersionUID = 1L;
	private transient FormulaElement root;
	private String textRepresentation = "0";
	private transient Integer formulaTextFieldId = null;
	private transient Drawable originalEditTextDrawable = null;

	//	public Formula() {
	//		root = new FormulaElement(FormulaElement.ElementType.VALUE, "0", null);
	//		textRepresentation = "0";
	//	}
	public Formula() {

	}

	public Object readResolve() {
		CalcGrammarParser parser = CalcGrammarParser.getFormulaParser(textRepresentation);
		root = parser.parseFormula();

		if (root == null) {
			root = new FormulaElement(ElementType.VALUE, "0 ", null);
			textRepresentation = "0 ";
		}

		return this;
	}

	public Formula(FormulaElement formEle) {
		root = formEle;
		textRepresentation = root.getEditTextRepresentation();
	}

	public Formula(String value) {
		textRepresentation = value;
		if (!textRepresentation.endsWith(" ")) {
			textRepresentation += " ";
		}
		CalcGrammarParser parser = CalcGrammarParser.getFormulaParser(textRepresentation);
		root = parser.parseFormula();

		if (root == null) {
			root = new FormulaElement(ElementType.VALUE, "0 ", null);
			textRepresentation = "0 ";
		}
	}

	//	public Formula(String value, int formulaTextFieldId) {
	//		root = new FormulaElement(FormulaElement.ELEMENT_VALUE, value, null);
	//		this.formulaTextFieldId = formulaTextFieldId;
	//	}

	//	public Double interpret() {
	//
	//		//Log.i("info", root.getTreeString());
	//
	//		return root.interpretRecursive();
	//	}

	public int interpretInteger() {
		return interpretInteger(1, 0);
	}

	public int interpretInteger(int minValue, int maxValue) {
		Object interpretedValue = root.interpretRecursive();

		int interpretedIntValue = 0;

		if (interpretedValue.getClass().equals(Double.class)) {
			interpretedIntValue = ((Double) interpretedValue).intValue();
		} else if (interpretedValue.getClass().equals(Integer.class)) {
			interpretedIntValue = ((Integer) interpretedValue).intValue();
		}

		if (minValue <= maxValue) {

			interpretedIntValue = Math.min(maxValue, interpretedIntValue);
			interpretedIntValue = Math.max(minValue, interpretedIntValue);
		}

		return interpretedIntValue;

	}

	public float interpretFloat() {
		return interpretFloat(1f, 0f);
	}

	public float interpretFloat(float minValue, float maxValue) {
		Object interpretedValue = root.interpretRecursive();

		float interpretedFloatValue = 0;

		if (interpretedValue.getClass().equals(Double.class)) {
			interpretedFloatValue = ((Double) interpretedValue).floatValue();
		} else if (interpretedValue.getClass().equals(Integer.class)) {
			interpretedFloatValue = ((Integer) interpretedValue).floatValue();
		}

		if (minValue <= maxValue) {

			interpretedFloatValue = Math.min(maxValue, interpretedFloatValue);
			interpretedFloatValue = Math.max(minValue, interpretedFloatValue);
		}

		return interpretedFloatValue;
	}

	@Override
	public String toString() {
		//return root.getEditTextRepresentation();
		return textRepresentation;
	}

	public void setRoot(FormulaElement formula) {
		root = formula;
		textRepresentation = root.getEditTextRepresentation();

	}

	public void setTextFieldId(int id) {
		formulaTextFieldId = id;
	}

	public void refreshTextField(View view) {
		if (formulaTextFieldId != null && root != null && view != null) {
			EditText formulaTextField = (EditText) view.findViewById(formulaTextFieldId);
			if (formulaTextField == null) {
				return;
			}
			formulaTextField.setText(textRepresentation);
			//			if (textRepresentation.length() > 5) {
			//				formulaTextField.setText(textRepresentation.substring(0, 5) + "...");
			//			} else {
			//				formulaTextField.setText(textRepresentation);
			//			}
		}

	}

	public void refreshTextField(View view, String formulaString) {
		if (formulaTextFieldId != null && root != null && view != null) {
			EditText formulaTextField = (EditText) view.findViewById(formulaTextFieldId);
			if (formulaTextField == null) {
				return;
			}
			formulaTextField.setText(formulaString);
			//			if (formulaString.length() > 5) {
			//				formulaTextField.setText(formulaString.substring(0, 5) + "...");
			//			} else {
			//				formulaTextField.setText(formulaString);
			//			}

		}
	}

	public void removeTextFieldHighlighting(View brickView, int orientation) {
		//		if (orientation == Configuration.ORIENTATION_LANDSCAPE || originalEditTextDrawable == null) {
		//			return;
		//		}
		//
		//		EditText formulaTextField = (EditText) brickView.findViewById(formulaTextFieldId);
		//
		//		int width = formulaTextField.getWidth();
		//		formulaTextField.setBackgroundDrawable(originalEditTextDrawable);
		//		formulaTextField.setWidth(width);
		//		originalEditTextDrawable = null;
	}

	public void highlightTextField(View brickView, int orientation) {
		//		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
		//			return;
		//		}
		//		Drawable highlightBackground = null;
		//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		//			highlightBackground = brickView.getResources().getDrawable(R.drawable.textfield_pressed_android4);
		//		} else {
		//			highlightBackground = brickView.getResources().getDrawable(R.drawable.textfield_pressed);
		//		}
		//
		//		EditText formulaTextField = (EditText) brickView.findViewById(formulaTextFieldId);
		//
		//		if (originalEditTextDrawable == null) {
		//			originalEditTextDrawable = formulaTextField.getBackground();
		//		}
		//		int width = formulaTextField.getWidth();
		//		width = Math.max(width, 130);
		//		formulaTextField.setBackgroundDrawable(highlightBackground);
		//		formulaTextField.setWidth(width);
	}

	public void prepareToRemove() {
		originalEditTextDrawable = null;
	}

}
