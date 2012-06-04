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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import at.tugraz.ist.catroid.tutorial.tasks.Task;

/**
 * @author faxxe
 * 
 */
public class NewTutor extends SurfaceObjectTutor implements SurfaceObject {

	private Context context;
	private TutorialOverlay tutorialOverlay;
	private Resources ressources;
	private Bitmap bitmap;
	private int state = -1;
	private int currentFrame = 0;
	private int currentStep = 0;
	private int maxSteps = 0;
	private long timeStamp = 0;
	private Paint paint;
	private int sizeX = 100;
	private int sizeY = 100;
	private int line = 0;
	private int targetX;
	private int targetY;

	public NewTutor(int drawable, TutorialOverlay tutorialOverlay) {
		super(Tutorial.getInstance(null).getActualContext(), tutorialOverlay);
		context = Tutorial.getInstance(null).getActualContext();
		ressources = context.getResources();
		bitmap = BitmapFactory.decodeResource(ressources, drawable);
		paint = new Paint();
		this.tutorialOverlay = tutorialOverlay;
	}

	public NewTutor(int drawable, TutorialOverlay tutorialOverlay, int x, int y, Task.Tutor tutorType) {
		super(Tutorial.getInstance(null).getActualContext(), tutorialOverlay);
		context = Tutorial.getInstance(null).getActualContext();
		ressources = context.getResources();
		bitmap = BitmapFactory.decodeResource(ressources, drawable);
		paint = new Paint();
		this.tutorialOverlay = tutorialOverlay;
		this.targetX = x;
		this.targetY = y;
		super.tutorType = tutorType;

	}

	@Override
	public void flip() {

	}

	@Override
	public void idle() {
		state = 3;
	}

	@Override
	public void say(String text) {
		Log.i("faxxe", "NewTutor: " + text);
		new NewBubble(text, tutorialOverlay, this);
	}

	@Override
	public void jumpTo(int x, int y) {
		targetX = x;
		targetY = y;

	}

	@Override
	public void appear(int x, int y) {
		targetX = x;
		targetY = y;
		Log.i("faxxe", " appear!");
		state = 0;
	}

	@Override
	public void disappear() {

	}

	@Override
	public void point() {

	}

	@Override
	public void setNewPositionAfterPort() {

	}

	@Override
	public void sayFinished() {

	}

	@Override
	public void draw(Canvas canvas) {
		Bitmap todraw = Bitmap.createBitmap(bitmap, 0, 0, 100, 100);
		if (currentStep == 6) {
			currentStep = 0;
		}
		switch (state) {
			case 1:

				todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, state * 100, 100, 100);
				break;
			case 0:
				if (currentStep == 5) {
					state = 1;
					Tutorial.getInstance(null).setNotification("appear done!");
				}
				todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, state * 100, 100, 100);
				break;
			case 2:
				if (currentStep == 6) {
					currentStep = 0;
				}
				todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, state * 100, 100, 100);
				break;
			case 3:
				if (currentStep == 6) {
					currentStep = 0;
				}
				todraw = Bitmap.createBitmap(bitmap, currentStep * sizeX, state * 100, 100, 100);
				break;

			default:
				return;

		}
		canvas.drawBitmap(todraw, targetX, targetY, paint);

	}

	@Override
	public void update(long gameTime) {
		currentFrame++;
		if (currentFrame % 10 == 0) {
			currentStep++;

		}

	}

	public void register(TutorialOverlay overlay) {
		overlay.addSurfaceObject(this);
	}
}
