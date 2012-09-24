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

import at.tugraz.ist.catroid.common.Values;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicBoundaryBox {

	private final transient World world;

	public PhysicBoundaryBox(World world) {
		this.world = world;
	}

	public void create() {
		float boxWidth = PhysicWorldConverter.lengthCatToBox2d(Values.SCREEN_WIDTH);
		float boxHeight = PhysicWorldConverter.lengthCatToBox2d(Values.SCREEN_HEIGHT);
		float boxElementSize = PhysicWorldConverter.lengthCatToBox2d(PhysicSettings.World.BoundaryBox.FRAME_SIZE);
		float halfBoxElementSize = boxElementSize / 2.0f;

		// Top
		createSide(new Vector2(0.0f, (boxHeight / 2.0f) + halfBoxElementSize), boxWidth, boxElementSize);
		// Bottom
		createSide(new Vector2(0.0f, -(boxHeight / 2.0f) - halfBoxElementSize), boxWidth, boxElementSize);
		// Left
		createSide(new Vector2(-(boxWidth / 2.0f) - halfBoxElementSize, 0.0f), boxElementSize, boxHeight);
		// Right
		createSide(new Vector2((boxWidth / 2.0f) + halfBoxElementSize, 0.0f), boxElementSize, boxHeight);
	}

	private void createSide(Vector2 center, float width, float height) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.allowSleep = false;

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2f, height / 2f, center, 0.0f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.filter.maskBits = PhysicSettings.Object.COLLISION_MASK;

		if (PhysicSettings.DEBUGFLAG) {
			fixtureDef.filter.categoryBits = PhysicSettings.Object.COLLISION_MASK;
		} else {
			fixtureDef.filter.categoryBits = PhysicSettings.World.BoundaryBox.COLLISION_MASK;
		}

		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
	}
}
