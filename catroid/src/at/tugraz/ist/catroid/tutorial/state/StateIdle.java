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
package at.tugraz.ist.catroid.tutorial.state;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.tutorial.tasks.Task;
import at.tugraz.ist.catroid.tutorial.tasks.Task.Tutor;

/**
 * @author User
 * 
 */
public class StateIdle implements State {
	public String stateName = this.getClass().getSimpleName();
	private static StateIdle instance;
	Bitmap bitmap;

	int currentFrame;
	int frameCount;

	@Override
	public String getStateName() {
		return (this.getClass().getSimpleName());
	}

	private StateIdle(StateController controller, Resources resources, Task.Tutor tutorType) {
		//this.controller = controller;
		if (tutorType.compareTo(Tutor.CAT) == 0) {
			bitmap = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_3);
		} else {
			bitmap = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_talk_1);
		}
		currentFrame = 0;
		frameCount = 3;
	}

	@Override
	public void resetState() {

	}

	public static State enter(StateController controller, Resources resources, Task.Tutor tutorType) {
		Log.i("catroid", "State Idle");
		if (instance == null) {
			instance = new StateIdle(controller, resources, tutorType);
		}
		return instance;
	}

	@Override
	public Bitmap updateAnimation(Tutor tutorType) {
		if (currentFrame < frameCount) {
			currentFrame++;
			return (this.bitmap);
		} else {
			currentFrame = 0;
			return (this.bitmap);
			// andre Idles
		}
	}
}
