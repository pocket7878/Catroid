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

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;

public class LiveWallpaper extends WallpaperService {

	public static final String SHARED_PREFS_NAME = "livewallpapersettings";

	@Override
	public Engine onCreateEngine() {
		return new CatWallEngine();
	}

	private class CatWallEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {
		private boolean mVisible = false;

		private Paint paint;
		private Script scriptToHandle;
		private Brick brickToHandle;
		private SharedPreferences mPreferences;

		private boolean startScript = false;
		private boolean tappedScript = false;

		private String licence = " licence ";

		private WallpaperCostume wallpaperCostume = WallpaperCostume.getInstance();
		private ScreenSize screensize = ScreenSize.getInstance();

		public CatWallEngine() {
			mPreferences = LiveWallpaper.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
			mPreferences.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(mPreferences, null);
		}

		private final Handler mHandler = new Handler();

		private final Runnable mUpdateDisplay = new Runnable() {
			public void run() {
				draw();
			}
		};

		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				startScript = true;
				tappedScript = false;
				handleScript();
				draw();
			} else {
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
			WallpaperCostume.getInstance().resetCostume();
			mVisible = false;
			mHandler.removeCallbacks(mUpdateDisplay);
		}

		public void handleScript() {
			List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
			for (Sprite sprite : spriteList) {
				for (int i = 0; i < sprite.getNumberOfScripts(); i++) {
					scriptToHandle = sprite.getScript(i);
					Log.v("DEBUG", "script of sprite name:" + sprite.getScript(i).toString());
					for (int j = 0; j < scriptToHandle.getBrickList().size(); j++) {
						brickToHandle = scriptToHandle.getBrick(j);
						Log.v("DEBUG", "Brick Name:" + scriptToHandle.getBrick(j).toString());
						if (startScript && scriptToHandle instanceof StartScript) {
							brickToHandle.executeLiveWallpaper();
							draw();
						} else if (tappedScript && scriptToHandle instanceof WhenScript) {
							brickToHandle.executeLiveWallpaper();
							draw();
						}
					}
				}
			}

			resetFlag();
		}

		public void resetFlag() {
			if (startScript) {
				startScript = false;
			}

			if (tappedScript) {
				tappedScript = false;
			}
		}

		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;

			try {
				c = holder.lockCanvas();
				paint = new Paint();
				if (c != null) {

					if (wallpaperCostume.getBackground() != null) {
						c.drawBitmap(wallpaperCostume.getBackground(), 0, 0, paint);
						//						Log.v("DEBUG", "genişlik : " + screensize.getWindowManager().getDefaultDisplay().getWidth());
						//						c.scale(screensize.getWindowManager().getDefaultDisplay().getWidth(), screensize
						//								.getWindowManager().getDefaultDisplay().getHeight());

						c.drawBitmap(wallpaperCostume.getBackground(), 0, 0, paint);

						if (wallpaperCostume.getCostume() != null && !wallpaperCostume.isCostumeHidden()) {

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

		@Override
		public void onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (!tappedScript && wallpaperCostume.touchedInsideTheCostume(event.getX(), event.getY())) {
					tappedScript = true;
					handleScript();
				}
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content
		 * .SharedPreferences, java.lang.String)
		 */
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			// TODO Auto-generated method stub

		}

	}
}
