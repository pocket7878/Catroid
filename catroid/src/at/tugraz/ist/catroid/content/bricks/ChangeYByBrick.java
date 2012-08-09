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

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class ChangeYByBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	@XStreamOmitField
	private transient View view;

	private Formula yMovementFormula;

	private transient Brick instance = null;
	private transient FormulaEditorDialog formulaEditor;

	public ChangeYByBrick(Sprite sprite, int yMovement) {
		this.sprite = sprite;

		yMovementFormula = new Formula(Integer.toString(yMovement));
	}

	public ChangeYByBrick(Sprite sprite, Formula yMovement) {
		this.sprite = sprite;

		yMovementFormula = yMovement;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		int yMovement = yMovementFormula.interpretInteger();

		sprite.costume.aquireXYWidthHeightLock();
		int yPosition = (int) sprite.costume.getYPosition();

		if (yPosition > 0 && yMovement > 0 && yPosition + yMovement < 0) {
			yPosition = Integer.MAX_VALUE;
		} else if (yPosition < 0 && yMovement < 0 && yPosition + yMovement > 0) {
			yPosition = Integer.MIN_VALUE;
		} else {
			yPosition += yMovement;
		}

		sprite.costume.setXYPosition(sprite.costume.getXPosition(), yPosition);
		sprite.costume.releaseXYWidthHeightLock();
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		if (instance == null) {
			instance = this;
		}

		view = View.inflate(context, R.layout.brick_change_y, null);

		TextView textY = (TextView) view.findViewById(R.id.brick_change_y_text_view);
		EditText editY = (EditText) view.findViewById(R.id.brick_change_y_edit_text);
		yMovementFormula.setTextFieldId(R.id.brick_change_y_edit_text);
		yMovementFormula.refreshTextField(view);
		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);
		editY.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_change_y, null);
	}

	@Override
	public Brick clone() {
		return new ChangeYByBrick(getSprite(), yMovementFormula);
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
		formulaEditor.setInputFocusAndFormula(this.yMovementFormula);

		//		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		//		final EditText input = new EditText(context);
		//		input.setText(String.valueOf(yMovement));
		//		input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
		//		input.setSelectAllOnFocus(true);
		//		dialog.setView(input);
		//		dialog.setOnCancelListener((OnCancelListener) context);
		//		dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
		//			public void onClick(DialogInterface dialog, int which) {
		//				try {
		//					yMovement = Integer.parseInt(input.getText().toString());
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
