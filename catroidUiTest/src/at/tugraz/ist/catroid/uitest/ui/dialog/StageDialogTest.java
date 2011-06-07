package at.tugraz.ist.catroid.uitest.ui.dialog;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageButton;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.Utils;

import com.jayway.android.robotium.solo.Solo;

public class StageDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private String testProject = Utils.PROJECTNAME1;
	private StorageHandler storageHandler;

	public StageDialogTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
		storageHandler = StorageHandler.getInstance();
	}

	@Override
	public void setUp() throws Exception {
		Utils.clearAllUtilTestProjects();

		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		Utils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void backButtonPressedTwiceTest() {

		List<ImageButton> btnList = solo.getCurrentImageButtons();
		for (int i = 0; i < btnList.size(); i++) {
			ImageButton btn = btnList.get(i);
			if (btn.getId() == R.id.btn_action_play) {
				solo.clickOnImageButton(i);
			}
		}
		solo.goBack();

		solo.goBack();

		assertTrue("Not in stage", solo.getCurrentActivity() instanceof StageActivity);
	}

	public void backToPreviousActivityTest() throws NameNotFoundException, IOException {
		createTestProject(testProject);
		solo.clickOnButton(getActivity().getString(R.string.projects_on_phone));
		solo.clickOnText(testProject);

		Activity previousActivity = getActivity();

		List<ImageButton> btnList = solo.getCurrentImageButtons();
		for (int i = 0; i < btnList.size(); i++) {
			ImageButton btn = btnList.get(i);
			if (btn.getId() == R.id.btn_action_play) {
				solo.clickOnImageButton(i);
			}
		}

		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.back_to_construction_site));
		solo.sleep(1000);
		assertEquals("Not equal Activities", previousActivity, getActivity());
	}

	public void pauseOnBackButtonTest() {
		double scale = 50.0;

		Project project = new Project(getActivity(), testProject);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript("script", sprite);
		WaitBrick waitBrick = new WaitBrick(sprite, 5000);
		ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(sprite, scale);

		script.getBrickList().add(waitBrick);
		script.getBrickList().add(scaleCostumeBrick);
		sprite.getScriptList().add(script);
		project.getSpriteList().add(sprite);

		storageHandler.saveProject(project);
		ProjectManager.getInstance().setProject(project);

		List<ImageButton> btnList = solo.getCurrentImageButtons();
		for (int i = 0; i < btnList.size(); i++) {
			ImageButton btn = btnList.get(i);
			if (btn.getId() == R.id.btn_action_play) {
				solo.clickOnImageButton(i);
			}
		}

		assertEquals(100.0, sprite.getScale());
		solo.goBack();
		solo.sleep(6000);
		solo.goBack();
		assertEquals(100.0, sprite.getScale());
		solo.sleep(4000);
		assertEquals(scale, sprite.getScale());
	}

	public void createTestProject(String projectName) throws IOException, NameNotFoundException {
		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("cat");
		Sprite secondSprite = new Sprite("dog");
		Sprite thirdSprite = new Sprite("horse");
		Sprite fourthSprite = new Sprite("pig");

		project.addSprite(firstSprite);
		project.addSprite(secondSprite);
		project.addSprite(thirdSprite);
		project.addSprite(fourthSprite);

		storageHandler.saveProject(project);
	}

}
