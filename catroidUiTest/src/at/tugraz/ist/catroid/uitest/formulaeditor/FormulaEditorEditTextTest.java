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

import java.util.LinkedList;
import java.util.List;

import android.test.suitebuilder.annotation.Smoke;
import android.text.style.BackgroundColorSpan;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ChangeSizeByNBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.formulaeditor.FormulaEditorEditText;
import at.tugraz.ist.catroid.formulaeditor.FormulaElement;
import at.tugraz.ist.catroid.formulaeditor.InternFormulaParser;
import at.tugraz.ist.catroid.formulaeditor.InternToken;
import at.tugraz.ist.catroid.formulaeditor.InternTokenType;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class FormulaEditorEditTextTest extends android.test.ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Project project;
	private Solo solo;
	private Sprite firstSprite;
	private Brick changeBrick;
	Script startScript1;

	private CatKeyboardClicker catKeyboardClicker;

	float oneCharacterWidth = 16; // this isnt exact!
	float threeCharactersWidth = 48;
	float brickOffset = 99;
	float actionbarOffset = 120;
	int lineHeight = 41;
	int visibleLines = 7;
	int totalLinesForTheInput = 12;
	float firstLineYCoordinate = brickOffset + actionbarOffset;

	public FormulaEditorEditTextTest() {
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
	public void testDoubleTapSelection() {
		//		float xCoordinate = 60;
		//		float brickOffset = 99;
		//		float greenBarOffset = 5;
		//		float yCoordinate = brickOffset + greenBarOffset + 5;
		BackgroundColorSpan COLOR_HIGHLIGHT = (BackgroundColorSpan) UiTestUtils.getPrivateField("COLOR_HIGHLIGHT",
				new FormulaEditorEditText(getActivity()));
		solo.clickOnEditText(0);

		catKeyboardClicker.clickOnKey("del");

		for (int i = 0; i < 6; i++) {
			catKeyboardClicker.clickOnKey("1");
		}
		assertTrue("Text not found", solo.searchText("11111"));

		assertTrue("Selection cursor found in text, but should not be",
				solo.getEditText(1).getText().getSpanStart(COLOR_HIGHLIGHT) == -1);
		//There is no doubleclick in robotium q.q, this is a workaround!
		solo.clickOnScreen(threeCharactersWidth, firstLineYCoordinate);
		solo.drag(threeCharactersWidth, threeCharactersWidth + 1, firstLineYCoordinate, firstLineYCoordinate, 50);
		assertEquals("Selection cursor not found in text, but should be", 0, solo.getEditText(1).getText()
				.getSpanStart(COLOR_HIGHLIGHT));
		assertEquals("Selection cursor not found in text, but should be", 6,
				solo.getEditText(1).getText().getSpanEnd(COLOR_HIGHLIGHT));
		catKeyboardClicker.clickOnKey("del");

		assertFalse("Text found but shouldnt", solo.searchText("1"));
		assertTrue("Error cursor found in text, but should not be",
				solo.getEditText(1).getText().getSpanStart(COLOR_HIGHLIGHT) == -1);

		catKeyboardClicker.clickOnKey("rand");
		assertTrue("Text not found", solo.searchText(solo.getString(R.string.formula_editor_function_rand) + "("));
		//There is no doubleclick in robotium q.q, this is a workaround!
		solo.clickOnScreen(threeCharactersWidth, firstLineYCoordinate);
		solo.drag(threeCharactersWidth, threeCharactersWidth + 1, firstLineYCoordinate, firstLineYCoordinate, 50);

		catKeyboardClicker.clickOnKey("del");

		assertFalse("Text found but shouldnt",
				solo.searchText(solo.getString(R.string.formula_editor_function_rand) + "("));

		catKeyboardClicker.switchToSensorKeyboard();
		//catKeyboardClicker.clickOnKey("keyboardswitch");
		catKeyboardClicker.clickOnKey("y-accel");
		assertTrue("Text not found", solo.searchText(solo.getString(R.string.formula_editor_sensor_y_acceleration)));
		//There is no doubleclick in robotium q.q, this is a workaround!
		solo.clickOnScreen(threeCharactersWidth, firstLineYCoordinate);
		solo.drag(threeCharactersWidth, threeCharactersWidth + 1, firstLineYCoordinate, firstLineYCoordinate, 50);

		catKeyboardClicker.clickOnKey("del");

		assertFalse("Text found but shouldnt",
				solo.searchText(solo.getString(R.string.formula_editor_sensor_y_acceleration)));

		catKeyboardClicker.clickOnKey("y-accel");
		catKeyboardClicker.clickOnKey("x-accel");
		assertTrue(
				"Text not found",
				solo.searchText(solo.getString(R.string.formula_editor_sensor_y_acceleration) + " "
						+ solo.getString(R.string.formula_editor_sensor_x_acceleration)));
		//There is no doubleclick in robotium q.q, this is a workaround!
		solo.clickOnScreen(threeCharactersWidth, firstLineYCoordinate);
		solo.drag(threeCharactersWidth, threeCharactersWidth + 1, firstLineYCoordinate, firstLineYCoordinate, 50);

		catKeyboardClicker.clickOnKey("del");

		assertTrue("Text not found", solo.searchText(solo.getString(R.string.formula_editor_sensor_x_acceleration)));

		solo.goBack();
		solo.goBack();
	}

	@Smoke
	public void testFunctionFirstParameterSelectionAndModification() {

		solo.clickOnEditText(0);

		catKeyboardClicker.switchToFunctionKeyboard();
		catKeyboardClicker.clickOnKey("sin");
		catKeyboardClicker.switchToNumberKeyboard();

		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("2");
		catKeyboardClicker.clickOnKey(".");
		catKeyboardClicker.clickOnKey("3");
		catKeyboardClicker.clickOnKey("4");

		assertEquals("Function parameter modification failed", solo.getString(R.string.formula_editor_function_sin)
				+ "( 12.34 ) ", solo.getEditText(1).getText().toString());

		solo.clickOnScreen(2.5f * oneCharacterWidth, firstLineYCoordinate);
		catKeyboardClicker.clickOnKey("del");

		assertEquals("Text deletion was wrong!", " ", solo.getEditText(1).getText().toString());

		catKeyboardClicker.clickOnKey("rand");
		catKeyboardClicker.switchToNumberKeyboard();

		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("2");
		catKeyboardClicker.clickOnKey(".");
		catKeyboardClicker.clickOnKey("3");
		catKeyboardClicker.clickOnKey("4");

		assertEquals("Function parameter modification failed", solo.getString(R.string.formula_editor_function_rand)
				+ "( 12.34 , 1 ) ", solo.getEditText(1).getText().toString());

		solo.clickOnScreen(2.5f * oneCharacterWidth, firstLineYCoordinate);
		catKeyboardClicker.clickOnKey("del");

		assertEquals("Text deletion was wrong!", " ", solo.getEditText(1).getText().toString());

		solo.goBack();
		solo.goBack();
	}

	@Smoke
	public void testFunctionReplaceButKeepParameters() {

		solo.clickOnEditText(0);

		catKeyboardClicker.switchToFunctionKeyboard();
		catKeyboardClicker.clickOnKey("sin");
		catKeyboardClicker.switchToNumberKeyboard();

		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("2");
		catKeyboardClicker.clickOnKey(".");
		catKeyboardClicker.clickOnKey("3");
		catKeyboardClicker.clickOnKey("4");

		assertEquals("Function parameter modification failed", solo.getString(R.string.formula_editor_function_sin)
				+ "( 12.34 ) ", solo.getEditText(1).getText().toString());

		solo.clickOnScreen(2.5f * oneCharacterWidth, firstLineYCoordinate);
		catKeyboardClicker.clickOnKey("rand");

		assertEquals("Keep function parameters failed", solo.getString(R.string.formula_editor_function_rand)
				+ "( 12.34 , 1 ) ", solo.getEditText(1).getText().toString());

		solo.goBack();
		solo.goBack();
	}

	@Smoke
	public void testBracketValueSelectionAndModification() {

		solo.clickOnEditText(0);

		catKeyboardClicker.switchToFunctionKeyboard();
		catKeyboardClicker.clickOnKey("bracket");
		catKeyboardClicker.switchToNumberKeyboard();

		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey(".");
		catKeyboardClicker.clickOnKey("3");

		assertEquals("Bracket value modification failed", "( 1.3 ) ", solo.getEditText(1).getText().toString());

		solo.clickOnScreen(12f * oneCharacterWidth, firstLineYCoordinate);
		catKeyboardClicker.clickOnKey("del");

		assertEquals("Text deletion was wrong!", " ", solo.getEditText(1).getText().toString());

		solo.goBack();
		solo.goBack();
	}

	@Smoke
	public void testNumberDelimiterInsertion() {

		solo.clickOnEditText(0);

		catKeyboardClicker.clickOnKey(".");
		catKeyboardClicker.clickOnKey("1");

		assertEquals("Number insertion was wrong!", "0.1 ", solo.getEditText(1).getText().toString());

		catKeyboardClicker.clickOnKey(".");

		assertEquals("Delimiter insertion was wrong!", "0.1 ", solo.getEditText(1).getText().toString());

		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		assertEquals("Number deletion failed!", " ", solo.getEditText(1).getText().toString());

		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("2");

		solo.clickOnScreen(oneCharacterWidth / 2f, firstLineYCoordinate);
		catKeyboardClicker.clickOnKey(".");

		assertEquals("Delimiter insertion was wrong!", "0.12 ", solo.getEditText(1).getText().toString());

		solo.goBack();
		solo.goBack();
	}

	@Smoke
	public void testGoBackToDiscardChanges() {

		solo.clickOnEditText(0);
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("9");
		catKeyboardClicker.clickOnKey("9");
		catKeyboardClicker.clickOnKey(".");
		catKeyboardClicker.clickOnKey("9");
		catKeyboardClicker.clickOnKey("+");
		solo.sleep(50);
		solo.goBack();
		solo.goBack();

		assertTrue("Toast not found", solo.searchText(solo.getString(R.string.formula_editor_changes_discarded)));
		assertEquals("Wrong text in FormulaEditor", "0.0 ", solo.getEditText(0).getText().toString());

	}

	@Smoke
	public void testErrorInFirstAndLastCharactersAndEmptyFormula() {

		solo.clickOnEditText(0);
		//catKeyboardClicker.clearEditTextWithDeletes(1);
		BackgroundColorSpan COLOR_ERROR = (BackgroundColorSpan) UiTestUtils.getPrivateField("COLOR_ERROR",
				new FormulaEditorEditText(getActivity()));
		catKeyboardClicker.clickOnKey("del");
		assertTrue("Error cursor found in text, but should not be",
				solo.getEditText(1).getText().getSpanStart(COLOR_ERROR) == -1);
		solo.goBack();
		assertTrue("Toast not found", solo.searchText(solo.getString(R.string.formula_editor_parse_fail)));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("+");
		solo.goBack();
		solo.sleep(50);
		assertTrue("Toast not found", solo.searchText(solo.getString(R.string.formula_editor_parse_fail)));
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(0);
		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("+");
		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("+");
		solo.goBack();
		solo.sleep(50);
		assertTrue("Toast not found", solo.searchText(solo.getString(R.string.formula_editor_parse_fail)));

		solo.goBack();
		solo.goBack();
	}

	//TODO Adapt tests below this line!
	//-------------------------------------------------------------------------------------------------

	//	@Smoke TODO: Not necessary anymore 
	//	public void testTextSelectionLogicForSensors() {
	//
	//		solo.clickOnEditText(0);
	//		catKeyboardClicker.switchToSensorKeyboard();
	//		catKeyboardClicker.clickOnKey("x-accel");
	//		solo.clickOnScreen(10, firstLineYCoordinate);
	//		catKeyboardClicker.clickOnKey("y-accel");
	//		catKeyboardClicker.clickOnKey("del");
	//		solo.clickOnScreen(25 * oneCharacterWidth, firstLineYCoordinate);
	//		catKeyboardClicker.clickOnKey("del");
	//		assertEquals("Text deletion was wrong!", "Y_ACCELERATION_ ", solo.getEditText(1).getText().toString());
	//
	//		solo.clickOnEditText(0);
	//		catKeyboardClicker.clickOnKey("del");
	//		catKeyboardClicker.clickOnKey("x-accel");
	//		solo.clickOnScreen(10, firstLineYCoordinate);
	//		catKeyboardClicker.clickOnKey("y-accel");
	//		catKeyboardClicker.clickOnKey("del");
	//		solo.clickOnScreen(10 * oneCharacterWidth, firstLineYCoordinate);
	//		catKeyboardClicker.clickOnKey("del");
	//		assertEquals("Text deletion was wrong!", "X_ACCELERATION_ ", solo.getEditText(1).getText().toString());
	//
	//		solo.goBack();
	//		solo.goBack();
	//	}

	@Smoke
	public void testTextCursorAndScrolling() {

		//		float xCoordinate = 60;
		//		float waitBrickOffset = 99;
		//		float greenBarOffset = 5;
		//		int lineHeight = 41;
		//		int visibleLines = 7;
		//		int totalLinesForTheInput = 12;
		//		float yCoordinate = waitBrickOffset + greenBarOffset + 5;

		//		Formula longFormula = createVeryLongFormula();
		//		WaitBrick waitBrick = new WaitBrick(firstSprite, longFormula);
		//		startScript1.addBrick(waitBrick);
		//		solo.clickOnEditText(0);
		//		solo.goBack();
		//		solo.goBack();
		//		solo.clickOnText(solo.getString(R.string.current_project));
		//		solo.clickOnText("nom nom nom");
		solo.clickOnEditText(1);

		for (int i = 0; i <= totalLinesForTheInput - visibleLines; i++) {
			solo.clickOnScreen(threeCharactersWidth, firstLineYCoordinate); //scroll edittext to top, solo 2 stupid q.q
		}
		assertTrue("Text could not be found!", solo.searchText("999999999999999999 ")); //note always ALL the text can be found by solo, not just the part currently visible due to scroll position 
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del"); //TODO: fix line 
		assertFalse("Wrong line deleted!", solo.searchText("999999999999999999 "));
		assertTrue("Wrong number of characters deleted!", solo.searchText("9999999999999999 "));

		assertTrue("Text could not be found!", solo.searchText("666666666666666666 "));
		solo.clickOnScreen(threeCharactersWidth, firstLineYCoordinate + 3 * lineHeight);
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		assertFalse("Wrong line deleted!", solo.searchText("666666666666666666 "));
		assertTrue("Wrong number of characters deleted!", solo.searchText("6666666666666666 "));

		solo.sleep(500);
		for (int i = 1; i < totalLinesForTheInput - visibleLines; i++) {
			solo.clickOnScreen(threeCharactersWidth, firstLineYCoordinate + 7.5f * lineHeight); //scroll edittext to bottom, solo 2 stupid q.q
		}
		assertTrue("Text could not be found!", solo.searchText("222222222222222222 "));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		assertFalse("Wrong line deleted!", solo.searchText("222222222222222222 "));
		assertTrue("Wrong number of characters deleted!", solo.searchText("2222222222222222 "));

		solo.goBack();
		solo.goBack();
	}

	private Formula createVeryLongFormula() {

		List<InternToken> internTokenList = new LinkedList<InternToken>();

		internTokenList.add(new InternToken(InternTokenType.NUMBER, "999999999999999999"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "888888888888888888"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "777777777777777777"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "666666666666666666"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "555555555555555555"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "444444444444444444"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "333333333333333333"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "222222222222222222"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "111111111111111111"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "000000000000000000"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "999999999999999999"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "888888888888888888"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "777777777777777777"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "666666666666666666"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "555555555555555555"));
		internTokenList.add(new InternToken(InternTokenType.OPERATOR, "+"));
		internTokenList.add(new InternToken(InternTokenType.NUMBER, "444444444444444444"));

		InternFormulaParser internParser = new InternFormulaParser(internTokenList);
		FormulaElement root = internParser.parseFormula();
		Formula formula = new Formula(root);

		return formula;
	}

	@Smoke
	public void testParseErrorsAndDeletion() {
		//		Note solo.enterText() modifications to the text are undetectable to FormulaEditorEditText.
		//		Text via solo.enterText() *must* be longer than the original text!!! To be safe use CatKeyboardKlicker to clear!
		//		Use CatKeyboardClicker for full functionality, is a lot slower and inconvenient! Will do just fine here without

		solo.clickOnEditText(0);
		catKeyboardClicker.clickOnKey("del");

		solo.enterText(1, "8 + X_ACCELERATION_+5Y_ACCELERATION_ + 76 ");
		catKeyboardClicker.clickOnKey("9");
		//solo.goBack();
		solo.goBack();
		solo.sleep(500);
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		//solo.goBack();

		assertEquals("Text not deleted correctly", "98 + X_ACCELERATION_ + 76 ", solo.getEditText(1).getText()
				.toString());

		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(0);
		solo.enterText(1, "8 +cos( 0 + 1 - 2)++ 76 ");
		catKeyboardClicker.clickOnKey("9");
		solo.goBack();
		solo.sleep(500);
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		assertEquals("Text not deleted correctly", "98 + 76 ", solo.getEditText(1).getText().toString());
		//
		//				solo.clearEditText(1);
		//
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(0);
		solo.enterText(1, "8 +cos(+ 0 + 1 - 2) 76 ");
		catKeyboardClicker.clickOnKey("9");
		solo.goBack();
		solo.sleep(500);
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		assertEquals("Text not deleted correctly", "98 + 76 ", solo.getEditText(1).getText().toString());

		//				solo.clearEditText(1);

		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(0);
		solo.enterText(1, "8 +rand( 0 ,+ 0 ) 76 ");
		catKeyboardClicker.clickOnKey("9");
		solo.goBack();
		solo.sleep(500);
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		assertEquals("Text not deleted correctly", "98 + 76 ", solo.getEditText(1).getText().toString());

		//		solo.clearEditText(1);

		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(0);
		solo.enterText(1, "8 + rand( 0+sin(+ 0 ) , 1 ) + 76 ");
		catKeyboardClicker.clickOnKey("9");
		solo.goBack();
		solo.sleep(500);
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("del");

		assertEquals("Text not deleted correctly", "98 + rand( 0 , 1 ) + 76 ", solo.getEditText(1).getText().toString());

		solo.goBack();
	}

	private void createProject(String projectName) throws InterruptedException {

		this.project = new Project(null, projectName);
		firstSprite = new Sprite("nom nom nom");
		startScript1 = new StartScript(firstSprite);
		changeBrick = new ChangeSizeByNBrick(firstSprite, 0);
		Formula longFormula = createVeryLongFormula();
		WaitBrick waitBrick = new WaitBrick(firstSprite, longFormula);
		firstSprite.addScript(startScript1);
		startScript1.addBrick(changeBrick);
		startScript1.addBrick(waitBrick);
		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

	}
}
