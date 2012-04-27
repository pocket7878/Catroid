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
package at.tugraz.ist.catroid.plugin.Drone;

import android.content.Context;
import android.util.Log;

public class DroneHandler {

	private static DroneHandler droneHandler;
	private IDrone droneLibraryWrapper;
	private boolean wasAlreadyConnected;

	public static DroneHandler getInstance() {
		if (droneHandler == null) {
			droneHandler = new DroneHandler();
		}
		return droneHandler;
	}

	private DroneHandler() {
	}

	public boolean createDroneFrameworkWrapper(Context context) {

		try {
			droneLibraryWrapper = new DroneLibraryWrapper(context);
		} catch (Exception e) {
			Log.e(DroneConsts.DroneLogTag, "could not create DroneLibraryWrapper. Drone plugin not installed.", e);
			droneLibraryWrapper = null;
			return false;
		}

		return true;
	}

	/** only used for testing */
	public void setIDrone(IDrone idrone) {
		this.droneLibraryWrapper = idrone;
	}

	public IDrone getDrone() {
		return droneLibraryWrapper;
	}

	public boolean wasAlreadyConnected() {
		if (DroneHandler.getInstance().getDrone().getFlyingMode() == -1) {
			return false;
		}

		return wasAlreadyConnected;
	}

	public void setWasAlreadyConnected() {
		wasAlreadyConnected = true;
	}
}
