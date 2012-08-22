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
package at.tugraz.ist.catroid.test.formulaeditor;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.formulaeditor.Sensors;

public class CostumeSensorValuesInterpretationTest extends AndroidTestCase {

	private static final float COSTUME_ALPHA = 0.5f;
	private static final float COSTUME_Y_POSITION = 23.4f;
	private static final float COSTUME_X_POSITION = 5.6f;
	private static final float COSTUME_BRIGHTNESS = 0.7f;
	private static final float COSTUME_SCALE = 90.3f;
	private static final float COSTUME_ROTATION = 30.7f;
	private static final int COSTUME_ZPOSITION = 3;
	private static final float DELTA = 0.01f;

	@Override
	protected void setUp() {
		Sprite testSprite = new Sprite("sprite");
		ProjectManager.getInstance().setCurrentSprite(testSprite);

		testSprite.costume.setXPosition(COSTUME_X_POSITION);
		testSprite.costume.setYPosition(COSTUME_Y_POSITION);
		testSprite.costume.setAlphaValue(COSTUME_ALPHA);
		testSprite.costume.setBrightnessValue(COSTUME_BRIGHTNESS);
		testSprite.costume.scaleX = COSTUME_SCALE;
		testSprite.costume.scaleY = COSTUME_SCALE;
		testSprite.costume.rotation = COSTUME_ROTATION;
		testSprite.costume.zPosition = COSTUME_ZPOSITION;
	}

	public void testCostumeSensorValues() {

		Formula costumeXPositionFormula = new Formula(Sensors.COSTUME_X_.sensorName);
		assertEquals("Formula interpretation is not as expected", COSTUME_X_POSITION,
				costumeXPositionFormula.interpretFloat(), DELTA);

		Formula costumeYPositionFormula = new Formula(Sensors.COSTUME_Y_.sensorName);
		assertEquals("Formula interpretation is not as expected", COSTUME_Y_POSITION,
				costumeYPositionFormula.interpretFloat(), DELTA);

		Formula costumeAlphaValueFormula = new Formula(Sensors.COSTUME_GHOSTEFFECT_.sensorName);
		assertEquals("Formula interpretation is not as expected", COSTUME_ALPHA,
				costumeAlphaValueFormula.interpretFloat(), DELTA);

		Formula costumeBrightnessFormula = new Formula(Sensors.COSTUME_BRIGHTNESS_.sensorName);
		assertEquals("Formula interpretation is not as expected", COSTUME_BRIGHTNESS,
				costumeBrightnessFormula.interpretFloat(), DELTA);

		Formula costumeScaleFormula = new Formula(Sensors.COSTUME_SIZE_.sensorName);
		assertEquals("Formula interpretation is not as expected", COSTUME_SCALE, costumeScaleFormula.interpretFloat(),
				DELTA);

		Formula costumeRotateFormula = new Formula(Sensors.COSTUME_ROTATION_.sensorName);
		assertEquals("Formula interpretation is not as expected", COSTUME_ROTATION,
				costumeRotateFormula.interpretFloat(), DELTA);

		Formula costumeZPositionFormula = new Formula(Sensors.COSTUME_LAYER_.sensorName);
		assertEquals("Formula interpretation is not as expected", COSTUME_ZPOSITION,
				costumeZPositionFormula.interpretInteger());

	}
}
