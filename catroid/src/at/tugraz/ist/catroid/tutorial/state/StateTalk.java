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

/**
 * @author Max
 * 
 */
public class StateTalk implements State {
	public String stateName = this.getClass().getSimpleName();
	int currentFrame;
	int frameCount;
	Bitmap bitmaps_talk[];
	Resources resources;
	Bitmap atlas;
	private static StateTalk instance;

	@Override
	public String getStateName() {
		return (this.getClass().getSimpleName());
	}

	private StateTalk(StateController controller, Resources resources, Task.Tutor tutorType) {
		this.resources = resources;
		//this.controller = controller;
		bitmaps_talk = new Bitmap[3];
		atlas = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_atlas);

		//		if (tutorType.compareTo(Task.Tutor.CAT) == 0) {
		//			bitmaps_talk[0] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_1);
		//			bitmaps_talk[1] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_2);
		//			bitmaps_talk[2] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_3);
		//		} else {
		//			bitmaps_talk[0] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_talk_1);
		//			bitmaps_talk[1] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_talk_2);
		//			bitmaps_talk[2] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_talk_3);
		//		}
		resetState();
	}

	@Override
	public void resetState() {
		currentFrame = 0;
		frameCount = 3;
	}

	public static State enter(StateController controller, Resources resources, Task.Tutor tutorType) {
		Log.i("catroid", "State Talk");
		if (instance == null) {
			instance = new StateTalk(controller, resources, tutorType);
		}
		controller.setDisappeared(false);
		return instance;
	}

	@Override
	public Bitmap updateAnimation(Task.Tutor tutorType) {
		if (currentFrame < (frameCount - 1)) {
			currentFrame++;
		} else {
			currentFrame = 0;
		}
		//	return (this.bitmaps_talk[currentFrame]);
		return Bitmap.createBitmap(atlas, currentFrame * 110, 0, 110, 102);
	}
}
