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

import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.physics.PhysicObject;
import at.tugraz.ist.catroid.physics.PhysicWorldConverter;

import com.badlogic.gdx.math.Vector2;

/**
 * @author robert
 * 
 */
public class PhysicSetXCommand extends PhysicCommand {

	public PhysicSetXCommand(PhysicObject physicObject, Brick brick) {
		super(physicObject, brick);
	}

	@Override
	public void execute() {
		physicObject.setPosition(PhysicWorldConverter.vecCatToBox2d(new Vector2(0.0f, 300.0f)));
	}

	@Override
	protected PhysicCommand clone() {
		return new PhysicSetXCommand(physicObject, brick);
	}
}
