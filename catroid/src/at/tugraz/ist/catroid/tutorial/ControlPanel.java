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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;

/**
 * @author Pinki
 * 
 */
public class ControlPanel {

	//ein paar gedanken zum control panel
	//play / pause panel je nach status abwechseln
	//wird es ausgeführt nur pause button anzeigen
	//sind wir im pause modus playbutton stattdessen anzeigen
	//pause bedeutet bleibt an der jeweiligen zeile stehen und wartet auf play
	//forward wird geschwindigkeit des textes verdoppelt
	//rewind bedeutet rückwärts schreiben ==> wird evt. tricky

	Resources resources;
	Context context;

	Drawable play;
	Drawable pause;
	Drawable forward;
	Drawable backward;
	Drawable circle;
	Bitmap playBitmap;
	Bitmap pauseBitmap;
	Bitmap forwardBitmap;
	Bitmap backwardBitmap;
	Bitmap menuBitmap;

	Rect bounds;
	Rect menuBounds;
	public static boolean active;
	private boolean open;
	Tutorial tut;

	public ControlPanel(Resources resources, Context context) {
		active = true;
		tut = Tutorial.getInstance(context);
		this.resources = resources;
		this.context = context;
		play = resources.getDrawable(R.drawable.play_tutorial);
		pause = resources.getDrawable(R.drawable.pause_tutorial);
		forward = resources.getDrawable(R.drawable.forward_tutorial);
		backward = resources.getDrawable(R.drawable.button_bar_tutorial_stop);
		circle = resources.getDrawable(R.drawable.panel_circle);

		bounds = new Rect();
		menuBounds = new Rect();

		playBitmap = ((BitmapDrawable) play).getBitmap();
		pauseBitmap = ((BitmapDrawable) pause).getBitmap();
		forwardBitmap = ((BitmapDrawable) forward).getBitmap();
		backwardBitmap = ((BitmapDrawable) backward).getBitmap();
		menuBitmap = ((BitmapDrawable) circle).getBitmap();
		open = false;
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		if (open) {
			setMenuBounds();
			canvas.drawBitmap(backwardBitmap, menuBounds.left, getScreenHeight() - backwardBitmap.getHeight(), paint);
		} else {
			setMenuBounds();
			canvas.drawBitmap(menuBitmap, menuBounds.left, menuBounds.top, paint);
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

	private void setMenuBounds() {
		menuBounds.left = 0;
		menuBounds.right = menuBitmap.getWidth();
		menuBounds.bottom = getScreenHeight();
		menuBounds.top = getScreenHeight() - menuBitmap.getHeight();
	}

	private void setBounds(int shift) {
		int height = getScreenHeight();
		int width = getScreenWidth();
		//abstand zwischen buttons = 20

		bounds.left = ((width - 260) / 2) + shift;
		bounds.right = bounds.left + 50;
		bounds.top = height - 40;
		bounds.bottom = height;// - 10;

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

	public Rect getPanelBounds() {
		//coordinaten von gesamten panel mit 4 button
		Rect panelBounds = new Rect();
		panelBounds.bottom = bounds.bottom;
		panelBounds.top = bounds.top;
		panelBounds.right = bounds.right;
		//3*20 = 60 ==> abstand zwischen buttons
		panelBounds.left = bounds.right - 4 * (bounds.right - bounds.left) - 60;
		return panelBounds;

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

}
