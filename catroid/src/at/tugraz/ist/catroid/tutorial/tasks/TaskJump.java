package at.tugraz.ist.catroid.tutorial.tasks;

import java.util.HashMap;

import at.tugraz.ist.catroid.tutorial.SurfaceObjectTutor;

public class TaskJump implements Task {
	private Tutor tutorType;
	private int newX;
	private int newY;

	@Override
	public Tutor getTutorType() {
		return tutorType;
	}

	public void setTutorType(Tutor tutorType) {
		this.tutorType = tutorType;
	}

	public int getNewX() {
		return newX;
	}

	public void setNewX(int newX) {
		this.newX = newX;
	}

	public int getNewY() {
		return newY;
	}

	public void setNewY(int newY) {
		this.newY = newY;
	}

	@Override
	public Type getType() {
		return (Type.JUMP);
	}

	@Override
	public boolean execute(HashMap<Task.Tutor, SurfaceObjectTutor> tutors) {
		SurfaceObjectTutor tutor = tutors.get(tutorType);
		if (tutor != null) {
			tutor.jumpTo(this.newX, this.newY);
		}
		return true;
	}

	@Override
	public void setEndPositionForTutor(HashMap<Tutor, SurfaceObjectTutor> tutors) {
		// TODO Auto-generated method stub

	}

}
