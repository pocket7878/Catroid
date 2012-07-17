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

import com.badlogic.gdx.Gdx;

public class OrientationVariableBrick implements Brick, OnItemSelectedListener {

	private static final long serialVersionUID = 1L;

	public static enum Angle {
		Azimuth, Pitch, Roll;
	}

	private Sprite sprite;
	private float orientationValue;
	private char axis;

	private transient Angle angle;

	protected Object readResolve() {
		// initialize direction if parsing from xml with XStream
		for (Angle angle : Angle.values()) {
			if (angle.name().toUpperCase().charAt(0) == axis) {
				this.angle = angle;
				break;
			}
		}
		return this;
	}

	public OrientationVariableBrick(Sprite sprite, Angle angle) {
		this.sprite = sprite;
		this.angle = angle;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {

		switch (axis) {
			case 'A':
				orientationValue = Gdx.input.getAzimuth();
				break;
			case 'P':
				orientationValue = Gdx.input.getPitch();
				break;
			case 'R':
				orientationValue = Gdx.input.getRoll();
				break;
			default:
				break;
		}
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public float getOrientationValue() {
		return this.orientationValue;
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {

		View view = View.inflate(context, R.layout.brick_orientation_sensor_variable, null);
		ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(context,
				R.array.orientation_Angle_value_strings, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = (Spinner) view.findViewById(R.id.angle_spinner);
		spinner.setAdapter(arrayAdapter);

		spinner.setClickable(true);
		spinner.setFocusable(true);

		spinner.setOnItemSelectedListener(this);

		spinner.setSelection(angle.ordinal());

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_orientation_sensor_variable, null);
	}

	@Override
	public Brick clone() {
		return new OrientationVariableBrick(getSprite(), angle);
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		angle = Angle.values()[position];
		axis = parent.getItemAtPosition(position).toString().toUpperCase().charAt(0);
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

}
