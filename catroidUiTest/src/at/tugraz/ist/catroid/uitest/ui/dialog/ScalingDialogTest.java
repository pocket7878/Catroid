package at.tugraz.ist.catroid.uitest.ui.dialog;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ScalingDialogTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private SetSizeToBrick setSizeBrick;
	private double originalSize = 110;

	public ScalingDialogTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {

		UiTestUtils.clearAllUtilTestProjects();
		createTestProject();
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

	public void testScaleEditText() {

		double scaleFactor = 175;

		solo.clickOnEditText(0);

		solo.sleep(1000);
		assertEquals(originalSize, Double.valueOf(solo.getEditText(0).getText().toString()));
		solo.sleep(1000);

		solo.clearEditText(solo.getEditText(0));
		solo.enterText(solo.getEditText(0), Double.toString(scaleFactor));

		solo.clickOnButton("OK");
		solo.sleep(10000);

		double actualScale = (Double) UiTestUtils.getPrivateField("size", setSizeBrick);
		assertEquals("Wrong text in private field", scaleFactor, actualScale);
		assertEquals("Text not updated", scaleFactor, Double.parseDouble(solo.getEditText(0).getText().toString()));
	}

	public void testScaleSeekBar() {

		int scaleFactor = 150;

		solo.clickOnEditText(0);

		solo.sleep(1000);
		assertEquals(originalSize, Double.valueOf(solo.getEditText(0).getText().toString()));
		solo.sleep(1000);

		solo.setProgressBar(0, scaleFactor);

		solo.clickOnButton("OK");
		solo.sleep(1000);

		double actualScale = (Double) UiTestUtils.getPrivateField("size", setSizeBrick);
		assertEquals("Wrong text in private field", (double) scaleFactor, actualScale);
		assertEquals("Text not updated", (double) scaleFactor, Double.valueOf(solo.getEditText(0).getText().toString()));
	}

	public void testScaleSeekBarUpdate() {

		int scaleFactor = 175;

		solo.clickOnEditText(0);

		solo.sleep(1000);
		assertEquals(originalSize, Double.valueOf(solo.getEditText(0).getText().toString()));
		solo.sleep(1000);
		solo.clearEditText(solo.getEditText(0));
		solo.enterText(solo.getEditText(0), Double.toString(scaleFactor));

		solo.sendKey(Solo.ENTER);
		solo.sleep(1000);

		assertEquals("Progress Bar was not updated correctly", scaleFactor, solo.getCurrentProgressBars().get(0)
				.getProgress());
	}

	public void testScaleEditTextUpdate() {

		int scaleFactor = 190;

		solo.clickOnEditText(0);

		solo.sleep(1000);
		assertEquals(originalSize, Double.valueOf(solo.getEditText(0).getText().toString()));
		solo.sleep(1000);

		solo.setProgressBar(0, scaleFactor);
		solo.sleep(1000);

		int editTextValue = Double.valueOf(solo.getEditText(0).getText().toString()).intValue();
		assertEquals("EditText was not correctly updated", scaleFactor, editTextValue);
	}

	public void testAbortSeekBar() {

		int scaleFactor = 135;

		solo.clickOnEditText(0);

		solo.sleep(1000);
		assertEquals(originalSize, Double.valueOf(solo.getEditText(0).getText().toString()));
		solo.sleep(1000);

		solo.setProgressBar(0, scaleFactor);

		solo.goBack();
		solo.sleep(1000);

		double actualScale = (Double) UiTestUtils.getPrivateField("size", setSizeBrick);
		assertEquals("Wrong text in private field", originalSize, actualScale);
		assertEquals("Text updated, but should be unchanged", originalSize,
				Double.valueOf(solo.getEditText(0).getText().toString()).doubleValue());
	}

	public void testAbortEditText() {

		int scaleFactor = 135;

		solo.clickOnEditText(0);

		solo.sleep(1000);
		assertEquals(originalSize, Double.valueOf(solo.getEditText(0).getText().toString()));
		solo.sleep(1000);

		solo.clearEditText(solo.getEditText(0));
		solo.enterText(solo.getEditText(0), Double.toString(scaleFactor));

		solo.sendKey(Solo.ENTER);

		solo.goBack();
		solo.sleep(1000);

		double actualScale = (Double) UiTestUtils.getPrivateField("size", setSizeBrick);
		assertEquals("Wrong text in private field", originalSize, actualScale);
		assertEquals("Text updated, but should be unchanged", originalSize,
				Double.valueOf(solo.getEditText(0).getText().toString()).doubleValue());
	}

	public void createTestProject() {
		Project project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript("script", sprite);
		script.addBrick(new HideBrick(sprite));
		setSizeBrick = new SetSizeToBrick(sprite, originalSize);
		script.addBrick(setSizeBrick);
		PlaySoundBrick soundBrick = new PlaySoundBrick(sprite);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName("sound.mp3");
		soundInfo.setTitle("sound.mp3");
		soundBrick.setSoundInfo(soundInfo);
		script.addBrick(soundBrick);

		script.addBrick(new SetSizeToBrick(sprite, 80));

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
