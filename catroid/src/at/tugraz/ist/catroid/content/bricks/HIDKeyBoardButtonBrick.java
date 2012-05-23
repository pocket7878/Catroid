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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

public class HIDKeyBoardButtonBrick implements Brick, KeyBrickInterface, OnItemSelectedListener {
	private static final long serialVersionUID = 1L;

	private Sprite sprite;
	private int keyCode;

	protected Object readResolve() {
		return this;
	}

	public HIDKeyBoardButtonBrick(Sprite sprite) {
		this.sprite = sprite;
		this.keyCode = 0;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
		Log.i("HID KeyBoard Key Brick", "KeyCode: " + getKeyCode());

	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_hid_keyboard_button_press, null);
	}

	@Override
	public Brick clone() {
		return new HIDKeyBoardButtonBrick(getSprite());
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {
		View brickView = View.inflate(context, R.layout.brick_hid_keyboard_button_press, null);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context,
				R.array.hid_keyboard_key_chooser, android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner motorSpinner = (Spinner) brickView.findViewById(R.id.keyboard_button_spinner);
		motorSpinner.setOnItemSelectedListener(this);
		motorSpinner.setClickable(true);
		motorSpinner.setEnabled(true);
		motorSpinner.setAdapter(motorAdapter);
		motorSpinner.setSelection(keyCode);

		return brickView;
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		keyCode = position;
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

	public int getKeyCode() {
		return keyCode;
	}

}
