package at.tugraz.ist.catroid.ui.dialogs;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class FormulaEditorDialog extends DialogFragment implements OnClickListener, OnKeyListener {

	private Context context;
	private static Brick currentBrick = null;
	private static Formula currentFormula = null;
	private FormulaEditorEditText formulaEditorEditText;
	private CatKeyboardView catKeyboardView;
	private LinearLayout brickSpace;
	private View brickView;
	private Button okButton = null;
	private Button undoButton = null;
	private Button redoButton = null;
	private long confirmBack = 0;
	private boolean buttonIsBackButton = true;
	public boolean restoreInstance = false;

	public FormulaEditorDialog() {
		//do not remove, used for orientation change
	}

	public FormulaEditorDialog(Context ctx, Brick brick) {

	}

	public FormulaEditorDialog(Brick brick, Formula formula) {
		currentBrick = brick;
		currentFormula = formula;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			restoreInstance = savedInstanceState.getBoolean("restoreInstance");
		}
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_FRAME, R.style.dialog_fullscreen);

	}

	@Override
	public void onSaveInstanceState(Bundle saveInstanceState) {
		saveInstanceState.putBoolean("restoreInstance", true);
		super.onSaveInstanceState(saveInstanceState);
	}

	public static void showDialog(View view, Brick brick, Formula formula) {
		SherlockFragmentActivity activity = null;
		if (SherlockFragmentActivity.class.isAssignableFrom(view.getContext().getClass())) { //this view is from any SherlockFragmentActivity
			activity = (SherlockFragmentActivity) view.getContext();
		} else {
			activity = (SherlockFragmentActivity) ((ContextWrapper) view.getContext()).getBaseContext(); //this view is from within this dialog, happens when you click an edittext within the editor
		}

		FormulaEditorDialog formulaEditorDialog = null;
		if (activity.getSupportFragmentManager().findFragmentByTag("formula_editor_dialog") == null) {
			formulaEditorDialog = new FormulaEditorDialog(brick, formula);
			formulaEditorDialog.show(activity.getSupportFragmentManager(), "formula_editor_dialog");
		} else {
			formulaEditorDialog = (FormulaEditorDialog) activity.getSupportFragmentManager().findFragmentByTag(
					"formula_editor_dialog");
			formulaEditorDialog.setInputFormula(formula);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View dialogView = inflater.inflate(R.layout.dialog_formula_editor, container);
		context = dialogView.getContext();
		brickSpace = (LinearLayout) dialogView.findViewById(R.id.formula_editor_brick_space);
		if (brickSpace != null) {
			brickView = currentBrick.getView(context, 0, null);
			brickSpace.addView(brickView);
		}

		okButton = (Button) dialogView.findViewById(R.id.formula_editor_ok_button);
		okButton.setOnClickListener(this);

		undoButton = (Button) dialogView.findViewById(R.id.formula_editor_undo_button);
		undoButton.setOnClickListener(this);

		redoButton = (Button) dialogView.findViewById(R.id.formula_editor_redo_button);
		redoButton.setOnClickListener(this);

		formulaEditorEditText = (FormulaEditorEditText) dialogView.findViewById(R.id.formula_editor_edit_field);
		if (brickSpace != null) {
			brickSpace.measure(0, 0);
		}
		catKeyboardView = (CatKeyboardView) dialogView.findViewById(R.id.keyboardcat);
		catKeyboardView.setEditText(formulaEditorEditText);
		//catKeyboardView.setCurrentBrick(currentBrick);

		makeRedoButtonClickable(false);
		makeUndoButtonClickable(false);
		makeOkButtonBackButton();

		if (brickSpace != null) {
			formulaEditorEditText.init(this, brickSpace.getMeasuredHeight(), catKeyboardView);
		} else {
			formulaEditorEditText.init(this, 0, catKeyboardView);
		}

		setInputFormula(currentFormula);
		getDialog().setOnKeyListener(this);
		return dialogView;
	}

	public void setInputFormula(Formula newFormula) {

		int orientation = getResources().getConfiguration().orientation;

		if (restoreInstance) { //after orientation switch
			restoreInstance = false;
			if (!formulaEditorEditText.restoreFieldFromPreviousHistory()) { //history is only deleted when editor is shut down by  user!
				formulaEditorEditText.enterNewFormula(newFormula.getEditTextRepresentation()); // this happens when onSaveInstanceState() is being called but not by orientation change (e.g.user turns off screen)
			}
			refreshFormulaPreviewString(formulaEditorEditText.getText().toString());

			currentFormula.highlightTextField(brickView,
					getResources().getDrawable(R.drawable.edit_text_formula_editor_selected), orientation);

		} else if (newFormula == currentFormula) {

			if (!formulaEditorEditText.hasChanges()) {
				currentFormula.removeTextFieldHighlighting(brickView, orientation);
				formulaEditorEditText.enterNewFormula(currentFormula.getEditTextRepresentation());
				currentFormula.highlightTextField(brickView,
						getResources().getDrawable(R.drawable.edit_text_formula_editor_selected), orientation);
			} else {
				formulaEditorEditText.quickSelect();
			}
			refreshFormulaPreviewString(formulaEditorEditText.getText().toString());
		} else {
			if (!formulaEditorEditText.hasChanges()) {
				if (currentFormula != null) {
					currentFormula.refreshTextField(brickView);
				}
				formulaEditorEditText.endEdit();
				currentFormula.removeTextFieldHighlighting(brickView, orientation);
				currentFormula = newFormula;
				currentFormula.highlightTextField(brickView,
						getResources().getDrawable(R.drawable.edit_text_formula_editor_selected), orientation);
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
			currentFormula.setRoot(parserFormulaElement);
		}
		return -1;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.formula_editor_ok_button:
				if (buttonIsBackButton) {
					onUserDismiss();
				} else {
					String formulaToParse = formulaEditorEditText.getText().toString();
					int err = parseFormula(formulaToParse);
					if (err == -1) {
						if (brickSpace != null) {
							currentFormula.refreshTextField(brickView);
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

	private void onUserDismiss() { //dont override onDismiss, this must not be called on orientation change
		formulaEditorEditText.endEdit();
		currentFormula = null;
		currentBrick = null;
		dismiss();
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
	public boolean onKey(DialogInterface di, int keyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					if (formulaEditorEditText.hasChanges()) {
						if (System.currentTimeMillis() <= confirmBack + 2000) {
							showToast(R.string.formula_editor_changes_discarded);
							onUserDismiss();
						} else {
							showToast(R.string.formula_editor_confirm_discard);
							confirmBack = System.currentTimeMillis();
						}
					} else {
						onUserDismiss();
					}
					break;
				default:
					break;

			}
		} else if (event.getAction() == KeyEvent.ACTION_DOWN) {

		}
		return true;
	}

	public void refreshFormulaPreviewString(String formulaString) {
		currentFormula.refreshTextField(brickView, formulaEditorEditText.getText().toString());

	}

}
