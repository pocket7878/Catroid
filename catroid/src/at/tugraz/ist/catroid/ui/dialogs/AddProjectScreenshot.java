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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.stage.StageActivity;

public class AddProjectScreenshot extends DialogFragment {
	public static final String DIALOG_FRAGMENT_TAG = "dialog_add_project_screenshot";
	private Button screenshotFromStageButton;
	private Button cancelButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_add_project_screenshot, container);

		cancelButton = (Button) rootView.findViewById(R.id.btn_cancel_add_project_screenshot);
		screenshotFromStageButton = (Button) rootView.findViewById(R.id.btn_ok_add_project_screenshot);

		initControls();

		getDialog().setTitle(R.string.add_project_screenshot_titel);
		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		return rootView;
	}

	private void initControls() {
		screenshotFromStageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ProjectManager.getInstance().getCurrentProject() != null) {
					Intent takeScreenshotIntent = new Intent(getActivity(), StageActivity.class);
					takeScreenshotIntent.putExtra("takeScreenshotForUpload", true);
					getActivity().startActivity(takeScreenshotIntent);
				}
				dismiss();
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}
