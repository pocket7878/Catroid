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
import java.util.HashMap;

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
import android.view.MotionEvent;
import android.view.WindowManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.tutorial.tasks.Task;

/**
 * @author faxxe
 * 
 */
public class TutorialController {
	private boolean tutorialActive = false;
	private boolean tutorialPaused;
	Context context = null;
	Dialog dialog = null;
	private HashMap<Task.Tutor, SurfaceObjectTutor> tutors;
	private Cloud cloud;
	private TutorialOverlay tutorialOverlay;
	private SurfaceObjectTutor tutor;
	private SurfaceObjectTutor tutor_2;
	private LessonCollection lessonCollection;
	private XmlHandler xmlHandler;
	private WindowManager windowManager;
	private static WindowManager.LayoutParams dragViewParameters;
	private TutorialThread tutorialThread;
	private boolean activityChanged = false;

	private static final String PREF_KEY_POSSIBLE_LESSON = "possibleLesson";

	public void cleanAll() {
		Cloud.getInstance(context).clear();
		cloud = null;
		tutorialOverlay = null;
		tutor.idle();
		tutor_2.idle();
		tutor = null;
		tutor_2 = null;
		lessonCollection = null;
		xmlHandler = null;
		windowManager = null;
		tutorialThread = null;
		context = null;
		dialog = null;
		tutors.clear();
		tutors = null;
		Tutorial.getInstance(null).clear();

	}

	public TutorialController() {
		tutors = new HashMap<Task.Tutor, SurfaceObjectTutor>();
		//	cloud = Cloud.getInstance(context);
		tutorialPaused = true;

	}

	public void setTutorialActive(boolean val) {
		this.tutorialActive = val;
	}

	public void notifyThread() {
		tutorialThread.notifyThread();
	}

	public void stopThread() {
		tutorialThread.stopThread();
	}

	public void removeOverlayFromWindow() {
		synchronized (tutorialThread) {
			windowManager.removeView(tutorialOverlay);
		}
	}

	public void setTutorialPaused(boolean val) {
		tutorialPaused = val;
	}

	public Activity correctActivity(Activity currentActivity) {
		if (currentActivity.getLocalClassName().compareTo("ui.ScriptActivity") == 0) {
			currentActivity = currentActivity.getParent();
		}
		if (currentActivity.getLocalClassName().compareTo("ui.CostumeActivity") == 0) {
			currentActivity = currentActivity.getParent();
		}
		if (currentActivity.getLocalClassName().compareTo("ui.SoundActivity") == 0) {
			currentActivity = currentActivity.getParent();
		}
		return currentActivity;
	}

	public void initializeTutors() {
		if (tutors == null) {
			tutors = new HashMap<Task.Tutor, SurfaceObjectTutor>();
		}
		if (cloud == null) {
			cloud = Cloud.getInstance(context);
			tutorialOverlay.addCloud(cloud);
		}
		if (tutor == null) {
			//			tutor = new TutorCat(context, tutorialOverlay);
			tutor = new Tutor(R.drawable.tutor_catro_animation, tutorialOverlay, 100, 100, Task.Tutor.CAT);
			tutors.put(tutor.tutorType, tutor);
		}
		if (tutor_2 == null) {
			//			tutor_2 = new TutorDog(context, tutorialOverlay);
			tutor_2 = new Tutor(R.drawable.tutor_miaus_animation, tutorialOverlay, 400, 400, Task.Tutor.DOG);
			tutors.put(tutor_2.tutorType, tutor_2);
		}
		lessonCollection.setTutors(tutors);
	}

	public void initalizeLessonCollection() {

		if (xmlHandler == null) {
			xmlHandler = new XmlHandler(context);
		}
		if (lessonCollection == null) {
			lessonCollection = xmlHandler.getLessonCollection();
		}
	}

	public void initalizeLessons() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		int possibleLesson = preferences.getInt(PREF_KEY_POSSIBLE_LESSON, 0);
		lessonCollection.setLastPossibleLessonNumber(possibleLesson);
		lessonCollection.switchToLesson(0);
		lessonCollection.setTutorialOverlay(tutorialOverlay);

	}

	public void showLessonDialog() {
		if (lessonCollection.getLastPossibleLessonNumber() != 0) {
			AlertDialog alert = generateLessonDialog();
			alert.show();
		} else {
			resumeTutorial();
		}
	}

	public void startThread() {
		if (tutorialThread == null) {
			tutorialThread = new TutorialThread();
			tutorialThread.setName("TutorialThread");
			tutorialThread.setLessonCollection(lessonCollection);
			tutorialThread.startThread();
		} else {
			synchronized (tutorialThread) {
				if (activityChanged) {
					activityChanged = false;
					tutorialThread.notify();
				}
			}
		}
	}

	public void resumeTutorial() {
		if (!tutorialActive || !tutorialPaused) {
			return;
		}
		tutorialPaused = false;
		setupTutorialOverlay();
		initializeTutors();
		startThread();
		//		setDisplayPreferences();
	}

	private AlertDialog generateLessonDialog() {
		//TODO: Cancle Tutorial if Dialog is cancled!
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
		return alert;
	}

	public WindowManager.LayoutParams createLayoutParameters() {
		WindowManager.LayoutParams windowParameters = new WindowManager.LayoutParams();
		windowParameters.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		windowParameters.height = Values.SCREEN_HEIGHT;
		windowParameters.width = Values.SCREEN_WIDTH;
		windowParameters.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParameters.format = PixelFormat.TRANSLUCENT;
		return windowParameters;
	}

	public void setSharedPreferences() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor sharedPreferencesEditor = preferences.edit();
		sharedPreferencesEditor.putInt(PREF_KEY_POSSIBLE_LESSON, lessonCollection.getLastPossibleLessonNumber());
		sharedPreferencesEditor.commit();
		// TODO: Dont know, maybe reset it to default, but if not: copying the project
		// so it dont get lost if tutorial is used again, so that the kids dont lose their work
		//ProjectManager.getInstance().loadProject("defaultProject", context, false);
	}

	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}

	public void rewindStep() {
		lessonCollection.rewindStep();
	}

	public Dialog getDialog() {
		return this.dialog;
	}

	public void stopButtonTutorial() {

		//		tutorialController.setKeepScreenOff();
		//		tutorialController.setOrientationSensorMode();
		this.dialog = null;
		lessonCollection.resetCurrentLesson();
		cleanAll();
	}

	public void idleTutors() {
		tutor.idle(); //TODO: improve! make dynamic :)
		tutor_2.idle();
		tutorialOverlay.removeCloud();
	}

	public void setupTutorialOverlay() {
		if (tutorialOverlay == null) {
			dragViewParameters = createLayoutParameters();
			windowManager = ((Activity) context).getWindowManager();
			tutorialOverlay = new TutorialOverlay(context);
			windowManager.addView(tutorialOverlay, dragViewParameters);
		} else {
			//			tutorialOverlay = new TutorialOverlay(context);
			Log.i("faxxe", "Tutorial: Adding Overlay again!");
			dragViewParameters = createLayoutParameters();
			windowManager = ((Activity) context).getWindowManager();
			windowManager.addView(tutorialOverlay, dragViewParameters);
		}

	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		Activity activity = correctActivity((Activity) context);
		boolean retval;
		if (dialog == null) {
			retval = activity.dispatchTouchEvent(ev);
		} else {
			retval = dialog.dispatchTouchEvent(ev);
		}
		return retval;
	}

	public void setActivityChanged(Context newContext) {
		this.context = newContext;
		activityChanged = true;
	}

	public boolean getActivityChanged() {
		return activityChanged;
	}
}
