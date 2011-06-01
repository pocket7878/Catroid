/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import at.tugraz.ist.catroid.R;

/**
 * @author
 * 
 */
public class StageDialog extends Dialog {
	private final Context context;
	private Activity activity;
	public static final String backToConstruction = "BACK_TO_CONSTRUCTION";

	public StageDialog(Activity currentActivity) {
		super(currentActivity);
		this.context = currentActivity.getApplicationContext();
		this.activity = currentActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_stage);
		setTitle(R.string.stage_dialog_title);
		setCanceledOnTouchOutside(true);
		this.getWindow().setGravity(Gravity.LEFT);

		Button backToConstructionSiteButton = (Button) findViewById(R.id.back_to_construction_site_button);
		backToConstructionSiteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				backToConstruction();
			}
		});

		Button resume_current_project_button = (Button) findViewById(R.id.resume_current_project_button);
		resume_current_project_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cancel();
			}
		});

		Button restart_current_project_button = (Button) findViewById(R.id.restart_current_project_button);
		restart_current_project_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//go to construction site
			}
		});

		Button snapshot_button = (Button) findViewById(R.id.snapshot_button);
		snapshot_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//go to construction site
			}
		});
	}

	public void backToConstruction() {
	}
}
