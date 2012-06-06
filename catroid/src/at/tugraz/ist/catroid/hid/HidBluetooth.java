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
package at.tugraz.ist.catroid.hid;

import java.util.Collection;

import android.content.Context;

/**
 * @author dominik
 * 
 */
public class HidBluetooth implements IHid {

	private static HidBluetooth instance;

	private HidBluetooth() {
	}

	public synchronized static HidBluetooth getInstance() {
		if (instance == null) {
			instance = new HidBluetooth();
		}
		return instance;
	}

	public void send(KeyCode key) {

	}

	public void send(Collection<KeyCode> keys) {
		/*
		 * int key = 0;
		 * int modifier = 0;
		 * boolean invalidCombo = false;
		 * 
		 * for (int index : spinnerIndices) {
		 * 
		 * Integer keyCode = new Integer(0);
		 * boolean mod = interpretKey(keyCode, index, keyXmlId);
		 * if (mod) {
		 * modifier = modifier | keyCode.intValue();
		 * } else {
		 * if (invalidCombo) {
		 * 
		 * }
		 * key = keyCode.intValue();
		 * invalidCombo = true;
		 * }
		 * }
		 */
		//buildHIDCode(..)
		//sending the code

	}

	public KeyCode interpretKey(Context context, int spinnerIndex, int keyXmlId) {

		String[] hidRes = context.getResources().getStringArray(keyXmlId);

		KeyCode blub = new KeyCode(false, 1);

		return blub;
	}

	public int generateHidCode() {
		return 0;
	}

}
