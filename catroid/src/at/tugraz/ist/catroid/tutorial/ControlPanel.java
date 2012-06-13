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
import android.view.Display;
import android.widget.Toast;
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
	private long timeOfLastChange = 0;

	public ControlPanel(Context context, TutorialOverlay tutorialOverlay) {
		active = true;

		this.resources = ((Activity) context).getResources();
		this.context = context;

		tutorialOverlay.addSurfaceObject(this);

		menuBar = (NinePatchDrawable) resources.getDrawable(R.drawable.tutmenubar);
		menuButton = (NinePatchDrawable) resources.getDrawable(R.drawable.circle);

		menuBarBounds = new Rect();
		menuBarBounds.bottom = getScreenHeight() - 5;
		menuBarBounds.top = getScreenHeight() - 55;
		menuBarBounds.right = getScreenWidth();
		menuBarBounds.left = 0;

		menuBar.setBounds(menuBarBounds);

		menuButtonBounds = new Rect();
		menuButtonBounds.bottom = getScreenHeight() - 5;
		menuButtonBounds.left = 0;
		menuButtonBounds.top = getScreenHeight() - 55;
		menuButtonBounds.right = 55;

		menuButton.setBounds(menuButtonBounds);

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
		long actTime = new Date().getTime();
		if ((actTime - 500) > timeOfLastChange) {
			open = true;
			timeOfLastChange = actTime;
		}
	}

	public void close() {
		long actTime = new Date().getTime();
		if ((actTime - 500) > timeOfLastChange) {
			open = false;
			timeOfLastChange = actTime;
		}
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

	public void pressPlay() {
		//check if tutorial is active, if so there is no need to press play
		//otherwise resume the tutorial where it was stopped
		active = true;
		//		tut.setNotification("BubblePlay");
		Toast toast = Toast.makeText(context, "PLAY", Toast.LENGTH_SHORT);
		toast.show();

	}

	public void pressPause() throws InterruptedException {
		active = false;
		//Toast toast = Toast.makeText(context, "PAUSE", Toast.LENGTH_SHORT);
		//toast.show();
		//		tut.waitForNotification("BubblePlay");

	}

	public void pressForward() {
		Toast toast = Toast.makeText(context, "FORWARD", Toast.LENGTH_SHORT);
		toast.show();
	}

	public void pressBackward() {
		Toast toast = Toast.makeText(context, "BACKWARD", Toast.LENGTH_SHORT);
		toast.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.tugraz.ist.catroid.tutorial.SurfaceObject#update(long)
	 */
	@Override
	public void update(long gameTime) {
		// TODO Auto-generated method stub

	}

}
