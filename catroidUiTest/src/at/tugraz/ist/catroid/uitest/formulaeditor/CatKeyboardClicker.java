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

package at.tugraz.ist.catroid.uitest.formulaeditor;

import java.util.HashMap;
import java.util.Vector;

import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.jayway.android.robotium.solo.Solo;

public class CatKeyboardClicker {

	private Solo solo;
	private static Vector<Vector<String>> keyString = null;
	private static HashMap<String, Point> keyMap = null;

	private static float amountOfDisplayspaceUsedForKeyboard;
	private static float keyboardHeight;
	private static int displayWidth;
	private static int displayHeight;
	private static int buttonsEachColumns;
	private static int buttonsEachRow;
	private static int buttonWidth;
	private static float buttonHeight;

	private int totalKeyboardSwitches = 0;

	public CatKeyboardClicker(Solo solo) {
		this.solo = solo;

		if (keyString == null) {
			keyString = new Vector<Vector<String>>();
			keyString.add(new Vector<String>());
			keyString.add(new Vector<String>());
			keyString.add(new Vector<String>());

			keyMap = new HashMap<String, Point>();
			buttonsEachColumns = 5;
			buttonsEachRow = 4;

			calculateCoordinatesOnScreen();
			createKeyStrings();
			setCoordinatesForKeys();
		}
	}

	public void clickOnKey(String key) {

		Point keyOnScreen = keyMap.get(key);
		//Log.i("info", "clickOnKey(" + key + ")" + "x:" + keyOnScreen.x + "y:" + keyOnScreen.y);
		solo.clickOnScreen(keyOnScreen.x, keyOnScreen.y);

		if (key.equals("keyboardswitch")) {
			totalKeyboardSwitches++;
		}

	}

	public void clearEditTextVerySlowButAlwaysForReal(int index) {
		int noChangesIndex = 0;
		int lastEditTextLength = solo.getEditText(index).getText().length();
		while (solo.getEditText(index).getText().length() > 0) {
			if (lastEditTextLength == solo.getEditText(index).getText().length() && ++noChangesIndex == 2) {
				solo.clickOnEditText(index);
				noChangesIndex = 0;
			}
			lastEditTextLength = solo.getEditText(index).getText().length();
			this.clickOnKey("del");
		}
	}

	public void clearEditTextPortraitModeOnlyQuickly(int editTextIndex) {
		this.clickOnKey("del");
		solo.clickOnEditText(editTextIndex);
		this.clickOnKey("del");
	}

	public void clearEditTextWithCursorBehindLastCharacterOnlyQuickly(int index) {
		int lastEditTextLength = solo.getEditText(index).getText().length();
		while (lastEditTextLength > 0) {
			this.clickOnKey("del");
			lastEditTextLength--;
		}
	}

	//when using this method, ALWAYS use spaces between sensors and functions and make sure the string is valid! 
	//Be VERY careful when using this for strings that should be detected as invalid by the parser
	//Doesnt work for more than one function
	public void enterText(String text) {
		for (int i = 0; i < text.length(); i++) {
			char current = text.charAt(i);

			if (charIsWhitespace(current)) {

			} else if (charIsNumber(current) || current == '.') {
				clickOnKey(current + "");
			} else if (charIsLowerCaseLetter(current)) {
				String clickOn = "" + current;
				while (!charIsWhitespace(current) && !(current == '(')) {
					i++;
					current = text.charAt(i);
					clickOn += current;
				}
				clickOnKey(clickOn);
			} else if (charIsCapitalLetter(current)) {
				String clickOn = "" + current;
				while (!charIsWhitespace(current)) {
					i++;
					current = text.charAt(i);
					clickOn += current;
				}
				clickOnKey(clickOn);
			}
		}
	}

	public void switchToNumberKeyboard() {
		//numbers at: 0,3,6,9... switches
		//functions at: 1,4,7,10... switches
		//sensors at: 2,5,8,11... switches
		if (totalKeyboardSwitches % 3 == 0) {
		} else if (totalKeyboardSwitches % 3 == 1) {
			clickOnKey("keyboardswitch");
			clickOnKey("keyboardswitch");
			totalKeyboardSwitches += 2;
		} else if (totalKeyboardSwitches % 3 == 2) {
			clickOnKey("keyboardswitch");
			totalKeyboardSwitches++;
		}
	}

	public void switchToFunctionKeyboard() {
		//numbers at: 0,3,6,9... switches
		//functions at: 1,4,7,10... switches
		//sensors at: 2,5,8,11... switches
		if (totalKeyboardSwitches % 3 == 0) {
			clickOnKey("keyboardswitch");
			totalKeyboardSwitches++;
		} else if (totalKeyboardSwitches % 3 == 1) {

		} else if (totalKeyboardSwitches % 3 == 2) {
			clickOnKey("keyboardswitch");
			clickOnKey("keyboardswitch");
			totalKeyboardSwitches += 2;
		}
	}

