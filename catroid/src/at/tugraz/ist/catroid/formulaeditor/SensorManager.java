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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class SensorManager {
	private static Input sensors = null;

	public static void setSensorSourceForNextCall(Input source) {
		sensors = source;
	}

	public static Double getSensorValue(String sensorName) {
		if (sensors == null) {
			sensors = Gdx.input;
		}
		Double sensorValue = 0.0;
		if (sensorName.equals("X_ACCELERATION_")) {
			sensorValue = Double.valueOf(sensors.getAccelerometerX());
		}
		if (sensorName.equals("Y_ACCELERATION_")) {
			sensorValue = Double.valueOf(-sensors.getAccelerometerY());
		}
		if (sensorName.equals("Z_ACCELERATION_")) {
			sensorValue = Double.valueOf(-sensors.getAccelerometerZ());
		}
		if (sensorName.equals("AZIMUTH_ORIENTATION_")) {
			sensorValue = Double.valueOf(sensors.getAzimuth());
		}
		if (sensorName.equals("PITCH_ORIENTATION_")) {
			sensorValue = Double.valueOf(sensors.getPitch());
		}
		if (sensorName.equals("ROLL_ORIENTATION_")) {
			sensorValue = Double.valueOf(-sensors.getRoll());
		}
		sensors = null;
		return sensorValue;
	}

}
