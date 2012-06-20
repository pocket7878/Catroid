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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import at.tugraz.ist.catroid.bluetooth.BTConnectable;
import at.tugraz.ist.catroid.bluetooth.RFCommCommunicator;

public class HidBluetooth implements IHid, BTConnectable {

	private static HidBluetooth instance;
	private static Activity activity;
	private static Handler recieverHandler;

	private RFCommCommunicator communicator;
	private static final UUID SPP_UUID = UUID.fromString("04c6093b-0000-1000-8000-00805f9b34fb");

	private HidBluetooth() {
	}

	public synchronized static HidBluetooth getUpdatedInstance(Activity activity, Handler recieverHandler) {
		if (instance == null) {
			instance = new HidBluetooth();
		}

		HidBluetooth.activity = activity;
		HidBluetooth.recieverHandler = recieverHandler;

		return instance;
	}

	public synchronized static HidBluetooth getInstance() {
		if (instance == null) {
			instance = new HidBluetooth();
		}
		return instance;
	}

	public byte[] generateHidCode(Collection<KeyCode> keys) {

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

		byte[] conv = new byte[10];

		for (int j = 0; j < 10; j++) {
			conv[j] = (byte) (hidCode[j]);
		}
		return conv;
	}

	public void send(KeyCode key) {

		//byte[] data = String.valueOf(key.getKeyCode()).getBytes();

		if (communicator == null) {
			Log.e("HidBluetooth", "Communicator no available!!");
			return;
		}
		Collection<KeyCode> c = new ArrayList<KeyCode>();
		c.add(key);
		try {
			communicator.send(generateHidCode(c));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(Collection<KeyCode> keys) {
		if (communicator == null) {
			Log.e("HidBluetooth", "Communicator no available!!");
			return;
		}

		try {
			communicator.send(generateHidCode(keys));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public KeyCode interpretKey(Context context, int spinnerIndex, int keyXmlId) {

		String[] hidRes = context.getResources().getStringArray(keyXmlId);

		String keyString = hidRes[spinnerIndex];

		String[] oneKey = keyString.split("\\|");

		KeyCode key = new KeyCode(((oneKey.length > 1) ? true : false), Integer.parseInt(oneKey[0]));

		return key;
	}

	public void startBTCommunicator(String macAddress) {
		communicator = new RFCommCommunicator(this, recieverHandler, BluetoothAdapter.getDefaultAdapter(),
				activity.getResources());
		communicator.setMACAddress(macAddress);
		communicator.setServiceUUID(SPP_UUID);
		communicator.start();

	}

	public boolean isPairing() {
		return false;
	}
}
