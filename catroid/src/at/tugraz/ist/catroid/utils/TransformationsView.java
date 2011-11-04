package at.tugraz.ist.catroid.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

public class TransformationsView extends View {
	private Bitmap bitmap;
	private Point position;

	public TransformationsView(Context context) {
		super(context);
		position = new Point(0, 0);
	}

	public void setPosition(int x, int y) {
		position.set(x, y);
	}

	public void setBitmap(Bitmap toDraw) {
		bitmap = toDraw;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawBitmap(bitmap, position.x - bitmap.getWidth() / 2, position.y - bitmap.getHeight() / 2, null);
	}
}