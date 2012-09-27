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

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.formulaeditor.CatKeyEvent;
import at.tugraz.ist.catroid.formulaeditor.CatKeyboardView;
import at.tugraz.ist.catroid.formulaeditor.Operators;

public class FormulaEditorChooseOperatorDialog extends DialogFragment implements DialogInterface.OnClickListener {

	private CatKeyboardView catKeyboardView;
	private final String[] operatorNames = { Operators.GREATER_THAN.operatorName, Operators.SMALLER_THAN.operatorName,
			Operators.EQUAL.operatorName, Operators.NOT_EQUAL.operatorName, Operators.LOGICAL_AND.operatorName,
			Operators.LOGICAL_OR.operatorName };
	private static final int CANCEL_INDEX = -2;
	private static final Map<String, Integer> indexToKeyCode = new HashMap<String, Integer>();
	static {
		indexToKeyCode.put(Operators.GREATER_THAN.operatorName, CatKeyEvent.KEYCODE_GREATER_THAN);
		indexToKeyCode.put(Operators.SMALLER_THAN.operatorName, CatKeyEvent.KEYCODE_SMALLER_THAN);
		indexToKeyCode.put(Operators.EQUAL.operatorName, KeyEvent.KEYCODE_EQUALS);
		indexToKeyCode.put(Operators.NOT_EQUAL.operatorName, CatKeyEvent.KEYCODE_NOT_EQUAL);
		indexToKeyCode.put(Operators.LOGICAL_AND.operatorName, CatKeyEvent.KEYCODE_LOGICAL_AND);
		indexToKeyCode.put(Operators.LOGICAL_OR.operatorName, CatKeyEvent.KEYCODE_LOGICAL_OR);
	}

	@Override
	public void onClick(DialogInterface dialog, int index) {
		if (index == CANCEL_INDEX) {
			this.dismiss();
			return;
		}

		if (index < 0 || index >= operatorNames.length) {
			return;
		}

		Integer keyCode = indexToKeyCode.get(operatorNames[index]);

		if (keyCode == null) {
			return;
		}

		catKeyboardView.onKey(keyCode, null);

	}

	public static FormulaEditorChooseOperatorDialog newInstance(int title) {
		FormulaEditorChooseOperatorDialog fragment = new FormulaEditorChooseOperatorDialog();

		return fragment;
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setCancelable(true);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		//		builder.setTitle(getString(R.string.formula_editor_choose_costume_variable));
		builder.setNegativeButton(getString(R.string.cancel_button), this);

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, operatorNames);

		builder.setAdapter(arrayAdapter, this);

		return builder.create();

	}

	public void setCatKeyboardView(CatKeyboardView catKeyboardView) {
		this.catKeyboardView = catKeyboardView;

	}

}
