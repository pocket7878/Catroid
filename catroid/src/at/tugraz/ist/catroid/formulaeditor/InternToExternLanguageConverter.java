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
		internExternLanguageConverterMap.put("sin", R.string.formula_editor_function_sin);

		internExternLanguageConverterMap.put(".", R.string.formula_editor_decimal_mark);
		internExternLanguageConverterMap.put("sin", R.string.formula_editor_function_sin);
		internExternLanguageConverterMap.put("cos", R.string.formula_editor_function_cos);
		internExternLanguageConverterMap.put("tan", R.string.formula_editor_function_tan);
		internExternLanguageConverterMap.put("ln", R.string.formula_editor_function_ln);
		internExternLanguageConverterMap.put("log", R.string.formula_editor_function_log);
		internExternLanguageConverterMap.put("pi", R.string.formula_editor_function_pi);
		internExternLanguageConverterMap.put("sqrt", R.string.formula_editor_function_sqrt);
		internExternLanguageConverterMap.put("e", R.string.formula_editor_function_e);
		internExternLanguageConverterMap.put("rand", R.string.formula_editor_function_rand);
		internExternLanguageConverterMap.put("abs", R.string.formula_editor_function_abs);
		internExternLanguageConverterMap.put("round", R.string.formula_editor_function_round);
		internExternLanguageConverterMap.put("X_ACCELERATION_", R.string.formula_editor_sensor_x_acceleration);
		internExternLanguageConverterMap.put("Y_ACCELERATION_", R.string.formula_editor_sensor_y_acceleration);
		internExternLanguageConverterMap.put("Z_ACCELERATION_", R.string.formula_editor_sensor_z_acceleration);
		internExternLanguageConverterMap
				.put("AZIMUTH_ORIENTATION_", R.string.formula_editor_sensor_azimuth_orientation);
		internExternLanguageConverterMap.put("PITCH_ORIENTATION_", R.string.formula_editor_sensor_pitch_orientation);
		internExternLanguageConverterMap.put("ROLL_ORIENTATION_", R.string.formula_editor_sensor_roll_orientation);
		internExternLanguageConverterMap.put("COSTUME_X_", R.string.formula_editor_costume_x);
		internExternLanguageConverterMap.put("COSTUME_Y_", R.string.formula_editor_costume_y);
		internExternLanguageConverterMap.put("COSTUME_GHOSTEFFECT_", R.string.formula_editor_costume_ghosteffect);
		internExternLanguageConverterMap.put("COSTUME_BRIGHTNESS_", R.string.formula_editor_costume_brightness);
		internExternLanguageConverterMap.put("COSTUME_SIZE_", R.string.formula_editor_costume_size);
		internExternLanguageConverterMap.put("COSTUME_ROTATION_", R.string.formula_editor_costume_rotation);
		internExternLanguageConverterMap.put("COSTUME_LAYER_", R.string.formula_editor_costume_layer);
	}

	public static String getExternStringForInternTokenValue(String internTokenValue, Context context) {
		Integer stringResourceID = internExternLanguageConverterMap.get(internTokenValue);
		if (stringResourceID == null) {
			return null;
		}
		return context.getString(stringResourceID);
	}

}
