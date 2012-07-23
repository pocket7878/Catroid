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
package at.tugraz.ist.catroid.uitest.ui;

import java.io.File;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ClickOnNewInSpinnerCostumeTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private String costumeName = "testCostume1";
	private String costumeName2 = "testCostume2";
	private File costumeFile;
	private File costumeFile2;
	private ArrayList<CostumeData> costumeDataList;
	private final int RESOURCE_COSTUME = at.tugraz.ist.catroid.uitest.R.raw.icon;
	private final int RESOURCE_COSTUME2 = at.tugraz.ist.catroid.uitest.R.raw.icon2;

	public ClickOnNewInSpinnerCostumeTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();

		ProjectManager projectManager = ProjectManager.getInstance();
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript(firstSprite);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		testScript.addBrick(setCostumeBrick);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();

		costumeFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "image.png",
				RESOURCE_COSTUME, getInstrumentation().getContext(), UiTestUtils.FileTypes.IMAGE);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(costumeFile.getName());
		costumeData.setCostumeName(costumeName);

		costumeFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "image2.png",
				RESOURCE_COSTUME2, getInstrumentation().getContext(), UiTestUtils.FileTypes.IMAGE);
		CostumeData costumeData2 = new CostumeData();
		costumeData2.setCostumeFilename(costumeFile2.getName());
		costumeData2.setCostumeName(costumeName2);

		costumeDataList.add(costumeData);
		costumeDataList.add(costumeData2);
		ProjectManager.getInstance().fileChecksumContainer.addChecksum(costumeData.getChecksum(),
				costumeData.getAbsolutePath());
		ProjectManager.getInstance().fileChecksumContainer.addChecksum(costumeData2.getChecksum(),
				costumeData2.getAbsolutePath());

		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testNothingSelected() {
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.brick_set_costume_none));
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.brick_set_costume_none));

		solo.sleep(100);
		assertTrue("No Costume was selected, WHY YOU NOT NOTHING?!",
				solo.searchText(getActivity().getString(R.string.brick_set_costume_none)));

	}

	public void testSelectNew() {
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.brick_set_costume_none));
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.brick_set_costume_new));
		solo.sleep(100);
		assertTrue("Switch to Costume activity was not successfull",
				solo.searchText(solo.getString(R.string.select_image)));

		solo.goBack();

		solo.sleep(100);
		assertTrue("No Costume was selected, WHY YOU NOT NOTHING?!",
				solo.searchText(getActivity().getString(R.string.brick_set_costume_none)));

	}

	public void testSelectItemAndCheckIfLostAfterTabChange() {
		solo.sleep(100);
		solo.clickOnText(getActivity().getString(R.string.brick_set_costume_none));
		solo.sleep(100);
		solo.clickOnText(costumeName);

		solo.clickOnText(getActivity().getString(R.string.backgrounds));

		assertTrue("We are not in the CostumesActivity", solo.searchText(costumeName));

		solo.clickOnText(getActivity().getString(R.string.scripts));

		assertTrue("We are not in the CostumesActivity", solo.searchText(costumeName));

	}
}
