package at.tugraz.ist.catroid.tutorial.tasks;

import java.util.HashMap;

import at.tugraz.ist.catroid.tutorial.ClickDispatcherV2;
import at.tugraz.ist.catroid.tutorial.SurfaceObjectTutor;

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
	public String execute(HashMap<Task.Tutor, SurfaceObjectTutor> tutors) {
		//		ClickDispatcher clickDispatcher = tutorialOverlay.getClickDispatcher();
		//		clickDispatcher.setCurrentNotification(notificationType, null);
		ClickDispatcherV2 clickDispatcher = new ClickDispatcherV2();
		clickDispatcher.processNotification(this);
		// TODO: Maybe Problem: User presses ProjectListItem between setting Notification at
		// begin of execute and the specific setCurrentNotification with the notificationString
		if (notificationType == notificationType.PROJECT_ADD_SPRITE) {
			return ("DialogDone");
		}

		//		if (notificationType == notificationType.PROJECT_LIST_ITEM) {
		//			//clickDispatcher.setCurrentNotification(notificationType, notificationString);
		//		}

		//		if (notificationType == notificationType.SCRIPTS_ADD_BRICK) {
		//			return ("DIALOG");
		//		}
		//		if (notificationType == notificationType.BRICK_CATEGORY_DIALOG) {
		//			return ("DIALOG");
		//		}
		if (notificationType == notificationType.BRICK_ADD_DIALOG) {
			return ("DIALOG");
		}
		//		if (notificationType == notificationType.BRICK_DIALOG_DONE) {
		//			return ("BRICK_DIALOG_DONE");
		//		}

		//		Cloud_old.getInstance(null).setCloud(notificationType);

		// All tasks which result in a switch to another activity, just wait for a dummy
		// notification, which will be deleted during pause/resume of the Tutorial
		return ("ActivityChange");
	}
}
