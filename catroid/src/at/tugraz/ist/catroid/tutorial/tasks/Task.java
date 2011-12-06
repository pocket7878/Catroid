package at.tugraz.ist.catroid.tutorial.tasks;

import at.tugraz.ist.catroid.tutorial.TutorialOverlay;

public interface Task {

	public enum Type {
		JUMP, APPEAR, DISAPPEAR, SAY, FLIP, POINT, NOTIFICATION, SLEEP
	}

	public enum Tutor {
		CAT, DOG
	}

	public enum Notification {
		CURRENT_PROJECT_BUTTON, NEW_PROJECT_BUTTON, ABOUT_BUTTON, MY_PROJECTS_BUTTON, UPLOAD_PROJECT_BUTTON, WEB_RESOURCES_BUTTON, SETTINGS_BUTTON, TUTORIAL_BUTTON,
		/**/
		PROJECT_ADD_SPRITE, PROJECT_LIST_ITEM, PROJECT_HOME_BUTTON, PROJECT_STAGE_BUTTON,
		/**/
		SCRIPTS_ADD_BRICK, SCRIPTS_TO_COSTUMES, SCRIPTS_TO_SOUNDS
	}

	public String execute(TutorialOverlay tutorialOverlay);

	public Type getType();

	public Tutor getTutorType();
}
