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
import at.tugraz.ist.catroid.content.bricks.SetGravityBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.badlogic.gdx.math.Vector2;
import com.jayway.android.robotium.solo.Solo;

public class SetGravityTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private Project project;
	private SetGravityBrick setGravityBrick;

	public SetGravityTest() {
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
	public void testSetGravityByBrick() {
		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();

		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0),
				getActivity().getAdapter().getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.brick_set_gravity)));

		Vector2 gravity = new Vector2(1.2f, -3.4f);

		UiTestUtils.clickEnterClose(solo, 0, Float.toString(gravity.x));
		Vector2 actualGravity = (Vector2) UiTestUtils.getPrivateField("gravity", setGravityBrick);
		assertEquals("Text not updated", Float.toString(gravity.x), solo.getEditText(0).getText().toString());
		assertEquals("Value in Brick is not updated", gravity.x, actualGravity.x);

		UiTestUtils.clickEnterClose(solo, 1, Float.toString(gravity.y));
		actualGravity = (Vector2) UiTestUtils.getPrivateField("gravity", setGravityBrick);
		assertEquals("Text not updated", Float.toString(gravity.y), solo.getEditText(1).getText().toString());
		assertEquals("Value in Brick is not updated", gravity.y, actualGravity.y);
	}

	public void testResizeInputField() {
		for (int editTextIndex = 0; editTextIndex < 2; editTextIndex++) {
			UiTestUtils.testDoubleEditText(solo, editTextIndex, 123456.0, 50, false);
			UiTestUtils.testDoubleEditText(solo, editTextIndex, 1.0, 50, true);
			UiTestUtils.testDoubleEditText(solo, editTextIndex, 123.0, 50, true);
			UiTestUtils.testDoubleEditText(solo, editTextIndex, -1, 50, true);
		}
	}

	private void createProject() {
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		setGravityBrick = new SetGravityBrick(null, sprite, 0.0f, 0.0f);
		script.addBrick(setGravityBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
