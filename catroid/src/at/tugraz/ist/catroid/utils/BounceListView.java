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

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class BounceListView extends ListView implements OnScrollListener {
	private ListAdapter adapter;
	private boolean bounceTop, bounceBottom;
	private int originalPaddingTop, originalPaddingBottom;
	private float downY;

	public BounceListView(Context context) {
		super(context);
		initBounceListView();
	}

	public BounceListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initBounceListView();
	}

	public BounceListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initBounceListView();
	}

	private void initBounceListView() {
		bounceTop = false;
		bounceBottom = false;
		originalPaddingTop = getPaddingTop();
		originalPaddingBottom = getPaddingBottom();

		setOnScrollListener(this);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		this.adapter = adapter;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (adapter == null) {
			return super.onTouchEvent(event);
		}

		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if (bounceTop || bounceBottom) {
					resetPadding();
				}
				break;
			case MotionEvent.ACTION_DOWN:
				downY = event.getY();

				break;
			case MotionEvent.ACTION_MOVE:
				applyPadding(event);
				break;
		}
		return super.onTouchEvent(event);
	}

	private void applyPadding(MotionEvent event) {
		if (event == null) {
			return;
		}

		if (bounceTop) {
			int topPadding = (int) ((event.getY() - downY) / 1.7);

			if (topPadding > 100) {
				topPadding = 100;
			} else if (topPadding < 0) {
				topPadding = 0;
			}

			setPadding(getPaddingLeft(), topPadding, getPaddingRight(), getPaddingBottom());
		}
		if (bounceBottom) {
			int bottomPadding = (int) ((event.getY() - downY) / -1.7);

			if (bottomPadding > 100) {
				bottomPadding = 100;
			} else if (bottomPadding < 0) {
				bottomPadding = 0;
			}

			setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), bottomPadding);
		}
	}

	private void resetPadding() {
		if (bounceTop) {
			setPadding(getPaddingLeft(), originalPaddingTop, getPaddingRight(), getPaddingBottom());
		}
		if (bounceBottom) {
			setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), originalPaddingBottom);
		}

		bounceTop = false;
		bounceBottom = false;
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (adapter == null) {
			return;
		}

		if (getFirstVisiblePosition() == 0) {
			bounceTop = true;
		} else {
			bounceTop = false;
			setPadding(getPaddingLeft(), originalPaddingTop, getPaddingRight(), getPaddingBottom());
		}
		if (getLastVisiblePosition() == adapter.getCount() - 1) {
			bounceBottom = true;
		} else {
			bounceBottom = false;
			setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), originalPaddingBottom);
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	// just supported for api > 8
	// have a look at http://jasonfry.co.uk/blog/android-overscroll-revisited/ for details of this method
	//	@Override
	//	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
	//			int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
	//		//This is where the magic happens, we have replaced the incoming maxOverScrollY with our own custom variable mMaxYOverscrollDistance; 
	//		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
	//				mMaxYOverscrollDistance, isTouchEvent);
	//	}

}