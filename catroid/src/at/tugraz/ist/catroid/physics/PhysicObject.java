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

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

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
		mass = PhysicSettings.World.DEAULT_MASS;

		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.2f;
		fixtureDef.restitution = 0.0f;

		setType(Type.NONE);
	}

	public void setShape(Shape shape) {
		if (shape == fixtureDef.shape) {
			return;
		}
		fixtureDef.shape = shape;

		List<Fixture> fixturesOld = new ArrayList<Fixture>(body.getFixtureList());

		if (shape != null) {
			body.createFixture(fixtureDef);
		}

		for (Fixture fixture : fixturesOld) {
			body.destroyFixture(fixture);
		}

		setMass(mass);
	}

	public void setType(Type type) {
		if (this.type == type) {
			return;
		}
		this.type = type;

		switch (type) {
			case DYNAMIC:
				body.setType(BodyType.DynamicBody);
				setMass(mass);
				body.setActive(true);
				break;
			case FIXED:
				body.setType(BodyType.KinematicBody);
				body.setActive(true);
				break;
			case NONE:
				body.setType(BodyType.StaticBody);
				body.setActive(false);
				break;
		}
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
		if (mass <= 0.0f) {
			mass = 1.0f;
		}
		this.mass = mass;

		float bodyMass = body.getMass();
		if (bodyMass == 0.0f) {
			return;
		}

		float area = bodyMass / fixtureDef.density;
		float density = mass / area;
		setDensity(density);
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

	public void setAngularVelocity(float radian) {
		body.setAngularVelocity(radian);
	}

	public void setLinearVelocicty(Vector2 velocity) {
		body.setLinearVelocity(velocity);
	}
}
