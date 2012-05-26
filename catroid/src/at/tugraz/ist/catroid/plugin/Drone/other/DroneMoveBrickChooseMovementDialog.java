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

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import at.tugraz.ist.catroid.R;

public class DroneMoveBrickChooseMovementDialog extends Dialog {

	private boolean options[];
	private boolean hack[];

	private RadioButton radioButton[];
	private RadioGroup radioGroup[];

	public DroneMoveBrickChooseMovementDialog(Context context, boolean options[]) {
		super(context);
		this.options = options;
		this.radioButton = new RadioButton[options.length];
		this.radioGroup = new RadioGroup[options.length / 2];
		this.hack = new boolean[options.length / 2];
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.drone_move_dialog);
		setCancelable(false);

		radioButton[0] = (RadioButton) findViewById(R.id.rbForward);
		radioButton[1] = (RadioButton) findViewById(R.id.rbBackward);
		radioButton[2] = (RadioButton) findViewById(R.id.rbLeft);
		radioButton[3] = (RadioButton) findViewById(R.id.rbRight);
		radioButton[4] = (RadioButton) findViewById(R.id.rbDownward);
		radioButton[5] = (RadioButton) findViewById(R.id.rbUpward);
		radioButton[6] = (RadioButton) findViewById(R.id.rbRotationLeft);
		radioButton[7] = (RadioButton) findViewById(R.id.rbRotationRight);

		radioGroup[0] = (RadioGroup) findViewById(R.id.rg_fw_bw);
		radioGroup[1] = (RadioGroup) findViewById(R.id.rg_l_r);
		radioGroup[2] = (RadioGroup) findViewById(R.id.rg_uw_dw);
		radioGroup[3] = (RadioGroup) findViewById(R.id.rg_rl_rr);

		Button dialogOkButton = (Button) findViewById(R.id.btOk);
		dialogOkButton.setOnClickListener(new android.view.View.OnClickListener() {

			public void onClick(View v) {
				for (int i = 0; i < radioButton.length; i++) {
					if (radioButton[i].isChecked()) {
						options[i] = true;
					} else {
						options[i] = false;
					}
				}
				dismiss();
			}
		});
	}

	@Override
	public void show() {
		super.show();

		for (int i = 0; i < options.length; i++) {
			if (options[i]) {
				radioButton[i].setChecked(true);
			}
		}
		for (int i = 0; i < hack.length; i++) {
			hack[i] = true;
		}

		for (int i = 0; i < radioGroup.length; i++) {
			final int j = i;

			android.view.View.OnClickListener onClickListener = new android.view.View.OnClickListener() {

				public void onClick(View v) {
					if (v.getId() == radioGroup[j].getCheckedRadioButtonId() && hack[j]) {
						radioGroup[j].clearCheck();
					} else {
						hack[j] = true;
					}
				}
			};

			OnCheckedChangeListener onCheckChangeListener = new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					hack[j] = false;
				}
			};

			radioButton[i * 2].setOnCheckedChangeListener(onCheckChangeListener);
			radioButton[i * 2].setOnClickListener(onClickListener);
			radioButton[i * 2 + 1].setOnCheckedChangeListener(onCheckChangeListener);
			radioButton[i * 2 + 1].setOnClickListener(onClickListener);
		}
	}

	public boolean[] getSelectedOptions() {
		return options;
	}

}
