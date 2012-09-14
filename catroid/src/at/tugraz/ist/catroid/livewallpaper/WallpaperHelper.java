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
package at.tugraz.ist.catroid.livewallpaper;

import android.os.Handler;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;

public class WallpaperHelper {

	private static WallpaperHelper wallpaperHelper;

	private Project project;

	private int centerXCoord;
	private int centerYCoord;

	private Handler drawingThreadHandler;
	private Runnable drawingThread;

	private boolean isLiveWallpaper = false;

	public WallpaperHelper() {

	}

	public static WallpaperHelper getInstance() {
		if (wallpaperHelper == null) {
			wallpaperHelper = new WallpaperHelper();
		}

		return wallpaperHelper;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.centerYCoord = Values.SCREEN_HEIGHT / 2;
		this.centerXCoord = Values.SCREEN_WIDTH / 2;
		this.project = project;
	}

	public int getCenterXCoord() {
		return centerXCoord;
	}

	public void setCenterXCoord(int centerXCoord) {
		this.centerXCoord = centerXCoord;
	}

	public int getCenterYCoord() {
		return centerYCoord;
	}

	public void setCenterYCoord(int centerYCoord) {
		this.centerYCoord = centerYCoord;
	}

	public void destroy() {

		for (Sprite sprite : project.getSpriteList()) {
			sprite.getWallpaperCostume().clear();
		}

	}

	public boolean isLiveWallpaper() {
		return isLiveWallpaper;
	}

	public void setLiveWallpaper(boolean isLiveWallpaper) {
		this.isLiveWallpaper = isLiveWallpaper;
	}

	public Handler getDrawingThreadHandler() {
		return drawingThreadHandler;
	}

	public void setDrawingThreadHandler(Handler drawingThreadHandler) {
		this.drawingThreadHandler = drawingThreadHandler;
	}

	public Runnable getDrawingThread() {
		return drawingThread;
	}

	public void setDrawingThread(Runnable drawingThread) {
		this.drawingThread = drawingThread;
	}

}
