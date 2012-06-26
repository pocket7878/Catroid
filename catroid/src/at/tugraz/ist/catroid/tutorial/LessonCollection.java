package at.tugraz.ist.catroid.tutorial;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;
import at.tugraz.ist.catroid.tutorial.tasks.Task;

public class LessonCollection {
	private ArrayList<Lesson> lessonArray;
	private int currentLesson;
	private int currentPossibleLesson;

	private HashMap<Task.Tutor, SurfaceObjectTutor> tutors;

	public void setTutorialOverlay(TutorialOverlay tutorialOverlay) {
	}

	public void resetCurrentLesson() {
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
		if (lessonID > lessonArray.size()) {
			return (false);
		} else {
			currentLesson = lessonID;
			return (true);
		}
	}

	boolean executeTask() {
		//		return (lessonArray.get(currentLesson).executeTask(tutorialOverlay));
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

	int getLastPossibleLessonNumber() {
		return currentPossibleLesson;
	}

	void setLastPossibleLessonNumber(int value) {
		currentPossibleLesson = value;
	}

	ArrayList<String> getLessons() {
		ArrayList<String> lessonNames = new ArrayList<String>();
		for (Lesson tmp : lessonArray) {
			lessonNames.add(tmp.lessonName);
		}
		return lessonNames;
	}

	boolean rewindStep() {
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

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		Log.i("drab", Thread.currentThread().getName() + ": LessonCollection: finalize called!");
		super.finalize();
	}

	public Task.Tutor getNameFromCurrentTaskInLesson() {
		return (lessonArray.get(currentLesson)).getCurrentTutorNameFromTask();
	}

}
