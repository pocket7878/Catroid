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
package at.tugraz.ist.catroid.stage;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import at.tugraz.ist.catroid.content.WhenScript;

/**
 * @author DENISE
 * 
 */
public class SimpleGestureFilter extends SimpleOnGestureListener {

	public final static int SWIPE_UP = 1;
	public final static int SWIPE_DOWN = 2;
	public final static int SWIPE_LEFT = 3;
	public final static int SWIPE_RIGHT = 4;

	public final static int MODE_TRANSPARENT = 0;
	public final static int MODE_SOLID = 1;
	public final static int MODE_DYNAMIC = 2;

	private final static int ACTION_FAKE = -13; //just an unlikely number
	private final static int SWIPE_MIN_DISTANCE = 100;
	private final static int SWIPE_MAX_DISTANCE = 500;
	private final static int SWIPE_MIN_VELOCITY = 100;

	private int mode = MODE_DYNAMIC;
	private boolean running = true;
	private boolean tapIndicator = false;

	private StageActivity context;
	private GestureDetector detector;
	private SimpleGestureListener listener;

	public SimpleGestureFilter(StageActivity stageActivity, SimpleGestureListener sgl) {

		this.context = stageActivity;
		this.detector = new GestureDetector(context, this);
		this.listener = sgl;
	}

	public void onTouchEvent(MotionEvent event) {

		if (!this.running) {
			return;
		}

		boolean result = this.detector.onTouchEvent(event);

		if (this.mode == MODE_SOLID) {
			event.setAction(MotionEvent.ACTION_CANCEL);
		} else if (this.mode == MODE_DYNAMIC) {

			if (event.getAction() == ACTION_FAKE) {
				event.setAction(MotionEvent.ACTION_UP);
			} else if (result) {
				event.setAction(MotionEvent.ACTION_CANCEL);
			} else if (this.tapIndicator) {
				event.setAction(MotionEvent.ACTION_DOWN);
				this.tapIndicator = false;
			}

		}
		//else just do nothing, it's Transparent
	}

	public void setMode(int m) {
		this.mode = m;
	}

	public int getMode() {
		return this.mode;
	}

	public void setEnabled(boolean status) {
		this.running = status;
	}

	@Override
	public boolean onFling(MotionEvent firstDownEvent, MotionEvent secondDownEvent, float velocityX, float velocityY) {

		final float xDistance = Math.abs(firstDownEvent.getX() - secondDownEvent.getX());
		final float yDistance = Math.abs(firstDownEvent.getY() - secondDownEvent.getY());

		if (xDistance > SWIPE_MAX_DISTANCE || yDistance > SWIPE_MAX_DISTANCE) {
			return false;
		}

		velocityX = Math.abs(velocityX);
		velocityY = Math.abs(velocityY);
		boolean result = false;

		if (velocityX > SWIPE_MIN_VELOCITY && xDistance > SWIPE_MIN_DISTANCE) {
			if (firstDownEvent.getX() > secondDownEvent.getX()) {
				this.listener.onSwipe(SWIPE_LEFT);
				context.processOnTouch((int) firstDownEvent.getX(), (int) firstDownEvent.getY(), WhenScript.SWIPELEFT);
			} else {
				this.listener.onSwipe(SWIPE_RIGHT);
				context.processOnTouch((int) firstDownEvent.getX(), (int) firstDownEvent.getY(), WhenScript.SWIPERIGHT);
			}

			result = true;
		} else if (velocityY > SWIPE_MIN_VELOCITY && yDistance > SWIPE_MIN_DISTANCE) {
			if (firstDownEvent.getY() > secondDownEvent.getY()) {
				this.listener.onSwipe(SWIPE_UP);
				context.processOnTouch((int) firstDownEvent.getX(), (int) firstDownEvent.getY(), WhenScript.SWIPEUP);
			} else {
				this.listener.onSwipe(SWIPE_DOWN);
				context.processOnTouch((int) firstDownEvent.getX(), (int) firstDownEvent.getY(), WhenScript.SWIPEDOWN);
			}

			result = true;
		}

		return result;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent upEvent) {
		this.tapIndicator = true;
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent doubleTap) {
		this.listener.onDoubleTap();
		context.processOnTouch((int) doubleTap.getX(), (int) doubleTap.getY(), WhenScript.DOUBLETAPPED);
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent arg0) {
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent singleTap) {
		this.listener.onSingleTouch();
		if (this.mode == MODE_DYNAMIC) { // we owe an ACTION_UP, so we fake an       
			singleTap.setAction(ACTION_FAKE); //action which will be converted to an ACTION_UP later.                                    
			this.context.dispatchTouchEvent(singleTap);
			context.processOnTouch((int) singleTap.getX(), (int) singleTap.getY(), WhenScript.TAPPED);
			context.processOnTouch((int) singleTap.getX(), (int) singleTap.getY(), WhenScript.TOUCHINGSTOPS);
		}

		return false;
	}

	@Override
	public void onLongPress(MotionEvent longPress) {
		this.listener.onLongPress();
		context.processOnTouch((int) longPress.getX(), (int) longPress.getY(), WhenScript.LONGPRESSED);
	}

	static interface SimpleGestureListener {
		void onSwipe(int direction);

		void onDoubleTap();

		void onSingleTouch();

		void onLongPress();
	}

}