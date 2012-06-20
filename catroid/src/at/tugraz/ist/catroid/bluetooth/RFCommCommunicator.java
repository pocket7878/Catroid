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
package at.tugraz.ist.catroid.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import at.tugraz.ist.catroid.R;

public class RFCommCommunicator extends Thread implements BtCommunicator {

	public static final int DISPLAY_TOAST = 1000;
	public static final int STATE_CONNECTED = 1001;
	public static final int STATE_CONNECTERROR = 1002;
	public static final int STATE_CONNECTERROR_PAIRING = 1022;
	public static final int STATE_RECEIVEERROR = 1004;

	private BluetoothAdapter btAdapter;
	private BluetoothSocket rfcCommSocket = null;
	private OutputStream rfcCommOutputStream = null;
	private InputStream rfcCommInputStream = null;

	private Queue<byte[]> messageQueue = new LinkedList<byte[]>();

	private String macAddress;
	private UUID serviceUUID;
	private BTConnectable myOwner;

	protected byte[] returnMessage;

	protected Handler uiHandler;
	protected Resources resources;

	protected boolean connected = false;

	public RFCommCommunicator(BTConnectable myOwner, Handler uiHandler, BluetoothAdapter btAdapter, Resources resources) {
		this.btAdapter = btAdapter;
		this.myOwner = myOwner;
		this.uiHandler = uiHandler;
		this.resources = resources;
	}

	public void setMACAddress(String mMACaddress) {
		this.macAddress = mMACaddress;
	}

	public void setServiceUUID(UUID serviceUUID) {
		this.serviceUUID = serviceUUID;
	}

	public boolean isConnected() {
		return connected;
	}

	public Handler getHandler() {
		return myHandler;
	}

	/**
	 * Creates the connection, waits for incoming messages and dispatches them. The thread will be terminated
	 * on closing of the connection.
	 */
	@Override
	public void run() {

		try {
			createConnection(macAddress, serviceUUID);
		} catch (IOException e) {
		}

		while (connected) {
			try {
				messageQueue.add(receiveMessage());

			} catch (IOException e) {
				// don't inform the user when connection is already closed
				if (connected) {
					sendState(STATE_RECEIVEERROR);
				}
				return;
			}
		}
	}

	public void createConnection(String macAddress, UUID serviceUUID) throws IOException {
		try {
			BluetoothSocket rfcCommBTSocketTemporary;
			BluetoothDevice rfcCommDevice = null;
			rfcCommDevice = btAdapter.getRemoteDevice(macAddress);
			if (rfcCommDevice == null) {
				if (uiHandler == null) {
					throw new IOException();
				} else {
					sendToast(resources.getString(R.string.no_paired_rfccomm_device));
					sendState(STATE_CONNECTERROR);
					return;
				}
			}

			rfcCommBTSocketTemporary = rfcCommDevice.createRfcommSocketToServiceRecord(serviceUUID);
			try {

				rfcCommBTSocketTemporary.connect();

			} catch (IOException e) {
				if (myOwner.isPairing()) {
					if (uiHandler != null) {
						sendToast(resources.getString(R.string.pairing_message));
						sendState(STATE_CONNECTERROR_PAIRING);
					} else {
						throw e;
					}
					return;
				}

				// try another method for connection, this should work on the HTC desire, credits to Michael Biermann
				try {

					Method mMethod = rfcCommDevice.getClass()
							.getMethod("createRfcommSocket", new Class[] { int.class });
					rfcCommBTSocketTemporary = (BluetoothSocket) mMethod.invoke(rfcCommDevice, Integer.valueOf(1));
					rfcCommBTSocketTemporary.connect();
				} catch (Exception e1) {
					if (uiHandler == null) {
						throw new IOException();
					} else {
						sendState(STATE_CONNECTERROR);
					}
					return;
				}
			}
			rfcCommSocket = rfcCommBTSocketTemporary;
			rfcCommInputStream = rfcCommSocket.getInputStream();
			rfcCommOutputStream = rfcCommSocket.getOutputStream();
			connected = true;
		} catch (IOException e) {
			if (uiHandler == null) {
				throw e;
			} else {
				if (myOwner.isPairing()) {
					sendToast(resources.getString(R.string.pairing_message));
				}
				sendState(STATE_CONNECTERROR);
				return;
			}
		}
		// everything was OK
		if (uiHandler != null) {
			sendState(STATE_CONNECTED);
		}
	}

	public void destroyConnection() throws IOException {
		try {
			byte[] exit = { -1 };
			
			if (rfcCommSocket != null && rfcCommOutputStream != null)
				send(exit);
			if (rfcCommSocket != null) {
				connected = false;
				rfcCommSocket.close();
				rfcCommSocket = null;
			}

			rfcCommOutputStream = null;
			rfcCommInputStream = null;

		} catch (IOException e) {
			if (uiHandler == null) {
				throw e;
			} else {
				sendToast(resources.getString(R.string.problem_at_closing));
			}
		}

	}

	public void send(byte[] data) throws IOException {
		this.rfcCommOutputStream.write(data);
	}

	protected void sendToast(String toastText) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("message", DISPLAY_TOAST);
		myBundle.putString("toastText", toastText);
		sendBundle(myBundle);
	}

	protected void sendBundle(Bundle myBundle) {
		Message myMessage = myHandler.obtainMessage();
		myMessage.setData(myBundle);
		uiHandler.sendMessage(myMessage);
	}

	protected void sendState(int message) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("message", message);
		sendBundle(myBundle);
	}

	// receive messages from the UI
	final Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {

			//			switch (myMessage.what) {
			//				case TONE_COMMAND:
			//					doBeep(myMessage.getData().getInt("frequency"), myMessage.getData().getInt("duration"));
			//					break;
			//				case DISCONNECT:
			//					break;
			//				default:
			//					int motor;
			//					int speed;
			//					int angle;
			//					motor = myMessage.getData().getInt("motor");
			//					speed = myMessage.getData().getInt("speed");
			//					angle = myMessage.getData().getInt("angle");
			//					moveMotor(motor, speed, angle);
			//
			//					break;
			//
			//			}
		}
	};

	public byte[] getNextMessage() {
		return messageQueue.poll();
	}

	public byte[] receiveMessage() throws IOException {
		if (rfcCommInputStream == null) {
			throw new IOException();
		}

		long length = rfcCommInputStream.available();
		byte[] bytes = new byte[(int) length];
		rfcCommInputStream.read(bytes);

		//		if (length >= 5) {
		//			Log.i("bt", "" + (int) bytes[4]);
		//		}
		return bytes;
	}
}
