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

import java.lang.reflect.Field;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.tutorial.Tutorial;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ActivateDeactivateTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

	public ActivateDeactivateTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		UiTestUtils.clearAllUtilTestProjects();
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
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	private boolean getTutorialActivatedBoolean(boolean valueItShouldNotBe) {
		Tutorial tut;
		boolean tutorialActive = valueItShouldNotBe;
		try {
			MainMenuActivity act = getActivity();
			tut = (Tutorial) UiTestUtils.getPrivateField("tutorial", act);
			Field booli = tut.getClass().getDeclaredField("tutorialActive");
			booli.setAccessible(true);
			tutorialActive = booli.getBoolean(tut);
		} catch (Exception e) {
			Log.e("maxxle", "Class Cast Exception for Tutorial");
		}
		return tutorialActive;
	}

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

	//	UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
	//	solo.waitForActivity(StageActivity.class.getName(), 1000);
	//	solo.sleep(5000);
	//	assertTrue("Wrong orientation! Screen height: " + Values.SCREEN_HEIGHT + ", Screen width: "
	//			+ Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT > Values.SCREEN_WIDTH);

	public void testStartAndStopTutorial() {
		solo.sleep(5000);
		assertFalse("Tutorial active but should not be", getTutorialActivatedBoolean(true));

		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				Button tutorialButton = (Button) getActivity().findViewById(R.id.tutorial_button);
				tutorialButton.performClick();
			}
		});

		solo.sleep(10000);

		assertTrue("Tutorial is not active but should be", getTutorialActivatedBoolean(false));

		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				Button tutorialButton = (Button) getActivity().findViewById(R.id.tutorial_button);
				tutorialButton.performClick();
			}
		});

		solo.sleep(5000);

		assertFalse("Tutorial active but should not be", getTutorialActivatedBoolean(true));
	}

}