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
package at.tugraz.ist.catroid.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;

public class BounceListView extends ListView {

	private static final int MAX_Y_OVERSCROLL_DISTANCE = 100;
	private int maxYOverscrollDistance;
	private Context context;

	public BounceListView(Context context) {
		super(context);
		this.context = context;
		initBounceListView();
	}

	public BounceListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initBounceListView();
	}

	public BounceListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initBounceListView();
	}

	private void initBounceListView() {
		final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		final float density = metrics.density;

		maxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);

		if (Build.VERSION.SDK_INT <= 8) {
			initOldLookAndFeel();
		}
	}

	private void initOldLookAndFeel() {
		setHorizontalFadingEdgeEnabled(true);
		setVerticalFadingEdgeEnabled(true);
		setFadingEdgeLength(100);
		setHorizontalScrollBarEnabled(true);
		setVerticalScrollBarEnabled(true);
		setScrollbarFadingEnabled(false);
	}

	@TargetApi(9)
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
			int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		//This is where the magic happens, we have replaced the incoming maxOverScrollY with our own custom variable mMaxYOverscrollDistance; 
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
				maxYOverscrollDistance, isTouchEvent);
	}
}