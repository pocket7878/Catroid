package at.tugraz.ist.catroid.livewallpaper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class UploadProject {

	private final static String TAG = "UploadProject";

	public static final String FILE_UPLOAD_TAG = "upload";
	public static final String PROJECT_NAME_TAG = "projectTitle";
	public static final String PROJECT_DESCRIPTION_TAG = "projectDescription";
	public static final String USER_LANGUAGE = "userLanguage";
	public static final String USER_EMAIL = "userEmail";
	public static final String PROJECT_CHECKSUM_TAG = "fileChecksum";

	private static final int SERVER_RESPONSE_TOKEN_OK = 200;
	private static final int SERVER_RESPONSE_REGISTER_OK = 201;

	public static final String BASE_URL = "http://192.168.1.100/";

	private static final String FILE_UPLOAD_URL = BASE_URL;
	private static final String CHECK_TOKEN_URL = BASE_URL;

	public static final String BASE_URL_TEST = "http://192.168.1.100/";
	public static final String TEST_FILE_UPLOAD_URL = BASE_URL_TEST + "uploads/";
	private static final String TEST_CHECK_TOKEN_URL = BASE_URL_TEST + "uploads/";

	//	public static final String TEST_FILE_UPLOAD_URL = BASE_URL_TEST + "api/upload/upload.json";

	private static UploadProject instance;
	public static boolean useTestUrl = false;
	private String resultString;
	private ConnectionWrapper connection;
	private String emailForUiTests;

	protected UploadProject() {
		// TODO Auto-generated constructor stub
		connection = new ConnectionWrapper();
	}

	public static UploadProject getInstance() {
		if (instance == null) {
			instance = new UploadProject();
		}
		return instance;
	}

	public void setConnectionToUse(ConnectionWrapper connection) {
		this.connection = connection;
	}

	public String uploadProject(String projectName, String projectDescription, String zipFileString, String userEmail,
			String language, String token) throws WebconnectionException {

		if (emailForUiTests != null) {
			userEmail = emailForUiTests;
		}

		try {
			String md5Checksum = Utils.md5Checksum(new File(zipFileString));

			HashMap<String, String> postValues = new HashMap<String, String>();
			postValues.put(PROJECT_NAME_TAG, projectName);
			postValues.put(PROJECT_DESCRIPTION_TAG, projectDescription);
			postValues.put(USER_EMAIL, "simgesezgin88@gmail.com");
			postValues.put(PROJECT_CHECKSUM_TAG, md5Checksum.toLowerCase());
			postValues.put(Constants.PROJECT_TOKEN, token);

			//			String serverUrl = useTestUrl ? BASE_URL_TEST : FILE_UPLOAD_URL;
			String serverUrl = BASE_URL;

			Log.v(TAG, "url to upload: " + serverUrl);
			resultString = connection.doHttpPostFileUpload(serverUrl, postValues, FILE_UPLOAD_TAG, null);
			Log.v(TAG, "result string:" + resultString);

			JSONObject jsonObject = null;
			int statusCode = 0;

			Log.v(TAG, "result string: " + resultString);

			jsonObject = new JSONObject(resultString);
			statusCode = jsonObject.getInt("statusCode");
			String serverAnswer = jsonObject.getString("answer");

			if (statusCode == 200) {
				return serverAnswer;
			} else {
				throw new WebconnectionException(statusCode, serverAnswer);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_JSON);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK);
		}
	}

	/**
	 * @param token
	 * @return
	 * @throws WebconnectionException
	 */
	public boolean checkToken(String token) throws WebconnectionException {
		// TODO Auto-generated method stub	
		try {
			HashMap<String, String> postValues = new HashMap<String, String>();
			postValues.put(Constants.TOKEN, token);

			//			String serverUrl = useTestUrl ? TEST_CHECK_TOKEN_URL : CHECK_TOKEN_URL;
			String serverUrl = BASE_URL;

			Log.v(TAG, "url to upload: " + serverUrl);
			resultString = connection.doHttpPost(serverUrl, postValues);

			JSONObject jsonObject = null;
			int statusCode = 0;

			Log.v(TAG, "result string: " + resultString);

			jsonObject = new JSONObject(resultString);
			statusCode = jsonObject.getInt("statusCode");
			String serverAnswer = jsonObject.optString("answer");

			if (statusCode == SERVER_RESPONSE_TOKEN_OK) {
				return true;
			} else {
				throw new WebconnectionException(statusCode, serverAnswer);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_JSON);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebconnectionException(WebconnectionException.ERROR_NETWORK);
		}

	}
}