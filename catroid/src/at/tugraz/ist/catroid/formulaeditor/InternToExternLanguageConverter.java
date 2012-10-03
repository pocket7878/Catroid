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

import java.util.HashMap;

import android.content.Context;
import at.tugraz.ist.catroid.R;

public class InternToExternLanguageConverter {

	private static final HashMap<String, Integer> internExternLanguageConverterMap = new HashMap<String, Integer>();
	static {

		internExternLanguageConverterMap.put(".", R.string.formula_editor_decimal_mark);
		internExternLanguageConverterMap.put(Functions.SIN.functionName, R.string.formula_editor_function_sin);
		internExternLanguageConverterMap.put(Functions.COS.functionName, R.string.formula_editor_function_cos);
		internExternLanguageConverterMap.put(Functions.TAN.functionName, R.string.formula_editor_function_tan);
		internExternLanguageConverterMap.put(Functions.LN.functionName, R.string.formula_editor_function_ln);
		internExternLanguageConverterMap.put(Functions.LOG.functionName, R.string.formula_editor_function_log);
		internExternLanguageConverterMap.put(Functions.PI.functionName, R.string.formula_editor_function_pi);
		internExternLanguageConverterMap.put(Functions.SQRT.functionName, R.string.formula_editor_function_sqrt);
		internExternLanguageConverterMap.put(Functions.EULER.functionName, R.string.formula_editor_function_e);
		internExternLanguageConverterMap.put(Functions.RAND.functionName, R.string.formula_editor_function_rand);
		internExternLanguageConverterMap.put(Functions.ABS.functionName, R.string.formula_editor_function_abs);
		internExternLanguageConverterMap.put(Functions.ROUND.functionName, R.string.formula_editor_function_round);
		internExternLanguageConverterMap.put(Sensors.X_ACCELERATION_.sensorName,
				R.string.formula_editor_sensor_x_acceleration);
		internExternLanguageConverterMap.put(Sensors.Y_ACCELERATION_.sensorName,
				R.string.formula_editor_sensor_y_acceleration);
		internExternLanguageConverterMap.put(Sensors.Z_ACCELERATION_.sensorName,
				R.string.formula_editor_sensor_z_acceleration);
		internExternLanguageConverterMap.put(Sensors.AZIMUTH_ORIENTATION_.sensorName,
				R.string.formula_editor_sensor_azimuth_orientation);
		internExternLanguageConverterMap.put(Sensors.PITCH_ORIENTATION_.sensorName,
				R.string.formula_editor_sensor_pitch_orientation);
		internExternLanguageConverterMap.put(Sensors.ROLL_ORIENTATION_.sensorName,
				R.string.formula_editor_sensor_roll_orientation);
		internExternLanguageConverterMap.put(Sensors.COSTUME_X_.sensorName, R.string.formula_editor_costume_x);
		internExternLanguageConverterMap.put(Sensors.COSTUME_Y_.sensorName, R.string.formula_editor_costume_y);
		internExternLanguageConverterMap.put(Sensors.COSTUME_GHOSTEFFECT_.sensorName,
				R.string.formula_editor_costume_ghosteffect);
		internExternLanguageConverterMap.put(Sensors.COSTUME_BRIGHTNESS_.sensorName,
				R.string.formula_editor_costume_brightness);
		internExternLanguageConverterMap.put(Sensors.COSTUME_SIZE_.sensorName, R.string.formula_editor_costume_size);
		internExternLanguageConverterMap.put(Sensors.COSTUME_ROTATION_.sensorName,
				R.string.formula_editor_costume_rotation);
		internExternLanguageConverterMap.put(Sensors.COSTUME_LAYER_.sensorName, R.string.formula_editor_costume_layer);
	}

	public static String getExternStringForInternTokenValue(String internTokenValue, Context context) {
		Integer stringResourceID = internExternLanguageConverterMap.get(internTokenValue);
		if (stringResourceID == null) {
			return null;
		}
		return context.getString(stringResourceID);
	}

}
