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

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.Display;

/**
 * @author User
 * 
 */
public class TutorBubble {

	boolean done = false;

	NinePatchDrawable mSpeechBubble;
	Paint textPaint;
	int textSize = 22;
	int textWidth = 200; // noch zu berechnen aus der breitesten Line
	int x; // defined by Tutor
	int y; // defined by Tutor
	int lineBreakYOffset = 0;

	int maximumCharsPerLine = 15;
	int maxLines = 4;

	int height;
	int width;

	int xText;
	int yText;

	int marginTop = 5;
	int marginLeft = 25;
	int marginRight = 20;
	int marginBottom = 20;

	boolean notificated = false;
	Context context;

	// Bubbletext related
	String bubbleTextRaw;
	Vector<String> bubbleTextWords;
	Vector<String> bubbleTextLines;

	// Update related
	private long frameTicker; // the time of the last frame update
	private int framePeriod; // milliseconds between each frame (1000/fps)
	public boolean animationFinished = false; // do muss ma no wos ueberlegen, das hier: totales ende
	public boolean textFinished = false; // nur ende vom tippen -> Katze muss Mund still halten
	private boolean waitAfterText = false;
	private boolean waitedAfterText = false;
	private int waitTimeAfterText = 1000;
	private int currentLine = 0;
	private int currentChar = 1;

	public void update(long gameTime) {
		if (waitAfterText) {
			if (gameTime > frameTicker + waitTimeAfterText) {
				waitAfterText = false;
			}
		} else if (gameTime > frameTicker + framePeriod) {
			frameTicker = gameTime;
			currentChar++;

			if (currentLine < bubbleTextLines.size()) {

				if (currentChar > bubbleTextLines.get(currentLine).length()) {
					currentLine++;
					currentChar = 1;
				}

			} else if (!waitedAfterText) {
				textFinished = true;
				waitedAfterText = true;
				waitAfterText = true;
			} else if (!notificated) {
				animationFinished = true;
				Tutorial tut = Tutorial.getInstance(context);
				tut.setNotification("BubbleDone");
				notificated = true;
			}
		}
	}

	TutorBubble(String bubbleText, Drawable bubbleDrawable, int posxTutor, int posyTutor, Context context) {

		bubbleTextWords = new Vector<String>();
		bubbleTextLines = new Vector<String>();
		this.context = context;
		this.bubbleTextRaw = bubbleText;

		mSpeechBubble = (NinePatchDrawable) bubbleDrawable;
		setSpeechBubblePaint();
		animationFinished = false;

		int screenWidth = getScreenWidth();

		if ((screenWidth - posxTutor) >= 150) {
			this.x = posxTutor + 70;
			this.y = posyTutor - 30;
		} else {
			this.x = posxTutor + 50;
			this.y = posyTutor + 280;
		}

		notificated = false;

		framePeriod = 50; // alle 200 ms neues Zeichen
		frameTicker = 0;

		prepareTextForOutput(bubbleText);
	}

	public void prepareTextForOutput(String text) {
		setBubbleLimits();
		calculateMaxCharsPerLine();
		bubbleTextWrap();
		buildTextLines();
		calculateTextCoords();

	}

	public void calculateMaxCharsPerLine() {
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();

		int orientation = display.getOrientation();
		Rect boxSize = mSpeechBubble.getBounds();
		int size = boxSize.width();

		int space = (size / (textSize / 2)) - 3;

		switch (orientation) {
			case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
				maximumCharsPerLine = space;
				break;
			case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
				maximumCharsPerLine = space;
				break;
		}

	}

	public void calculateTextCoords() {
		Rect bounds = mSpeechBubble.getBounds();
		xText = bounds.left + marginLeft;
		yText = y - marginBottom - (textSize * maxLines);
	}

	public int getScreenHeight() {
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
		int screenHeight = display.getHeight();
		return screenHeight;
	}

	public int getScreenWidth() {
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
		int screenWidth = display.getWidth();
		return screenWidth;
	}

