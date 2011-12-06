package at.tugraz.ist.catroid.tutorial;

import java.util.ArrayList;

public class LessonCollection {
	private ArrayList<Lesson> lessonArray;
	private int currentLesson;
	private TutorialOverlay tutorialOverlay;

	public void setTutorialOverlay(TutorialOverlay tutorialOverlay) {
		this.tutorialOverlay = tutorialOverlay;
	}

	public void cleanAfterXML() {
		for (int i = 0; i < lessonArray.size(); i++) {
			lessonArray.get(i).cleanAfterXML();
		}
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

	//	void setNotification(String notification) {
	//		lessonArray.get(currentLesson).setNotification(notification);
	//	}

	String executeTask() {
		return (lessonArray.get(currentLesson).executeTask(tutorialOverlay));
	}

	boolean nextLesson() {
		if ((currentLesson + 1) > lessonArray.size()) {
			return (false);
		} else {
			currentLesson++;
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
		return currentLesson;
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
