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
package at.tugraz.ist.catroid.uitest.formulaeditor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ChangeSizeByNBrick;
import at.tugraz.ist.catroid.formulaeditor.CalcGrammarParser;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.formulaeditor.FormulaElement;
import at.tugraz.ist.catroid.formulaeditor.SensorManager;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.badlogic.gdx.Input;
import com.jayway.android.robotium.solo.Solo;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Input.class)
public class SensorTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;
	private Project project;
	private Sprite firstSprite;
	private Brick changeBrick;
	Script startScript1;

	public SensorTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		//createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	@Test
	public void testSensors() {
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		int expectedX = 995;
		int expectedY = 990;
		int expectedZ = 985;
		int expectedRoll = 980;
		int expectedPitch = 975;
		int expectedAzimuth = 970;

		createProject();

		Formula formula = createFormulaWithSensor("X_ACCELERATION_");
		ChangeSizeByNBrick xBrick = new ChangeSizeByNBrick(firstSprite, formula);
		startScript1.addBrick(xBrick);

		Formula formula1 = createFormulaWithSensor("Y_ACCELERATION_");
		ChangeSizeByNBrick yBrick = new ChangeSizeByNBrick(firstSprite, formula1);
		startScript1.addBrick(yBrick);

		Formula formula2 = createFormulaWithSensor("Z_ACCELERATION_");
		ChangeSizeByNBrick zBrick = new ChangeSizeByNBrick(firstSprite, formula2);
		startScript1.addBrick(zBrick);

		Formula formula3 = createFormulaWithSensor("AZIMUTH_ORIENTATION_");
		ChangeSizeByNBrick azimuthBrick = new ChangeSizeByNBrick(firstSprite, formula3);
		startScript1.addBrick(azimuthBrick);

		Formula formula4 = createFormulaWithSensor("PITCH_ORIENTATION_");
		ChangeSizeByNBrick pitchBrick = new ChangeSizeByNBrick(firstSprite, formula4);
		startScript1.addBrick(pitchBrick);

		Formula formula5 = createFormulaWithSensor("ROLL_ORIENTATION_");
		ChangeSizeByNBrick rollBrick = new ChangeSizeByNBrick(firstSprite, formula5);
		startScript1.addBrick(rollBrick);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		assertEquals("Sensor value is wrong", tryMock("getAccelerometerX", formula, expectedX), expectedX);
		assertEquals("Sensor value is wrong", tryMock("getAccelerometerY", formula1, expectedY), -expectedY);
		assertEquals("Sensor value is wrong", tryMock("getAccelerometerZ", formula2, expectedZ), -expectedZ);
		assertEquals("Sensor value is wrong", tryMock("getAzimuth", formula3, expectedAzimuth), expectedAzimuth);
		assertEquals("Sensor value is wrong", tryMock("getPitch", formula4, expectedPitch), expectedPitch);
		assertEquals("Sensor value is wrong", tryMock("getRoll", formula5, expectedRoll), -expectedRoll);

	}

	private int tryMock(String method, Formula formula, int expectedResult) {
		Input mock = PowerMock.createPartialMock(Input.class, method);
		SensorManager.setSensorSourceForNextCall(mock);

		try {
			PowerMock.expectPrivate(mock, method).andReturn(expectedResult);
			PowerMock.replayAll();
			return formula.interpretInteger();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;

	}

	private Formula createFormulaWithSensor(String sensor) {

		CalcGrammarParser parser = CalcGrammarParser.getFormulaParser(sensor);
		FormulaElement root = parser.parseFormula();
		Formula formula = new Formula(root);

		return formula;
	}

	private void createProject() {
		this.project = new Project(null, "SensorTestProject");
		firstSprite = new Sprite("zwoosh");
		startScript1 = new StartScript(firstSprite);
		changeBrick = new ChangeSizeByNBrick(firstSprite, 10);
		firstSprite.addScript(startScript1);
		startScript1.addBrick(changeBrick);
		project.addSprite(firstSprite);

	}
}
