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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Sprite;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicShapeBuilder {
	static {
		GdxNativesLoader.load();
	}

	private World world;

	public World getWorld() {
		return world;
	}

	private transient Map<Sprite, Body> bodys;
	ArrayList<Body> staticBodyList;

	public PhysicShapeBuilder() {
		world = new World(PhysicWorldSetting.defaultgravity, PhysicWorldSetting.ignoreSleepingObjects);
		staticBodyList = new ArrayList<Body>();
		bodys = new HashMap<Sprite, Body>();
	}

	private Body createBody(Sprite sprite) {

		// What kind of body is it ???
		float w;
		float h;
		float r;
		Body body = null;

		CostumeData costumeData = sprite.costume.getCostumeData();
		int[] resulution = costumeData.getResolution();
		Vector2 pos = new Vector2(sprite.costume.getXPosition(), sprite.costume.getYPosition());
		Vector2 b2pos = PhysicWorldConverter.vectCatToBox2D(pos);
		float rotation = PhysicWorldConverter.angleCatToBox2D(sprite.costume.rotation);

		if (resulution[0] == resulution[1]) {
			r = PhysicWorldConverter.lengthCatToBox2D(resulution[0] / 2.0f);
			body = createCircle(BodyType.DynamicBody, r, b2pos, rotation, 2.0f);

		} else {
			w = PhysicWorldConverter.lengthCatToBox2D(resulution[0]);
			h = PhysicWorldConverter.lengthCatToBox2D(resulution[1]);
			body = createBox(BodyType.DynamicBody, w, h, rotation, b2pos);
		}

		bodys.put(sprite, body);
		return body;
	}

	public Body createCircle(BodyType type, float radius, Vector2 pos, float angle, float density) {
		BodyDef bodydef = new BodyDef();
		bodydef.type = type;
		bodydef.angle = angle;
		bodydef.fixedRotation = false;

		FixtureDef fd = new FixtureDef();
		fd.friction = 0.3f;
		fd.restitution = 0.75f;
		fd.shape = new PolygonShape();
		((PolygonShape) fd.shape).setAsBox(radius, radius, pos, angle);
		fd.density = density;

		bodydef.position.x = pos.x;
		bodydef.position.y = pos.y;

		Body circle = world.createBody(bodydef);
		circle.createFixture(fd);

		return circle;
	}

	public Body createBox(BodyType type, float width, float height, float rotation, Vector2 pos) {
		BodyDef def = new BodyDef();
		def.type = type;
		def.fixedRotation = false;
		def.position.x = pos.x;
		def.position.y = pos.y;
		//def.angle = 0;
		Body box = world.createBody(def);

		FixtureDef fd = new FixtureDef();
		fd.shape = new PolygonShape();
		((PolygonShape) fd.shape).setAsBox(width / 2.0f, height / 2.0f);
		fd.density = 1.0f;
		fd.friction = 0.5f;
		fd.restitution = 0.5f;
		box.createFixture(fd);
		// DEBUG
		//box.setAngularVelocity(1f);

		return box;
	}

	public void createStaticBody(int with, int height, Vector2 pos, float angle_rad) {

		float w = PhysicWorldConverter.lengthCatToBox2D(with);
		float h = PhysicWorldConverter.lengthCatToBox2D(height);
		Vector2 pos_ = PhysicWorldConverter.vectCatToBox2D(pos);
		System.out.println(" # # DEBUG  in :" + pos + " out :" + pos_);
		Body body = createstaticBox(w, h, 0f, pos_);
		//body.setTransform(trans, angle_rad);
		staticBodyList.add(body);
	}

	public void turn(Sprite sprite) {

		Body body = getBody(sprite);
		body.applyAngularImpulse(10.5f);
	}

	public void createSurroundingBox() {
		int frameWidthPixels = PhysicWorldSetting.surroundingBoxFrameWidth;
		createStaticBody(frameWidthPixels * 2, Values.SCREEN_HEIGHT, new Vector2(-Values.SCREEN_WIDTH / 2 - 2
				* frameWidthPixels, 0), 0);
		createStaticBody(frameWidthPixels * 2, Values.SCREEN_HEIGHT, new Vector2(Values.SCREEN_WIDTH / 2 + 2
				* frameWidthPixels, 0), 0);
		createStaticBody(Values.SCREEN_WIDTH, frameWidthPixels * 2, new Vector2(0, -Values.SCREEN_HEIGHT / 2 - 2
				* frameWidthPixels), 0);
		createStaticBody(Values.SCREEN_WIDTH, frameWidthPixels * 2, new Vector2(0, Values.SCREEN_HEIGHT / 2 + 2
				* frameWidthPixels), 0);
	}

	public Body createstaticBox(float width, float height, float density, Vector2 pos) {
		BodyDef def = new BodyDef();
		def.type = BodyType.StaticBody;
		def.fixedRotation = false;
		def.position.x = pos.x;
		def.position.y = pos.y;

		FixtureDef fd = new FixtureDef();
		fd.shape = new PolygonShape();
		((PolygonShape) fd.shape).setAsBox(width, height);

		Body box = world.createBody(def);
		box.createFixture(fd);
		return box;
	}

	public float getAngle(Sprite sprite) {
		Body body = getBody(sprite);
		boolean fr = body.isFixedRotation(); // debug

		return body.getAngle();
	}

	public Vector2 getPosition(Sprite sprite) {
		Body body = getBody(sprite);
		return PhysicWorldConverter.vectBox2DToCat(body.getPosition());
	}

	public boolean setMassData(Sprite sprite, float mass) {
		boolean added = false;
		Body body = bodys.get(sprite);

		if (null == body) {
			body = createBody(sprite);
			added = true;
		}
		MassData mdata = new MassData();
		mdata.mass = mass;
		body.setMassData(mdata);
		return added;
	}

	public void setGravity(Sprite sprite, Vector2 gravity) {
		world.setGravity(PhysicWorldConverter.vectCatToBox2D(gravity));
	}

	public boolean setVelocity(Sprite sprite, Vector2 velocity) {

		boolean added = false;
		Body body = bodys.get(sprite);

		if (null == body) {
			body = createBody(sprite);
			added = true;
		}
		body.applyLinearImpulse(velocity, body.getPosition());
		return added;
	}

	private Body getBody(Sprite sprite) {
		Body body = bodys.get(sprite);
		if (null == body) {
			body = createBody(sprite);
		}
		return body;
	}

	public Collection<Body> getBodies() {
		return bodys.values();
	}

	public void printStaticBodys() {
		if (staticBodyList.size() == 0) {
			System.out.println("NO STATIC BODYS FOUND");
		}
		for (Body body : staticBodyList) {
			for (Fixture fixture : body.getFixtureList()) {
				Shape shape = fixture.getShape();

				for (int i = 0; i < ((PolygonShape) shape).getVertexCount(); i++) {
					Vector2 v = new Vector2();
					((PolygonShape) shape).getVertex(i, v);
					v = body.getWorldPoint(v);

					//v = PhysicWorldConverter.Vector2FromBox2DToCatroid(v);
					//System.out.print(" x:" + v.x + "\ty:" + v.y);
				}

				//System.out.println();

			}
			System.out.println(" x :" + body.getPosition().x * 40 + " y :" + body.getPosition().y * 40);
		}
	}

}
