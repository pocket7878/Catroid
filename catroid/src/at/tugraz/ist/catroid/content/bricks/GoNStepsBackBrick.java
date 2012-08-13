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

public class GoNStepsBackBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	public Formula stepsFormula;

	public GoNStepsBackBrick(Sprite sprite, int steps) {
		this.sprite = sprite;
		stepsFormula = new Formula(Integer.toString(steps));
	}

	public GoNStepsBackBrick(Sprite sprite, Formula steps) {
		this.sprite = sprite;
		stepsFormula = steps;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		int steps = stepsFormula.interpret().intValue();

		int zPosition = sprite.costume.zPosition;
		if (steps > 0 && (zPosition - steps) > zPosition) {
			sprite.costume.zPosition = Integer.MIN_VALUE;
		} else if (steps < 0 && (zPosition - steps) < zPosition) {
			sprite.costume.zPosition = Integer.MAX_VALUE;
		} else {
			sprite.costume.zPosition -= steps;
		}
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		View view = View.inflate(context, R.layout.brick_go_back, null);

		TextView text = (TextView) view.findViewById(R.id.brick_go_back_n_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_go_back_edit_text);

		stepsFormula.setTextFieldId(R.id.brick_go_back_edit_text);
		stepsFormula.refreshTextField(view);

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);
		edit.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_go_back, null);
	}

	@Override
	public Brick clone() {
		return new GoNStepsBackBrick(getSprite(), stepsFormula);
	}

	@Override
	public void onClick(View view) {
		FormulaEditorDialog.showDialog(view, this, stepsFormula);
	}

}
