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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.hid.KeyCode;
import at.tugraz.ist.catroid.utils.Utils;

public class HIDComboBrick extends LoopBeginBrick implements OnClickListener {
	private static final long serialVersionUID = 1L;
	private int timesToRepeat;

	public HIDComboBrick(Sprite sprite, int timesToRepeat) {
		this.sprite = sprite;
		this.timesToRepeat = timesToRepeat;
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

		for (int begin = script.getBrickList().indexOf(this) + 1; begin != end; begin++) {
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

		// TODO Interface.send(keyCodes);
		//		for (Integer i : keyCodes) {
		//			Log.i("HIDComboBrick", i + "");
		//		}

		script.setExecutingBrickIndex(script.getBrickList().indexOf(loopEndBrick));
	}

	@Override
	public Brick clone() {
		return new HIDComboBrick(getSprite(), timesToRepeat);
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {

		View view = View.inflate(context, R.layout.brick_combo_start, null);

		EditText edit = (EditText) view.findViewById(R.id.brick_repeat_edit_text);
		edit.setText(timesToRepeat + "");

		edit.setOnClickListener(this);
		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_combo_start, null);
	}

	public void onClick(View view) {
		final Context context = view.getContext();

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		final EditText input = new EditText(context);
		input.setText(String.valueOf(timesToRepeat));
		input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
				| InputType.TYPE_NUMBER_FLAG_SIGNED);
		input.setSelectAllOnFocus(true);
		dialog.setView(input);
		dialog.setOnCancelListener((OnCancelListener) context);
		dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					timesToRepeat = Integer.parseInt(input.getText().toString());
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
