/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.tutorial.tasks;

import java.util.HashMap;
import java.util.Map.Entry;

import at.tugraz.ist.catroid.tutorial.SurfaceObjectTutor;

/**
 * @author drab
 * 
 */

public class TaskWalk implements Task {
	private Tutor tutorType;
	private int walkToX;
	private int walkToY;
	private boolean walkFast;

	public int getWalkToX() {
		return walkToX;
	}

	public void setWalkToX(int walkToX) {
		this.walkToX = walkToX;
	}

	public int getWalkToY() {
		return walkToY;
	}

	public void setWalkToY(int walkToY) {
		this.walkToY = walkToY;
	}

	@Override
	public Tutor getTutorType() {
		return tutorType;
	}

	public void setTutorType(Tutor tutorType) {
		this.tutorType = tutorType;
	}

	public boolean isWalkFast() {
		return walkFast;
	}

	public void setWalkFast(boolean walkFast) {
		this.walkFast = walkFast;
	}

	@Override
	public Type getType() {
		return (Type.WALK);
	}

	@Override
	public boolean execute(HashMap<Task.Tutor, SurfaceObjectTutor> tutors) {
		SurfaceObjectTutor tutor = tutors.get(tutorType);
		if (tutor != null) {
			tutor.walk(this.walkToX, this.walkToY, this.walkFast);
		}
		return true;
	}

	@Override
	public void setEndPositionForTutor(HashMap<Tutor, SurfaceObjectTutor> tutors) {
		for (Entry<Task.Tutor, SurfaceObjectTutor> tempTutor : tutors.entrySet()) {
			if (tutorType == tempTutor.getValue().tutorType) {
				tempTutor.getValue().setTutorToStateAndPosition(this.walkToX, this.walkToY, false);
			}
		}
	}
}
