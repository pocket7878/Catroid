/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.plugin.Drone.other;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class DroneBrickListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> flyingModeList;

	public DroneBrickListAdapter(Context context, ArrayList<String> flyingModeList) {
		this.context = context;
		this.flyingModeList = flyingModeList;
	}

	public int getCount() {
		return flyingModeList.size();
	}

	public Object getItem(int arg0) {
		return flyingModeList.get(arg0);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parent.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		TextView textView = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null);
		textView.setText(flyingModeList.get(position));

		return textView;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
