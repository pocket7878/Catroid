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
package at.tugraz.ist.catroid.test.drone;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.plugin.Drone.bricks.*;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;

import com.jayway.android.robotium.solo.Solo;

public class TakeOfBrickTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private Project project;
	private static String projectName = "BrickTest";

	public TakeOfBrickTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		ProjectManager manager = ProjectManager.getInstance();
		manager.setProject(project);
		
		deleteTestProject();
	}
	
	private void deleteTestProject(){
		if ( StorageHandler.getInstance().projectExists(projectName)){
			Project delete = StorageHandler.getInstance().loadProject(projectName);
			StorageHandler.getInstance().deleteProject(delete);
			delete = null;
		}	
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		deleteTestProject();
		super.tearDown();
	}

	@Smoke
	public void testBroadcastBricks() {
		
		solo.clickOnText("New Project");
		
		solo.enterText(0, projectName);
		
		solo.sendKey(solo.ENTER);
		
		solo.clickOnText("Catroid");
		
		
		solo.sleep(1000);
		
		solo.clickOnText("Add");
		
		solo.clickOnText("Drone");
		
		solo.clickOnText("take off");
		
		solo.clickOnText("Add");
		solo.clickOnText("Drone");
		solo.clickOnText("land");
		
		solo.clickOnText("Start");
		
		solo.sleep(5000);
		
//		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
//		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());
//
//		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getItem(adapter
//				.getScriptId(groupCount - 1) + 1));
//
//		String testString = "test";
//		String testString2 = "test2";
//		String testString3 = "test3";
//
//		solo.clickOnButton(0);
//		solo.enterText(0, testString);
//		solo.sleep(600);
//
//		solo.sendKey(Solo.ENTER);
//		solo.sendKey(Solo.ENTER);
//
//		solo.sleep(500);
//		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(0).getSelectedItem());
//		assertNotSame("Wrong selection", testString, solo.getCurrentSpinners().get(1).getSelectedItem());
//
//		solo.pressSpinnerItem(1, 2);
//		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(1).getSelectedItem());
//
//		solo.pressSpinnerItem(2, 2);
//		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(2).getSelectedItem());
//
//		solo.clickOnButton(1);
//		solo.enterText(0, testString2);
//		solo.sleep(600);
//
//		solo.sendKey(Solo.ENTER);
//		solo.sendKey(Solo.ENTER);
//
//		solo.sleep(500);
//
//		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(0).getSelectedItem());
//		assertEquals("Wrong selection", testString2, (String) solo.getCurrentSpinners().get(1).getSelectedItem());
//		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(2).getSelectedItem());
//
//		solo.clickOnButton(2);
//		solo.enterText(0, testString3);
//		solo.sleep(600);
//
//		solo.sendKey(Solo.ENTER);
//		solo.sendKey(Solo.ENTER);
//
//		solo.sleep(500);
//
//		assertEquals("Wrong selection", testString, (String) solo.getCurrentSpinners().get(0).getSelectedItem());
//		assertEquals("Wrong selection", testString2, (String) solo.getCurrentSpinners().get(1).getSelectedItem());
//		assertEquals("Wrong selection", testString3, (String) solo.getCurrentSpinners().get(2).getSelectedItem());
//
//		solo.pressSpinnerItem(1, 4);
//		assertEquals("Wrong selection", testString3, (String) solo.getCurrentSpinners().get(1).getSelectedItem());

	}

	private void createProject() {
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		DroneLandBrick broadcastBrick = new DroneLandBrick(sprite);
		DroneTakeOffBrick takeOffBrick = new DroneTakeOffBrick(sprite);
		script.addBrick(broadcastBrick);
		script.addBrick(takeOffBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
