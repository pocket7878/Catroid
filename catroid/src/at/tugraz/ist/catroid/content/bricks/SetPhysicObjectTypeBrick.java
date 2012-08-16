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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.physics.PhysicObject;
import at.tugraz.ist.catroid.physics.PhysicWorld;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class SetPhysicObjectTypeBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private PhysicWorld physicWorld;
	private Sprite sprite;
	private PhysicObject.Type type;

	@XStreamOmitField
	private transient View view;

	public SetPhysicObjectTypeBrick(PhysicWorld physicWorld, Sprite sprite, PhysicObject.Type type) {
		this.physicWorld = physicWorld;
		this.sprite = sprite;
		this.type = type;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		physicWorld.getPhysicObject(sprite).setType(type);
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		view = View.inflate(context, R.layout.brick_set_physic_object_type, null);

		Spinner spinner = (Spinner) view.findViewById(R.id.brick_set_physic_object_type_spinner);
		spinner.setClickable(true);
		spinner.setFocusable(true);
		spinner.setAdapter(createAdapter(context));

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position < PhysicObject.Type.values().length) {
					type = PhysicObject.Type.values()[position];
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		return view;
	}

	private ArrayAdapter<String> createAdapter(Context context) {
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		for (PhysicObject.Type spinnerItem : PhysicObject.Type.values()) {
			String spinnerItemText = spinnerItem.toString();
			spinnerItemText = spinnerItemText.toLowerCase();
			spinnerItemText = spinnerItemText.substring(0, 1).toUpperCase() + spinnerItemText.substring(1);
			arrayAdapter.add(spinnerItemText);
		}
		return arrayAdapter;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_set_physic_object_type, null);
	}

	@Override
	public Brick clone() {
		return new SetPhysicObjectTypeBrick(physicWorld, getSprite(), type);
	}
}
