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
public class StatePoint implements State {
	public String stateName = this.getClass().getSimpleName();
	int currentFrame;
	int frameCount;
	Bitmap bitmaps_point[];
	Resources resources;
	private static StatePoint instance;
	boolean animationDirectionToBody;

	@Override
	public String getStateName() {
		return (this.getClass().getSimpleName());
	}

	private StatePoint(StateController controller, Resources resources, Task.Tutor tutorType) {
		this.resources = resources;
		bitmaps_point = new Bitmap[6];
		if (tutorType.compareTo(Task.Tutor.CAT) == 0) {
			bitmaps_point[0] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_point_1);
			bitmaps_point[1] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_point_2);
			bitmaps_point[2] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_point_3);
			bitmaps_point[3] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_point_4);
			bitmaps_point[4] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_point_5);
			bitmaps_point[5] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_point_6);
		} else {
			bitmaps_point[0] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_point_1);
			bitmaps_point[1] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_point_2);
			bitmaps_point[2] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_point_3);
			bitmaps_point[3] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_point_4);
			bitmaps_point[4] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_point_5);
			bitmaps_point[5] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_point_6);
		}
		resetState();
	}

	@Override
	public void resetState() {
		currentFrame = 0;
		frameCount = 6;
		animationDirectionToBody = true;
	}

	public static State enter(StateController controller, Resources resources, Task.Tutor tutorType) {
		Log.i("catroid", "State Point");
		if (instance == null) {
			instance = new StatePoint(controller, resources, tutorType);
		}
		controller.setDisappeared(false);
		return instance;
	}

	@Override
	public Bitmap updateAnimation(Task.Tutor tutorType) {
		if (animationDirectionToBody) {
			if (currentFrame < (frameCount - 1)) {
				currentFrame++;
			} else {
				animationDirectionToBody = false;
			}
		} else {
			if (currentFrame > 3) {
				currentFrame--;
			} else {
				animationDirectionToBody = true;
			}
		}
		return (this.bitmaps_point[currentFrame]);
	}
}
