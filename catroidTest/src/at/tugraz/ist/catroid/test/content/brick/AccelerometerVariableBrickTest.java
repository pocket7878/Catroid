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
package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.AccelerometerVariableBrick;
import at.tugraz.ist.catroid.content.bricks.AccelerometerVariableBrick.Dimension;

public class AccelerometerVariableBrickTest extends AndroidTestCase {

	public void testDimensionX() {
		Sprite sprite = new Sprite("test");
		AccelerometerVariableBrick accelerometerVariableBrick = new AccelerometerVariableBrick(sprite,
				Dimension.X_DIMENSION);

		accelerometerVariableBrick.execute();
		assertNotNull("Accelerometer value X is not obtained.", accelerometerVariableBrick.getAccelerometerValue());
	}

	public void testDimensionY() {
		Sprite sprite = new Sprite("test");
		AccelerometerVariableBrick accelerometerVariableBrick = new AccelerometerVariableBrick(sprite,
				Dimension.Y_DIMENSION);

		accelerometerVariableBrick.execute();
		assertNotNull("Accelerometer value Y is not obtained.", accelerometerVariableBrick.getAccelerometerValue());
	}

	public void testDimensionZ() {
		Sprite sprite = new Sprite("test");
		AccelerometerVariableBrick accelerometerVariableBrick = new AccelerometerVariableBrick(sprite,
				Dimension.Z_DIMENSION);

		accelerometerVariableBrick.execute();
		assertNotNull("Accelerometer value Z is not obtained.", accelerometerVariableBrick.getAccelerometerValue());
	}
}
