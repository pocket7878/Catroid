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
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.dialogs.FormulaEditorDialog;

public class SetVolumeToBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;

	private Sprite sprite;
	private Formula volumeFormula;

	private transient Brick instance = null;
	private transient FormulaEditorDialog formulaEditor;

	public SetVolumeToBrick(Sprite sprite, float volume) {
		this.sprite = sprite;
		volumeFormula = new Formula(Float.toString(volume));
	}

	public SetVolumeToBrick(Sprite sprite, Formula volume) {
		this.sprite = sprite;
		volumeFormula = volume;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
		float volume = volumeFormula.interpret().floatValue();

		if (volume < 0.0f) {
			volume = 0.0f;
		} else if (volume > 100.0f) {
			volume = 100.0f;
		}
		SoundManager.getInstance().setVolume(volume);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {

		if (instance == null) {
			instance = this;
		}

		View view = View.inflate(context, R.layout.brick_set_volume_to, null);

		TextView text = (TextView) view.findViewById(R.id.brick_set_volume_to_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_set_volume_to_edit_text);
		//		edit.setText(String.valueOf(volume));
		//		edit.setText(volumeFormula.getEditTextRepresentation());
		volumeFormula.setTextFieldId(R.id.brick_set_volume_to_edit_text);
		volumeFormula.refreshTextField(view);

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);

		edit.setOnClickListener(this);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_set_volume_to, null);
	}

	@Override
	public Brick clone() {
		return new SetVolumeToBrick(getSprite(), volumeFormula);
	}

	public void onClick(View view) {
		Log.i("info", "Brick.onClick() editorActive: " + FormulaEditorDialog.mScriptTabActivity.isEditorActive());
		final Context context = view.getContext();

		if (!FormulaEditorDialog.mScriptTabActivity.isEditorActive()) {
			FormulaEditorDialog.mScriptTabActivity.setEditorStatus(true);
			formulaEditor = new FormulaEditorDialog(context, instance);

			Log.i("", "getOwnerActivity()" + FormulaEditorDialog.mScriptTabActivity);
			FormulaEditorDialog.mScriptTabActivity.showDialog(ScriptTabActivity.DIALOG_FORMULA, null);
			FormulaEditorDialog.mScriptTabActivity.setCurrentBrick(this);

			formulaEditor.setInputFocusAndFormula(this.volumeFormula);
		}

	}
}
