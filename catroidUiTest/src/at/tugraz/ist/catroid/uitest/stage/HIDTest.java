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

import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.bluetooth.DeviceListActivity;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.HIDComboBrick;
import at.tugraz.ist.catroid.content.bricks.HIDComboEndBrick;
import at.tugraz.ist.catroid.content.bricks.HIDKeyBoardButtonBrick;
import at.tugraz.ist.catroid.content.bricks.SetBrightnessBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class HIDTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private ArrayList<Brick> startbrickListToCheck;
	private ArrayList<Brick> firstbrickListToCheck;
	Script startScript;
	Script firstScript;
	private Sprite firstSprite;

	public HIDTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {

		createProject("HelloHid");
		solo = new Solo(getInstrumentation(), getActivity());

		super.setUp();

		// enable bluetooth before
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);

		bluetoothAdapter.enable();
		solo.sleep(5000);
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

	public void testOneBluetoothDeviceAvailableAtLeast() {

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);
		solo.sleep(1000);

		ArrayList<String> autoConnectIDs = new ArrayList<String>();
		autoConnectIDs.add("IM_NOT_A_MAC_ADDRESS");
		DeviceListActivity dla = new DeviceListActivity();
		UiTestUtils.setPrivateField("autoConnectIDs", dla, autoConnectIDs, false);

		solo.sleep(1000);

		int number_of_devices = solo.getCurrentListViews().get(0).getCount();
		assertTrue("At least one bluetooth device should be available", number_of_devices >= 1);
	}

	private void createProject(String projectName) {

		Project project = new Project(null, projectName);
		firstSprite = new Sprite("cat");

		startScript = new StartScript(firstSprite);
		firstScript = new WhenScript(firstSprite);

		startbrickListToCheck = new ArrayList<Brick>();
		startbrickListToCheck.add(new HIDKeyBoardButtonBrick(firstSprite));
		HIDComboBrick startCombo = new HIDComboBrick(firstSprite);
		HIDComboEndBrick startEnd = new HIDComboEndBrick(firstSprite, startCombo);
		startCombo.setLoopEndBrick(startEnd);
		startbrickListToCheck.add(startCombo);
		startbrickListToCheck.add(startEnd);

		firstbrickListToCheck = new ArrayList<Brick>();
		SetBrightnessBrick brightBrick = new SetBrightnessBrick(firstSprite, 100);
		firstbrickListToCheck.add(brightBrick);

		// adding Bricks: ----------------
		for (Brick brick : startbrickListToCheck) {
			startScript.addBrick(brick);
		}

		for (Brick brick : firstbrickListToCheck) {
			firstScript.addBrick(brick);
		}

		// -------------------------------

		firstSprite.addScript(startScript);
		firstSprite.addScript(firstScript);

		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
	}
}