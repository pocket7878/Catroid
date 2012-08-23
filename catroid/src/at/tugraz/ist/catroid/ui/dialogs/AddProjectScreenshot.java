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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.stage.StageActivity;

public class AddProjectScreenshot extends Dialog {
	private Context context;
	Dialog addProjectScreenshot;

	public AddProjectScreenshot(Context context) {
		super(context);
		this.context = context;
	}

	public Dialog createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getString(R.string.add_project_screensht_titel));

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_add_project_screenshot, null);

		Button fromStageButton = (Button) view.findViewById(R.id.btn_ok_add_project_screenshot);
		fromStageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ProjectManager.getInstance().getCurrentProject() != null) {
					Intent takeScreenshotIntent = new Intent(context, StageActivity.class);
					takeScreenshotIntent.putExtra("takeScreenshotForUpload", true);
					context.startActivity(takeScreenshotIntent);
				}
				addProjectScreenshot.dismiss();
			}
		});

		Button cancelButton = (Button) view.findViewById(R.id.btn_cancel_add_project_screenshot);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addProjectScreenshot.dismiss();
			}
		});

		builder.setView(view);

		addProjectScreenshot = builder.create();
		addProjectScreenshot.setCanceledOnTouchOutside(true);

		return addProjectScreenshot;
	}
}
