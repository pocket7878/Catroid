package at.tugraz.ist.catroid.test.physics;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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

	public void testProperties() {
		assertEquals(0, world.getBodyCount());
		new PhysicBoundaryBox(world).create();
		assertEquals(4, world.getBodyCount());

		Iterator<Body> bodyIterator = world.getBodies();
		while (bodyIterator.hasNext()) {
			Body body = bodyIterator.next();
			assertEquals(BodyType.StaticBody, body.getType());
			assertFalse(body.isSleepingAllowed());

			List<Fixture> fixtures = body.getFixtureList();
			assertEquals(1, fixtures.size());
			for (Fixture fixture : fixtures) {
				Filter filter = fixture.getFilterData();
				assertEquals(PhysicSettings.Object.COLLISION_MASK, filter.maskBits);

				// Fails if you forgot to set PhysicSettings.DEBUGFLAG to true. Only for release issues.
				//				assertEquals(PhysicSettings.World.BoundaryBox.COLLISION_MASK, filter.categoryBits);
			}
		}
	}

	public void testPositionAndSize() {
		Values.SCREEN_WIDTH = 800;
		Values.SCREEN_HEIGHT = 640;

		float halfWidth = Values.SCREEN_WIDTH / 2;
		float halfHeight = Values.SCREEN_HEIGHT / 2;
		float frameSize = PhysicSettings.World.BoundaryBox.FRAME_SIZE;

		List<Float> boundaryXValues = Arrays.asList(new Float[] { -(halfWidth + frameSize), -halfWidth, halfWidth,
				halfWidth + frameSize });
		List<Float> boundaryYValues = Arrays.asList(new Float[] { -(halfHeight + frameSize), -halfHeight, halfHeight,
				halfHeight + frameSize });

		// TODO: Implement these values to check correctness:
		//		Rect upperBoundaryBox = new Rect(-400, 320, 400, 325);
		//		Rect lowerBoundaryBox = new Rect(-400, -320, 400, -325);
		//
		//		Rect leftBoundaryBox = new Rect(-405, -320, -400, 325);
		//		Rect rightBoundaryBox = new Rect(400, -320, 405, 325);

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
				assertEquals(Shape.Type.Polygon, fixture.getType());
				PolygonShape shape = (PolygonShape) fixture.getShape();
				assertEquals(4, shape.getVertexCount());

				Vector2 vertex = new Vector2();
				for (int index = 0; index < shape.getVertexCount(); index++) {
					shape.getVertex(index, vertex);
					vertex = PhysicWorldConverter.vecBox2dToCat(vertex);

					assertTrue(boundaryXValues.contains(vertex.x));
					assertTrue(boundaryYValues.contains(vertex.y));
				}
			}
		}
	}
}
