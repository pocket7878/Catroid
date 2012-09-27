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
import at.tugraz.ist.catroid.R;
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
		super(ScriptTabActivity.class);
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
			solo.sleep(1000);
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		this.project = null;
		super.tearDown();
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

		catKeyboardClicker.clickOnKey("rand");
		assertEquals(
				"Wrong button clicked",
				solo.getString(R.string.formula_editor_function_rand) + "( 0 , 1 )",
				text.getText().toString()
						.substring(0, (solo.getString(R.string.formula_editor_function_rand) + "( 0 , 1 )").length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		solo.goBack();
		solo.goBack();

	}

	public void testKeysFromFunctionKeyboard() {

		String functionString = "";

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
		functionString = solo.getString(R.string.formula_editor_function_cos) + "( 0 )";
		assertEquals("Wrong button clicked", functionString,
				text.getText().toString().substring(0, functionString.length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("sin");
		functionString = solo.getString(R.string.formula_editor_function_sin) + "( 0 )";
		assertEquals("Wrong button clicked", functionString,
				text.getText().toString().substring(0, functionString.length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("tan");
		functionString = solo.getString(R.string.formula_editor_function_tan) + "( 0 )";
		assertEquals("Wrong button clicked", functionString,
				text.getText().toString().substring(0, functionString.length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("ln");
		functionString = solo.getString(R.string.formula_editor_function_ln) + "( 0 )";
		assertEquals("Wrong button clicked", functionString,
				text.getText().toString().substring(0, functionString.length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("log");
		functionString = solo.getString(R.string.formula_editor_function_log) + "( 0 )";
		assertEquals("Wrong button clicked", functionString,
				text.getText().toString().substring(0, functionString.length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("sqrt");
		functionString = solo.getString(R.string.formula_editor_function_sqrt) + "( 0 )";
		assertEquals("Wrong button clicked", functionString,
				text.getText().toString().substring(0, functionString.length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("bracket");
		assertEquals("Wrong button clicked", "( 0 )", text.getText().toString().substring(0, 5));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("abs");
		functionString = solo.getString(R.string.formula_editor_function_abs) + "( 0 )";
		assertEquals("Wrong button clicked", functionString,
				text.getText().toString().substring(0, functionString.length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("round");
		functionString = solo.getString(R.string.formula_editor_function_round) + "( 0 )";
		assertEquals("Wrong button clicked", functionString,
				text.getText().toString().substring(0, functionString.length()));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("pi");
		functionString = solo.getString(R.string.formula_editor_function_pi);
		assertEquals("Wrong button clicked", functionString,
				text.getText().toString().substring(0, functionString.length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("e");
		functionString = solo.getString(R.string.formula_editor_function_e);
		assertEquals("Wrong button clicked", functionString,
				text.getText().toString().substring(0, functionString.length()));
		catKeyboardClicker.clickOnKey("del");

		solo.goBack();
		solo.goBack();
		//		solo.clickOnButton(2); //TODO what button2 
		//		solo.clickOnButton(2);
	}

	public void testKeysFromSensorKeyboard() {

		String sensorString = "";

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
		sensorString = solo.getString(R.string.formula_editor_sensor_x_acceleration);
		assertEquals("Wrong button clicked", sensorString, text.getText().toString()
				.substring(0, sensorString.length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("y-accel");
		sensorString = solo.getString(R.string.formula_editor_sensor_y_acceleration);
		assertEquals("Wrong button clicked", sensorString, text.getText().toString()
				.substring(0, sensorString.length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("z-accel");
		sensorString = solo.getString(R.string.formula_editor_sensor_z_acceleration);
		assertEquals("Wrong button clicked", sensorString, text.getText().toString()
				.substring(0, sensorString.length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("pitch");
		sensorString = solo.getString(R.string.formula_editor_sensor_pitch_orientation);
		assertEquals("Wrong button clicked", sensorString, text.getText().toString()
				.substring(0, sensorString.length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("roll");
		sensorString = solo.getString(R.string.formula_editor_sensor_roll_orientation);
		assertEquals("Wrong button clicked", sensorString, text.getText().toString()
				.substring(0, sensorString.length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("azimuth");
		sensorString = solo.getString(R.string.formula_editor_sensor_azimuth_orientation);
		assertEquals("Wrong button clicked", sensorString, text.getText().toString()
				.substring(0, sensorString.length()));
		catKeyboardClicker.clickOnKey("del");

		solo.goBack();
		solo.goBack();
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

	public void testChooseCostumeDialog() {

		String costumeString = "";

		//		solo.clickOnEditText(0);
		solo.clickOnEditText(0);
		solo.clickOnEditText(1);

		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		ArrayList<EditText> textList = solo.getCurrentEditTexts();
		EditText text = textList.get(textList.size() - 1);

		catKeyboardClicker.clickOnKey("costume");

		costumeString = solo.getString(R.string.formula_editor_costume_x);
		solo.clickOnText(costumeString);
		solo.sleep(100);// without sleep it crashes x.x
		assertEquals("Wrong button clicked", costumeString,
				text.getText().toString().substring(0, costumeString.length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("costume");
		costumeString = solo.getString(R.string.formula_editor_costume_y);
		solo.clickOnText(costumeString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", costumeString,
				text.getText().toString().substring(0, costumeString.length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("costume");
		costumeString = solo.getString(R.string.formula_editor_costume_ghosteffect);
		solo.clickOnText(costumeString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", costumeString,
				text.getText().toString().substring(0, costumeString.length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("costume");

		costumeString = solo.getString(R.string.formula_editor_costume_brightness);
		solo.clickOnText(costumeString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", costumeString,
				text.getText().toString().substring(0, costumeString.length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("costume");

		costumeString = solo.getString(R.string.formula_editor_costume_size);
		solo.clickOnText(costumeString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", costumeString,
				text.getText().toString().substring(0, costumeString.length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("costume");

		costumeString = solo.getString(R.string.formula_editor_costume_rotation);
		solo.clickOnText(costumeString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", costumeString,
				text.getText().toString().substring(0, costumeString.length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("costume");

		costumeString = solo.getString(R.string.formula_editor_costume_layer);
		solo.clickOnText(costumeString);
		solo.sleep(100);
		assertEquals("Wrong button clicked", costumeString,
				text.getText().toString().substring(0, costumeString.length()));
		catKeyboardClicker.clickOnKey("del");

		catKeyboardClicker.clickOnKey("costume");
		solo.clickOnText("Cancel");
		solo.sleep(100);
		assertEquals("Wrong button clicked", "", "");

		solo.goBack();
		solo.goBack();
	}

	//	public void testOrientationChanges() {
	//
	//		solo.clickOnEditText(0);
	//		solo.clickOnEditText(1);
	//		solo.setActivityOrientation(Solo.LANDSCAPE);
	//		solo.sleep(2500); //orientation change takes forever...		
	//		catKeyboardClicker.clickOnKey("del");
	//		catKeyboardClicker.clickOnKey("del");
	//		catKeyboardClicker.clickOnKey("del");
	//		catKeyboardClicker.clickOnKey("del");
	//
	//		//		catKeyboardClicker.clickOnKey("costume");
	//		//		solo.setActivityOrientation(Solo.LANDSCAPE);
	//		//		solo.sleep(2500); //orientation change takes forever...		
	//		//		solo.clickOnText("COSTUME_X_");
	//		//		solo.sleep(100);// without sleep it crashes x.x
	//		//
	//		//		EditText text = solo.getEditText(0);
	//		//		assertEquals("Wrong button clicked", "COSTUME_X_", text.getText().toString()
	//		//				.substring(0, "COSTUME_X_".length()));
	//		//		catKeyboardClicker.clickOnKey("del");
	//		//		solo.sleep(100);
	//		//
	//		//		catKeyboardClicker.clickOnKey("costume");
	//		//		solo.setActivityOrientation(Solo.PORTRAIT);
	//		//		solo.sleep(2500);
	//		//		solo.clickOnText("COSTUME_LAYER_");
	//		//		solo.sleep(100);
	//		//		text = solo.getEditText(0);
	//		//		assertEquals("Wrong button clicked", "COSTUME_LAYER_",
	//		//				text.getText().toString().substring(0, "COSTUME_LAYER_".length()));
	//		//		catKeyboardClicker.clickOnKey("del");
	//		//		solo.sleep(100);
	//
	//		solo.goBack();
	//		solo.goBack();
	//
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
