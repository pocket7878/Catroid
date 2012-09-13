/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.livewallpaper;

import android.graphics.Bitmap;
import android.graphics.Color;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.utils.ImageEditing;

public class WallpaperCostume {

	private CostumeData costumeData;
	private Sprite sprite;
	private Bitmap costume = null;

	private int x;
	private int y;
	private int top;
	private int left;

	private double size = 1;

	private boolean hidden = false;
	private boolean isBackground = false;
	private boolean topNeedsAdjustment = false;
	private boolean leftNeedsAdjustment = false;
	private boolean sizeNeedsAdjustment = false;

	private WallpaperHelper wallpaperHelper;

	public WallpaperCostume(Sprite sprite, CostumeData costumeData) {

		this.wallpaperHelper = WallpaperHelper.getInstance();
		this.sprite = sprite;

		//TODO: refactor the hard-coded value
		if (sprite.getName().equals("Background")) {
			this.isBackground = true;
			this.top = 0;
			this.left = 0;
		} else {
			setY(0);
			setX(0);
		}

		if (costumeData != null) {
			setCostume(costumeData);
		}

		wallpaperHelper.addNewCostume(this);

	}

	public float getTop() {
		if (topNeedsAdjustment) {
			this.topNeedsAdjustment = false;
			this.top = wallpaperHelper.getCenterXCoord() + x - (this.costume.getWidth() / 2);
		}
		return top;
	}

	public float getLeft() {
		if (leftNeedsAdjustment) {
			this.leftNeedsAdjustment = false;
			this.left = wallpaperHelper.getCenterYCoord() - y - (this.costume.getHeight() / 2);
		}
		return left;
	}

	public void setX(int x) {
		this.topNeedsAdjustment = true;
		this.x = x;
	}

	public void setY(int y) {
		this.leftNeedsAdjustment = true;
		this.y = y;
	}

	public void changeXBy(int x) {
		this.topNeedsAdjustment = true;
		this.x += x;
	}

	public void changeYby(int y) {
		this.leftNeedsAdjustment = true;
		this.y += y;
	}

	public boolean touchedInsideTheCostume(float x, float y) {
		if (isBackground) {
			return false;
		}

		float right = costume.getWidth() + top;
		float bottom = costume.getHeight() + left;

		if (x > top && x < right && y > left && y < bottom) {
			return true;
		}

		return false;

	}

	public Bitmap getCostume() {
		return costume;
	}

	public void setCostume(CostumeData costumeData) {
		this.costumeData = costumeData;
		Bitmap costumeImage = costumeData.getImageBitmap();

		if (isBackground && Values.SCREEN_WIDTH != costumeImage.getWidth()
				&& Values.SCREEN_HEIGHT != costumeImage.getHeight()) {
			this.costume = ImageEditing.scaleBitmap(costumeImage, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);

		} else {
			this.costume = costumeImage;
		}

		if (sizeNeedsAdjustment) {
			resizeCostume();
		}

	}

	public void setCostumeSize(double size) {
		this.size = size / 100;
		this.sizeNeedsAdjustment = true;
	}

	private void resizeCostume() {
		int newWidth = (int) (costume.getWidth() * size);
		int newHeight = (int) (costume.getHeight() * size);
		this.costume = ImageEditing.scaleBitmap(this.costume, newWidth, newHeight);

		this.topNeedsAdjustment = true;
		this.leftNeedsAdjustment = true;

	}

	public CostumeData getCostumeData() {
		return costumeData;
	}

	public boolean isCostumeHidden() {
		return hidden;
	}

	public void setCostumeHidden(boolean hideCostume) {
		this.hidden = hideCostume;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public boolean isBackground() {
		return isBackground;
	}

	public void setBackground(boolean isBackground) {
		this.isBackground = isBackground;
	}

	/**
	 * @param brightness
	 */
	public void setBrightness(float brightness) {

		Bitmap resultBitmap = Bitmap.createBitmap(costume.getWidth(), costume.getHeight(), costume.getConfig());

		for (int x = 0; x < costume.getWidth(); x++) {
			for (int y = 0; y < costume.getHeight(); y++) {
				int oldPixelColor = costume.getPixel(x, y);

				int red = Color.red(oldPixelColor) + (int) (255 * (brightness - 1));
				int green = Color.green(oldPixelColor) + (int) (255 * (brightness - 1));
				int blue = Color.blue(oldPixelColor) + (int) (255 * (brightness - 1));
				int alpha = Color.alpha(oldPixelColor);

				if (red > 255) {
					red = 255;
				} else if (red < 0) {
					red = 0;
				}
				if (green > 255) {
					green = 255;
				} else if (green < 0) {
					green = 0;
				}
				if (blue > 255) {
					blue = 255;
				} else if (blue < 0) {
					blue = 0;
				}

				int newPixel = Color.argb(alpha, red, green, blue);
				resultBitmap.setPixel(x, y, newPixel);
			}
		}

		costume = resultBitmap;

	}
}