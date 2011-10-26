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
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Display;
import at.tugraz.ist.catroid.R;

/**
 * @author Pinki
 * 
 */
public class TutorialControlPanel {

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

	Rect bounds;

	public TutorialControlPanel(Resources resources, Context context) {
		this.resources = resources;
		this.context = context;
		play = resources.getDrawable(R.drawable.play_tutorial);
		pause = resources.getDrawable(R.drawable.pause_tutorial);
		forward = resources.getDrawable(R.drawable.forward_tutorial);
		backward = resources.getDrawable(R.drawable.backwards_tutorial);
	}

	public void draw(Canvas canvas) {
		bounds = new Rect();

		//		bounds.left = (width - 260) / 2;
		//		bounds.right = bounds.left + 50;
		//		bounds.top = height - 70;
		//		bounds.bottom = height - 20;

		setBounds(0);

		play.setBounds(bounds);
		play.draw(canvas);

		setBounds(70);
		pause.setBounds(bounds);
		pause.draw(canvas);

		setBounds(140);
		forward.setBounds(bounds);
		forward.draw(canvas);

		setBounds(210);
		backward.setBounds(bounds);
		backward.draw(canvas);

	}

	private void setBounds(int shift) {
		int height = getScreenHeight();
		int width = getScreenWidth();
		//abstand zwischen buttons = 20

		bounds.left = ((width - 260) / 2) + shift;
		bounds.right = bounds.left + 50;
		bounds.top = height - 60;
		bounds.bottom = height - 10;

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
		Rect panelBounds = bounds;
		//3*20 = 60 ==> abstand zwischen buttons
		panelBounds.left = bounds.right - 4 * (bounds.right - bounds.left) - 60;
		return panelBounds;

	}

}
