package at.tugraz.ist.catroid.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

public class TransformationsView extends View{
	private Bitmap bitmap;
	private int windowHeight;
	private int windowWidth;
	private Point screenCenter;
	private Point position;
	
	public TransformationsView(Context context) {
		super(context);
		position = new Point(0,0);
	}

	public void setPosition(int x, int y) {
		position.set(x,y);
	}
	
	public void setBitmap(Bitmap toDraw){
		bitmap = toDraw;
	}
	
	//TODO will be obsolete
	public void setWindowDimensions(int height, int width){
		windowHeight = height;
		windowWidth = width;
		screenCenter = new Point((width-bitmap.getWidth())/2, height/2-bitmap.getHeight());
	}
	
	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	
		canvas.drawBitmap(bitmap, position.x - bitmap.getWidth()/2, position.y - bitmap.getHeight()/2, null);
	}
}