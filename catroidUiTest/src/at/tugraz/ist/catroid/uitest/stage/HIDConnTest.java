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
package at.tugraz.ist.catroid.uitest.stage;

import java.io.File;
import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.bluetooth.DeviceListActivity;
import at.tugraz.ist.catroid.bluetooth.RFCommCommunicator;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.HIDKeyBoardButtonBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class HIDConnTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private StorageHandler storageHandler;
	private final String projectName = UiTestUtils.PROJECTNAME1;

	private File image1;
	private String imageName1 = "image1";
	private static final int IMAGE_FILE_ID = at.tugraz.ist.catroid.uitest.R.raw.icon;

	public static final String TEST_SERVER_NAME = "BCM2045";
	public static final String SERVER_MAC_ADDRESS = "00:16:41:86:AA:4A";
	ArrayList<int[]> commands = new ArrayList<int[]>();

	public HIDConnTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
		storageHandler = StorageHandler.getInstance();
	}

	@Override
	public void setUp() throws Exception {
		UiTestUtils.clearAllUtilTestProjects();

		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		UiTestUtils.clearAllUtilTestProjects();

		getActivity().finish();
		super.tearDown();
	}

	public void testRfcCommPersistentConnection() {
		createTestproject(projectName);

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
			solo.sleep(5000);
		}

		solo.clickOnButton(0);
		solo.sleep(1000);
		solo.clickOnText("sprite1");
		solo.sleep(1000);

		ArrayList<String> autoConnectIDs = new ArrayList<String>();
		autoConnectIDs.add(SERVER_MAC_ADDRESS);
		DeviceListActivity dla = new DeviceListActivity();
		UiTestUtils.setPrivateField("autoConnectIDs", dla, autoConnectIDs, false);

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);
		solo.sleep(1000);
		solo.clickOnText(TEST_SERVER_NAME + "-" + SERVER_MAC_ADDRESS);
		solo.sleep(4000);
		solo.clickOnScreen(260, 420);
		solo.sleep(4000);
		byte[] result = RFCommCommunicator.getNextMessage();
		assertNotNull(result);
		byte[] r = { 4 };
		assertEquals(result[4], r[0]);
	}

	public void createTestproject(String projectName) {

		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new StartScript(firstSprite);
		Script whenScript = new WhenScript(firstSprite);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);

		HIDKeyBoardButtonBrick keyBoardButtonBrick = new HIDKeyBoardButtonBrick(firstSprite);

		whenScript.addBrick(keyBoardButtonBrick);

		startScript.addBrick(setCostumeBrick);
		firstSprite.addScript(startScript);
		firstSprite.addScript(whenScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		image1 = UiTestUtils.saveFileToProject(projectName, imageName1, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), UiTestUtils.FileTypes.IMAGE);
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(image1.getAbsolutePath(), o);

		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(image1.getName());
		costumeData.setCostumeName("image1");
		setCostumeBrick.setCostume(costumeData);
		firstSprite.getCostumeDataList().add(costumeData);

		storageHandler.saveProject(project);
	}
}
