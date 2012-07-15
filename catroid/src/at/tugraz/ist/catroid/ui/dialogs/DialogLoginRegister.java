/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.transfers.TestRegistration;

/**
 * @author simgeszgn
 * 
 */
public class DialogLoginRegister extends Dialog implements android.view.View.OnClickListener {
	private final Activity activity;

	private EditText usernameEditText;
	private EditText passwordEditText;
	private Button loginOrRegister;
	private Button passwordForgotten;

	/**
	 * @param context
	 */
	public DialogLoginRegister(Activity activity) {
		super(activity);
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_login_register);
		setTitle(R.string.login_register_dialog_title);
		setCanceledOnTouchOutside(true);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		initializeViews();
		initializeListeners();

		this.setOnShowListener(new OnShowListener() {
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) activity
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(usernameEditText, InputMethodManager.SHOW_IMPLICIT);
			}
		});

	}

	private void initializeViews() {
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		loginOrRegister = (Button) findViewById(R.id.login_register_button);
		passwordForgotten = (Button) findViewById(R.id.password_forgotten_button);
	}

	private void initializeListeners() {
		loginOrRegister.setOnClickListener(this);
		passwordForgotten.setOnClickListener(this);
	}

	public void onClick(View v) {

		String username;
		switch (v.getId()) {
			case R.id.login_register_button:
				username = usernameEditText.getText().toString();
				String password = passwordEditText.getText().toString();

				new TestRegistration(activity, username, password, this).execute();

				break;
		}
	}

}
