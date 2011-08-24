/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import android.util.Log;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.VibrateBrick;

public class VibrateBrickTest extends AndroidTestCase {

	public void testVibrate() {
		Sprite testSprite = new Sprite("testSprite");
		VibrateBrick vibrateBrick = new VibrateBrick(testSprite, 3000);
		if (vibrateBrick.isMobile() == true) {
			Log.d("MyActivity", "Emulator reached: Vibrate doesn't work on the emulator");
			assertTrue(true);
		} else {
			Log.d("MyActivity", "IS Mobile reached");
			vibrateBrick.execute();
			//NUR SINNVOLL WENN SENSOREVENTLISTENER EINGEBAUT WERDEN KANN
		}
	}

	public void testVibrateandPause() {
		Sprite testSprite = new Sprite("testSprite");
		Script script = new Script("testScript", testSprite);
		VibrateBrick vibrateBrick = new VibrateBrick(testSprite, 3000);

		assertFalse("Expected behaviour", vibrateBrick.vibrateStartValue());
		assertFalse("Expected behaviour", vibrateBrick.vibrateEndValue());

		script.addBrick(vibrateBrick);
		testSprite.getScriptList().add(script);
		Log.d("MyActivity", "VOR EXECUTE");
		testSprite.startScripts();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("Expected behaviour", vibrateBrick.vibrateStartValue());
		assertFalse("Expected behaviour", vibrateBrick.vibrateEndValue());
		Log.d("MyActivity", "Nach EXECUTE");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue("Expected behaviour", vibrateBrick.vibrateStartValue());
		assertTrue("Expected behaviour", vibrateBrick.vibrateEndValue());

	}

	public void testNullSprite() {
		VibrateBrick vibrateBrick = new VibrateBrick(null, 1000);
		try {
			vibrateBrick.execute();
			fail("Execution of Vibrate Brick with null Sprite did not cause a " +
					"NullPointerException to be thrown");
		} catch (NullPointerException e) {
			// expected behavior
		}
	}
}
