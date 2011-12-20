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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;

public class DroneSelectTimeDialogInteger extends Dialog implements OnClickListener {

	private int value;
	private SeekBar seekBar;
	private Button button;
	private TextView textView;

	public DroneSelectTimeDialogInteger(Context context, int oldValue) {
		super(context);
		this.value = oldValue;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_select_time);

		seekBar = (SeekBar) findViewById(R.id.seekBar_select_time);
		seekBar.setMax(10);
		seekBar.setProgress(value);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
				textView.setText(((Integer) value).toString());
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// do nothing
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// do nothing
			}
		});

		button = (Button) findViewById(R.id.dialog_select_time_ok_button);
		button.setOnClickListener(new android.view.View.OnClickListener() {
			public void onClick(View v) {
				value = Integer.parseInt((String) textView.getText());
				dismiss();
			}
		});

		textView = (TextView) findViewById(R.id.tv_select_time);
		textView.setText(((Integer) value).toString());
	}

	public void onClick(View v) {
		show();
	}

	public double getValue() {
		return value;
	}

}
