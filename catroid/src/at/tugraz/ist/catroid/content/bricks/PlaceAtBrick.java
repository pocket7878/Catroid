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

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class PlaceAtBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	private Formula xPositionFormula;
	private Formula yPositionFormula;

	@XStreamOmitField
	private transient View view;

	public PlaceAtBrick(Sprite sprite, int xPosition, int yPosition) {
		this.sprite = sprite;

		xPositionFormula = new Formula(Integer.toString(xPosition));
		yPositionFormula = new Formula(Integer.toString(yPosition));
	}

	public PlaceAtBrick(Sprite sprite, Formula xPosition, Formula yPosition) {
		this.sprite = sprite;

		xPositionFormula = xPosition;
		yPositionFormula = yPosition;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		int xPosition = xPositionFormula.interpret().intValue();
		int yPosition = yPositionFormula.interpret().intValue();

		sprite.costume.aquireXYWidthHeightLock();
		sprite.costume.setXYPosition(xPosition, yPosition);
		sprite.costume.releaseXYWidthHeightLock();
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_place_at, null);
		TextView textX = (TextView) view.findViewById(R.id.brick_place_at_x_text_view);
		EditText editX = (EditText) view.findViewById(R.id.brick_place_at_x_edit_text);
		//		editX.setText(String.valueOf(xPosition));
		//		editX.setText(xPositionFormula.getEditTextRepresentation());
		xPositionFormula.setTextFieldId(R.id.brick_place_at_x_edit_text);
		xPositionFormula.refreshTextField(view);

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		editX.setOnClickListener(this);

		TextView textY = (TextView) view.findViewById(R.id.brick_place_at_y_text_view);
		EditText editY = (EditText) view.findViewById(R.id.brick_place_at_y_edit_text);
		//		editY.setText(String.valueOf(yPosition));
		//		yPositionFormula.refreshTextField(view);
		yPositionFormula.setTextFieldId(R.id.brick_place_at_y_edit_text);
		yPositionFormula.refreshTextField(view);

		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);
		editY.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_place_at, null);
	}

	@Override
	public Brick clone() {
		return new PlaceAtBrick(getSprite(), xPositionFormula, yPositionFormula);
	}

	@Override
	public void onClick(View view) {
		//		FormulaEditorDialog.showDialog(view);
		//		ScriptTabActivity activity = null;
		//		if (view.getContext().getClass().equals(ScriptTabActivity.class)) {
		//			activity = (ScriptTabActivity) view.getContext();
		//		} else {
		//			activity = (ScriptTabActivity) ((ContextWrapper) view.getContext()).getBaseContext();
		//		}

		switch (view.getId()) {
			case R.id.brick_place_at_x_edit_text:
				//activity.showFormulaEditorDialog(this, xPositionFormula);
				FormulaEditorDialog.showDialog(view, this, xPositionFormula);
				break;

			case R.id.brick_place_at_y_edit_text:
				//activity.showFormulaEditorDialog(this, yPositionFormula);
				FormulaEditorDialog.showDialog(view, this, yPositionFormula);
				break;
		}

	}

}
