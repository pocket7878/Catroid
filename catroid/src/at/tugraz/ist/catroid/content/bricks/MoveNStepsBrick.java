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
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;

public class MoveNStepsBrick implements Brick, OnClickListener {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	private transient View view;

	private Formula stepsFormula;

	public MoveNStepsBrick(Sprite sprite, double steps) {
		this.sprite = sprite;
		stepsFormula = new Formula(Double.toString(steps));
	}

	public MoveNStepsBrick(Sprite sprite, Formula steps) {
		this.sprite = sprite;

		stepsFormula = steps;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		double steps = stepsFormula.interpret().doubleValue();

		sprite.costume.aquireXYWidthHeightLock();

		double radians = Math.toRadians(sprite.costume.rotation);

		int newXPosition = (int) Math.round(sprite.costume.getXPosition() + steps * Math.cos(radians));
		int newYPosition = (int) Math.round(sprite.costume.getYPosition() + steps * Math.sin(radians));

		sprite.costume.setXYPosition(newXPosition, newYPosition);
		sprite.costume.releaseXYWidthHeightLock();

	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_move_n_steps, null);

		TextView text = (TextView) view.findViewById(R.id.brick_move_n_steps_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_move_n_steps_edit_text);

		//		edit.setText(String.valueOf(steps));
		stepsFormula.setTextFieldId(R.id.brick_move_n_steps_edit_text);
		stepsFormula.refreshTextField(view);

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);
		edit.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.brick_move_n_steps, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new MoveNStepsBrick(getSprite(), stepsFormula);
	}

	@Override
	public void onClick(View view) {
		ScriptTabActivity activity = null;
		if (view.getContext().getClass().equals(ScriptTabActivity.class)) {
			activity = (ScriptTabActivity) view.getContext();
		} else {
			activity = (ScriptTabActivity) ((ContextWrapper) view.getContext()).getBaseContext();
		}

		activity.showFormulaEditorDialog(this, stepsFormula);

	}
}
