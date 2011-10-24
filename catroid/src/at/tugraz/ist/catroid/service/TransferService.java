package at.tugraz.ist.catroid.service;

import java.io.File;
import java.util.LinkedList;

import android.app.NotificationManager;
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
import at.tugraz.ist.catroid.ui.NotificationHandler;
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
			if (msg.what == Consts.UPLOAD_FINISHED_NUMBER) {
				NotificationHandler.getInstance().createFinishedNotification();
			} else if (msg.what == Consts.UPLOAD_ERROR_NUMBER) {
				NotificationHandler.getInstance().createErrorNotification();
			} else {
				int progress = msg.getData().getInt(Consts.UPLOAD_PROGRESS_KEY);
				Log.d("UPDATE", "Update in % : " + progress);
				NotificationHandler.getInstance().createProgressNotification(progress);
			}
		}
	};

	@Override
	public void onCreate() {
		instance = new TransferService();
		NotificationHandler.getInstance().setNotificationManager(
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
		NotificationHandler.getInstance().setContext(getApplicationContext());
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {

		if (isRunning == false) {
			Thread uploadThread = new Thread() {
				@Override
				public void run() {
					Context context = getApplicationContext();
					projectName = intent.getStringExtra(Consts.UPLOAD_PROJECT_NAME_KEY);
					NotificationHandler.getInstance().setProjectName(projectName);
					NotificationHandler.getInstance().createProgressNotification(0);
					isRunning = true;

					String projectDescription = intent.getStringExtra(Consts.UPLOAD_INTENT_DESCRIPTION);
					String token = intent.getStringExtra(Consts.UPLOAD_INTENT_TOKEN);
					String zipFileString = intent.getStringExtra(Consts.UPLOAD_INTENT_PROJECTPATH);
					UtilZip utilZip = new UtilZip();
					File zipFile = utilZip.createzipFile(zipFileString);
					zipFileString = Consts.TMP_PATH + Consts.UPLOAD_ZIP + Consts.CATROID_EXTENTION;

					try {
						ServerCalls.getInstance().uploadProject(projectName, projectDescription, zipFileString,
								UtilDeviceInfo.getUserEmail(context), UtilDeviceInfo.getUserLanguageCode(context),
								token, handler);
						handler.sendEmptyMessage(Consts.UPLOAD_FINISHED_NUMBER);
					} catch (WebconnectionException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(Consts.UPLOAD_ERROR_NUMBER);
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

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onServiceConnected address of transfer Service: " + ((LocalBinder) binder).getService());
		return binder;
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
