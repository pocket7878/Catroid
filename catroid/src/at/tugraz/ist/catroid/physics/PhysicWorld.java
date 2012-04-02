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
import java.util.Iterator;
import java.util.Map;

import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Sprite;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * @author Philipp
 * 
 */
public class PhysicWorld {

	static {
		System.loadLibrary("gdx");
	}

	static final float timeStep = 1.0f / 40.0f;
	static final int velocityIterations = 5;
	static final int positionIterations = 5;

	private World world;

	private Map<Sprite, Body> bodys;
	private Map<Sprite, DummyBody> dummys;

	public PhysicWorld() {
		Vector2 gravity = new Vector2(0, -10);
		boolean ignoreSleeping = false;

		world = new World(gravity, ignoreSleeping);

		bodys = new HashMap<Sprite, Body>();
		dummys = new HashMap<Sprite, DummyBody>();

		CreateBox();
	}

	private void CreateBox() {
		createStaticBody(800, 10, new Vector2(-Values.SCREEN_WIDTH / 2 + 5, 0), 0);
		createStaticBody(800, 10, new Vector2(Values.SCREEN_WIDTH / 2 - 5, 0), 0);
		createStaticBody(10, 460, new Vector2(0, -Values.SCREEN_HEIGHT / 2 + 5), 0);
		createStaticBody(10, 460, new Vector2(0, Values.SCREEN_HEIGHT / 2 - 5), 0);
	}

	public void createStaticBody(int with, int height, Vector2 pos, float angle_rad) {
		Body body = createstaticBox(with / 40, height / 40, 0f);
		Vector2 trans = new Vector2(pos.x / 40, pos.y / 40);
		body.setTransform(trans, angle_rad);
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

	public void step() {
		world.step(timeStep, velocityIterations, positionIterations);
		refreshCooordsOfSprites();
	}

	private void refreshCooordsOfSprites() {

		for (Sprite sprite : bodys.keySet()) {

			Body tempbody = bodys.get(sprite);

			//world.getBodies();

			Iterator<Body> itr = world.getBodies();

			Vector2 catroidCoords = null;// = tempbody.getPosition();
			while (itr.hasNext()) {

				Object element = itr.next();
				Body b = (Body) element;
				if (BodyType.DynamicBody == b.getType()) {
					catroidCoords = Convert.CoordsFromBox2DToCatroid(b.getPosition().x, b.getPosition().y);
				}

			}

			if (catroidCoords != null) {
				sprite.costume.aquireXYWidthHeightLock();
				sprite.costume.x = (int) catroidCoords.x;
				sprite.costume.y = (int) catroidCoords.y;
				sprite.costume.releaseXYWidthHeightLock();
			}
		}
	}

	private boolean spriteIsValide(Sprite sprite) {
		boolean isvalid = true;
		// isPhysical Object ?
		// if(sprite.isPhysicalObject)

		// has a Costume ?
		if (null == sprite.costume) {
			isvalid = false;
		}

		return isvalid;
	}

	// TODO
	private void setBodysGravity(Body body) {
	}

	// TODO
	private void setBodysVelocity(Body body) {
	};

	public void setMass(Sprite sprite, float mass) {

		if (bodys.containsKey(sprite)) {
			MassData data = new MassData();
			data.mass = mass;
			bodys.get(sprite).setMassData(data);
		} else if (dummys.containsKey(sprite)) {
			dummys.get(sprite).mass = mass;
			if (spriteIsValide(sprite)) {
				admitDummytoPhysicsWorld(sprite, dummys.get(sprite));
			}
		} else {
			DummyBody new_dummybody = new DummyBody();
			new_dummybody.mass = mass;
			dummys.put(sprite, new_dummybody);
		}
	}

	public void setGravity(Sprite sprite, Vector2 gravity) {

		//Vector2 screen = new Vector2(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
		//Vector2 gravity = new Vector2(x, y);
		float with = sprite.costume.width;
		float height = sprite.costume.height;
		Vector2 pos = new Vector2(sprite.costume.x + with / 2f, sprite.costume.y + height / 2f);

		Body body = createDynamicCircle(with / 2, pos, 0);
		bodys.put(sprite, body);

		//world

		//		if (bodys.containsKey(sprite)) {
		//			setBodysGravity(bodys.get(sprite));
		//		} else if (dummys.containsKey(sprite)) {
		//			dummys.get(sprite).gravity = new Vector2(x, y);
		//			if (spriteIsValide(sprite)) {
		//				admitDummytoPhysicsWorld(sprite, dummys.get(sprite));
		//			}
		//		} else {
		//			DummyBody new_dummybody = new DummyBody();
		//			new_dummybody.gravity = new Vector2(x, y);
		//			dummys.put(sprite, new_dummybody);
		//		}

	}

	public Body createDynamicCircle(float radius, Vector2 pos, float angle_rad) {
		float r = Convert.LengthFromCatroidToBox2D(radius);
		Body body = createCircle(BodyType.DynamicBody, r, 5.0f);
		Vector2 p = Convert.Vector2FromCatroidToBox2D(pos);
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
		//fd.friction = 0.3f;
		fd.restitution = 0.75f;
		fd.shape = poly;
		fd.density = density;
		circle.createFixture(fd);
		poly.dispose();

		return circle;
	}

	public void setVelocity(Sprite sprite, Vector2 velocity) {

		// only for testing

		//		if (bodys.containsKey(sprite)) {
		//			setBodysVelocity(bodys.get(sprite));
		//		} else if (dummys.containsKey(sprite)) {
		//			dummys.get(sprite).velocity = new Vector2(x, y);
		//			if (spriteIsValide(sprite)) {
		//				admitDummytoPhysicsWorld(sprite, dummys.get(sprite));
		//			}
		//		} else {
		//			DummyBody new_dummybody = new DummyBody();
		//			new_dummybody.velocity = new Vector2(x, y);
		//			dummys.put(sprite, new_dummybody);
		//		}
		//
		//		sprite.costume.x += 20;
		//		sprite.costume.y += 20;

	}

	private void admitDummytoPhysicsWorld(Sprite sprite, DummyBody dummy) {

	}

	public void addNewBodyToWorld() {

	}

	class DummyBody {
		float mass;
		Vector2 gravity;
		Vector2 velocity;

		public DummyBody() {
		}
	}

	private static class Convert {

		// Ratio of pixels to meters
		private static int RATIO = 40;

		public static Vector2 CoordsFromBox2DToCatroid(float x, float y) {
			Vector2 coords = new Vector2(x * 40, y * 40);

			return coords;
		}

		public static float LengthFromCatroidToBox2D(float x) {
			return x / RATIO;
		}

		public static Vector2 Vector2FromCatroidToBox2D(Vector2 x) {
			return new Vector2(x.x / RATIO, x.y / RATIO);
		}

		public static Vector2 Vector2FromBox2DToCatroid(Vector2 x) {
			return new Vector2(x.x * RATIO, x.y * RATIO);
		}
	}

}