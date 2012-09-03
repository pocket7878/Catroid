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

import java.util.List;

import android.util.SparseArray;
import android.view.KeyEvent;

/**
 * @author obusher
 * 
 */
public class CatKeyEvent extends KeyEvent {

	private static SparseArray<String> keyMap;

	/* FUNCTIONS */
	public static final int KEYCODE_SIN = 1000;
	public static final int KEYCODE_COS = 1001;
	public static final int KEYCODE_TAN = 1002;
	public static final int KEYCODE_LN = 1003;
	public static final int KEYCODE_LOG = 1004;
	public static final int KEYCODE_PI = 1005;
	public static final int KEYCODE_SQUAREROOT = 1006;
	public static final int KEYCODE_EULER = 1007;
	public static final int KEYCODE_RANDOM = 1008;
	public static final int KEYCODE_ABS = 1009;
	public static final int KEYCODE_ROUND = 1010;

	/* SENSOR */
	public static final int KEYCODE_SENSOR1 = 1100;
	public static final int KEYCODE_SENSOR2 = 1101;
	public static final int KEYCODE_SENSOR3 = 1102;
	public static final int KEYCODE_SENSOR4 = 1103;
	public static final int KEYCODE_SENSOR5 = 1104;
	public static final int KEYCODE_SENSOR6 = 1105;
	public static final int KEYCODE_SENSOR7 = 1106;

	/* OTHER STUFF */
	public static final int KEYCODE_BRACKET = 1200;
	public static final int KEYCODE_COSTUME_BUTTON = 1201;
	public static final int KEYCODE_COSTUME_X = 1202;
	public static final int KEYCODE_COSTUME_Y = 1203;
	public static final int KEYCODE_COSTUME_GHOSTEFFECT = 1204;
	public static final int KEYCODE_COSTUME_BRIGHTNESS = 1205;
	public static final int KEYCODE_COSTUME_SIZE = 1206;
	public static final int KEYCODE_COSTUME_ROTATION = 1207;
	public static final int KEYCODE_COSTUME_LAYER = 1208;

	// Please update the functions of this class if you add new KEY_CODE constants ^_^

	/**
	 * @param origEvent
	 */
	public CatKeyEvent(KeyEvent origEvent) {
		super(origEvent);

		if (CatKeyEvent.keyMap == null) {
			initializeKeyMap();
		}

	}

	private List<InternToken> buildSingleParameterFunction(String functionName, String paramValue) {
		InternToken functionNameToken = new InternToken("function:" + functionName,
				InternToken.InternTokenType.FUNCTION_NAME);

		InternToken functionParametersBracketOpen = new InternToken("(",
				InternToken.InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN);

		InternToken functionParametersBracketClose = new InternToken("(",
				InternToken.InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN);
		return null;

	}

	private void initializeKeyMap() {
		CatKeyEvent.keyMap = new SparseArray<String>();

		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_SIN, new String("sin( 0 )"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_COS, new String("cos( 0 )"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_TAN, new String("tan( 0 )"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_LN, new String("ln( 0 )"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_LOG, new String("log( 0 )"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_PI, new String("pi"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_SQUAREROOT, new String("sqrt( 0 )"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_EULER, new String("e"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_RANDOM, new String("rand( 0 , 1 )"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_ABS, new String("abs( 0 )"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_ROUND, new String("round( 0 )"));

		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_SENSOR1, new String("X_ACCELERATION_"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_SENSOR2, new String("Y_ACCELERATION_"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_SENSOR3, new String("Z_ACCELERATION_"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_SENSOR4, new String("AZIMUTH_ORIENTATION_"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_SENSOR5, new String("PITCH_ORIENTATION_"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_SENSOR6, new String("ROLL_ORIENTATION_"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_SENSOR7, new String("SLIDER_"));

		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_PLUS, new String("+"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_MINUS, new String("-"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_STAR, new String("*"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_SLASH, new String("/"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_COMMA, new String(","));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_PERIOD, new String("."));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_POWER, new String("^"));

		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_BRACKET, new String("( 0 )"));
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_COSTUME_BUTTON, null);
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_COSTUME_X, "COSTUME_X_");
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_COSTUME_Y, "COSTUME_Y_");
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_COSTUME_GHOSTEFFECT, "COSTUME_GHOSTEFFECT_");
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_COSTUME_BRIGHTNESS, "COSTUME_BRIGHTNESS_");
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_COSTUME_SIZE, "COSTUME_SIZE_");
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_COSTUME_ROTATION, "COSTUME_ROTATION_");
		CatKeyEvent.keyMap.put(CatKeyEvent.KEYCODE_COSTUME_LAYER, "COSTUME_LAYER_");

	}

	public boolean isOperator(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_PLUS || event.getKeyCode() == KeyEvent.KEYCODE_MINUS
				|| event.getKeyCode() == KeyEvent.KEYCODE_STAR || event.getKeyCode() == KeyEvent.KEYCODE_SLASH) {
			return true;
		}

		return false;
	}

	public boolean isNumber() {
		if (this.getKeyCode() >= KeyEvent.KEYCODE_0 && this.getKeyCode() <= KeyEvent.KEYCODE_9) {
			return true;
		}

		return false;
	}

	public boolean isFunction() {
		if (this.getKeyCode() >= CatKeyEvent.KEYCODE_SIN && this.getKeyCode() <= CatKeyEvent.KEYCODE_RANDOM) {
			return true;
		}
		return false;

	}

	public boolean isSensor() {
		if (this.getKeyCode() >= CatKeyEvent.KEYCODE_SENSOR1 && this.getKeyCode() <= CatKeyEvent.KEYCODE_SENSOR5) {
			return true;
		}
		return false;

	}

	public String getDisplayLabelString() {
		if (this.isNumber()) {
			return "" + super.getDisplayLabel();
		} else {

			return CatKeyEvent.keyMap.get(this.getKeyCode());
		}
	}
}
