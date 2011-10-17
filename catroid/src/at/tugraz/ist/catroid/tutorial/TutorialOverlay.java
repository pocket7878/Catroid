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
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;

/**
 * @author User
 * 
 */
public class TutorialOverlay extends SurfaceView implements SurfaceHolder.Callback {

	Tutor tutor;
	Tutor tutor_2;
	Tutor currentTutor;
	Context context;

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
		this.context = context;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		Activity dings = (Activity) context;
		if (dings.getLocalClassName().compareTo("ui.MainMenuActivity") == 0) {
			return dispatchMainMenu(ev);
		} else if (dings.getLocalClassName().compareTo("ui.ProjectActivity") == 0) {
			return dispatchProject(ev);
		} else if (dings.getLocalClassName().compareTo("ui.ScriptActivit") == 0) {
			return dispatchSkript(ev);
		}
		return false;
	}

	public boolean dispatchSkript(MotionEvent ev) {

		return true;
	}

	public boolean dispatchProject(MotionEvent ev) {
		ListActivity test = (ListActivity) context;
		ListView livi = test.getListView();
		int y = livi.getChildAt(0).getTop();
		ev.setLocation(ev.getX(), ev.getY() - 100); // please anyone find out the real height of the titlebar!
		int x = livi.getChildAt(0).getLeft();
		int maxx = livi.getChildAt(0).getRight();
		int maxy = livi.getChildAt(0).getBottom();
		Log.i("faxxe", "touched!" + x + " " + y + " " + maxx + " " + maxy + " " + ev.getX() + " " + ev.getY());

		if (ev.getX() < maxx && ev.getX() > x && ev.getY() < maxy && ev.getY() > y) {
			Log.i("faxxe", "irgendwos" + x + " " + y + " " + maxx + " " + maxy + " " + ev.getX() + " " + ev.getY());
			livi.dispatchTouchEvent(ev);
		}

		return true;
	}

	public boolean dispatchMainMenu(MotionEvent ev) {
		Activity dings = (Activity) context;
		Button teifl = (Button) dings.findViewById(R.id.current_project_button);
		Button teifl1 = (Button) dings.findViewById(R.id.tutorial_button);
		int location[] = new int[2];
		int location1[] = new int[2];
		teifl.getLocationOnScreen(location);
		teifl1.getLocationOnScreen(location1);
		int width = teifl.getWidth();
		int height = teifl.getHeight();
		int width1 = teifl1.getWidth();
		int height1 = teifl1.getHeight();

		if (ev.getX() > location[0] && ev.getX() < location[0] + width && ev.getY() > location[1]
				&& ev.getY() < location[1] + height) {
			dings.dispatchTouchEvent(ev);
			Log.i("faxxe", "clicked: " + ev.getX() + " " + ev.getY());
		}
		if (ev.getX() > location1[0] && ev.getX() < location1[0] + width1 && ev.getY() > location1[1]
				&& ev.getY() < location1[1] + height1) {
			dings.dispatchTouchEvent(ev);
			Log.i("faxxe", "clicked: " + ev.getX() + " " + ev.getY());
		}
		return true;
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