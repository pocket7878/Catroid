package at.tugraz.ist.catroid.tutorial.tasks;

import at.tugraz.ist.catroid.tutorial.TutorialOverlay;

public class TaskSleep implements Task {
	private Tutor tutorType;
	private int sleepTime;

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	@Override
	public Tutor getTutorType() {
		return tutorType;
	}

	public void setTutorType(Tutor tutorType) {
		this.tutorType = tutorType;
	}

	@Override
	public Type getType() {
		return (Type.SLEEP);
	}

	@Override
	public String execute(TutorialOverlay tutorialOverlay) {
		try {
			Thread.sleep(sleepTime);
		} catch (Exception e) {

		}
		return (null);
	}
}