	public void switchToSensorKeyboard() {
		//numbers at: 0,3,6,9... switches
		//functions at: 1,4,7,10... switches
		//sensors at: 2,5,8,11... switches
		if (totalKeyboardSwitches % 3 == 0) {
			clickOnKey("keyboardswitch");
			clickOnKey("keyboardswitch");
			totalKeyboardSwitches += 2;
		} else if (totalKeyboardSwitches % 3 == 1) {
			clickOnKey("keyboardswitch");
			totalKeyboardSwitches++;
		} else if (totalKeyboardSwitches % 3 == 2) {

		}
	}

	private void createKeyStrings() {
		// Clicking keys on screen in this order:
		//0,7,4,1,
		//.,8,5,2,
		//space,9,6,3,
		//space2,del,*,+,
		//shift,enter,/,-
		keyString.get(0).add("0");
		keyString.get(0).add("7");
		keyString.get(0).add("4");
		keyString.get(0).add("1");

		keyString.get(0).add(".");
		keyString.get(0).add("8");
		keyString.get(0).add("5");
		keyString.get(0).add("2");

		keyString.get(0).add("bracket");
		keyString.get(0).add("9");
		keyString.get(0).add("6");
		keyString.get(0).add("3");

		keyString.get(0).add("keyboardswitch");
		keyString.get(0).add("*");
		keyString.get(0).add("+");
		keyString.get(0).add("del");

		keyString.get(0).add("keyboardswitch2");
		keyString.get(0).add("/");
		keyString.get(0).add("-");
		keyString.get(0).add("del2");

		keyString.get(1).add("pi");
		keyString.get(1).add("rand");
		keyString.get(1).add("ln");
		keyString.get(1).add("sin");

		keyString.get(1).add("e");
		keyString.get(1).add("abs");
		keyString.get(1).add("log");
		keyString.get(1).add("cos");

		keyString.get(1).add("^");
		keyString.get(1).add("round");
		keyString.get(1).add("sqrt");
		keyString.get(1).add("tan");

		keyString.get(1).add("keyboardswitch");
		keyString.get(1).add("*");
		keyString.get(1).add("+");
		keyString.get(1).add("del");

		keyString.get(1).add("keyboardswitch2");
		keyString.get(1).add("/");
		keyString.get(1).add("-");
		keyString.get(1).add("del2");

		keyString.get(2).add("pitch");
		keyString.get(2).add("z-accel");
		keyString.get(2).add("y-accel");
		keyString.get(2).add("x-accel");

		keyString.get(2).add(null);
		keyString.get(2).add(null);
		keyString.get(2).add(null);
		keyString.get(2).add(null);

		keyString.get(2).add(null);
		keyString.get(2).add(null);
		keyString.get(2).add(null);
		keyString.get(2).add(null);

		keyString.get(2).add("keyboardswitch");
		keyString.get(2).add("roll");
		keyString.get(2).add("azimuth");
		keyString.get(2).add("del");

		keyString.get(2).add(null);
		keyString.get(2).add(null);
		keyString.get(2).add(null);
		keyString.get(2).add(null);

	}

	private void setCoordinatesForKeys() {
		//Setting x,y coordinates for each key
		int z = 0;
		for (int h = 0; h < keyString.size(); h++) {
			for (int i = 0; i < buttonsEachColumns; i++) {
				for (int j = 0; j < buttonsEachRow; j++) {

					//Log.i("info", "setUp()" + " i:" + i + " j:" + j + " z:" + z + " h:" + h);
					int x = i * buttonWidth + buttonWidth / 2;
					int y = displayHeight - (j * (int) buttonHeight + (int) buttonHeight / 2);
					keyMap.put(keyString.get(h).get(z), new Point(x, y));
					++z;
					z = z % keyString.get(h).size();

				}
			}
		}
	}

	private void calculateCoordinatesOnScreen() {

		DisplayMetrics currentDisplayMetrics = new DisplayMetrics();
		solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(currentDisplayMetrics);

		//Log.i("info", "DisplayMetrics" + "width:" + currentDisplayMetrics.widthPixels + " height:"
		//		+ currentDisplayMetrics.heightPixels);

		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, currentDisplayMetrics);
		//Log.i("info", "pixel: " + px);

		displayWidth = currentDisplayMetrics.widthPixels;
		displayHeight = currentDisplayMetrics.heightPixels;

		keyboardHeight = buttonsEachRow * 42 * px;
		//Log.i("info", "keyboardHeight: " + keyboardHeight);

		amountOfDisplayspaceUsedForKeyboard = displayHeight / keyboardHeight;
		//Log.i("info", "amountOfDisplayspaceUsedForKeyboard: " + amountOfDisplayspaceUsedForKeyboard);

		buttonWidth = displayWidth / buttonsEachColumns;
		float divisor = amountOfDisplayspaceUsedForKeyboard * buttonsEachRow;
		//Log.i("info", "divisor: " + divisor);
		buttonHeight = displayHeight / divisor;
		//Log.i("info", "buttonHeight: " + buttonHeight);

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
}
