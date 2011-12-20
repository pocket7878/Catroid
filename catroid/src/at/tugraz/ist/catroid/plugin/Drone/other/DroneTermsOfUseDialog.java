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
package at.tugraz.ist.catroid.plugin.Drone.other;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.plugin.PluginManager;

public class DroneTermsOfUseDialog extends Dialog {

	private boolean accepted;

	public DroneTermsOfUseDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);

		setContentView(R.layout.dialog_drone_terms_of_use);

		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);
		setTitle(R.string.drone_terms_of_use_title);

		final CheckBox cbNeverShowAgain = (CheckBox) findViewById(R.id.cbAlwaysAccept);
		Button btAccept = (Button) findViewById(R.id.accept_button);
		Button btAbort = (Button) findViewById(R.id.abort_button);

		btAccept.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				accepted = true;

				if (cbNeverShowAgain.isChecked()) {
					PluginManager.getInstance().setDroneTermsOfUseAccepted();
				}

				dismiss();
			}
		});

		btAbort.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				accepted = false;
				dismiss();
			}
		});
	}

	public boolean accepted() {
		return accepted;
	}
}
