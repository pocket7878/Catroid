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

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.hid.KeyCode;

public class HIDComboBrick extends LoopBeginBrick {
	private static final long serialVersionUID = 1L;

	public HIDComboBrick(Sprite sprite) {
		this.sprite = sprite;

	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		loopEndBrick.setTimesToRepeat(1);

		Script script = loopEndBrick.getScript();
		int end = script.getBrickList().indexOf(loopEndBrick);
		Collection<KeyCode> keyCodes = new ArrayList<KeyCode>();

		int begin = script.getBrickList().indexOf(this) + 1;
		for (; begin != end; begin++) {
			try {
				// TODO find better solution!!!
				HIDBrick brick = (HIDBrick) script.getBrick(begin);
				keyCodes.add(brick.getKeyCode());
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("HIDCombo Brick", "Wrong element in Combo Brick! Only KeyBrick allowed!");
				return;
			}
		}

		((HIDBrick) script.getBrick(begin)).getHidConnection().send(keyCodes);
		script.setExecutingBrickIndex(script.getBrickList().indexOf(loopEndBrick));
	}

	@Override
	public Brick clone() {
		return new HIDComboBrick(getSprite());
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {

		View view = View.inflate(context, R.layout.brick_combo_start, null);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_combo_start, null);
	}

}
