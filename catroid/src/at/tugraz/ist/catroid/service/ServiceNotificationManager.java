//package de.androidmag.app.backend.loader;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.view.View;
//import android.widget.RemoteViews;
//
///**
// * This class manages the notification for the application it's possible to add
// * notifications, update, finish and cancel notifications
// * 
// * @author martin
// * 
// */
//public class ServiceNotificationManager {
//
//	private static final String TAG = ServiceNotificationManager.class.getName();
//
//	private static int notifyCounter = 0;
//
//	private Service boundService;
//	private NotificationManager notificationManager;
//
//	private Map<String, Notifications> notificationMap = null;
//
//	public ServiceNotificationManager(Service bound_service) {
//		this.boundService = bound_service;
//		this.notificationMap = new HashMap<String, Notifications>();
//		this.notificationManager = (NotificationManager) boundService.getSystemService(Context.NOTIFICATION_SERVICE);
//	}
//
//	/**
//	 * creates a pendingIntent with the correct flags
//	 * 
//	 * @return PendingIntent
//	 */
//	private PendingIntent getPendingIntent() {
//		Intent intent = new Intent(boundService, MagazinListActivity.class);
//		//intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		//intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//		return PendingIntent.getActivity(boundService, 0, intent, 0/* PendingIntent.FLAG_UPDATE_CURRENT */);
//	}
//
//	/**
//	 * Adds a new Notification of the given edition to the notificationMap and
//	 * show it in the notification bar
//	 * 
//	 * @param edition_title
//	 * @param edition_id
//	 * @param page_count
//	 */
//	public void addDownloadNotification() {
//
//		int icon = android.R.drawable.stat_sys_download;
//		CharSequence tickerText = "Upload wird gestartet: ";
//		long when = System.currentTimeMillis();
//
//		Notification notification = new Notification(icon, tickerText, when);
//
//		RemoteViews contentView = new RemoteViews(boundService.getPackageName(), R.layout.download_progress);
//		contentView.setTextViewText(R.id.status_text, edition_title + " - Download wird vorbereitet...");
//		contentView.setProgressBar(R.id.status_bar, page_count, 0, false);
//		contentView.setViewVisibility(R.id.llProgress, View.GONE);
//		notification.contentView = contentView;
//
//		notification.contentIntent = getPendingIntent();
//
//		notificationManager.notify(notifyCounter, notification);
//		notificationMap.put(edition_id, new Notifications(notifyCounter, notification, edition_title, edition_id));
//		notifyCounter++;
//	}
//
//	/**
//	 * Updates the edition notification with the given edition_id
//	 * 
//	 * @param edition_id
//	 * @param progress
//	 * @param page_count
//	 */
//	public void updateNotification(String edition_id, int progress, int page_count) {
//		Notifications ns = notificationMap.get(edition_id);
//
//		Notification n = ns.getNotification();
//
//		n.contentView.setViewVisibility(R.id.llProgress, View.VISIBLE);
//		n.contentView.setTextViewText(R.id.progress_text, Math.round(100 * progress / page_count) + "%");
//
//		n.contentView.setProgressBar(R.id.status_bar, page_count, progress, false);
//		n.contentView.setTextViewText(R.id.status_text, ns.getEditionName() + "  -  Seite " + progress + "/"
//				+ page_count);
//
//		notificationManager.notify(ns.getNotifyId(), n);
//	}
//
//	/**
//	 * Sets the edition with the given edition_id to finished state
//	 * 
//	 * @param edition_id
//	 */
//	public void finishNotification(String edition_id) {
//		Notifications ns = notificationMap.get(edition_id);
//		Notification n = ns.getNotification();
//		String edition_name = ns.getEditionName();
//
//		CharSequence contentTitle = "Herunterladen erfolgreich";
//		CharSequence contentText = edition_name + " vollständig geladen!";
//
//		n.icon = android.R.drawable.stat_sys_download_done;
//		n.tickerText = "Download abgeschlossen: " + edition_name;
//		n.when = System.currentTimeMillis();
//		n.setLatestEventInfo(boundService, contentTitle, contentText, getPendingIntent());
//
//		n.flags |= Notification.FLAG_AUTO_CANCEL;
//
//		notificationManager.notify(ns.getNotifyId(), n);
//		notificationMap.remove(edition_id);
//	}
//
//	/**
//	 * Sets the given notification to finishedfaild state fires a callback that
//	 * the download of the edition of this notification fails
//	 * 
//	 * @param Notifications
//	 *            ns
//	 */
//	private void finishNotificationFailed(Notifications ns) {
//
//		Notification n = ns.getNotification();
//		String edition_name = ns.getEditionName();
//
//		// Context THISS = boundService.getApplicationContext();
//		CharSequence contentTitle = "Download fehlgeschlagen";
//		CharSequence contentText = "Download " + edition_name + " nicht möglich.";
//
//		n.icon = android.R.drawable.stat_notify_error;
//		n.tickerText = "Download fehlgeschlagen!";
//		n.when = System.currentTimeMillis();
//		n.setLatestEventInfo(boundService, contentTitle, contentText, getPendingIntent());
//
//		n.flags |= Notification.FLAG_AUTO_CANCEL;
//
//		notificationManager.notify(ns.getNotifyId(), n);
//
//		EditionDownloader.onError(ns.getEditionId(), M_Constants.DOWNLOAD_FAILED);
//	}
//
//	/**
//	 * sets all edition download notification to faild state used when one
//	 * download fails, all others are aborted
//	 */
//	public void cancleDownloads() {
//
//		for (String key : notificationMap.keySet()) {
//			finishNotificationFailed(notificationMap.get(key));
//		}
//		notificationMap.clear();
//	}
//
//	/**
//	 * Internal Entity class wich represents a edition notification
//	 * 
//	 * @author martin
//	 * 
//	 */
//	private class Notifications {
//		private int notifyId;
//		private Notification entityNotification;
//		private String editionName;
//		private String editionId;
//
//		private Notifications(int notify_id, Notification entity_notification, String edition_name, String edition_id) {
//			this.notifyId = notify_id;
//			this.entityNotification = entity_notification;
//			this.editionName = edition_name;
//			this.editionId = edition_id;
//		}
//
//		private int getNotifyId() {
//			return this.notifyId;
//		}
//
//		private Notification getNotification() {
//			return this.entityNotification;
//		}
//
//		private String getEditionName() {
//			return editionName;
//		}
//
//		private String getEditionId() {
//			return editionId;
//		}
//	}
//}
