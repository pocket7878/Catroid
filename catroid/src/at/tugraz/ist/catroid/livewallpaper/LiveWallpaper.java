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
		private boolean visible = true;
		private int width;
		private int height;

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

		/**
		 * @return the visible
		 */
		@Override
		public boolean isVisible() {
			return visible;
		}

		/**
		 * @param visible
		 *            the visible to set
		 */
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
