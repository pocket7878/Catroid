package at.tugraz.ist.catroid.uitest.ui.dialog;

import java.io.File;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

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

		costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();

		int oldCostumeCount = costumeDataList.size();

		File imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);

		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile.getName());
		costumeData.setCostumeName(costumeName);
		costumeDataList.add(costumeData);
		projectManager.fileChecksumContainer.addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());

		int newCostumeCount = costumeDataList.size();
		assertEquals("The costume has not been added, but it should have been", oldCostumeCount + 1, newCostumeCount);

		assertTrue("The notification about the added coustume could not be found",
				solo.searchText(getActivity().getString(R.string.notification_costume_added)));

	}
}
