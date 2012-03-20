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
import android.widget.BaseAdapter;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class SetVelocityBrick implements Brick {
	private static final long serialVersionUID = 1L;
	//private int velocity;
	private Sprite sprite;

	@XStreamOmitField
	private transient View view;

	public SetVelocityBrick(Sprite sprite) { //, int xPosition) {
		this.sprite = sprite;
		//this.xPosition = xPosition;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
		//PhysicWorld.getInstance().setVelocity(sprite, 10, 10);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_set_velocity, null);

		//		EditText editX = (EditText) view.findViewById(R.id.brick_set_x_edit_text);
		//		editX.setText(String.valueOf(xPosition));

		//		editX.setOnClickListener(this);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_set_velocity, null);
	}

	@Override
	public Brick clone() {
		return new SetVelocityBrick(getSprite()); //, xPosition);
	}
}
