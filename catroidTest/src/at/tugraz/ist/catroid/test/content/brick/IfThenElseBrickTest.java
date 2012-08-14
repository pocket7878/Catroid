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
package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.ElseBrick;
import at.tugraz.ist.catroid.content.bricks.IfThenElseBrick;
import at.tugraz.ist.catroid.content.bricks.IfThenElseBrick.LogicOperator;
import at.tugraz.ist.catroid.content.bricks.SetYBrick;

public class IfThenElseBrickTest extends AndroidTestCase {

	private int firstValue = 100;
	private int secondValue = 200;
	final int deltaY = -10;
	private Sprite testSprite;
	private StartScript testScript;
	private IfThenElseBrick ifThenElseBrick;
	private ElseBrick elseBrick;

	public void testMoreThan() {
		testSprite = new Sprite("testSprite");

		testSprite.removeAllScripts();
		testScript = new StartScript(testSprite);
		ifThenElseBrick = new IfThenElseBrick(testSprite, secondValue, LogicOperator.MORE_THAN, firstValue);
		elseBrick = new ElseBrick(testSprite, ifThenElseBrick);
		ifThenElseBrick.setElseBrick(elseBrick);

		testScript.addBrick(ifThenElseBrick);
		testScript.addBrick(new SetYBrick(testSprite, deltaY));
		testScript.addBrick(elseBrick);

		testSprite.addScript(testScript);
		testSprite.startStartScripts();

		try {
			Thread.sleep(100);
			testSprite.finish();
			assertEquals("Inner brick is not executed!", deltaY, (int) testSprite.costume.getYPosition());
		} catch (InterruptedException e) {

		}
	}

	public void testLessThan() {
		testSprite = new Sprite("testSprite");

		testSprite.removeAllScripts();
		testScript = new StartScript(testSprite);

		ifThenElseBrick = new IfThenElseBrick(testSprite, firstValue, LogicOperator.LESS_THAN, secondValue);
		elseBrick = new ElseBrick(testSprite, ifThenElseBrick);
		ifThenElseBrick.setElseBrick(elseBrick);

		testScript.addBrick(ifThenElseBrick);
		testScript.addBrick(new SetYBrick(testSprite, deltaY));
		testScript.addBrick(elseBrick);

		testSprite.addScript(testScript);
		testSprite.startStartScripts();

		try {
			Thread.sleep(100);
			testSprite.finish();
			assertEquals("Inner brick is not executed!", deltaY, (int) testSprite.costume.getYPosition());
		} catch (InterruptedException e) {

		}
	}

	public void testEqualTo() {
		testSprite = new Sprite("testSprite");

		testSprite.removeAllScripts();
		testScript = new StartScript(testSprite);

		ifThenElseBrick = new IfThenElseBrick(testSprite, firstValue, LogicOperator.EQUAL_TO, firstValue);
		elseBrick = new ElseBrick(testSprite, ifThenElseBrick);
		ifThenElseBrick.setElseBrick(elseBrick);

		testScript.addBrick(ifThenElseBrick);
		testScript.addBrick(new SetYBrick(testSprite, deltaY));
		testScript.addBrick(elseBrick);

		testSprite.addScript(testScript);
		testSprite.startStartScripts();

		try {
			Thread.sleep(100);
			testSprite.finish();
			assertEquals("Inner brick is not executed!", deltaY, (int) testSprite.costume.getYPosition());
		} catch (InterruptedException e) {

		}
	}

	public void testIncorrectLogic() {
		testSprite = new Sprite("testSprite");

		testSprite.removeAllScripts();
		testScript = new StartScript(testSprite);

		ifThenElseBrick = new IfThenElseBrick(testSprite, secondValue, LogicOperator.LESS_THAN, firstValue);
		elseBrick = new ElseBrick(testSprite, ifThenElseBrick);
		ifThenElseBrick.setElseBrick(elseBrick);

		testScript.addBrick(ifThenElseBrick);
		testScript.addBrick(new SetYBrick(testSprite, deltaY));
		testScript.addBrick(elseBrick);

		testSprite.addScript(testScript);
		testSprite.startStartScripts();

		try {
			Thread.sleep(100);
			testSprite.finish();
			assertNotSame("Wrong Execution!", deltaY, (int) testSprite.costume.getYPosition());
		} catch (InterruptedException e) {

		}
	}
}
