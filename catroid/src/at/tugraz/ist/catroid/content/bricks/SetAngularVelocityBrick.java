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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.physics.PhysicWorld;
import at.tugraz.ist.catroid.physics.PhysicWorldConverter;
import at.tugraz.ist.catroid.utils.Utils;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class SetAngularVelocityBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private PhysicWorld physicWorld;
	private Sprite sprite;
	private float degrees;

	@XStreamOmitField
	private transient View view;

	public SetAngularVelocityBrick(PhysicWorld physicWorld, Sprite sprite, float degrees) {
		this.physicWorld = physicWorld;
		this.sprite = sprite;
		this.degrees = degrees;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		float radian = PhysicWorldConverter.angleCatToBox2d(degrees);
		physicWorld.getPhysicObject(sprite).setAngularVelocity(radian);
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		view = View.inflate(context, R.layout.brick_set_angular_velocity, null);

		EditText editText = (EditText) view.findViewById(R.id.brick_set_angular_velocity_edit_text);
		editText.setText(String.valueOf(degrees));

		editText.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_set_angular_velocity, null);
	}

	@Override
	public Brick clone() {
		return new SetAngularVelocityBrick(physicWorld, sprite, degrees);
	}

	@Override
	public void onClick(final View view) {
		final Context context = view.getContext();

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		final EditText input = new EditText(context);
		input.setText(String.valueOf(degrees));
		input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		input.setSelectAllOnFocus(true);
		dialog.setView(input);
		dialog.setOnCancelListener((OnCancelListener) context);
		dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					degrees = Float.parseFloat(input.getText().toString());
				} catch (NumberFormatException exception) {
					Toast.makeText(context, R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}
				dialog.cancel();
			}
		});
		dialog.setNeutralButton(context.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		AlertDialog finishedDialog = dialog.create();
		finishedDialog.setOnShowListener(Utils.getBrickDialogOnClickListener(context, input));

		finishedDialog.show();

	}
}