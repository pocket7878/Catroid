package at.tugraz.ist.catroid.uitest.ui.dialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.Utils;

import com.jayway.android.robotium.solo.Solo;

public class AddCostumeAndSoundDialogTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Solo solo;

	private ProjectManager projectManager = ProjectManager.getInstance();

	private ArrayList<CostumeData> costumeDataList;
	private String costumeName = "costumeNametest";
	private final int RESOURCE_IMAGE = at.tugraz.ist.catroid.uitest.R.drawable.catroid_sunglasses;

	public AddCostumeAndSoundDialogTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());

	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	public void testAddCostumeDialog() {

		solo.clickOnText(getActivity().getString(R.string.backgrounds));

		try {
			File imageFile = UiTestUtils.createTestMediaFile(
					Utils.buildPath(Constants.DEFAULT_ROOT, "catroid_sunglasses.png"), RESOURCE_IMAGE, getActivity());
			Bundle bundleForGallery = new Bundle();
			bundleForGallery.putString("filePath", imageFile.getAbsolutePath());
			Intent intent = new Intent(getInstrumentation().getContext(),
					at.tugraz.ist.catroid.uitest.mockups.MockGalleryActivity.class);
			intent.putExtras(bundleForGallery);

		} catch (IOException e) {
			e.printStackTrace();
			fail("Image was not created");
		}

		solo.clickOnButton(R.id.btn_action_add_button);
		solo.clickOnText("Galery");
		solo.sleep(20000);

	}
}
