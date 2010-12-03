package at.tugraz.ist.catroid.test.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.constructionSite.tasks.ProjectUploadTask;
import at.tugraz.ist.catroid.download.tasks.ProjectDownloadTask;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class UpAndDownloadTest extends AndroidTestCase {

	private MockConnection mMockConnection;
	private File mProjectZipOnMockServer;
	
	public UpAndDownloadTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		mProjectZipOnMockServer = new File(ConstructionSiteActivity.TMP_PATH+"/projectSave.zip");
		mMockConnection = new MockConnection();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInit() throws Throwable {
	}

	public void testUpAndDownload() throws Throwable {
		String testProjectName = "UpAndDownloadTest"+System.currentTimeMillis();
		String pathToDefaultProject = ConstructionSiteActivity.DEFAULT_ROOT+"/defaultSaveFile";
		
		ProjectUploadTask uploadTask = new ProjectUploadTask(null, testProjectName,
				pathToDefaultProject, ConstructionSiteActivity.TMP_PATH+"/tmp.zip") {
			@Override
			protected ConnectionWrapper createConnection() {
				return mMockConnection;
			}
		};
		ProjectDownloadTask downloadTask = new ProjectDownloadTask(null, 
				"", testProjectName, ConstructionSiteActivity.TMP_PATH+"/down.zip") {
			@Override
			protected ConnectionWrapper createConnection() {
				return mMockConnection;
			}
		};
		
		assertTrue("The default Project does not exist.", new File(pathToDefaultProject).exists());
		uploadTask.execute();		
		Thread.sleep(1000);
		
		assertTrue("uploaded file does not exist", mProjectZipOnMockServer.exists());
		
		downloadTask.execute();
		Thread.sleep(1000);
		
		File downloadProjectRoot = new File(ConstructionSiteActivity.DEFAULT_ROOT+"/"+testProjectName);
		assertTrue("project does not exist after download", downloadProjectRoot.exists());
		
		boolean spfFilePresent = false;
		String[] projectFiles = downloadProjectRoot.list();
		for (String fileName : projectFiles) {
			if(fileName.endsWith(ConstructionSiteActivity.DEFAULT_FILE_ENDING))
				spfFilePresent = true;
		}
		
		assertTrue("No project file available.", spfFilePresent);
		UtilFile.deleteDirectory(downloadProjectRoot);
	}

	private class MockConnection extends ConnectionWrapper {
		@Override
		public String doHttpPostFileUpload(String urlstring,
				HashMap<String, String> postValues, String filetag,
				String filePath) throws IOException, WebconnectionException {
			new File(filePath).renameTo(mProjectZipOnMockServer);
			return "";
		}
		@Override
		public void doHttpPostFileDownload(String urlstring,
				HashMap<String, String> postValues, String filePath)
				throws IOException {
			mProjectZipOnMockServer.renameTo(new File(filePath));
		}
	}
}