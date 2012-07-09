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
import android.app.Dialog;
import android.app.ListActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import at.tugraz.ist.catroid.R;

/**
 * @author faxxe
 * 
 */
public class LayoutExaminer {
	private Activity currentActivity;

	public LayoutExaminer() {
		currentActivity = (Activity) Tutorial.getInstance(null).getActualContext();
	}

	public ClickableArea getTabCenterCoordinates(String tab) {
		int w = 0;
		int h = 0;
		correctCurrentActivity();
		TabHost tabHost = (TabHost) currentActivity.findViewById(android.R.id.tabhost);

		ArrayList<View> tabViews = tabHost.getTouchables();
		LinearLayout tab1 = (LinearLayout) tabViews.get(0);
		LinearLayout tab2 = (LinearLayout) tabViews.get(1);
		LinearLayout tab3 = (LinearLayout) tabViews.get(2);
		int[] coords = { 0, 0 };
		int[] coordsXYWH = { 0, 0, 0, 0 };
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
		coordsXYWH[0] = coords[0];
		coordsXYWH[1] = coords[1];
		coordsXYWH[2] = w;
		coordsXYWH[3] = h;
		ClickableArea ca = new ClickableArea(x, y, w, h);
		return ca;
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

	public ClickableArea examineCategoryBrickDialog(int itemNr) {
		Dialog dialog = Tutorial.getInstance(null).getDialog();
		ListView dings = (ListView) dialog.findViewById(R.id.categoriesListView);
		View tmp = null;
		if (dings.getChildCount() < itemNr) {
			itemNr = 0;
		}
		while (tmp == null) {
			tmp = dings.getChildAt(itemNr);
			Log.i("faxxe", "bowling for colombine....");
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		int[] coords = { 0, 0 };
		tmp.getLocationOnScreen(coords);
		float x = coords[0];
		float y = coords[1];
		return new ClickableArea(x, y, tmp.getWidth(), tmp.getHeight());
	}

	public ClickableArea examineAddBrickDialog(int itemNr) {
		Dialog dialog = Tutorial.getInstance(null).getDialog();
		ListView dings = (ListView) dialog.findViewById(R.id.brick_list_view);
		View tmp = null;
		if (dings.getChildCount() < itemNr) {
			Log.i("faxxe", "LE: There is no such item! in ADD-Brick-- resetting");
			itemNr = 0;
		}
		while (tmp == null) {
			tmp = dings.getChildAt(itemNr);
			Log.i("faxxe", "...waitin' for Dialog to come...");
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		int[] coords = { 0, 0 };
		tmp.getLocationOnScreen(coords);
		float x = coords[0];
		float y = coords[1];
		return new ClickableArea(x, y, tmp.getWidth(), tmp.getHeight());
	}

	public ClickableArea getButtonCenterCoordinates(int buttonID) {
		Log.i("faxxe", "LE: getButtenCenterCoordinates: " + currentActivity.getLocalClassName());
		correctCurrentActivity();
		View buttonView = currentActivity.findViewById(buttonID);
		int[] coords = { 0, 0 };
		buttonView.getLocationInWindow(coords);
		int x = coords[0];
		int y = coords[1];
		int w = buttonView.getWidth();
		int h = buttonView.getHeight();
		ClickableArea ca = new ClickableArea(x, y, w, h);
		return ca;
	}

	public ClickableArea getListItemCenter(int itemNr) {
		//	if (isListItemClicked(listView, Integer.parseInt(notificationValue)) != -1) {

		Activity activity = (Activity) Tutorial.getInstance(null).getActualContext();
		ListActivity listActivity = (ListActivity) activity;
		ListView listView = listActivity.getListView();
		View child = listView.getChildAt(itemNr);
		int[] coords = { 0, 0 };
		child.getLocationInWindow(coords);
		int x = 50;
		int y = coords[1];
		int w = 50;
		int h = child.getHeight();
		ClickableArea ca = new ClickableArea(x, y, w, h);
		return ca;
	}

}
