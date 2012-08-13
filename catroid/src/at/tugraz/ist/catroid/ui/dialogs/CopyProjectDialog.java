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
package at.tugraz.ist.catroid.ui.dialogs;

import java.io.File;
import java.io.IOException;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MyProjectsActivity;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

/**
 * @author Jakob
 * 
 */
public class CopyProjectDialog extends TextDialog {

	public CopyProjectDialog(MyProjectsActivity myProjectActivity) {
		super(myProjectActivity, myProjectActivity.getString(R.string.copy_project_dialog_title), null);
		initKeyListenerAndClickListener();
	}

	public void handleOkButton() {
		String newProjectName = (input.getText().toString()).trim();
		String oldProjectName = (((MyProjectsActivity) activity).projectToEdit.projectName);

		if (newProjectName != null && !newProjectName.equalsIgnoreCase("")) {

			if (StorageHandler.getInstance().projectExistsIgnoreCase(newProjectName)) {
				Utils.displayErrorMessage(activity, activity.getString(R.string.error_project_exists));
				return;
			} else {
				try {
					UtilFile.copyProject(newProjectName, oldProjectName);
				} catch (IOException e) {
					Utils.displayErrorMessage(activity, activity.getString(R.string.error_copy_project));
					UtilFile.deleteDirectory(new File(Utils.buildProjectPath(newProjectName)));
					Log.e("CATROID", "Error while copying project, destroy newly created directories.", e);
				}
			}
			((MyProjectsActivity) activity).initAdapter();
		} else {
			Utils.displayErrorMessage(activity, activity.getString(R.string.notification_invalid_text_entered));
			return;
		}
		activity.dismissDialog(MyProjectsActivity.DIALOG_COPY_PROJECT);
	}

	private void initKeyListenerAndClickListener() {
		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					String newProjectName = (input.getText().toString()).trim();
					if (StorageHandler.getInstance().projectExistsCheckCase(newProjectName)) {
						Utils.displayErrorMessage(activity, activity.getString(R.string.error_project_exists));
					} else if (newProjectName.equalsIgnoreCase("")) {
						Utils.displayErrorMessage(activity,
								activity.getString(R.string.notification_invalid_text_entered));
					} else {
						handleOkButton();
					}
					return true;
				}
				return false;
			}
		});

		buttonPositive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleOkButton();
			}
		});

		buttonNegative.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.dismissDialog(MyProjectsActivity.DIALOG_COPY_PROJECT);
			}
		});
	}
}
