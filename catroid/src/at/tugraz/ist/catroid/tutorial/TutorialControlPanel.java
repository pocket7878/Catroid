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
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import at.tugraz.ist.catroid.R;

/**
 * @author Pinki
 * 
 */
public class TutorialControlPanel {

	Resources resources;
	Context context;

	Drawable play;

	public TutorialControlPanel(Resources resources, Context context) {
		this.resources = resources;
		this.context = context;
		play = resources.getDrawable(R.drawable.play_tutorial);
	}

	public void draw(Canvas canvas) {
		Rect bounds = new Rect();
		bounds.left = 150;
		bounds.right = 190;
		bounds.top = 740;
		bounds.bottom = 780;

		play.setBounds(bounds);
		play.draw(canvas);

	}

}
