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
import at.tugraz.ist.catroid.content.bricks.SetPhysicObjectTypeBrick;
import at.tugraz.ist.catroid.physics.PhysicObject;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;
import at.tugraz.ist.catroid.ui.fragment.ScriptFragment;

import com.jayway.android.robotium.solo.Solo;

public class SetPhysicObjectTypeBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private Project project;
	private SetPhysicObjectTypeBrick setPhysicObjectTypeBrick;

	public SetPhysicObjectTypeBrickTest() {
		super(ScriptTabActivity.class);
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
	public void testPhysicObjectTypeBrick() {
		ScriptTabActivity activity = (ScriptTabActivity) solo.getCurrentActivity();
		ScriptFragment fragment = (ScriptFragment) activity.getTabFragment(ScriptTabActivity.INDEX_TAB_SCRIPTS);
		BrickAdapter adapter = fragment.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2 + 1, solo.getCurrentListViews().get(0).getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		String textSetPhysicObjectType = solo.getString(R.string.brick_set_physic_object_type);
		assertNotNull("TextView does not exist.", solo.getText(textSetPhysicObjectType));

		PhysicObject.Type type = PhysicObject.Type.DYNAMIC;

		solo.pressSpinnerItem(0, 0);
		solo.sleep(200);
		String[] directionStringArray = getActivity().getResources().getStringArray(R.array.physical_object_type);
		assertEquals("Wrong selection", directionStringArray[0], solo.getCurrentSpinners().get(0).getSelectedItem());

		solo.pressSpinnerItem(0, 1);
		solo.sleep(200);
		assertEquals("Wrong selection", directionStringArray[1], solo.getCurrentSpinners().get(0).getSelectedItem());

		solo.pressSpinnerItem(0, 2);
		solo.sleep(200);
		assertEquals("Wrong selection", directionStringArray[2], solo.getCurrentSpinners().get(0).getSelectedItem());

	}

	private void createProject() {
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		PhysicObject.Type type = PhysicObject.Type.DYNAMIC;
		setPhysicObjectTypeBrick = new SetPhysicObjectTypeBrick(null, sprite, type);
		script.addBrick(setPhysicObjectTypeBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
