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
package at.tugraz.ist.catroid.plugin.Drone.bricks;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.plugin.Drone.DroneHandler;
import at.tugraz.ist.catroid.plugin.Drone.other.DroneBrickListAdapter;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class DroneChangeFlyingModeBrick implements Brick, OnItemClickListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	private String title;
	private int flyingMode;

	private transient ArrayList<String> flyingModeList;
	private transient Dialog flyingModeDialog;

	private transient BaseAdapter adapter;

	@XStreamOmitField
	private transient View view;

	public DroneChangeFlyingModeBrick(Sprite sprite, int flyingMode) {
		this.sprite = sprite;
		this.flyingMode = flyingMode;
	}

	public void execute() {
		DroneHandler.getInstance().getDrone().changeFlyingMode(flyingMode);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(final Context context, int brickId, BaseAdapter adapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_drone_change_flyingmode, null);
		}

		this.adapter = adapter;

		flyingModeList = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(
				R.array.drone_flying_modes)));

		Button flyingStateButton = (Button) view.findViewById(R.id.btFlyingStateChoose);

		if (title != null) {
			flyingStateButton.setText(title);
		} else {
			flyingStateButton.setText(context.getString(R.string.drone_choose_flyingstate_title));
		}

		final DroneBrickListAdapter flyingModeBrickAdapter = new DroneBrickListAdapter(context, flyingModeList);
		flyingStateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				flyingModeDialog = new Dialog(context);
				flyingModeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				flyingModeDialog.setContentView(R.layout.drone_list);
				flyingModeDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);

				ListView list = (ListView) flyingModeDialog.findViewById(R.id.drone_list);
				list.setAdapter(flyingModeBrickAdapter);
				list.setOnItemClickListener(DroneChangeFlyingModeBrick.this);

				flyingModeDialog.show();
			}
		});

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_drone_change_flyingmode, null);
	}

	@Override
	public Brick clone() {
		return new DroneChangeFlyingModeBrick(getSprite(), flyingMode);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		title = flyingModeList.get(position);
		flyingMode = position;
		flyingModeDialog.dismiss();
		adapter.notifyDataSetChanged();
	}

	public int getRequiredResources() {
		return WIFI_DRONE;
	}

}
