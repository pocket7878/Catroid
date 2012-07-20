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

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class AddCostumeAndSoundDialogTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Solo solo;

	private ProjectManager projectManager = ProjectManager.getInstance();

	private ArrayList<CostumeData> costumeDataList;
	private String costumeName = "costumeNametest";
	private final int RESOURCE_IMAGE = at.tugraz.ist.catroid.uitest.R.drawable.catroid_sunglasses;

	private ArrayList<SoundInfo> soundInfoList;
	private String soundName = "testSound";
	private final int RESOURCE_SOUND = at.tugraz.ist.catroid.uitest.R.raw.longsound;

	public AddCostumeAndSoundDialogTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());

	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	public void addCostume() {
		File imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile.getName());
		costumeData.setCostumeName(costumeName);
		costumeDataList.add(costumeData);
		projectManager.fileChecksumContainer.addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());
	}

	public void testAddCostumeDialog() {
		Activity activity = getActivity();
		solo.clickOnText(activity.getString(R.string.backgrounds));

		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		int oldCostumeCount = costumeDataList.size();
		addCostume();
		int newCostumeCount = costumeDataList.size();
		assertEquals("The costume has not been added, but it should have been", oldCostumeCount + 1, newCostumeCount);

		solo.sleep(500);
		Activity currentActivity = getActivity().getCurrentActivity();
		UiTestUtils.setPrivateField("costumeAddedFlag", currentActivity, true, false);
		boolean costumeAddedFlag = (Boolean) UiTestUtils.getPrivateField("costumeAddedFlag", currentActivity);
		assertEquals("The costumeAddedFlag was not set as it should have been.", costumeAddedFlag, true);

		solo.clickOnText(activity.getString(R.string.scripts));
		solo.sleep(200);
		solo.clickOnText(activity.getString(R.string.backgrounds));
		solo.sleep(500);
		assertTrue("The notification about the added background could not be found",
				solo.searchText(activity.getString(R.string.notification_background_added)));

	}

	public void addSound() {
		File soundFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "longsound.mp3",
				RESOURCE_SOUND, getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(soundName);
		soundInfoList.add(soundInfo);

		ProjectManager.getInstance().fileChecksumContainer.addChecksum(soundInfo.getChecksum(),
				soundInfo.getAbsolutePath());
	}

	public void testAddSoundDialog() {
		Activity activity = getActivity();
		solo.clickOnText(activity.getString(R.string.sounds));

		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		int oldSoundCount = soundInfoList.size();
		addSound();
		int newSoundCount = soundInfoList.size();
		assertEquals("The sound has not been added, but it should have been.", oldSoundCount + 1, newSoundCount);

		solo.sleep(500);
		Activity currentActivity = getActivity().getCurrentActivity();
		UiTestUtils.setPrivateField("soundAddedFlag", currentActivity, true, false);
		boolean soundAddedFlag = (Boolean) UiTestUtils.getPrivateField("soundAddedFlag", currentActivity);
		assertEquals("The costumeAddedFlag was not set as it should have been.", soundAddedFlag, true);

		solo.clickOnText(activity.getString(R.string.scripts));
		solo.sleep(200);
		solo.clickOnText(activity.getString(R.string.sounds));
		solo.sleep(500);
		assertTrue("The notification about the added sound could not be found",
				solo.searchText(activity.getString(R.string.notification_sound_added)));

	}
}
