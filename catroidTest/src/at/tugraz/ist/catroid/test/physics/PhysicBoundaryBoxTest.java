package at.tugraz.ist.catroid.test.physics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.physics.PhysicBoundaryBox;
import at.tugraz.ist.catroid.physics.PhysicSettings;
import at.tugraz.ist.catroid.physics.PhysicWorldConverter;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicBoundaryBoxTest extends AndroidTestCase {
	static {
		GdxNativesLoader.load();
	}

	private World world;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		world = new World(PhysicSettings.World.DEFAULT_GRAVITY, PhysicSettings.World.IGNORE_SLEEPING_OBJECTS);
	}

	public void testSettings() {
		assertEquals(0, world.getBodyCount());
		new PhysicBoundaryBox(world).create();
		assertEquals(4, world.getBodyCount());

		Body body;
		Iterator<Body> bodyIterator = world.getBodies();
		while (bodyIterator.hasNext()) {
			body = bodyIterator.next();
			assertEquals(BodyType.StaticBody, body.getType());

			List<Fixture> fixtures = body.getFixtureList();
			assertEquals(1, fixtures.size());
			for (Fixture fixture : fixtures) {
				Filter filter = fixture.getFilterData();
				assertEquals(PhysicSettings.Object.COLLISION_MASK, filter.maskBits);

				// Fails if you forgot to set PhysicSettings.DEBUGFLAG to true.
				//				assertEquals(PhysicSettings.World.BoundaryBox.COLLISION_MASK, filter.categoryBits);

				assertEquals(Shape.Type.Polygon, fixture.getType());
				PolygonShape shape = (PolygonShape) fixture.getShape();
				assertEquals(4, shape.getVertexCount());
			}
		}
	}

	public void testPositionAndSize() {
		Values.SCREEN_WIDTH = 800;
		Values.SCREEN_HEIGHT = 640;

		Set<Float> values = new HashSet<Float>();
		values.add(-405.0f);
		values.add(-400.0f);
		values.add(-325.0f);
		values.add(-320.0f);

		values.add(320.0f);
		values.add(325.0f);
		values.add(400.0f);
		values.add(405.0f);

		assertEquals(0, world.getBodyCount());
		new PhysicBoundaryBox(world).create();
		assertEquals(4, world.getBodyCount());

		Body body;
		Iterator<Body> bodyIterator = world.getBodies();

		while (bodyIterator.hasNext()) {
			body = bodyIterator.next();
			List<Fixture> fixtures = body.getFixtureList();
			assertEquals(1, fixtures.size());
			for (Fixture fixture : fixtures) {
				PolygonShape shape = (PolygonShape) fixture.getShape();
				assertEquals(4, shape.getVertexCount());

				Vector2 vertex = new Vector2();
				for (int index = 0; index < shape.getVertexCount(); index++) {
					shape.getVertex(index, vertex);
					vertex = PhysicWorldConverter.vecBox2dToCat(vertex);

					assertTrue(values.contains(vertex.x));
					assertTrue(values.contains(vertex.y));
				}
			}
		}

		Float[] array = new Float[values.size()];
		array = values.toArray(array);
		System.out.println();
	}
}
