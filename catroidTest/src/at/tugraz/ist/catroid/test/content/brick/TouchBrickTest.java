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

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.TouchBrick;
import at.tugraz.ist.catroid.content.bricks.TouchEndBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class TouchBrickTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = R.raw.icon;
	private File testImage;
	int width;
	int height;
	private String projectName = "testProject";
	private Project project;
	private CostumeData costumeData;

	@Override
	protected void setUp() throws Exception {

		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(this.projectName, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		costumeData = new CostumeData();
		costumeData.setCostumeFilename(testImage.getName());
		costumeData.setCostumeName("CostumeName");

		Bitmap bitmap = BitmapFactory.decodeFile(testImage.getAbsolutePath());
		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}

	@Override
	protected void tearDown() throws Exception {
		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
		if (testImage != null && testImage.exists()) {
			testImage.delete();
		}
	}

	public void testTouchBrick() throws InterruptedException {

		final int deltaX = -100;
		Sprite sprite1 = new Sprite("cat1");
		Sprite sprite2 = new Sprite("cat2");

		sprite1.costume.width = width;
		sprite1.costume.height = height;
		sprite1.costume.setXPosition(0);
		sprite1.costume.setYPosition(0);

		sprite2.costume.width = width;
		sprite2.costume.height = height;
		sprite2.costume.setXPosition(0);
		sprite2.costume.setYPosition(0);

		project.addSprite(sprite1);
		project.addSprite(sprite2);

		TouchBrick touchBrick = new TouchBrick(sprite1, sprite2);
		TouchEndBrick touchEndBrick = new TouchEndBrick(sprite1, touchBrick);
		touchBrick.setTouchEndBrick(touchEndBrick);

		Script startScript1 = new StartScript(sprite1);
		SetCostumeBrick setCostumeBrick1 = new SetCostumeBrick(sprite1);
		sprite1.getCostumeDataList().add(costumeData);
		setCostumeBrick1.setCostume(costumeData);
		PlaceAtBrick placeAt1 = new PlaceAtBrick(sprite1, 20, 10);
		startScript1.addBrick(setCostumeBrick1);
		startScript1.addBrick(placeAt1);
		startScript1.addBrick(touchBrick);
		startScript1.addBrick(new SetXBrick(sprite1, deltaX));
		startScript1.addBrick(touchEndBrick);
		sprite1.addScript(startScript1);

		Script startScript2 = new StartScript(sprite2);
		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(sprite2);
		sprite2.getCostumeDataList().add(costumeData);
		setCostumeBrick2.setCostume(costumeData);
		PlaceAtBrick placeAt2 = new PlaceAtBrick(sprite2, 20, 10);
		startScript2.addBrick(setCostumeBrick2);
		startScript2.addBrick(placeAt2);
		sprite2.addScript(startScript2);

		sprite1.startStartScripts();
		sprite2.startStartScripts();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		assertEquals("The Touch Sensing is failed!", deltaX, (int) sprite1.costume.getXPosition());
	}

}
