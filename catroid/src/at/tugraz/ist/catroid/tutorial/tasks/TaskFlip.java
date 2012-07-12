package at.tugraz.ist.catroid.tutorial.tasks;

import java.util.HashMap;
import java.util.Map.Entry;

import at.tugraz.ist.catroid.tutorial.SurfaceObjectTutor;

public class TaskFlip implements Task {
	private Tutor tutorType;
	private boolean flipFast;

	@Override
	public Tutor getTutorType() {
		return tutorType;
	}

	public void setTutorType(Tutor tutorType) {
		this.tutorType = tutorType;
	}

	public boolean isFlipFast() {
		return flipFast;
	}

	public void setFlipFast(boolean flipFast) {
		this.flipFast = flipFast;
	}

	@Override
	public Type getType() {
		return (Type.FLIP);
	}

	@Override
	public boolean execute(HashMap<Task.Tutor, SurfaceObjectTutor> tutors) {
		SurfaceObjectTutor tutor = tutors.get(tutorType);
		if (tutor != null) {
			tutor.flip(this.flipFast);
		}
		return true;
	}

	@Override
	public void setEndPositionOfTaskForTutor(HashMap<Tutor, SurfaceObjectTutor> tutors) {
		for (Entry<Task.Tutor, SurfaceObjectTutor> tempTutor : tutors.entrySet()) {
			if (tutorType == tempTutor.getValue().tutorType) {
				tempTutor.getValue().setTutorToStateAndPosition(-1, -1, false);
			}
		}
	}
}
