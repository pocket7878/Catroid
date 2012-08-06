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

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.formulaeditor.FormulaElement;
import at.tugraz.ist.catroid.ui.dialogs.FormulaEditorDialog;

public class NXTPlayToneBrick implements Brick, OnClickListener, OnSeekBarChangeListener {
	private static final long serialVersionUID = 1L;
	public static final int REQUIRED_RESSOURCES = BLUETOOTH_LEGO_NXT;

	private static final int MIN_FREQ_IN_HERTZ = 200;
	private static final int MAX_FREQ_IN_HERTZ = 14000;
	private static final int MIN_DURATION = 0;
	private static final int MAX_DURATION = Integer.MAX_VALUE;

	private Sprite sprite;
	private int hertz;
	private int durationInMs;

	private transient EditText editFreq;
	private transient EditText editSeekBarValue;
	private transient SeekBar frequencyBar;

	private Formula hertzFormula;
	private Formula durationInMsFormula;

	private transient Brick instance = null;
	private transient FormulaEditorDialog formulaEditor;
	public transient boolean editorActive = false;

	public NXTPlayToneBrick(Sprite sprite, int hertz, int duration) {
		Log.i("info", "NXTPlayToneBrick: Konstruktor");
		this.sprite = sprite;
		this.hertz = hertz;
		this.durationInMs = duration;

		FormulaElement sliderElement = new FormulaElement(FormulaElement.ElementType.SENSOR, "SLIDER_", null);
		hertzFormula = new Formula(sliderElement);
		durationInMsFormula = new Formula(Integer.toString(duration));
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT;
	}

