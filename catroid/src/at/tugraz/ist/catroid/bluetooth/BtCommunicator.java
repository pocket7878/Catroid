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
package at.tugraz.ist.catroid.bluetooth;

import android.os.Handler;

// TODO Not needed for LegoNXT anymore, functionality now in LegoNXTBtCommunicator! Maybe refactor similary for arduino or move to arduino package...
public abstract interface BtCommunicator {

	// this is the only OUI registered by LEGO, see http://standards.ieee.org/regauth/oui/index.shtml
	public static final String OUI_LEGO = "00:16:53";

	public void setMACAddress(String mMACaddress);

	public abstract boolean isConnected();

	public Handler getHandler();
}
