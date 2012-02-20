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
import android.app.ListActivity;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;

/**
 * @author faxxe
 * 
 */
public class LayoutExaminer {
	private Activity currentActivity;

	public LayoutExaminer() {
		currentActivity = (Activity) Tutorial.getInstance(null).getActualContext();
		Log.i("faxxe", "LE: current Activity is " + currentActivity.getLocalClassName());
	}

	public Point getTabCenterCoordinates(String tab) {
		int w = 0;
		int h = 0;
		correctCurrentActivity();
		TabHost tabHost = (TabHost) currentActivity.findViewById(android.R.id.tabhost);

		ArrayList<View> tabViews = tabHost.getTouchables();
		LinearLayout tab1 = (LinearLayout) tabViews.get(0);
		LinearLayout tab2 = (LinearLayout) tabViews.get(1);
		LinearLayout tab3 = (LinearLayout) tabViews.get(2);
		int[] coords = { 0, 0 };
		if (tab.compareTo("Costumes") == 0) {
			tab2.getLocationInWindow(coords);
			w = tab2.getWidth();
			h = tab2.getHeight();
		}
		if (tab.compareTo("Scripts") == 0) {
			tab1.getLocationInWindow(coords);
			w = tab1.getWidth();
			h = tab1.getHeight();
		}
		if (tab.compareTo("Sounds") == 0) {
			tab3.getLocationInWindow(coords);
			w = tab3.getWidth();
			h = tab3.getHeight();
		}
		int x = coords[0];
		int y = coords[1];

		Point point = new Point(x + w / 2, y + h / 2);
		return point;
	}

	public void correctCurrentActivity() {
		if (currentActivity.getLocalClassName().compareTo("ui.ScriptActivity") == 0) {
			currentActivity = currentActivity.getParent();
		}
		if (currentActivity.getLocalClassName().compareTo("ui.CostumeActivity") == 0) {
			currentActivity = currentActivity.getParent();
		}
		if (currentActivity.getLocalClassName().compareTo("ui.SoundActivity") == 0) {
			currentActivity = currentActivity.getParent();
		}
	}

	public Point getButtonCenterCoordinates(int buttonID) {
		correctCurrentActivity();
		View buttonView = currentActivity.findViewById(buttonID);
		int[] coords = { 0, 0 };
		buttonView.getLocationInWindow(coords);
		int x = coords[0];
		int y = coords[1];
		int w = buttonView.getWidth();
		int h = buttonView.getHeight();
		Point point = new Point(x + w / 2, y + h / 2);
		return point;
	}

	public Point getListItemCenter(int itemNr) {
		//	if (isListItemClicked(listView, Integer.parseInt(notificationValue)) != -1) {

		Point point;
		Activity activity = (Activity) Tutorial.getInstance(null).getActualContext();
		ListActivity listActivity = (ListActivity) activity;
		ListView listView = listActivity.getListView();

		View child = listView.getChildAt(itemNr);
		int[] coords = { 0, 0 };
		child.getLocationInWindow(coords);
		int x = coords[0];
		int y = coords[1];
		int w = child.getWidth();
		int h = child.getHeight();
		point = new Point(60, y + h / 2);
		return point;
	}

}
