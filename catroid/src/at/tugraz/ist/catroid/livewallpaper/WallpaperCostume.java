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
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Project;

public class WallpaperCostume {
	private static WallpaperCostume wallpaperCostume;

	private CostumeData costumeData;

	private Bitmap costume = null;
	private Bitmap background = null;

	private float X;
	private float Y;

	private int centerXCoord;
	private int centerYCoord;

	private boolean coordsSetManuallyFlag = false;
	private boolean costumeHiddenFlag = false;

	private WallpaperCostume() {

		Project currentProject = ProjectManager.getInstance().getCurrentProject();

		this.centerYCoord = currentProject.virtualScreenHeight / 2;
		this.centerXCoord = currentProject.virtualScreenWidth / 2;
	}

	public static WallpaperCostume getInstance() {
		if (wallpaperCostume == null) {
			wallpaperCostume = new WallpaperCostume();
		}

		return wallpaperCostume;
	}

	public void initCostumeToDraw(CostumeData costumeData, boolean isBackground) {
		this.costumeData = costumeData;
		Bitmap bitmap = costumeData.getImageBitmap();

		if (isBackground) {
			this.background = bitmap;
		} else {
			this.costume = bitmap;
		}

	}

	public float getTopCoordinateToDraw() {
		float top = centerXCoord - this.costume.getWidth() / 2;
		if (coordsSetManuallyFlag) {
			top += X;
		}
		return top;
	}

	public float getLeftCoordinateToDraw() {
		float left = centerYCoord - this.costume.getHeight() / 2;
		if (coordsSetManuallyFlag) {
			left -= Y;
		}
		return left;

	}

	public boolean touchedInsideTheCostume(float x, float y) {
		float top = getTopCoordinateToDraw();
		float left = getLeftCoordinateToDraw();
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

	public void setCostume(Bitmap costume) {
		this.costume = costume;
	}

	public Bitmap getBackground() {
		return background;
	}

	public void setBackground(Bitmap background) {
		this.background = background;
	}

	public CostumeData getCostumeData() {
		return costumeData;
	}

	public void setCostumeData(CostumeData costumeData) {
		this.costumeData = costumeData;
	}

	public boolean isCostumeHidden() {
		return costumeHiddenFlag;
	}

	public void setCostumeHiddenFlag(boolean hideCostume) {
		this.costumeHiddenFlag = hideCostume;
	}

	public boolean isCoordsSetManuallyFlag() {
		return coordsSetManuallyFlag;
	}

	public boolean isCoordsSetManually() {
		return coordsSetManuallyFlag;
	}

	public void setCoordsSetManuallyFlag(boolean coordsSetManuallyFlag) {
		this.coordsSetManuallyFlag = coordsSetManuallyFlag;
	}

	public float getX() {
		return X;
	}

	public void setX(float x) {
		X = x;
	}

	public float getY() {
		return Y;
	}

	public void setY(float y) {
		Y = y;
	}

}
