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

	public int[] generateHidCode(Collection<KeyCode> keys) {

		int[] hidCode = new int[] { 161, 1, 0, 0, 0, 0, 0, 0, 0, 0 };

		int i = 4;
		for (KeyCode key : keys) {

			if (key.isModifier()) {
				hidCode[2] |= key.getKeyCode();
			} else {
				if (i < 10) {
					hidCode[i] = key.getKeyCode();
					i++;
				}

			}
		}

		return hidCode;
	}

	public void send(KeyCode key) {

	}

	public void send(Collection<KeyCode> keys) {
		int[] hidCode = generateHidCode(keys);
	}

	public KeyCode interpretKey(Context context, int spinnerIndex, int keyXmlId) {

		String[] hidRes = context.getResources().getStringArray(keyXmlId);

		String keyString = hidRes[spinnerIndex];

		String[] oneKey = keyString.split("\\|");

		KeyCode key = new KeyCode(((oneKey.length > 1) ? true : false), Integer.parseInt(oneKey[0]));

		return key;
	}

}
