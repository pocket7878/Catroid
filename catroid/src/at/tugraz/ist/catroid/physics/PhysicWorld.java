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

import java.util.HashMap;
import java.util.Map;

import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Sprite;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicWorld {
	static {
		GdxNativesLoader.load();
	}

	static final float timeStep = 1.0f / 60.0f;
	static final int velocityIterations = 5;
	static final int positionIterations = 5;
	static final int defaultmass = 10;
	static final Vector2 defaultgravity = new Vector2(0, -10);
	static final boolean ignoreSleepingObjects = false;

	private World world;
	private Map<Sprite, Body> bodys;

	public PhysicWorld() {
		world = new World(defaultgravity, ignoreSleepingObjects);
		world = null;
		bodys = new HashMap<Sprite, Body>();
	}

	public void step() {
		world.step(timeStep, velocityIterations, positionIterations);
		refreshCooordsOfSprites();
	}

	private void refreshCooordsOfSprites() {
		for (Sprite sprite : bodys.keySet()) {

			Body body = bodys.get(sprite);
			Vector2 catroidCoords = PhysicWorldConverter.CoordsFromBox2DToCatroid(body.getPosition().x,
					body.getPosition().y);
			sprite.costume.aquireXYWidthHeightLock();
			sprite.costume.x = (int) catroidCoords.x;
			sprite.costume.y = (int) catroidCoords.y;
			sprite.costume.releaseXYWidthHeightLock();
		}
	}

	// #############################################################################

	/**
	 * @param sprite
	 * @param gravity
	 */
	public void setGravity(Sprite sprite, Vector2 gravity) {
		Body body;
		if (bodys.containsKey(sprite)) {
			world.setGravity(PhysicWorldConverter.VetorFromCatroidToBox2D(gravity));
		} else {
			world.setGravity(PhysicWorldConverter.VetorFromCatroidToBox2D(gravity));
		}
	}

	/**
	 * @param sprite
	 * @param velocity
	 */
	public void setVelocity(Sprite sprite, Vector2 velocity) {
		Body body;
		if (bodys.containsKey(sprite)) {
			Body b = bodys.get(sprite);
			if (BodyType.DynamicBody == b.getType()) {
				b.applyLinearImpulse(velocity, b.getPosition());
			}
		} else {
			// add new Body

		}
	}

	/**
	 * @param sprite
	 * @param mass
	 */
	public void setMass(Sprite sprite, float mass) {
		Body body;
		if (bodys.containsKey(sprite)) {

		} else {

		}

	}

	// #############################################################################

	private Body createDynamicCircle(float radius, Vector2 pos, float angle_rad) {
		float r = PhysicWorldConverter.LengthFromCatroidToBox2D(radius);
		Vector2 p = PhysicWorldConverter.Vector2FromCatroidToBox2D(pos);
		Body body = createCircle(BodyType.DynamicBody, r, 2.0f);
		body.setTransform(p, angle_rad);
		return body;
	}

	private Body createCircle(BodyType type, float radius, float density) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body circle = world.createBody(def);
		CircleShape poly = new CircleShape();
		poly.setRadius(radius);
		FixtureDef fd = new FixtureDef();
		fd.friction = 0.3f;
		fd.restitution = 0.75f;
		fd.shape = poly;
		fd.density = density;
		circle.createFixture(fd);
		poly.dispose();

		return circle;
	}

	private Body createstaticBox(float width, float height, float density) {
		BodyDef def = new BodyDef();
		def.type = BodyType.StaticBody;
		def.fixedRotation = true;
		Body box = world.createBody(def);

		PolygonShape poly = new PolygonShape();
		poly.setAsBox(width, height);
		FixtureDef fd = new FixtureDef();
		fd.shape = poly;
		box.createFixture(fd);
		poly.dispose();
		return box;
	}

	public void createStaticBody(int with, int height, Vector2 pos, float angle_rad) {
		Body body = createstaticBox(with / 40, height / 40, 0f);
		Vector2 trans = new Vector2(pos.x / 40, pos.y / 40);
		body.setTransform(trans, angle_rad);
	}

	private void CreateBox() {
		createStaticBody(10, 800, new Vector2(-Values.SCREEN_WIDTH / 2 + 5, 0), 0);
		createStaticBody(10, 800, new Vector2(Values.SCREEN_WIDTH / 2 - 5, 0), 0);
		createStaticBody(460, 10, new Vector2(0, -Values.SCREEN_HEIGHT / 2 + 5), 0);
		createStaticBody(460, 10, new Vector2(0, Values.SCREEN_HEIGHT / 2 - 5), 0);
	}
}