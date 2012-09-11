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
package at.tugraz.ist.catroid.physics.commands;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.physics.PhysicWorld;
import at.tugraz.ist.catroid.physics.PhysicWorldConverter;

/**
 * @author robert
 * 
 */
public class PhysicPlaceAtBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private transient final PhysicWorld physicWorld;
	private transient final PlaceAtBrick placeAtBrick;

	public PhysicPlaceAtBrick(PlaceAtBrick placeAtBrick) {
		this.placeAtBrick = placeAtBrick;
		physicWorld = ProjectManager.getInstance().getCurrentProject().getPhysicWorld();
	}

	@Override
	public void execute() {
		float box2dXPosition = PhysicWorldConverter.lengthCatToBox2d(placeAtBrick.getXPosition());
		float box2dYPosition = PhysicWorldConverter.lengthCatToBox2d(placeAtBrick.getYPosition());

		placeAtBrick.execute();
		physicWorld.getPhysicObject(getSprite()).setXYPosition(box2dXPosition, box2dYPosition);
	}

	@Override
	public void onClick(View view) {
		placeAtBrick.onClick(view);
	}

	@Override
	public Brick clone() {
		return placeAtBrick.clone();
	}

	@Override
	public Sprite getSprite() {
		return placeAtBrick.getSprite();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		return placeAtBrick.getView(context, brickId, adapter);
	}

	@Override
	public View getPrototypeView(Context context) {
		return placeAtBrick.getPrototypeView(context);
	}

	@Override
	public int getRequiredResources() {
		return placeAtBrick.getRequiredResources();
	}

}
