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
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.tutorial.Tutorial;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class TutorialActivationTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

	public TutorialActivationTest() {
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

	public void testMainMenuActivity() {
		String test = getActivity().getString(R.string.tutorial);

		//		solo.setActivityOrientation(Solo.LANDSCAPE);
		//		solo.sleep(5000);
		//		solo.setActivityOrientation(Solo.PORTRAIT);
		//		solo.sleep(5000);

		assertFalse("Tutorial active but should not be", getTutorialActivatedBoolean(true));

		try {
			//UiTestUtils.clickOnImageButton(solo, R.id.tutorial_button);
			solo.clickOnButton(test);
		} catch (Exception e) {
			Log.e("maxxle", "Exception bei Click Button in MainMenuActivity");
		}

		solo.sleep(5000);

		assertTrue("Tutorial is not active but should be", getTutorialActivatedBoolean(false));

		try {
			//UiTestUtils.clickOnImageButton(solo, R.id.tutorial_button);
			solo.clickOnButton(test);
		} catch (Exception e) {
			Log.e("maxxle", "Exception bei Click Button in MainMenuActivity");
		}

		solo.sleep(10000);

		assertFalse("Tutorial is actived but should not be", getTutorialActivatedBoolean(false));

		//
		//		solo.setActivityOrientation(Solo.PORTRAIT);
		//
		//		solo.sleep(5000);
		//
		//		assertTrue("Tutorial is not active but should be after setting portrait mode",
		//				getTutorialActivatedBoolean(false));
		//
		//		solo.setActivityOrientation(Solo.LANDSCAPE);
		//
		//		solo.sleep(5000);
		//
		//		assertTrue("Tutorial is not active but should be, after going back to landscape mode",
		//				getTutorialActivatedBoolean(false));
	}
}