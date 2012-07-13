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
import java.util.ArrayList;

import at.tugraz.ist.catroid.content.Sprite;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class PhysicWorld implements Serializable {
	private static final long serialVersionUID = -9103964560286141267L;

	private ArrayList<Sprite> spriteList;
	private transient PhysicShapeBuilder physicShapeBuilder;
	private transient PhysicRenderer renderer;

	public PhysicWorld() {
		spriteList = new ArrayList<Sprite>();
		physicShapeBuilder = new PhysicShapeBuilder();
		physicShapeBuilder.createSurroundingBox();
	}

	public void step(float deltaTime) {
		physicShapeBuilder.getWorld().step(PhysicWorldSetting.timeStep, PhysicWorldSetting.velocityIterations,
				PhysicWorldSetting.positionIterations);
		refreshSprites();
	}

	private void refreshSprites() {
		for (Sprite sprite : spriteList) {
			Vector2 catrobatCoords = physicShapeBuilder.getPosition(sprite);
			float angle = physicShapeBuilder.getAngle(sprite);
			angle = PhysicWorldConverter.angleBox2DToCat(angle);

			System.out.println("#### DEBUG : angle : " + angle);

			sprite.costume.aquireXYWidthHeightLock(); //   ?
			sprite.costume.setXYPosition(catrobatCoords.x, catrobatCoords.y);
			sprite.costume.rotation = angle;
			sprite.costume.releaseXYWidthHeightLock();
			physicShapeBuilder.turn(sprite);
			//System.out.println("#### DEBUG  x:" + sprite.costume.x + "  y:" + sprite.costume.y);
			//physicShapeBuilder.printStaticBodys();
			//System.out.println("#### DEBUG - ENDE  ");
		}
	}

	public void render(Matrix4 perspectiveMatrix) {
		if (renderer == null) {
			renderer = new PhysicRenderer();
		}
		renderer.render(perspectiveMatrix, physicShapeBuilder.getBodies());
	}

	public void setGravity(Sprite sprite, Vector2 gravity) {
		physicShapeBuilder.setGravity(sprite, gravity);
	}

	public void setVelocity(Sprite sprite, Vector2 velocity) {
		boolean added = physicShapeBuilder.setVelocity(sprite, velocity);
		if (added) {
			spriteList.add(sprite);
		}
	}

	public void setMass(Sprite sprite, float mass) {
		boolean added = physicShapeBuilder.setMassData(sprite, mass);
		if (added) {
			spriteList.add(sprite);
		}
	}

	public void setPhysicShapeBuilderTestMock(PhysicShapeBuilder physShBTestMock) {
		physicShapeBuilder = physShBTestMock;
	}
	/*
	 * private ShapeRenderer renderer;
	 * 
	 * public void drawCollisionBorders(Matrix4 projectionMatrix) {
	 * if (renderer == null) {
	 * renderer = new ShapeRenderer();
	 * }
	 * renderer.setProjectionMatrix(projectionMatrix);
	 * 
	 * for (Sprite sprite : spriteList) {
	 * Body body = physicShapeBuilder.getBody(sprite);
	 * for (Fixture fixture : body.getFixtureList()) {
	 * Shape shape = fixture.getShape();
	 * 
	 * renderer.begin(ShapeType.Line);
	 * int i = 0;
	 * for (; i < (((PolygonShape) shape).getVertexCount() - 1); i++) {
	 * Vector2 start = new Vector2();
	 * ((PolygonShape) shape).getVertex(i, start);
	 * start = PhysicWorldConverter.vectBox2DToCat(body.getWorldPoint(start));
	 * Vector2 end = new Vector2();
	 * ((PolygonShape) shape).getVertex(i + 1, end);
	 * end = PhysicWorldConverter.vectBox2DToCat(body.getWorldPoint(end));
	 * 
	 * renderer.line(start.x, start.y, end.x, end.y);
	 * }
	 * Vector2 start = new Vector2();
	 * ((PolygonShape) shape).getVertex(i, start);
	 * start = PhysicWorldConverter.vectBox2DToCat(body.getWorldPoint(start));
	 * Vector2 end = new Vector2();
	 * ((PolygonShape) shape).getVertex(0, end);
	 * end = PhysicWorldConverter.vectBox2DToCat(body.getWorldPoint(end));
	 * renderer.line(start.x, start.y, end.x, end.y);
	 * 
	 * renderer.end();
	 * }
	 * }
	 * }
	 */
}