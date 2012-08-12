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
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.BaseAdapter;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;

/**
 * @author robert
 * 
 */
public class PhysicCommandBrickAdapter implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private final Brick brick;
	private final PhysicCommand command;

	public PhysicCommandBrickAdapter(PhysicCommand command) {
		this.command = command;
		this.brick = command.getBrick();
	}

	@Override
	public Brick clone() {
		return new PhysicCommandBrickAdapter(command.clone());
	}

	public void execute() {
		command.execute();
	}

	public Sprite getSprite() {
		return brick.getSprite();
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {
		return brick.getView(context, brickId, adapter);
	}

	public View getPrototypeView(Context context) {
		return brick.getPrototypeView(context);
	}

	public int getRequiredResources() {
		return brick.getRequiredResources();
	}

	public void onClick(DialogInterface dialog, int which) {
		if (brick instanceof OnClickListener) {
			((OnClickListener) brick).onClick(dialog, which);
		}
	}
}
