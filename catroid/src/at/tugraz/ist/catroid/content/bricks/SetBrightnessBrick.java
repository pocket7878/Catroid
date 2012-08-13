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

public class SetBrightnessBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	private transient View view;

	private Formula brightnessFormula;

	public SetBrightnessBrick(Sprite sprite, double brightnessValue) {
		this.sprite = sprite;
		brightnessFormula = new Formula(Double.toString(brightnessValue));
	}

	public SetBrightnessBrick(Sprite sprite, Formula brightnessValue) {
		this.sprite = sprite;
		brightnessFormula = brightnessValue;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		double brightness = brightnessFormula.interpret();
		sprite.costume.setBrightnessValue((float) brightness / 100);
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	//	public double getBrightnessValue() {
	//		return brightness;
	//	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		view = View.inflate(context, R.layout.brick_set_brightness, null);

		TextView textX = (TextView) view.findViewById(R.id.brick_set_brightness_text_view);
		EditText editX = (EditText) view.findViewById(R.id.brick_set_brightness_edit_text);
		brightnessFormula.setTextFieldId(R.id.brick_set_brightness_edit_text);
		brightnessFormula.refreshTextField(view);

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);

		editX.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_set_brightness, null);
	}

	@Override
	public Brick clone() {
		return new SetBrightnessBrick(getSprite(), brightnessFormula);
	}

	@Override
	public void onClick(View view) {
		FormulaEditorDialog.showDialog(view, this, brightnessFormula);
	}
}
