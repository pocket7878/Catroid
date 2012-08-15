package at.tugraz.ist.catroid.test.physics;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.physics.PhysicObject;
import at.tugraz.ist.catroid.physics.PhysicObjectMap;
import at.tugraz.ist.catroid.physics.PhysicSettings;
import at.tugraz.ist.catroid.physics.PhysicWorldConverter;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicObjectTest extends AndroidTestCase {
	static {
		GdxNativesLoader.load();
	}

	private abstract class FixtureTemplate {
		protected final PhysicObject physicObject;
		protected final float[] values;

		public FixtureTemplate(PhysicObject physicObject, float[] values) {
			this.physicObject = physicObject;
			this.values = values;
		}

		public void test() {
			assertTrue(values.length > 0);
			assertFalse(physicObject.getBody().getFixtureList().isEmpty());

			for (float value : values) {
				setValue(value);
				for (Fixture fixture : physicObject.getBody().getFixtureList()) {
					assertEquals(value, getValue(fixture));
				}
			}
		}

		protected abstract float getValue(Fixture fixture);

		protected abstract void setValue(float value);
	}

	private PhysicObjectMap objects;

	@Override
	protected void setUp() throws Exception {
		objects = new PhysicObjectMap(new World(PhysicSettings.World.DEFAULT_GRAVITY,
				PhysicSettings.World.IGNORE_SLEEPING_OBJECTS));
	}

	@Override
	protected void tearDown() throws Exception {
	}

	protected PhysicObject createPhysicObject() {
		PhysicObject physicObject = objects.get(new Sprite("TestSprite"));
		Shape shape = new PolygonShape();
		physicObject.setShape(shape);
		return physicObject;
	}

	public void testNullBody() {
		PhysicObject objectWithNullBody = new PhysicObject(null);

		assertNull(objectWithNullBody.getBody());

		try {
			objectWithNullBody.setAngle(0.0f);
			assertTrue(false);
		} catch (NullPointerException exception) {
			assertTrue(true);
		}

		try {
			objectWithNullBody.setDensity(0.0f);
			assertTrue(false);
		} catch (NullPointerException exception) {
			assertTrue(true);
		}

		try {
			objectWithNullBody.setFriction(0.0f);
			assertTrue(false);
		} catch (NullPointerException exception) {
			assertTrue(true);
		}

		try {
			objectWithNullBody.setMass(0.0f);
			assertTrue(false);
		} catch (NullPointerException exception) {
			assertTrue(true);
		}

		try {
			objectWithNullBody.setPosition(new Vector2());
			assertTrue(false);
		} catch (NullPointerException exception) {
			assertTrue(true);
		}

		try {
			objectWithNullBody.setRestitution(0.0f);
			assertTrue(false);
		} catch (NullPointerException exception) {
			assertTrue(true);
		}

		try {
			objectWithNullBody.setShape(new PolygonShape());
			assertTrue(false);
		} catch (NullPointerException exception) {
			assertTrue(true);
		}

		//		try {
		//			objectWithNullBody.setType(BodyType.StaticBody);
		//			assertTrue(false);
		//		} catch (NullPointerException exception) {
		//			assertTrue(true);
		//		}
	}

	public void testAngle() {
		PhysicObject physicObject = createPhysicObject();
		assertEquals(0.0f, physicObject.getBody().getAngle());

		float[] degrees = { 45.0f, 66.66f, -90.0f, 500.0f };

		for (float angle : degrees) {
			float radian = PhysicWorldConverter.angleCatToBox2d(angle);
			physicObject.setAngle(radian);
			assertEquals(radian, physicObject.getBody().getAngle());
		}
	}

	public void testPosition() {
		PhysicObject physicObject = createPhysicObject();
		assertEquals(new Vector2(), physicObject.getBody().getPosition());

		Vector2[] positions = { new Vector2(12.34f, 56.78f), new Vector2(-87.65f, -43.21f) };
		for (Vector2 position : positions) {
			physicObject.setPosition(position);
			assertEquals(position, physicObject.getBody().getPosition());
		}
	}

	public void testAngleAndPosition() {
		PhysicObject physicObject = createPhysicObject();
		assertEquals(0.0f, physicObject.getBody().getAngle());
		assertEquals(new Vector2(), physicObject.getBody().getPosition());

		float angle = PhysicWorldConverter.angleCatToBox2d(13.56f);
		Vector2 position = new Vector2(12.34f, 56.78f);
		physicObject.setAngle(angle);
		physicObject.setPosition(position);

		assertEquals(angle, physicObject.getBody().getAngle());
		assertEquals(position, physicObject.getBody().getPosition());
	}

	public void testDensity() {
		PhysicObject physicObject = createPhysicObject();
		float[] densities = { 0.123f, -0.765f, 24.32f };

		FixtureTemplate densityTemplate = new FixtureTemplate(physicObject, densities) {
			@Override
			protected void setValue(float value) {
				physicObject.setDensity(value);
			}

			@Override
			protected float getValue(Fixture fixture) {
				return fixture.getDensity();
			}
		};
		densityTemplate.test();
	}

	public void testFriction() {
		PhysicObject physicObject = createPhysicObject();
		float[] friction = { 0.123f, -0.765f, 24.32f };

		FixtureTemplate frictionTemplate = new FixtureTemplate(physicObject, friction) {
			@Override
			protected void setValue(float value) {
				physicObject.setFriction(value);
			}

			@Override
			protected float getValue(Fixture fixture) {
				return fixture.getFriction();
			}
		};
		frictionTemplate.test();
	}

	public void testRestitution() {
		PhysicObject physicObject = createPhysicObject();
		float[] restitution = { 0.123f, -0.765f, 24.32f };

		FixtureTemplate restitutionTemplate = new FixtureTemplate(physicObject, restitution) {
			@Override
			protected void setValue(float value) {
				physicObject.setRestitution(value);
			}

			@Override
			protected float getValue(Fixture fixture) {
				return fixture.getRestitution();
			}
		};
		restitutionTemplate.test();
	}

	public void testMass() {
		PhysicObject physicObject = createPhysicObject();
		assertEquals(1.0f, physicObject.getBody().getMass());

		float[] masses = { 0.01f, 1.0f, 123456.0f };
		for (float mass : masses) {
			physicObject.setMass(mass);
			assertEquals(mass, physicObject.getBody().getMass());
		}

		float[] massesResetedToZero = { 0.0f, -0.123f, -123.456f };
		for (float mass : massesResetedToZero) {
			physicObject.setMass(mass);
			assertEquals(1.0f, physicObject.getBody().getMass());
		}
	}

	//	public void testType() {
	//		PhysicObject physicObject = createPhysicObject();
	//		assertEquals(BodyType.DynamicBody, physicObject.getBody().getType());
	//
	//		BodyType[] types = { BodyType.StaticBody, BodyType.DynamicBody };
	//		for (BodyType type : types) {
	//			physicObject.setType(type);
	//			assertEquals(type, physicObject.getBody().getType());
	//		}
	//	}

	public void testShape() {
		PhysicObject physicObject = createPhysicObject();
		assertTrue(physicObject.getBody().getFixtureList().size() == 1);

		Shape shape = physicObject.getBody().getFixtureList().get(0).getShape();
		assertEquals(Shape.Type.Polygon, shape.getType());
		assertEquals(1, shape.getChildCount());
		assertEquals(0.01f, shape.getRadius());

		PolygonShape polygonShape = new PolygonShape();
		assertEquals(0, polygonShape.getVertexCount());

		// TODO: Much more testing coming soon!
	}
}
