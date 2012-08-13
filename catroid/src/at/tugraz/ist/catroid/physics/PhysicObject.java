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
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * @author robert
 * 
 */
public class PhysicObject {
	private final Body body;
	private final FixtureDef fixtureDef = new FixtureDef();

	public PhysicObject(Body body) {
		this.body = body;
	}

	public Body getBody() {
		return body;
	}

	public void setShape(Shape shape) {
		// TODO: This code is cursed, I tell you!
		fixtureDef.shape = shape;
		for (Fixture fixture : body.getFixtureList()) {
			body.destroyFixture(fixture);
		}
		body.createFixture(fixtureDef);
	}

	public void setType(BodyDef.BodyType type) {
		body.setType(type);
	}

	public void setAngle(float angle) {
		body.setTransform(body.getPosition(), angle);
	}

	public void setPosition(Vector2 position) {
		body.setTransform(position, body.getAngle());
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
}
