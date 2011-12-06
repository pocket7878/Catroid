package at.tugraz.ist.catroid.tutorial;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.SurfaceHolder;

public class AnimationThread extends Thread {
	private TutorialOverlay mOverlay;
	private SurfaceHolder mHolder;
	private volatile boolean mRun = false;

	public AnimationThread(TutorialOverlay overlay) {
		mOverlay = overlay;
		mHolder = mOverlay.getHolder();
	}

	public void setRunning(boolean run) {
		mRun = run;
	}

	@Override
	public void run() {
		Canvas canvas = null;
		while (mRun) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				//pfusch
			}
			canvas = mHolder.lockCanvas();
			synchronized (mHolder) {
				if (canvas != null) {
					canvas.drawColor(0, PorterDuff.Mode.CLEAR);
					mOverlay.update();
					mOverlay.onDraw(canvas);
					mHolder.unlockCanvasAndPost(canvas);
				}
			}
		}

		canvas = mHolder.lockCanvas();
		if (canvas != null) {
			canvas.drawColor(0, PorterDuff.Mode.CLEAR);
			// verzweifelter versuch das bild zu clearen
			Paint paint = new Paint();
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			canvas.drawPaint(paint);
			mHolder.unlockCanvasAndPost(canvas);
		}

	}
}