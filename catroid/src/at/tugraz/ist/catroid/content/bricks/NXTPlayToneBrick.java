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

import android.util.Log;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.formulaeditor.Formula;

import at.tugraz.ist.catroid.formulaeditor.FormulaElement;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;

import at.tugraz.ist.catroid.ui.dialogs.FormulaEditorDialog;

public class NXTPlayToneBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	public static final int REQUIRED_RESSOURCES = BLUETOOTH_LEGO_NXT;

	private static final int MIN_FREQ_IN_HERTZ = 200;
	private static final int MAX_FREQ_IN_HERTZ = 14000;
	private static final int MIN_DURATION = 0;
	private static final int MAX_DURATION = Integer.MAX_VALUE;

	private Sprite sprite;

	private transient EditText editFreq;

	

private Formula frequencyFormula;
	private Formula durationInMsFormula;

	private transient Brick instance = null;
	private transient FormulaEditorDialog formulaEditor;

	public NXTPlayToneBrick(Sprite sprite, int hertz, int duration) {
		this.sprite = sprite;

		this.frequencyFormula = new Formula(Integer.toString(hertz));
		this.durationInMsFormula = new Formula(Integer.toString(duration));
	}

	public NXTPlayToneBrick(Sprite sprite, Formula frequencyFormula, Formula durationFormula) {
		this.sprite = sprite;

		this.frequencyFormula = frequencyFormula;
		this.durationInMsFormula = durationFormula;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT;
	}

	@Override
	public void execute() {
		int interpretFrequency = Math.min(MAX_FREQ_IN_HERTZ, frequencyFormula.interpret().intValue());
		interpretFrequency = Math.max(MIN_FREQ_IN_HERTZ, interpretFrequency);
		int durationInMs = Math.min(MAX_DURATION, durationInMsFormula.interpret().intValue());
		durationInMs = Math.max(MIN_DURATION, durationInMs);

		LegoNXT.sendBTCPlayToneMessage(interpretFrequency, durationInMs);

	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_nxt_play_tone, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new NXTPlayToneBrick(getSprite(), frequencyFormula, durationInMsFormula);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
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
		frequencyFormula.setTextFieldId(R.id.nxt_tone_freq_edit_text);
		frequencyFormula.refreshTextField(brickView);

		textFreq.setVisibility(View.GONE);
		editFreq.setVisibility(View.VISIBLE);

		editFreq.setOnClickListener(this);

		return brickView;
	}

	@Override
	public void onClick(View view) {
		Log.i("info", "Brick.onClick() editorActive: " + FormulaEditorDialog.mScriptTabActivity.isEditorActive());
		final Context context = view.getContext();

		if (!FormulaEditorDialog.mScriptTabActivity.isEditorActive()) {
			FormulaEditorDialog.mScriptTabActivity.setEditorStatus(true);
			formulaEditor = new FormulaEditorDialog(context, instance);

			Log.i("", "getOwnerActivity()" + FormulaEditorDialog.mScriptTabActivity);
			FormulaEditorDialog.mScriptTabActivity.showDialog(ScriptTabActivity.DIALOG_FORMULA, null);
			FormulaEditorDialog.mScriptTabActivity.setCurrentBrick(this);
			switch (view.getId()) {
				case R.id.nxt_tone_freq_edit_text:
					formulaEditor.setInputFocusAndFormula(frequencyFormula);
					break;
				case R.id.nxt_tone_duration_edit_text:
					formulaEditor.setInputFocusAndFormula(durationInMsFormula);
					break;
			}

		}

	}

}
