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
package at.tugraz.ist.catroid.uitest.ui.dialog;

import android.test.ActivityInstrumentationTestCase2;
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
import at.tugraz.ist.catroid.uitest.formulaeditor.CatKeyboardClicker;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class FormulaEditorDialogTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private Project project;
	private PlaceAtBrick placeAtBrick;
	CatKeyboardClicker catKeyboardClicker = null;
	private static final int INITIAL_X = 8;
	private static final int INITIAL_Y = 7;

	private static final int X_POS_EDIT_TEXT_ID = 0;
	private static final int Y_POS_EDIT_TEXT_ID = 1;
	private static final int FORMULA_EDITOR_EDIT_TEXT_ID = 2;

	public FormulaEditorDialogTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
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

	public void testFormulaEditorChangeFormulaWithoutSaving() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clickOnKey("1");
		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		solo.sleep(50);
		assertTrue("Error message for switching from an unsaved formula not found",
				solo.searchText(solo.getString(R.string.formula_editor_save_first)));

		solo.goBack();
		solo.sleep(100);
		assertTrue("Confirmation message for discarding changes in formula not found",
				solo.searchText(solo.getString(R.string.formula_editor_confirm_discard)));
		solo.goBack();
		solo.goBack();

	}

	public void testOnTheFlyUpdateOfBrickEditText() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clickOnKey("1");

		assertEquals("Wrong text in FormulaEditor", "1", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());
		assertEquals("Wrong text in FormulaEditor", "1", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());

		catKeyboardClicker.clickOnKey("2");

		assertEquals("Wrong text in FormulaEditor", "12", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());
		assertEquals("Wrong text in FormulaEditor", "12", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());

		solo.goBack();
		solo.goBack();

		assertEquals("Wrong text in FormulaEditor", INITIAL_X + " ", solo.getEditText(X_POS_EDIT_TEXT_ID).getText()
				.toString());

	}

	public void testFormulaEditorDialogAndSimpleInterpretation() {
		//		Note solo.enterText() modifications to the text are undetectable to FormulaEditorEditText.
		//		Text via solo.enterText() *must* be longer than the original text!!! To be safe use CatKeyboardKlicker to clear!
		//		Use CatKeyboardClicker for full functionality, is a lot slower and inconvenient! Will do just fine here without

		String newXFormula = "10 + 12 - 2 * 3 - 4 ";
		int newXValue = 10 + 12 - 2 * 3 - 4;
		String newYFormula = "rand( cos( 90 ) , 10 * sin( 90 ) ) ";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clearEditTextWithOnlyNumbersQuickly(FORMULA_EDITOR_EDIT_TEXT_ID);
		solo.enterText(FORMULA_EDITOR_EDIT_TEXT_ID, "999 " + newXFormula);
		catKeyboardClicker.clickOnKey("9");
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_save));
		solo.sleep(50);
		assertTrue("Save failed toast not found", solo.searchText(solo.getString(R.string.formula_editor_parse_fail)));

		solo.clearEditText(FORMULA_EDITOR_EDIT_TEXT_ID);
		solo.enterText(FORMULA_EDITOR_EDIT_TEXT_ID, newXFormula);
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_save));
		solo.sleep(50);
		assertTrue("Changes saved toast not found",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clickOnKey("1");
		solo.clearEditText(FORMULA_EDITOR_EDIT_TEXT_ID);
		solo.enterText(FORMULA_EDITOR_EDIT_TEXT_ID, newYFormula);

		solo.clickOnButton(solo.getString(R.string.formula_editor_button_save));
		solo.sleep(50);
		assertTrue("Changes saved toast not found",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.sleep(50);
		assertEquals("Wrong text in FormulaEditor", newXFormula, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		solo.sleep(50);
		assertEquals("Wrong text in FormulaEditor", newYFormula, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.goBack();
		solo.sleep(300);

		//Interpretation test
		Formula formula = (Formula) UiTestUtils.getPrivateField("xPositionFormula", placeAtBrick);
		assertEquals("Wrong text in field", newXValue, formula.interpretInteger());

		formula = (Formula) UiTestUtils.getPrivateField("yPositionFormula", placeAtBrick);

		int newYValue = formula.interpretInteger();
		Log.i("info", "" + newYValue);
		assertTrue("Wrong text in field", newYValue >= 0 && newYValue <= 10);

	}

	public void testUndo() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clearEditTextWithOnlyNumbersQuickly(FORMULA_EDITOR_EDIT_TEXT_ID);

		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("-");
		catKeyboardClicker.clickOnKey("2");
		catKeyboardClicker.clickOnKey("*");
		catKeyboardClicker.clickOnKey("keyboardswitch");
		catKeyboardClicker.clickOnKey("cos");
		catKeyboardClicker.clickOnKey("sin");
		catKeyboardClicker.clickOnKey("tan");

		assertEquals("Wrong text in field", "1 - 2 * cos( sin( tan( 0 ) ) )",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		solo.clickOnButton(solo.getString(R.string.formula_editor_button_undo));
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 * cos( sin( 0 ) )",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		solo.clickOnButton(solo.getString(R.string.formula_editor_button_undo));
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 * cos( 0 )", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnButton(solo.getString(R.string.formula_editor_button_undo));
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 *", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		solo.clickOnButton(solo.getString(R.string.formula_editor_button_undo));
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		solo.clickOnButton(solo.getString(R.string.formula_editor_button_save));
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_return));

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

		assertEquals("Wrong text in field", "9 - 8 * 7 + 9", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		for (int i = 0; i < 7; i++) {
			solo.clickOnButton(solo.getString(R.string.formula_editor_button_undo));
		}

		solo.sleep(50);
		assertEquals("Undo did something wrong", INITIAL_X + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnButton(solo.getString(R.string.formula_editor_button_undo));

		assertEquals("Undo did something wrong", INITIAL_X + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		for (int i = 0; i < 7; i++) {
			solo.clickOnButton(solo.getString(R.string.formula_editor_button_redo));
		}

		solo.sleep(50);
		assertEquals("Undo did something wrong", "9 - 8 * 7 + 9", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.goBack();
		solo.goBack();

	}

	public void testUndoLimit() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		//catKeyboardClicker.clearEditTextWithOnlyNumbersQuickly(FORMULA_EDITOR_EDIT_TEXT_ID);
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

		assertEquals("Wrong text in field", "1" + searchString, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		for (int i = 0; i < maxHistoryElements + 2; i++) {
			solo.clickOnButton(solo.getString(R.string.formula_editor_button_undo));
		}

		assertEquals("Wrong text in field", "1", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		for (int i = 0; i < maxHistoryElements + 2; i++) {
			solo.clickOnButton(solo.getString(R.string.formula_editor_button_redo));
		}

		assertEquals("Wrong text in field", "1" + searchString, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		solo.goBack();
		solo.goBack();

	}
}
