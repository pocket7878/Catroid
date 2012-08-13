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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.ui.dialogs.FormulaEditorDialog;

public class ChangeBrightnessBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;

	private Sprite sprite;

	private transient View view;

	private Formula changeBrightness;

	private transient Brick instance = null;

	public ChangeBrightnessBrick() {

	}

	public ChangeBrightnessBrick(Sprite sprite, double changeBrightnessValue) {
		this.sprite = sprite;
		changeBrightness = new Formula(Double.toString(changeBrightnessValue));
	}

	public ChangeBrightnessBrick(Sprite sprite, Formula changeBrightness) {
		this.sprite = sprite;
		this.changeBrightness = changeBrightness;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		//		sprite.costume.changeBrightnessValueBy((float) (this.changeBrightness / 100));
		double changeBrightnessValue = changeBrightness.interpret() / 100;

		sprite.costume.changeBrightnessValueBy((float) changeBrightnessValue);
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

		view = View.inflate(context, R.layout.brick_change_brightness, null);

		TextView textX = (TextView) view.findViewById(R.id.brick_change_brightness_text_view);
		EditText editX = (EditText) view.findViewById(R.id.brick_change_brightness_edit_text);
		changeBrightness.setTextFieldId(R.id.brick_change_brightness_edit_text);
		changeBrightness.refreshTextField(view);

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
		return new ChangeBrightnessBrick(getSprite(), changeBrightness);
	}

	@Override
	public void onClick(View view) {
		FormulaEditorDialog.showDialog(view, this, changeBrightness);
	}

}
