package at.tugraz.ist.catroid.test.physics;

import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.Assert;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.physics.PhysicShapeBuilder;
import at.tugraz.ist.catroid.physics.PhysicWorldSetting;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicShapeBuilderTest extends AndroidTestCase {
	static {
		GdxNativesLoader.load();
	}
	private World world;
	private PhysicShapeBuilder phyWB;

	@Override
	public void setUp() {
		world = new World(PhysicWorldSetting.defaultgravity, PhysicWorldSetting.ignoreSleepingObjects);
		phyWB = new PhysicShapeBuilder();
	}

	@Override
	public void tearDown() {
		world = null;
		phyWB = null;
	}

	public void testGravityCircle() {
		Vector2 pos = new Vector2(4, 4);
		float angle = 0;
		float density = 0;
		Body body = phyWB.createCircle(BodyType.DynamicBody, 1, pos, angle, density);

		Assert.assertTrue(1 == world.getBodyCount());
		Assert.assertEquals(pos.y, body.getPosition().y);
		Assert.assertEquals(pos.x, body.getPosition().x);
		debug("" + body.getPosition());
		float y = body.getPosition().y;
		world.step(1.0f / 30f, PhysicWorldSetting.velocityIterations, PhysicWorldSetting.positionIterations);
		Assert.assertFalse(y == body.getPosition().y);
		y = body.getPosition().y;
		world.step(1.0f / 30f, PhysicWorldSetting.velocityIterations, PhysicWorldSetting.positionIterations);
		Assert.assertFalse(y == body.getPosition().y);
		y = body.getPosition().y;
		world.step(1.0f / 30f, PhysicWorldSetting.velocityIterations, PhysicWorldSetting.positionIterations);
		Assert.assertFalse(y == body.getPosition().y);
		y = body.getPosition().y;
	}

	public void testStaticBoxPos() {
		Vector2 pos = new Vector2(4, 4);
		float w = 1.5f;
		float h = 2.5f;
		Body body = phyWB.createstaticBox(w, h, 1, pos);
		Assert.assertTrue(1 == world.getBodyCount());
		float y = body.getPosition().y;
		printBodyInfo(body);
		world.step(1.0f / 30f, PhysicWorldSetting.velocityIterations, PhysicWorldSetting.positionIterations);
		Assert.assertTrue(y == body.getPosition().y);
		printBodyInfo(body);
		y = body.getPosition().y;
		world.step(1.0f / 30f, PhysicWorldSetting.velocityIterations, PhysicWorldSetting.positionIterations);
		Assert.assertTrue(y == body.getPosition().y);
		printBodyInfo(body);
		y = body.getPosition().y;
		world.step(1.0f / 30f, PhysicWorldSetting.velocityIterations, PhysicWorldSetting.positionIterations);
		Assert.assertTrue(y == body.getPosition().y);
		printBodyInfo(body);
		y = body.getPosition().y;
	}

	public void testStaticBoxDim() {
		Vector2 pos = new Vector2(4, 4);
		float w = 1.5f;
		float h = 2.5f;
		Body body = phyWB.createstaticBox(w, h, 1, pos);
		Assert.assertTrue(1 == world.getBodyCount());
		ArrayList<Fixture> fixtureList = body.getFixtureList();
		Iterator<Fixture> it = fixtureList.iterator();
		while (it.hasNext()) {
			Shape shape = it.next().getShape();
			debug("" + shape.getType());
			PolygonShape poly = ((PolygonShape) shape);
			debug("VertexCount : " + poly.getVertexCount());
			for (int i = 0; i < poly.getVertexCount(); i++) {
				Vector2 vertex = new Vector2();
				poly.getVertex(i, vertex);
				Vector2 world = body.getWorldPoint(vertex);
				Assert.assertEquals(world.x, vertex.x + pos.x);
				Assert.assertEquals(world.y, vertex.y + pos.y);
				// debug("Vertex  x: " + world.x + "  y: " + world.y);
			}
		}
	}

	private void printBodyInfo(Body body) {
		Vector2 pos = body.getPosition();
		debug("# POS #  x: " + pos.x + "  y: " + pos.y);
	}

	private void debug(String str) {
		System.out.println("DebugPSB " + str);
	}

}
