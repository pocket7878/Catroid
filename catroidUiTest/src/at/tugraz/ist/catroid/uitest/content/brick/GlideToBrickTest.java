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

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.GlideToBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;
import at.tugraz.ist.catroid.ui.fragment.ScriptFragment;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class GlideToBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Solo solo;
	private static final int X_POSITION = 800;
	private static final int Y_POSITION = 0;
	private static final int DURATION = 1000;
	private Project project;
	private Brick glideToBrick;

	public GlideToBrickTest() {
		super(ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testNumberInput() {

		ScriptTabActivity activity = (ScriptTabActivity) solo.getCurrentActivity();
		ScriptFragment fragment = (ScriptFragment) activity.getTabFragment(ScriptTabActivity.INDEX_TAB_SCRIPTS);
		BrickAdapter adapter = fragment.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2 + 1, solo.getCurrentListViews().get(0).getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.brick_glide_to)));

		UiTestUtils.testBrickWithFormulaEditor(solo, R.id.brick_glide_to_duration_text_view, 8, DURATION,
				"durationInSeconds", glideToBrick);

		UiTestUtils.testBrickWithFormulaEditor(solo, R.id.brick_glide_to_x_text_view, 12, X_POSITION, "xDestination",
				glideToBrick);

		UiTestUtils.testBrickWithFormulaEditor(solo, R.id.brick_glide_to_y_text_view, 14, Y_POSITION, "yDestination",
				glideToBrick);

		//		View formulaTextViewView = solo.getView(R.id.brick_glide_to_duration_text_view);
		//		solo.clickOnView(formulaTextViewView);
		//
		//		CatKeyboardClicker catKeyboardClicker = new CatKeyboardClicker(solo);
		//		catKeyboardClicker.clickOnKey("del");
		//		for (char item : (String.valueOf(DURATION).toCharArray())) {
		//			catKeyboardClicker.clickOnKey("" + item);
		//		}
		//
		//		solo.goBack();
		//		solo.sleep(200);
		//
		//		formulaTextViewView = solo.getView(R.id.brick_glide_to_x_text_view);
		//		solo.clickOnView(formulaTextViewView);
		//
		//		catKeyboardClicker.clickOnKey("del");
		//		for (char item : (String.valueOf(X_POSITION).toCharArray())) {
		//			catKeyboardClicker.clickOnKey("" + item);
		//		}
		//		solo.goBack();
		//		solo.sleep(200);
		//
		//		formulaTextViewView = solo.getView(R.id.brick_glide_to_y_text_view);
		//		solo.clickOnView(formulaTextViewView);
		//
		//		catKeyboardClicker.clickOnKey("del");
		//		for (char item : (String.valueOf(Y_POSITION).toCharArray())) {
		//			catKeyboardClicker.clickOnKey("" + item);
		//		}
		//		solo.sleep(200);

		//		for (int i = 0; i < 15; i++) {
		//			Log.i("info", "i:" + i + ": " + solo.getText(i).getText().toString());
		//		}

		//		assertEquals("Text not updated within FormulaEditor", DURATION,
		//				Integer.parseInt(solo.getText(4).getText().toString().replace(' ', '\0')));
		//		Log.i("info", "After DURATION check");
		//		assertEquals("Text not updated within FormulaEditor", X_POSITION,
		//				Integer.parseInt(solo.getText(8).getText().toString().replace(' ', '\0')));
		//		Log.i("info", "After X_POSITION check");
		//		assertEquals("Text not updated within FormulaEditor", Y_POSITION,
		//				Integer.parseInt(solo.getText(10).getText().toString().replace(' ', '\0')));

		//		solo.goBack();
		//		solo.sleep(200);
		//
		//		Log.i("info", "After TextView Check 2");
		//
		//		ProjectManager manager = ProjectManager.getInstance();
		//		List<Brick> brickList = manager.getCurrentSprite().getScript(0).getBrickList();
		//		GlideToBrick glideToBrick = (GlideToBrick) brickList.get(0);
		//
		//		Formula formula = (Formula) UiTestUtils.getPrivateField("durationInSeconds", glideToBrick);
		//		float temp = formula.interpretFloat();
		//
		//		assertEquals("Wrong duration input in Glide to brick", Math.round(DURATION * 1000), Math.round(temp * 1000));
		//		formula = (Formula) UiTestUtils.getPrivateField("xDestination", glideToBrick);
		//		int temp2 = formula.interpretInteger();
		//		assertEquals("Wrong x input in Glide to brick", X_POSITION, temp2);
		//
		//		formula = (Formula) UiTestUtils.getPrivateField("yDestination", glideToBrick);
		//		temp2 = formula.interpretInteger();
		//		assertEquals("Wrong y input in Glide to brick", Y_POSITION, temp2);
	}

	//	public void testResizeInputFields() {
	//		UiTestUtils.goToHomeActivity(getActivity());
	//		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
	//		createProject();
	//		solo.sleep(200);
	//		solo.clickOnText(getActivity().getString(R.string.current_project_button));
	//		solo.waitForActivity(ProjectActivity.class.getSimpleName());
	//		solo.clickOnText(solo.getCurrentListViews().get(0).getItemAtPosition(0).toString());
	//		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
	//
	//		UiTestUtils.testDoubleEditText(solo, 0, 3, 1.1, 60, true);
	//		UiTestUtils.testDoubleEditText(solo, 0, 3, 12345.67, 60, true);
	//		UiTestUtils.testDoubleEditText(solo, 0, 3, -1, 60, true);
	//		UiTestUtils.testDoubleEditText(solo, 0, 3, 12345.678, 60, false);
	//
	//		for (int i = 1; i < 3; i++) {
	//			UiTestUtils.testIntegerEditText(solo, i, 3, 1, 60, true);
	//			UiTestUtils.testIntegerEditText(solo, i, 3, 123456, 60, true);
	//			UiTestUtils.testIntegerEditText(solo, i, 3, -1, 60, true);
	//			UiTestUtils.testIntegerEditText(solo, i, 3, 1234567, 60, false);
	//		}
	//	}

	private void createProject() {

		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		glideToBrick = new GlideToBrick(sprite, X_POSITION, Y_POSITION, DURATION);
		script.addBrick(glideToBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
