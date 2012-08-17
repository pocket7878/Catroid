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
import at.tugraz.ist.catroid.ui.fragment.FormulaEditorFragment;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class PlaceAtBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	private Formula xPosition;
	private Formula yPosition;

	@XStreamOmitField
	private transient View view;

	public PlaceAtBrick(Sprite sprite, int xPositionValue, int yPositionValue) {
		this.sprite = sprite;

		xPosition = new Formula(Integer.toString(xPositionValue));
		yPosition = new Formula(Integer.toString(yPositionValue));
	}

	public PlaceAtBrick(Sprite sprite, Formula xPosition, Formula yPosition) {
		this.sprite = sprite;

		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		int xPositionValue = xPosition.interpretInteger();
		int yPositionValue = yPosition.interpretInteger();

		sprite.costume.aquireXYWidthHeightLock();
		sprite.costume.setXYPosition(xPositionValue, yPositionValue);
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
		xPosition.setTextFieldId(R.id.brick_place_at_x_edit_text);
		xPosition.refreshTextField(view);

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		editX.setOnClickListener(this);

		TextView textY = (TextView) view.findViewById(R.id.brick_place_at_y_text_view);
		EditText editY = (EditText) view.findViewById(R.id.brick_place_at_y_edit_text);
		//		editY.setText(String.valueOf(yPosition));
		//		yPositionFormula.refreshTextField(view);
		yPosition.setTextFieldId(R.id.brick_place_at_y_edit_text);
		yPosition.refreshTextField(view);

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
		return new PlaceAtBrick(getSprite(), xPosition, yPosition);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.brick_place_at_x_edit_text:
				FormulaEditorFragment.showDialog(view, this, xPosition);
				break;

			case R.id.brick_place_at_y_edit_text:
				FormulaEditorFragment.showDialog(view, this, yPosition);
				break;
		}
	}

}
