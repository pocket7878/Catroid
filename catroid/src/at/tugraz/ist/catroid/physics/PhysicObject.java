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

	private final Body body;
	private final FixtureDef fixtureDef = new FixtureDef();
	private Shape[] shapes;
	private Type type;
	private float mass;

	public PhysicObject(Body body) {
		this.body = body;
		mass = PhysicSettings.Object.DEFAULT_MASS;

		fixtureDef.density = PhysicSettings.Object.DEFAULT_DENSITY;
		fixtureDef.friction = PhysicSettings.Object.DEFAULT_FRICTION;
		fixtureDef.restitution = PhysicSettings.Object.DEFAULT_RESTITUTION;

		setType(Type.NONE);
	}

	public void setShape(Shape[] shapes) {
		if (this.shapes == shapes) {
			return;
		}
		this.shapes = shapes;

		List<Fixture> fixturesOld = new ArrayList<Fixture>(body.getFixtureList());

		if (shapes != null) {
			for (Shape tempShape : shapes) {
				fixtureDef.shape = tempShape;
				body.createFixture(fixtureDef);
			}
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
				body.setType(BodyType.KinematicBody);
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

	public void setXYPosition(float x, float y) {
		body.setTransform(x, y, body.getAngle());
	}

	public void setXYPosition(Vector2 vecCatToBox2d) {
		body.setTransform(vecCatToBox2d, body.getAngle());
	}

	public void setMass(float mass) {
		if (mass < PhysicSettings.Object.MIN_MASS) {
			mass = PhysicSettings.Object.MIN_MASS;
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

	private void setDensity(float density) {
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

	public void setBounceFactor(float bounceFactor) {
		fixtureDef.restitution = bounceFactor;
		for (Fixture fixture : body.getFixtureList()) {
			fixture.setRestitution(bounceFactor);
		}
	}

	public void setRotationSpeed(float radian) {
		body.setAngularVelocity(radian);
	}

	public void setVelocity(Vector2 velocity) {
		body.setLinearVelocity(velocity);
	}

}
