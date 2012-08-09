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

import java.util.ArrayList;

import android.test.suitebuilder.annotation.Smoke;
import android.widget.EditText;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ChangeSizeByNBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class CatKeyboardTest extends android.test.ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Project project;
	private Solo solo;
	private Sprite firstSprite;
	private Brick changeBrick;
	private CatKeyboardClicker catKeyboardClicker;

	public CatKeyboardTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {

		createProject("testProjectCatKeyboard");
		this.solo = new Solo(getInstrumentation(), getActivity());
		catKeyboardClicker = new CatKeyboardClicker(solo);

	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		this.project = null;
		super.tearDown();
		Thread.sleep(1000); //This has to be sleep as solo is destroyed. Not sleeping might cause the next test to fail
	}

	@Smoke
	public void testKeysFromNumbersKeyboard() {

		//		solo.clickOnEditText(0);
		solo.clickOnEditText(0);
		solo.clickOnEditText(1);// View.performclick()

		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("9");
		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		//		Log.i("info", "text.size()" + textList.size());
		EditText text = textList.get(textList.size() - 1);
		//		Log.i("info", "textstring" + text.getText().toString());
		assertEquals("Wrong button clicked", "9", text.getText().toString().substring(0, 1));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("8");
		assertEquals("Wrong button clicked", "8", text.getText().toString().substring(0, 1));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("7");
		assertEquals("Wrong button clicked", "7", text.getText().toString().substring(0, 1));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("6");
		assertEquals("Wrong button clicked", "6", text.getText().toString().substring(0, 1));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("5");
		assertEquals("Wrong button clicked", "5", text.getText().toString().substring(0, 1));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("4");
		assertEquals("Wrong button clicked", "4", text.getText().toString().substring(0, 1));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("3");
		assertEquals("Wrong button clicked", "3", text.getText().toString().substring(0, 1));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("2");
		assertEquals("Wrong button clicked", "2", text.getText().toString().substring(0, 1));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("1");
		assertEquals("Wrong button clicked", "1", text.getText().toString().substring(0, 1));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("+");
		assertEquals("Wrong button clicked", "+", text.getText().toString().substring(0, 1));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("-");
		assertEquals("Wrong button clicked", "-", text.getText().toString().substring(0, 1));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("*");
		assertEquals("Wrong button clicked", "*", text.getText().toString().substring(0, 1));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("/");
		assertEquals("Wrong button clicked", "/", text.getText().toString().substring(0, 1));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("^");
		assertEquals("Wrong button clicked", "^", text.getText().toString().substring(0, 1));
		catKeyboardClicker.clickOnKey("del");

		solo.clickOnButton(2);
		solo.clickOnButton(2);

	}

	public void testKeysFromFunctionKeyboard() {

		solo.clickOnEditText(0);
		//		solo.clickOnEditText(0);
		solo.clickOnEditText(1);

		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		EditText text = textList.get(textList.size() - 1);

		catKeyboardClicker.clickOnKey("keyboardswitch");
		catKeyboardClicker.clickOnKey("cos");
		assertEquals("Wrong button clicked", "cos( 0 )", text.getText().toString().substring(0, "cos( 0 )".length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("sin");
		assertEquals("Wrong button clicked", "sin( 0 )", text.getText().toString().substring(0, "sin( 0 )".length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("tan");
		assertEquals("Wrong button clicked", "tan( 0 )", text.getText().toString().substring(0, "tan( 0 )".length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("ln");
		assertEquals("Wrong button clicked", "ln( 0 )", text.getText().toString().substring(0, "ln( 0 )".length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("log");
		assertEquals("Wrong button clicked", "log( 0 )", text.getText().toString().substring(0, "log( 0 )".length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("sqrt");
		assertEquals("Wrong button clicked", "sqrt( 0 )", text.getText().toString().substring(0, "sqrt( 0 )".length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("rand");
		assertEquals("Wrong button clicked", "rand( 0 , 1 )",
				text.getText().toString().substring(0, "rand( 0 , 1 )".length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("pi");
		assertEquals("Wrong button clicked", "pi", text.getText().toString().substring(0, "pi".length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("e");
		assertEquals("Wrong button clicked", "e", text.getText().toString().substring(0, "e".length()));
		catKeyboardClicker.clickOnKey("del");

		solo.clickOnButton(2);
		solo.clickOnButton(2);
	}

	public void testKeysFromSensorKeyboard() {

		//		solo.clickOnEditText(0);
		solo.clickOnEditText(0);
		solo.clickOnEditText(1);

		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		EditText text = textList.get(textList.size() - 1);

		catKeyboardClicker.clickOnKey("keyboardswitch");
		catKeyboardClicker.clickOnKey("keyboardswitch");
		catKeyboardClicker.clickOnKey("x-accel");
		assertEquals("Wrong button clicked", "X_ACCELERATION_",
				text.getText().toString().substring(0, "X_ACCELERATION_".length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("y-accel");
		assertEquals("Wrong button clicked", "Y_ACCELERATION_",
				text.getText().toString().substring(0, "Y_ACCELERATION_".length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("z-accel");
		assertEquals("Wrong button clicked", "Z_ACCELERATION_",
				text.getText().toString().substring(0, "Z_ACCELERATION_".length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("pitch");
		assertEquals("Wrong button clicked", "PITCH_ORIENTATION_",
				text.getText().toString().substring(0, "PITCH_ORIENTATION_".length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("roll");
		assertEquals("Wrong button clicked", "ROLL_ORIENTATION_",
				text.getText().toString().substring(0, "ROLL_ORIENTATION_".length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("azimuth");
		assertEquals("Wrong button clicked", "AZIMUTH_ORIENTATION_",
				text.getText().toString().substring(0, "AZIMUTH_ORIENTATION_".length()));
		catKeyboardClicker.clickOnKey("del");

		solo.clickOnButton(2);
		solo.clickOnButton(2);
	}

	//	public void testLanguageKeys() {
	//
	//		solo.clickOnEditText(0);
	//		solo.clickOnEditText(0);
	//		solo.clickOnEditText(1);
	//
	//		catKeyboardClicker.clickOnKey("del");
	//		catKeyboardClicker.clickOnKey("del");
	//		catKeyboardClicker.clickOnKey("del");
	//		catKeyboardClicker.clickOnKey("del");
	//
	//		Log.i("info", "searchText for ',' " + solo.searchText(","));
	//		Log.i("info", "searchButton for ',' " + solo.searchButton(","));
	//		Log.i("info", "imageButton size: " + solo.getCurrentImageButtons().size());
	//		Log.i("info", "imageViews size: " + solo.getCurrentImageViews().size());
	//		Log.i("info", "listViews size: " + solo.getCurrentListViews().size());
	//		Log.i("info", "allOpenedActivities size: " + solo.getAllOpenedActivities().size());
	//		Log.i("info", "currentActivity: " + solo.getCurrentActivity());
	//		Log.i("info", "allCurrentButtons size: " + solo.getCurrentButtons().size());
	//
	//		assertEquals(solo.searchText(","), true);
	//	}

	private void createProject(String projectName) throws InterruptedException {

		this.project = new Project(null, projectName);
		firstSprite = new Sprite("nom nom nom");
		Script startScript1 = new StartScript(firstSprite);
		changeBrick = new ChangeSizeByNBrick(firstSprite, 0);
		firstSprite.addScript(startScript1);
		startScript1.addBrick(changeBrick);
		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

	}

}
