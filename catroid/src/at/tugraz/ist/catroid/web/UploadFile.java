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

package at.tugraz.ist.catroid.web;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import at.tugraz.ist.catroid.common.Consts;

public class UploadFile {

	public int calcProgressRefresh(int totalPackages) {

		if (totalPackages < 10) {
			return 1;
		} else if (totalPackages > 100) {
			return (totalPackages / 100) + 1;
		} else {
			return 10;
		}
	}

	public void writeFile(DataOutputStream out, InputStream is, Handler handler) {
		try {
			out.writeBytes(Consts.NEWLINE);
			int packageCounter = 1;
			int totalPackages = (is.available() / Consts.BUFFER_8K) + 1;
			int progressRefresh = calcProgressRefresh(totalPackages);

			byte[] writeData = new byte[Consts.BUFFER_8K];
			int length = 0;
			while ((length = is.read(writeData, 0, writeData.length)) != -1) {
				packageCounter++;
				//out.write(writeData, 0, length);
				if (packageCounter % progressRefresh == 0) {
					int progress = (int) ((float) packageCounter / (float) totalPackages * 100);
					if (progress > 100 || progress == 99) {
						progress = 100;
					}
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putInt(Consts.UPLOAD_PROGRESS_KEY, progress);
					message.setData(bundle);
					handler.sendMessage(message);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
