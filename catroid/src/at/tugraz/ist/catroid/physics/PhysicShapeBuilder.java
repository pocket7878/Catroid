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

import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Sprite;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * @author Philipp
 * 
 */
public class PhysicShapeBuilder {

	private World world;
	ArrayList<Body> staticBodyList;

	public PhysicShapeBuilder(World w) {
		world = w;
		staticBodyList = new ArrayList<Body>();
	}

	public void createSurroundingBox() {
		int frameWidthPixels = PhysicWorldSetting.surroundingBoxFrameWidth;
		//createStaticBody(frameWidthPixels * 2, Values.SCREEN_HEIGHT, new Vector2(-Values.SCREEN_WIDTH / 2
		//	+ frameWidthPixels, 0), 0);
		//createStaticBody(frameWidthPixels * 2, Values.SCREEN_HEIGHT, new Vector2(Values.SCREEN_WIDTH / 2
		//- frameWidthPixels, 0), 0);
		createStaticBody(Values.SCREEN_WIDTH, frameWidthPixels * 2, new Vector2(0, -Values.SCREEN_HEIGHT / 2
				+ frameWidthPixels), 0);
		//createStaticBody(Values.SCREEN_WIDTH, frameWidthPixels * 2, new Vector2(0, Values.SCREEN_HEIGHT / 2
		//	- frameWidthPixels), 0);
	}

	public Body createBody(Sprite sprite) {

		// What kind of body is it ???
		CostumeData costumeData = sprite.costume.getCostumeData();
		int[] resulution = costumeData.getResolution();

		float r = (resulution[0] > resulution[1]) ? resulution[0] / 2f : resulution[1] / 2f;
		Vector2 pos = new Vector2(sprite.costume.getXPosition(), sprite.costume.getYPosition());
		float b2r = PhysicWorldConverter.LengthFromCatroidToBox2D(r);
		Vector2 b2pos = PhysicWorldConverter.Vector2FromCatroidToBox2D(pos);

		return createDynamicCircle(b2r, b2pos, sprite.costume.rotation);
	}

	private Body createDynamicCircle(float radius, Vector2 pos, float angle_rad) {
		Body body = createCircle(BodyType.DynamicBody, radius, pos, angle_rad, 2.0f);
		//Body body = createCircle(BodyType.DynamicBody, radius, 2.0f);
		//body.setTransform(pos, angle_rad);
		return body;
	}

	public Body createCircle(BodyType type, float radius, Vector2 pos, float angle, float density) {
		BodyDef bodydef = new BodyDef();
		bodydef.type = type;

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

		return circle;
	}

	// #############################################################################

	public void createStaticBody(int with, int height, Vector2 pos, float angle_rad) {

		float w = PhysicWorldConverter.LengthFromCatroidToBox2D(with);
		float h = PhysicWorldConverter.LengthFromCatroidToBox2D(height);
		Vector2 pos_ = PhysicWorldConverter.Vector2FromCatroidToBox2D(pos);
		System.out.println(" # # DEBUG  in :" + pos + " out :" + pos_);
		Body body = createstaticBox(w, h, 0f, pos_);
		//body.setTransform(trans, angle_rad);
		staticBodyList.add(body);
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
