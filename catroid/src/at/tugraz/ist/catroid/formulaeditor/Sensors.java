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

import java.util.EnumSet;

public enum Sensors {
	//PLUS("+", 0), MINUS("-", 0), MULT("*", 1), DIVIDE("/", 1), MOD("%", 1), POW("^", 2);
	X_ACCELERATION_("X_ACCELERATION_"), Y_ACCELERATION_("Y_ACCELERATION_"), Z_ACCELERATION_("Z_ACCELERATION_"), AZIMUTH_ORIENTATION_(
			"AZIMUTH_ORIENTATION_"), PITCH_ORIENTATION_("PITCH_ORIENTATION_"), ROLL_ORIENTATION_("ROLL_ORIENTATION_");
	private final String sensorName;

	Sensors(String value) {
		this.sensorName = value;
	}

	//	public static Functions geFunctionByValue(String value) {
	//		for (Functions fct : EnumSet.allOf(Functions.class)) {
	//			if (fct.value.equals(value)) {
	//				return fct;
	//			}
	//		}
	//		return null;
	//	}

	public static boolean isStartOfSensorName(String value) {
		for (Sensors fct : EnumSet.allOf(Sensors.class)) {
			if (fct.sensorName.startsWith(value)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isEndOfSensorName(String value) {
		for (Sensors fct : EnumSet.allOf(Sensors.class)) {
			if (fct.sensorName.endsWith(value)) {
				return true;
			}
		}
		return false;
	}

}
