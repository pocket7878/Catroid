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
package at.tugraz.ist.catroid.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.fragment.FormulaEditorFragment;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author marki
 * 
 */
public class FormulaEditorActivity extends SherlockFragmentActivity {

	private ActionBar actionBar;
	private FormulaEditorFragment formulaEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_formula_editor);
		setUpActionBar();
		formulaEditor = (FormulaEditorFragment) getSupportFragmentManager().findFragmentById(
				R.id.fragment_formula_editor);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_formula_editor, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void setUpActionBar() {
		actionBar = getSupportActionBar();
		actionBar.setTitle(this.getString(R.string.dialog_formula_editor_title));
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:

				break;
			case R.id.menu_save:
				formulaEditor.handleSaveButton();
				break;
			case R.id.menu_undo:
				formulaEditor.handleUndoButton();
				break;
			case R.id.menu_redo:
				formulaEditor.handleRedoButton();
				break;
			default:
				break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return formulaEditor.onKey(null, keyCode, event);
	}

}
