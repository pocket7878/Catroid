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
		Log.i("new", Thread.currentThread().getName() + ": currentStep was set to " + currentStep);
		this.currentStep = currentStep;
	}

	public void cleanAfterXML() {

	}

	boolean forwardStep() {
		synchronized (this) {
			Log.i("new", "THE SIZE of the Lesson is: " + lessonContent.size());
			if ((this.currentStep + 1) < lessonContent.size()) {
				Log.i("new", Thread.currentThread().getName() + ":Forwarding from " + this.currentStep + " to "
						+ (this.currentStep + 1));
				currentStep++;
				Log.i("drab", Thread.currentThread().getName() + ":Lesson was forwarded to " + this.currentStep);
				return true;
			} else {
				return false;
			}
		}
	}

	int rewindStep() {
		synchronized (this) {
			if (currentStep <= 0) {
				// TODO: da ghoert vorherige Lesson
				Log.i("drab", Thread.currentThread().getName() + ":Lesson not rewinded");
				return 0;
			} else {
				currentStep--;
				int rewindBackSteps = 1;

				while ((lessonContent.get(currentStep).getType() == Task.Type.FLIP
						|| lessonContent.get(currentStep).getType() == Task.Type.JUMP
						|| lessonContent.get(currentStep).getType() == Task.Type.FADEIN || lessonContent.get(
						currentStep).getType() == Task.Type.FADEOUT)
						&& currentStep != 0) {
					Log.i("new", "REWINDED to " + (lessonContent.get(currentStep).getType())
							+ " need to rewind one more");
					currentStep--;
					rewindBackSteps++;
				}
				Log.i("new", "Lesson rewinded to: " + currentStep);
				return rewindBackSteps;
			}
		}
	}

	boolean executeTask(HashMap<Task.Tutor, SurfaceObjectTutor> tutors) {
		Log.i("new",
				Thread.currentThread().getName() + ": @ Step " + currentStep + " executing: "
						+ lessonContent.get(currentStep).toString());
		return (lessonContent.get(currentStep).execute(tutors));
	}

	public int getSizeOfLesson() {
		return this.lessonContent.size();
	}

	public Tutor getCurrentTutorNameFromTask() {
		return lessonContent.get(currentStep).getTutorType();
	}
}