	public void setBubbleLimits() {

		int screenWidth = getScreenWidth();

		Rect bounds = new Rect();

		//		bounds.top = screenHeight - (screenHeight-y+marginTop+marginBottom);
		//		bounds.top = y - (marginTop + marginBottom +(textSize * bubbleTextLines.size()));	
		//		bounds.right = x + marginLeft + marginRight + textWidth;

		if ((screenWidth - x) >= screenWidth / 2) {
			//Katze
			bounds.left = x;
			bounds.top = y - (marginTop + marginBottom + (textSize * maxLines)); //immer 4 lines
			bounds.right = screenWidth - marginRight;
			bounds.bottom = y + marginBottom;

		} else {
			//Hund
			bounds.left = marginLeft;
			bounds.top = y - (2 * marginTop + 2 * marginBottom + (textSize * maxLines)); //immer 4 lines
			bounds.right = x;
			bounds.bottom = y;
		}

		mSpeechBubble.setBounds(bounds);

	}

	public void draw(Canvas canvas) {
		mSpeechBubble.draw(canvas);

		if (bubbleTextLines.size() >= maxLines) {
			drawText(canvas, maxLines);

			if (currentLine > maxLines) {
				for (int i = 0; i < maxLines; i++) {
					bubbleTextLines.remove(0);
				}
				currentLine = 0;
				canvas.drawColor(0, PorterDuff.Mode.CLEAR);
			}

		}

		if (bubbleTextLines.size() < maxLines) {
			drawText(canvas, bubbleTextLines.size());
		}

	}

	public void drawText(Canvas canvas, int limit) {
		for (int i = 0; i < limit; i++) {
			if (i == currentLine) {
				int charsToShow = currentChar;
				if (currentChar >= bubbleTextLines.get(i).length()) {
					charsToShow = bubbleTextLines.get(i).length() - 1;
				}
				int yTextFinal = yText + (textSize * (i + 1));
				canvas.drawText(bubbleTextLines.get(i).substring(0, charsToShow), xText, yTextFinal, textPaint);
			} else if (i < currentLine) {
				int yTextFinal = yText + (textSize * (i + 1));
				canvas.drawText(bubbleTextLines.get(i), xText, yTextFinal, textPaint);
			}
		}

	}

	public void drawSpeechBubble(Canvas canvas, int pos_x, int pos_y, int offset_x, int offset_y) {

		// Wichtig: Otto davon informieren dass Text fertig!
		if (!notificated) {
			Tutorial tut = Tutorial.getInstance(context);
			tut.setNotification("BubbleDone");
			notificated = true;
		}
	}

	private void buildTextLines() {

		String stringForOutput = "";
		// Speziallfall: leerer Textstring
		String stringLastLine = bubbleTextWords.get(0);

		//Spezialfall: ein Wort > 20 Zeichen
		for (int i = 1; i < bubbleTextWords.size(); i++) {
			if (bubbleTextWords.get(i).length() + stringLastLine.length() < maximumCharsPerLine) {
				stringLastLine += " ";
				stringLastLine += bubbleTextWords.get(i);
			} else {
				bubbleTextLines.add(stringLastLine);
				stringLastLine = bubbleTextWords.get(i);
			}
		}
		bubbleTextLines.add(stringLastLine);
	}

	private void bubbleTextWrap() {
		//Spezialfall: nur ein Wort
		if (!bubbleTextRaw.contains(" ")) {
			bubbleTextWords.add(bubbleTextRaw);
		}

		while (bubbleTextRaw.contains(" ")) {
			bubbleTextRaw = bubbleTextRaw.trim();
			if (bubbleTextRaw.contains(" ")) {
				bubbleTextWords.add(bubbleTextRaw.substring(0, bubbleTextRaw.indexOf(" ")));
				bubbleTextRaw = bubbleTextRaw.substring(bubbleTextRaw.indexOf(" "));
			} else {
				bubbleTextWords.add(bubbleTextRaw);
				bubbleTextRaw = "";
			}
		}
	}

	public void setSpeechBubblePaint() {
		textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(textSize);
		textPaint.setFakeBoldText(true);
		textPaint.setTypeface(Typeface.SANS_SERIF);
		textPaint.setTextAlign(Align.LEFT);

	}

}
