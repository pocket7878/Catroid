package at.tugraz.ist.catroid.livewallpaper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ChangeXByBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;

public class LiveWallpaper extends WallpaperService {

	private ProjectManager projectManager;
	private Project currentProject;
	private Map<String, Bitmap> costumes;
	private List<Sprite> spriteList;

	@Override
	public Engine onCreateEngine() {
		costumes = new HashMap<String, Bitmap>();
		projectManager = ProjectManager.getInstance();
		currentProject = projectManager.getCurrentProject();
		spriteList = currentProject.getSpriteList();

		initCostumes();
		return new CatWallEngine();
	}

	public void initCostumes() {
		ArrayList<CostumeData> costumeDataList;
		CostumeData costumeData;
		String path;

		for (Sprite sprite : spriteList) {
			costumeDataList = sprite.getCostumeDataList();
			for (int i = 0; i < costumeDataList.size(); i++) {
				costumeData = costumeDataList.get(i);
				path = costumeData.getAbsolutePath();
				costumes.put(path, BitmapFactory.decodeFile(path));

			}
		}

	}

	private class CatWallEngine extends Engine {
		private boolean mVisible = false;

		private Paint paint;
		private Sprite sprite;
		private Script scriptToHandle;
		private Brick brickToHandle;

		private boolean startScript = false;
		private boolean tappedScript = false;

		private Bitmap costume = null;
		private Bitmap background = null;

		private float width;
		private float height;
		//
		//		private int screenWidthHalf = currentProject.virtualScreenWidth / 2;
		private int screenHeightHalf = currentProject.virtualScreenHeight / 2;

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
				setTouchEventsEnabled(true);
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
			mVisible = false;
			mHandler.removeCallbacks(mUpdateDisplay);
		}

		public void handleScript() {
			for (Sprite sprite : spriteList) {
				this.sprite = sprite;

				for (int i = 0; i < sprite.getNumberOfScripts(); i++) {
					scriptToHandle = sprite.getScript(i);
					Log.v("DEBUG", "script of sprite name:" + sprite.getScript(i).toString());
					for (int j = 0; j < scriptToHandle.getBrickList().size(); j++) {
						brickToHandle = scriptToHandle.getBrick(j);
						Log.v("DEBUG", "Brick Name:" + scriptToHandle.getBrick(j).toString());
						if (startScript && scriptToHandle instanceof StartScript) {
							handleBrick();
						} else if (tappedScript && scriptToHandle instanceof WhenScript) {
							handleBrick();
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

			//			if (tappedScript) {
			//				tappedScript = false;
			//			}
		}

		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;

			try {
				c = holder.lockCanvas();
				paint = new Paint();
				if (c != null) {

					if (background != null) {
						c.drawBitmap(background, 0, 0, paint);
						if (costume != null) {
							c.drawBitmap(costume, width, height, paint);
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
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				if (!tappedScript) {
					tappedScript = true;
					handleScript();
				}
			}
		}

		public void handleBrick() {
			if (brickToHandle instanceof SetCostumeBrick) {
				handleSetCostumeBrick(sprite.costume.getXPosition());
			} else if (brickToHandle instanceof WaitBrick) {
				((WaitBrick) brickToHandle).execute();
			} else if (brickToHandle instanceof ChangeXByBrick) {
				((ChangeXByBrick) brickToHandle).execute();
			} else if (brickToHandle instanceof SetXBrick) {
				sprite.costume.getXPosition();
				sprite.costume.getYPosition();
			}
		}

		private void handleSetCostumeBrick(float f) {

			SetCostumeBrick brick = (SetCostumeBrick) brickToHandle;
			Bitmap bitmap = costumes.get(brick.getImagePath());
			width = f;
			height = screenHeightHalf - (bitmap.getHeight() / 2);
			if (sprite.getName().equals(getApplicationContext().getString(R.string.background))) {
				background = bitmap;
			} else {
				costume = bitmap;
				draw();
			}

		}
		/**
		 * 
		 */

	}

}
