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
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.plugin.PluginManager;
import at.tugraz.ist.catroid.plugin.Drone.DroneHandler;
import at.tugraz.ist.catroid.plugin.Drone.other.DroneDownloadInstallDialog;

public class SettingsActivity extends PreferenceActivity {

	private static final int DIALOG_DOWNLOAD_INSTALL_DRONE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		PreferenceManager.getDefaultSharedPreferences(this);
		Preference dronePlugin = findPreference("setting_drone_plugin");
		Preference droneBricks = findPreference("setting_drone_bricks");
		Preference droneDslTimeout = findPreference("setting_drone_dsl_timeout");

		if (PluginManager.getInstance().isDroneAddonInstalled()) {
			dronePlugin.setSummary(R.string.drone_plugin_installed);
			dronePlugin.setSelectable(false);
			droneBricks.setEnabled(true);
			droneDslTimeout.setEnabled(true);

			droneDslTimeout.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					try {
						int seconds = Integer.parseInt((String) newValue);
						DroneHandler.getInstance().getDrone().setDslTimeout(seconds);
						return true;
					} catch (NumberFormatException e) {
						Toast.makeText(getApplicationContext(), R.string.drone_settings_dsl_only_numbers,
								Toast.LENGTH_LONG).show();
						DroneHandler.getInstance().getDrone().setDslTimeout(5);
					}
					return false;
				}
			});

		} else {
			dronePlugin.setSummary(R.string.drone_plugin_not_installed);
			dronePlugin.setSelectable(true);
			droneBricks.setEnabled(false);
			droneDslTimeout.setEnabled(false);

			dronePlugin.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				public boolean onPreferenceClick(Preference preference) {
					boolean connected = false;
					ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo ni = cm.getActiveNetworkInfo();
					if (ni != null && ni.isAvailable() && ni.isConnected()) {
						connected = true;
					}

					if (!connected) {
						Toast.makeText(getApplicationContext(), R.string.drone_not_connected_to_internet,
								Toast.LENGTH_LONG).show();
						startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
					} else {
						showDialog(DIALOG_DOWNLOAD_INSTALL_DRONE);
					}

					return false;
				}
			});
		}

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