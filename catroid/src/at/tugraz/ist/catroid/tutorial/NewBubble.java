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

import android.graphics.Bitmap;
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
public class NewBubble implements SurfaceObject {
	private String text = " So, donn woll !";
	private int currentPosition = 0;
	private int frames = 0;
	private NinePatchDrawable speechBubble;
	private TutorialOverlay tutorialOverlay;
	private SurfaceObjectTutor tutor;
	private Bitmap bubble;
	private Rect bounds;
	private String textToDraw = "";
	private String[] textArray = { "", "", "", "", "", "", "", "", "" };
	private int currentLine = 0;

	public NewBubble(String text, TutorialOverlay tutorialOverlay, SurfaceObjectTutor tutor) {
		this.tutor = tutor;
		this.text = text;
		Log.i("faxxe", "text to draw: " + text);
		tutorialOverlay.addSurfaceObject(this);
		this.tutorialOverlay = tutorialOverlay;
		speechBubble = (NinePatchDrawable) Tutorial.getInstance(null).getActualContext().getResources()
				.getDrawable(R.drawable.speech_bubble);
		bounds = new Rect();
		bounds.top = 80;
		bounds.left = 80;
		bounds.bottom = 300;
		bounds.right = 150;
		speechBubble.setBounds(bounds);
	}

	@Override
	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setFakeBoldText(true);
		paint.setTextSize(16);
		if (currentPosition < text.length() && currentLine < 5) {
			if (bounds.right < bounds.left + 50 + 8 * textArray[currentLine].length()) {
				bounds.right = bounds.left + 50 + 8 * textArray[currentLine].length();
			}
			bounds.bottom = 70 + bounds.top + 10 * currentLine + 1;
			speechBubble.setBounds(bounds);
			speechBubble.draw(canvas);
			for (int i = 0; i < 8; i++) {
				canvas.drawText(textArray[i], 100, 100 + i * 15, paint);
			}
		}
	}

	@Override
	public void update(long gameTime) {
		frames++;
		if (currentPosition < text.length() - 1 && currentLine < 8) {
			if (frames % 6 == 0) {
				currentPosition++;
				//				frames = 0;
				if (currentPosition > 20 * (currentLine + 1) && text.charAt(currentPosition) == ' ') {
					currentLine++;
				}

				textArray[currentLine] = textArray[currentLine] + text.charAt(currentPosition);
			}
		}
		if (frames == 10 * text.length()) {
			tutor.idle();
			tutorialOverlay.removeSurfaceObject(this);
			Tutorial.getInstance(null).setNotification("blafasel");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		tutorialOverlay.removeSurfaceObject(this);
	}

}
