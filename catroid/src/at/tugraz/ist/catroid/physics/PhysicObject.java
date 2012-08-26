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

import java.util.Vector;

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

	public final Body body;
	public final FixtureDef fixtureDef = new FixtureDef();
	public Type type;
	public float mass;

	public PhysicObject(Body body) {
		this.body = body;
		mass = body.getMass();

		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.2f;
		fixtureDef.restitution = 0.0f;

		setType(Type.NONE);
	}

	// TODO: "Ueberarbeiten, dalli dalli!
	public void setShape(Shape shape) {
		if (shape == fixtureDef.shape && !body.getFixtureList().isEmpty()) {
			return;
		}

		Fixture createdFixture = null;
		if (shape != null) {
			fixtureDef.shape = shape;
			if (type != Type.NONE) {
				createdFixture = body.createFixture(fixtureDef);
				setMass(mass);
			}
		}

		// TODO find a better way to get rid of ConcurrentModificationException.
		Vector<Fixture> bodyFixtures = new Vector<Fixture>(body.getFixtureList());
		for (Fixture fixture : bodyFixtures) {
			if (fixture != createdFixture) {
				fixture.setDensity(0.0f);
				body.destroyFixture(fixture);
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
		setMass(mass); // TODO: Every time there is something to do. Check if needed.
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

	public void setPosition(float x, float y) {
		body.setTransform(x, y, body.getAngle());
	}

	public void setXPosition(float x) {
		body.setTransform(x, body.getPosition().y, body.getAngle());
	}

	public void setYPosition(float y) {
		body.setTransform(body.getPosition().x, y, body.getAngle());
	}

	public void setMass(float mass) {
		this.mass = mass;

		MassData massData = body.getMassData();
		massData.mass = mass;
		body.setMassData(massData);
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
