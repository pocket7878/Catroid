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

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.tutorial.Tutorial;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class NewSpriteDialog extends TextDialog {

	public NewSpriteDialog(ProjectActivity projectActivity) {
		super(projectActivity, projectActivity.getString(R.string.new_sprite_dialog_title), projectActivity
				.getString(R.string.new_sprite_dialog_default_sprite_name));
		initKeyAndClickListener();
	}

	public void handleOkButton() {
		String spriteName = input.getText().toString();

		if (spriteName == null || spriteName.equalsIgnoreCase("")) {
			Utils.displayErrorMessage(activity, activity.getString(R.string.spritename_invalid));
			return;
		}

		if (projectManager.spriteExists(spriteName)) {
			Utils.displayErrorMessage(activity, activity.getString(R.string.spritename_already_exists));
			return;
		}

		Tutorial tutorial = Tutorial.getInstance(null);
		tutorial.setNotification("DialogDone");

		Sprite sprite = new Sprite(spriteName);
		projectManager.addSprite(sprite);
		((ArrayAdapter<?>) ((ProjectActivity) activity).getListAdapter()).notifyDataSetChanged();

		input.setText(null);
		activity.dismissDialog(ProjectActivity.DIALOG_NEW_SPRITE);
	}

<<<<<<< HEAD
	private void initKeyAndClickListener() {
		dialog.setOnKeyListener(new OnKeyListener() {
=======
	private void initKeyListener(AlertDialog.Builder builder) {
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				Tutorial tutorial = Tutorial.getInstance(projectActivity.getApplicationContext());
				tutorial.rewindStep();
				tutorial.rewindStep();
				tutorial.setNotification("DialogDone");
			}
		});

		builder.setOnKeyListener(new OnKeyListener() {
			@Override
>>>>>>> tutorial_otto
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					String newSpriteName = (input.getText().toString()).trim();
					if (projectManager.spriteExists(newSpriteName)) {
<<<<<<< HEAD
						Utils.displayErrorMessage(activity, activity.getString(R.string.spritename_already_exists));
=======
						Utils.displayErrorMessage(projectActivity,
								projectActivity.getString(R.string.spritename_already_exists));
						Tutorial.getInstance(null).setNotification("DialogDone");
>>>>>>> tutorial_otto
					} else {
						handleOkButton();
						return true;
					}
				}
				return false;
			}
		});

<<<<<<< HEAD
		buttonPositive.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handleOkButton();
			}
		});

		buttonNegative.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				input.setText(null);
				activity.dismissDialog(ProjectActivity.DIALOG_NEW_SPRITE);
=======
		dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) projectActivity
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
			}
		});

		input.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0 || (s.length() == 1 && s.charAt(0) == '.')) {
					buttonPositive.setEnabled(false);
				} else {
					buttonPositive.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
>>>>>>> tutorial_otto
			}
		});
	}

}
