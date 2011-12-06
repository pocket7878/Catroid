package at.tugraz.ist.catroid.tutorial.tasks;

import at.tugraz.ist.catroid.tutorial.TutorialOverlay;

public class TaskSay implements Task {
	private String message;
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
		return (Type.SAY);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String execute(TutorialOverlay tutorialOverlay) {
		if (tutorType == Tutor.CAT) {
			tutorialOverlay.switchToCat();
		} else {
			tutorialOverlay.switchToDog();
		}
		tutorialOverlay.say(message);
		return ("BubbleDone");
	}
}
