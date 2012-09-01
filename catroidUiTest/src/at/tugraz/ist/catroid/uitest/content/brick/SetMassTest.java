package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.EditText;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetMassBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

/*
 * TODO: Test doesn' work correctly.
 */
public class SetMassTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private Project project;
	private SetMassBrick setMassBrick;

	public SetMassTest() {
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
	public void testSetMassByBrick() {
		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();

		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0),
				getActivity().getAdapter().getChild(groupCount - 1, 0));
		String massByString = solo.getString(R.string.brick_set_mass);
		solo.waitForText(massByString);
		assertNotNull("TextView does not exist", solo.getText(massByString));

		float mass = 1.234f;

		solo.waitForView(EditText.class);
		UiTestUtils.clickEnterClose(solo, 0, Float.toString(mass));
		float actualMass = (Float) UiTestUtils.getPrivateField("mass", setMassBrick);
		assertEquals("Text not updated", Float.toString(mass), solo.getEditText(0).getText().toString());
		assertEquals("Value in Brick is not updated", mass, actualMass);
	}

	@Smoke
	public void testResizeInputField() {
		//assertTrue("", false);
		UiTestUtils.testDoubleEditText(solo, 0, 12345.0, 50, false);
		//		UiTestUtils.testDoubleEditText(solo, 0, 1.0, 50, true);
		//		UiTestUtils.testDoubleEditText(solo, 0, 1234.0, 50, true);
		//		UiTestUtils.testDoubleEditText(solo, 0, -1, 50, true);
	}

	private void createProject() {
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		setMassBrick = new SetMassBrick(null, sprite, 0.0f);
		script.addBrick(setMassBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
