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

/**
 * @author drab
 * 
 */
public class ScreenParameters {
	private static ScreenParameters screenParameters = new ScreenParameters();

	private int bubbleTextSize = 0;
	private boolean bubbleTextBold = false;
	private boolean bubbleTextAliasing = false;
	private int bubbleBottomMargin = 0;
	private int bubbleUpTopMargin = 0;
	private int bubbleDownTopMargin = 0;
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
				bubbleBottomMargin = 20;
				bubbleUpTopMargin = 20;
				bubbleDownTopMargin = 45;
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
				bubbleTextAliasing = false;
				bubbleBottomMargin = 40;
				bubbleUpTopMargin = 20;
				bubbleDownTopMargin = 45;
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
				bubbleBottomMargin = 70;
				bubbleUpTopMargin = 20;
				bubbleDownTopMargin = 45;
				bubbleFlipDownMargin = 200;
				xMarginBubbleUpToTutor = 20;
				yMarginBubbleUpToTutor = 90;
				xMarginBubbleDownToTutor = 20;
				yMarginBubbleDownToTutor = 110;
				bubbleResizeWidthMargin = 40;
				bubbleMaxWidth = 160;
				bubbleMinWidth = 80;

				break;

			case XDPI:
				bubbleTextSize = 19;
				bubbleTextBold = true;
				bubbleTextAliasing = false;
				bubbleBottomMargin = 90;
				bubbleUpTopMargin = 20;
				bubbleDownTopMargin = 45;
				bubbleFlipDownMargin = 200;
				xMarginBubbleUpToTutor = 20;
				yMarginBubbleUpToTutor = 90;
				xMarginBubbleDownToTutor = 20;
				yMarginBubbleDownToTutor = 110;
				bubbleResizeWidthMargin = 40;
				bubbleMaxWidth = 160;
				bubbleMinWidth = 80;

				break;

			default:
				break;
		}
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

	public int getBubbleBottomMargin() {
		return bubbleBottomMargin;
	}

	public int getBubbleUpTopMargin() {
		return bubbleUpTopMargin;
	}

	public int getBubbleDownTopMargin() {
		return bubbleDownTopMargin;
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