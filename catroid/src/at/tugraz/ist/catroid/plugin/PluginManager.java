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
package at.tugraz.ist.catroid.plugin;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import at.tugraz.ist.catroid.plugin.Drone.DroneConsts;
import at.tugraz.ist.catroid.plugin.Drone.DroneHandler;

public class PluginManager {

	private static PluginManager pluginManager;
	private Context context;
	private boolean droneAddonInstalled;
	private boolean droneTermsOfUseAccepted;

	public static PluginManager getInstance() {
		return pluginManager;
	}

	public static void createPluginManager(Context context) {
		pluginManager = null;
		pluginManager = new PluginManager(context);
	}

	private PluginManager(Context context) {
		this.context = context;

		if (checkDroneAddonInstalled()) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			droneTermsOfUseAccepted = prefs.getBoolean(DroneConsts.PREF_DRONE_TOF, false);
		}
	}

	private boolean checkDroneAddonInstalled() {
		droneAddonInstalled = DroneHandler.getInstance().createDroneFrameworkWrapper(this.context);
		return droneAddonInstalled;
	}

	public boolean reCheckDroneAddonInstalled() {
		return checkDroneAddonInstalled();
	}

	public boolean isDroneAddonInstalled() {
		return droneAddonInstalled;
	}

	public boolean areDroneTermsOfUseAccepted() {
		return droneTermsOfUseAccepted;
	}

	public void setDroneTermsOfUseAccepted() {
		droneTermsOfUseAccepted = true;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();
		edit.putBoolean(DroneConsts.PREF_DRONE_TOF, droneTermsOfUseAccepted);
		edit.commit();
	}
}
