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
package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.LegoNXT.LegoNXTBtCommunicator;
import at.tugraz.ist.catroid.bluetooth.BluetoothManager;
import at.tugraz.ist.catroid.bluetooth.DeviceListActivity;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.plugin.PluginManager;
import at.tugraz.ist.catroid.plugin.Drone.DroneConsts;
import at.tugraz.ist.catroid.plugin.Drone.DroneHandler;
import at.tugraz.ist.catroid.plugin.Drone.DroneService;
import at.tugraz.ist.catroid.plugin.Drone.DroneService.LocalDroneServiceBinder;
import at.tugraz.ist.catroid.plugin.Drone.DroneServiceHandler;
import at.tugraz.ist.catroid.ui.SettingsActivity;
import at.tugraz.ist.droned.client.CatroidDrone;

public class PreStageActivity extends Activity {

	//Drone Service Members
	DroneService droneService;
	boolean isDroneServiceBound = false;

	private static final int REQUEST_ENABLE_BT = 2000;
	private static final int REQUEST_CONNECT_DEVICE = 1000;
	public static final int REQUEST_RESOURCES_INIT = 0101;
	private static final int DRONEWIFICONNECTIONACTIVITY = 9000;
	public static final int MY_DATA_CHECK_CODE = 0;

	public static final int DRONE_BRICKS_ENABLED_DIALOG = 101010;

	public static StageListener stageListener;
	private static LegoNXT legoNXT;
	private ProgressDialog connectingProgressDialog;
	public static TextToSpeech textToSpeech;
	private int requiredResourceCounter;
	private static boolean DronePartOfProject;

	private boolean autoConnect = false;

