package at.tugraz.ist.catroid.tutorial.tasks;

import java.util.HashMap;

import at.tugraz.ist.catroid.tutorial.SurfaceObjectTutor;

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
	public boolean execute(HashMap<Task.Tutor, SurfaceObjectTutor> tutors) {
		SurfaceObjectTutor tutor = tutors.get(tutorType);
		if (tutor != null) {
			tutor.say(message);
		}

		return true;
	}
}
