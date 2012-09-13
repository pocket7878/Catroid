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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.WallpaperManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.io.SoundManager;

public class LiveWallpaper extends WallpaperService {

	@Override
	public Engine onCreateEngine() {

		ProjectManager.getInstance().loadProject(Constants.PROJECTCODE_NAME, getApplicationContext(), false);
		WallpaperHelper.getInstance().setProject(ProjectManager.getInstance().getCurrentProject());

		return new CatWallEngine();

	}

	private class CatWallEngine extends Engine {

		private boolean mVisible = false;

		private boolean isStartScript = false;
		private boolean isWhenScript = false;

		private Paint paint;

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
				isStartScript = true;
				List<Sprite> spriteList = wallpaperHelper.getProject().getSpriteList();
				for (Sprite sprite : spriteList) {
					executeSprite(sprite);
				}
				isStartScript = false;

			} else {
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

		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;
			paint = new Paint();
			try {
				c = holder.lockCanvas();
				if (c != null) {
					Iterator<WallpaperCostume> iterator = wallpaperHelper.getWallpaperCostumes().iterator();

					WallpaperCostume wallpaperCostume;
					while (iterator.hasNext()) {
						wallpaperCostume = iterator.next();
						if (wallpaperCostume.getCostume() != null) {
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

		//		@Override
		//		public void onTouchEvent(MotionEvent event) {
		//			if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
		//
		//				ArrayList<WallpaperCostume> wallpaperCostumes = wallpaperHelper.getWallpaperCostumes();
		//
		//				for (int costumeIndex = wallpaperCostumes.size() - 1; costumeIndex > 0; costumeIndex--) {
		//					if (wallpaperCostumes.get(costumeIndex).touchedInsideTheCostume(event.getX(), event.getY())) {
		//						isWhenScript = true;
		//						executeSprite(wallpaperCostumes.get(costumeIndex).getSprite());
		//						isWhenScript = false;
		//						break;
		//					}
		//				}
		//
		//			}
		//		}

		@Override
		public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
			if (action.equals(WallpaperManager.COMMAND_TAP)) {
				ArrayList<WallpaperCostume> wallpaperCostumes = wallpaperHelper.getWallpaperCostumes();

				for (int costumeIndex = wallpaperCostumes.size() - 1; costumeIndex > 0; costumeIndex--) {
					if (wallpaperCostumes.get(costumeIndex).touchedInsideTheCostume(x, y)) {
						isWhenScript = true;
						executeSprite(wallpaperCostumes.get(costumeIndex).getSprite());
						isWhenScript = false;
						break;
					}
				}
			}
			return null;
		}

		private void executeSprite(Sprite sprite) {
			for (int scriptIndex = 0; scriptIndex < sprite.getNumberOfScripts(); scriptIndex++) {
				if ((isStartScript && sprite.getScript(scriptIndex) instanceof StartScript)
						|| (isWhenScript && sprite.getScript(scriptIndex) instanceof WhenScript)) {
					executeScript(sprite.getScript(scriptIndex));
					break;
				}

			}

		}

		public void executeScript(Script script) {
			ArrayList<Brick> bricks = script.getBrickList();
			for (Brick brick : bricks) {
				brick.executeLiveWallpaper();
				draw();

			}

		}
	}

}
