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
package at.tugraz.ist.catroid.test.stage;

import java.io.File;
import java.io.IOException;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageRecorder;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

public class ScreenRecorderTest extends InstrumentationTestCase {
	private static final int SOUND_FILE_ID = R.raw.testsound;
	private File soundFile;
	private String projectName = "projectName";
	private SoundInfo soundInfo;
	private Costume costume;
	StageRecorder recorder;

	@Override
	protected void setUp() throws Exception {
		File directory = new File(Consts.DEFAULT_ROOT + "/" + projectName);
		UtilFile.deleteDirectory(directory);
		createTestProject();

		recorder = StageRecorder.getInstance();
		soundFile = TestUtils.saveFileToProject(projectName, "soundTest.mp3", SOUND_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_SOUND_FILE);
		soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle("testSoundFile");
		costume = new Costume(new Sprite("sprite"));
	};

	public void testRecorder() throws InterruptedException {
		assertEquals("Recorder started before start().", 0, recorder.getStartTime());
		recorder.start();
		Thread.sleep(500);
		assertTrue("Recorder didn't started at time.", recorder.getTime() >= 500);
		recorder.recordCostume(costume);
		recorder.recordSound(soundInfo);
		Thread.sleep(500);
		String currentProject = ProjectManager.getInstance().getCurrentProject().getName();
		File recordedFile = new File(Utils.buildPath(Consts.DEFAULT_ROOT, currentProject), "record.json");

		assertTrue("Costume recorded wrongly.",
				recorder.getRecording().costumeList.get(0).first.name.equals(costume.name));
		assertTrue("SoundInfo recorded wrongly.",
				recorder.getRecording().soundList.get(0).first.getTitle().equals(soundInfo.getTitle()));

		assertTrue("Duration recorded wrongly", recorder.getRecording().duration >= 1000);

		String[] expectedFields = { "<name>", "<rotation>", "<scaleX>", "<scaleY>", "<visible>", "<x>", "<y>",
				"<zPosition>", "<fileName>", "<show>", "<brightnessValue>", "<alphaValue>", "<fileName>", "<isPlaying>" };
		for (String field : expectedFields) {
			assertTrue("Recorded XML doesn't contain the field: " + field, recordedXml.contains(field));
		}
	}

	private void createTestProject() throws IOException {
		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		setUpSoundFile();
	}

	private void setUpSoundFile() throws IOException {
		soundFile = TestUtils.saveFileToProject(projectName, "soundTest.mp3", SOUND_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_SOUND_FILE);
	}
}
