package at.tugraz.ist.catroid.tutorial;

import java.util.ArrayList;

import at.tugraz.ist.catroid.tutorial.tasks.Task;

public class Lesson {
	public int lessonID;
	public String lessonName;
	int currentStep;
	private ArrayList<Task> lessonContent = new ArrayList<Task>();

	Lesson() {
	}

	public int getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public void cleanAfterXML() {
		if (lessonContent.size() > 0) {
			lessonContent.remove(0);
		}
	}

	boolean forwardStep() {
		if ((currentStep + 1) >= lessonContent.size()) {
			// TODO: da ghoert naechste Lesson
			// TODO: da stimmt was nicht, das +1 sollte nicht noetig sein

			return (false);
		} else {
			currentStep++;
			return (true);
		}
	}

	boolean rewindStep() {
		if (currentStep <= 0) {
			// TODO: da ghoert vorherige Lesson
			return (false);
		} else {
			currentStep--;
			return (true);
		}
	}

	String executeTask(TutorialOverlay tutorialOverlay) {
		return (lessonContent.get(currentStep).execute(tutorialOverlay));
		//		if (notification != null) {
		//			try {
		//				waitForNotification(notification);
		//			} catch (Exception e) {
		//			}
		//		}
	}

}
