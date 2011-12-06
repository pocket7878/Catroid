package at.tugraz.ist.catroid.tutorial.tasks;

import at.tugraz.ist.catroid.tutorial.TutorialOverlay;

public class TaskPoint implements Task {
	private Tutor tutorType;

	@Override
	public Tutor getTutorType() {
		return tutorType;
	}

	public void setTutorType(Tutor tutorType) {
		this.tutorType = tutorType;
	}

	@Override
	public Type getType() {
		return (Type.POINT);
	}

	@Override
	public String execute(TutorialOverlay tutorialOverlay) {
		if (tutorType == Tutor.CAT) {
			tutorialOverlay.switchToCat();
		} else {
			tutorialOverlay.switchToDog();
		}
		tutorialOverlay.point();
		return (null);
	}
}