	private ConnectThread connThread;

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		restartConnectThread();

	}

	private void restartConnectThread() {
		connThread = null;
		connThread = new ConnectThread();
		connThread.start();
	}

	private void checkProjects() {
		int required_resources = getRequiredRessources();
		int mask = 0x1;
		int value = required_resources;
		boolean noResources = true;
		DronePartOfProject = false;

		while (value > 0) {
			if ((mask & required_resources) > 0) {
				requiredResourceCounter++;
				noResources = false;
			}
			value = value >> 1;
			mask = mask << 1;
		}
		if ((required_resources & Brick.TEXT_TO_SPEECH) > 0) {
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
		}
		if ((required_resources & Brick.BLUETOOTH_LEGO_NXT) > 0) {
			BluetoothManager bluetoothManager = new BluetoothManager(this);

			int bluetoothState = bluetoothManager.activateBluetooth();
			if (bluetoothState == BluetoothManager.BLUETOOTH_NOT_SUPPORTED) {

				Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
				resourceFailed();
			} else if (bluetoothState == BluetoothManager.BLUETOOTH_ALREADY_ON) {
				if (legoNXT == null) {
					startBTComm(true);
				} else {
					resourceInitialized();
				}

			}
		}
		if ((required_resources & Brick.WIFI_DRONE) > 0) {
			// Drone bricks are in the project
			DronePartOfProject = true;

			if (PluginManager.getInstance().areDroneBricksEnabled()) {
				Log.d(DroneConsts.DroneLogTag, "Prestageactivity, drone bricks enabled");
				// Bricks are in the project
				// drone bricks are enabled

				//Init the drone service
				if (!isDroneServiceBound) {
					startDroneService();
					//					while (!isDroneServiceBound) {
					//						Log.d(DroneConsts.DroneLogTag, "Drone Service not Bound, sleeping");
					//						try {
					//							Thread.sleep(100);
					//						} catch (InterruptedException e) {
					//							// TODO Auto-generated catch block
					//							e.printStackTrace();
					//						}
					//					}
					//					Log.d(DroneConsts.DroneLogTag, "Drone Service bound, initializing");
				} else {
					// Check if Drone is ready

					Log.d(DroneConsts.DroneLogTag, "Check if Drone is ready - service already running");

					if (DroneServiceHandler.getInstance().getDrone().getSate() != CatroidDrone.State.READY) {
						DroneServiceHandler.getInstance().getDrone().resetEmergency();
						int sleepCntr = 0;
						while (DroneServiceHandler.getInstance().getDrone().getSate() != CatroidDrone.State.READY) {
							sleepCntr++;
							if (sleepCntr > 30) {

								Toast.makeText(getApplicationContext(), "Problem connecting to drone",
										Toast.LENGTH_LONG).show();

								finishMethod();

								return;
							}
							Log.d(DroneConsts.DroneLogTag, "Waiting for drone to be ready, Resetting Emergency");
							try {
								Thread.sleep(700);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						Log.d(DroneConsts.DroneLogTag, "Drone is ready!");

					}
				}

				// initDroneService();
				// check if we are already connected to an Drone
				//				if (!DroneHandler.getInstance().wasAlreadyConnected()) {
				//					Intent intent = new Intent(this, DroneWifiConnectionActivity.class);
				//					startActivityForResult(intent, DRONEWIFICONNECTIONACTIVITY);
				//				} else {
				//					if (!DroneHandler.getInstance().getDrone().connect()) {
				//						Intent intent = new Intent(this, DroneWifiConnectionActivity.class);
				//						startActivityForResult(intent, DRONEWIFICONNECTIONACTIVITY);
				//					} else {
				//						startStage();
				//					}
				//				}
			} else {
				// Bricks are in the project
				// drone bricks are disabled
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Go to settings?");
				builder.setMessage("Do you want to go to the settings and enable the drone bricks?")
						.setPositiveButton("Yes", dialogGoToSettingsClickListener)
						.setNegativeButton("No", dialogGoToSettingsClickListener).show();
				//Toast.makeText(PreStageActivity.this, R.string.drone_plugin_not_enabled, Toast.LENGTH_LONG).show();
				//finishMethod() ;
			}
		}
		if (noResources == true) {
			startStage();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_prestage);

	}

	private boolean isDroneServiceStarted() {
		if (startService(getDroneServiceIntent()) != null) {
			Log.d(DroneConsts.DroneLogTag, "Drone Service is Running");
			return true;
		} else {
			Log.d(DroneConsts.DroneLogTag, "Drone Service is not running");
			return false;
		}
	}

	private Intent getDroneServiceIntent() {
		return new Intent(this, DroneService.class);
	}

	private void startDroneService() {
		Intent intent = getDroneServiceIntent();
		startService(intent);
		bindService(intent, droneServiceConnection, Context.BIND_AUTO_CREATE);

		if (isDroneServiceBound) {
			// Call a method from the LocalService.
			// However, if this call were something that might hang, then this request should
			// occur in a separate thread to avoid slowing down the activity performance.

		}
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection droneServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(DroneConsts.DroneLogTag, "onServiceConnected:: Drone is Connected");
			LocalDroneServiceBinder droneServiceBinder = (LocalDroneServiceBinder) service;
			DroneServiceHandler.getInstance().setDroneServiceInstance(droneServiceBinder.getDroneService());
			isDroneServiceBound = true;
			DroneServiceHandler.getInstance().getDrone().init();

			// wait until the drone is ready for 5 seconds
			int sleepCntr = 0;
			while (DroneServiceHandler.getInstance().getDrone().getSate() != CatroidDrone.State.READY) {
				sleepCntr++;
				if (sleepCntr > 12) {

					Toast.makeText(getApplicationContext(), "Problem connecting to drone", Toast.LENGTH_LONG).show();

					finishMethod();

					return;
				}
				Log.d(DroneConsts.DroneLogTag, "Waiting for drone to be ready");
				sleep(700);

			}
			Log.d(DroneConsts.DroneLogTag, "Drone is ready!");
			DroneServiceHandler.getInstance().getDrone().resetEmergency();
			startStage();

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.d("Catroid", "onServiceDisconnected:: Drone Service disconnected");
			isDroneServiceBound = false;
		}

		private void sleep(int millis) {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};

	@Override
	public void onResume() {
		super.onResume();
		Log.d(DroneConsts.DroneLogTag, "Prestage onResume() ... should not happen");
		setVisible(true);
		restartConnectThread();
		//checkProjects();

		//getResources();
		//		if (requiredResourceCounter == 0) {
		//			finishMethod();
		//		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Unbind from the service
		if (isDroneServiceBound) {
			unbindService(droneServiceConnection);
			isDroneServiceBound = false;
		}

	}

	//all resources that should be reinitialized with every stage start
	public static void shutdownResources() {
		if (textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		if (legoNXT != null) {
			legoNXT.pauseCommunicator();
		}
		if (DronePartOfProject) {
			// TODO change to DroneService
			//DroneHandler.getInstance().getDrone().emergencyLand();
			//DroneHandler.getInstance().getDrone().disconnect();
		}
	}

	private void finishMethod() {
		Log.d(DroneConsts.DroneLogTag, "finish is called");
		finish();
	}

	//all resources that should not have to be reinitialized every stage start
	public static void shutdownPersistentResources() {

		if (legoNXT != null) {
			legoNXT.destroyCommunicator();
			legoNXT = null;
		}
	}

	private void resourceFailed() {
		Intent intent = this.getIntent();
		this.setResult(RESULT_CANCELED, intent);
		finishMethod();
	}

	private synchronized void resourceInitialized() {
		//Log.i("res", "Resource initialized: " + requiredResourceCounter);

		requiredResourceCounter--;
		if (requiredResourceCounter <= 0) {
			startStage();

		}
	}

	public void startStage() {
		Intent intent = this.getIntent();
		this.setResult(RESULT_OK, intent);
		finishMethod();
	}

	private void startBTComm(boolean autoConnect) {
		connectingProgressDialog = ProgressDialog.show(this, "",
				getResources().getString(R.string.connecting_please_wait), true);
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		serverIntent.putExtra(DeviceListActivity.AUTO_CONNECT, autoConnect);
		this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	private int getRequiredRessources() {

		ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();
		int ressources = Brick.NO_RESOURCES;

		for (Sprite sprite : spriteList) {
			ressources |= sprite.getRequiredResources();
		}
		return ressources;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("bt", "requestcode " + requestCode + " result code" + resultCode);
		switch (requestCode) {

			case REQUEST_ENABLE_BT:
				switch (resultCode) {
					case Activity.RESULT_OK:
						startBTComm(true);
						break;

					case Activity.RESULT_CANCELED:
						Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG)
								.show();
						//finishMethod() ;
						resourceFailed();
						break;
				}
				break;

			case REQUEST_CONNECT_DEVICE:
				switch (resultCode) {
					case Activity.RESULT_OK:
						legoNXT = new LegoNXT(this, recieveHandler);
						String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
						autoConnect = data.getExtras().getBoolean(DeviceListActivity.AUTO_CONNECT);
						legoNXT.startBTCommunicator(address);
						break;

					case Activity.RESULT_CANCELED:
						connectingProgressDialog.dismiss();
						Toast.makeText(PreStageActivity.this, R.string.bt_connection_failed, Toast.LENGTH_LONG).show();
						//finishMethod() ;
						resourceFailed();
						break;
				}
				break;

			case MY_DATA_CHECK_CODE:
				if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
					// success, create the TTS instance
					textToSpeech = new TextToSpeech(this.getApplicationContext(), new OnInitListener() {
						@Override
						public void onInit(int status) {
							resourceInitialized();
							if (status == TextToSpeech.ERROR) {
								Toast.makeText(PreStageActivity.this,
										"Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG)
										.show();
								resourceFailed();
							}
						}
					});

					if (textToSpeech.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_MISSING_DATA) {
						Intent installIntent = new Intent();
						installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
						startActivity(installIntent);
						resourceFailed();
					}
					;
				} else {
					// missing data, install it
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(getString(R.string.text_to_speech_engine_not_installed)).setCancelable(false)
							.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									Intent installIntent = new Intent();
									installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
									startActivity(installIntent);
									resourceFailed();
								}
							}).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
									resourceFailed();
								}
							});
					AlertDialog alert = builder.create();
					alert.show();

				}

				break;

			case DRONEWIFICONNECTIONACTIVITY:
				switch (resultCode) {
					case Activity.RESULT_OK:
						Toast.makeText(PreStageActivity.this, R.string.drone_connect_drone_success, Toast.LENGTH_LONG)
								.show();
						DroneHandler.getInstance().setWasAlreadyConnected();
						// let drone blink 2 seconds green on success
						DroneHandler.getInstance().getDrone().playLedAnimation(1, 5.0f, 2);
						startStage();
						break;

					case Activity.RESULT_CANCELED:
						Toast.makeText(PreStageActivity.this, R.string.drone_connect_drone_cancel, Toast.LENGTH_LONG)
								.show();
						finishMethod();
						break;
				}
				break;

			default:
				resourceFailed();
				break;
		}
	}

	public void makeToast(String text) {
		Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}

	public static void textToSpeech(String text, OnUtteranceCompletedListener listener,
			HashMap<String, String> speakParameter) {

		textToSpeech.setOnUtteranceCompletedListener(listener);
		textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, speakParameter);
	}

	//messages from Lego NXT device can be handled here
	final Handler recieveHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {

			Log.i("bt", "message" + myMessage.getData().getInt("message"));
			switch (myMessage.getData().getInt("message")) {
				case LegoNXTBtCommunicator.STATE_CONNECTED:
					//autoConnect = false;
					connectingProgressDialog.dismiss();
					resourceInitialized();
					break;
				case LegoNXTBtCommunicator.STATE_CONNECTERROR:
					Toast.makeText(PreStageActivity.this, R.string.bt_connection_failed, Toast.LENGTH_SHORT);
					connectingProgressDialog.dismiss();
					legoNXT.destroyCommunicator();
					legoNXT = null;
					if (autoConnect) {
						startBTComm(false);
					} else {
						resourceFailed();
					}
					break;
				default:

					//Toast.makeText(StageActivity.this, myMessage.getData().getString("toastText"), Toast.LENGTH_SHORT);
					break;

			}
		}
	};

	DialogInterface.OnClickListener dialogGoToSettingsClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					//Yes button clicked
					startSettingsActivity();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					//No button clicked
					finishMethod();
					break;
			}
		}
	};

	private void startSettingsActivity() {
		startActivityForResult(new Intent(this, SettingsActivity.class), 1111);
	}

	private class ConnectThread extends Thread {
		@Override
		public void run() {
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			checkProjects();
		}

	}
	//	@Override
	//	protected Dialog onCreateDialog(int id) {
	//		Dialog dialog = null;
	//
	//		// TODO Add enable Brick Dialog
	//		switch (id) {
	//			case DRONE_BRICKS_ENABLED_DIALOG:
	//				//dialog = new EnableDroneBricksDialog(this);
	//				break;
	//			default:
	//				dialog = null;
	//				break;
	//		}
	//
	//		return dialog;
	//	}
}
