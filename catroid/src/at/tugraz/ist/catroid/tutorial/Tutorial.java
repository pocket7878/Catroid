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
package at.tugraz.ist.catroid.tutorial;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Values;

/**
 * @author faxxe
 * 
 */
public class Tutorial {
	public static final boolean DEBUG = true;
	private static final String PREF_KEY_POSSIBLE_LESSON = "possibleLesson";

	public volatile ArrayList<String> notifies = new ArrayList<String>();
	private String currentNotification = "";

	private Thread tutorialThread;

	private static final Tutorial tutorial = new Tutorial();
	private static boolean tutorialActive = false;
	private static Context context;
	private static XmlHandler xmlHandler;
	boolean tutorialThreadRunning = true;
	private static WindowManager.LayoutParams dragViewParameters;
	public TutorialOverlay tutorialOverlay;
	private Dialog dialog;
	WindowManager windowManager;
	static LessonCollection lessonCollection;
	private static Tutor tutor;
	private static Tutor tutor_2;

	private Tutorial() {
	}

	public void rewindStep() {
		lessonCollection.rewindStep();
	}

	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}

	public Dialog getDialog() {
		return this.dialog;
	}

	public static Tutorial getInstance(Context con) {
		if (con != null) {
			context = con;
		}
		if (xmlHandler == null) {
			xmlHandler = new XmlHandler(context);

		}
		if (lessonCollection == null) {
			lessonCollection = xmlHandler.getLessonCollection();
		}
		if (tutor == null) {
			tutor = new Tutor(context.getResources(), context, Tutor.TutorType.CAT_TUTOR);
		}
		if (tutor_2 == null) {
			tutor_2 = new Tutor(context.getResources(), context, Tutor.TutorType.DOG_TUTOR);
		}

		return tutorial;
	}

	private void showLessonDialog() {

		if (lessonCollection.getLastPossibleLessonNumber() == 0) {
			lessonCollection.switchToLesson(0);
			resumeTutorial();
			return;
		}

		//final CharSequence[] items = { "Red", "Green", "Blue" };

		ArrayList<String> lessons = lessonCollection.getLessons();
		final CharSequence[] items = new CharSequence[lessonCollection.getLastPossibleLessonNumber() + 1];
		for (int i = 0; i < lessonCollection.getLastPossibleLessonNumber() + 1; i++) {
			items[i] = lessons.get(i);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("choose lesson:");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				lessonCollection.switchToLesson(item);
				resumeTutorial();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	private boolean startTutorial() {
		ProjectManager.getInstance().initializeThumbTutorialProject(context);
		tutorialActive = true;

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		int possibleLesson = preferences.getInt(PREF_KEY_POSSIBLE_LESSON, 0);
		lessonCollection.setLastPossibleLessonNumber(possibleLesson);

		showLessonDialog();
		Log.i("catroid", "starting tutorial...");
		return tutorialActive;
	}

	public void resumeTutorial() {
		if (!tutorialActive) {
			return;
		}
		dragViewParameters = createLayoutParameters();
		windowManager = ((Activity) context).getWindowManager();
		tutorialOverlay = new TutorialOverlay(context, tutor, tutor_2);
		windowManager.addView(tutorialOverlay, dragViewParameters);

		tutorialThreadRunning = true;
		tutorialThread = new Thread(new Runnable() {
			@Override
			public void run() {
				continueTutorial();
				return;
			}
		});
		tutorialThread.setName("TutorialThread");
		tutorialThread.start();
	}

	public void stopTutorial() {
		pauseTutorial();
		tutorialActive = false;

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor sharedPreferencesEditor = preferences.edit();
		sharedPreferencesEditor.putInt(PREF_KEY_POSSIBLE_LESSON, lessonCollection.getLastPossibleLessonNumber());
		sharedPreferencesEditor.commit();

		// TODO: Dont know, maybe reset it to default, but if not: copying the project
		// so it dont get lost if tutorial is used again, so that the kids dont lose their work
		//ProjectManager.getInstance().loadProject("defaultProject", context, false);
	}

	public void stopButtonTutorial() {
		stopTutorial();
		lessonCollection.resetCurrentLesson();
	}

	public void pauseTutorial() {
		if (!tutorialActive) {
			return;
		}
		tutorialThreadRunning = false;
		tutor.idle();
		tutor_2.idle();
		boolean retry = true;
		while (retry) {
			try {
				tutorialThread.join(1);
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}

		notifies.clear();

		if (tutorialOverlay != null) {
			windowManager.removeView(tutorialOverlay);
			tutorialOverlay = null;
		}
	}

	public boolean toggleTutorial() {
		if (tutorialActive == false) {
			startTutorial();
		} else {
			stopTutorial();
		}
		return tutorialActive;
	}

	private WindowManager.LayoutParams createLayoutParameters() {

		WindowManager.LayoutParams windowParameters = new WindowManager.LayoutParams();
		windowParameters.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

		windowParameters.height = Values.SCREEN_HEIGHT;
		windowParameters.width = Values.SCREEN_WIDTH;
		windowParameters.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParameters.format = PixelFormat.TRANSLUCENT;

		return windowParameters;
	}

	public boolean continueTutorial() {
		if (tutorialActive) {

			lessonCollection.setTutorialOverlay(tutorialOverlay);
			do {
				String notification = lessonCollection.executeTask();
				if (notification != null) {
					try {
						waitForNotification(notification);
					} catch (Exception e) {

					}
				}
			} while (lessonCollection.forwardStep() && tutorialThreadRunning);
		}

		if (!lessonCollection.forwardStep()) {
			lessonCollection.resetCurrentLesson();
		}

		if (tutorialThreadRunning) {
			lessonCollection.nextLesson();
			stopTutorial();
		}

		return tutorialActive;
	}

	public void waitForNotification(String waitNotification) throws InterruptedException {
		Log.i("catroid", "waiting for: " + waitNotification);
		while (tutorialThreadRunning) {
			for (int i = 0; i < notifies.size(); i++) {
				currentNotification = notifies.get(i);
				if (currentNotification.compareTo(waitNotification) == 0) {
					notifies.remove(i);
					notifies.clear();
					Log.i("catroid", "waited enough!" + waitNotification);
					return;
				}

				try {
					Thread.sleep(50);
				} catch (Exception e) {

				}

				//				synchronized (this) {
				//					wait(100);
				//				}
			}
		}
	}

	public void setNotification(String notification) {
		Log.i("catroid", "setting Notification " + notification);
		notifies.add(notification);
	}
}