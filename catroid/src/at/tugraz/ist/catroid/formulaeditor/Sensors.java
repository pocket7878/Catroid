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

public enum Sensors {
	X_ACCELERATION_("X_ACCELERATION_"), Y_ACCELERATION_("Y_ACCELERATION_"), Z_ACCELERATION_("Z_ACCELERATION_"), AZIMUTH_ORIENTATION_(
			"AZIMUTH_ORIENTATION_"), PITCH_ORIENTATION_("PITCH_ORIENTATION_"), ROLL_ORIENTATION_("ROLL_ORIENTATION_"), COSTUME_X_(
			"COSTUME_X_"), COSTUME_Y_("COSTUME_Y_"), COSTUME_GHOSTEFFECT_("COSTUME_GHOSTEFFECT_"), COSTUME_BRIGHTNESS_(
			"COSTUME_BRIGHTNESS_"), COSTUME_SIZE_("COSTUME_SIZE_"), COSTUME_ROTATION_("COSTUME_ROTATION_"), COSTUME_LAYER_(
			"COSTUME_LAYER_");
	public final String sensorName;

	Sensors(String value) {
		this.sensorName = value;
	}

	public String getSensorName() {
		return sensorName;
	}

}
