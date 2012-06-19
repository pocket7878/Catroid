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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
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

/**
 * @author faxxe
 * 
 */
public class TutorialOverlay extends SurfaceView implements SurfaceHolder.Callback {
	private Context context;
	//	private ArrayList<SurfaceObject> surfaceObjects;
	List<SurfaceObject> surfaceObjects = Collections.synchronizedList(new ArrayList<SurfaceObject>());
	//	private AnimationThread animationThread;
	private Cloud cloud;
	private CloudController co;
	private ControlPanel panel;

	@Override
	protected void finalize() throws Throwable {
		getHolder().removeCallback(this);
		surfaceObjects.clear();
		surfaceObjects = null;
		cloud = null;
		co = null;
		super.finalize();

	};

	public void playIntro(SurfaceObject intro) {

	}

	public void clean() {
		getHolder().removeCallback(this);
		surfaceObjects.clear();
		surfaceObjects = null;
		cloud = null;
		co = null;
		panel = null;
		context = null;
	}

	public TutorialOverlay(Context context) {
		super(context);
		this.context = context;
		this.setBackgroundColor(Color.TRANSPARENT);
		this.setZOrderOnTop(true); //necessary
		getHolder().setFormat(PixelFormat.TRANSPARENT);
		getHolder().addCallback(this);
		surfaceObjects = new ArrayList<SurfaceObject>();
		panel = new ControlPanel(context, this);
		co = new CloudController();
	}

	@Override
	public void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		canvas.drawPaint(paint);
		postInvalidate();
		if (cloud == null) {
			cloud = Cloud.getInstance(getContext());
		}
		cloud.draw(canvas);
		if (surfaceObjects != null) {
			synchronized (surfaceObjects) {
				for (SurfaceObject tmp : surfaceObjects) {
					if (tmp != null) {
						tmp.update(System.currentTimeMillis());
						tmp.draw(canvas);
					}
				}
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		getHolder().addCallback(this);
		cloud = Cloud.getInstance(context);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		getHolder().addCallback(this);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		getHolder().removeCallback(this);
	}

	public void addSurfaceObject(SurfaceObject surfaceObject) {
		if (!surfaceObjects.contains(surfaceObject)) {
			synchronized (surfaceObjects) {
				surfaceObjects.add(surfaceObject);
			}
		}
	}

	public void removeSurfaceObject(SurfaceObject surfaceViewObject) {
		if (surfaceObjects.contains(surfaceViewObject)) {
			synchronized (surfaceObjects) {
				surfaceObjects.remove(surfaceViewObject);
			}
		}
	}

	public void addCloud(Cloud cloud) {
		this.cloud = cloud;
	}

	public void removeCloud() {
		this.cloud = null;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Activity activity = (Activity) context;
		boolean retval = false;

		float displayHeight = activity.getWindowManager().getDefaultDisplay().getHeight();

		ClickableArea clickableArea = Cloud.getInstance(null).getClickableArea();

		if (ev.getY() > displayHeight - panel.getMenuButton().getIntrinsicHeight() && panel != null) {
			dispatchPanel(ev, displayHeight);
		}

		if (clickableArea == null || clickableArea.x == 0 && clickableArea.y == 0) {
			return false;
		}

		if (isEVinArea(clickableArea, ev)) {
			retval = Tutorial.getInstance(null).dispatchTouchEvent(ev);
			co.disapear();
		}
		return retval;
	}

	public void dispatchPanel(MotionEvent ev, float displayHeight) {
		//TODO: Dispatch ALL the Panel!

		if (panel.isReadyToDispatch()) {
			if (isOnStopButton(ev)) {
				Tutorial.getInstance(null).stopButtonTutorial();
				Log.i("drab", "DISPATCH Stop");
			} else if (isOnPauseButton(ev)) {
				//Tutorial.getInstance(null).pauseTutorial();
				Log.i("drab", "DISPATCH Pause");
			} else if (isOnButton(ev)) {
				if (panel.isOpen()) {
					panel.close();
				} else {
					panel.open();
				}
				Log.i("drab", "DISPATCH Button open/close");
			} else if (isOnBackwardButton(ev)) {
				panel.pressBackward();
				Tutorial.getInstance(null).rewindStep();
				Log.i("drab", "DISPATCH Button rewind");
			} else if (isOnForwardButton(ev)) {
				panel.pressForward();
				//TODO: implement foward step
				//Tutorial.getInstance(null).rewindStep();
				Log.i("drab", "DISPATCH Button rewind");
			}

		} else {
			Log.i("drab", "Not ready to DISPATCH!");
		}
	}

	private boolean isOnPauseButton(MotionEvent ev) {
		// TODO Auto-generated method stub
		double[] pausePosition = panel.getPausePosition();
		if (ev.getX() > pausePosition[0] && ev.getX() < pausePosition[1]) {
			return true;
		}
		return false;
	}

	private boolean isOnStopButton(MotionEvent ev) {
		// TODO Auto-generated method stub
		double[] stopPosition = panel.getStopPosition();
		if (ev.getX() > stopPosition[0] && ev.getX() < stopPosition[1]) {
			return true;
		}
		return false;
	}

	private boolean isOnButton(MotionEvent ev) {
		if (ev.getX() < panel.getMenuButton().getIntrinsicWidth()) {
			return true;
		}
		return false;
	}

	private boolean isOnBackwardButton(MotionEvent ev) {
		double[] backwardPosition = panel.getBackwardPosition();
		if (ev.getX() > backwardPosition[0] && ev.getX() < backwardPosition[1]) {
			return true;
		}
		return false;
	}

	private boolean isOnForwardButton(MotionEvent ev) {
		double[] forwardPosition = panel.getForwardPosition();
		if (ev.getX() > forwardPosition[0] && ev.getX() < forwardPosition[1]) {
			return true;
		}
		return false;
	}

	public boolean isEVinArea(ClickableArea area, MotionEvent ev) {
		float x = ev.getX();
		float y = ev.getY();

		if (x > area.x && x < area.x + area.width && y > area.y && y < area.y + area.height) {
			return true;
		}
		return false;
	}

	public float abs(float fl) {
		return (fl > 0) ? fl : fl * -1;
	}

}
