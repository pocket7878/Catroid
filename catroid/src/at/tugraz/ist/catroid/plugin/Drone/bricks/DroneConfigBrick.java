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
import android.util.Log;
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
import at.tugraz.ist.catroid.plugin.Drone.DroneConsts;
import at.tugraz.ist.catroid.plugin.Drone.DroneHandler;
import at.tugraz.ist.catroid.plugin.Drone.other.DroneBrickListAdapter;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class DroneConfigBrick implements Brick, OnItemClickListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	private String configKeyTitle;
	private int configKey;
	private String configValueTitle;
	private float configValue;

	private transient ArrayList<String> configList;
	private transient ArrayList<String> configValueList;
	private transient Dialog configKeyDialog;
	private transient Dialog configValueDialog;
	private transient DroneBrickListAdapter configKeyBrickAdapter;
	private transient DroneBrickListAdapter configValueBrickAdapter;
	private transient Button configKeyButton;
	private transient Button configValueButton;

	private transient BaseAdapter adapter;

	@XStreamOmitField
	private transient View view;

	public DroneConfigBrick(Sprite sprite, int configKey, float configValue) {
		this.sprite = sprite;
		this.configKey = configKey;
		this.configValue = configValue;
	}

	public void execute() {

		String cmd = "";

		switch (configKey) {
		// altitude_max
			case 0:
				if (configValue == 0) {
					cmd = Conf_altitude_max.m1;
				} else if (configValue == 1) {
					cmd = Conf_altitude_max.m2;
				} else if (configValue == 2) {
					cmd = Conf_altitude_max.m3;
				} else if (configValue == 3) {
					cmd = Conf_altitude_max.m4;
				} else if (configValue == 4) {
					cmd = Conf_altitude_max.m5;
				}
				break;
			// euler_angle_max
			case 1:
				if (configValue == 0) {
					cmd = Conf_euler_angle_max.veryLow;
				} else if (configValue == 1) {
					cmd = Conf_euler_angle_max.low;
				} else if (configValue == 2) {
					cmd = Conf_euler_angle_max.normal;
				} else if (configValue == 3) {
					cmd = Conf_euler_angle_max.high;
				} else if (configValue == 4) {
					cmd = Conf_euler_angle_max.veryHigh;
				}
				break;
			// control_vz_max
			case 2:
				if (configValue == 0) {
					cmd = Conf_control_vz_max.veryLow;
				} else if (configValue == 1) {
					cmd = Conf_control_vz_max.low;
				} else if (configValue == 2) {
					cmd = Conf_control_vz_max.normal;
				} else if (configValue == 3) {
					cmd = Conf_control_vz_max.high;
				} else if (configValue == 4) {
					cmd = Conf_control_vz_max.veryHigh;
				}
				break;
			// control_yaw
			case 3:
				if (configValue == 0) {
					cmd = Conf_control_yaw.veryLow;
				} else if (configValue == 1) {
					cmd = Conf_control_yaw.low;
				} else if (configValue == 2) {
					cmd = Conf_control_yaw.normal;
				} else if (configValue == 3) {
					cmd = Conf_control_yaw.high;
				} else if (configValue == 4) {
					cmd = Conf_control_yaw.veryHigh;
				}
				break;
			// video_channel
			case 4:
				if (configValue == 0) {
					cmd = Conf_video_channel.hori;
				} else if (configValue == 1) {
					cmd = Conf_video_channel.vert;
				} else if (configValue == 2) {
					cmd = Conf_video_channel.horiSmallVert;
				} else if (configValue == 3) {
					cmd = Conf_video_channel.largeVertSmallHori;
				}
				break;
		}

		try {
			int counter = 0;
			while (!DroneHandler.getInstance().getDrone().setConfiguration("AT*CONFIG=#SEQ#," + cmd, true)) {
				if (counter++ > 2) {
					break;
				}
			}
		} catch (Exception e) {
			Log.e(DroneConsts.DroneLogTag, "Exception DroneConfigBrick -> execute()", e);
		}

	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(final Context context, int brickId, BaseAdapter adapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_drone_config, null);
		}

		this.adapter = adapter;

		configList = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(
				R.array.drone_config_settings)));
		configValueList = new ArrayList<String>(Arrays.asList(context.getResources()
				.getStringArray(R.array.drone_speed)));

		configKeyButton = (Button) view.findViewById(R.id.btConfigKeyChoose);
		configValueButton = (Button) view.findViewById(R.id.btConfigValueChoose);

		configKeyBrickAdapter = new DroneBrickListAdapter(context, configList);
		configKeyButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				configKeyDialog = new Dialog(context);
				configKeyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				configKeyDialog.setContentView(R.layout.drone_list);
				configKeyDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);

				ListView list = (ListView) configKeyDialog.findViewById(R.id.drone_list);
				list.setAdapter(configKeyBrickAdapter);
				list.setOnItemClickListener(DroneConfigBrick.this);

				configKeyDialog.show();
			}
		});

		configValueBrickAdapter = new DroneBrickListAdapter(context, configValueList);
		configValueButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				configValueDialog = new Dialog(context);
				configValueDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				configValueDialog.setContentView(R.layout.drone_list);
				configValueDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);

				ListView list = (ListView) configValueDialog.findViewById(R.id.drone_list);
				list.setAdapter(configValueBrickAdapter);
				list.setOnItemClickListener(DroneConfigBrick.this);

				configValueDialog.show();
			}
		});

		if (configValueTitle != null) {
			configValueButton.setText(configValueTitle);
		} else {
			configValueButton.setText(context.getString(R.string.drone_choose_config_value_title));
		}

		if (configKeyTitle != null) {
			configKeyButton.setText(configKeyTitle);
			configValueButton.setClickable(true);
			configValueButton.setVisibility(Button.VISIBLE);

			if (configKeyTitle.equals(configList.get(0))) {
				configValueList = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(
						R.array.drone_max_altitude)));
				configValueBrickAdapter = new DroneBrickListAdapter(context, configValueList);
			} else if (configKeyTitle.equals(configList.get(4))) {
				configValueList = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(
						R.array.drone_video_channel)));
				configValueBrickAdapter = new DroneBrickListAdapter(context, configValueList);
			} else {
				configValueList = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(
						R.array.drone_speed)));
				configValueBrickAdapter = new DroneBrickListAdapter(context, configValueList);
			}

		} else {
			configKeyButton.setText(context.getString(R.string.drone_choose_config_key_title));
			configValueButton.setClickable(false);
			configValueButton.setVisibility(Button.GONE);
		}

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_drone_config, null);
	}

	@Override
	public Brick clone() {
		return new DroneConfigBrick(getSprite(), configKey, configValue);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getAdapter().getItem(0) == configList.get(0)) {
			adapter.notifyDataSetChanged();
			configKeyTitle = configList.get(position);
			configKey = position;

			if (position == 0) {
				configValueList = new ArrayList<String>(Arrays.asList(parent.getContext().getResources()
						.getStringArray(R.array.drone_max_altitude)));
				configValueBrickAdapter = new DroneBrickListAdapter(parent.getContext(), configValueList);
				configValue = 0;
				configValueTitle = null;
			} else if (position == 4) {
				configValueList = new ArrayList<String>(Arrays.asList(parent.getContext().getResources()
						.getStringArray(R.array.drone_video_channel)));
				configValueBrickAdapter = new DroneBrickListAdapter(parent.getContext(), configValueList);
				configValue = 0;
				configValueTitle = null;
			} else {
				configValueList = new ArrayList<String>(Arrays.asList(parent.getContext().getResources()
						.getStringArray(R.array.drone_speed)));
				configValueBrickAdapter = new DroneBrickListAdapter(parent.getContext(), configValueList);
				configValue = 0;
				configValueTitle = null;
			}

			configKeyDialog.dismiss();

		} else {
			adapter.notifyDataSetChanged();
			configValueTitle = configValueList.get(position);
			configValue = position;
			configValueDialog.dismiss();
		}

		adapter.notifyDataSetChanged();
	}

	public int getRequiredResources() {
		return WIFI_DRONE;
	}

	public static final class Conf_altitude_max {
		public static final String m1 = "\"control:altitude_max\",\"1000\"";
		public static final String m2 = "\"control:altitude_max\",\"2000\"";
		public static final String m3 = "\"control:altitude_max\",\"3000\"";
		public static final String m4 = "\"control:altitude_max\",\"4000\"";
		public static final String m5 = "\"control:altitude_max\",\"5000\"";
	}

	public static final class Conf_euler_angle_max {
		public static final String veryLow = "\"control:euler_angle_max\",\"0.1\"";
		public static final String low = "\"control:euler_angle_max\",\"0.2\"";
		public static final String normal = "\"control:euler_angle_max\",\"0.3\"";
		public static final String high = "\"control:euler_angle_max\",\"0.4\"";
		public static final String veryHigh = "\"control:euler_angle_max\",\"0.52\"";
	}

	public static final class Conf_control_vz_max {
		public static final String veryLow = "\"control:control_vz_max\",\"200\"";
		public static final String low = "\"control:control_vz_max\",\"650\"";
		public static final String normal = "\"control:control_vz_max\",\"1100\"";
		public static final String high = "\"control:control_vz_max\",\"1550\"";
		public static final String veryHigh = "\"control:control_vz_max\",\"2000\"";
	}

	public static final class Conf_control_yaw {
		public static final String veryLow = "\"control:control_yaw\",\"0.7\"";
		public static final String low = "\"control:control_yaw\",\"2.05\"";
		public static final String normal = "\"control:control_yaw\",\"3.41\"";
		public static final String high = "\"control:control_yaw\",\"4.76\"";
		public static final String veryHigh = "\"control:control_yaw\",\"6.11\"";
	}

	public static final class Conf_video_channel {
		public static final String hori = "\"video:video_channel\",\"0\"";
		public static final String vert = "\"video:video_channel\",\"1\"";
		public static final String horiSmallVert = "\"video:video_channel\",\"2\"";
		public static final String largeVertSmallHori = "\"video:video_channel\",\"3\"";
	}

}
