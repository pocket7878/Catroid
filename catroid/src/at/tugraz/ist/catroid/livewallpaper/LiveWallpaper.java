package at.tugraz.ist.catroid.livewallpaper;

import java.util.List;

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
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;

public class LiveWallpaper extends WallpaperService {

	private ProjectManager projectManager;
	private Project currentProject;

	@Override
	public Engine onCreateEngine() {
		projectManager = ProjectManager.getInstance();
		currentProject = projectManager.getCurrentProject();
		return new CatWallEngine();
	}

	private class CatWallEngine extends Engine {
		private boolean visible = true;

		private Paint paint;
		private Sprite sprite;
		private Script script;
		private Script scriptToHandle;

		private Canvas canvas;

		private final Handler handler = new Handler();
		private final Runnable run = new Runnable() {
			public void run() {
				draw();
			}
		};

		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			if (visible) {
				handler.post(run);
			} else {
				handler.removeCallbacks(run);
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(run);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
		}

		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;

			try {
				c = holder.lockCanvas();
				paint = new Paint();
				if (c != null) {
					this.canvas = c;

					if (currentProject.getSpriteList() != null) {
						List<Sprite> spriteList = currentProject.getSpriteList();
						for (Sprite sprite : spriteList) {
							this.sprite = sprite;
							handleScripts();

						}
					}

				}
			} finally {
				if (c != null) {
					holder.unlockCanvasAndPost(c);
				}
			}
			handler.removeCallbacks(run);
			//handler.postDelayed(run, 1000/targetFramerate -(System.currentTimeMillis() - mLastTime));

		}

		private void handleScripts() {

			int numberScripts = sprite.getNumberOfScripts();

			for (int i = 0; i < numberScripts; i++) {
				script = sprite.getScript(i);
				if (script instanceof StartScript) {
					scriptToHandle = script;
					handleBricks();
				} else if (script instanceof WhenScript) {
					scriptToHandle = script;
					setTouchEventsEnabled(true);

				}

			}
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				setTouchEventsEnabled(false);
				handleBricks();
			}
		}

		private void handleBricks() {
			int numberOfBricks = scriptToHandle.getBrickList().size();
			for (int i = 0; i < numberOfBricks; i++) {
				if (scriptToHandle.getBrick(i) instanceof SetCostumeBrick) {
					handleSetCostumeBrick(scriptToHandle.getBrick(i));
				}
			}
		}

		private void handleSetCostumeBrick(Brick b) {
			SetCostumeBrick brick = (SetCostumeBrick) b;
			Bitmap bitmap = BitmapFactory.decodeFile(brick.getImagePath());
			float width = currentProject.virtualScreenWidth / 2 - (bitmap.getWidth() / 2);
			float height = currentProject.virtualScreenHeight / 2 - (bitmap.getHeight() / 2);

			if (sprite.getName().equals(getApplicationContext().getString(R.string.background))) {
				canvas.drawBitmap(bitmap, 0, 0, paint);
			} else {
				canvas.drawBitmap(bitmap, width, height, paint);
			}

		}
	}

}
