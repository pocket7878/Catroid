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
package at.tugraz.ist.catroid.physics;

import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Sprite;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * @author robert
 * 
 */
public class PhysicBodyBuilder {

	private transient final World world;

	public PhysicBodyBuilder(World world) {
		this.world = world;

		if (PhysicSettings.World.SURROUNDING_BOX) {
			int frameWidthPixels = PhysicSettings.World.SURROUNDING_BOX_FRAME_SIZE;

			float screenWidth = PhysicWorldConverter.lengthCatToBox2D(Values.SCREEN_WIDTH);
			float screenHeight = PhysicWorldConverter.lengthCatToBox2D(Values.SCREEN_HEIGHT);

			float topPos = PhysicWorldConverter.lengthCatToBox2D(-Values.SCREEN_HEIGHT / 2 - 2 * frameWidthPixels);
			float bottomPos = PhysicWorldConverter.lengthCatToBox2D(Values.SCREEN_HEIGHT / 2 + 2 * frameWidthPixels);
			float leftPos = PhysicWorldConverter.lengthCatToBox2D(-Values.SCREEN_WIDTH / 2 - 2 * frameWidthPixels);
			float rightPos = PhysicWorldConverter.lengthCatToBox2D(Values.SCREEN_WIDTH / 2 + 2 * frameWidthPixels);

			Body top = createBox(BodyType.StaticBody, screenWidth, frameWidthPixels * 2);
			top.setTransform(0, topPos, 0.0f);
			Body bottom = createBox(BodyType.StaticBody, screenWidth, frameWidthPixels * 2);
			bottom.setTransform(0, bottomPos, 0.0f);
			Body left = createBox(BodyType.StaticBody, frameWidthPixels * 2, screenHeight);
			left.setTransform(leftPos, 0, 0.0f);
			Body right = createBox(BodyType.StaticBody, frameWidthPixels * 2, screenHeight);
			right.setTransform(rightPos, 0, 0.0f);
		}
	}

	public Body createBody(Sprite sprite) {
		Body body = null;
		int[] resolution = sprite.costume.getCostumeData().getResolution();

		if (resolution[0] == resolution[1]) {
			float radius = PhysicWorldConverter.lengthCatToBox2D(resolution[0] / 2.0f);
			body = createCircle(BodyType.DynamicBody, radius);
		} else {
			float width = PhysicWorldConverter.lengthCatToBox2D(resolution[0]);
			float height = PhysicWorldConverter.lengthCatToBox2D(resolution[1]);
			body = createBox(BodyType.DynamicBody, width, height);
		}

		return body;
	}

	private Body createBox(BodyType type, float width, float height) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2f, height / 2f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.3f;
		fixtureDef.restitution = 0.75f;

		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		return body;
	}

	private Body createCircle(BodyType type, float radius) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;

		CircleShape shape = new CircleShape();
		shape.setRadius(radius);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.3f;

		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		return body;
	}
}