	@Override
	public void execute() {
		int interpretHertz = Math.min(MAX_FREQ_IN_HERTZ, hertzFormula.interpret(Double.valueOf(hertz)).intValue());
		interpretHertz = Math.max(MIN_FREQ_IN_HERTZ, interpretHertz);
		durationInMs = Math.min(MAX_DURATION, durationInMsFormula.interpret().intValue());
		durationInMs = Math.max(MIN_DURATION, durationInMs);

		LegoNXT.sendBTCPlayToneMessage(hertz, durationInMs);

	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_nxt_play_tone, null);
		SeekBar noClick = (SeekBar) view.findViewById(R.id.seekBarNXTToneFrequency);
		noClick.setEnabled(false);
		return view;
	}

	@Override
	public Brick clone() {
		return new NXTPlayToneBrick(getSprite(), hertz, durationInMs);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		Log.i("info", "NXTPlayToneBrick: getView");

		if (instance == null) {
			instance = this;
		}

		View brickView = View.inflate(context, R.layout.brick_nxt_play_tone, null);

		TextView textDuration = (TextView) brickView.findViewById(R.id.nxt_tone_duration_text_view);
		EditText editDuration = (EditText) brickView.findViewById(R.id.nxt_tone_duration_edit_text);
		//		editDuration.setText(String.valueOf(durationInMs / 1000.0));
		durationInMsFormula.setTextFieldId(R.id.nxt_tone_duration_edit_text);
		durationInMsFormula.refreshTextField(brickView);
		//		EditDoubleDialog dialogDuration = new EditDoubleDialog(context, editDuration, duration, MIN_DURATION,
		//				MAX_DURATION);
		//		dialogDuration.setOnDismissListener(this);
		//		dialogDuration.setOnCancelListener((OnCancelListener) context);
		//		editDuration.setOnClickListener(dialogDuration);

		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);

		TextView textFreq = (TextView) brickView.findViewById(R.id.nxt_tone_freq_text_view);
		editFreq = (EditText) brickView.findViewById(R.id.nxt_tone_freq_edit_text);
		//		editFreq.setText(String.valueOf(hertz / 100));
		hertzFormula.setTextFieldId(R.id.nxt_tone_freq_edit_text);
		hertzFormula.refreshTextField(brickView);
		//		dialogFreq = new EditIntegerDialog(context, editFreq, frequency, true, MIN_FREQ, MAX_FREQ);
		//		dialogFreq.setOnDismissListener(this);
		//		dialogFreq.setOnCancelListener((OnCancelListener) context);
		//		editFreq.setOnClickListener(dialogFreq);

		textFreq.setVisibility(View.GONE);
		editFreq.setVisibility(View.VISIBLE);

		editFreq.setOnClickListener(this);

		frequencyBar = (SeekBar) brickView.findViewById(R.id.seekBarNXTToneFrequency);
		frequencyBar.setOnSeekBarChangeListener(this);
		frequencyBar.setMax(MAX_FREQ_IN_HERTZ / 100);
		frequencyBar.setEnabled(true);

		TextView textViewSeekBarValue = (TextView) brickView.findViewById(R.id.nxt_tone_freq_seekBar_text_view);
		editSeekBarValue = (EditText) brickView.findViewById(R.id.nxt_tone_freq_seekBar_edit_text);

		textViewSeekBarValue.setVisibility(View.GONE);
		editSeekBarValue.setVisibility(View.VISIBLE);

		freqToSeekBarVal(frequencyBar);
		freqToSeekBarEditText();

		editSeekBarValue.setText("TEST");

		Button freqDown = (Button) brickView.findViewById(R.id.freq_down_btn);
		freqDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (hertz <= 200) {
					return;
				}

				hertz -= 100;
				freqToSeekBarVal(frequencyBar);
				freqToSeekBarEditText();
			}
		});

		Button freqUp = (Button) brickView.findViewById(R.id.freq_up_btn);
		freqUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (hertz >= 14000) {
					return;
				}

				hertz += 100;
				freqToSeekBarVal(frequencyBar);
				freqToSeekBarEditText();
			}
		});

		return brickView;
	}

	@Override
	public void onProgressChanged(SeekBar freqBar, int progress, boolean fromUser) {
		Log.i("info", "onProgressChanged: enter");
		Log.i("info", "onProgressChanged.freqBarValue = " + freqBar.getProgress());
		if (!fromUser) { //Robotium fromUser=false
			if (progress == 0) {
				return;
			}
		}

		if (progress != (hertz / 100)) {
			seekbarValToFreq(freqBar);
			freqToSeekBarEditText();
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar freqBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar freqBar) {

	}

	private void freqToSeekBarEditText() {
		editSeekBarValue.setText(String.valueOf(hertz / 100));
	}

	private void seekbarValToFreq(SeekBar freqBar) {

		hertz = freqBar.getProgress() * 100;

		if (hertz < 200) {
			hertz = 200;
			freqBar.setProgress(2);
		}
	}

	private void freqToSeekBarVal(SeekBar freqBar) {
		if (hertz < 200) {
			hertz = 200;
			freqBar.setProgress(2);
		}
		freqBar.setProgress(hertz / 100);
	}

	@Override
	public void onClick(final View view) {
		final Context context = view.getContext();

		if (!editorActive) {
			editorActive = true;
			formulaEditor = new FormulaEditorDialog(context, instance);
			formulaEditor.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface editor) {

					//size = formulaEditor.getReturnValue();
					formulaEditor.dismiss();

					editorActive = false;
				}
			});
			formulaEditor.show();
		}

		switch (view.getId()) {
			case R.id.nxt_tone_freq_edit_text:
				formulaEditor.setInputFocusAndFormula(hertzFormula);
				break;
			case R.id.nxt_tone_duration_edit_text:
				formulaEditor.setInputFocusAndFormula(durationInMsFormula);
				break;
		}

		//		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		//		final EditText input = new EditText(context);
		//		if (view.getId() == R.id.nxt_tone_duration_edit_text) {
		//			input.setText(String.valueOf(durationInMs / 1000.0));
		//			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		//		} else if (view.getId() == R.id.nxt_tone_freq_edit_text) {
		//			input.setText(String.valueOf(hertz / 100));
		//			input.setInputType(InputType.TYPE_CLASS_NUMBER);
		//		}
		//		input.setSelectAllOnFocus(true);
		//		dialog.setView(input);
		//		dialog.setOnCancelListener((OnCancelListener) context);
		//		dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
		//			public void onClick(DialogInterface dialog, int which) {
		//				try {
		//					if (view.getId() == R.id.nxt_tone_duration_edit_text) {
		//
		//						int newDuration = (int) (Double.parseDouble(input.getText().toString()) * 1000);
		//						if (newDuration > MAX_DURATION) {
		//							newDuration = MAX_DURATION;
		//							Toast.makeText(context, R.string.number_to_big, Toast.LENGTH_SHORT).show();
		//						} else if (newDuration < MIN_DURATION) {
		//							newDuration = MIN_DURATION;
		//							Toast.makeText(context, R.string.number_to_small, Toast.LENGTH_SHORT).show();
		//						}
		//						durationInMs = newDuration;
		//					} else if (view.getId() == R.id.nxt_tone_freq_edit_text) {
		//						int newFrequency = Integer.parseInt(input.getText().toString()) * 100;
		//						if (newFrequency > MAX_FREQ_IN_HERTZ) {
		//							newFrequency = MAX_FREQ_IN_HERTZ;
		//							Toast.makeText(context, R.string.number_to_big, Toast.LENGTH_SHORT).show();
		//						} else if (newFrequency < MIN_FREQ_IN_HERTZ) {
		//							newFrequency = MIN_FREQ_IN_HERTZ;
		//							Toast.makeText(context, R.string.number_to_small, Toast.LENGTH_SHORT).show();
		//						}
		//						hertz = newFrequency;
		//					}
		//				} catch (NumberFormatException exception) {
		//					Toast.makeText(context, R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
		//				}
		//				dialog.cancel();
		//			}
		//		});
		//		dialog.setNeutralButton(context.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
		//			public void onClick(DialogInterface dialog, int which) {
		//				dialog.cancel();
		//			}
		//		});
		//
		//		AlertDialog finishedDialog = dialog.create();
		//		finishedDialog.setOnShowListener(Utils.getBrickDialogOnClickListener(context, input));
		//
		//		finishedDialog.show();
	}

}
