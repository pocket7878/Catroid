/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.common;

import android.os.Environment;

public final class Consts {

	public static final String PROJECT_EXTENTION = ".xml";
	public static final String CATROID_EXTENTION = ".catroid";

	public static final String DEFAULT_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/catroid";
	public static final String TMP_PATH = DEFAULT_ROOT + "/tmp";
	public static final String IMAGE_DIRECTORY = "images";
	public static final String SOUND_DIRECTORY = "sounds";

	public static final String NO_MEDIA_FILE = ".nomedia";

	//Web:
	public static final String TOKEN = "token";

	public static final int SERVER_RESPONCE_TOKEN_OK = 200;
	public static final int SERVER_RESPONCE_REGISTER_OK = 201;

	//Stage:
	public static final String SCREENSHOT_FILE_NAME = "screenshot.png";
	public static final int STRETCH = 0;
	public static final int MAXIMIZE = 1;

	//Various:
	public static final int BUFFER_8K = 8 * 1024;
	public static final String PAINTROID_DOWNLOAD_LINK = "https://code.google.com/p/catroid/downloads/detail?name=Paintroid_0.6.4b.apk&can=2&q=";
	public static final String PASSWORD_FORGOTTEN_PATH = "catroid/passwordrecovery?username=";

	//Upload
	public static final String HIGH_POINT = "\"";
	public static final String SLASH = "/";
	public static final String NEWLINE = "\r\n";
	public static final String UPLOAD_PROGRESS_KEY = "uploadProgress";
	public static final String UPLOAD_PROJECT_NAME_KEY = "uploadName";
	public static final String UPLOAD_INTENT_DESCRIPTION = "description";
	public static final String UPLOAD_INTENT_TOKEN = "token";
	public static final String UPLOAD_INTENT_PROJECTPATH = "projectPath";
	public static final String UPLOAD_PROGREESS_KEY = "uploadProgress";
	public static final int UPLOAD_FINISHED_NUMBER = 1;
	public static final int UPLOAD_ERROR_NUMBER = 2;
	public static final int UPLOAD_PROGRESS_MAX = 100;
	public static final String UPLOAD_ZIP = "/upload";
	public static final int UPLOAD_NOTIFICATION_PROGRESS = 1;
	public static final int UPLOAD_NOTIFICATION_FAILED = 333;
	public static final int UPLOAD_NOTIFICATION_FINISHED = 222;
	public static final int UPLOAD_REFRESH_MS = 300;
	public static final String FTP_LOGIN_SUCCESS = "230";

	//UrlConnection
	public static final String URL_ACCEPT_KEY = "Accept";
	public static final String URL_ACCEPT_VALUE = "*/*";
	public static final String URL_CONTENT_TYPE_KEY = "Content-Type";
	public static final String URL_CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
	public static final String URL_CONNECTION_KEY = "Connection";
	public static final String URL_CONNECTION_VALUE = "Keep-Alive";
	public static final String URL_CACHE_KEY = "Cache-Control";
	public static final String URL_CACHE_VALUE = "no-cache";
	public static final String URL_LENGTH_KEY = "Content-Length";

	public static final String SERVER = "catroidwebtest.ist.tugraz.at";
	public static final String USERNAME = "ftp-uploader";
	public static final String PASSWORD = "cat.ftp.loader";
	public static final String PATH = "";
}
