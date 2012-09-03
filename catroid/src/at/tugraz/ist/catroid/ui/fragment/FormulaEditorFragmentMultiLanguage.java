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
package at.tugraz.ist.catroid.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.formulaeditor.CalcGrammarParser;
import at.tugraz.ist.catroid.formulaeditor.CatKeyboardViewMultiLanguage;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.formulaeditor.FormulaEditorEditTextMultiLanguage;
import at.tugraz.ist.catroid.formulaeditor.FormulaElement;
import at.tugraz.ist.catroid.utils.Utils;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class FormulaEditorFragmentMultiLanguage extends SherlockFragment implements OnKeyListener {

	private static final int PARSER_OK = -1;
	private static final int PARSER_STACK_OVERFLOW = -2;
	private static final int PARSER_INPUT_SYNTAX_ERROR = -3;

	private static final int SET_FORMULA_ON_CREATE_VIEW = 0;
	private static final int SET_FORMULA_ON_SWITCH_EDIT_TEXT = 1;
	public static final String FORMULA_EDITOR_FRAGMENT_TAG = "formula_editor_fragment";

	private Context context;

	private static Brick currentBrick;
	private static Formula currentFormula;
	private FormulaEditorEditTextMultiLanguage formulaEditorEditTextMultiLanguage;
	private CatKeyboardViewMultiLanguage catKeyboardView;
	private LinearLayout brickSpace;
	private View brickView;
	private long confirmBack = 0;
	public boolean restoreInstance = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			restoreInstance = savedInstanceState.getBoolean("restoreInstance");
		}
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle saveInstanceState) {
		saveInstanceState.putBoolean("restoreInstance", true);
		super.onSaveInstanceState(saveInstanceState);
	}

	public static void showFragment(View view, Brick brick, Formula formula) {

		SherlockFragmentActivity activity = null;
		activity = (SherlockFragmentActivity) view.getContext();

		FormulaEditorFragmentMultiLanguage formulaEditorDialog = (FormulaEditorFragmentMultiLanguage) activity
				.getSupportFragmentManager().findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG);

		//FormulaEditorFragment formulaEditorDialog = ((ScriptTabActivity) activity).formulaEditor;

		if (formulaEditorDialog == null) {
			FormulaEditorFragmentMultiLanguage.currentBrick = brick;
			FormulaEditorFragmentMultiLanguage.currentFormula = formula;
			formulaEditorDialog = new FormulaEditorFragmentMultiLanguage();
			formulaEditorDialog.startFormulaEditor(activity);
			//			String title = activity.getResources().getString(R.string.formula_editor_title) + " "
			//					+ ProjectManager.getInstance().getCurrentProject().getName();
			//			activity.getSupportActionBar().setTitle(title);
		} else {
			formulaEditorDialog.setInputFormula(formula, SET_FORMULA_ON_SWITCH_EDIT_TEXT);
		}
	}

	public void startFormulaEditor(SherlockFragmentActivity activity) {
		activity.findViewById(R.id.fragment_formula_editor).setVisibility(View.VISIBLE);
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
		//FormulaEditorFragment formulaEditorDialog = new FormulaEditorFragment();
		fragTransaction.add(R.id.fragment_formula_editor, this, FORMULA_EDITOR_FRAGMENT_TAG);
		fragTransaction.commit();

	}

	private void onUserDismiss() { //dont override onDismiss, this must not be called on orientation change
		formulaEditorEditTextMultiLanguage.endEdit();
		currentFormula.prepareToRemove();
		currentFormula = null;
		currentBrick = null;

		SherlockFragmentActivity activity = null;
		activity = (SherlockFragmentActivity) context;
		activity.findViewById(R.id.fragment_formula_editor).setVisibility(View.GONE);
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
		fragTransaction.remove(fragmentManager.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG));
		fragTransaction.commit();
		activity.getSupportActionBar().setTitle(
				getResources().getString(R.string.sprite_name) + " "
						+ ProjectManager.getInstance().getCurrentSprite().getName());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View dialogView = inflater.inflate(R.layout.fragment_formula_editor, container, false);
		context = getActivity();
		brickSpace = (LinearLayout) dialogView.findViewById(R.id.formula_editor_brick_space);
		if (brickSpace != null) {
			brickView = currentBrick.getView(context, 0, null);
			brickSpace.addView(brickView);
		}

		formulaEditorEditTextMultiLanguage = (FormulaEditorEditTextMultiLanguage) dialogView
				.findViewById(R.id.formula_editor_edit_field);
		if (brickSpace != null) {
			brickSpace.measure(0, 0);
		}
		catKeyboardView = (CatKeyboardViewMultiLanguage) dialogView.findViewById(R.id.keyboardcat);
		catKeyboardView.init(formulaEditorEditTextMultiLanguage, dialogView.findViewById(R.id.swipe_bar));

		if (brickSpace != null) {
			formulaEditorEditTextMultiLanguage.init(this, brickSpace.getMeasuredHeight(), catKeyboardView);
		} else {
			formulaEditorEditTextMultiLanguage.init(this, 0, catKeyboardView);
		}

		setInputFormula(currentFormula, SET_FORMULA_ON_CREATE_VIEW);

		((SherlockFragmentActivity) context).getSupportActionBar().setTitle(
				((SherlockFragmentActivity) context).getResources().getString(R.string.formula_editor_title));
		return dialogView;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		menu.clear();
		getSherlockActivity().getSupportMenuInflater().inflate(R.menu.menu_formula_editor, menu);

	}

	public void setInputFormula(Formula newFormula, int mode) {

		int orientation = getResources().getConfiguration().orientation;

		switch (mode) {
			case SET_FORMULA_ON_CREATE_VIEW:
				if (restoreInstance) { //after orientation switch
					restoreInstance = false;
					if (!formulaEditorEditTextMultiLanguage.restoreFieldFromPreviousHistory()) { //history is only deleted when editor is shut down by  user!
						formulaEditorEditTextMultiLanguage.enterNewFormula(newFormula.toString()); // this happens when onSaveInstanceState() is being called but not by orientation change (e.g.user turns off screen)
					}
					refreshFormulaPreviewString(formulaEditorEditTextMultiLanguage.getText().toString());

					currentFormula.highlightTextField(brickView, orientation);
					getActivity().findViewById(R.id.fragment_formula_editor).setVisibility(View.VISIBLE);
					//((ScriptTabActivity) getActivity()).formulaEditor = this;
				} else { //on create
					if (!formulaEditorEditTextMultiLanguage.hasChanges()) {
						currentFormula.removeTextFieldHighlighting(brickView, orientation);
						formulaEditorEditTextMultiLanguage.enterNewFormula(currentFormula.toString());
						currentFormula.highlightTextField(brickView, orientation);
					} else {
						formulaEditorEditTextMultiLanguage.quickSelect();
					}
					refreshFormulaPreviewString(formulaEditorEditTextMultiLanguage.getText().toString());
				}
				break;
			case SET_FORMULA_ON_SWITCH_EDIT_TEXT:
				if (currentFormula == newFormula && formulaEditorEditTextMultiLanguage.hasChanges()) {
					formulaEditorEditTextMultiLanguage.quickSelect();
					break;
				}
				if (formulaEditorEditTextMultiLanguage.hasChanges()) {
					if (!saveFormulaIfPossible()) {
						return;
					}
				}
				if (currentFormula != null) {
					currentFormula.refreshTextField(brickView);
				}
				formulaEditorEditTextMultiLanguage.endEdit();
				currentFormula.removeTextFieldHighlighting(brickView, orientation);
				currentFormula = newFormula;
				currentFormula.highlightTextField(brickView, orientation);
				formulaEditorEditTextMultiLanguage.enterNewFormula(newFormula.toString());
				break;
			default:
				break;
		}
	}

	private int parseFormula(String formulaToParse) {
		CalcGrammarParser parser = CalcGrammarParser.getFormulaParser(formulaToParse);
		FormulaElement parserFormulaElement = parser.parseFormula();
		int parserResult = parser.getErrorCharacterPosition();

		if (parserResult == PARSER_OK) {
			currentFormula.setRoot(parserFormulaElement);
		}

		return parserResult;
	}

	//	@Override
	//	public void onClick(View v) {
	//
	//		switch (v.getId()) {
	//			case R.id.formula_editor_ok_button:
	//				if (saveFormulaIfPossible()) {
	//					onUserDismiss();
	//				}
	//				break;
	//
	//			case R.id.formula_editor_undo_button:
	//				makeUndoButtonClickable(formulaEditorEditText.undo());
	//				makeRedoButtonClickable(true);
	//				break;
	//
	//			case R.id.formula_editor_redo_button:
	//				makeRedoButtonClickable(formulaEditorEditText.redo());
	//				makeUndoButtonClickable(true);
	//				break;
	//
	//			default:
	//				break;
	//
	//		}
	//	}

	public void handleUndoButton() {
		formulaEditorEditTextMultiLanguage.undo();
	}

	public void handleRedoButton() {
		formulaEditorEditTextMultiLanguage.redo();
	}

	public boolean saveFormulaIfPossible() {
		String formulaToParse = formulaEditorEditTextMultiLanguage.getText().toString();
		int err = parseFormula(formulaToParse);
		switch (err) {
			case PARSER_OK:
				if (brickSpace != null) {
					currentFormula.refreshTextField(brickView);
				}
				formulaEditorEditTextMultiLanguage.formulaSaved();
				showToast(R.string.formula_editor_changes_saved);
				return true;
			case PARSER_STACK_OVERFLOW:
				return checkReturnWithoutSaving(PARSER_STACK_OVERFLOW);
			default:
				formulaEditorEditTextMultiLanguage.setParseErrorCursor(err);
				return checkReturnWithoutSaving(PARSER_INPUT_SYNTAX_ERROR);
		}
	}

	private boolean checkReturnWithoutSaving(int errorType) {
		if (System.currentTimeMillis() <= confirmBack + 2000) {
			showToast(R.string.formula_editor_changes_discarded);
			return true;
		} else {
			switch (errorType) {
				case PARSER_INPUT_SYNTAX_ERROR:
					showToast(R.string.formula_editor_parse_fail);
					break;
				case PARSER_STACK_OVERFLOW:
					showToast(R.string.formula_editor_parse_fail_formula_too_long);
					break;
			}
			confirmBack = System.currentTimeMillis();
			return false;
		}

	}

	public void showToast(int ressourceId) {
		Utils.displayToast((SherlockFragmentActivity) context, context.getResources().getString(ressourceId));
	}

	@Override
	public boolean onKey(DialogInterface di, int keyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					endFormulaEditor();
					break;
				default:
					break;
			}
		} else if (event.getAction() == KeyEvent.ACTION_DOWN) {

		}
		return true;
	}

	public void endFormulaEditor() {

		if (formulaEditorEditTextMultiLanguage.hasChanges()) {
			if (saveFormulaIfPossible()) {
				onUserDismiss();
			}
		} else {
			onUserDismiss();
		}
	}

	public void refreshFormulaPreviewString(String formulaString) {
		currentFormula.refreshTextField(brickView, formulaEditorEditTextMultiLanguage.getText().toString());

	}

}
