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

		if (notificationType == notificationType.PROJECT_ADD_SPRITE) {
			return ("DialogDone");
		}

		// All tasks which result in a switch to another activity, just wait for a dummy
		// notification, which will be deleted during pause/resume of the Tutorial
		return ("ActivityChange");
	}

}
