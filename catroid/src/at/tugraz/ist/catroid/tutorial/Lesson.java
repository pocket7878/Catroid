package at.tugraz.ist.catroid.tutorial;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;
import at.tugraz.ist.catroid.tutorial.tasks.Task;
import at.tugraz.ist.catroid.tutorial.tasks.Task.Tutor;

public class Lesson {
	public int lessonID;
	public String lessonName;
	private int currentStep;
	private ArrayList<Task> lessonContent = new ArrayList<Task>();

	public Lesson() {
		this.currentStep = 0;
		Log.i("drab", Thread.currentThread().getName() + ": Lesson currentStep: " + this.currentStep);
	}

	public void clean() {
		lessonContent.clear();
	}

	public int getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(int currentStep) {
		Log.i("drab", Thread.currentThread().getName() + ": currentStep was set to " + currentStep);
		this.currentStep = currentStep;
	}

	public void cleanAfterXML() {
		//		if (lessonContent.size() > 0) {
		//			lessonContent.remove(0);
		//		}
	}

	boolean forwardStep() {
		synchronized (this) {
			if ((this.currentStep) + 1 >= lessonContent.size()) {
				// TODO: da ghoert naechste Lesson
				// TODO: da stimmt was nicht, das +1 sollte nicht noetig sein
				return false;
			} else {
				Log.i("drab", Thread.currentThread().getName() + ":Forwarding from " + this.currentStep + " to "
						+ (this.currentStep + 1));
				currentStep++;
				Log.i("drab", Thread.currentThread().getName() + ":Lesson was forwarded to " + this.currentStep);
				return true;
			}
		}
	}

	boolean rewindStep() {
		synchronized (this) {
			if (currentStep <= 0) {
				// TODO: da ghoert vorherige Lesson
				Log.i("drab", Thread.currentThread().getName() + ":Lesson not rewinded");
				return false;
			} else {
				Log.i("new", Thread.currentThread().getName() + ":Backwarding from " + currentStep + " to "
						+ (currentStep - 1));
				currentStep--;
				Log.i("drab", Thread.currentThread().getName() + ":Lesson rewinded to: " + currentStep);
				return true;
			}
		}
	}

	//	String executeTask(NewTutorialOverlay tutorialOverlay) {
	boolean executeTask(HashMap<Task.Tutor, SurfaceObjectTutor> tutors) {
		Log.i("drab", Thread.currentThread().getName() + ": @ Step " + currentStep + " executing: "
				+ lessonContent.get(currentStep).toString());
		return (lessonContent.get(currentStep).execute(tutors));
	}

	public int getSizeOfLesson() {
		return this.lessonContent.size();
	}

	public Tutor getCurrentTutorNameFromTask() {
		// TODO Auto-generated method stub
		return lessonContent.get(currentStep).getTutorType();
	}
}
