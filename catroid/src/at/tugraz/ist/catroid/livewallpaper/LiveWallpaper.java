package at.tugraz.ist.catroid.livewallpaper;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
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
		private int width;
		private int height;

		private Paint paint;
		private Sprite sprite;
		private Canvas canvas;

		private final Handler handler = new Handler();
		private final Runnable run = new Runnable() {
			public void run() {
				draw();
			}
		};

		@Override
		public void onVisibilityChanged(boolean visible) {
			this.setVisible(visible);
			if (visible) {
				handler.post(run);
			} else {
				handler.removeCallbacks(run);
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			this.setVisible(false);
			handler.removeCallbacks(run);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			this.setWidth(width);
			this.setHeight(height);
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
							handleStartScripts();

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

		private void handleStartScripts() {

			int numberScripts = sprite.getNumberOfScripts();
			Script script;

			for (int i = 0; i < numberScripts; i++) {
				script = sprite.getScript(i);
				if (script instanceof StartScript) {
					handleBricks(script);

				}
			}

		}

		private void handleBricks(Script script) {
			SetCostumeBrick brick;
			Bitmap bitmap;
			int numberOfBricks = script.getBrickList().size();
			for (int i = 0; i < numberOfBricks; i++) {
				if (script.getBrick(i) instanceof SetCostumeBrick) {
					brick = (SetCostumeBrick) script.getBrick(i);
					bitmap = BitmapFactory.decodeFile(brick.getImagePath());
					canvas.drawBitmap(bitmap, canvas.getWidth(), canvas.getHeight(), paint);

				}
			}
		}

		@Override
		public boolean isVisible() {
			return visible;
		}

		public void setVisible(boolean visible) {
			this.visible = visible;
		}

		@SuppressWarnings("unused")
		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		@SuppressWarnings("unused")
		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

	}

}
