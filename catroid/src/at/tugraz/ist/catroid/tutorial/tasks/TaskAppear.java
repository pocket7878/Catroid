package at.tugraz.ist.catroid.tutorial.tasks;

import java.util.HashMap;

import android.util.Log;
import at.tugraz.ist.catroid.tutorial.SurfaceObjectTutor;

public class TaskAppear implements Task {
	private int x;
	private int y;
	private Tutor tutorType;

	public int getX() {
		return x;
	}

	@Override
	public Tutor getTutorType() {
		return tutorType;
	}

	public void setTutorType(Tutor tutorType) {
		this.tutorType = tutorType;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public Type getType() {
		return (Type.APPEAR);
	}

	@Override
	public boolean execute(HashMap<Task.Tutor, SurfaceObjectTutor> tutors) {
		SurfaceObjectTutor tutor = tutors.get(tutorType);
		if (tutor != null) {
			Log.i("faxxe", "Lesson: tutor exists!");
			tutor.appear(x, y);
		}
		return true;
	}
}
