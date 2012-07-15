package at.tugraz.ist.catroid.livewallpaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class LiveWallpaper extends WallpaperService {

	@Override
	public Engine onCreateEngine() {
		return new CatWallEngine();
	}

	private class CatWallEngine extends Engine {
		private final Handler handler = new Handler();
		private final Runnable run = new Runnable() {
			public void run() {
				draw();
			}
		};

		private boolean visible = true;
		private int width;
		int height;

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
			this.width = width;
			this.height = height;
			super.onSurfaceChanged(holder, format, width, height);
		}

		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;
			try {
				c = holder.lockCanvas();
				if (c != null) {
					c.drawColor(Color.WHITE);
				}
			} finally {
				if (c != null) {
					holder.unlockCanvasAndPost(c);
				}
			}
			handler.removeCallbacks(run);
			//handler.postDelayed(run, 1000/targetFramerate -(System.currentTimeMillis() - mLastTime));

		}

	}

}
