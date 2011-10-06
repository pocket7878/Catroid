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
package at.tugraz.ist.catroid.tutorial;

import android.content.res.Resources;
import android.graphics.Bitmap;

/**
 * @author User
 * 
 */
public class StateController {

	private State state;
	public Tutor tutor;
	public boolean disappeared;

	StateController(Resources resources, Tutor tutor) {
		state = StateIdle.enter(this, resources, tutor.tutorType);
		this.tutor = tutor;
		disappeared = true;
	}

	public void changeState(State state) {
		this.state = state;
		this.state.resetState();
	}

	public boolean isDisappeared() {
		return (disappeared);
	}

	public void setDisappeared(boolean disappeared) {
		this.disappeared = disappeared;
	}

	public Bitmap updateAnimation(Tutor.TutorType tutorType) {
		return (this.state.updateAnimation(tutorType));
	}

}
