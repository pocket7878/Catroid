package at.tugraz.ist.catroid.tutorial.tasks;

import java.util.HashMap;

import at.tugraz.ist.catroid.tutorial.ClickDispatcher;
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
	public boolean execute(HashMap<Task.Tutor, SurfaceObjectTutor> tutors) {
		ClickDispatcher clickDispatcher = new ClickDispatcher();
		clickDispatcher.processNotification(this);

		// TODO: Maybe Problem: User presses ProjectListItem between setting Notification at
		// begin of execute and the specific setCurrentNotification with the notificationString

		if (notificationType == Notification.PROJECT_ADD_SPRITE) {
			return true;
		}

		if (notificationType == Notification.CURRENT_PROJECT_BUTTON) {
			return true;
		}

		//		if (notificationType == notificationType.PROJECT_LIST_ITEM) {
		//			//clickDispatcher.setCurrentNotification(notificationType, notificationString);
		//		}
		//
		//		if (notificationType == notificationType.SCRIPTS_ADD_BRICK) {
		//			return ("DIALOG");
		//		}
		//
		//		if (notificationType == notificationType.BRICK_CATEGORY_DIALOG) {
		//			return ("DIALOG");
		//		}
		//
		//		if (notificationType == Notification.BRICK_ADD_DIALOG) {
		//			return true;
		//		}
		//
		//		if (notificationType == notificationType.BRICK_DIALOG_DONE) {
		//			return ("BRICK_DIALOG_DONE");
		//		}

		//		Cloud_old.getInstance(null).setCloud(notificationType);

		// All tasks which result in a switch to another activity, just wait for a dummy
		// notification, which will be deleted during pause/resume of the Tutorial
		return true;
	}

	@Override
	public void setEndPositionForTutor(HashMap<Tutor, SurfaceObjectTutor> tutors) {
	}
}
