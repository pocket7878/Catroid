package at.tugraz.ist.catroid.uitest.ui.dialog;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.TurnLeftBrick;
import at.tugraz.ist.catroid.content.bricks.TurnRightBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class RotationDialogTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private TurnLeftBrick turnLeftBrick;
	private TurnRightBrick turnRightBrick;
	private double originalAngle = 110;

	public RotationDialogTest() {
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

	public void testClickOnVariousPositions() {
		assertTrue("Not implemented yet", false);
	}

	public void testRotateLeftEditText() {

		double rotationAngle = 175;

		solo.clickOnEditText(0);

		solo.sleep(1000);
		assertEquals(originalAngle, Double.valueOf(solo.getEditText(0).getText().toString()));
		solo.sleep(1000);

		solo.clearEditText(solo.getEditText(0));
		solo.enterText(solo.getEditText(0), Double.toString(rotationAngle));

		solo.clickOnButton("OK");
		solo.sleep(1000);

		double actualAngle = (Double) UiTestUtils.getPrivateField("degrees", turnLeftBrick);
		assertEquals("Wrong text in private field", rotationAngle, actualAngle);
		assertEquals("Text not updated", rotationAngle, Double.parseDouble(solo.getEditText(0).getText().toString()));
	}

	public void testRotateRightEditText() {

		double rotationAngle = 210;

		solo.clickOnEditText(1);

		solo.sleep(1000);
		assertEquals(originalAngle, Double.valueOf(solo.getEditText(0).getText().toString()));
		solo.sleep(1000);

		solo.clearEditText(solo.getEditText(0));
		solo.enterText(solo.getEditText(0), Double.toString(rotationAngle));

		solo.clickOnButton("OK");
		solo.sleep(1000);

		double actualAngle = (Double) UiTestUtils.getPrivateField("degrees", turnRightBrick);
		assertEquals("Wrong text in private field", rotationAngle, actualAngle);
		assertEquals("Text not updated", rotationAngle, Double.parseDouble(solo.getEditText(1).getText().toString()));
	}

	public void testAbortLeftEditText() {

		int rotationAngle = 135;

		solo.clickOnEditText(0);

		solo.sleep(1000);
		assertEquals(originalAngle, Double.valueOf(solo.getEditText(0).getText().toString()));
		solo.sleep(1000);

		solo.clearEditText(solo.getEditText(0));
		solo.enterText(solo.getEditText(0), Double.toString(rotationAngle));

		solo.sendKey(Solo.ENTER);

		solo.goBack();
		solo.sleep(1000);

		double actualAngle = (Double) UiTestUtils.getPrivateField("degrees", turnLeftBrick);
		assertEquals("Wrong text in private field", originalAngle, actualAngle);
		assertEquals("Text updated, but should be unchanged", originalAngle,
				Double.valueOf(solo.getEditText(0).getText().toString()).doubleValue());
	}

	public void createTestProject() {
		Project project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		turnLeftBrick = new TurnLeftBrick(sprite, originalAngle);
		script.addBrick(turnLeftBrick);
		turnRightBrick = new TurnRightBrick(sprite, originalAngle);
		script.addBrick(turnRightBrick);

		script.addBrick(new SetSizeToBrick(sprite, 80));

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
