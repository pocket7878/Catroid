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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import at.tugraz.ist.catroid.plugin.Drone.DroneConsts;
import at.tugraz.ist.catroid.plugin.Drone.DroneHandler;

public class FirmwareUpdateAsyncTask extends AsyncTask<Void, Integer, Boolean> {

	// TODO Move to own Const Class

	private Context cont;
	private int state;

	public int getState() {
		return state;
	}

	public Context getCont() {
		return cont;
	}

	private Handler handler;

	public FirmwareUpdateAsyncTask(Context cont, Handler handler) {
		this.cont = cont;
		this.handler = handler;
	}

	private static ProgressDialog pd;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		showUploadDialogProgressBar();
	}

	private void showUploadDialogProgressBar() {
		pd = new ProgressDialog(this.getCont());
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Uploading...");
		pd.setCancelable(false);
		pd.show();
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		Log.d(DroneConsts.DroneLogTag, "AsyncTask doInBackground");
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (DroneHandler.getInstance().getDrone().uploadFirmwareFile()) {

			publishProgress(0);
			try {
				Thread.sleep(2000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return DroneHandler.getInstance().getDrone().restartDrone();
		} else {
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		Log.d(DroneConsts.DroneLogTag, "AsyncTask onPostExecute");

		Message msg = new Message();
		msg.what = DroneConsts.RESULT_FIRMWARE_UPDATE_OK;

		handler.sendMessage(msg);

		pd.dismiss();
	}
}
