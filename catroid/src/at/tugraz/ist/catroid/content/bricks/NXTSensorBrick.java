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
package at.tugraz.ist.catroid.content.bricks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.utils.Utils;

public class NXTSensorBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	public static final int REQUIRED_RESSOURCES = BLUETOOTH_LEGO_NXT;

	private static final int MIN_SENSOR = 0;
	private static final int MAX_SENSOR = 4;

	private Sprite sprite;
	private int sensorTest;

	private transient EditText sensor;

	public NXTSensorBrick(Sprite sprite, int sensorTest) {
		this.sprite = sprite;
		this.sensorTest = sensorTest;
	}

	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT;
	}

	public void execute() {
		LegoNXT.sendBTCTestMessage(sensorTest);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_nxt_sensor, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new NXTSensorBrick(getSprite(), sensorTest);
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {
		View brickView = View.inflate(context, R.layout.brick_nxt_sensor, null);

		sensor = (EditText) brickView.findViewById(R.id.nxt_sensor_edit_text);
		sensor.setText(String.valueOf(sensorTest));

		sensor.setOnClickListener(this);

		return brickView;
	}

	public void onStartTrackingTouch(SeekBar freqBar) {

	}

	public void onStopTrackingTouch(SeekBar freqBar) {

	}

	public void onClick(final View view) {
		final Context context = view.getContext();

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		final EditText input = new EditText(context);
		if (view.getId() == R.id.nxt_sensor_edit_text) {
			input.setText(String.valueOf(sensorTest));
			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		input.setSelectAllOnFocus(true);
		dialog.setView(input);
		dialog.setOnCancelListener((OnCancelListener) context);
		dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					int newVal = (Integer.parseInt(input.getText().toString()));
					if (newVal > MAX_SENSOR) {
						newVal = MAX_SENSOR;
						Toast.makeText(context, R.string.number_to_big, Toast.LENGTH_SHORT).show();
					} else if (newVal < MIN_SENSOR) {
						newVal = MIN_SENSOR;
						Toast.makeText(context, R.string.number_to_small, Toast.LENGTH_SHORT).show();
					}
					sensorTest = newVal;

				} catch (NumberFormatException exception) {
					Toast.makeText(context, R.string.error_no_number_entered, Toast.LENGTH_SHORT);
				}
				dialog.cancel();
			}
		});
		dialog.setNeutralButton(context.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		AlertDialog finishedDialog = dialog.create();
		finishedDialog.setOnShowListener(Utils.getBrickDialogOnClickListener(context, input));

		finishedDialog.show();
	}
}