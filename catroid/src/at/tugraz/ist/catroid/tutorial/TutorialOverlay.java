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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author User
 * 
 */
public class TutorialOverlay extends SurfaceView implements SurfaceHolder.Callback {

	Tutor tutor;
	Tutor tutor_2;
	Tutor currentTutor;

	private TutorialThread mThread;

	/**
	 * @param context
	 */
	public TutorialOverlay(Context context, int test) {
		super(context);
		this.setBackgroundColor(Color.TRANSPARENT);
		this.setZOrderOnTop(true); //necessary
		getHolder().setFormat(PixelFormat.TRANSPARENT);
		tutor = new Tutor(getResources(), context, Tutor.TutorType.CAT_TUTOR);
		tutor_2 = new Tutor(getResources(), context, Tutor.TutorType.DOG_TUTOR);
		currentTutor = tutor;
		getHolder().addCallback(this);
		mThread = new TutorialThread(this);
	}

	public void switchToDog() {
		currentTutor = tutor_2;
	}

	public void switchToCat() {
		currentTutor = tutor;
	}

	public void flip() {
		currentTutor.flip();
	}

	public void say(String text) {
		currentTutor.say(text);
	}

	public void jumpTo(int x, int y) {
		currentTutor.jumpTo(x, y);
	}

	public void appear(int x, int y) {
		currentTutor.appear(x, y);
	}

	public void disappear() {
		currentTutor.disappear();
	}

	public void point() {
		currentTutor.point();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (!mThread.isAlive()) {
			mThread = new TutorialThread(this);
			mThread.setRunning(true);
			mThread.start();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {

		// simply copied from sample application LunarLander:
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		boolean retry = true;
		mThread.setRunning(false);
		while (retry) {
			try {
				mThread.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		canvas.drawPaint(paint);
		postInvalidate();
		tutor.draw(canvas);
		tutor_2.draw(canvas);
	}

	public void update() {
		tutor.update(System.currentTimeMillis());
		tutor_2.update(System.currentTimeMillis());
	}

}