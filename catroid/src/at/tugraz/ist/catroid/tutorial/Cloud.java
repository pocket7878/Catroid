/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.tutorial;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;

public class Cloud implements SurfaceObject {
	private Paint paint = new Paint();
	private static Context context;
	private static Cloud cloud = null;
	private static int alpha = 255;
	float radius = 90;
	float startX = 0;
	float startY = 0;
	float targetAlpha = 255;
	boolean show = false;
	double actX = 0;
	double actY = 0;
	float sollX = 100;
	float sollY = 100;
	boolean visible;
	ClickableArea clickableArea;

	private Cloud() {

	}

	public void clear() {
		cloud = null;
	}

	public static Cloud getInstance(Context con) {
		if (con != null) {
			context = con;
			alpha = 255; // not necessary but cool!
		}
		if (cloud == null) {
			cloud = new Cloud();
		}
		cloud.fadeIn();
		return cloud;
	}

	@Override
	public void update(long time) {

	}

	public void addYourselfToTutorialOverlay(TutorialOverlay tutorialOverlay) {
		tutorialOverlay.addCloud(this);
	}

	@Override
	public void draw(Canvas canvas) {
		//	Log.i("faxxe", "Cloud: draw!" + alpha);
		int height = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
		int width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
		paint.setColor(Color.GRAY);
		paint.setAlpha(alpha);
		Path path = new Path();
		path.moveTo(0, 0);
		path.lineTo(width, 0);
		path.lineTo(width, height);
		path.lineTo(0, height);
		path.lineTo(0, 0);
		if (alpha > targetAlpha) {
			alpha -= 2;
		}
		if (visible) {
			path.addCircle((int) actX, (int) actY, radius, Direction.CCW);
		}
		canvas.drawPath(path, paint);

		updateXY();

	}

	public void updateXY() {
		//fix the following 6 lines for all directions!
		float deltax = sollX;
		float deltay = sollY;
		double dist = Math.sqrt(sollX * sollX + sollY * sollY);
		dist /= 7;
		deltax /= dist;
		deltay /= dist;

		if (actX < sollX) {
			actX += deltax;
		}
		if (actY < sollY) {
			actY += deltay;
		}

	}

	public void fadeTo(ClickableArea ca) {
		this.clickableArea = ca;
		this.sollX = ca.centerX;
		this.sollY = ca.centerY;
		actX = 0;
		actY = 0;
		startX = 0;
		startY = 0;
	}

	public void fadeIn() {
		this.targetAlpha = 125;
	}

	public void fadeOut() {
		this.targetAlpha = 255;
	}

	public void jumpTo(ClickableArea ca) {
		this.clickableArea = ca;
		this.actX = ca.centerX;
		this.actY = ca.centerY;
		this.startX = ca.centerX;
		this.startY = ca.centerY;
	}

	public void disappear() {
		this.visible = false;
		this.sollX = 0;
		this.sollY = 0;

	}

	public void show() {
		this.visible = true;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public ClickableArea getClickableArea() {
		return this.clickableArea;
	}
}
