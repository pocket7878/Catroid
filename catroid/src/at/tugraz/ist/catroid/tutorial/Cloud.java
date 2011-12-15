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

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.tutorial.tasks.Task;
import at.tugraz.ist.catroid.tutorial.tasks.Task.Notification;

public class Cloud {
	private static Cloud cloud;
	int focusX1;
	int focusY1;
	int focusX2;
	int focusY2;

	private Drawable drawableInnerPart;
	private Drawable drawableOuterPartRight;
	private Drawable drawableOuterPartLeft;
	private Drawable drawableOuterPartTop;
	private Drawable drawableOuterPartBottom;

	private int timeOfLastUpdate;
	private static final int updateAfterMilliseconds = 300;

	private Context context;

	private Cloud() {
	}

	public static Cloud getInstance(Context context) {
		if (cloud == null) {
			cloud = new Cloud();
		}
		if (context != null) {
			cloud.setContext(context);
		}
		cloud.clearCloud();
		return (cloud);
	}

	void setContext(Context context) {
		this.context = context;
	}

	public void setCloud(Notification notification) {
		if (context == null) {
			return;
		}

		if (notification == Task.Notification.CURRENT_PROJECT_BUTTON) {
			Button newProjectButton = (Button) ((Activity) context).findViewById(R.id.current_project_button);
			setBitmap();
			fetchViewPosition(newProjectButton);
		}

		if (notification == Task.Notification.TAB_COSTUMES) {
			Activity currentActivity = ((Activity) context).getParent();
			TabHost tabHost = (TabHost) currentActivity.findViewById(android.R.id.tabhost);
			ArrayList<View> tabViews = tabHost.getTouchables();
			LinearLayout tab = (LinearLayout) tabViews.get(1);
			setBitmap();
			fetchViewPosition(tab);
		}

		if (notification == Task.Notification.TAB_SCRIPTS) {
			Activity currentActivity = ((Activity) context).getParent();
			TabHost tabHost = (TabHost) currentActivity.findViewById(android.R.id.tabhost);
			ArrayList<View> tabViews = tabHost.getTouchables();
			LinearLayout tab = (LinearLayout) tabViews.get(0);
			setBitmap();
			fetchViewPosition(tab);
		}

		if (notification == Task.Notification.TAB_SOUNDS) {
			Activity currentActivity = ((Activity) context).getParent();
			TabHost tabHost = (TabHost) currentActivity.findViewById(android.R.id.tabhost);
			ArrayList<View> tabViews = tabHost.getTouchables();
			LinearLayout tab = (LinearLayout) tabViews.get(2);
			setBitmap();
			fetchViewPosition(tab);
		}

		updateCloudPosition();
	}

	void setBitmap() {
		drawableInnerPart = context.getResources().getDrawable(R.drawable.cloud_inner_part);
		drawableOuterPartRight = context.getResources().getDrawable(R.drawable.cloud_outer_part);
		drawableOuterPartLeft = context.getResources().getDrawable(R.drawable.cloud_outer_part);
		drawableOuterPartTop = context.getResources().getDrawable(R.drawable.cloud_outer_part);
		drawableOuterPartBottom = context.getResources().getDrawable(R.drawable.cloud_outer_part);
	}

	void updateCloudPosition() {
		Rect bounds = new Rect();
		bounds.set(focusX1, focusY1, focusX2, focusY2);
		drawableInnerPart.setBounds(bounds);

		Rect boundsOuterRight = new Rect();
		boundsOuterRight.set(focusX2, focusY1, Values.SCREEN_WIDTH, focusY2);
		drawableOuterPartRight.setBounds(boundsOuterRight);

		Rect boundsOuterLeft = new Rect();
		boundsOuterLeft.set(0, focusY1, focusX1, focusY2);
		drawableOuterPartLeft.setBounds(boundsOuterLeft);

		Rect boundsOuterTop = new Rect();
		boundsOuterTop.set(0, 0, Values.SCREEN_WIDTH, focusY1);
		drawableOuterPartTop.setBounds(boundsOuterTop);

		Rect boundsOuterBottom = new Rect();
		boundsOuterBottom.set(0, focusY2, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
		drawableOuterPartBottom.setBounds(boundsOuterBottom);
	}

	void fetchViewPosition(View view) {
		int location[] = new int[2];
		view.getLocationOnScreen(location);

		int width = view.getWidth();
		int height = view.getHeight();
		focusX1 = location[0];
		focusY1 = location[1];
		focusX2 = location[0] + width;
		focusY2 = location[1] + height;
	}

	public void clearCloud() {
		focusX1 = -1;
		focusY1 = -1;
		focusX2 = -1;
		focusY2 = -1;
	}

	public void update(long gameTime) {
		if (focusX1 != -1) {
			if (updateAfterMilliseconds > (gameTime - timeOfLastUpdate)) {
				// dann tua mol bissi desn fokus bewegen
			}
		}
	}

	public void draw(Canvas canvas) {

		if (focusX1 != -1) {
			drawableInnerPart.draw(canvas);
			drawableOuterPartRight.draw(canvas);
			drawableOuterPartLeft.draw(canvas);
			drawableOuterPartTop.draw(canvas);
			drawableOuterPartBottom.draw(canvas);
		}
	}

	private int getRelativeLeft(View myView) {
		if (myView.getParent() == myView.getRootView()) {
			return myView.getLeft();
		} else {
			return myView.getLeft() + getRelativeLeft((View) myView.getParent());
		}
	}

	private int getRelativeTop(View myView) {
		if (myView.getParent() == myView.getRootView()) {
			return myView.getTop();
		} else {
			return myView.getTop() + getRelativeTop((View) myView.getParent());
		}
	}
}