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
package at.tugraz.ist.catroid.uitest.ui.dialog;

import java.io.IOException;
import java.util.ArrayList;

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.BroadcastBrick;
import at.tugraz.ist.catroid.content.bricks.BroadcastWaitBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeBrightnessBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeGhostEffectBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeSizeByNBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeVolumeByBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeXByBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;
import at.tugraz.ist.catroid.content.bricks.ClearGraphicEffectBrick;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.ForeverBrick;
import at.tugraz.ist.catroid.content.bricks.GlideToBrick;
import at.tugraz.ist.catroid.content.bricks.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.IfOnEdgeBounceBrick;
import at.tugraz.ist.catroid.content.bricks.MoveNStepsBrick;
import at.tugraz.ist.catroid.content.bricks.NextCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.NoteBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.PointInDirectionBrick;
import at.tugraz.ist.catroid.content.bricks.PointToBrick;
import at.tugraz.ist.catroid.content.bricks.RepeatBrick;
import at.tugraz.ist.catroid.content.bricks.SetBrightnessBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetGhostEffectBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.SetVolumeToBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.SetYBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.SpeakBrick;
import at.tugraz.ist.catroid.content.bricks.StopAllSoundsBrick;
import at.tugraz.ist.catroid.content.bricks.TurnLeftBrick;
import at.tugraz.ist.catroid.content.bricks.TurnRightBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class CopySpriteDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private String testProject = UiTestUtils.PROJECTNAME1;
	private ProjectManager projectMangaer = ProjectManager.getInstance();

	public CopySpriteDialogTest() {
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

	public void testCopySpriteDialog() throws NameNotFoundException, IOException {

		createTestProject(testProject);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.clickOnText(testProject);
		solo.clickLongOnText("blue");

		solo.sleep(500);
		assertEquals("Copy is not in context menu!", true, solo.searchText("Kopieren"));
		solo.clickOnText("Kopieren");
		solo.sleep(500);
		solo.sendKey(Solo.ENTER);

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite copiedSprite = ((Sprite) spritesList.getItemAtPosition(5));
		Sprite firstSprite = ((Sprite) spritesList.getItemAtPosition(1));

		assertEquals("The first sprite is NOT copied!", copiedSprite.getName(), "blue - Kopie");
		assertEquals("The first sprite has a new name!", firstSprite.getName(), "blue");

		ArrayList<Brick> brickListCopiedSprite = copiedSprite.getScript(0).getBrickList();
		ArrayList<Brick> brickListFirstSprite = firstSprite.getScript(0).getBrickList();

		assertEquals("The number of Scripts differs!", copiedSprite.getNumberOfScripts(),
				firstSprite.getNumberOfScripts());
		assertEquals("The number of Bricks differs!", brickListCopiedSprite.size(), brickListFirstSprite.size());

		int brickCounter = 0;
		for (Brick element : brickListCopiedSprite) {
			assertEquals("Brick classes are different!", element.getClass(), brickListFirstSprite.get(brickCounter)
					.getClass());
			brickCounter++;
		}
	}

	public void testCopySpriteAddDeleteBricks() throws NameNotFoundException, IOException {

		createTestProject(testProject);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.clickOnText(testProject);
		solo.clickLongOnText("blue");

		solo.sleep(500);
		solo.clickOnText("Kopieren");
		solo.sleep(500);
		solo.sendKey(Solo.ENTER);
		solo.sleep(500);
		solo.clickOnText("blue - Kopie");
		solo.sleep(500);

		Sprite currentSprite = projectMangaer.getCurrentSprite();
		Script scriptCopied = projectMangaer.getCurrentScript();
		Script scriptOriginal = projectMangaer.getCurrentProject().getSpriteList().get(1).getScript(0);

		scriptCopied.addBrick(new SetXBrick(currentSprite, 10));
		assertEquals("The number of Bricks differs!", scriptCopied.getBrickList().size() - 1, scriptOriginal
				.getBrickList().size());

		scriptOriginal.addBrick(new SetXBrick(currentSprite, 10));
		assertEquals("The number of Bricks differs!", scriptCopied.getBrickList().size(), scriptOriginal.getBrickList()
				.size());

		scriptCopied.removeBrick(scriptCopied.getBrickList().get(5));
		assertEquals("The number of Bricks differs!", scriptCopied.getBrickList().size() + 1, scriptOriginal
				.getBrickList().size());

		int brickCounter = scriptCopied.getBrickList().size();
		scriptOriginal.removeBrick(scriptOriginal.getBrickList().get(6));
		assertEquals("The number of Bricks differs!", scriptCopied.getBrickList().size(), scriptOriginal.getBrickList()
				.size());

		solo.goBack();
		solo.sleep(500);
		solo.clickLongOnText("blue");
		solo.clickOnText("Delete");
		solo.sleep(500);
		solo.sendKey(Solo.ENTER);
		solo.sleep(500);
		solo.clickOnText("blue - Kopie");
		solo.sleep(500);

		assertEquals("The number of Bricks differs!", projectMangaer.getCurrentScript().getBrickList().size(),
				brickCounter);

	}

	public void testCopySpriteIDs() throws NameNotFoundException, IOException {

		createTestProject(testProject);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.clickOnText(testProject);
		solo.clickLongOnText("blue");

		solo.sleep(500);
		solo.clickOnText("Kopieren");
		solo.sleep(500);
		solo.sendKey(Solo.ENTER);
		solo.sleep(500);

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite copiedSprite = ((Sprite) spritesList.getItemAtPosition(5));
		Sprite firstSprite = ((Sprite) spritesList.getItemAtPosition(1));

		assertNotSame("Sprite is not copied!", firstSprite, copiedSprite);
		assertNotSame("CustomDataList is not copied!", firstSprite.getCostumeDataList(),
				copiedSprite.getCostumeDataList());
		assertNotSame("Scriptlist is no copied!", firstSprite.getScript(0), copiedSprite.getScript(0));
		assertNotSame("Script is no copied!", firstSprite.getScript(0), copiedSprite.getScript(0));
		assertNotSame("Soundlist is no copied!", firstSprite.getSoundList(), copiedSprite.getSoundList());

		ArrayList<Brick> brickListFirstSprite = firstSprite.getScript(0).getBrickList();
		ArrayList<Brick> brickListCopiedSprite = copiedSprite.getScript(0).getBrickList();
		assertNotSame("Script is no copied!", brickListFirstSprite, brickListCopiedSprite);

		int loopCounter = 0;
		for (Brick element : brickListFirstSprite) {
			assertNotSame("Brick is no copied!", element, brickListCopiedSprite.get(loopCounter));
			loopCounter++;
		}

	}

	public void createTestProject(String projectName) {
		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("blue");
		Sprite secondSprite = new Sprite("lila");
		Sprite thirdSprite = new Sprite("pink");
		Sprite fourthSprite = new Sprite("yellow");

		Script firstSpriteScript = new StartScript("firstSpriteScript", firstSprite);

		ArrayList<Brick> brickList = new ArrayList<Brick>();
		brickList.add(new PlaceAtBrick(firstSprite, 11, 12));
		brickList.add(new SetXBrick(firstSprite, 13));
		brickList.add(new SetYBrick(firstSprite, 14));
		brickList.add(new ChangeXByBrick(firstSprite, 15));
		brickList.add(new ChangeYByBrick(firstSprite, 16));
		brickList.add(new IfOnEdgeBounceBrick(firstSprite));
		brickList.add(new MoveNStepsBrick(firstSprite, 17));
		brickList.add(new TurnLeftBrick(firstSprite, 18));
		brickList.add(new TurnRightBrick(firstSprite, 19));
		brickList.add(new PointInDirectionBrick(firstSprite, 20));
		brickList.add(new PointToBrick(firstSprite, secondSprite));
		brickList.add(new GlideToBrick(firstSprite, 21, 22, 23));
		brickList.add(new GoNStepsBackBrick(firstSprite, 24));
		brickList.add(new ComeToFrontBrick(firstSprite));

		for (Brick brick : brickList) {
			firstSpriteScript.addBrick(brick);
		}

		Script secondSpriteScript = new StartScript("secondSpriteScript", secondSprite);

		brickList = new ArrayList<Brick>();
		brickList.add(new SetCostumeBrick(secondSprite));
		brickList.add(new SetSizeToBrick(secondSprite, 11));
		brickList.add(new ChangeSizeByNBrick(secondSprite, 12));
		brickList.add(new HideBrick(secondSprite));
		brickList.add(new ShowBrick(secondSprite));
		brickList.add(new SetGhostEffectBrick(secondSprite, 13));
		brickList.add(new ChangeGhostEffectBrick(secondSprite, 14));
		brickList.add(new SetBrightnessBrick(secondSprite, 15));
		brickList.add(new ChangeBrightnessBrick(secondSprite, 16));
		brickList.add(new ClearGraphicEffectBrick(secondSprite));
		brickList.add(new NextCostumeBrick(secondSprite));

		for (Brick brick : brickList) {
			secondSpriteScript.addBrick(brick);
		}

		Script thirdSpriteScript = new StartScript("thirdSpriteScript", thirdSprite);

		brickList = new ArrayList<Brick>();
		brickList.add(new PlaySoundBrick(thirdSprite));
		brickList.add(new StopAllSoundsBrick(thirdSprite));
		brickList.add(new SetVolumeToBrick(thirdSprite, 17));
		brickList.add(new ChangeVolumeByBrick(thirdSprite, 18));
		brickList.add(new SpeakBrick(thirdSprite, "Hallo"));

		brickList.add(new WaitBrick(thirdSprite, 19));
		brickList.add(new BroadcastBrick(thirdSprite));
		brickList.add(new BroadcastWaitBrick(thirdSprite));
		brickList.add(new NoteBrick(thirdSprite));
		brickList.add(new ForeverBrick(thirdSprite));
		brickList.add(new RepeatBrick(thirdSprite, 20));

		for (Brick brick : brickList) {
			thirdSpriteScript.addBrick(brick);
		}

		firstSprite.addScript(firstSpriteScript);
		secondSprite.addScript(secondSpriteScript);
		thirdSprite.addScript(thirdSpriteScript);
		project.addSprite(firstSprite);
		project.addSprite(secondSprite);
		project.addSprite(thirdSprite);
		project.addSprite(fourthSprite);

		projectMangaer.fileChecksumContainer = new FileChecksumContainer();
		projectMangaer.setProject(project);
		projectMangaer.setCurrentSprite(firstSprite);
		projectMangaer.setCurrentScript(firstSpriteScript);

		projectMangaer.setProject(project);
		storageHandler.saveProject(project);
	}
}
