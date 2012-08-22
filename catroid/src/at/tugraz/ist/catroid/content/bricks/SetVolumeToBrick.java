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

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.ui.fragment.FormulaEditorFragment;

public class SetVolumeToBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;

	private Sprite sprite;
	private Formula volume;

	public SetVolumeToBrick(Sprite sprite, float volumeValue) {
		this.sprite = sprite;
		volume = new Formula(Float.toString(volumeValue));
	}

	public SetVolumeToBrick(Sprite sprite, Formula volume) {
		this.sprite = sprite;
		this.volume = volume;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		SoundManager.getInstance().setVolume(volume.interpretFloat(0.0f, 100.0f));
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		View view = View.inflate(context, R.layout.brick_set_volume_to, null);

		TextView text = (TextView) view.findViewById(R.id.brick_set_volume_to_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_set_volume_to_edit_text);
		volume.setTextFieldId(R.id.brick_set_volume_to_edit_text);
		volume.refreshTextField(view);

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);

		edit.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_set_volume_to, null);
	}

	@Override
	public Brick clone() {
		return new SetVolumeToBrick(getSprite(), volume);
	}

	@Override
	public void onClick(View view) {
		FormulaEditorFragment.showFragment(view, this, volume);
	}
}
