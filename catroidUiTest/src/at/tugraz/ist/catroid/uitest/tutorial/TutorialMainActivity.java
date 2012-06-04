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
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 * 
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.uitest.tutorial;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Configuration;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.tutorial.Tutorial;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class TutorialMainActivity extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private static final boolean DEBUG = true;
	private Runnable buttonPressRunnable;
	private ArrayList<View> actual_views;
	private Activity mainActivity;
	private float screenWidth;
	private float screenHeight;

	public TutorialMainActivity() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		String languageToLoad_before = "en";

		Configuration config_before = new Configuration();

		mainActivity = solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources()
				.updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());

		screenWidth = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
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
		super.tearDown();
	}

	public void testTutorialActive() {
		pressTutorialButton();
		solo.sleep(3000);
		boolean isActive = Tutorial.getInstance(null).isActive();
		solo.sleep(3000);
		Tutorial.getInstance(null).stopButtonTutorial();
		solo.sleep(3000);
		assertTrue("Tutorail: Tutorial not active!", isActive);
	}

	public void testCurrentProjectButton() {
		Button cpbutton = solo.getButton(getActivity().getString(R.string.current_project_button));
		int x = cpbutton.getLeft() + 10;
		int y = cpbutton.getTop() + 10;
		pressTutorialButton();
		solo.sleep(20000);
		Log.i("faxxe", "clicking...");
		solo.clickOnScreen(x, y);
		solo.sleep(9000);

		Tutorial.getInstance(null).stopButtonTutorial();
	}

	public void testMenuBar() {
		Activity activity = solo.getCurrentActivity();
		int height = activity.getWindow().getWindowManager().getDefaultDisplay().getHeight();
		pressTutorialButton();
		solo.sleep(3000);
		solo.clickOnScreen(10, height - 10);
		solo.sleep(3000);
		assertTrue("have you tried turning it off and on again?", Tutorial.getInstance(null).isActive());
		solo.clickOnScreen(140, height - 10);
		solo.sleep(3000);
		assertFalse("have you tried turning it off and on again?", Tutorial.getInstance(null).isActive());
		solo.sleep(3000);
	}

	public void testRotateToPortrait() {
		solo.setActivityOrientation(Solo.LANDSCAPE);//	public void testRotateToPortrait() {
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(3000);
		pressTutorialButton();//	public void testRotateToPortrait() {
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(3000);
		pressTutorialButton();
		solo.sleep(3000);
		int orientation = solo.getActivityMonitor().getLastActivity().getRequestedOrientation();
		Tutorial.getInstance(null).stopButtonTutorial();
		assertTrue("Tutorial: Setting Orientation to Portrait failed!", orientation == Solo.PORTRAIT);
	}

	//	solo.sleep(3000);
	//	int orientation = solo.getActivityMonitor().getLastActivity().getRequestedOrientation();
	//	Tutorial.getInstance(null).stopButtonTutorial();
	//	assertTrue("Tutorial: Setting Orientation to Portrait failed!", orientation == Solo.PORTRAIT);
	//}
	//		solo.sleep(3000);
	//		pressTutorialButton();
	//		solo.sleep(3000);
	//		int orientation = solo.getActivityMonitor().getLastActivity().getRequestedOrientation();
	//		Tutorial.getInstance(null).stopButtonTutorial();
	//		assertTrue("Tutorial: Setting Orientation to Portrait failed!", orientation == Solo.PORTRAIT);
	//	}

	private void pressTutorialButton() {
		buttonPressRunnable = new Runnable() {
			public void run() {
				Button tutorialButton = (Button) getActivity().findViewById(R.id.tutorial_button);
				tutorialButton.performClick();
			}
		};
		getActivity().runOnUiThread(buttonPressRunnable);
	}

	//	test= new Runnable() {
	//			public void run() {
	//				Button tutorialButton = (Button) getActivity().findViewById(R.id.tutorial_button);
	//				tutorialButton.performClick();
	//			}
	//		getActivity().runOnUiThread(test);
	//	private void pressCurrentProjectButton() {
	//		getActivity().runOnUiThread(new Runnable() {
	//			public void run() {
	//				Button tutorialButton = (Button) getActivity().findViewById(R.id.current_project_button);
	//				tutorialButton.performClick();
	//			}
	//		});
	//	}
	//	public void testAboutCatroid() {
	//		solo.clickOnButton(getActivity().getString(R.string.about));
	//		ArrayList<TextView> textViewList = solo.getCurrentTextViews(null);
	//
	//		assertEquals("Title is not correct!", getActivity().getString(R.string.about_title), textViewList.get(0)
	//				.getText().toString());
	//		assertEquals("About text not correct!", getActivity().getString(R.string.about_text), textViewList.get(1)
	//				.getText().toString());
	//		assertEquals("Link text is not correct!", getActivity().getString(R.string.about_link_text), textViewList
	//				.get(2).getText().toString());
	//	}

	//	private void setTutorialActivatedBoolean(boolean newValue) {
	//		Tutorial tutorial;
	//		try {
	//			MainMenuActivity act = getActivity();
	//			tutorial = (Tutorial) UiTestUtils.getPrivateField("tutorial", act);
	//			Field field = tutorial.getClass().getDeclaredField("tutorialActive");
	//			field.setAccessible(true);
	//			field.setBoolean(tutorial, newValue);
	//		} catch (Exception e) {
	//			Log.e("maxxle", "Class Cast Exception for Tutorial");
	//		}
	//	}

	//	assertTrue("Wrong orientation! Screen height: " + Values.SCREEN_HEIGHT + ", Screen width: "
	//			+ Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT > Values.SCREEN_WIDTH);

	public boolean isPortrait() {

		return true;
	}

	//	public void testStartAndStopTutorial() {
	//		if (!DEBUG) {
	//			solo.sleep(3000);
	//			assertFalse("Tutorial active but should not be", getTutorialActivatedBoolean(true));
	//			pressTutorialButton();
	//			solo.sleep(3000);
	//			assertTrue("Tutorial is not active but should be", getTutorialActivatedBoolean(false));
	//			pressTutorialButton();
	//			solo.sleep(3000);
	//			assertFalse("Tutorial active but should not be", getTutorialActivatedBoolean(true));
	//		}
	//	}
	//
	//	public void testLandscapePortraitStart() {
	//		if (!DEBUG) {
	//			solo.sleep(5000);
	//			assertFalse("Tutorial active but should not be", getTutorialActivatedBoolean(true));
	//			solo.setActivityOrientation(Solo.LANDSCAPE);
	//			solo.sleep(5000);
	//			solo.setActivityOrientation(Solo.PORTRAIT);
	//			solo.sleep(5000);
	//			assertFalse("Tutorial active but should not be", getTutorialActivatedBoolean(true));
	//
	//			pressTutorialButton();
	//
	//			solo.sleep(5000);
	//
	//			assertTrue("Tutorial is not active but should be", getTutorialActivatedBoolean(false));
	//
	//			pressTutorialButton();
	//
	//			solo.sleep(5000);
	//
	//			assertFalse("Tutorial is active but should not be", getTutorialActivatedBoolean(false));
	//		}
	//	}
}