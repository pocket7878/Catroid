package at.tugraz.ist.catroid.tutorial;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;
import at.tugraz.ist.catroid.tutorial.tasks.Task;

public class Lesson {
	public int lessonID;
	public String lessonName;
	int currentStep;
	private ArrayList<Task> lessonContent = new ArrayList<Task>();

	Lesson() {
	}

	public void clean() {
		lessonContent.clear();

	}

	//	public void insertTask(Task task){
	//		lessonContent.add(currentStep, task);
	//	}
	public int getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public void cleanAfterXML() {
		//		if (lessonContent.size() > 0) {
		//			lessonContent.remove(0);
		//		}
	}

	boolean forwardStep() {
		synchronized (this) {
			Log.i("lesson", "Forwarding 1 Step");
			if ((currentStep) + 1 >= lessonContent.size()) {
				// TODO: da ghoert naechste Lesson
				// TODO: da stimmt was nicht, das +1 sollte nicht noetig sein
				return (false);
			} else {
				currentStep++;
				return (true);
			}
		}
	}

	boolean rewindStep() {
		synchronized (this) {
			Log.i("lesson", "Backwarding 1 Step");
			if (currentStep <= 0) {
				// TODO: da ghoert vorherige Lesson
				Log.i("drab", "Lesson not rewinded");
				return false;
			} else {
				currentStep--;
				Log.i("drab", "Lesson rewinded to: " + currentStep);
				return true;
			}
		}
	}

	//	String executeTask(NewTutorialOverlay tutorialOverlay) {
	boolean executeTask(HashMap<Task.Tutor, SurfaceObjectTutor> tutors) {
		Log.i("faxxe", "executing: " + lessonContent.get(currentStep).toString());
		return (lessonContent.get(currentStep).execute(tutors));
		//		if (notification != null) {
		//			try {
		//				waitForNotification(notification);
		//			} catch (Exception e) {
		//			}
		//		}
	}

}
