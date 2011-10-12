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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import at.tugraz.ist.catroid.R;

public class Tutor {
	static public enum TutorType {
		CAT_TUTOR, DOG_TUTOR
	}

	int x;
	int y;

	Bitmap bitmap;

	int currentFrame;
	int frameCount;
	private int framePeriod; // milliseconds between each frame (1000/fps)
	private int fps;
	private long frameTicker; // the time of the last frame update
	TutorBubble tutorBubble;
	Resources resources;
	Context context;
	int xPortTo = 0;
	int yPortTo = 0;
	boolean isAppeared;
	boolean facingFlipped;
	Matrix flipMatrix;

	StateController controller;
	TutorType tutorType;

	public Tutor(Resources resources, Context context, TutorType tutorType) {
		this.tutorType = tutorType;
		this.resources = resources;
		this.context = context;
		isAppeared = false;
		fps = 10;
		currentFrame = 0;
		framePeriod = 1000 / fps;
		frameTicker = 0l;
		facingFlipped = false;
		controller = new StateController(resources, this);
	}

	public void update(long gameTime) {
		if (gameTime > frameTicker + framePeriod) {
			frameTicker = gameTime;
			bitmap = controller.updateAnimation(tutorType);
		}

		if (tutorBubble != null) {
			if (tutorBubble.done) { //intresting!!
				tutorBubble = null;
			} else {
				tutorBubble.update(gameTime);
			}
		}
	}

	public void flip() {
		if (facingFlipped) {
			facingFlipped = false;
		} else {
			facingFlipped = true;
		}
	}

	public void jumpTo(int x, int y) {
		controller.changeState(StateJump.enter(controller, resources, tutorType));
		xPortTo = x;
		yPortTo = y;
	}

	public void point() {
		controller.changeState(StatePoint.enter(controller, resources));
	}

	public void setNewPositionAfterPort() {
		x = xPortTo;
		y = yPortTo;
	}

	public void appear(int x, int y) {
		controller.changeState(StateAppear.enter(controller, resources, tutorType));
		this.x = x;
		this.y = y;
		isAppeared = true;
	}

	public void disappear() {
		controller.changeState(StateDisappear.enter(controller, resources, tutorType));
		//TODO: Das erst wenn Animation fertig
		isAppeared = false;
	}

	public void say(String text) {
		controller.changeState(StateTalk.enter(controller, resources, tutorType));

		if (tutorType.equals(TutorType.DOG_TUTOR)) {
			tutorBubble = new TutorBubble(text, resources.getDrawable(R.drawable.bubble_up), this.x, this.y, context);

		} else if (tutorType.equals(TutorType.CAT_TUTOR)) {
			tutorBubble = new TutorBubble(text, resources.getDrawable(R.drawable.bubble), this.x, this.y, context);
		}

	}

	public void draw(Canvas canvas) {

		if (!controller.isDisappeared()) {
			try {
				if (facingFlipped) {

					flipMatrix = new Matrix();
					flipMatrix.setScale(-1, 1);
					flipMatrix.postTranslate(bitmap.getWidth(), 0);

					canvas.save(Canvas.MATRIX_SAVE_FLAG);
					canvas.concat(flipMatrix);
					canvas.drawBitmap(bitmap, -x, y, null);
					canvas.restore();
				} else {
					canvas.drawBitmap(bitmap, x, y, null);
				}
			} catch (Exception e) {
				// mh
			}
			if (tutorBubble != null) {
				tutorBubble.draw(canvas);
			}
		}

	}
}
