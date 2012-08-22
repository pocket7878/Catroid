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

public class ChangeXByBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	@XStreamOmitField
	private transient View view;

	private Formula xMovement;

	public ChangeXByBrick(Sprite sprite, int xMovementValue) {
		this.sprite = sprite;

		xMovement = new Formula(Integer.toString(xMovementValue));
	}

	public ChangeXByBrick(Sprite sprite, Formula xMovement) {
		this.sprite = sprite;

		this.xMovement = xMovement;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		int xMovementValue = xMovement.interpretInteger();

		sprite.costume.aquireXYWidthHeightLock();
		int xPosition = (int) sprite.costume.getXPosition();

		if (xPosition > 0 && xMovementValue > 0 && xPosition + xMovementValue < 0) {
			xPosition = Integer.MAX_VALUE;
		} else if (xPosition < 0 && xMovementValue < 0 && xPosition + xMovementValue > 0) {
			xPosition = Integer.MIN_VALUE;
		} else {
			xPosition += xMovementValue;
		}

		sprite.costume.setXYPosition(xPosition, sprite.costume.getYPosition());
		sprite.costume.releaseXYWidthHeightLock();
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_change_x, null);

		TextView textX = (TextView) view.findViewById(R.id.brick_change_x_text_view);
		EditText editX = (EditText) view.findViewById(R.id.brick_change_x_edit_text);
		xMovement.setTextFieldId(R.id.brick_change_x_edit_text);
		xMovement.refreshTextField(view);
		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		editX.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_change_x, null);
	}

	@Override
	public Brick clone() {
		return new ChangeXByBrick(getSprite(), xMovement);
	}

	@Override
	public void onClick(View view) {
		FormulaEditorFragment.showFragment(view, this, xMovement);
	}

}
