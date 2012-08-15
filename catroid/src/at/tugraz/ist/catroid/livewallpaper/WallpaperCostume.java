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
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.Project;

public class WallpaperCostume {
	private static WallpaperCostume wallpaperCostume;

	private CostumeData costumeData;
	private Costume backgroundCostume;

	private Bitmap costume = null;
	private Bitmap background = null;

	private float top;
	private float left;

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

	public void resetCostume() {
		this.coordsSetManuallyFlag = false;
		this.costumeHiddenFlag = false;
	}

	public void initCostumeToDraw(CostumeData costumeData, boolean isBackground) {
		this.costumeData = costumeData;
		Bitmap bitmap = costumeData.getImageBitmap();

		if (isBackground) {
			this.background = bitmap;
			return;
		}

		this.costume = bitmap;

		if (!coordsSetManuallyFlag) {
			setTop(0);
			setLeft(0);
		}

	}

	public float getTop() {
		return top;
	}

	public void setTop(float x) {
		this.top = centerXCoord - (this.costume.getWidth() / 2) + x;
	}

	public float getLeft() {
		return left;
	}

	public void setLeft(float y) {
		this.left = centerYCoord - (this.costume.getHeight() / 2) - y;
	}

	public void changeTopBy(float x) {
		this.top += x;
	}

	public void changeLeftBy(float y) {
		this.left -= y;
	}

	public boolean touchedInsideTheCostume(float x, float y) {
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

}
