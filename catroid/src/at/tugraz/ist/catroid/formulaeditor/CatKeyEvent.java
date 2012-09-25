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
				return buildSingleParameterFunction(Functions.SIN, "0");
			case CatKeyEvent.KEYCODE_COS:
				return buildSingleParameterFunction(Functions.COS, "0");
			case CatKeyEvent.KEYCODE_TAN:
				return buildSingleParameterFunction(Functions.TAN, "0");
			case CatKeyEvent.KEYCODE_LN:
				return buildSingleParameterFunction(Functions.LN, "0");
			case CatKeyEvent.KEYCODE_LOG:
				return buildSingleParameterFunction(Functions.LOG, "0");
			case CatKeyEvent.KEYCODE_PI:
				return buildFunctionWithoutParametersAndBrackets(Functions.PI);
			case CatKeyEvent.KEYCODE_SQUAREROOT:
				return buildSingleParameterFunction(Functions.SQRT, "0");
			case CatKeyEvent.KEYCODE_EULER:
				return buildFunctionWithoutParametersAndBrackets(Functions.EULER);
			case CatKeyEvent.KEYCODE_RANDOM:
				return buildDoubleParameterFunction(Functions.RAND, "0", "1");
			case CatKeyEvent.KEYCODE_ABS:
				return buildSingleParameterFunction(Functions.ABS, "0");
			case CatKeyEvent.KEYCODE_ROUND:
				return buildSingleParameterFunction(Functions.ROUND, "0");

				//SENSOR

			case CatKeyEvent.KEYCODE_SENSOR1:
				return buildSensor(Sensors.X_ACCELERATION_);
			case CatKeyEvent.KEYCODE_SENSOR2:
				return buildSensor(Sensors.Y_ACCELERATION_);
			case CatKeyEvent.KEYCODE_SENSOR3:
				return buildSensor(Sensors.Z_ACCELERATION_);
			case CatKeyEvent.KEYCODE_SENSOR4:
				return buildSensor(Sensors.AZIMUTH_ORIENTATION_);
			case CatKeyEvent.KEYCODE_SENSOR5:
				return buildSensor(Sensors.PITCH_ORIENTATION_);
			case CatKeyEvent.KEYCODE_SENSOR6:
				return buildSensor(Sensors.ROLL_ORIENTATION_);

				//PERIOD
			case CatKeyEvent.KEYCODE_PERIOD:
				return buildPeriod();

				//OPERATOR

			case CatKeyEvent.KEYCODE_PLUS:
				return buildOperator(Operators.PLUS);
			case CatKeyEvent.KEYCODE_MINUS:
				return buildOperator(Operators.MINUS);
			case CatKeyEvent.KEYCODE_STAR:
				return buildOperator(Operators.MULT);
			case CatKeyEvent.KEYCODE_SLASH:
				return buildOperator(Operators.DIVIDE);
			case CatKeyEvent.KEYCODE_POWER:
				return buildOperator(Operators.POW);

				//BRACKET

			case CatKeyEvent.KEYCODE_BRACKET:
				return buildBracket("0");

				//COSTUME

			case CatKeyEvent.KEYCODE_COSTUME_X:
				return buildCostume(Sensors.COSTUME_X_);
			case CatKeyEvent.KEYCODE_COSTUME_Y:
				return buildCostume(Sensors.COSTUME_Y_);
			case CatKeyEvent.KEYCODE_COSTUME_GHOSTEFFECT:
				return buildCostume(Sensors.COSTUME_GHOSTEFFECT_);
			case CatKeyEvent.KEYCODE_COSTUME_BRIGHTNESS:
				return buildCostume(Sensors.COSTUME_BRIGHTNESS_);
			case CatKeyEvent.KEYCODE_COSTUME_SIZE:
				return buildCostume(Sensors.COSTUME_SIZE_);
			case CatKeyEvent.KEYCODE_COSTUME_ROTATION:
				return buildCostume(Sensors.COSTUME_ROTATION_);
			case CatKeyEvent.KEYCODE_COSTUME_LAYER:
				return buildCostume(Sensors.COSTUME_LAYER_);

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

	private List<InternToken> buildCostume(Sensors sensors) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.COSTUME, sensors.sensorName));
		return returnList;
	}

	private List<InternToken> buildBracket(String bracketValue) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.BRACKET_OPEN));
		returnList.add(new InternToken(InternTokenType.NUMBER, bracketValue));
		returnList.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		return returnList;
	}

	private List<InternToken> buildOperator(Operators operator) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.OPERATOR, operator.operatorName));
		return returnList;
	}

	private List<InternToken> buildSensor(Sensors sensor) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.SENSOR, sensor.sensorName));
		return returnList;
	}

	private List<InternToken> buildDoubleParameterFunction(Functions function, String firstParameterNumberValue,
			String secondParameterNumberValue) {

		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.functionName));

		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));

		returnList.add(new InternToken(InternTokenType.NUMBER, firstParameterNumberValue));

		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));

		returnList.add(new InternToken(InternTokenType.NUMBER, secondParameterNumberValue));

		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		return returnList;

	}

	private List<InternToken> buildSingleParameterFunction(Functions function, String parameterNumberValue) {

		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.functionName));
		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		returnList.add(new InternToken(InternTokenType.NUMBER, parameterNumberValue));
		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return returnList;
	}

	private List<InternToken> buildFunctionWithoutParametersAndBrackets(Functions function) {

		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.functionName));
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
