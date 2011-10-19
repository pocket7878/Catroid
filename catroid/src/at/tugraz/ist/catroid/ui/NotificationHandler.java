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

package at.tugraz.ist.catroid.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.service.TransferService;

public class NotificationHandler {

	private static NotificationHandler instance;
	private NotificationManager notificationManager;
	private Context context;
	private String projectName;

	public static NotificationHandler getInstance() {
		if (instance == null) {
			instance = new NotificationHandler();
		}
		return instance;
	}

	public void createNotification(int progress) {

		String title = context.getString(R.string.upload_notify_title) + projectName + Consts.HIGH_POINT;
		Intent intent = new Intent(context, TransferService.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		Notification notification = new Notification(R.drawable.catroid_upload, title, System.currentTimeMillis());
		notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;
		notification.contentView = new RemoteViews(context.getPackageName(), R.layout.upload_progress);
		notification.contentIntent = pendingIntent;
		notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.catroid_upload);
		notification.contentView.setTextViewText(R.id.status_text, title + " " + progress + " %");
		notification.contentView.setProgressBar(R.id.status_progress, Consts.UPLOAD_PROGRESS_MAX, progress, false);
		notificationManager.notify(1, notification);
	}

	public void createFinishedNotification() {

		notificationManager.cancelAll();
		int icon = R.drawable.catroid;
		CharSequence tickerText = "Upload successful!";
		long when = System.currentTimeMillis();
		CharSequence contentTitle = "Upload successful!";
		CharSequence contentText = "Uploading \"" + projectName + "\" finished!";

		Intent notificationIntent = new Intent(context, TransferService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(2, notification);

	}

	public void createErrorNotification() {

		notificationManager.cancelAll();
		int icon = R.drawable.catroid;
		CharSequence tickerText = "Upload failed!";
		long when = System.currentTimeMillis();
		CharSequence contentTitle = "Upload failed!";
		CharSequence contentText = "Uploading \"" + projectName + "\" failed!";

		Intent notificationIntent = new Intent(context, TransferService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(3, notification);
	}

	public void setNotificationManager(NotificationManager nManager) {
		notificationManager = nManager;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
