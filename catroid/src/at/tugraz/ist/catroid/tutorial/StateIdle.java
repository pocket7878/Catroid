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

import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import at.tugraz.ist.catroid.R;

/**
 * @author User
 * 
 */
public class StateIdle implements State {
	public String stateName = this.getClass().getSimpleName();
	private static HashMap<Tutor.TutorType, StateIdle> instances;
	Bitmap bitmap;

	int currentFrame;
	int frameCount;

	@Override
	public String getStateName() {
		return (this.getClass().getSimpleName());
	}

	private StateIdle(StateController controller, Resources resources, Tutor.TutorType tutorType) {
		//this.controller = controller;
		if (tutorType.compareTo(Tutor.TutorType.CAT_TUTOR) == 0) {
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

	public static State enter(StateController controller, Resources resources, Tutor.TutorType tutorType) {
		Log.i("catroid", "State Idle");
		if (instances == null) {
			instances = new HashMap<Tutor.TutorType, StateIdle>();
		}
		if (!instances.containsKey(tutorType)) {
			instances.put(tutorType, new StateIdle(controller, resources, tutorType));
		}
		controller.setDisappeared(false);
		return (instances.get(tutorType));
	}

	@Override
	public Bitmap updateAnimation(Tutor.TutorType tutorType) {
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
