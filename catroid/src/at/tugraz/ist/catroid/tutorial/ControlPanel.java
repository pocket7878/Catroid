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

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;
import android.view.Display;
import at.tugraz.ist.catroid.R;

/**
 * @author Pinki, Herb
 * 
 */
public class ControlPanel implements SurfaceObject {
	private Resources resources;
	private Context context;

	private NinePatchDrawable menuBar;
	private NinePatchDrawable menuButton;

	private Rect menuButtonBounds;
	private Rect menuBarBounds;

	public static boolean active;
	private boolean open;
	private long waitTimeToDispatch = 500;
	private long timeOfLastChange = 0;

	private double scaleDifference = 0;

	private double[] stopPosition = { 241, 273 };
	private double[] pausePosition = { 79, 111 };
	private double[] backwardPosition = { 129, 167 };
	private double[] forwardPosition = { 185, 223 };

	public ControlPanel(Context context, TutorialOverlay tutorialOverlay) {
		active = true;

		this.resources = ((Activity) context).getResources();
		this.context = context;

		tutorialOverlay.addSurfaceObject(this);

		menuBar = (NinePatchDrawable) resources.getDrawable(R.drawable.tutorial_menu_bar);
		menuButton = (NinePatchDrawable) resources.getDrawable(R.drawable.tutorial_menu_button);

		menuBarBounds = new Rect();
		menuBarBounds.bottom = getScreenHeight();
		menuBarBounds.top = getScreenHeight() - menuBar.getIntrinsicHeight();
		menuBarBounds.right = getScreenWidth();
		menuBarBounds.left = 2;

		menuBar.setBounds(menuBarBounds);

		menuButtonBounds = new Rect();
		menuButtonBounds.bottom = getScreenHeight();
		menuButtonBounds.top = getScreenHeight() - menuButton.getIntrinsicHeight();
		menuButtonBounds.right = 64;
		menuButtonBounds.left = 2;

		menuButton.setBounds(menuButtonBounds);

		scaleDifference = ((double) menuBar.getIntrinsicWidth() - 300.0f) / 4.0f;

		pausePosition[0] = pausePosition[0] + scaleDifference * 1.0f;
		pausePosition[1] = pausePosition[1] + scaleDifference * 1.0f;

		backwardPosition[0] = backwardPosition[0] + scaleDifference * 2.0f;
		backwardPosition[1] = backwardPosition[1] + scaleDifference * 2.0f;

		forwardPosition[0] = forwardPosition[0] + scaleDifference * 3.0f;
		forwardPosition[1] = forwardPosition[1] + scaleDifference * 3.0f;

		stopPosition[0] = stopPosition[0] + scaleDifference * 4.0f;
		stopPosition[1] = stopPosition[1] + scaleDifference * 4.0f;

		open = false;
	}

	@Override
	public void draw(Canvas canvas) {
		new Paint();
		if (open) {
			menuBar.draw(canvas);
		} else {
			menuButton.draw(canvas);
		}
	}

	public boolean isOpen() {
		return open;
	}

	public void open() {
		open = true;
	}

	public void close() {
		open = false;
	}

	private int getScreenHeight() {
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
		int screenHeight = display.getHeight();
		return screenHeight;
	}

	private int getScreenWidth() {
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
		int screenWidth = display.getWidth();
		return screenWidth;
	}

	public boolean isReadyToDispatch() {
		long actTime = new Date().getTime();
		Log.i("drab", "(actTime - waitTimeToDispatch) > timeOfLastChange = " + (actTime - waitTimeToDispatch) + " and "
				+ timeOfLastChange);
		if ((actTime - waitTimeToDispatch) > timeOfLastChange) {
			timeOfLastChange = actTime;
			return true;
		}
		return false;
	}

	//	public void pressPlay() {
	//		//		//check if tutorial is active, if so there is no need to press play
	//		//		//otherwise resume the tutorial where it was stopped
	//		//		active = true;
	//		//		//		tut.setNotification("BubblePlay");
	//		//		Toast toast = Toast.makeText(context, "PLAY", Toast.LENGTH_SHORT);
	//		//		toast.show();
	//
	//	}
	//
	//	public void pressPause() throws InterruptedException {
	//		//		active = false;
	//		//		//Toast toast = Toast.makeText(context, "PAUSE", Toast.LENGTH_SHORT);
	//		//		//toast.show();
	//		//		//		tut.waitForNotification("BubblePlay");
	//
	//	}
	//
	//	public void pressForward() {
	//		//		Toast toast = Toast.makeText(context, "FORWARD", Toast.LENGTH_SHORT);
	//		//		toast.show();
	//	}
	//
	//	public void pressBackward() {
	//		//		Toast toast = Toast.makeText(context, "BACKWARD", Toast.LENGTH_SHORT);
	//		//		toast.show();
	//	}

	public NinePatchDrawable getMenuBar() {
		return menuBar;
	}

	public NinePatchDrawable getMenuButton() {
		return menuButton;
	}

	public double[] getPausePosition() {
		return pausePosition;
	}

	public double[] getStopPosition() {
		return stopPosition;
	}

	public double[] getBackwardPosition() {
		return backwardPosition;
	}

	public double[] getForwardPosition() {
		return forwardPosition;
	}

	@Override
	public void update(long gameTime) {
		// TODO Auto-generated method stub

	}

}
