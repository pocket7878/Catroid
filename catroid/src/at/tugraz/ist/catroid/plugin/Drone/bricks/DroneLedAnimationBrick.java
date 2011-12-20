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
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.plugin.Drone.DroneHandler;
import at.tugraz.ist.catroid.plugin.Drone.other.DroneBrickListAdapter;
import at.tugraz.ist.catroid.plugin.Drone.other.DroneSelectTimeDialogInteger;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class DroneLedAnimationBrick implements Brick, OnItemClickListener, OnDismissListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	private String animationTitle;
	private String animationFrequencyTitle;
	private int animation;
	private float frequency;
	private int durationSeconds;

	private transient ArrayList<String> animationList;
	private transient ArrayList<String> animationFrequencyList;
	private transient Dialog animationDialog;
	private transient Dialog animationFrequencyDialog;

	private transient BaseAdapter adapter;

	@XStreamOmitField
	private transient View view;

	public DroneLedAnimationBrick(Sprite sprite, int animation, float frequency, int durationSeconds) {
		this.sprite = sprite;
		this.animation = animation;
		this.frequency = frequency;
		this.durationSeconds = durationSeconds;
	}

	public void execute() {
		DroneHandler.getInstance().getDrone().playLedAnimation(animation, frequency, durationSeconds);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(final Context context, int brickId, BaseAdapter adapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_drone_led_animation, null);
		}

		this.adapter = adapter;

		animationList = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(
				R.array.drone_led_animations)));
		animationFrequencyList = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(
				R.array.drone_speed)));

		Button animationButton = (Button) view.findViewById(R.id.btLedAnimationChoose);
		Button animationFrequencyButton = (Button) view.findViewById(R.id.btLedAnimationFrequencyChoose);

		if (animationTitle != null) {
			animationButton.setText(animationTitle);
		} else {
			animationButton.setText(context.getString(R.string.drone_choose_animation_title));
		}

		if (animationFrequencyTitle != null) {
			animationFrequencyButton.setText(animationFrequencyTitle);
		} else {
			animationFrequencyButton.setText(context.getString(R.string.drone_choose_animation_frequency_title));
		}

		final DroneBrickListAdapter ledAnimationBrickAdapter = new DroneBrickListAdapter(context, animationList);
		animationButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				animationDialog = new Dialog(context);
				animationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				animationDialog.setContentView(R.layout.drone_list);
				animationDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);

				ListView list = (ListView) animationDialog.findViewById(R.id.drone_list);
				list.setAdapter(ledAnimationBrickAdapter);
				list.setOnItemClickListener(DroneLedAnimationBrick.this);

				animationDialog.show();
			}
		});

		final DroneBrickListAdapter ledAnimationFrequencyBrickAdapter = new DroneBrickListAdapter(context,
				animationFrequencyList);
		animationFrequencyButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				animationFrequencyDialog = new Dialog(context);
				animationFrequencyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				animationFrequencyDialog.setContentView(R.layout.drone_list);
				animationFrequencyDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);

				ListView list = (ListView) animationFrequencyDialog.findViewById(R.id.drone_list);
				list.setAdapter(ledAnimationFrequencyBrickAdapter);
				list.setOnItemClickListener(DroneLedAnimationBrick.this);

				animationFrequencyDialog.show();
			}
		});

		EditText editDuration = (EditText) view.findViewById(R.id.drone_edit_led_aniamtion_duration);
		editDuration.setText(String.valueOf(durationSeconds));
		DroneSelectTimeDialogInteger dialogDuration = new DroneSelectTimeDialogInteger(context, durationSeconds);
		dialogDuration.setOnDismissListener(this);
		dialogDuration.setOnCancelListener((OnCancelListener) context);
		editDuration.setOnClickListener(dialogDuration);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_drone_led_animation, null);
	}

	@Override
	public Brick clone() {
		return new DroneLedAnimationBrick(getSprite(), animation, frequency, durationSeconds);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getCount() == animationList.size()) {
			animationTitle = animationList.get(position);
			animation = position;
			animationDialog.dismiss();
		} else {
			animationFrequencyTitle = animationFrequencyList.get(position);
			frequency = position + 0.5f;
			animationFrequencyDialog.dismiss();
		}
		adapter.notifyDataSetChanged();
	}

	public void onDismiss(DialogInterface dialog) {
		durationSeconds = (int) Math.round(((DroneSelectTimeDialogInteger) dialog).getValue());
		adapter.notifyDataSetChanged();
		dialog.cancel();
	}

	public int getRequiredResources() {
		return WIFI_DRONE;
	}

}
