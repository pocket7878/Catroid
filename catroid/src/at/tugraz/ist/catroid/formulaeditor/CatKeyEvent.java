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

import java.util.LinkedList;
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

	public List<InternToken> createInternTokensByCatKeyEvent() {

		if (this.getKeyCode() >= KeyEvent.KEYCODE_0 && this.getKeyCode() <= KeyEvent.KEYCODE_9) {
			return buildNumber("" + super.getDisplayLabel());
		}

		switch (getKeyCode()) {

		//FUNCTIONS:
			case CatKeyEvent.KEYCODE_SIN:
				return buildSingleParameterFunction("sin", "0");
			case CatKeyEvent.KEYCODE_COS:
				return buildSingleParameterFunction("cos", "0");
			case CatKeyEvent.KEYCODE_TAN:
				return buildSingleParameterFunction("tan", "0");
			case CatKeyEvent.KEYCODE_LN:
				return buildSingleParameterFunction("ln", "0");
			case CatKeyEvent.KEYCODE_LOG:
				return buildSingleParameterFunction("log", "0");
			case CatKeyEvent.KEYCODE_PI:
				return buildFunctionWithoutParametersAndBrackets("pi");
			case CatKeyEvent.KEYCODE_SQUAREROOT:
				return buildSingleParameterFunction("sqrt", "0");
			case CatKeyEvent.KEYCODE_EULER:
				return buildFunctionWithoutParametersAndBrackets("e");
			case CatKeyEvent.KEYCODE_RANDOM:
				return buildDoubleParameterFunction("rand", "0", "1");
			case CatKeyEvent.KEYCODE_ABS:
				return buildSingleParameterFunction("abs", "0");
			case CatKeyEvent.KEYCODE_ROUND:
				return buildSingleParameterFunction("round", "0");

				//SENSOR

			case CatKeyEvent.KEYCODE_SENSOR1:
				return buildSensor("X_ACCELERATION_");
			case CatKeyEvent.KEYCODE_SENSOR2:
				return buildSensor("Y_ACCELERATION_");
			case CatKeyEvent.KEYCODE_SENSOR3:
				return buildSensor("Z_ACCELERATION_");
			case CatKeyEvent.KEYCODE_SENSOR4:
				return buildSensor("AZIMUTH_ORIENTATION_");
			case CatKeyEvent.KEYCODE_SENSOR5:
				return buildSensor("PITCH_ORIENTATION_");
			case CatKeyEvent.KEYCODE_SENSOR6:
				return buildSensor("ROLL_ORIENTATION_");
			case CatKeyEvent.KEYCODE_SENSOR7:
				return buildSensor("SLIDER_");

				//PERIOD
			case CatKeyEvent.KEYCODE_PERIOD:
				return buildPeriod();

				//OPERATOR

			case CatKeyEvent.KEYCODE_PLUS:
				return buildOperator("+");
			case CatKeyEvent.KEYCODE_MINUS:
				return buildOperator("-");
			case CatKeyEvent.KEYCODE_STAR:
				return buildOperator("*");
			case CatKeyEvent.KEYCODE_SLASH:
				return buildOperator("/");
			case CatKeyEvent.KEYCODE_POWER:
				return buildOperator("^");

				//BRACKET

			case CatKeyEvent.KEYCODE_BRACKET:
				return buildBracket("0");

				//COSTUME

			case CatKeyEvent.KEYCODE_COSTUME_X:
				return buildCostume("COSTUME_X_");
			case CatKeyEvent.KEYCODE_COSTUME_Y:
				return buildCostume("COSTUME_Y_");
			case CatKeyEvent.KEYCODE_COSTUME_GHOSTEFFECT:
				return buildCostume("COSTUME_GHOSTEFFECT_");
			case CatKeyEvent.KEYCODE_COSTUME_BRIGHTNESS:
				return buildCostume("COSTUME_BRIGHTNESS_");
			case CatKeyEvent.KEYCODE_COSTUME_SIZE:
				return buildCostume("COSTUME_SIZE_");
			case CatKeyEvent.KEYCODE_COSTUME_ROTATION:
				return buildCostume("COSTUME_ROTATION_");
			case CatKeyEvent.KEYCODE_COSTUME_LAYER:
				return buildCostume("COSTUME_LAYER_");

		}

		return null;

	}

	private List<InternToken> buildPeriod() {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.PERIOD));
		return returnList;
	}

	private List<InternToken> buildNumber(String numberValue) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.NUMBER, numberValue));
		return returnList;
	}

	private List<InternToken> buildCostume(String costumeName) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.COSTUME, costumeName));
		return returnList;
	}

	private List<InternToken> buildBracket(String bracketValue) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.BRACKET_OPEN));
		returnList.add(new InternToken(InternTokenType.NUMBER, bracketValue));
		returnList.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		return returnList;
	}

	private List<InternToken> buildOperator(String operatorName) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.OPERATOR, operatorName));
		return returnList;
	}

	private List<InternToken> buildSensor(String sensorName) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.SENSOR, sensorName));
		return returnList;
	}

	private List<InternToken> buildDoubleParameterFunction(String functionName, String firstParameterNumberValue,
			String secondParameterNumberValue) {

		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.FUNCTION_NAME, functionName));

		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));

		returnList.add(new InternToken(InternTokenType.NUMBER, firstParameterNumberValue));

		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));

		returnList.add(new InternToken(InternTokenType.NUMBER, secondParameterNumberValue));

		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		return returnList;

	}

	private List<InternToken> buildSingleParameterFunction(String functionName, String parameterNumberValue) {

		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.FUNCTION_NAME, functionName));

		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));

		returnList.add(new InternToken(InternTokenType.NUMBER, parameterNumberValue));

		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		return returnList;

	}

	private List<InternToken> buildFunctionWithoutParametersAndBrackets(String functionName) {

		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.FUNCTION_NAME, functionName));

		return returnList;

	}

	private void initializeKeyMap() {

	}

	public boolean isOperator() {
		if (this.getKeyCode() == KeyEvent.KEYCODE_PLUS || this.getKeyCode() == KeyEvent.KEYCODE_MINUS
				|| this.getKeyCode() == KeyEvent.KEYCODE_STAR || this.getKeyCode() == KeyEvent.KEYCODE_SLASH) {
			return true;
		}

		return false;
	}

	public boolean isPeriod() {
		if (this.getKeyCode() == CatKeyEvent.KEYCODE_PERIOD) {
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
		if (this.getKeyCode() >= CatKeyEvent.KEYCODE_SENSOR1 && this.getKeyCode() <= CatKeyEvent.KEYCODE_SENSOR7) {
			return true;
		}
		return false;

	}

	public boolean isBracket() {
		if (this.getKeyCode() == CatKeyEvent.KEYCODE_BRACKET) {
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
