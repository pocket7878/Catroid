package at.tugraz.ist.catroid.test.drone;

import java.util.ArrayList;

import org.easymock.EasyMock;

import com.jayway.android.robotium.solo.Solo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.CheckBox;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.plugin.Drone.DroneHandler;
import at.tugraz.ist.catroid.plugin.Drone.DroneLibraryWrapper;
import at.tugraz.ist.catroid.plugin.Drone.IDrone;
import at.tugraz.ist.catroid.ui.MainMenuActivity;

public class SettingsUITest extends
		ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

	IDrone idronemock;

	private final static String droneNamespaceString = "at.tugraz.ist.droned";
	private final static String droneClassString = "at.tugraz.ist.droned.Drone";

	private void createDroneMock() {
		idronemock = null;
		idronemock = EasyMock.createMock(IDrone.class);
		DroneHandler.getInstance().setIDrone(idronemock);
	}

	public SettingsUITest() {
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
		String currentProject = getActivity().getString(
				R.string.current_project_button);
		String background = getActivity().getString(R.string.background);
		String settings = getActivity().getString(R.string.settings);
		String prefMsBricks = getActivity().getString(
				R.string.pref_enable_ms_bricks);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		// disable mindstorm bricks, if enabled at start
		if (prefs.getBoolean("setting_mindstorm_bricks", false)) {
			solo.clickOnText(settings);
			solo.clickOnText(prefMsBricks);
			solo.goBack();
		}

		solo.clickOnText("Settings");
		solo.sleep(2000);

		if (prefs.getBoolean("setting_mindstorm_bricks", false)) {
		}

		Object droneInstance;
		Context droneContext;

		boolean dcfInstalled = false;
		try {

			droneContext = solo.getCurrentActivity().createPackageContext(
					droneNamespaceString,
					Context.CONTEXT_INCLUDE_CODE
							| Context.CONTEXT_IGNORE_SECURITY);

			Class<?> droneClass = Class.forName(droneClassString, true,
					droneContext.getClassLoader());

			droneInstance = droneClass.getMethod("getInstance", (Class[]) null)
					.invoke(null, (Object[]) null);

			dcfInstalled = true;

		} catch (Exception e) {
			dcfInstalled = false;
		}

		ArrayList<CheckBox> settingsCheckboxes = solo.getCurrentCheckBoxes();

		assertEquals("Drone is installed, Brick settings are not clickable", dcfInstalled, 
				settingsCheckboxes.get(1).isEnabled());
//		solo.sleep(2000);
	}
		
		
}
