package at.tugraz.ist.catroid.tutorial.tasks;

import at.tugraz.ist.catroid.tutorial.ClickDispatcher;
import at.tugraz.ist.catroid.tutorial.Cloud;
import at.tugraz.ist.catroid.tutorial.TutorialOverlay;

public class TaskNotification implements Task {
	private Tutor tutorType;
	private Notification notificationType;
	private String notificationString;

	public String getNotificationString() {
		return notificationString;
	}

	public void setNotificationString(String notificationString) {
		this.notificationString = notificationString;
	}

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
		clickDispatcher.setCurrentNotification(notificationType, null);

		// TODO: Maybe Problem: User presses ProjectListItem between setting Notification at
		// begin of execute and the specific setCurrentNotification with the notificationString
		if (notificationType == notificationType.PROJECT_ADD_SPRITE) {
			return ("DialogDone");
		}

		if (notificationType == notificationType.PROJECT_LIST_ITEM) {
			clickDispatcher.setCurrentNotification(notificationType, notificationString);
		}

		if (notificationType == notificationType.SCRIPTS_ADD_BRICK) {
			return ("DialogDone");
		}

		Cloud.getInstance(null).setCloud(notificationType);

		// All tasks which result in a switch to another activity, just wait for a dummy
		// notification, which will be deleted during pause/resume of the Tutorial
		return ("ActivityChange");
	}
}
