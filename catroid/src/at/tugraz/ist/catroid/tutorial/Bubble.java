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
	private int currentLine = 0;
	private String[] textArray = new String[] { "", "", "", "" };
	private int x = 0;
	private int y = 0;

	private int minWidth;
	private int maxWidth;
	private int textSize;
	private boolean isBold;
	private boolean isAntiAliasing;
	private int textMarginY;
	private int bottomMargin;

	private boolean reset = false;
	private long endTimeBubble = 0;
	private boolean setEndTime = false;
	private int waitTime = 2000;
	private boolean waitForReset = false;
	private boolean holdBubble = false;
	private int updateTime = 110;
	private long lastUpdateTime = 0;

	public Bubble(String text, TutorialOverlay tutorialOverlay, SurfaceObjectTutor tutor, int x, int y) {
		minWidth = ScreenParameters.getInstance().getBubbleMinWidth();
		maxWidth = ScreenParameters.getInstance().getBubbleMaxWidth();
		textSize = ScreenParameters.getInstance().getBubbleTextSize();
		isBold = ScreenParameters.getInstance().isBubbleTextBold();
		isAntiAliasing = ScreenParameters.getInstance().isBubbleTextAliasing();
		bottomMargin = ScreenParameters.getInstance().getBubbleBottomMarginToText();

		this.tutor = tutor;
		this.text = text;
		this.x = x;
		this.y = y;
		tutorialOverlay.addSurfaceObject(this);
		this.tutorialOverlay = tutorialOverlay;

		bubbleBounds = new Rect();

		if (this.y > ScreenParameters.getInstance().getBubbleFlipDownMargin()) {
			if (this.tutor.tutorType == Tutor.CATRO) {
				speechBubble = (NinePatchDrawable) Tutorial.getInstance(null).getActualContext().getResources()
						.getDrawable(R.drawable.bubble_catro);
			} else {
				speechBubble = (NinePatchDrawable) Tutorial.getInstance(null).getActualContext().getResources()
						.getDrawable(R.drawable.bubble_miaus);
			}
			this.y -= ScreenParameters.getInstance().getyMarginBubbleUpToTutor();
			this.x -= ScreenParameters.getInstance().getxMarginBubbleUpToTutor();
			textMarginY = ScreenParameters.getInstance().getUpperBubbleTopMarginToText();
		} else {
			if (this.tutor.tutorType == Tutor.CATRO) {
				speechBubble = (NinePatchDrawable) Tutorial.getInstance(null).getActualContext().getResources()
						.getDrawable(R.drawable.bubble_catro_low);
			} else {
				speechBubble = (NinePatchDrawable) Tutorial.getInstance(null).getActualContext().getResources()
						.getDrawable(R.drawable.bubble_miaus_low);
			}
			this.y += ScreenParameters.getInstance().getyMarginBubbleDownToTutor();
			this.x -= ScreenParameters.getInstance().getxMarginBubbleDownToTutor();
			textMarginY = ScreenParameters.getInstance().getLowerBubbleTopMarginToText();

		}
		bubbleBounds.top = this.y;
		bubbleBounds.left = this.x;
		bubbleBounds.right = bubbleBounds.left + minWidth;
		speechBubble.setBounds(bubbleBounds);
	}

	@Override
	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setFakeBoldText(isBold);
		paint.setTextSize(textSize);
		paint.setAntiAlias(isAntiAliasing);

		int width = (int) paint.measureText(textArray[currentLine]);

		if (bubbleBounds.right < (bubbleBounds.left + width + ScreenParameters.getInstance()
				.getBubbleResizeWidthMargin())) {
			bubbleBounds.right = bubbleBounds.left + width
					+ ScreenParameters.getInstance().getBubbleResizeWidthMargin();
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
					paint.setFakeBoldText(isBold);
					paint.setTextSize(textSize);
					paint.setAntiAlias(isAntiAliasing);
					int width = (int) paint.measureText(textArray[currentLine]);

					if ((width > maxWidth && text.charAt(currentPosition) == ' ')
							|| (x + textSize + width > Tutorial.getInstance(null).getScreenWidth())) {

						if ((x + textSize + width <= Tutorial.getInstance(null).getScreenWidth())
								&& text.charAt(currentPosition) != ' ') {
							currentPosition++;
						}
						if (currentLine < 3) {
							currentLine++;

						} else {
							reset = true;
							waitForReset = true;
						}
					}

					if (reset) {
						resetBubble(System.currentTimeMillis() + waitTime);
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
			for (int i = 1; i < textArray.length; i++) {
				textArray[i - 1] = textArray[i];
			}
			textArray[3] = "";
			waitForReset = false;
			return;
		}
	}

	public void clearBubbleRemoveSurfaceObject() {
		tutorialOverlay.removeSurfaceObject(this);
	}

	public void setHoldBubble(boolean value) {
		holdBubble = value;
	}
}
