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
package at.tugraz.ist.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.ArrayAdapter;
import at.tugraz.ist.catroid.formulaeditor.CatKeyEvent;
import at.tugraz.ist.catroid.formulaeditor.CatKeyboardView;

/**
 * @author obusher
 * 
 */
public class ChooseCostumeVariableFragment extends DialogFragment implements DialogInterface.OnClickListener {

	private CatKeyboardView catKeyboardView;
	private final String[] items = { "COSTUME_X_", "COSTUME_Y_", "COSTUME_GHOSTEFFECT_", "COSTUME_BRIGHTNESS_",
			"COSTUME_SIZE_", "COSTUME_ROTATION_", "COSTUME_LAYER_" };
	private static final int CANCEL_INDEX = -2;

	@Override
	public void onClick(DialogInterface dialog, int index) {
		if (index == CANCEL_INDEX) {
			this.dismiss();
			return;
		}
		Log.v("touched: ", "" + index);
		Log.v("touched: ", items[index].toString());

		int[] keyCode = new int[1];
		keyCode[0] = 0;

		//		if (catKeyboardView == null) {
		//			Log.i("info", "catKeyboardView == null");
		//		}
		//
		//		Log.i("info", "fuuuuuuuuuuuuuuuuuuuuuuuuu catKeyboardView: " + catKeyboardView
		//				+ "\n ChooseCostumeDialogFragment: " + this);

		catKeyboardView.onKey(CatKeyEvent.KEYCODE_COSTUME_X + index, keyCode);

	}

	public static ChooseCostumeVariableFragment newInstance(int title) {
		ChooseCostumeVariableFragment fragment = new ChooseCostumeVariableFragment();

		//		Bundle arguments = new Bundle();
		//		arguments.putInt("title", title);
		//		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setCancelable(true);
		//		int style = DialogFragment.STYLE_NORMAL;
		//		int theme = 0;
		//		setStyle(style, theme);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(new String("Choose your Costume Variable:"));
		builder.setNegativeButton("Cancel", this);

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, items);

		builder.setAdapter(arrayAdapter, this);

		return builder.create();

	}

	public void setCatKeyboardView(CatKeyboardView catKeyboardView) {
		Log.i("info", "ChooseCostumeVariableFragment.setCatKeyboardView() catKeyboardView= " + catKeyboardView
				+ "\n ChooseCostumeDialogFragment: " + this);
		this.catKeyboardView = catKeyboardView;

	}

}
