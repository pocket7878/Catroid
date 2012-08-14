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
package at.tugraz.ist.catroid.uitest.content.brick;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.IfThenElseBrick;
import at.tugraz.ist.catroid.content.bricks.IfThenElseBrick.LogicOperator;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Daniel Burtscher
 * 
 */
public class IfThenElseBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private Project project;
	private IfThenElseBrick ifThenElseBrick;

	public IfThenElseBrickTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	@Smoke
	public void testIfThenElseBrick() throws InterruptedException {

		solo.pressSpinnerItem(0, 1);
		solo.sleep(200);
		String logicOperatorStringArray = (LogicOperator.values()[1]).getLogicOperator();
		assertEquals("Wrong selection", logicOperatorStringArray, solo.getCurrentSpinners().get(0).getSelectedItem()
				.toString());

		solo.sleep(300);

		int firstValue = 987;
		int secondValue = 654;

		UiTestUtils.testBrickWithFormulaEditor(solo, 0, 2, firstValue, "conditionToCheckFormula1", ifThenElseBrick);

		solo.sleep(300);

		UiTestUtils.testBrickWithFormulaEditor(solo, 1, 2, secondValue, "conditionToCheckFormula2", ifThenElseBrick);
	}

	//	public void testIfInputFields() {
	//		ProjectManager.getInstance().deleteCurrentProject();
	//		createTestProject();
	//
	//		for (int i = 0; i < 2; i++) {
	//			UiTestUtils.testIntegerEditText(solo, i, 2, 1, 60, true);
	//			UiTestUtils.testIntegerEditText(solo, i, 2, 12345, 60, true);
	//			UiTestUtils.testIntegerEditText(solo, i, 2, -1, 60, true);
	//			UiTestUtils.testIntegerEditText(solo, i, 2, 123456, 60, false);
	//		}
	//	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		ifThenElseBrick = new IfThenElseBrick(sprite, 3, LogicOperator.MORE_THAN, 3);
		script.addBrick(ifThenElseBrick);
		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	//	private void createTestProject() {
	//		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
	//		Sprite sprite = new Sprite("cat");
	//		Script script = new StartScript(sprite);
	//		ifThenElseBrick = new IfThenElseBrick(sprite, 0, LogicOperator.MORE_THAN, 0);
	//		script.addBrick(ifThenElseBrick);
	//
	//		sprite.addScript(script);
	//		project.addSprite(sprite);
	//
	//		ProjectManager.getInstance().setProject(project);
	//		ProjectManager.getInstance().setCurrentSprite(sprite);
	//		ProjectManager.getInstance().setCurrentScript(script);
	//	}
}
