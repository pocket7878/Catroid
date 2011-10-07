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
	private static int notificationCounter = 0;
	private String projectName = "";
	private static TransferService instance = null;

	public static TransferService getInstance() {
		return instance;
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				CharSequence toast_message = "Uploading project: " + projectName + " finished";
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(TransferService.this, toast_message, duration);
				toast.show();
			}
		}
	};

	public void updateProgress(int progress) {

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

	public void createNotification(String projectName) {
		final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(this, TransferService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		String title = getString(R.string.upload_notify_title);
		String content = getString(R.string.upload_notify_content);
		String ticker = getString(R.string.upload_notify_ticker);
		Notification notification = new Notification(R.drawable.catroid, ticker, System.currentTimeMillis());
		notification.setLatestEventInfo(getApplicationContext(), title, content + projectName, contentIntent);
		mNotificationManager.notify(notificationCounter, notification);
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {

		Thread uploadThread = new Thread() {
			@Override
			public void run() {
				notificationCounter++;
				Context context = getApplicationContext();
				projectName = intent.getStringExtra("uploadName");
				createNotification(projectName);

				String projectDescription = intent.getStringExtra(getString(R.string.upload_intent_description));
				String token = intent.getStringExtra(getString(R.string.upload_intent_token));
				File dirPath = new File(intent.getStringExtra(getString(R.string.upload_intent_projectPath)));
				String[] paths = dirPath.list();

				if (paths == null) {
					return;
				}
				for (int i = 0; i < paths.length; i++) {
					paths[i] = dirPath + "/" + paths[i];
				}

				String zipFileString = Consts.TMP_PATH + "/upload" + Consts.CATROID_EXTENTION;
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
					return;
				}

				try {
					ServerCalls.getInstance().uploadProject(projectName, projectDescription, zipFileString,
							UtilDeviceInfo.getUserEmail(context), UtilDeviceInfo.getUserLanguageCode(context), token);

					handler.sendEmptyMessage(1);
					notificationCounter--;
					final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					mNotificationManager.cancel(1);
				} catch (WebconnectionException e) {
					e.printStackTrace();
					return;
				}
				zipFile.delete();
				stopSelf();
			}
		};
		uploadThread.start();

		return Service.START_STICKY;
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
