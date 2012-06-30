package at.tugraz.ist.catroid.livewallpaper;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class UploadProject {

	private final static String TAG = "UploadProject";

	private static final String FILE_UPLOAD_TAG = "upload";
	private static final String PROJECT_NAME_TAG = "projectTitle";
	private static final String PROJECT_DESCRIPTION_TAG = "projectDescription";
	private static final String USER_LANGUAGE = "userLanguage";

	public static final String BASE_URL = "http://192.168.1.100/";

	private static final String FILE_UPLOAD_URL = BASE_URL + "api/upload/upload.json";

	public static final String BASE_URL_TEST = "http://catroidtest.ist.tugraz.at/";

	public static final String TEST_FILE_UPLOAD_URL = BASE_URL_TEST + "api/upload/upload.json";

	private static UploadProject instance;
	public static boolean useTestUrl = false;
	private String resultString;
	private ConnectionWrapper connection;

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

	public String uploadProject(String projectName, String projectDescription, String language)
			throws WebconnectionException {

		try {

			HashMap<String, String> postValues = new HashMap<String, String>();
			postValues.put(PROJECT_NAME_TAG, projectName);
			postValues.put(PROJECT_DESCRIPTION_TAG, projectDescription);

			if (language != null) {
				postValues.put(USER_LANGUAGE, language);
			}
			String serverUrl = useTestUrl ? TEST_FILE_UPLOAD_URL : FILE_UPLOAD_URL;

			Log.v(TAG, "url to upload: " + serverUrl);
			resultString = connection.doHttpPostFileUpload(serverUrl, postValues, FILE_UPLOAD_TAG, null);

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

}
