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
import at.tugraz.ist.catroid.content.bricks.NextCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;

public class LiveWallpaper extends WallpaperService {

	private ProjectManager projectManager;
	private Project currentProject;
	private Map<String, Bitmap> costumes;
	private Map<String, Integer> positions;
	private List<Sprite> spriteList;
	private ArrayList<CostumeData> costumeDataList;

	@Override
	public Engine onCreateEngine() {
		costumes = new HashMap<String, Bitmap>();
		positions = new HashMap<String, Integer>();

		projectManager = ProjectManager.getInstance();
		currentProject = projectManager.getCurrentProject();
		spriteList = currentProject.getSpriteList();

		initCostumes();
		return new CatWallEngine();
	}

	public void initCostumes() {
		CostumeData costumeData;
		String path;

		for (Sprite sprite : spriteList) {
			costumeDataList = sprite.getCostumeDataList();
			for (int i = 0; i < costumeDataList.size(); i++) {
				costumeData = costumeDataList.get(i);
				path = costumeData.getAbsolutePath();
				costumes.put(path, BitmapFactory.decodeFile(path));
				positions.put(path, i);

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

		private String path = null;

		private float width;
		private float height;

		private int screenWidthHalf = currentProject.virtualScreenWidth / 2;
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
					for (int j = 0; j < scriptToHandle.getBrickList().size(); j++) {
						brickToHandle = scriptToHandle.getBrick(j);
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
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (!tappedScript) {
					tappedScript = true;
					handleScript();
				}
			}

		}

		public void handleBrick() {
			if (brickToHandle instanceof SetCostumeBrick) {
				path = ((SetCostumeBrick) brickToHandle).getImagePath();
				handleCostumesAndBackgrounds(costumes.get(path));

			} else if (brickToHandle instanceof NextCostumeBrick) {
				if (path == null) {
					path = costumeDataList.get(0).getAbsolutePath();
				} else {
					int position = positions.get(path) + 1;
					path = costumeDataList.get(position).getAbsolutePath();
				}
				handleCostumesAndBackgrounds(costumes.get(path));
			} else if (brickToHandle instanceof SetSizeToBrick) {
				//				double size = ((SetSizeToBrick) brickToHandle).getSize();
				//				int newWidth = (int) (costume.getWidth() * size);
				//				int newHeight = (int) (costume.getHeight() * size);
				//handleCostumesAndBackgrounds(Bitmap.createScaledBitmap(costume, newWidth, newHeight, false));
			} else if (brickToHandle instanceof WaitBrick) {
				((WaitBrick) brickToHandle).execute();
			}
		}

		public void handleCostumesAndBackgrounds(Bitmap bitmap) {
			width = screenWidthHalf - (bitmap.getWidth() / 2);
			height = screenHeightHalf - (bitmap.getHeight() / 2);

			if (sprite.getName().equals(getApplicationContext().getString(R.string.background))) {
				background = bitmap;
			} else {
				costume = bitmap;
			}
			draw();
		}
	}

}
