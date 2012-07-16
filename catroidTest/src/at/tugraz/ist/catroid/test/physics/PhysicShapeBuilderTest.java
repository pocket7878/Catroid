package at.tugraz.ist.catroid.test.physics;

import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.Assert;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.physics.PhysicBodyBuilder;
import at.tugraz.ist.catroid.physics.PhysicSettings;

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
	private PhysicBodyBuilder bodyBuilder;
	float timestep = 1.0f / 30.0f;

	@Override
	public void setUp() {
		world = new World(PhysicSettings.World.DEFAULT_GRAVITY, PhysicSettings.World.IGNORE_SLEEPING_OBJECTS);
		bodyBuilder = new PhysicBodyBuilder(world);
	}

	@Override
	public void tearDown() {
		world = null;
		bodyBuilder = null;
	}

	public void testGravityCircle() {
		Body body = bodyBuilder.createCircle(BodyType.DynamicBody, 1.f);
		Vector2 position = new Vector2(4.0f, 4.0f);
		body.setTransform(position, 0);

		Assert.assertTrue(1 == world.getBodyCount());
		Assert.assertEquals(position.y, body.getPosition().y);
		Assert.assertEquals(position.x, body.getPosition().x);
		debug("" + body.getPosition());
		float y = body.getPosition().y;
		world.step(timestep, PhysicSettings.World.VELOCITY_ITERATIONS, PhysicSettings.World.POSITION_ITERATIONS);
		Assert.assertFalse(y == body.getPosition().y);
		y = body.getPosition().y;
		world.step(timestep, PhysicSettings.World.VELOCITY_ITERATIONS, PhysicSettings.World.POSITION_ITERATIONS);
		Assert.assertFalse(y == body.getPosition().y);
		y = body.getPosition().y;
		world.step(timestep, PhysicSettings.World.VELOCITY_ITERATIONS, PhysicSettings.World.POSITION_ITERATIONS);
		Assert.assertFalse(y == body.getPosition().y);
		y = body.getPosition().y;
	}

	public void testStaticBoxPos() {
		Body body = bodyBuilder.createBox(BodyType.StaticBody, 1.0f, 1.0f);
		Vector2 position = new Vector2(4.0f, 4.0f);
		body.setTransform(position, 0);

		Assert.assertTrue(1 == world.getBodyCount());
		float y = body.getPosition().y;
		printBodyInfo(body);
		world.step(timestep, PhysicSettings.World.VELOCITY_ITERATIONS, PhysicSettings.World.POSITION_ITERATIONS);
		Assert.assertTrue(y == body.getPosition().y);
		printBodyInfo(body);
		y = body.getPosition().y;
		world.step(timestep, PhysicSettings.World.VELOCITY_ITERATIONS, PhysicSettings.World.POSITION_ITERATIONS);
		Assert.assertTrue(y == body.getPosition().y);
		printBodyInfo(body);
		y = body.getPosition().y;
		world.step(timestep, PhysicSettings.World.VELOCITY_ITERATIONS, PhysicSettings.World.POSITION_ITERATIONS);
		Assert.assertTrue(y == body.getPosition().y);
		printBodyInfo(body);
		y = body.getPosition().y;
	}

	public void testStaticBoxDim() {
		Body body = bodyBuilder.createBox(BodyType.StaticBody, 1.0f, 1.0f);
		Vector2 position = new Vector2(4.0f, 4.0f);
		body.setTransform(position, 0);

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
				Assert.assertEquals(world.x, vertex.x + position.x);
				Assert.assertEquals(world.y, vertex.y + position.y);
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
