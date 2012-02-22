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
package at.tugraz.ist.catroid.uitest.drone;

import static org.easymock.EasyMock.createMock;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.AssertionFailedError;
import android.widget.CheckBox;
import at.tugraz.ist.catroid.plugin.Drone.DroneHandler;
import at.tugraz.ist.catroid.plugin.Drone.IDrone;
import at.tugraz.ist.catroid.ui.MainMenuActivity;

import com.jayway.android.robotium.solo.Solo;

public class TakeOfAndLandTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

	IDrone idronemock;

	private final static String BRICKS_NOT_AVAILABLE = "Drone is installed, Drone Brick settings are not clickable";
	private final static String droneNamespaceString = "at.tugraz.ist.droned";
	private final static String droneClassString = "at.tugraz.ist.droned.Drone";

	private void createDroneMock() {
		idronemock = null;
		idronemock = createMock(IDrone.class);
		DroneHandler.getInstance().setIDrone(idronemock);
	}

	public TakeOfAndLandTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
		createDroneMock();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	public void testDroneSettings() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

		solo.clickOnText("Settings");
		solo.sleep(2000);

		/** TODO check Drone shared preferences */
		if (prefs.getBoolean("setting_mindstorm_bricks", false)) {
		}

		Object droneInstance;
		Context droneContext;

		boolean dcfInstalled = false;
		try {

			droneContext = solo.getCurrentActivity().createPackageContext(droneNamespaceString,
					Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);

			Class<?> droneClass = Class.forName(droneClassString, true, droneContext.getClassLoader());

			droneInstance = droneClass.getMethod("getInstance", (Class[]) null).invoke(null, (Object[]) null);

			if (droneInstance == null) {
				throw new AssertionFailedError();
			}
			dcfInstalled = true;

		} catch (Exception e) {
			dcfInstalled = false;
		}

		ArrayList<CheckBox> settingsCheckboxes = solo.getCurrentCheckBoxes();

		CheckBox droneCheckbox = settingsCheckboxes.get(1);

		boolean droneCheckboxEnabled = droneCheckbox.isEnabled();
		boolean droneCheckboxChecked = droneCheckbox.isChecked();
		assertEquals(BRICKS_NOT_AVAILABLE, dcfInstalled, droneCheckboxEnabled);

		if (!droneCheckboxChecked) {
			solo.clickOnCheckBox(1);
			solo.sleep(2000);
		}

		settingsCheckboxes = solo.getCurrentCheckBoxes();
		droneCheckbox = settingsCheckboxes.get(1);
		assertTrue("Drone Bricks not enabled", droneCheckbox.isChecked());

	}

}
