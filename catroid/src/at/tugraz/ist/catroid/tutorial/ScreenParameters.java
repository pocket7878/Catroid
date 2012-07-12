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

import android.util.Log;

/**
 * @author drab
 * 
 */
public class ScreenParameters {
	private static ScreenParameters screenParameters = new ScreenParameters();

	private int bubbleTextSize = 0;
	private boolean bubbleTextBold = false;
	private boolean bubbleTextAliasing = false;
	private int bubbleBottomMarginToText = 0;
	private int upperBubbleTopMarginToText = 0;
	private int lowerBubbleTopMarginToText = 0;
	private int bubbleFlipDownMargin = 0;
	private int xMarginBubbleDownToTutor = 0;
	private int yMarginBubbleDownToTutor = 0;
	private int xMarginBubbleUpToTutor = 0;
	private int yMarginBubbleUpToTutor = 0;
	private int bubbleResizeWidthMargin = 0;
	private int bubbleMinWidth = 0;
	private int bubbleMaxWidth = 0;

	private enum DENSITY {
		LDPI, MDPI, HDPI, XDPI
	}

	private DENSITY density;

	private ScreenParameters() {

	}

	public static ScreenParameters getInstance() {
		if (screenParameters == null) {
			screenParameters = new ScreenParameters();
		}
		return screenParameters;
	}

	public void setScreenParameters() {
		setDensityParameter(Tutorial.getInstance(null).getDensity());
		setBubbleParameters();
	}

	private void setDensityParameter(float density) {
		if (density < 1.0f) {
			this.density = DENSITY.LDPI;
		} else if (density == 1.0f) {
			this.density = DENSITY.MDPI;
		} else if (density == 1.5f) {
			this.density = DENSITY.HDPI;
		} else if (density > 1.5f) {
			this.density = DENSITY.XDPI;
		}
	}

	private void setBubbleParameters() {
		switch (this.density) {
			case LDPI:
				bubbleTextSize = 10;
				bubbleTextBold = false;
				bubbleTextAliasing = true;
				bubbleBottomMarginToText = 50;
				upperBubbleTopMarginToText = 25;
				lowerBubbleTopMarginToText = 25;
				bubbleFlipDownMargin = 200;
				xMarginBubbleUpToTutor = 20;
				yMarginBubbleUpToTutor = 90;
				xMarginBubbleDownToTutor = 20;
				yMarginBubbleDownToTutor = 110;
				bubbleResizeWidthMargin = 40;
				bubbleMaxWidth = 160;
				bubbleMinWidth = 80;

				break;

			case MDPI:
				bubbleTextSize = 13;
				bubbleTextBold = true;
				bubbleTextAliasing = true;
				bubbleBottomMarginToText = 60;
				upperBubbleTopMarginToText = 20;
				lowerBubbleTopMarginToText = 45;
				bubbleFlipDownMargin = 200;
				xMarginBubbleUpToTutor = 20;
				yMarginBubbleUpToTutor = 90;
				xMarginBubbleDownToTutor = 20;
				yMarginBubbleDownToTutor = 110;
				bubbleResizeWidthMargin = 40;
				bubbleMaxWidth = 160;
				bubbleMinWidth = 80;

				break;

			case HDPI:
				bubbleTextSize = 16;
				bubbleTextBold = true;
				bubbleTextAliasing = false;
				bubbleBottomMarginToText = 70;
				upperBubbleTopMarginToText = 20;
				lowerBubbleTopMarginToText = 45;

				bubbleFlipDownMargin = 150;

				xMarginBubbleUpToTutor = 20;
				yMarginBubbleUpToTutor = 90;
				xMarginBubbleDownToTutor = 20;
				yMarginBubbleDownToTutor = 110;

				bubbleResizeWidthMargin = 25;

				bubbleMaxWidth = 200;
				bubbleMinWidth = 150;

				break;

			case XDPI:
				bubbleTextSize = 24;
				bubbleTextBold = true;
				bubbleTextAliasing = true;
				bubbleBottomMarginToText = 90;
				upperBubbleTopMarginToText = 30;
				lowerBubbleTopMarginToText = 45;
				bubbleFlipDownMargin = 200;
				xMarginBubbleUpToTutor = 20;
				yMarginBubbleUpToTutor = 130;
				xMarginBubbleDownToTutor = 20;
				yMarginBubbleDownToTutor = 110;
				bubbleResizeWidthMargin = 40;
				bubbleMaxWidth = 200;
				bubbleMinWidth = 120;

				break;

			default:
				break;
		}
	}

	public int setCoordinatesToDensity(int value, boolean width) {
		if (value > 100) {
			value = 100;
		}

		if (width) {
			Log.i("state", "DEN: Old x is: " + value);
			value = (int) ((value / 100.0f) * Tutorial.getInstance(null).getScreenWidth());
			Log.i("state", "DEN: New x is: " + value);
		} else {
			Log.i("state", "DEN: Old y is: " + value);
			value = (int) ((value / 100.0f) * Tutorial.getInstance(null).getScreenHeight());
			Log.i("state", "DEN: Old y is: " + value);
		}

		return value;
	}

	public int getBubbleMinWidth() {
		return bubbleMinWidth;
	}

	public int getBubbleMaxWidth() {
		return bubbleMaxWidth;
	}

	public static ScreenParameters getScreenParameters() {
		return screenParameters;
	}

	public int getBubbleTextSize() {
		return bubbleTextSize;
	}

	public boolean isBubbleTextBold() {
		return bubbleTextBold;
	}

	public boolean isBubbleTextAliasing() {
		return bubbleTextAliasing;
	}

	public int getBubbleBottomMarginToText() {
		return bubbleBottomMarginToText;
	}

	public int getUpperBubbleTopMarginToText() {
		return upperBubbleTopMarginToText;
	}

	public int getLowerBubbleTopMarginToText() {
		return lowerBubbleTopMarginToText;
	}

	public int getBubbleFlipDownMargin() {
		return bubbleFlipDownMargin;
	}

	public int getxMarginBubbleDownToTutor() {
		return xMarginBubbleDownToTutor;
	}

	public int getyMarginBubbleDownToTutor() {
		return yMarginBubbleDownToTutor;
	}

	public int getxMarginBubbleUpToTutor() {
		return xMarginBubbleUpToTutor;
	}

	public int getyMarginBubbleUpToTutor() {
		return yMarginBubbleUpToTutor;
	}

	public int getBubbleResizeWidthMargin() {
		return bubbleResizeWidthMargin;
	}

	public DENSITY getDensity() {
		return density;
	}
}