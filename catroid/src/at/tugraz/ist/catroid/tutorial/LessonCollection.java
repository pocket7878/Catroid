package at.tugraz.ist.catroid.tutorial;

import java.util.ArrayList;

public class LessonCollection {
	private ArrayList<Lesson> lessonArray;
	private int currentLesson;
	private int currentPossibleLesson;
	private TutorialOverlay tutorialOverlay;

	public void setTutorialOverlay(TutorialOverlay tutorialOverlay) {
		this.tutorialOverlay = tutorialOverlay;
	}

	public void resetCurrentLesson() {
		lessonArray.get(currentLesson).currentStep = 0;
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

	String executeTask() {
		return (lessonArray.get(currentLesson).executeTask(tutorialOverlay));
	}

	boolean nextLesson() {
		if ((currentLesson + 1) >= lessonArray.size()) {
			return (false);
		} else {
			currentLesson++;
			if (currentPossibleLesson < currentLesson) {
				currentPossibleLesson = currentLesson;
			}
			return (true);
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

}
