package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetVelocityBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.badlogic.gdx.math.Vector2;
import com.jayway.android.robotium.solo.Solo;

public class SetVelocityBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private Project project;
	private SetVelocityBrick setVelocityBrick;

	public SetVelocityBrickTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
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
		super.tearDown();
	}

	@Smoke
	public void testSetVelocityByBrick() {
		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();

		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0),
				getActivity().getAdapter().getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.brick_set_velocity)));

		Vector2 velocity = new Vector2(1.2f, -3.4f);

		UiTestUtils.clickEnterClose(solo, 0, Float.toString(velocity.x));
		Vector2 actualVelocity = (Vector2) UiTestUtils.getPrivateField("velocity", setVelocityBrick);
		assertEquals("Text not updated", Float.toString(velocity.x), solo.getEditText(0).getText().toString());
		assertEquals("Value in Brick is not updated", velocity.x, actualVelocity.x);

		UiTestUtils.clickEnterClose(solo, 1, Float.toString(velocity.y));
		actualVelocity = (Vector2) UiTestUtils.getPrivateField("velocity", setVelocityBrick);
		assertEquals("Text not updated", Float.toString(velocity.y), solo.getEditText(1).getText().toString());
		assertEquals("Value in Brick is not updated", velocity.y, actualVelocity.y);
	}

	public void testResizeInputField() {
		for (int editTextIndex = 0; editTextIndex < 2; editTextIndex++) {
			UiTestUtils.testDoubleEditText(solo, editTextIndex, 12345.0, 50, false);
			UiTestUtils.testDoubleEditText(solo, editTextIndex, 1.0, 50, true);
			UiTestUtils.testDoubleEditText(solo, editTextIndex, 123.0, 50, true);
			UiTestUtils.testDoubleEditText(solo, editTextIndex, -1, 50, true);
		}
	}

	private void createProject() {
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		setVelocityBrick = new SetVelocityBrick(null, sprite, new Vector2());
		script.addBrick(setVelocityBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
