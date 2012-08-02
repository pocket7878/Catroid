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
package at.tugraz.ist.catroid.stage;

import java.io.File;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.transfers.CheckTokenTask;
import at.tugraz.ist.catroid.ui.dialogs.LoginRegisterDialog;
import at.tugraz.ist.catroid.ui.dialogs.StageDialog;
import at.tugraz.ist.catroid.ui.dialogs.UploadVideoDialog;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.badlogic.gdx.backends.android.AndroidApplication;

public class StageActivity extends AndroidApplication {
	public static final int DIALOG_UPLOAD_PROJECT = 2;
	private static final int DIALOG_LOGIN_REGISTER = 4;

	private boolean stagePlaying = true;
	public static StageListener stageListener;
	private boolean resizePossible;
	private StageDialog stageDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		stageListener = new StageListener();
		stageDialog = new StageDialog(this, stageListener, R.style.stage_dialog);
		this.calculateScreenSizes();
		initialize(stageListener, true);

	}

	@Override
	public void onBackPressed() {
		pauseOrContinue();
		stageDialog.show();
	}

	@Override
	protected void onDestroy() {
		if (stagePlaying) {
			this.manageLoadAndFinish();
		}
		super.onDestroy();
	}

	public void manageLoadAndFinish() {
		stageListener.pause();
		stageListener.finish();

		PreStageActivity.shutdownResources();

		ProjectManager projectManager = ProjectManager.getInstance();
		int currentSpritePos = projectManager.getCurrentSpritePosition();
		int currentScriptPos = projectManager.getCurrentScriptPosition();
		projectManager.loadProject(projectManager.getCurrentProject().getName(), this, false);
		projectManager.setCurrentSpriteWithPosition(currentSpritePos);
		projectManager.setCurrentScriptWithPosition(currentScriptPos);
		stagePlaying = false;

		finish();
	}

	public void toggleAxes() {
		if (stageListener.axesOn) {
			stageListener.axesOn = false;
		} else {
			stageListener.axesOn = true;
		}
	}

	public void pauseOrContinue() {
		if (stagePlaying) {
			stageListener.menuPause();
			stagePlaying = false;
		} else {
			stageListener.menuResume();
			stagePlaying = true;
		}
	}

	private void calculateScreenSizes() {
		ifLandscapeSwitchWidthAndHeight();
		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().virtualScreenWidth;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().virtualScreenHeight;
		if (virtualScreenWidth == Values.SCREEN_WIDTH && virtualScreenHeight == Values.SCREEN_HEIGHT) {
			resizePossible = false;
			return;
		}
		resizePossible = true;
		stageListener.maximizeViewPortWidth = Values.SCREEN_WIDTH + 1;
		do {
			stageListener.maximizeViewPortWidth--;
			stageListener.maximizeViewPortHeight = (int) (((float) stageListener.maximizeViewPortWidth / (float) virtualScreenWidth) * virtualScreenHeight);
		} while (stageListener.maximizeViewPortHeight > Values.SCREEN_HEIGHT);

		stageListener.maximizeViewPortX = (Values.SCREEN_WIDTH - stageListener.maximizeViewPortWidth) / 2;
		stageListener.maximizeViewPortY = (Values.SCREEN_HEIGHT - stageListener.maximizeViewPortHeight) / 2;
	}

	private void ifLandscapeSwitchWidthAndHeight() {
		if (Values.SCREEN_WIDTH > Values.SCREEN_HEIGHT) {
			int tmp = Values.SCREEN_HEIGHT;
			Values.SCREEN_HEIGHT = Values.SCREEN_WIDTH;
			Values.SCREEN_WIDTH = tmp;
		}
	}

	public void makeToast(String text) {
		Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}

	public boolean getResizePossible() {
		return resizePossible;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.getCurrentProject() != null
				&& StorageHandler.getInstance().projectExistsIgnoreCase(projectManager.getCurrentProject().getName())) {
			projectManager.saveProject();
		}

		switch (id) {
			case DIALOG_UPLOAD_PROJECT:
				dialog = new UploadVideoDialog(this);
				break;
			case DIALOG_LOGIN_REGISTER:
				dialog = new LoginRegisterDialog(this);
				break;
			default:
				dialog = null;
				break;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
			case DIALOG_UPLOAD_PROJECT:
				Project currentProject = ProjectManager.getInstance().getCurrentProject();
				String currentProjectName = currentProject.getName();
				EditText videoDescriptionField = (EditText) dialog.findViewById(R.id.video_description_upload);
				EditText videotUploadName = (EditText) dialog.findViewById(R.id.video_name_upload);
				TextView sizeOfProject = (TextView) dialog.findViewById(R.id.dialog_upload_size_of_project);
				sizeOfProject.setText(UtilFile.getSizeAsString(new File(Constants.DEFAULT_ROOT + "/"
						+ currentProjectName)));

				videotUploadName.setText(ProjectManager.getInstance().getCurrentProject().getName());
				videoDescriptionField.setText("");
				videotUploadName.requestFocus();
				videotUploadName.selectAll();
				break;
			case DIALOG_LOGIN_REGISTER:
				EditText usernameEditText = (EditText) dialog.findViewById(R.id.username);
				EditText passwordEditText = (EditText) dialog.findViewById(R.id.password);
				usernameEditText.setText("");
				passwordEditText.setText("");
				break;
		}
	}

	public void uploadRecordedStage() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String token = preferences.getString(Constants.TOKEN, null);

		if (token == null || token.length() == 0 || token.equals("0")) {
			showDialog(DIALOG_LOGIN_REGISTER);
		} else {
			new CheckTokenTask(this, token).execute();
		}
	}

}
