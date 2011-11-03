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

package at.tugraz.ist.catroid.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;

public class NotificationHandler {

	private static NotificationHandler instance;
	private NotificationManager notificationManager;
	private Context context;
	private String projectName;
	private int lastNotification = 0;

	public static NotificationHandler getInstance() {
		if (instance == null) {
			instance = new NotificationHandler();
		}
		return instance;
	}

	public void createNotification(int id) {

		int progress = id;
		if ((id - Consts.UPLOAD_PROGRESS_MAX) <= 0) {
			id = 1;
		}
		setLastNotification(id);

		switch (id) {
			case Consts.UPLOAD_NOTIFICATION_PROGRESS:
				createProgressNotification(progress);
				break;
			case Consts.UPLOAD_NOTIFICATION_FINISHED:
				createFinishedNotification();
				break;
			case Consts.UPLOAD_NOTIFICATION_FAILED:
				createErrorNotification();
				break;
			default:
				break;
		}
	}

	public void createProgressNotification(int progress) {

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

		int icon = R.drawable.catroid;
		CharSequence tickerText = context.getText(R.string.upload_ticker_success);
		CharSequence contentTitle = context.getText(R.string.upload_ticker_success);
		CharSequence contentText = context.getText(R.string.upload_text_first) + projectName
				+ context.getText(R.string.upload_text_finished);
		createNotification(icon, tickerText, contentTitle, contentText);

	}

	public void createErrorNotification() {

		int icon = R.drawable.catroid;
		CharSequence tickerText = context.getText(R.string.upload_ticker_failed);
		CharSequence contentTitle = context.getText(R.string.upload_ticker_failed);
		CharSequence contentText = context.getText(R.string.upload_text_first) + projectName
				+ context.getText(R.string.upload_text_failed);
		createNotification(icon, tickerText, contentTitle, contentText);
	}

	public void createNotification(int icon, CharSequence tickerText, CharSequence contentTitle,
			CharSequence contentText) {

		notificationManager.cancelAll();
		long when = System.currentTimeMillis();
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

	public void setLastNotification(int lastNotification) {
		this.lastNotification = lastNotification;
	}

	public int getLastNotification() {
		return lastNotification;
	}
}
