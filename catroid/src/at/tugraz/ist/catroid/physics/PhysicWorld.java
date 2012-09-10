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
import java.util.Map.Entry;

import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.Sprite;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicWorld implements Serializable {
	static {
		GdxNativesLoader.load();
	}

	private static final long serialVersionUID = -9103964560286141267L;

	private final transient World world = new World(PhysicSettings.World.DEFAULT_GRAVITY,
			PhysicSettings.World.IGNORE_SLEEPING_OBJECTS);
	private final transient PhysicObjectMap objects;
	private final transient PhysicShapeBuilder shapeBuilder;
	private transient Box2DDebugRenderer renderer;

	public PhysicWorld() {
		objects = new PhysicObjectMap(world);
		shapeBuilder = new PhysicShapeBuilder();
		if (PhysicSettings.World.SURROUNDING_BOX) {
			createSurroundingBox();
		}
	}

	private void createSurroundingBox() {
		float boxWidth = PhysicWorldConverter.lengthCatToBox2d(Values.SCREEN_WIDTH);
		float boxHeight = PhysicWorldConverter.lengthCatToBox2d(Values.SCREEN_HEIGHT);
		float boxElementSize = PhysicSettings.World.SURROUNDING_BOX_FRAME_SIZE;

		// Top Element
		createSurroundingBoxElement(0.0f, boxHeight / 2 + boxElementSize, boxWidth, boxElementSize * 2);
		// Bottom Element
		createSurroundingBoxElement(0.0f, -boxHeight / 2 - boxElementSize, boxWidth, boxElementSize * 2);
		// Left Element
		createSurroundingBoxElement(-boxWidth / 2 - boxElementSize, 0.0f, boxElementSize * 2, boxHeight);
		// Right Element
		createSurroundingBoxElement(boxWidth / 2 + boxElementSize, 0.0f, boxElementSize * 2, boxHeight);
	}

	private void createSurroundingBoxElement(float x, float y, float width, float height) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2f, height / 2f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;

		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		body.setTransform(x, y, 0.0f);
	}

	public void step(float deltaTime) {
		world.step(deltaTime, PhysicSettings.World.VELOCITY_ITERATIONS, PhysicSettings.World.POSITION_ITERATIONS);
		updateSprites();
	}

	private void updateSprites() {
		PhysicObject physicObject;
		Costume costume;
		for (Entry<Sprite, PhysicObject> entry : objects) {
			physicObject = entry.getValue();
			Vector2 position = PhysicWorldConverter.vecBox2dToCat(physicObject.getPosition());
			float angle = PhysicWorldConverter.angleBox2dToCat(physicObject.getAngle());

			costume = entry.getKey().costume;
			costume.aquireXYWidthHeightLock();
			costume.setXYPosition(position.x, position.y);
			costume.rotation = angle;
			costume.releaseXYWidthHeightLock();
		}
	}

	public void render(Matrix4 perspectiveMatrix) {
		if (renderer == null) {
			renderer = new Box2DDebugRenderer(PhysicSettings.Render.RENDER_BODIES, PhysicSettings.Render.RENDER_JOINTS,
					PhysicSettings.Render.RENDER_AABBs, PhysicSettings.Render.RENDER_INACTIVE_BODIES);
		}
		renderer.render(world, perspectiveMatrix.scl(PhysicSettings.World.RATIO));
	}

	public void setGravity(Sprite sprite, Vector2 gravity) {
		world.setGravity(gravity);
	}

	public PhysicObject getPhysicObject(Sprite sprite) {
		return objects.get(sprite);
	}

	public void changeCostume(Sprite sprite) {
		CostumeData costumeData = sprite.costume.getCostumeData();
		Shape[] shapes = shapeBuilder.createShape(costumeData);
		objects.get(sprite).setShape(shapes);
	}
}