package at.tugraz.ist.catroid.uitest.ui.dialog;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class TranslationDialogTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private PlaceAtBrick placeAtBrick;

	private int originalX = 42;
	private int originalY = 42;

	public TranslationDialogTest() {
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

	public void testClickOnScreenPosition() {//TODO alot!
		int xpos = 250;
		int ypos = 200;

		solo.clickOnEditText(1);
		solo.sleep(300);

		solo.clickOnScreen(xpos, ypos);
		solo.sleep(2000);

		//test ob edit text passen
		assertEquals(xpos, Integer.valueOf(solo.getEditText(0).getText().toString()).intValue());
		assertEquals(ypos, Integer.valueOf(solo.getEditText(1).getText().toString()).intValue());

		solo.clickOnButton("OK");

		int xActual = (Integer) UiTestUtils.getPrivateField("xPosition", placeAtBrick);
		int yActual = (Integer) UiTestUtils.getPrivateField("yPosition", placeAtBrick);
		assertEquals("Text not updated, but should be changed", xpos,
				Integer.valueOf(solo.getEditText(0).getText().toString()).intValue());
		assertEquals("Text not updated, but should be changed", ypos,
				Integer.valueOf(solo.getEditText(1).getText().toString()).intValue());
		assertEquals("x-Position in private field was not changed", xpos, xActual);
		assertEquals("y-Position in private field was not changed", ypos, yActual);
	}

	public void testTranslationEditTexts() {

		int xpos = 135;
		int ypos = 256;

		solo.clickOnEditText(0);

		solo.sleep(1000);
		assertEquals("Wrong starting value in EditTExt", originalX,
				Integer.valueOf(solo.getEditText(0).getText().toString()).intValue());
		assertEquals("Wrong starting value in EditTExt", originalY,
				Integer.valueOf(solo.getEditText(1).getText().toString()).intValue());
		solo.sleep(1000);

		solo.clearEditText(solo.getEditText(0));
		solo.enterText(solo.getEditText(0), Integer.toString(xpos));
		solo.sendKey(Solo.ENTER);

		solo.clearEditText(solo.getEditText(1));
		solo.enterText(solo.getEditText(1), Integer.toString(ypos));
		solo.sendKey(Solo.ENTER);

		solo.clickOnButton("OK");
		solo.sleep(1000);

		int xActual = (Integer) UiTestUtils.getPrivateField("xPosition", placeAtBrick);
		int yActual = (Integer) UiTestUtils.getPrivateField("yPosition", placeAtBrick);
		assertEquals("Text not updated, but should be changed", xpos,
				Integer.valueOf(solo.getEditText(0).getText().toString()).intValue());
		assertEquals("Text not updated, but should be changed", ypos,
				Integer.valueOf(solo.getEditText(1).getText().toString()).intValue());
		assertEquals("x-Position in private field was not changed", xpos, xActual);
		assertEquals("y-Position in private field was not changed", ypos, yActual);
	}

	public void testAbortChanges() {
		int xpos = 135;
		int ypos = 256;

		solo.clickOnEditText(0);

		solo.sleep(1000);
		assertEquals("Wrong starting value in EditTExt", originalX,
				Integer.valueOf(solo.getEditText(0).getText().toString()).intValue());
		assertEquals("Wrong starting value in EditTExt", originalY,
				Integer.valueOf(solo.getEditText(1).getText().toString()).intValue());
		solo.sleep(1000);

		solo.clearEditText(solo.getEditText(0));
		solo.enterText(solo.getEditText(0), Integer.toString(xpos));
		solo.sendKey(Solo.ENTER);

		solo.clearEditText(solo.getEditText(1));
		solo.enterText(solo.getEditText(1), Integer.toString(ypos));
		solo.sendKey(Solo.ENTER);

		solo.goBack();
		solo.sleep(1000);

		int xActual = (Integer) UiTestUtils.getPrivateField("xPosition", placeAtBrick);
		int yActual = (Integer) UiTestUtils.getPrivateField("yPosition", placeAtBrick);
		assertEquals("x-Position in private field was changed", originalX, xActual);
		assertEquals("y-Position in private field was changed", originalY, yActual);
		assertEquals("Text updated, but should be unchanged", originalX,
				Integer.valueOf(solo.getEditText(0).getText().toString()).intValue());
		assertEquals("Text updated, but should be unchanged", originalY,
				Integer.valueOf(solo.getEditText(1).getText().toString()).intValue());
	}

	public void testAbortChangesClickOnScreen() {
		int xpos = 135;
		int ypos = 256;

		solo.clickOnEditText(0);

		solo.sleep(1000);
		assertEquals("Wrong starting value in EditTExt", originalX,
				Integer.valueOf(solo.getEditText(0).getText().toString()).intValue());
		assertEquals("Wrong starting value in EditTExt", originalY,
				Integer.valueOf(solo.getEditText(1).getText().toString()).intValue());
		solo.sleep(1000);

		solo.clickOnScreen(xpos, ypos);
		solo.sleep(2000);

		solo.goBack();
		solo.sleep(1000);

		int xActual = (Integer) UiTestUtils.getPrivateField("xPosition", placeAtBrick);
		int yActual = (Integer) UiTestUtils.getPrivateField("yPosition", placeAtBrick);
		assertEquals("x-Position in private field was changed", originalX, xActual);
		assertEquals("y-Position in private field was changed", originalY, yActual);
		assertEquals("Text updated, but should be unchanged", originalX,
				Integer.valueOf(solo.getEditText(0).getText().toString()).intValue());
		assertEquals("Text updated, but should be unchanged", originalY,
				Integer.valueOf(solo.getEditText(1).getText().toString()).intValue());
	}

	public void createTestProject() {
		Project project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript("script", sprite);
		script.addBrick(new HideBrick(sprite));
		placeAtBrick = new PlaceAtBrick(sprite, originalX, originalY);
		script.addBrick(placeAtBrick);
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
