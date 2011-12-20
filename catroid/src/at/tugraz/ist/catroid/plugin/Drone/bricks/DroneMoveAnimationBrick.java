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
import android.util.Log;
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
import at.tugraz.ist.catroid.plugin.Drone.DroneConsts;
import at.tugraz.ist.catroid.plugin.Drone.DroneHandler;
import at.tugraz.ist.catroid.plugin.Drone.other.DroneBrickListAdapter;
import at.tugraz.ist.catroid.plugin.Drone.other.DroneSelectTimeDialogInteger;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class DroneMoveAnimationBrick implements Brick, OnItemClickListener, OnDismissListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	private String title;
	private int animation;
	private int durationSeconds;

	private transient ArrayList<String> animationList;
	private transient Dialog animationDialog;

	private transient BaseAdapter adapter;

	@XStreamOmitField
	private transient View view;

	public DroneMoveAnimationBrick(Sprite sprite, int animation, int durationSeconds) {
		this.sprite = sprite;
		this.animation = animation;
		this.durationSeconds = durationSeconds;
	}

	public void execute() {

		DroneHandler.getInstance().getDrone().playMoveAnimation(animation, durationSeconds);

		// wait for finishing executing animation
		try {
			Thread.sleep(durationSeconds * 1000);
		} catch (InterruptedException e) {
			Log.e(DroneConsts.DroneLogTag, "Exception DroneMoveAnimationBrick -> execute()", e);
		}
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(final Context context, int brickId, BaseAdapter adapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_drone_move_animation, null);
		}

		this.adapter = adapter;

		animationList = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(
				R.array.drone_move_animations)));

		Button animationButton = (Button) view.findViewById(R.id.btMoveAnimationChoose);

		if (title != null) {
			animationButton.setText(title);
		} else {
			animationButton.setText(context.getString(R.string.drone_choose_animation_title));
		}

		final DroneBrickListAdapter moveAnimationBrickAdapter = new DroneBrickListAdapter(context, animationList);

		animationButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				animationDialog = new Dialog(context);
				animationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				animationDialog.setContentView(R.layout.drone_list);
				animationDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);

				ListView list = (ListView) animationDialog.findViewById(R.id.drone_list);
				list.setAdapter(moveAnimationBrickAdapter);
				list.setOnItemClickListener(DroneMoveAnimationBrick.this);

				animationDialog.show();
			}
		});

		EditText editDuration = (EditText) view.findViewById(R.id.drone_edit_move_aniamtion_duration);
		editDuration.setText(String.valueOf(durationSeconds));
		DroneSelectTimeDialogInteger dialogDuration = new DroneSelectTimeDialogInteger(context, durationSeconds);
		dialogDuration.setOnDismissListener(this);
		dialogDuration.setOnDismissListener(this);
		dialogDuration.setOnCancelListener((OnCancelListener) context);
		editDuration.setOnClickListener(dialogDuration);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_drone_move_animation, null);
	}

	@Override
	public Brick clone() {
		return new DroneMoveAnimationBrick(getSprite(), animation, durationSeconds);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		title = animationList.get(position);
		animation = position;
		adapter.notifyDataSetChanged();
		animationDialog.dismiss();
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
