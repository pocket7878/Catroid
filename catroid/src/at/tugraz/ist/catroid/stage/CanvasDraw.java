package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * 
 * Draws DrawObjects into a canvas.
 * 
 * @author Thomas Holzmann
 *
 */
public class CanvasDraw implements IDraw {
	private Canvas mCanvas=null;
	private SurfaceView mSurfaceView;
	private Paint mWhitePaint;
	private SurfaceHolder mHolder;
	private ArrayList<Sprite> mSpritesList;
	//TODO destroy surface somewhere!
	
	public CanvasDraw(ArrayList<Sprite> spritesList){
		super();
		mSurfaceView = StageActivity.mStage;
		mHolder = mSurfaceView.getHolder();
		mWhitePaint = new Paint();
		mWhitePaint.setStyle(Paint.Style.FILL);
		mWhitePaint.setColor(Color.WHITE);
		mSpritesList = spritesList;
		
	}

	public synchronized void draw() {
//		if (drawObject.getBitmap() == null)
//			Log.i("CanvasDraw", "draw: no bitmap!");
//		if (mCanvas == null)
//			Log.i("CanvasDraw", "draw: no canvas!");
		mCanvas = mHolder.lockCanvas();
		if (mCanvas != null) {
			// we want to start with a white rectangle
			mCanvas.drawRect(new Rect(0, 0, mCanvas.getWidth(), mCanvas.getHeight()),
				mWhitePaint);
			for (int i=0; i<mSpritesList.size(); i++){
				DrawObject drawObject = mSpritesList.get(i).mDrawObject;
				if (drawObject.getBitmap() != null)
					mCanvas.drawBitmap(drawObject.getBitmap(), drawObject.getPosition().first, drawObject.getPosition().second, null);
			}
			mHolder.unlockCanvasAndPost(mCanvas);
		}
				
	}


}
