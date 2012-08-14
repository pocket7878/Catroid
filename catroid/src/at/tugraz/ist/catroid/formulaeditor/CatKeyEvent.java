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

	/* BRACKET */
	public static final int KEYCODE_BRACKET = 1200;

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
	}

	public boolean isOperator(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_PLUS || event.getKeyCode() == KeyEvent.KEYCODE_MINUS
				|| event.getKeyCode() == KeyEvent.KEYCODE_STAR || event.getKeyCode() == KeyEvent.KEYCODE_SLASH) {
			return true;
		}

		return false;
	}

	public boolean isNumber(KeyEvent event) {
		if (event.getKeyCode() >= KeyEvent.KEYCODE_0 && event.getKeyCode() <= KeyEvent.KEYCODE_9) {
			return true;
		}

		return false;
	}

	public boolean isFunction(KeyEvent event) {
		if (event.getKeyCode() >= CatKeyEvent.KEYCODE_SIN && event.getKeyCode() <= CatKeyEvent.KEYCODE_RANDOM) {
			return true;
		}
		return false;

	}

	public boolean isSensor(KeyEvent event) {
		if (event.getKeyCode() >= CatKeyEvent.KEYCODE_SENSOR1 && event.getKeyCode() <= CatKeyEvent.KEYCODE_SENSOR5) {
			return true;
		}
		return false;

	}

	public String getDisplayLabelString() {
		if (this.isNumber(this)) {
			return "" + super.getDisplayLabel();
		} else {

			return CatKeyEvent.keyMap.get(this.getKeyCode());
		}
	}
}
