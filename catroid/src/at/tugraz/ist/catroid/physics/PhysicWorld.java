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

import java.io.Serializable;

import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.Sprite;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicWorld implements Serializable {
	static {
		GdxNativesLoader.load();
	}

	private static final long serialVersionUID = -9103964560286141267L;

	private final transient World world = new World(PhysicSettings.World.DEFAULT_GRAVITY,
			PhysicSettings.World.IGNORE_SLEEPING_OBJECTS);
	private final transient PhysicObjectContainer objects = new PhysicObjectContainer(world);
	private transient PhysicRenderer renderer; // Don't add modifier 'final'

	public PhysicWorld() {
		if (PhysicSettings.World.SURROUNDING_BOX) {
			PhysicBodyBuilder bodyBuilder = objects.getBodyBuilder();
			int frameWidthPixels = PhysicSettings.World.SURROUNDING_BOX_FRAME_SIZE;

			float screenWidth = PhysicWorldConverter.lengthCatToBox2d(Values.SCREEN_WIDTH);
			float screenHeight = PhysicWorldConverter.lengthCatToBox2d(Values.SCREEN_HEIGHT);

			float topPos = PhysicWorldConverter.lengthCatToBox2d(-Values.SCREEN_HEIGHT / 2 - 2 * frameWidthPixels);
			float bottomPos = PhysicWorldConverter.lengthCatToBox2d(Values.SCREEN_HEIGHT / 2 + 2 * frameWidthPixels);
			float leftPos = PhysicWorldConverter.lengthCatToBox2d(-Values.SCREEN_WIDTH / 2 - 2 * frameWidthPixels);
			float rightPos = PhysicWorldConverter.lengthCatToBox2d(Values.SCREEN_WIDTH / 2 + 2 * frameWidthPixels);

			Body top = bodyBuilder.createBox(BodyType.StaticBody, screenWidth, frameWidthPixels * 2);
			top.setTransform(0, topPos, 0.0f);
			Body bottom = bodyBuilder.createBox(BodyType.StaticBody, screenWidth, frameWidthPixels * 2);
			bottom.setTransform(0, bottomPos, 0.0f);
			Body left = bodyBuilder.createBox(BodyType.StaticBody, frameWidthPixels * 2, screenHeight);
			left.setTransform(leftPos, 0, 0.0f);
			Body right = bodyBuilder.createBox(BodyType.StaticBody, frameWidthPixels * 2, screenHeight);
			right.setTransform(rightPos, 0, 0.0f);
		}
	}

	public void step(float deltaTime) {
		world.step(PhysicSettings.World.TIMESTEP, PhysicSettings.World.VELOCITY_ITERATIONS,
				PhysicSettings.World.POSITION_ITERATIONS);
		updateSprites();
	}

	private void updateSprites() {
		for (Sprite sprite : objects.getSprites()) {
			Body body = objects.get(sprite);
			Vector2 position = PhysicWorldConverter.vecBox2dToCat(body.getPosition());
			float angle = PhysicWorldConverter.angleBox2dToCat(body.getAngle());

			Costume costume = sprite.costume;
			costume.aquireXYWidthHeightLock();
			costume.setXYPosition(position.x, position.y);
			costume.rotation = angle;
			costume.releaseXYWidthHeightLock();
		}
	}

	public void render(Matrix4 perspectiveMatrix) {
		if (renderer == null) {
			renderer = new PhysicRenderer();
		}
		renderer.render(perspectiveMatrix, objects.getBodies());
	}

	public void setGravity(Sprite sprite, Vector2 gravity) {
		world.setGravity(PhysicWorldConverter.vecCatToBox2d(gravity));
	}

	public void setVelocity(Sprite sprite, Vector2 velocity) {
		Body body = objects.get(sprite);
		body.applyLinearImpulse(velocity, body.getPosition());
	}

	public void setMass(Sprite sprite, float mass) {
		objects.get(sprite).getMassData().mass = mass;
	}
}