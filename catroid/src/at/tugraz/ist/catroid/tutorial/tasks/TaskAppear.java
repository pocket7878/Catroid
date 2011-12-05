package at.tugraz.ist.catroid.tutorial.tasks;

import at.tugraz.ist.catroid.tutorial.TutorialOverlay;

public class TaskAppear implements Task {
	private int x;
	private int y;
	private Tutor tutorType;

	public int getX() {
		return x;
	}

	@Override
	public Tutor getTutorType() {
		return tutorType;
	}

	public void setTutorType(Tutor tutorType) {
		this.tutorType = tutorType;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public Type getType() {
		return (Type.APPEAR);
	}

	@Override
	public String execute(TutorialOverlay tutorialOverlay) {
		if (tutorType == Tutor.CAT) {
			tutorialOverlay.switchToCat();
		} else {
			tutorialOverlay.switchToDog();
		}
		tutorialOverlay.appear(x, y);
		return ("AppearDone");
	}
}
