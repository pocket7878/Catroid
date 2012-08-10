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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.dialogs.FormulaEditorDialog;

public class ChangeBrightnessBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;

	private Sprite sprite;

	private transient View view;

	private Formula changeBrightnessFormula;

	private transient Brick instance = null;
	private transient FormulaEditorDialog formulaEditor;

	public ChangeBrightnessBrick(){
		
	}
	
	public ChangeBrightnessBrick(Sprite sprite, double changeBrightness) {
		this.sprite = sprite;
		changeBrightnessFormula = new Formula(Double.toString(changeBrightness));
	}

	public ChangeBrightnessBrick(Sprite sprite, Formula changeBrightness) {
		this.sprite = sprite;
		changeBrightnessFormula = changeBrightness;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		//		sprite.costume.changeBrightnessValueBy((float) (this.changeBrightness / 100));
		double changeBrightness = changeBrightnessFormula.interpret() / 100;

		sprite.costume.changeBrightnessValueBy((float) changeBrightness);
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	//	public double getChangeBrightness() {
	//		return changeBrightness;
	//	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		if (instance == null) {
			instance = this;
		}

		view = View.inflate(context, R.layout.brick_change_brightness, null);

		TextView textX = (TextView) view.findViewById(R.id.brick_change_brightness_text_view);
		EditText editX = (EditText) view.findViewById(R.id.brick_change_brightness_edit_text);
		//		editX.setText(String.valueOf(changeBrightness));
		changeBrightnessFormula.setTextFieldId(R.id.brick_change_brightness_edit_text);
		changeBrightnessFormula.refreshTextField(view);

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);

		editX.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_change_brightness, null);
	}

	@Override
	public Brick clone() {
		return new ChangeBrightnessBrick(getSprite(), changeBrightnessFormula);
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
		}
		formulaEditor.setInputFocusAndFormula(this.changeBrightnessFormula);

		//		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		//		final EditText input = new EditText(context);
		//		input.setText(String.valueOf(changeBrightness));
		//		input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
		//				| InputType.TYPE_NUMBER_FLAG_SIGNED);
		//		input.setSelectAllOnFocus(true);
		//		dialog.setView(input);
		//		dialog.setOnCancelListener((OnCancelListener) context);
		//		dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
		//			public void onClick(DialogInterface dialog, int which) {
		//				try {
		//					changeBrightness = Double.parseDouble(input.getText().toString());
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
