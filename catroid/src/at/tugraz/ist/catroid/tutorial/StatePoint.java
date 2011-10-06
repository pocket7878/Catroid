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
import android.graphics.BitmapFactory;
import at.tugraz.ist.catroid.R;

/**
 * @author User
 * 
 */
public class StatePoint implements State {
	int currentFrame;
	int frameCount;
	Bitmap bitmaps_point[];
	Resources resources;
	//private StateController controller;
	private static StatePoint instance;
	boolean animationDirectionToBody;

	private StatePoint(StateController controller, Resources resources) {
		this.resources = resources;
		//this.controller = controller;
		bitmaps_point = new Bitmap[6];
		bitmaps_point[0] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_point_1);
		bitmaps_point[1] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_point_2);
		bitmaps_point[2] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_point_3);
		bitmaps_point[3] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_point_4);
		bitmaps_point[4] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_point_5);
		bitmaps_point[5] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_point_6);
		resetState();
	}

	public void resetState() {
		currentFrame = 0;
		frameCount = 6;
		animationDirectionToBody = true;
	}

	public static State enter(StateController controller, Resources resources) {
		if (instance == null) {
			instance = new StatePoint(controller, resources);
		}
		return (instance);
	}

	public Bitmap updateAnimation(Tutor.TutorType tutorType) {
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
