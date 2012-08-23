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

import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.formulaeditor.FormulaEditorHistory;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class FormulaEditorFragmentTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private Project project;
	private PlaceAtBrick placeAtBrick;
	CatKeyboardClicker catKeyboardClicker = null;
	private static final int INITIAL_X = 8;
	private static final int INITIAL_Y = 7;

	private static final int X_POS_EDIT_TEXT_ID = 0;
	private static final int Y_POS_EDIT_TEXT_ID = 1;
	private static final int FORMULA_EDITOR_EDIT_TEXT_ID = 2;

	public FormulaEditorFragmentTest() {
		super(ScriptTabActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		catKeyboardClicker = new CatKeyboardClicker(solo);
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		placeAtBrick = new PlaceAtBrick(sprite, INITIAL_X, INITIAL_Y);
		script.addBrick(placeAtBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	public void testChangeFormula() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clickOnKey("1");
		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		solo.sleep(50);
		assertTrue("Saved changes message not found!",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		solo.goBack();
		solo.sleep(100);
		assertEquals("Value not saved!", "1 ", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("+");

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		assertTrue("Fix error message not found!", solo.searchText(solo.getString(R.string.formula_editor_parse_fail)));
		solo.sleep(500);
		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		assertTrue("Changes saved message not found!",
				solo.searchText(solo.getString(R.string.formula_editor_changes_discarded)));

		solo.goBack();
		solo.goBack();

	}

	public void testOnTheFlyUpdateOfBrickEditText() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clickOnKey("1");

		assertEquals("Wrong text in FormulaEditor", "1 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());
		assertEquals("Wrong text in X EditText", "1 ", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());

		catKeyboardClicker.clickOnKey("2");

		assertEquals("Wrong text in FormulaEditor", "12 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());
		assertEquals("Wrong text in X EditText", "12 ", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());

		solo.goBack();
		solo.sleep(50);
		assertEquals("Wrong text in X EditText", "12 ", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString()); //TODO changed it to 12 because we expect that or?
	}

	public void testDialogAndSimpleInterpretation() {
		//		Note solo.enterText() modifications to the text are undetectable to FormulaEditorEditText.
		//		Text via solo.enterText() *must* be longer than the original text!!! To be safe use CatKeyboardKlicker to clear!
		//		Use CatKeyboardClicker for full functionality, is a lot slower and inconvenient! Will do just fine here without

		String newXFormula = "10 + 12 - 2 * 3 - 4 ";
		int newXValue = 10 + 12 - 2 * 3 - 4;
		String newYFormula = "rand( cos( 90 ) , 10 * sin( 90 ) ) ";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clearEditTextWithCursorBehindLastCharacterOnlyQuickly(FORMULA_EDITOR_EDIT_TEXT_ID);

		solo.sleep(50);
		catKeyboardClicker.enterText("999++" + newXFormula);
		solo.goBack();
		assertTrue("Save failed toast not found", solo.searchText(solo.getString(R.string.formula_editor_parse_fail)));

		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.enterText(newXFormula);
		solo.goBack();
		solo.sleep(200);
		assertTrue("Changes saved toast not found",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clickOnKey("del");
		solo.enterText(FORMULA_EDITOR_EDIT_TEXT_ID, newYFormula);

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		assertTrue("Changes saved toast not found",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		assertEquals("Wrong text in FormulaEditor", newXFormula, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		solo.sleep(50);
		assertEquals("Wrong text in FormulaEditor", newYFormula, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.goBack();
		solo.sleep(300);

		//Interpretation test
		Formula formula = (Formula) UiTestUtils.getPrivateField("xPosition", placeAtBrick);
		assertEquals("Wrong text in field", newXValue, formula.interpretInteger());

		formula = (Formula) UiTestUtils.getPrivateField("yPosition", placeAtBrick);

		int newYValue = formula.interpretInteger();
		Log.i("info", "" + newYValue);
		assertTrue("Wrong text in field", newYValue >= 0 && newYValue <= 10);

	}

	public void testUndo() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clearEditTextWithCursorBehindLastCharacterOnlyQuickly(FORMULA_EDITOR_EDIT_TEXT_ID);

		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("-");
		catKeyboardClicker.clickOnKey("2");
		catKeyboardClicker.clickOnKey("*");
		catKeyboardClicker.switchToFunctionKeyboard();
		catKeyboardClicker.clickOnKey("cos");
		catKeyboardClicker.clickOnKey("sin");
		catKeyboardClicker.clickOnKey("tan");

		assertEquals("Wrong text in field", "1 - 2 * cos( sin( tan( 0 ) ) ) ",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_undo);
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 * cos( sin( 0 ) ) ",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_undo);
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 * cos( 0 ) ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_undo);
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 * ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_undo);
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		solo.goBack();

		assertEquals("Undo did something wrong", "1 - 2", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());

		solo.goBack();
		solo.goBack();

	}

	public void testUndoRedo() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		//catKeyboardClicker.clearEditTextWithOnlyNumbersQuickly(FORMULA_EDITOR_EDIT_TEXT_ID);

		catKeyboardClicker.clickOnKey("9");
		catKeyboardClicker.clickOnKey("-");
		catKeyboardClicker.clickOnKey("8");
		catKeyboardClicker.clickOnKey("*");
		catKeyboardClicker.clickOnKey("7");
		catKeyboardClicker.clickOnKey("+");
		catKeyboardClicker.clickOnKey("9");

		assertEquals("Wrong text in field", "9 - 8 * 7 + 9 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		for (int i = 0; i < 7; i++) {
			UiTestUtils.clickOnLinearLayout(solo, R.id.menu_undo);
		}

		solo.sleep(50);
		assertEquals("Undo did something wrong", INITIAL_X + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		UiTestUtils.clickOnLinearLayout(solo, R.id.menu_undo);

		assertEquals("Undo did something wrong", INITIAL_X + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		for (int i = 0; i < 7; i++) {
			UiTestUtils.clickOnLinearLayout(solo, R.id.menu_redo);
		}

		solo.sleep(50);
		assertEquals("Undo did something wrong", "9 - 8 * 7 + 9 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.goBack();
		solo.goBack();

	}

	public void testUndoLimit() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		int maxHistoryElements = (Integer) UiTestUtils.getPrivateField("MAXIMUM_HISTORY_LENGTH",
				new FormulaEditorHistory(null, 0, 0, 0));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("1");

		String searchString = "";
		for (int i = 0; i < maxHistoryElements; i++) {
			catKeyboardClicker.clickOnKey("+");
			searchString += " +";
		}
		solo.sleep(50);

		assertEquals("Wrong text in field", "1" + searchString + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		for (int i = 0; i < maxHistoryElements + 2; i++) {
			UiTestUtils.clickOnLinearLayout(solo, R.id.menu_undo);
		}

		assertEquals("Wrong text in field", "1 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		for (int i = 0; i < maxHistoryElements + 2; i++) {
			UiTestUtils.clickOnLinearLayout(solo, R.id.menu_redo);
		}

		assertEquals("Wrong text in field", "1" + searchString + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.goBack();
		solo.goBack();

	}

	public void testOrientationChanges() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.switchToNumberKeyboard();
		catKeyboardClicker.clickOnKey("rand");

		solo.setActivityOrientation(Solo.LANDSCAPE);

		solo.sleep(2500); //orientation change takes forever...
		assertEquals("Wrong text after oprientation switch", "rand( 0 , 1 ) ", solo.getEditText(0).getText().toString());

		solo.goBack();

		solo.sleep(200);
		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		assertEquals("Wrong text after oprientation switch", "rand( 0 , 1 ) ", solo.getEditText(X_POS_EDIT_TEXT_ID)
				.getText().toString());
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(500);

		catKeyboardClicker.switchToFunctionKeyboard();
		catKeyboardClicker.clickOnKey("sin");
		catKeyboardClicker.clickOnKey("cos");

		solo.sleep(500);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(1500);
		solo.setActivityOrientation(Solo.PORTRAIT);

		assertEquals("Wrong text after oprientation switch", "sin( cos( 0 ) ) ",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		solo.goBack();
		solo.sleep(2000);
		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		assertEquals("Wrong text after oprientation switch", "sin( cos( 0 ) ) ",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		solo.goBack();

	}

	public void testKeyboardSwipeAndSwipeBar() {
		DisplayMetrics currentDisplayMetrics = new DisplayMetrics();
		solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(currentDisplayMetrics);

		int displayWidth = currentDisplayMetrics.widthPixels;
		int displayHeight = currentDisplayMetrics.heightPixels;

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.clickOnText(solo.getString(R.string.functions));
		catKeyboardClicker.clickOnKey("sin");
		assertEquals("Wrong keyboard after keyboard switch", "sin( 0 ) ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(X_POS_EDIT_TEXT_ID);

		solo.clickOnText(solo.getString(R.string.numbers));
		catKeyboardClicker.clickOnKey("1");
		assertEquals("Wrong keyboard after keyboard switch", "1 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(X_POS_EDIT_TEXT_ID);

		solo.clickOnText(solo.getString(R.string.sensors));
		catKeyboardClicker.clickOnKey("x-accel");
		assertEquals("Wrong keyboard after keyboard switch", "X_ACCELERATION_ ",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(X_POS_EDIT_TEXT_ID);

		solo.drag(10, displayWidth - 10, displayHeight - 50, displayHeight - 50, 100);
		catKeyboardClicker.clickOnKey("1");
		assertEquals("Wrong keyboard after keyboard switch", "1 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(X_POS_EDIT_TEXT_ID);

		solo.drag(10, displayWidth - 10, displayHeight - 50, displayHeight - 50, 100);
		catKeyboardClicker.clickOnKey("sin");
		assertEquals("Wrong keyboard after keyboard switch", "sin( 0 ) ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(X_POS_EDIT_TEXT_ID);

		solo.drag(10, displayWidth - 10, displayHeight - 50, displayHeight - 50, 100);
		catKeyboardClicker.clickOnKey("x-accel");
		assertEquals("Wrong keyboard after keyboard switch", "X_ACCELERATION_ ",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(X_POS_EDIT_TEXT_ID);

		solo.drag(displayWidth - 10, 10, displayHeight - 50, displayHeight - 50, 100);
		catKeyboardClicker.clickOnKey("sin");
		assertEquals("Wrong keyboard after keyboard switch", "sin( 0 ) ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(X_POS_EDIT_TEXT_ID);

		solo.drag(displayWidth - 10, 10, displayHeight - 50, displayHeight - 50, 100);
		catKeyboardClicker.clickOnKey("1");
		assertEquals("Wrong keyboard after keyboard switch", "1 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(X_POS_EDIT_TEXT_ID);

		solo.drag(displayWidth - 10, 10, displayHeight - 50, displayHeight - 50, 100);
		catKeyboardClicker.clickOnKey("x-accel");
		assertEquals("Wrong keyboard after keyboard switch", "X_ACCELERATION_ ",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		solo.goBack();

	}

}
