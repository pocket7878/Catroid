package at.tugraz.ist.catroid.tutorial;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import at.tugraz.ist.catroid.tutorial.tasks.Task;

public class LessonCollection {
	private ArrayList<Lesson> lessonArray;
	private int currentLesson;
	private int currentPossibleLesson;
	private TutorialOverlay tutorialOverlay;

	private HashMap<Task.Tutor, SurfaceObjectTutor> tutors;

	public void setTutorialOverlay(TutorialOverlay tutorialOverlay) {
		this.tutorialOverlay = tutorialOverlay;
	}

	int getLastPossibleLessonNumber() {
		return currentPossibleLesson;
	}

	void setLastPossibleLessonNumber(int value) {
		currentPossibleLesson = value;
	}

	public void resetCurrentLesson() {
		Log.i("new", "LESSON OVER and will be set to 0");
		(lessonArray.get(currentLesson)).setCurrentStep(0);
	}

	public void cleanAfterXML() {
		//		for (int i = 0; i < lessonArray.size(); i++) {
		//			lessonArray.get(i).cleanAfterXML();
		//		}
	}

	LessonCollection() {
		lessonArray = new ArrayList<Lesson>();
	}

	void addLesson(String lessonName) {
		Lesson lesson = new Lesson();
		lesson.lessonID = lessonArray.size();
		lesson.lessonName = lessonName;
		lessonArray.add(lesson);
	}

	boolean switchToLesson(int lessonID) {
		if (lessonID >= lessonArray.size()) {
			return false;
		} else {
			currentLesson = lessonID;
			Log.i("dialog", "Switched to Cl: " + currentLesson);
			return true;
		}
	}

	void initializeIntroForLesson(Context context) {
		new Intro(tutorialOverlay, context);
	}

	boolean executeTask() {
		return (lessonArray.get(currentLesson)).executeTask(tutors);
	}

	boolean nextLesson() {
		if ((currentLesson + 1) >= lessonArray.size()) {
			return false;
		} else {
			currentLesson++;
			if (currentPossibleLesson < currentLesson) {
				currentPossibleLesson = currentLesson;
			}
			return true;
		}
	}

	boolean previousLesson() {
		if ((currentLesson - 1) < 0) {
			return (false);
		} else {
			currentLesson--;
			return (true);
		}
	}

	ArrayList<String> getLessons() {
		ArrayList<String> lessonNames = new ArrayList<String>();
		for (Lesson tmp : lessonArray) {
			lessonNames.add(tmp.lessonName);
		}
		return lessonNames;
	}

	int rewindStep() {
		return (lessonArray.get(currentLesson).rewindStep());
	}

	boolean forwardStep() {
		return (lessonArray.get(currentLesson).forwardStep());
	}

	public void setTutors(HashMap<Task.Tutor, SurfaceObjectTutor> tutors) {
		this.tutors = tutors;
	}

	public void clean() {
		Log.i("drab", Thread.currentThread().getName() + ": LessonCollection: clean called!");
		for (Lesson lesson : lessonArray) {
			lesson.clean();
		}
		tutors.clear();
		tutors = null;
	}

	public int currentStepOfLesson() {
		return (lessonArray.get(currentLesson)).getCurrentStep();
	}

	public int getSizeOfCurrentLesson() {
		return (lessonArray.get(currentLesson)).getSizeOfLesson();
	}

	public int getSizeOfLessonCollection() {
		return lessonArray.size();
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		Log.i("drab", Thread.currentThread().getName() + ": LessonCollection: finalize called!");
		super.finalize();
	}

	public Task.Tutor getTutorNameFromCurrentTaskInLesson() {
		return (lessonArray.get(currentLesson)).getCurrentTutorNameFromTask();
	}

	public Task.Type getTypeFromCurrentTaskInLesson() {
		return (lessonArray.get(currentLesson)).getCurrentTypeFromTask();
	}

	public Task getCurrentTaskObject() {
		return (lessonArray.get(currentLesson)).getCurrentTaskObject();
	}

}
