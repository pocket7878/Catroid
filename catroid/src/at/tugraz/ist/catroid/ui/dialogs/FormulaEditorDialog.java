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

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
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

public class FormulaEditorDialog extends DialogFragment implements OnClickListener, OnKeyListener {

	private final Context context;
	private Brick currentBrick;
	private FormulaEditorEditText formulaEditorEditText;
	private static Formula activeFormula = null;
	private CatKeyboardView catKeyboardView;
	private LinearLayout brickSpace;
	private View brickView;
	private Button okButton = null;
	private Button undoButton = null;
	private Button redoButton = null;
	private long confirmBack = 0;
	private boolean buttonIsBackButton = true;
	public static ScriptTabActivity mScriptTabActivity;
	private static boolean restorePreviousTextField = false;

	public static void setOwnerActivity(ScriptTabActivity owner) {
		FormulaEditorDialog.mScriptTabActivity = owner;

	}

	public FormulaEditorDialog(Context context, Brick brick) {
		//super(context, R.style.dialog_fullscreen);
		currentBrick = brick;
		this.context = context;

		//mScriptTabActivity.setCurrentFormulaEditorDialog(this);
		Log.i("info", "FormulaEditorDialog()");
	}

	//	@Override
	//	public boolean dispatchKeyEvent(KeyEvent event) {
	//		//for function keys
	//		//Log.i("info", "Key: " + event.getKeyCode());
	//
	//		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
	//
	//		}
	//		super.dispatchKeyEvent(event);
	//		return false;
	//
	//	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("info", "FormulaEditorDialog.onCreate()");
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_FRAME, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i("info", "FormulaEditorDialog.onCreateView()");

		View dialogView = inflater.inflate(R.layout.dialog_formula_editor, container, false);

		brickSpace = (LinearLayout) dialogView.findViewById(R.id.formula_editor_brick_space);
		if (brickSpace != null) {
			brickView = currentBrick.getView(context, 0, null);
			brickSpace.addView(brickView);
		}

		//view.setTitle(R.string.dialog_formula_editor_title);
		//setCanceledOnTouchOutside(true);

		okButton = (Button) dialogView.findViewById(R.id.formula_editor_ok_button);
		okButton.setOnClickListener(this);

		undoButton = (Button) dialogView.findViewById(R.id.formula_editor_undo_button);
		undoButton.setOnClickListener(this);

		redoButton = (Button) dialogView.findViewById(R.id.formula_editor_redo_button);
		redoButton.setOnClickListener(this);

		formulaEditorEditText = (FormulaEditorEditText) dialogView.findViewById(R.id.formula_editor_edit_field);
		Log.i("info", "edittext: " + formulaEditorEditText);
		if (brickSpace != null) {
			brickSpace.measure(0, 0);
		}
		catKeyboardView = (CatKeyboardView) dialogView.findViewById(R.id.keyboardcat);
		catKeyboardView.setEditText(formulaEditorEditText);
		catKeyboardView.setCurrentBrick(currentBrick);

		makeRedoButtonClickable(false);
		makeUndoButtonClickable(false);
		makeOkButtonBackButton();
		//dialogView.setOnDismissListener(this);
		if (brickSpace != null) {
			formulaEditorEditText.init(this, brickSpace.getMeasuredHeight(), catKeyboardView, context);
		} else {
			formulaEditorEditText.init(this, 0, catKeyboardView, context);
		}

		//

		dialogView.setOnKeyListener(this);

		return dialogView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		restorePreviousTextField = true;
	}

	public void setInputFocusAndFormula(Formula newFormula) {

		Log.i("info", "edittext is: " + formulaEditorEditText);
		if (formulaEditorEditText == null) {
			return;
		}

		if (restorePreviousTextField) { //after orientation switch
			restorePreviousTextField = false;
			if (!formulaEditorEditText.restoreFieldFromPreviousHistory()) { //history is only deleted when editor is shut down by user!
				formulaEditorEditText.enterNewFormula(newFormula.getEditTextRepresentation()); // this happens when onSaveInstanceState() is being called but not by orientation change (e.g.user turns off screen)
			}
			refreshFormulaPreviewString(formulaEditorEditText.getText().toString());
		} else if (newFormula == activeFormula) {

			if (!formulaEditorEditText.hasChanges()) {
				formulaEditorEditText.enterNewFormula(activeFormula.getEditTextRepresentation());
			} else {
				formulaEditorEditText.quickSelect();
			}
			refreshFormulaPreviewString(formulaEditorEditText.getText().toString());
		} else {
			if (!formulaEditorEditText.hasChanges()) {
				if (activeFormula != null) {
					activeFormula.refreshTextField(brickView);
				}
				activeFormula = newFormula;
				makeOkButtonBackButton();
				formulaEditorEditText.enterNewFormula(newFormula.getEditTextRepresentation());
			} else {
				showToast(R.string.formula_editor_save_first);
			}

		}

	}

	private int parseFormula(String formulaToParse) {
		CalcGrammarParser parser = CalcGrammarParser.getFormulaParser(formulaToParse);
		FormulaElement parserFormulaElement = parser.parseFormula();

		if (parserFormulaElement == null) {
			showToast(R.string.formula_editor_parse_fail);
			return parser.getErrorCharacterPosition();
		} else {
			activeFormula.setRoot(parserFormulaElement);
		}
		return -1;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.formula_editor_ok_button:
				if (buttonIsBackButton) {
					mScriptTabActivity.setCurrentFormulaEditorDialog(null);
					//mScriptTabActivity.removeDialog(ScriptTabActivity.DIALOG_FORMULA);
					mScriptTabActivity.setEditorStatus(false);
					mScriptTabActivity.setCurrentBrick(null);
					formulaEditorEditText.endEdit();
					dismiss();
				} else {
					String formulaToParse = formulaEditorEditText.getText().toString();
					int err = parseFormula(formulaToParse);
					if (err == -1) {
						if (brickSpace != null) {
							activeFormula.refreshTextField(brickView);
						}
						formulaEditorEditText.formulaSaved();
						showToast(R.string.formula_editor_changes_saved);
						//dismiss();
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
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (formulaEditorEditText.hasChanges()) {
					if (System.currentTimeMillis() <= confirmBack + 2000) {
						showToast(R.string.formula_editor_changes_discarded);
						Log.i("info", "FormulaEditorDialog.onKeyDown()");
						mScriptTabActivity.setCurrentFormulaEditorDialog(null);
						//mScriptTabActivity.removeDialog(ScriptTabActivity.DIALOG_FORMULA);
						mScriptTabActivity.setEditorStatus(false);
						mScriptTabActivity.setCurrentBrick(null);
						formulaEditorEditText.endEdit();
						dismiss();
					} else {
						showToast(R.string.formula_editor_confirm_discard);
						confirmBack = System.currentTimeMillis();
					}
				} else {
					Log.i("info", "FormulaEditorDialog.onKeyDown()");
					mScriptTabActivity.setCurrentFormulaEditorDialog(null);
					//mScriptTabActivity.removeDialog(ScriptTabActivity.DIALOG_FORMULA);
					mScriptTabActivity.setEditorStatus(false);
					mScriptTabActivity.setCurrentBrick(null);
					formulaEditorEditText.endEdit();
					dismiss();
				}

		}
		return formulaEditorEditText.catKeyboardView.onKeyDown(keyCode, event);
	}

	public void refreshFormulaPreviewString(String formulaString) {
		activeFormula.refreshTextField(brickView, formulaEditorEditText.getText().toString());
	}

}
