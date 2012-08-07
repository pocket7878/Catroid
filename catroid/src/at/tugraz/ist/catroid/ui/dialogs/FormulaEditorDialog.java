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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.Gravity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.formulaeditor.CalcGrammarParser;
import at.tugraz.ist.catroid.formulaeditor.CatKeyboardView;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.formulaeditor.FormulaEditorEditText;
import at.tugraz.ist.catroid.formulaeditor.FormulaElement;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;

public class FormulaEditorDialog extends Dialog implements OnClickListener, OnDismissListener {

	private final Context context;
	private Brick currentBrick;
	private FormulaEditorEditText formulaEditorEditText;
	private Formula formula = null;
	private CatKeyboardView catKeyboardView;
	private LinearLayout brickSpace;
	private View brickView;
	private Button okButton = null;
	private Button undoButton = null;
	private Button redoButton = null;
	private long confirmBack = 0;
	private boolean buttonIsBackButton = true;
	public static ScriptTabActivity mScriptTabActivity;

	public static void setOwnerActivity(ScriptTabActivity owner) {
		FormulaEditorDialog.mScriptTabActivity = owner;

	}

	public FormulaEditorDialog(Context context, Brick brick) {
		super(context, R.style.dialog_fullscreen);
		currentBrick = brick;
		this.context = context;

		mScriptTabActivity.setCurrentFormulaEditorDialog(this);
		Log.i("info", "FormulaEditorDialog()");
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//for function keys
		//Log.i("info", "Key: " + event.getKeyCode());

		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

		}
		super.dispatchKeyEvent(event);
		return false;

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("info", "FormulaEditorDialog.onCreate()");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_formula_editor);

		brickSpace = (LinearLayout) findViewById(R.id.formula_editor_brick_space);
		if (brickSpace != null) {
			brickView = currentBrick.getView(context, 0, null);
			brickSpace.addView(brickView);


			int brickHeight = brickView.getMeasuredHeight();
		}
		//		flipView = (ViewFlipper) findViewById(R.id.catflip);
		//		flipView.setDisplayedChild(1);
		//		Animation slideOut = AnimationUtils.loadAnimation(context, R.anim.slide_in);
		//		flipView.setOutAnimation(slideOut);
		//		Animation slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_out);
		//		flipView.setInAnimation(slideIn);
		//
		//		flipView.setOnTouchListener(new OnTouchListener() {
		//			public boolean onTouch(View v, MotionEvent event) {
		//
		//				gestureDetector.onTouchEvent(event);
		//				return true;
		//			}
		//		});
		//		gestureDetector = new GestureDetector(context, this);
		//LinearLayout brickSpace = (LinearLayout) findViewById(R.id.formula_editor_brick_space);
		//brickSpace.addView(currentBrick.getView(context, 0, null));

		setTitle(R.string.dialog_formula_editor_title);
		setCanceledOnTouchOutside(true);

		okButton = (Button) findViewById(R.id.formula_editor_ok_button);
		okButton.setOnClickListener(this);

		undoButton = (Button) findViewById(R.id.formula_editor_undo_button);
		undoButton.setOnClickListener(this);

		redoButton = (Button) findViewById(R.id.formula_editor_redo_button);
		redoButton.setOnClickListener(this);

		formulaEditorEditText = (FormulaEditorEditText) findViewById(R.id.formula_editor_edit_field);
		if (brickSpace != null) {
			brickSpace.measure(0, 0);
		}
		catKeyboardView = (CatKeyboardView) findViewById(R.id.keyboardcat);
		catKeyboardView.setEditText(formulaEditorEditText);
		catKeyboardView.setCurrentBrick(currentBrick);

		makeRedoButtonClickable(false);
		makeUndoButtonClickable(false);
		this.setOnDismissListener(this);
		if (brickSpace != null) {
			formulaEditorEditText.init(this, brickSpace.getMeasuredHeight(), catKeyboardView, context);
		} else {
			formulaEditorEditText.init(this, 0, catKeyboardView, context);
		}

		Log.i("info", "FormulaEditorDialog.onCreate() at the end of method");
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i("info", "FormulaEditorDialog.onRestoreInstanceState()");
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public Bundle onSaveInstanceState() {
		Log.i("info", "FormulaEditorDialog.onSaveInstanceState()");
		return super.onSaveInstanceState();
	}

	public void setInputFocusAndFormula(Formula formula) {

		if (formula == this.formula) {
			return;
		} else if (formulaEditorEditText.hasChanges() == true) {
			showToast(R.string.formula_editor_save_first);
			return;
		}

		this.formula = formula;
		makeOkButtonBackButton();
		formulaEditorEditText.setFieldActive(formula.getEditTextRepresentation());

	}

	private int parseFormula(String formulaToParse) {
		CalcGrammarParser parser = CalcGrammarParser.getFormulaParser(formulaToParse);
		FormulaElement parserFormulaElement = parser.parseFormula();

		if (parserFormulaElement == null) {
			showToast(R.string.formula_editor_parse_fail);
			return parser.getErrorCharacterPosition();
		} else {
			formula.setRoot(parserFormulaElement);
		}
		return -1;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.formula_editor_ok_button:
				Log.i("info", "FormulaEditorDialog.onClick()  case ok_button");
				if (buttonIsBackButton == true) {
					dismiss();
				} else {
					String formulaToParse = formulaEditorEditText.getText().toString();
					int err = parseFormula(formulaToParse);
					if (err == -1) {
						if (brickSpace != null) {
							formula.refreshTextField(brickView);
						}
						formulaEditorEditText.formulaSaved();
						showToast(R.string.formula_editor_changes_saved);
					} else if (err == -2) {
						//Crashed it like a BOSS! 
					} else {
						formulaEditorEditText.highlightParseError(err);
					}
				}
				break;

			case R.id.formula_editor_undo_button:
				makeUndoButtonClickable(formulaEditorEditText.undo());
				makeRedoButtonClickable(true);
				if (buttonIsBackButton) {
					makeOkButtonSaveButton();
				}
				break;

			case R.id.formula_editor_redo_button:
				makeRedoButtonClickable(formulaEditorEditText.redo());
				makeUndoButtonClickable(true);
				if (buttonIsBackButton) {
					makeOkButtonSaveButton();
				}
				Log.i("info", "FormulaEditorDialog.onClick() case back_button");
				mScriptTabActivity.setCurrentFormulaEditorDialog(null);
				mScriptTabActivity.removeDialog(ScriptTabActivity.DIALOG_FORMULA);
				mScriptTabActivity.setEditorStatus(false);
				mScriptTabActivity.setCurrentBrick(null);
				break;

			default:
				break;

		}
	}

	public void showToast(int ressourceId) {
		Toast userInfo = Toast.makeText(context, ressourceId, Toast.LENGTH_SHORT);
		userInfo.setGravity(Gravity.TOP, 0, 10);
		userInfo.show();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {

		//		Log.i("info", "FormulaEditorDialog.onDismiss()");
		//			mScriptTabActivity.setCurrentFormulaEditorDialog(null);
		//		mScriptTabActivity.removeDialog(ScriptTabActivity.DIALOG_FORMULA);
		//							mScriptTabActivity.setEditorStatus(false);
		//		mScriptTabActivity.setCurrentBrick(null);
		//			this.dismiss();

	}

	public void makeUndoButtonClickable(boolean clickable) {
		undoButton.setClickable(clickable);
	}

	public void makeRedoButtonClickable(boolean clickable) {
		redoButton.setClickable(clickable);
	}

	public void makeOkButtonSaveButton() {
		okButton.setText(R.string.formula_editor_button_save);
		buttonIsBackButton = false;
	}

	public void makeOkButtonBackButton() {
		okButton.setText(R.string.formula_editor_button_return);
		buttonIsBackButton = true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (formulaEditorEditText.hasChanges()) {
					if (System.currentTimeMillis() <= confirmBack + 2000) {
						showToast(R.string.formula_editor_changes_discarded);
						Log.i("info", "FormulaEditorDialog.onKeyDown()");
						mScriptTabActivity.setCurrentFormulaEditorDialog(null);
						mScriptTabActivity.removeDialog(ScriptTabActivity.DIALOG_FORMULA);
						mScriptTabActivity.setEditorStatus(false);
						mScriptTabActivity.setCurrentBrick(null);
						dismiss();
					} else {
						showToast(R.string.formula_editor_confirm_discard);
						confirmBack = System.currentTimeMillis();
					}
				} else {
					Log.i("info", "FormulaEditorDialog.onKeyDown()");
					mScriptTabActivity.setCurrentFormulaEditorDialog(null);
					mScriptTabActivity.removeDialog(ScriptTabActivity.DIALOG_FORMULA);
					mScriptTabActivity.setEditorStatus(false);
					mScriptTabActivity.setCurrentBrick(null);
					dismiss();
				}

		}
		return formulaEditorEditText.catKeyboardView.onKeyDown(keyCode, event);
	}

}
