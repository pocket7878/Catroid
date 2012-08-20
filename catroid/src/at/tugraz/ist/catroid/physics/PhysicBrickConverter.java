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
package at.tugraz.ist.catroid.physics;

import java.util.List;

import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.SetPhysicObjectTypeBrick;
import at.tugraz.ist.catroid.physics.commands.PhysicPlaceAtBrick;

/**
 * TODO: Find better name.
 */
public class PhysicBrickConverter {

	public PhysicBrickConverter() {
	}

	// TODO: Here we are missing OOP!
	public void convert(Project project) {
		for (Sprite sprite : project.getSpriteList()) {
			boolean containsPhysicObjectBrick = false;

			for (int scriptIndex = 0; scriptIndex < sprite.getNumberOfScripts(); scriptIndex++) {
				Script script = sprite.getScript(scriptIndex);

				if (containsPhysicObjectBrick) {
					List<Brick> brickList = script.getBrickList();

					for (int brickIndex = 0; brickIndex < brickList.size(); brickIndex++) {
						Brick brick = brickList.get(brickIndex);
						if (brick instanceof PlaceAtBrick) {
							brick = new PhysicPlaceAtBrick((PlaceAtBrick) brick);
							brickList.set(brickIndex, brick);
						}
					}
				} else {
					for (Brick brick : script.getBrickList()) {
						if (brick instanceof SetPhysicObjectTypeBrick) {
							containsPhysicObjectBrick = true;
							project.getPhysicWorld().getPhysicObject(sprite);
							scriptIndex = -1;
							break;
						}
					}
				}
			}
		}
	}
}
