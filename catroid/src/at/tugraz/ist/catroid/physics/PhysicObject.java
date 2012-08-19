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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * @author robert
 * 
 */
public class PhysicObject {

	public enum Type {
		DYNAMIC, FIXED, NONE;
	}

	private final Body body;
	private final FixtureDef fixtureDef = new FixtureDef();
	private Type type;

	public PhysicObject(Body body) {
		this.body = body;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.2f;
		fixtureDef.restitution = 0.0f;
		setType(Type.NONE);
	}

	public void setShape(Shape shape) {
		// TODO: This code is cursed, I tell you!
		// Switch code, so body always has a shape.

		if (shape == fixtureDef.shape) {
			return;
		}

		for (Fixture fixture : body.getFixtureList()) {
			body.destroyFixture(fixture);
		}

		if (shape != null) {
			fixtureDef.shape = shape;
			if (type != Type.NONE) {
				body.createFixture(fixtureDef);
			}
		}
	}

	public void setType(Type type) {
		if (this.type == type) {
			return;
		}
		this.type = type;

		Shape shape = null;
		switch (type) {
			case DYNAMIC:
				body.setType(BodyType.DynamicBody);
				shape = fixtureDef.shape;
				break;
			case FIXED:
				body.setType(BodyType.KinematicBody);
				shape = fixtureDef.shape;
				break;
			case NONE:
				body.setType(BodyType.StaticBody);
				break;
		}

		setShape(shape);
	}

	public float getAngle() {
		return body.getAngle();
	}

	public void setAngle(float angle) {
		body.setTransform(body.getPosition(), angle);
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}

	// TODO: Test it!
	public void setPosition(float x, float y) {
		body.setTransform(x, y, body.getAngle());
	}

	// TODO: Test it!
	public void setXPosition(float x) {
		body.setTransform(x, body.getPosition().y, body.getAngle());
	}

	// TODO: Test it!
	public void setYPosition(float y) {
		body.setTransform(body.getPosition().x, y, body.getAngle());
	}

	public void setMass(float mass) {
		MassData massData = body.getMassData();
		massData.mass = mass;
		body.setMassData(massData);
	}

	public void setDensity(float density) {
		fixtureDef.density = density;
		for (Fixture fixture : body.getFixtureList()) {
			fixture.setDensity(density);
		}
		body.resetMassData();
	}

	public void setFriction(float friction) {
		fixtureDef.friction = friction;
		for (Fixture fixture : body.getFixtureList()) {
			fixture.setFriction(friction);
		}
	}

	public void setRestitution(float restitution) {
		fixtureDef.restitution = restitution;
		for (Fixture fixture : body.getFixtureList()) {
			fixture.setRestitution(restitution);
		}
	}

	public void setAngularVelocicty(float radian) {
		body.setAngularVelocity(radian);
	}

	public void setLinearVelocicty(Vector2 velocity) {
		body.setLinearVelocity(velocity);
	}
}
