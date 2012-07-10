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
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.tutorial.tasks.Task.Tutor;

/**
 * @author faxxe
 * 
 */
public class Bubble implements SurfaceObject {
	private String text = new String();
	private int currentPosition = 0;
	private NinePatchDrawable speechBubble;
	private TutorialOverlay tutorialOverlay;
	private SurfaceObjectTutor tutor;
	private Rect bubbleBounds;

	private String[] textArray = new String[] { "", "", "", "" };
	private int currentLine = 0;
	private int minWidth = 80;
	private int maxWidth = 160;

	private int x = 0;
	private int y = 0;
	private int textSize = 16;
	private boolean isBold = false;
	private boolean isAntiAliasing = false;

	private int textMarginY = 0;
	private int bottomMargin = 0;

	private int updateTime = 110;
	private long lastUpdateTime = 0;

	private boolean reset = false;
	private long endTimeBubble = 0;
	private boolean setEndTime = false;
	private int waitTime = 1000;
	private boolean waitForReset = false;

	private boolean holdBubble = false;

	public Bubble(String text, TutorialOverlay tutorialOverlay, SurfaceObjectTutor tutor, int x, int y) {

		setTextSizeAndMarginForDisplayDensity(Tutorial.getInstance(null).getDensity());

		this.tutor = tutor;
		this.text = text;
		this.x = x;
		this.y = y;
		tutorialOverlay.addSurfaceObject(this);
		this.tutorialOverlay = tutorialOverlay;

		bubbleBounds = new Rect();

		if (this.y > 200) {
			if (this.tutor.tutorType == Tutor.CATRO) {
				speechBubble = (NinePatchDrawable) Tutorial.getInstance(null).getActualContext().getResources()
						.getDrawable(R.drawable.bubble_catro);
			} else {
				speechBubble = (NinePatchDrawable) Tutorial.getInstance(null).getActualContext().getResources()
						.getDrawable(R.drawable.bubble_miaus);
			}
			this.y -= 90;
			this.x -= 20;
			textMarginY = 20;
		} else {
			if (this.tutor.tutorType == Tutor.CATRO) {
				speechBubble = (NinePatchDrawable) Tutorial.getInstance(null).getActualContext().getResources()
						.getDrawable(R.drawable.bubble_catro_low);
			} else {
				speechBubble = (NinePatchDrawable) Tutorial.getInstance(null).getActualContext().getResources()
						.getDrawable(R.drawable.bubble_miaus_low);
			}
			this.y += 110;
			this.x -= 20;
			textMarginY = 45;
		}
		bubbleBounds.top = this.y;
		bubbleBounds.left = this.x;
		bubbleBounds.right = bubbleBounds.left + minWidth;
		speechBubble.setBounds(bubbleBounds);
	}

	private void setTextSizeAndMarginForDisplayDensity(float density) {
		if (density < 1.0f) { //ldpi
			textSize = 10;
			isBold = false;
			isAntiAliasing = true;
			bottomMargin = 20;
		} else if (density == 1.0f) { //mdpi
			textSize = 13;
			isBold = false;
			bottomMargin = 40;
		} else if (density == 1.5f) { //hdpi
			textSize = 16;
			isBold = false;
			bottomMargin = 70;
		} else { //xdpi
			textSize = 19;
			isBold = false;
			bottomMargin = 90;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setFakeBoldText(isBold);
		paint.setTextSize(textSize);
		paint.setAntiAlias(isAntiAliasing);

		int width = (int) paint.measureText(textArray[currentLine]);

		if (bubbleBounds.right < bubbleBounds.left + width + 40) {
			bubbleBounds.right = bubbleBounds.left + width + 40;
		}

		bubbleBounds.bottom = bottomMargin + bubbleBounds.top + textSize * currentLine;

		speechBubble.setBounds(bubbleBounds);
		speechBubble.draw(canvas);

		if (!holdBubble) {
			for (int i = 0; i < textArray.length; i++) {
				if (textArray[i] != "") {
					canvas.drawText(textArray[i], x + textSize, y + textMarginY + (i * textSize), paint);
				}
			}
		} else {
			for (int i = 0; i < textArray.length; i++) {
				if (textArray[i] != "") {
					canvas.drawText(textArray[i], x + textSize, y + textMarginY + (i * textSize), paint);
				}
			}
		}
	}

	@Override
	public void update(long gameTime) {
		if (!holdBubble) {
			if (currentPosition < text.length() && currentLine < textArray.length) {
				if ((lastUpdateTime + updateTime) < gameTime && !waitForReset) {

					Paint paint = new Paint();
					paint.setFakeBoldText(true);
					paint.setTextSize(textSize);
					paint.setAntiAlias(isAntiAliasing);
					int width = (int) paint.measureText(textArray[currentLine]);

					if ((width > maxWidth && text.charAt(currentPosition) == ' ')
							|| (x + textSize + width + 25) > tutorialOverlay.getScreenWidth()) {

						if ((x + textSize + width + 25) <= tutorialOverlay.getScreenWidth()
								&& text.charAt(currentPosition) != ' ') {
							currentPosition++;
						}
						if (currentLine < 3) {
							currentLine++;

						} else {
							currentLine = 0;
							reset = true;
							waitForReset = true;
						}
					}

					if (reset) {
						resetBubble(new Date().getTime() + waitTime);
						textArray[currentLine] = "" + text.charAt(currentPosition);
						reset = false;
					} else {
						textArray[currentLine] = textArray[currentLine] + text.charAt(currentPosition);
					}
					lastUpdateTime = gameTime;
					currentPosition++;
				}
			}

			if (currentPosition == text.length() && !setEndTime) {
				endTimeBubble = gameTime;
				setEndTime = true;
			}

			if ((endTimeBubble + waitTime) < gameTime && endTimeBubble != 0) {
				tutor.idle();
				tutorialOverlay.removeSurfaceObject(this);
				Tutorial.getInstance(null).setNotification("Bubble finished!");
			}
		}
	}

	private void resetBubble(long time) {
		while (true) {
			long actTime = new Date().getTime();
			if (actTime > time) {
				for (int i = 0; i < textArray.length; i++) {
					textArray[i] = "";
				}
				bubbleBounds.top = this.y;
				bubbleBounds.left = this.x;
				bubbleBounds.right = bubbleBounds.left + minWidth;
				speechBubble.setBounds(bubbleBounds);
				waitForReset = false;
				return;
			}
		}
	}

	public void clearBubbleRemoveSurfaceObject() {
		tutorialOverlay.removeSurfaceObject(this);
	}

	public void setHoldBubble(boolean value) {
		holdBubble = value;
	}
}
