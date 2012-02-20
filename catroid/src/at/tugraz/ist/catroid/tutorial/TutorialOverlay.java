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

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
	ControlPanel panel;
	Context context;
	Cloud cloud;

	ClickDispatcher clickDispatcher;

	private AnimationThread mThread;

	/**
	 * @param context
	 */
	public TutorialOverlay(Context context, Tutor tutor, Tutor tutor_2) {
		super(context);
		this.setBackgroundColor(Color.TRANSPARENT);
		this.setZOrderOnTop(true); //necessary
		getHolder().setFormat(PixelFormat.TRANSPARENT);
		this.tutor = tutor;
		this.tutor_2 = tutor_2;

		currentTutor = tutor;
		panel = new ControlPanel(getResources(), context);
		clickDispatcher = new ClickDispatcher(context, panel);
		getHolder().addCallback(this);
		mThread = new AnimationThread(this);
		this.context = context;
		cloud = Cloud.getInstance(context);
	}

	public ClickDispatcher getClickDispatcher() {
		return clickDispatcher;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Activity activity = (Activity) context;
		boolean retval = false;
		if (panel.isOpen()) {
			panel.close();
		}
		float displayHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
		//		clickDispatcher.dispatchEvent(ev);
		int[] clickableArea = Cloud.getInstance(null).getClickableArea();
		if (ev.getY() > displayHeight - 100) {
			dispatchPanel(ev, displayHeight);
		}
		if (clickableArea[0] == 0 && clickableArea[1] == 0) {
			return false;
		}
		if (isEVinArea(clickableArea, ev)) {
			retval = Tutorial.getInstance(null).dispatchTouchEvent(ev);
		}
		return retval;
	}

	public void dispatchPanel(MotionEvent ev, float displayHeight) {
		//TODO: Dispatch ALL the Panel!

		double x1 = 0;
		double x2 = ev.getX();
		double y1 = displayHeight;
		double y2 = ev.getY();
		double diffx = ev.getX();
		double diffy = y1 - y2;
		double distance = Math.sqrt(diffx * diffx + diffy * diffy);
		if (distance < 80 && panel != null) {
			if (!panel.isOpen()) {
				panel.open();
			} else {
				panel.close();
			}
		} else if (ev.getY() > displayHeight - 50) {
			Tutorial.getInstance(null).stopButtonTutorial();
		}
	}

	public boolean isEVinArea(int[] area, MotionEvent ev) {
		float x = ev.getX();
		float y = ev.getY();
		float diffx = abs(area[0] - x);
		float diffy = abs(area[1] - y);
		if (diffx < area[2] && diffy < area[2]) {
			return true;
		}

		return false;
	}

	public float abs(float fl) {
		return (fl > 0) ? fl : fl * -1;
	}

	public void switchToDog() {
		currentTutor = tutor_2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#dispatchKeyEvent(android.view.KeyEvent)
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		Log.i("faxxe", "dispatchKeyEvent");
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#dispatchKeyEventPreIme(android.view.KeyEvent)
	 */
	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {
		// TODO Auto-generated method stub
		Log.i("faxxe", "dispatchKeyEvent");
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#dispatchKeyShortcutEvent(android.view.KeyEvent)
	 */
	@Override
	public boolean dispatchKeyShortcutEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return super.dispatchKeyShortcutEvent(event);
	}

	public void switchToCat() {
		currentTutor = tutor;
	}

	public void flip() {
		currentTutor.flip();
	}

	public void idle() {
		tutor.idle();
		tutor_2.idle();
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

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// DEBUG, is nur a test de line
		try {
			//			Thread.sleep(10);
			Thread.yield();
		} catch (Exception e) {

		}

		if (!mThread.isAlive()) {
			mThread = new AnimationThread(this);
			mThread.setRunning(true);
			mThread.setName("AnimationThread");
			mThread.start();
		}
	}

	@Override
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
		cloud.draw(canvas);
		tutor.draw(canvas);
		tutor_2.draw(canvas);
		panel.draw(canvas);
	}

	public void update() {
		tutor.update(System.currentTimeMillis());
		tutor_2.update(System.currentTimeMillis());
		cloud.update(System.currentTimeMillis());
	}

}