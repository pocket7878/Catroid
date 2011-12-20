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

/**
 * Consts for the Drone Catroid Integration
 * 
 */
public final class DroneConsts {

	// Firmware Update
	public static final int RESULT_FIRMWARE_UPDATE_OK = 10000;
	public static final int RESULT_FIRMWARE_UPDATE_ERROR = -12;
	public static final int RESULT_FIRMWARE_UPDATE_CANCEL = -13;

	// Wifi Connection
	public static final int SUCCESS = 9;
	public static final int CANCEL = -1;
	public static final int WAITING = 99;
	public static final int WAITING_FOR_FWCHECK = 100;
	public static final int FINSIHED = 999;

	public static final int ERROR_SCANNING = -2;
	public static final int ERROR_FINDING_DRONE = -3;
	public static final int ERROR_CONNECTING_DRONE = -4;
	public static final int ERROR_FIRMWARE = -5;
	public static final int ERROR_NAVDATA = -6;
	public static final int ERROR_CONFIG = -7;

	public static final int START = 0;
	public static final int WIFI_ACTIVATING = 1;
	public static final int SCANNING = 2;
	public static final int CONNECTING_TO_DRONE_WIFI = 3;
	public static final int CHECKING_FIRMWARE = 4;
	public static final int WAITING_FOR_NAVDATA = 5;
	public static final int CHECKING_CONFIG = 6;
	public static final int CONNECTING_TO_DRONE = 7;
	public static final int UPDATING_FIRMWARE = 8;

	public static final int SELECT_DRONE_DIALOG = 20;
	public static final int DIALOG_TERMS_OF_USE = 21;
	public static final int DIALOG_FIRMWAREUPDATE = 22;

	public static final int FIRMWARE_UPDATE_REQEUST = 9999;

	// DownloadAndinstall
	public static final String DCF_DWONLOADLINK = "http://code.google.com/p/catroid/downloads/";
	public static final String DCF_FILENAME = "DroneCatroidPlugin.apk";

	// log Tag
	public static final String DroneLogTag = "Drone";

	// Terms of Use
	public static final String PREF_DRONE_TOF = "DroneTermsOfUseAccepted";

	private DroneConsts() {
		/**
		 * prevent native Code calling
		 */
		throw new AssertionError();
	}
}
