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
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.BroadcastReceiverBrick;
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
import at.tugraz.ist.catroid.content.bricks.LoopBeginBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.content.bricks.MoveNStepsBrick;
import at.tugraz.ist.catroid.content.bricks.NextCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.NoteBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
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

	public int checkNumberOfElements(Sprite firstSprite, Sprite copiedSprite) {

		ArrayList<SoundInfo> copiedSoundList = copiedSprite.getSoundList();
		ArrayList<SoundInfo> firstSoundList = firstSprite.getSoundList();
		assertEquals("The number of sounds differs!", firstSoundList.size(), copiedSoundList.size());

		ArrayList<CostumeData> copiedCustomeList = copiedSprite.getCostumeDataList();
		ArrayList<CostumeData> firstCustomeList = firstSprite.getCostumeDataList();
		assertEquals("The number of customes differs!", firstCustomeList.size(), copiedCustomeList.size());

		assertEquals("The first sprite is NOT copied!", copiedSprite.getName(),
				"blue " + getActivity().getString(R.string.copy_sprite_extension));
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
		return brickCounter;
	}

	public void checkSpecialBricks(Sprite firstSprite, Sprite copiedSprite) {

		assertEquals("Message of BroadcastReceiver Brick is not right!",
				((BroadcastScript) (firstSprite.getScript(1))).getBroadcastMessage(),
				((BroadcastScript) (copiedSprite.getScript(1))).getBroadcastMessage());

		ArrayList<Brick> brickListCopiedSprite = copiedSprite.getScript(0).getBrickList();
		ArrayList<Brick> brickListFirstSprite = firstSprite.getScript(0).getBrickList();

		LoopBeginBrick firstLoopBrick = (LoopBeginBrick) brickListFirstSprite.get(32);
		LoopBeginBrick copiedLoopBrick = (LoopBeginBrick) brickListCopiedSprite.get(32);
		LoopEndBrick firstEndBrick = firstLoopBrick.getLoopEndBrick();
		LoopEndBrick copiedEndBrick = copiedLoopBrick.getLoopEndBrick();
		assertNotSame("Loop Brick is not copied right!", firstEndBrick, copiedEndBrick);
		assertNotSame("Loop Brick is not copied right!", firstEndBrick.getLoopBeginBrick(),
				copiedEndBrick.getLoopBeginBrick());
		assertEquals("Loop Brick is not copied right!", firstEndBrick.getLoopBeginBrick(), firstLoopBrick);
		assertEquals("Loop Brick is not copied right!", copiedEndBrick.getLoopBeginBrick(), copiedLoopBrick);
		assertEquals("Loop Brick is not copied right!", firstLoopBrick.getLoopEndBrick(), firstEndBrick);
		assertEquals("Loop Brick is not copied right!", copiedLoopBrick.getLoopEndBrick(), copiedEndBrick);
	}

	public int checkIds(Sprite firstSprite, Sprite copiedSprite) {

		ArrayList<Brick> brickListCopiedSprite = copiedSprite.getScript(0).getBrickList();
		ArrayList<Brick> brickListFirstSprite = firstSprite.getScript(0).getBrickList();

		assertNotSame("Sprite is not copied!", firstSprite, copiedSprite);
		assertNotSame("CustomDataList is not copied!", firstSprite.getCostumeDataList(),
				copiedSprite.getCostumeDataList());
		assertNotSame("Script is no copied!", firstSprite.getScript(0), copiedSprite.getScript(0));
		assertNotSame("Script is no copied!", firstSprite.getScript(1), copiedSprite.getScript(1));
		assertNotSame("Soundlist is no copied!", firstSprite.getSoundList(), copiedSprite.getSoundList());

		brickListFirstSprite = firstSprite.getScript(0).getBrickList();
		brickListCopiedSprite = copiedSprite.getScript(0).getBrickList();
		assertNotSame("Script is not copied!", brickListFirstSprite, brickListCopiedSprite);

		int loopCounter = 0;
		for (Brick element : brickListFirstSprite) {
			assertNotSame("Brick is not copied!", element, brickListCopiedSprite.get(loopCounter));
			loopCounter++;
		}

		solo.clickOnText("blue " + getActivity().getString(R.string.copy_sprite_extension));
		solo.sleep(1000);

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

		scriptOriginal.removeBrick(scriptOriginal.getBrickList().get(6));
		assertEquals("The number of Bricks differs!", scriptCopied.getBrickList().size(), scriptOriginal.getBrickList()
				.size());

		return scriptCopied.getBrickList().size();

	}

	public void testCopySpriteDialog() throws NameNotFoundException, IOException {

		createTestProject(testProject);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.clickOnText(testProject);
		solo.clickLongOnText("blue");
		solo.sleep(500);
		assertEquals("Copy is not in context menu!", true, solo.searchText(getActivity().getString(R.string.copy)));
		solo.clickOnText(getActivity().getString(R.string.copy));
		solo.sleep(500);
		solo.sendKey(Solo.ENTER);

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite copiedSprite = ((Sprite) spritesList.getItemAtPosition(3));
		Sprite firstSprite = ((Sprite) spritesList.getItemAtPosition(1));

		checkNumberOfElements(firstSprite, copiedSprite);
		checkSpecialBricks(firstSprite, copiedSprite);
		int brickCounter = checkIds(firstSprite, copiedSprite);

		solo.goBack();
		solo.sleep(500);
		solo.clickLongOnText("blue");
		solo.clickOnText(getActivity().getString(R.string.delete));
		solo.sleep(500);
		solo.sendKey(Solo.ENTER);
		solo.sleep(500);
		solo.clickOnText("blue " + getActivity().getString(R.string.copy_sprite_extension));
		solo.sleep(500);

		assertEquals("The number of Bricks differs!", projectMangaer.getCurrentScript().getBrickList().size(),
				brickCounter);
	}

	public void createTestProject(String projectName) {
		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("blue");
		Sprite secondSprite = new Sprite("lila");

		Script firstSpriteScript = new StartScript(firstSprite);

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
		brickList.add(new PointToBrick(firstSprite, secondSprite));
		brickList.add(new GlideToBrick(firstSprite, 21, 22, 23));
		brickList.add(new GoNStepsBackBrick(firstSprite, 24));
		brickList.add(new ComeToFrontBrick(firstSprite));

		brickList.add(new SetCostumeBrick(firstSprite));
		brickList.add(new SetSizeToBrick(firstSprite, 11));
		brickList.add(new ChangeSizeByNBrick(firstSprite, 12));
		brickList.add(new HideBrick(firstSprite));
		brickList.add(new ShowBrick(firstSprite));
		brickList.add(new SetGhostEffectBrick(firstSprite, 13));
		brickList.add(new ChangeGhostEffectBrick(firstSprite, 14));
		brickList.add(new SetBrightnessBrick(firstSprite, 15));
		brickList.add(new ChangeBrightnessBrick(firstSprite, 16));
		brickList.add(new ClearGraphicEffectBrick(firstSprite));
		brickList.add(new NextCostumeBrick(firstSprite));

		brickList.add(new PlaySoundBrick(firstSprite));
		brickList.add(new StopAllSoundsBrick(firstSprite));
		brickList.add(new SetVolumeToBrick(firstSprite, 17));
		brickList.add(new ChangeVolumeByBrick(firstSprite, 18));
		brickList.add(new SpeakBrick(firstSprite, "Hallo"));

		brickList.add(new WaitBrick(firstSprite, 19));
		brickList.add(new BroadcastWaitBrick(firstSprite));
		brickList.add(new NoteBrick(firstSprite));
		LoopBeginBrick beginBrick = new ForeverBrick(firstSprite);
		LoopEndBrick endBrick = new LoopEndBrick(firstSprite, beginBrick);
		beginBrick.setLoopEndBrick(endBrick);
		brickList.add(beginBrick);
		brickList.add(endBrick);

		beginBrick = new RepeatBrick(firstSprite, 20);
		endBrick = new LoopEndBrick(firstSprite, beginBrick);
		beginBrick.setLoopEndBrick(endBrick);
		brickList.add(beginBrick);
		brickList.add(endBrick);

		for (Brick brick : brickList) {
			firstSpriteScript.addBrick(brick);
		}
		firstSprite.addScript(firstSpriteScript);

		BroadcastScript broadcastScript = new BroadcastScript(firstSprite);
		broadcastScript.setBroadcastMessage("Hallo");
		BroadcastReceiverBrick brickBroad = new BroadcastReceiverBrick(firstSprite, broadcastScript);
		firstSprite.addScript(broadcastScript);
		brickList.add(brickBroad);

		project.addSprite(firstSprite);
		project.addSprite(secondSprite);

		projectMangaer.fileChecksumContainer = new FileChecksumContainer();
		projectMangaer.setCurrentSprite(firstSprite);
		projectMangaer.setCurrentScript(firstSpriteScript);

		storageHandler.saveProject(project);
	}
}
