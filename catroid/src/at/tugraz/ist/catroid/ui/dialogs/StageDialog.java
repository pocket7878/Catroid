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

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import at.tugraz.ist.catroid.R;

/**
 * @author
 * 
 */
public class StageDialog extends Dialog {
	private final Context context;

	public StageDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_stage);
		setTitle(R.string.stage_dialog_title);
		setCanceledOnTouchOutside(true);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		/*
		 * this.setOnShowListener(new OnShowListener() {
		 * public void onShow(DialogInterface dialog) {
		 * InputMethodManager inputManager = (InputMethodManager) context
		 * .getSystemService(Context.INPUT_METHOD_SERVICE);
		 * inputManager.showSoftInput(findViewById(R.id.newSpriteNameEditText), InputMethodManager.SHOW_IMPLICIT);
		 * }
		 * });
		 */

		Button backToConstructionSiteButton = (Button) findViewById(R.id.back_to_construction_site_button);
		backToConstructionSiteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//go to construction site
			}
		});

		Button resume_current_project_button = (Button) findViewById(R.id.resume_current_project_button);
		backToConstructionSiteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//go to construction site
			}
		});

		Button restart_current_project_button = (Button) findViewById(R.id.restart_current_project_button);
		backToConstructionSiteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//go to construction site
			}
		});

		Button snapshot_button = (Button) findViewById(R.id.snapshot_button);
		backToConstructionSiteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//go to construction site
			}
		});
	}
}
