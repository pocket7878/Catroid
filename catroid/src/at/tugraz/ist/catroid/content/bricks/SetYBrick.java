/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.dialogs.BrickTextDialog;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class SetYBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private int yPosition;
	private Sprite sprite;

	@XStreamOmitField
	private transient View view;

	public SetYBrick(Sprite sprite, int yPosition) {
		this.sprite = sprite;
		this.yPosition = yPosition;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
		sprite.costume.aquireXYWidthHeightLock();
		sprite.costume.setYPosition(yPosition);
		sprite.costume.releaseXYWidthHeightLock();
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_set_y, null);

		TextView textY = (TextView) view.findViewById(R.id.brick_set_y_text_view);
		EditText editY = (EditText) view.findViewById(R.id.brick_set_y_edit_text);
		editY.setText(String.valueOf(yPosition));

		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);
		editY.setOnClickListener(this);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_set_y, null);
	}

	@Override
	public Brick clone() {
		return new SetYBrick(getSprite(), yPosition);
	}

	public void onClick(View view) {
		ScriptTabActivity activity = (ScriptTabActivity) view.getContext();
		
		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				input.setText(String.valueOf(yPosition));
				input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
						| InputType.TYPE_NUMBER_FLAG_SIGNED);
				input.setSelectAllOnFocus(true);
			}
			
			@Override
			protected boolean handleOkButton() {
				try {
					yPosition = Integer.parseInt(input.getText().toString());
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}
				
				return true;
			}
		};
		
		editDialog.show(activity.getSupportFragmentManager(), "dialog_set_y_brick");
	}
}
