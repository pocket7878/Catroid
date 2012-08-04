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

import android.test.suitebuilder.annotation.Smoke;
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

public class FormulaEditorEditTextTest extends android.test.ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Project project;
	private Solo solo;
	private Sprite firstSprite;
	private Brick changeBrick;

	private CatKeyboardClicker catKeyboardClicker;

	public FormulaEditorEditTextTest() {
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
		solo.sleep(1000);
		super.tearDown();

	}

	@Smoke
	public void testDiscardChanges() {

		solo.clickOnEditText(0);
		catKeyboardClicker.clearEditTextWithDeletes(1);
		catKeyboardClicker.clickOnKey("9");
		catKeyboardClicker.clickOnKey("9");
		catKeyboardClicker.clickOnKey(".");
		catKeyboardClicker.clickOnKey("9");
		catKeyboardClicker.clickOnKey("9");
		solo.sleep(50);
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_discard));
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_discard));
		solo.sleep(50);
		assertTrue("Toast not found", solo.searchText(solo.getString(R.string.formula_editor_changes_discarded)));
		assertEquals("Wrong text in FormulaEditor", "0.0 ", solo.getEditText(1).getText().toString());
		solo.clickOnButton(2);
	}

	@Smoke
	public void testErrorInFirstAndLastCharacters() {

		solo.clickOnEditText(0);
		catKeyboardClicker.clearEditTextWithDeletes(1);
		catKeyboardClicker.clickOnKey("+");
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_save));
		solo.sleep(50);
		assertTrue("Toast not found", solo.searchText(solo.getString(R.string.formula_editor_parse_fail)));
		catKeyboardClicker.clearEditTextWithDeletes(1);
		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("+");
		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("+");
		solo.clickOnButton(solo.getString(R.string.formula_editor_button_save));
		solo.sleep(50);
		assertTrue("Toast not found", solo.searchText(solo.getString(R.string.formula_editor_parse_fail)));

		solo.clickOnButton(2);
		solo.clickOnButton(2);
	}

	@Smoke
	public void testDeletingAndSelectionAndParseErrors() {

		solo.clickOnEditText(0);
		//		solo.clickOnEditText(1);
		//
		//		solo.clearEditText(1);

		catKeyboardClicker.clearEditTextWithDeletes(1);
		solo.enterText(1, "8 + XACC_+5YACC_ + 76");
		catKeyboardClicker.clickOnKey("9");
		solo.clickOnButton(getActivity().getString(R.string.formula_editor_button_save));
		solo.sleep(500);
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		solo.clickOnButton(getActivity().getString(R.string.formula_editor_button_save));

		assertEquals("Text not deleted correctly", "98 + XACC_ + 76", solo.getEditText(1).getText().toString());

		catKeyboardClicker.clearEditTextWithDeletes(1);
		solo.enterText(1, "8 +cos( 0 + 1 - 2)++ 76");
		catKeyboardClicker.clickOnKey("9");
		solo.clickOnButton(getActivity().getString(R.string.formula_editor_button_save));
		solo.sleep(500);
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		assertEquals("Text not deleted correctly", "98 + 76", solo.getEditText(1).getText().toString());
		//
		//				solo.clearEditText(1);
		//
		catKeyboardClicker.clearEditTextWithDeletes(1);
		solo.enterText(1, "8 +cos(+ 0 + 1 - 2) 76");
		catKeyboardClicker.clickOnKey("9");
		solo.clickOnButton(getActivity().getString(R.string.formula_editor_button_save));
		solo.sleep(500);
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		assertEquals("Text not deleted correctly", "98 + 76", solo.getEditText(1).getText().toString());

		//				solo.clearEditText(1);

		catKeyboardClicker.clearEditTextWithDeletes(1);
		solo.enterText(1, "8 +rand( 0 ,+ 0 ) 76");
		catKeyboardClicker.clickOnKey("9");
		solo.clickOnButton(getActivity().getString(R.string.formula_editor_button_save));
		solo.sleep(500);
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		assertEquals("Text not deleted correctly", "98 + 76", solo.getEditText(1).getText().toString());

		//		solo.clearEditText(1);

		catKeyboardClicker.clearEditTextWithDeletes(1);
		solo.enterText(1, "8 + rand( 0 +sin(+ 0 ) , 1 ) + 76");
		catKeyboardClicker.clickOnKey("9");
		solo.clickOnButton(getActivity().getString(R.string.formula_editor_button_save));
		solo.sleep(500);
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		assertEquals("Text not deleted correctly", "98 + rand( 0 , 1 ) + 76", solo.getEditText(1).getText().toString());

		solo.clickOnButton(2);
		solo.clickOnButton(2);

	}

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
