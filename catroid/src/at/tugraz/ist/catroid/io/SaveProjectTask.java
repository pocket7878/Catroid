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
package at.tugraz.ist.catroid.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import android.os.AsyncTask;
import android.util.Log;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.utils.Utils;

public class SaveProjectTask extends AsyncTask<Runnable, Void, Boolean> {

	public interface SaveProjectTaskCallback {
		public void onProjectSaved(boolean success);
	}

	private class SaveProjectRunnable implements Runnable {
		private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n";

		private final Project mProject;
		private boolean mResult;

		public SaveProjectRunnable(Project project) {
			mProject = project;
			mResult = false;
		}

		@Override
		public void run() {
			synchronized (mProject) {
				if (mProject == null || isCancelled()) {
					return;
				}
				{
					File catroidRoot = new File(Constants.DEFAULT_ROOT);
					if (!catroidRoot.exists()) {
						catroidRoot.mkdirs();
					}
				}
				try {
					String projectFile = StorageHandler.xstream.toXML(mProject);
					String projectDirectoryName = Utils.buildProjectPath(mProject.getName());
					File projectDirectory = new File(projectDirectoryName);

					if (!isCancelled()
							&& !(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory
									.canWrite())) {
						projectDirectory.mkdir();

						File imageDirectory = new File(Utils.buildPath(projectDirectoryName, Constants.IMAGE_DIRECTORY));
						imageDirectory.mkdir();

						File noMediaFile = new File(Utils.buildPath(projectDirectoryName, Constants.IMAGE_DIRECTORY,
								Constants.NO_MEDIA_FILE));
						noMediaFile.createNewFile();

						File soundDirectory = new File(projectDirectoryName + "/" + Constants.SOUND_DIRECTORY);
						soundDirectory.mkdir();

						noMediaFile = new File(Utils.buildPath(projectDirectoryName, Constants.SOUND_DIRECTORY,
								Constants.NO_MEDIA_FILE));
						noMediaFile.createNewFile();
					}
					if (!isCancelled()) {
						Writer writer = new BufferedWriter(new FileWriter(Utils.buildPath(projectDirectoryName,
								Constants.PROJECTCODE_NAME)), Constants.BUFFER_8K);
						writer.write(XML_HEADER.concat(projectFile));
						writer.flush();
						writer.close();
						mResult = true;
					}
				} catch (IOException e) {
					Log.e("CATROID", "Could not save project.", e);
					mResult = false;
				}
			}
		}
	}

	private boolean mSaveProjectCalled;
	private final SaveProjectTaskCallback mCallback;
	private static SaveProjectTask mInstance;
	// HACK: when under test, this variable should be true
	public static boolean mForceSynchronousSave;

	public SaveProjectTask(SaveProjectTaskCallback callback) {
		if (mInstance != null && !mInstance.isCancelled()) {
			mInstance.cancel(false);
		}
		mInstance = this;
		mCallback = callback;
	}

	public void saveProject(Project project) {
		mSaveProjectCalled = true;
		if (mForceSynchronousSave) {
			executeSynchronously(project);
		} else if (!mInstance.isCancelled()) {
			mInstance.execute(new SaveProjectRunnable(project), null);
		}
	}

	private void executeSynchronously(Project project) {
		SaveProjectRunnable runnable = new SaveProjectRunnable(project);
		runnable.run();
		mInstance = null;
		if (mCallback != null) {
			mCallback.onProjectSaved(runnable.mResult);
		}
	}

	@Override
	protected void onPreExecute() {
		if (!mSaveProjectCalled) {
			throw new RuntimeException("SaveProjectTask must be called through saveProject(Project).");
		}
	}

	@Override
	protected Boolean doInBackground(Runnable... params) {
		SaveProjectRunnable runnable = (SaveProjectRunnable) params[0];
		runnable.run();
		return runnable.mResult;
	}

	@Override
	protected void onCancelled() {
		Log.w("CATROID", "Cancelled save project task.");
	}

	@Override
	protected void onPostExecute(Boolean result) {
		mInstance = null;
		if (mCallback != null) {
			mCallback.onProjectSaved(result);
		}
	}
}
