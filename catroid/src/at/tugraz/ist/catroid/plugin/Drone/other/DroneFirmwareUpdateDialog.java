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
package at.tugraz.ist.catroid.plugin.Drone.other;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.plugin.Drone.DroneConsts;

public class DroneFirmwareUpdateDialog extends Dialog {

	private int status;

	private Button firmware_button_restart_done_ok;
	private Button button_start_firmware_update;
	private Button button_cancel_firmware_update;
	private TextView textview_user_msg;
	WifiManager wifimanager;
	private View view_update;

	public DroneFirmwareUpdateDialog(Context context, WifiManager wifimanager) {
		super(context);
		this.wifimanager = wifimanager;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		this.setCancelable(false);
		this.setTitle("Firmware Update Guide");

		setContentView(R.layout.dialog_drone_updatefirmware);

		this.setCancelable(false);

		textview_user_msg = (TextView) findViewById(R.id.firmware_update_text_view);

		firmware_button_restart_done_ok = (Button) findViewById(R.id.firmware_button_restart_done_ok);
		firmware_button_restart_done_ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				status = DroneConsts.RESULT_FIRMWARE_UPDATE_OK;
				Message msg = new Message();
				msg.what = DroneConsts.RESULT_FIRMWARE_UPDATE_OK;
				handler.sendMessage(msg);
				dismiss();
			}
		});

		button_start_firmware_update = (Button) findViewById(R.id.firmware_start_firmware_update_button);
		button_start_firmware_update.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Start firmware upload
				FirmwareUpdateAsyncTask up = new FirmwareUpdateAsyncTask(getContext(), handler);
				up.execute();

				Log.d(DroneConsts.DroneLogTag, "FirmwareUpdateAsyncTask up.execute();");

			}
		});

		view_update = findViewById(R.id.drone_firmware_update_acitvity_view);
		button_cancel_firmware_update = (Button) findViewById(R.id.firmware_cancel_firmware_update_button);
		button_cancel_firmware_update.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(DroneConsts.DroneLogTag, "Firmware Update Cancelled");
				status = DroneConsts.RESULT_FIRMWARE_UPDATE_CANCEL;
				dismiss();
			}
		});

	}

	public int status() {
		return status;
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			Log.d(DroneConsts.DroneLogTag, "Firmware Update Handler Called");

			switch (msg.what) {

				case DroneConsts.RESULT_FIRMWARE_UPDATE_OK:
					button_cancel_firmware_update.setVisibility(Button.GONE);
					button_start_firmware_update.setVisibility(Button.GONE);
					firmware_button_restart_done_ok.setVisibility(Button.VISIBLE);
					textview_user_msg.setText("Restarting the Drone. Wait until the Drone Leds are green.");
					status = DroneConsts.RESULT_FIRMWARE_UPDATE_OK;
					wifimanager.disconnect();
					break;

				case DroneConsts.RESULT_FIRMWARE_UPDATE_ERROR:
					status = DroneConsts.RESULT_FIRMWARE_UPDATE_ERROR;
					break;

			}
			view_update.invalidate();
		}
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		dismiss();
	}

}
