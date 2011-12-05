package at.tugraz.ist.catroid.tutorial.tasks;

import at.tugraz.ist.catroid.tutorial.ClickDispatcher;
import at.tugraz.ist.catroid.tutorial.TutorialOverlay;

public class TaskNotification implements Task {
	private Tutor tutorType;
	private Notification notificationType;

	public Notification getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(Notification notificationType) {
		this.notificationType = notificationType;
	}

	@Override
	public Type getType() {
		return (Type.NOTIFICATION);
	}

	@Override
	public Tutor getTutorType() {
		return tutorType;
	}

	public void setTutorType(Tutor tutorType) {
		this.tutorType = tutorType;
	}

	@Override
	public String execute(TutorialOverlay tutorialOverlay) {
		ClickDispatcher clickDispatcher = tutorialOverlay.getClickDispatcher();
		clickDispatcher.setCurrentNotification(notificationType);
		return ("ActivityChange");
	}

}
