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
package at.tugraz.ist.catroid.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.plugin.Drone.other.DroneDownloadInstallDialog;

public class SettingsActivity extends PreferenceActivity {

	private static final int DIALOG_DOWNLOAD_INSTALL_DRONE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		PreferenceManager.getDefaultSharedPreferences(this);
		Preference droneBricks = findPreference("setting_drone_bricks");
		Preference droneDslTimeout = findPreference("setting_drone_dsl_timeout");

		droneBricks.setEnabled(true);
		droneDslTimeout.setEnabled(true);

		droneDslTimeout.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				try {
					int seconds = Integer.parseInt((String) newValue);
					// TODO set Security Time Out
					// DroneHandler.getInstance().getDrone().setDslTimeout(seconds);
					return true;
				} catch (NumberFormatException e) {
					Toast.makeText(getApplicationContext(), R.string.drone_settings_dsl_only_numbers, Toast.LENGTH_LONG)
							.show();
					// TODO Necessary
					//DroneHandler.getInstance().getDrone().setDslTimeout(5);
				}
				return false;
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;

		switch (id) {
			case DIALOG_DOWNLOAD_INSTALL_DRONE:
				dialog = new DroneDownloadInstallDialog(this);
				break;
			default:
				dialog = null;
				break;
		}

		return dialog;
	}
}
