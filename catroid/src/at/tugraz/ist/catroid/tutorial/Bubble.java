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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;
import at.tugraz.ist.catroid.R;

/**
 * @author faxxe
 * 
 */
public class Bubble implements SurfaceObject {
	private String text = " So, donn woll !";
	private int currentPosition = 0;
	private int linePosition = 0;
	private int frames = 0;
	private NinePatchDrawable speechBubble;
	private TutorialOverlay tutorialOverlay;
	private SurfaceObjectTutor tutor;
	private Rect bounds;
	private String[] textArray = { "", "", "", "" };
	private int currentLine = 0;
	private int minWidth = 70;
	private int x = 0;
	private int y = 0;
	private int textSize = 16;
	private boolean reset = false;
	private long endTimeBubble = 0;
	private boolean setEndTime = false;

	public Bubble(String text, TutorialOverlay tutorialOverlay, SurfaceObjectTutor tutor, int x, int y) {
		this.tutor = tutor;
		this.text = text;
		this.x = x;
		this.y = y;
		tutorialOverlay.addSurfaceObject(this);
		this.tutorialOverlay = tutorialOverlay;
		speechBubble = (NinePatchDrawable) Tutorial.getInstance(null).getActualContext().getResources()
				.getDrawable(R.drawable.bubble);

		bounds = new Rect();
		bounds.top = y;
		bounds.left = x;
		bounds.right = bounds.left + minWidth;
		speechBubble.setBounds(bounds);
	}

	@Override
	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setFakeBoldText(true);
		paint.setTextSize(textSize);
		if (currentLine < 5) {
			if (bounds.right < bounds.left + 10 * textArray[currentLine].length()) {
				bounds.right = bounds.left + 10 * textArray[currentLine].length();
			}

			bounds.bottom = 70 + bounds.top + 14 * currentLine;
			speechBubble.setBounds(bounds);
			speechBubble.draw(canvas);

			for (int i = 0; i < textArray.length; i++) {
				if (textArray[i] != "") {
					canvas.drawText(textArray[i], x + textSize, y + 20 + i * textSize, paint);
				}
			}
		} else {

		}
	}

	@Override
	public void update(long gameTime) {
		frames++;
		if (currentPosition < text.length() && currentLine < textArray.length) {
			if (frames % 6 == 0) {
				if (linePosition > 15 && text.charAt(currentPosition) == ' ') {
					if (currentLine < 3) {
						currentLine++;
						currentPosition++;
					} else {
						currentLine = 0;
						currentPosition++;
						reset = true;
					}
					linePosition = 0;
				}

				if (reset) {
					resetTextArray();
					textArray[currentLine] = "" + text.charAt(currentPosition);
					reset = false;
				} else {
					textArray[currentLine] = textArray[currentLine] + text.charAt(currentPosition);
				}
				Log.i("drab", "TEXT: " + textArray[currentLine]);
				currentPosition++;
				linePosition++;
			}
		}

		if (currentPosition == text.length() && !setEndTime) {
			endTimeBubble = new Date().getTime();
			setEndTime = true;
		}

		long actTime = new Date().getTime();

		if ((endTimeBubble + 2000) < actTime && endTimeBubble != 0) {
			tutor.idle();
			tutorialOverlay.removeSurfaceObject(this);
			Tutorial.getInstance(null).setNotification("Bubble finished!");
		}
	}

	private void resetTextArray() {
		for (int i = 0; i < textArray.length; i++) {
			textArray[i] = "";
		}
	}
}
