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
import at.tugraz.ist.catroid.tutorial.Tutorial;
import at.tugraz.ist.catroid.tutorial.tasks.Task;
import at.tugraz.ist.catroid.tutorial.tasks.Task.Tutor;

/**
 * @author User
 * 
 */
public class StateAppear implements State {
	private StateController controller;
	private static StateAppear instance;
	private Resources resources;
	Bitmap bitmaps_portal[];

	int currentFrame;
	int frameCount;
	Task.Tutor tutorType;

	private StateAppear(StateController controller, Resources resources, Task.Tutor tutorType) {
		this.controller = controller;
		this.resources = resources;
		bitmaps_portal = new Bitmap[5];
		if (tutorType.compareTo(Tutor.CAT) == 0) {
			bitmaps_portal[0] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_portal_5);
			bitmaps_portal[1] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_portal_4);
			bitmaps_portal[2] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_portal_3);
			bitmaps_portal[3] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_portal_2);
			bitmaps_portal[4] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_portal_1);
		} else {
			bitmaps_portal[0] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_portal_1);
			bitmaps_portal[1] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_portal_2);
			bitmaps_portal[2] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_portal_3);
			bitmaps_portal[3] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_portal_4);
			bitmaps_portal[4] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_portal_5);
		}
		resetState();
	}

	@Override
	public void resetState() {
		currentFrame = 0;
		frameCount = 5;
	}

	@Override
	public String getStateName() {
		return (this.getClass().getSimpleName());
	}

	public static State enter(StateController controller, Resources resources, Task.Tutor tutorType) {
		Log.i("catroid", "State Appear");
		if (instance == null) {
			instance = new StateAppear(controller, resources, tutorType);
		}
		controller.setDisappeared(false);
		return instance;
	}

	@Override
	public Bitmap updateAnimation(Task.Tutor tutorType) {
		Log.i("catroid", "Appear_update: " + currentFrame + " " + tutorType.toString());
		if (currentFrame < (frameCount - 1)) {
			currentFrame++;
		} else {
			Log.i("catroid", "State Appear: Last Image");
			controller.changeState(StateIdle.enter(controller, resources, tutorType));
			Tutorial tut = Tutorial.getInstance(null);
			tut.setNotification("AppearDone");
			controller.setDisappeared(false);
			resetState();
			return (bitmaps_portal[currentFrame]);
		}
		return (bitmaps_portal[currentFrame]);
	}
}
