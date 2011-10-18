package at.tugraz.ist.catroid.service;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.service.requests.BillingRequest;
import at.tugraz.ist.catroid.service.requests.UploadRequest;
import at.tugraz.ist.catroid.utils.UtilDeviceInfo;
import at.tugraz.ist.catroid.utils.UtilZip;
import at.tugraz.ist.catroid.web.ServerCalls;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class TransferService extends Service implements ServiceConnection {

	private final static String TAG = TransferService.class.getSimpleName();
	private static LinkedList<BillingRequest> pendingRequests = new LinkedList<BillingRequest>();
	private TransferService transferService = null;
	private Binder binder = new LocalBinder();
	private String projectName;
	private static TransferService instance = null;
	private boolean isRunning = false;

	public static TransferService getInstance() {
		return instance;
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == Consts.UPLOAD_FINISHED_TOAST) {
				createFinishedNotification();
			} else if (msg.what == Consts.UPLOAD_ERROR_TOAST) {
				createErrorNotification();
			} else {
				updateProgress(msg.getData().getInt(Consts.UPLOAD_PROGRESS_KEY));
			}
		}
	};

	public void updateProgress(int progress) {
		Log.d("UPDATE", "Update in % : " + progress);
		createNotification(projectName, progress);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onServiceConnected address of transfer Service: " + ((LocalBinder) binder).getService());
		return binder;
	}

	@Override
	public void onCreate() {
		instance = new TransferService();
	}

	public void createNotification(String projectName, int progress) {

		final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String title = getString(R.string.upload_notify_title) + projectName + Consts.HIGH_POINT;
		Intent intent = new Intent(this, TransferService.class);
		final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

		Notification notification = new Notification(R.drawable.catroid_upload, title, System.currentTimeMillis());
		notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;
		notification.contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.upload_progress);
		notification.contentIntent = pendingIntent;
		notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.catroid_upload);
		notification.contentView.setTextViewText(R.id.status_text, title + " " + progress + " %");
		notification.contentView.setProgressBar(R.id.status_progress, Consts.UPLOAD_PROGRESS_MAX, progress, false);
		notificationManager.notify(1, notification);
	}

	public void createFinishedNotification() {

		final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();

		int icon = R.drawable.catroid; // icon from resources
		CharSequence tickerText = "Upload successful!"; // ticker-text
		long when = System.currentTimeMillis(); // notification time
		Context context = getApplicationContext(); // application Context
		CharSequence contentTitle = "Upload successful!"; // message title
		CharSequence contentText = "Uploading \"" + projectName + "\" finished!"; // message text

		Intent notificationIntent = new Intent(this, TransferService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(2, notification);

	}

	public void createErrorNotification() {

		final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();

		int icon = R.drawable.catroid; // icon from resources
		CharSequence tickerText = "Upload failed!"; // ticker-text
		long when = System.currentTimeMillis(); // notification time
		Context context = getApplicationContext(); // application Context
		CharSequence contentTitle = "Upload failed!"; // message title
		CharSequence contentText = "Uploading \"" + projectName + "\" failed!"; // message text

		Intent notificationIntent = new Intent(this, TransferService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(3, notification);
	}

	public File createzipFile(String zipFileString) {
		File dirPath = new File(zipFileString);
		String[] paths = dirPath.list();

		if (paths == null) {
			return null;
		}
		for (int i = 0; i < paths.length; i++) {
			paths[i] = dirPath + Consts.SLASH + paths[i];
		}

		zipFileString = Consts.TMP_PATH + Consts.UPLOAD_ZIP + Consts.CATROID_EXTENTION;
		File zipFile = new File(zipFileString);
		if (!zipFile.exists()) {
			zipFile.getParentFile().mkdirs();
			try {
				zipFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!UtilZip.writeToZipFile(paths, zipFileString)) {
			zipFile.delete();
			return null;
		}
		return zipFile;
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {

		if (isRunning == false) {
			Thread uploadThread = new Thread() {
				@Override
				public void run() {
					Context context = getApplicationContext();
					projectName = intent.getStringExtra(Consts.UPLOAD_PROJECT_NAME_KEY);
					createNotification(projectName, 0);
					isRunning = true;

					String projectDescription = intent.getStringExtra(Consts.UPLOAD_INTENT_DESCRIPTION);
					String token = intent.getStringExtra(Consts.UPLOAD_INTENT_TOKEN);
					String zipFileString = intent.getStringExtra(Consts.UPLOAD_INTENT_PROJECTPATH);
					File zipFile = createzipFile(zipFileString);
					zipFileString = Consts.TMP_PATH + Consts.UPLOAD_ZIP + Consts.CATROID_EXTENTION;

					try {
						ServerCalls.getInstance().uploadProject(projectName, projectDescription, zipFileString,
								UtilDeviceInfo.getUserEmail(context), UtilDeviceInfo.getUserLanguageCode(context),
								token, handler);
						handler.sendEmptyMessage(Consts.UPLOAD_FINISHED_TOAST);
					} catch (WebconnectionException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(Consts.UPLOAD_ERROR_TOAST);
					}
					zipFile.delete();
					stopSelf();
					isRunning = false;
				}
			};
			uploadThread.start();
			return Service.START_STICKY;
		} else {
			CharSequence toast_message = getString(R.string.error_project_upload_running);
			Toast toast = Toast.makeText(TransferService.this, toast_message, Toast.LENGTH_LONG);
			toast.show();
			return 0;
		}

	}

	public boolean bindToMarketBillingService() {
		Log.i(TAG, "binding to TransferService");
		boolean bindResult = bindService(new Intent(this, TransferService.class), this, Context.BIND_AUTO_CREATE);

		if (bindResult) {
			Log.d(TAG, "Bind ok");
			return true;
		} else {
			Log.e(TAG, "Could not bind to service.");
		}

		return false;
	}

	public void uploadRequest(String projectName, String projectDescription, String projectPath, String token) {
		BillingRequest request = new UploadRequest(transferService, projectName, projectDescription, projectPath, token);
		request.runRequest();
	}

	private void runPendingRequests() {
		Log.d(TAG, "run pending requests");

		Log.d(TAG, "Pending requests count: " + pendingRequests.size());

		BillingRequest request;
		while ((request = pendingRequests.peek()) != null) {
			if (request.runIfConnected()) {
				pendingRequests.remove();

			} else {
				Log.i(TAG, "The service crashed, so restart it");
				bindToMarketBillingService();
				return;
			}
		}
	}

	//	public static void updateStatus(String edition_id, int page, int page_count) {
	//		synchronized (TransferService.class) {
	//			//instance.notifyManager.updateNotification(edition_id, page, page_count);
	//
	//		}
	//	}
	//
	//	public void downloadFaild() {
	//		//instance.notifyManager.cancleDownloads();
	//		transferService.stopSelf();
	//	}
	//
	//	public void downloadFinished(String edition_id, boolean is_preview) {
	//		synchronized (TransferService.class) {
	//			//instance.notifyManager.finishNotification(edition_id);
	//
	//			transferService.stopSelf();
	//
	//		}
	//	}

	public void onServiceConnected(ComponentName name, IBinder service) {
		transferService = ((LocalBinder) service).getService();
		Log.d(TAG, "onServiceConnected address of transfer Service: " + transferService);
		runPendingRequests();
	}

	public void onServiceDisconnected(ComponentName name) {
		transferService = null;
	}

	public static void setPendingRequests(LinkedList<BillingRequest> pendingRequests) {
		TransferService.pendingRequests = pendingRequests;
	}

	public static LinkedList<BillingRequest> getPendingRequests() {
		return pendingRequests;
	}

	public TransferService getTransferService() {
		return transferService;
	}

	class LocalBinder extends Binder {
		TransferService getService() {
			return TransferService.this;
		}
	}
}
