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
import java.util.HashMap;
import java.util.Map;
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
	private final transient Map<Sprite, PhysicObject> physicObjects;
	private final transient PhysicShapeBuilder shapeBuilder;
	private transient Box2DDebugRenderer renderer;

	public PhysicWorld() {
		physicObjects = new HashMap<Sprite, PhysicObject>();
		shapeBuilder = new PhysicShapeBuilder();
		createBoundaryBox();
	}

	private void createBoundaryBox() {
		float boxWidth = PhysicWorldConverter.lengthCatToBox2d(Values.SCREEN_WIDTH);
		float boxHeight = PhysicWorldConverter.lengthCatToBox2d(Values.SCREEN_HEIGHT);
		float boxElementSize = PhysicSettings.World.BoundaryBox.FRAME_SIZE;

		// Top Element
		createBoundaryBoxElement(0.0f, boxHeight / 2 + boxElementSize, boxWidth, boxElementSize * 2);
		// Bottom Element
		createBoundaryBoxElement(0.0f, -boxHeight / 2 - boxElementSize, boxWidth, boxElementSize * 2);
		// Left Element
		createBoundaryBoxElement(-boxWidth / 2 - boxElementSize, 0.0f, boxElementSize * 2, boxHeight);
		// Right Element
		createBoundaryBoxElement(boxWidth / 2 + boxElementSize, 0.0f, boxElementSize * 2, boxHeight);
	}

	private void createBoundaryBoxElement(float x, float y, float width, float height) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2f, height / 2f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.filter.categoryBits = PhysicSettings.World.BoundaryBox.COLLISION_MASK;
		fixtureDef.filter.maskBits = PhysicSettings.Object.COLLISION_MASK;

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
		for (Entry<Sprite, PhysicObject> entry : physicObjects.entrySet()) {
			physicObject = entry.getValue();
			physicObject.setIfOnEdgeBounce(false);
			Vector2 position = PhysicWorldConverter.vecBox2dToCat(physicObject.getPosition());
			float angle = PhysicWorldConverter.angleBox2dToCat(physicObject.getAngle());

			costume = entry.getKey().costume;
			costume.aquireXYWidthHeightLock();
			costume.setXYPosition(position.x, position.y);
			costume.setRotation(angle);
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

	public PhysicObject createPhysicObject(Sprite sprite) {
		if (physicObjects.containsKey(sprite)) {
			return physicObjects.get(sprite);
		}

		PhysicObject physicObject = new PhysicObject(world.createBody(new BodyDef()));
		physicObjects.put(sprite, physicObject);

		return physicObject;
	}

	public PhysicObject getPhysicObject(Sprite sprite) {
		return createPhysicObject(sprite);
	}

	public boolean isPhysicObject(Sprite sprite) {
		return physicObjects.containsKey(sprite);
	}

	public void changeCostume(Sprite sprite) {
		CostumeData costumeData = sprite.costume.getCostumeData();
		Shape[] shapes = shapeBuilder.createShape(costumeData);
		physicObjects.get(sprite).setShape(shapes);
	}
}
