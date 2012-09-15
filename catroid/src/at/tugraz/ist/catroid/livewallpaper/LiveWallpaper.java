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

import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.SurfaceHolder;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.utils.Utils;

public class LiveWallpaper extends WallpaperService {

	@Override
	public Engine onCreateEngine() {

		ProjectManager.getInstance().setProject(null);
		Utils.loadProjectIfNeeded(getApplicationContext());
		WallpaperHelper.getInstance().setProject(ProjectManager.getInstance().getCurrentProject());

		return new CatWallEngine();

	}

	public class CatWallEngine extends Engine {

		private boolean mVisible = false;

		private Paint paint;
		private List<Sprite> sprites;

		private WallpaperHelper wallpaperHelper = WallpaperHelper.getInstance();

		private final Handler mHandler = new Handler();

		private final Runnable mUpdateDisplay = new Runnable() {
			@Override
			public void run() {
				draw();
			}
		};

		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				wallpaperHelper.setLiveWallpaper(true);
				wallpaperHelper.setDrawingThread(mUpdateDisplay);
				wallpaperHelper.setDrawingThreadHandler(mHandler);

				sprites = wallpaperHelper.getProject().getSpriteList();

				for (Sprite sprite : sprites) {
					sprite.resetScripts();
					sprite.startStartScripts();
					draw();
				}

			} else {
				wallpaperHelper.setLiveWallpaper(false);
				SoundManager.getInstance().stopAllSounds();
				mHandler.removeCallbacks(mUpdateDisplay);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			draw();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			wallpaperHelper.destroy();
			SoundManager.getInstance().stopAllSounds();
			mVisible = false;
			mHandler.removeCallbacks(mUpdateDisplay);
		}

		public void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;
			paint = new Paint();
			try {
				c = holder.lockCanvas();
				if (c != null && sprites != null) {

					WallpaperCostume wallpaperCostume;
					for (Sprite sprite : sprites) {
						wallpaperCostume = sprite.getWallpaperCostume();
						if (wallpaperCostume != null && wallpaperCostume.getCostume() != null
								&& !wallpaperCostume.isCostumeHidden()) {
							c.drawBitmap(wallpaperCostume.getCostume(), wallpaperCostume.getTop(),
									wallpaperCostume.getLeft(), paint);

						}
					}

				}

			} finally {
				if (c != null) {
					holder.unlockCanvasAndPost(c);
				}
			}
			mHandler.removeCallbacks(mUpdateDisplay);
			if (mVisible) {
				mHandler.postDelayed(mUpdateDisplay, 100);
			}
		}

		@SuppressLint("NewApi")
		@Override
		public void onTouchEvent(MotionEvent event) {

			Sprite spriteToExecute = null;
			PointerCoords coords = new PointerCoords();

			int action = event.getActionMasked();
			if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
				for (int pointerIndex = 0; pointerIndex < event.getPointerCount(); pointerIndex++) {

					event.getPointerCoords(pointerIndex, coords);

					for (Sprite sprite : sprites) {
						if (sprite.getWallpaperCostume() != null
								&& sprite.getWallpaperCostume().touchedInsideTheCostume(coords.x, coords.y)) {
							spriteToExecute = sprite;
						}
					}

					if (spriteToExecute != null) {
						spriteToExecute.startWhenScripts("Tapped");
						draw();
					}
				}
			}
		}

	}

}
