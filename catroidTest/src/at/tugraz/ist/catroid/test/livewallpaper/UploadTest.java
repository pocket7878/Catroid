package at.tugraz.ist.catroid.test.livewallpaper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.livewallpaper.UploadProject;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.transfers.ProjectUploadTask;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class UploadTest extends AndroidTestCase {

	private File uploadServerFile;

	public UploadTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		uploadServerFile = new File(Constants.TMP_PATH + "/projectSave" + Constants.CATROID_EXTENTION);

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

		UploadProject.getInstance().setConnectionToUse(new MockConnection());

		assertTrue("The default Project does not exist.", new File(pathToDefaultProject).exists());
		new ProjectUploadTask(null, testProjectName, projectDescription, pathToDefaultProject, "0").execute();
		Thread.sleep(3000);

		assertTrue("Uploaded file does not exist", uploadServerFile.exists());

		UtilFile.deleteDirectory(new File(pathToDefaultProject));
	}

	private class MockConnection extends ConnectionWrapper {
		@Override
		public String doHttpPostFileUpload(String urlstring, HashMap<String, String> postValues, String filetag,
				String filePath) throws IOException, WebconnectionException {

			new File(filePath).renameTo(uploadServerFile);
			return "";
		}
	}
}
