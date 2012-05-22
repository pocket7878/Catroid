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

import at.tugraz.ist.catroid.common.CostumeData;
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
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicWorld implements Serializable {
	private static final long serialVersionUID = -9103964560286141267L;
	static {
		GdxNativesLoader.load();
	}

	private transient World world;
	private transient Map<Sprite, Body> bodys;

	public PhysicWorld() {
		world = new World(PhysicWorldSetting.defaultgravity, PhysicWorldSetting.ignoreSleepingObjects);
		bodys = new HashMap<Sprite, Body>();
		CreateBox();
	}

	public void step(float deltaTime) {
		world.step(deltaTime, PhysicWorldSetting.velocityIterations, PhysicWorldSetting.positionIterations);
		refreshCoordsOfSprites();
	}

	private void refreshCoordsOfSprites() {
		for (Sprite sprite : bodys.keySet()) {

			Body body = bodys.get(sprite);
			Vector2 catrobatCoords = PhysicWorldConverter.CoordsFromBox2DToCatroid(body.getPosition().x,
					body.getPosition().y);
			sprite.costume.aquireXYWidthHeightLock(); // Sinn ?
			sprite.costume.setXYPosition(catrobatCoords.x, catrobatCoords.y);
			sprite.costume.releaseXYWidthHeightLock();
			System.out.println("x:" + sprite.costume.x + "  y:" + sprite.costume.y);
		}
	}

	// #############################################################################

	/**
	 * @param sprite
	 * @param gravity
	 */
	public void setGravity(Sprite sprite, Vector2 gravity) {
		world.setGravity(PhysicWorldConverter.VectorFromCatroidToBox2D(gravity));
	}

	/**
	 * @param sprite
	 * @param velocity
	 */
	public void setVelocity(Sprite sprite, Vector2 velocity) {
		Body body = getBody(sprite);
		body.applyLinearImpulse(PhysicWorldConverter.Vector2FromCatroidToBox2D(velocity), body.getPosition());
	}

	/**
	 * @param sprite
	 * @param mass
	 */
	public void setMass(Sprite sprite, float mass) {
		Body body = getBody(sprite);
		MassData mdata = new MassData();
		mdata.mass = mass;
		body.setMassData(mdata);
	}

	private Body getBody(Sprite sprite) {
		Body body = bodys.get(sprite);
		if (null == body) {
			body = createBody(sprite);
			bodys.put(sprite, body);
		}
		return body;
	}

	private Body createBody(Sprite sprite) {
		CostumeData costumeData = sprite.costume.getCostumeData();
		int[] resulution = costumeData.getResolution();
		//		float r = (sprite.costume.getHeight() > sprite.costume.getWidth()) ? sprite.costume.getHeight() / 2
		//				: sprite.costume.getWidth() / 2;
		float r = (resulution[0] > resulution[1]) ? resulution[0] / 2f : resulution[1] / 2f;
		System.out.println("r:" + r);
		Vector2 pos = new Vector2(sprite.costume.getXPosition(), sprite.costume.getYPosition());
		return createDynamicCircle(r, pos, sprite.costume.rotation);
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