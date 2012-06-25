package at.tugraz.ist.catroid.test.livewallpaper;

import java.io.File;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.transfers.ProjectUploadTask;

public class UploadTest extends AndroidTestCase {

	private File uploadServerFile;
	private File destinationPath;

	public UploadTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		uploadServerFile = new File(Constants.TMP_PATH + "/projectSave" + Constants.CATROID_EXTENTION);
		destinationPath = new File("http://192.168.1.100/testFile");
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.clearProject("uploadtestProject");
		super.tearDown();
	}

	public void testUpload() throws Throwable {

		String testProjectName = "UploadTest" + System.currentTimeMillis();
		String pathToDefaultProject = Constants.DEFAULT_ROOT + "/uploadtestProject";
		new File(pathToDefaultProject).mkdirs();

		String projectFilename = Constants.PROJECTCODE_NAME;
		new File(pathToDefaultProject + "/" + projectFilename).createNewFile();

		String projectDescription = "this is just a testproject";

		assertTrue("The default Project does not exist.", new File(pathToDefaultProject).exists());

		new ProjectUploadTask(null, testProjectName, projectDescription, pathToDefaultProject, "0").execute();
		Thread.sleep(3000);

		assertTrue("testFile does not exist", destinationPath.exists());

		assertTrue("Uploaded file does not exist", uploadServerFile.exists());
	}
}
