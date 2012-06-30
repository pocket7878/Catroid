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
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.plugin.Drone.DroneServiceHandler;
import at.tugraz.ist.catroid.plugin.Drone.other.DroneMoveBrickChooseMovementDialog;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class DroneMoveBrick implements Brick, OnSeekBarChangeListener, OnDismissListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	private transient ArrayList<String> movementList;
	private boolean[] options;
	private int velocity;
	private int duration;
	private transient Dialog dialog;
	private transient SeekBar sbMoveSpeed;
	private transient SeekBar sbMoveDuration;
	private transient TextView tvMoveSpeed;
	private transient TextView tvMoveDuration;

	private transient BaseAdapter adapter;

	@XStreamOmitField
	private transient View view;

	public DroneMoveBrick(Sprite sprite, int velocity, int duration) {
		this.sprite = sprite;
		this.velocity = velocity;
		this.duration = duration;
	}

	@Override
	public void execute() {
		float throttle = 0, roll = 0, pitch = 0, yaw = 0;
		float constant = (float) velocity / 100;

		if (options != null) {
			if (options[0] == true) {
				pitch -= constant;
			}
			if (options[1] == true) {
				pitch += constant;
			}
			if (options[2] == true) {
				roll -= constant;
			}
			if (options[3] == true) {
				roll += constant;
			}
			if (options[4] == true) {
				throttle -= constant;
			}
			if (options[5] == true) {
				throttle += constant;
			}
			if (options[6] == true) {
				yaw -= constant;
			}
			if (options[7] == true) {
				yaw += constant;
			}
		}

		DroneServiceHandler.getInstance().getDrone().move(roll, pitch, throttle, yaw, duration);
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(final Context context, int brickId, final BaseAdapter adapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_drone_move, null);
		}

		this.adapter = adapter;

		movementList = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(
				R.array.drone_move_directions)));

		if (options == null) {
			options = new boolean[movementList.size()];
		}

		ImageView ivDirection = (ImageView) view.findViewById(R.id.ivMoveDirection);
		ImageView ivAltitude = (ImageView) view.findViewById(R.id.ivMoveAltitude);
		ImageView ivRotation = (ImageView) view.findViewById(R.id.ivMoveRotation);
		if (options[0]) {
			if (options[2]) {
				ivDirection.setImageDrawable(context.getResources().getDrawable(R.drawable.drone_move_forward_left));
			} else if (options[3]) {
				ivDirection.setImageDrawable(context.getResources().getDrawable(R.drawable.drone_move_forward_right));
			} else {
				ivDirection.setImageDrawable(context.getResources().getDrawable(R.drawable.drone_move_forward));
			}
			ivDirection.setVisibility(ImageView.VISIBLE);
		} else if (options[1]) {
			if (options[2]) {
				ivDirection.setImageDrawable(context.getResources().getDrawable(R.drawable.drone_move_backward_left));
			} else if (options[3]) {
				ivDirection.setImageDrawable(context.getResources().getDrawable(R.drawable.drone_move_backward_right));
			} else {
				ivDirection.setImageDrawable(context.getResources().getDrawable(R.drawable.drone_move_backward));
			}
			ivDirection.setVisibility(ImageView.VISIBLE);
		} else {
			if (options[2]) {
				ivDirection.setImageDrawable(context.getResources().getDrawable(R.drawable.drone_move_left));
				ivDirection.setVisibility(ImageView.VISIBLE);
			} else if (options[3]) {
				ivDirection.setImageDrawable(context.getResources().getDrawable(R.drawable.drone_move_right));
				ivDirection.setVisibility(ImageView.VISIBLE);
			} else {
				ivDirection.setVisibility(ImageView.GONE);
			}
		}

		if (options[4]) {
			ivAltitude.setImageDrawable(context.getResources().getDrawable(R.drawable.drone_move_downward));
			ivAltitude.setVisibility(ImageView.VISIBLE);
		} else if (options[5]) {
			ivAltitude.setImageDrawable(context.getResources().getDrawable(R.drawable.drone_move_upward));
			ivAltitude.setVisibility(ImageView.VISIBLE);
		} else {
			ivAltitude.setVisibility(ImageView.GONE);
		}

		if (options[6]) {
			ivRotation.setImageDrawable(context.getResources().getDrawable(R.drawable.drone_move_rotation_left));
			ivRotation.setVisibility(ImageView.VISIBLE);
		} else if (options[7]) {
			ivRotation.setImageDrawable(context.getResources().getDrawable(R.drawable.drone_move_rotation_right));
			ivRotation.setVisibility(ImageView.VISIBLE);
		} else {
			ivRotation.setVisibility(ImageView.GONE);
		}

		Button button = (Button) view.findViewById(R.id.btMoveChoose);
		button.setText(context.getString(R.string.drone_choose_movement_title));

		dialog = new DroneMoveBrickChooseMovementDialog(context, options);
		dialog.setOnDismissListener(this);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.show();
			}
		});

		tvMoveSpeed = (TextView) view.findViewById(R.id.drone_TvSpeed);
		String vel = Integer.toString(velocity);
		if (vel.length() == 1) {
			vel = "  " + vel;
		} else if (vel.length() == 2) {
			vel = " " + vel;
		}

		tvMoveSpeed.setText("Speed: " + vel + "%");

		sbMoveSpeed = (SeekBar) view.findViewById(R.id.seekBarSpeed);
		sbMoveSpeed.setOnSeekBarChangeListener(this);
		sbMoveSpeed.setEnabled(true);
		sbMoveSpeed.setProgress(velocity / 10);

		sbMoveDuration = (SeekBar) view.findViewById(R.id.sbDroneMoveDuration);
		sbMoveDuration.setProgress(duration);
		sbMoveDuration.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				/** Do Nothing */
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				/** Do Nothing */
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				progressChangedHelper(progress);
			}

			private void progressChangedHelper(int progress) {
				duration = progress;
				tvMoveDuration.setText("t=" + duration + "ms");
			}
		});
		sbMoveDuration.setEnabled(true);
		tvMoveDuration = (TextView) view.findViewById(R.id.tvDroneMoveDuration);
		tvMoveDuration.setText("t=" + Integer.toString(duration) + "ms");
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_drone_move, null);
	}

	@Override
	public Brick clone() {
		return new DroneMoveBrick(getSprite(), velocity, duration);
	}

	@Override
	public void onProgressChanged(SeekBar bar, int progress, boolean arg2) {
		//
		velocity = progress * 10;
		String vel = Integer.toString(velocity);
		if (vel.length() == 1) {
			vel = "  " + vel;
		} else if (vel.length() == 2) {
			vel = " " + vel;
		}

		tvMoveSpeed.setText("Speed: " + vel + "%");
	}

	@Override
	public void onStartTrackingTouch(SeekBar bar) {
		// do nothing
	}

	@Override
	public void onStopTrackingTouch(SeekBar bar) {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		options = ((DroneMoveBrickChooseMovementDialog) this.dialog).getSelectedOptions();
		adapter.notifyDataSetChanged();
		dialog.cancel();
	}

	@Override
	public int getRequiredResources() {
		return WIFI_DRONE;
	}

}
