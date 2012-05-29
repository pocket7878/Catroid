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
package org.catrobat.catroid.uitest.content.brick;

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessBrick;
import org.catrobat.catroid.content.bricks.ChangeGhostEffectBrick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByBrick;
import org.catrobat.catroid.content.bricks.ChangeXByBrick;
import org.catrobat.catroid.content.bricks.ChangeYByBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetCostumeBrick;
import org.catrobat.catroid.content.bricks.SetGhostEffectBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick.Direction;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptTabActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import org.catrobat.catroid.R;

import com.jayway.android.robotium.solo.Solo;

public class BrickExceptionOnDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private Project project;
	private Sprite sprite;
	private Script script;
	private final String spriteName = "cat";

	public BrickExceptionOnDialogTest() {
		super("org.catrobat.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		createProject();
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

	public void testBroadCastBrick() {
		BroadcastBrick broadcastBrick = new BroadcastBrick(sprite);
		script.addBrick(broadcastBrick);

		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.clickOnText(spriteName);
		solo.clickOnText(getActivity().getString(R.string.new_broadcast_message));
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnText(getActivity().getString(R.string.new_broadcast_message));
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void testBroadcastWaitBrick() {
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(sprite);
		script.addBrick(broadcastWaitBrick);

		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.clickOnText(spriteName);
		solo.clickOnText(getActivity().getString(R.string.new_broadcast_message));
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnText(getActivity().getString(R.string.new_broadcast_message));
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void testChangeBrightnessBrick() {
		ChangeBrightnessBrick brightnessBrick = new ChangeBrightnessBrick(sprite, 40);
		script.addBrick(brightnessBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testChangeGhostEffectBrick() {
		ChangeGhostEffectBrick ghostBrick = new ChangeGhostEffectBrick(sprite, 40);
		script.addBrick(ghostBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testChangeSizeByNBrick() {
		ChangeSizeByNBrick sizeByNBrick = new ChangeSizeByNBrick(sprite, 40);
		script.addBrick(sizeByNBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testChangeVolumeByBrick() {
		ChangeVolumeByBrick changeVolumeBrick = new ChangeVolumeByBrick(sprite, 40);
		script.addBrick(changeVolumeBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testChangeXByBrick() {
		ChangeXByBrick changeXByBrick = new ChangeXByBrick(sprite, 40);
		script.addBrick(changeXByBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testChangeYByBrick() {
		ChangeYByBrick changeYByBrick = new ChangeYByBrick(sprite, 40);
		script.addBrick(changeYByBrick);

		clickEditTextGoBackAndClickAgain();
	}

	//TODO test glideToBrick

	public void testGoNStepsBackBrick() {
		GoNStepsBackBrick nStepsBackBrick = new GoNStepsBackBrick(sprite, 40);
		script.addBrick(nStepsBackBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testMoveNStepsBrick() {
		MoveNStepsBrick nStepsBrick = new MoveNStepsBrick(sprite, 40);
		script.addBrick(nStepsBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testNoteBrick() {
		NoteBrick noteBrick = new NoteBrick(sprite);
		script.addBrick(noteBrick);

		clickEditTextGoBackAndClickAgain();
	}

	//TODO test nxt
	public void testPlaceAtBrick() {
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(sprite, 40, 40);
		script.addBrick(placeAtBrick);

		clickEditTextGoBackAndClickAgain();

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnEditText(1);
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void testPlaySoundBrick() {
		PlaySoundBrick playSoundBrick = new PlaySoundBrick(sprite);
		script.addBrick(playSoundBrick);

		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.clickOnText(spriteName);
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void testSetCostumeBrick() {
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		script.addBrick(setCostumeBrick);

		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.clickOnText(spriteName);
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void testPointInDirectionBrick() {
		PointInDirectionBrick directionBrick = new PointInDirectionBrick(sprite, Direction.DIRECTION_RIGHT);
		script.addBrick(directionBrick);

		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.clickOnText(spriteName);
		solo.clickOnText("90");
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnText("90");
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void testRepeatBrick() {
		RepeatBrick repeatbrick = new RepeatBrick(sprite, 3);
		script.addBrick(repeatbrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testSetBrightnessBrick() {
		SetBrightnessBrick setBrightnessBrick = new SetBrightnessBrick(sprite, 4);
		script.addBrick(setBrightnessBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testSetGhostEffectBrick() {
		SetGhostEffectBrick ghostEffectBrick = new SetGhostEffectBrick(sprite, 4);
		script.addBrick(ghostEffectBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testSetSizeToBrick() {
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(sprite, 4);
		script.addBrick(setSizeToBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testSetVolumeToBrick() {
		SetVolumeToBrick setVolumeToBrick = new SetVolumeToBrick(sprite, 4);
		script.addBrick(setVolumeToBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testSetXBrick() {
		SetXBrick setXBrick = new SetXBrick(sprite, 4);
		script.addBrick(setXBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testSetYBrick() {
		SetYBrick setYBrick = new SetYBrick(sprite, 4);
		script.addBrick(setYBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testSpeakBrick() {
		SpeakBrick speakBrick = new SpeakBrick(sprite, "I say lol");
		script.addBrick(speakBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void testWaitBrick() {
		WaitBrick waitBrick = new WaitBrick(sprite, 500);
		script.addBrick(waitBrick);

		clickEditTextGoBackAndClickAgain();
	}

	public void clickEditTextGoBackAndClickAgain() {
		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.clickOnText(spriteName);
		solo.sleep(500);
		solo.clickOnEditText(0);
		solo.sleep(500);
		solo.goBack();
		solo.sleep(300);
		solo.goBack();
		solo.sleep(300);
		solo.goBack();
		solo.sleep(500);
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnEditText(0);
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void createProject() {
		sprite = new Sprite(spriteName);
		script = new StartScript(sprite);
		sprite.addScript(script);
		ProjectManager manager = ProjectManager.getInstance();
		manager.fileChecksumContainer = new FileChecksumContainer();
		manager.setProject(project);
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(sprite);
		project = UiTestUtils.createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, spriteList, getActivity());
	}
}
