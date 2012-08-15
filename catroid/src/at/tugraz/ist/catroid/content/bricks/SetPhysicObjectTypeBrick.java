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
import at.tugraz.ist.catroid.physics.PhysicObject;
import at.tugraz.ist.catroid.physics.PhysicObject.Type;
import at.tugraz.ist.catroid.physics.PhysicWorld;
import at.tugraz.ist.catroid.utils.Utils;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class SetPhysicObjectTypeBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private PhysicWorld physicWorld;
	private Sprite sprite;
	private int type;

	@XStreamOmitField
	private transient View view;

	public SetPhysicObjectTypeBrick(PhysicWorld physicWorld, Sprite sprite, int type) {
		this.physicWorld = physicWorld;
		this.sprite = sprite;
		this.type = type;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
		PhysicObject.Type physicObjectType = null;
		switch (type) {
			case 0:
				physicObjectType = Type.DYNAMIC;
				break;
			case 1:
				physicObjectType = Type.FIXED;
				break;
			case 2:
				physicObjectType = Type.NONE;
				break;
		}

		physicWorld.getPhysicObject(sprite).setType(physicObjectType);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {
		view = View.inflate(context, R.layout.brick_set_physic_object_type, null);

		EditText editMass = (EditText) view.findViewById(R.id.brick_set_physic_object_type_text);
		editMass.setText(String.valueOf(type));
		editMass.setOnClickListener(this);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_set_physic_object_type, null);
	}

	@Override
	public Brick clone() {
		return new SetPhysicObjectTypeBrick(physicWorld, getSprite(), type);
	}

	public void onClick(View view) {
		final Context context = view.getContext();

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		final EditText input = new EditText(context);
		input.setText(String.valueOf(type));
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setSelectAllOnFocus(true);
		dialog.setView(input);
		dialog.setOnCancelListener((OnCancelListener) context);
		dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					type = Integer.parseInt(input.getText().toString());
				} catch (NumberFormatException exception) {
					Toast.makeText(context, R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}
				dialog.cancel();
			}
		});
		dialog.setNeutralButton(context.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		AlertDialog finishedDialog = dialog.create();
		finishedDialog.setOnShowListener(Utils.getBrickDialogOnClickListener(context, input));

		finishedDialog.show();

	}
}
