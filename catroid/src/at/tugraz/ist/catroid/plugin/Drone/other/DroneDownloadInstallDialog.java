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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.plugin.Drone.DroneConsts;

public class DroneDownloadInstallDialog extends ProgressDialog {

	private DownloadDronePlugin download;
	private Context context;

	public DroneDownloadInstallDialog(Context context) {
		super(context);
		this.context = context;

		setMessage("downloading...");
		setIndeterminate(false);
		setMax(100);
		setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		download = new DownloadDronePlugin();
		download.execute(DroneConsts.DCF_DWONLOADLINK + DroneConsts.DCF_FILENAME);
	}

	@Override
	public void onBackPressed() {
		if (download != null) {
			download.cancel(true);
			download = null;
		}
		Toast.makeText(context, R.string.drone_abort_install, Toast.LENGTH_LONG).show();
		dismiss();
	}

	private class DownloadDronePlugin extends AsyncTask<String, Integer, Integer> {
		@Override
		protected Integer doInBackground(String... arg) {
			int count;
			try {
				URL url = new URL(arg[0]);
				URLConnection conexion = url.openConnection();
				conexion.connect();

				int lenghtOfFile = conexion.getContentLength();

				// download the file
				InputStream input = new BufferedInputStream(url.openStream());
				File file = new File(Environment.getExternalStorageDirectory() + "/download/", DroneConsts.DCF_FILENAME);
				OutputStream output = new FileOutputStream(file);

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					setProgress((int) (total * 100 / lenghtOfFile));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();

			} catch (Exception e) {
				Log.e(DroneConsts.DroneLogTag, "Exception downloading", e);
				return -1;
			}

			return 0;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			this.publishProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {

			if (download != null) {
				download.cancel(true);
				download = null;
			}

			dismiss();

			if (result == -1) {
				Toast.makeText(context, R.string.drone_donwload_fail, Toast.LENGTH_LONG).show();
			} else {

				// trigger installation
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
						Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/"
								+ DroneConsts.DCF_FILENAME)), "application/vnd.android.package-archive");
				context.startActivity(intent);

				Toast.makeText(context, R.string.drone_donwload_success, Toast.LENGTH_LONG).show();
			}
		}
	}

}
