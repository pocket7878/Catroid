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

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;

public class TouchBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private Sprite touchedSprite;
	protected TouchEndBrick touchEndBrick;

	public TouchBrick(Sprite sprite, Sprite touchedSprite) {
		this.sprite = sprite;
		this.touchedSprite = touchedSprite;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public Sprite getTouchedSprite() {
		return touchedSprite;
	}

	public TouchEndBrick getTouchEndBrick() {
		return this.touchEndBrick;
	}

	public void setTouchEndBrick(TouchEndBrick touchEndBrick) {
		this.touchEndBrick = touchEndBrick;
	}

	public void execute() {
		final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();

		if (!spriteList.contains(touchedSprite)) {
			touchedSprite = null;
		}

		if (touchedSprite == null) {
			touchedSprite = this.sprite;
		}

		Script script = touchEndBrick.getScript();
		if (!this.checkCollision(sprite, touchedSprite)) {
			script.setExecutingBrickIndex(script.getBrickList().indexOf(touchEndBrick));

			while (!sprite.isFinished) {

				if (this.checkCollision(sprite, touchedSprite)) {
					script.setExecutingBrickIndex(script.getBrickList().indexOf(this));
					return;
				}
			}
		}
	}

	public boolean checkCollision(Sprite sprite, Sprite touchedSprite) {
		int spriteXPosition = 0, spriteYPosition = 0, spriteWidth = 0, spriteHeight = 0;
		int touchedSpriteXPosition = 0, touchedSpriteYPosition = 0, touchedSpriteWidth = 0, touchedSpriteHeight = 0;

		sprite.costume.aquireXYWidthHeightLock();
		spriteXPosition = (int) sprite.costume.getXPosition();
		spriteYPosition = (int) sprite.costume.getYPosition();
		spriteWidth = (int) sprite.costume.getWidth();
		spriteHeight = (int) sprite.costume.getHeight();
		sprite.costume.releaseXYWidthHeightLock();
		touchedSprite.costume.aquireXYWidthHeightLock();
		touchedSpriteXPosition = (int) touchedSprite.costume.getXPosition();
		touchedSpriteYPosition = (int) touchedSprite.costume.getYPosition();
		touchedSpriteWidth = (int) touchedSprite.costume.getWidth();
		touchedSpriteHeight = (int) touchedSprite.costume.getHeight();
		touchedSprite.costume.releaseXYWidthHeightLock();

		if (spriteXPosition < touchedSpriteXPosition + touchedSpriteWidth
				&& spriteXPosition + spriteWidth > touchedSpriteXPosition
				&& spriteYPosition < touchedSpriteYPosition + touchedSpriteHeight
				&& spriteYPosition + spriteHeight > touchedSpriteYPosition) {
			return true;
		} else {
			return false;
		}

	}

	public View getView(final Context context, int brickId, BaseAdapter adapter) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.brick_touch, null);

		final Spinner spinner = (Spinner) brickView.findViewById(R.id.touch_spinner);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);
		spinner.setClickable(true);
		spinner.setEnabled(true);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAdapter.add(context.getString(R.string.broadcast_nothing_selected));

		final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();
		for (Sprite sprite : spriteList) {
			String spriteName = sprite.getName();
			String temp = this.sprite.getName();
			if (!spriteName.equals(temp) && !spriteName.equals("Background")) {
				spinnerAdapter.add(sprite.getName());
			}
		}
		spinner.setAdapter(spinnerAdapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String itemSelected = parent.getSelectedItem().toString();
				String nothingSelected = context.getString(R.string.broadcast_nothing_selected);
				final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance()
						.getCurrentProject().getSpriteList();

				if (itemSelected.equals(nothingSelected)) {
					touchedSprite = null;
				}
				for (Sprite sprite : spriteList) {
					String spriteName = sprite.getName();
					if (spriteName.equals(itemSelected)) {
						touchedSprite = sprite;
					}
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		if (spriteList.contains(touchedSprite)) {
			int pointedSpriteIndex = spinnerAdapter.getPosition(touchedSprite.getName());
			spinner.setSelection(pointedSpriteIndex);
		} else {
			spinner.setSelection(0);
		}

		return brickView;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.brick_touch, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new TouchBrick(sprite, touchedSprite);
	}

}
