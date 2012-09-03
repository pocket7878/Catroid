package at.tugraz.ist.catroid.test.physics;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.physics.PhysicObject;
import at.tugraz.ist.catroid.physics.PhysicObject.Type;
import at.tugraz.ist.catroid.physics.PhysicObjectMap;
import at.tugraz.ist.catroid.physics.PhysicSettings;
import at.tugraz.ist.catroid.physics.PhysicWorldConverter;
import at.tugraz.ist.catroid.test.utils.TestUtils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicObjectTest extends AndroidTestCase {
	static {
		GdxNativesLoader.load();
	}

	private PhysicObjectMap objects;

	private abstract class FixtureProptertyTestTemplate {
		protected final PhysicObject physicObject;
		protected final float[] values;

		public FixtureProptertyTestTemplate(PhysicObject physicObject, float[] values) {
			this.physicObject = physicObject;
			this.values = values;
		}

		public void test() {
			assertTrue("Without any values the correctness won't be tested.", values.length > 0);
			assertFalse("Without any fixtures the correctness won't be tested.", getBody(physicObject).getFixtureList()
					.isEmpty());

			for (float value : values) {
				setValue(value);
				for (Fixture fixture : getBody(physicObject).getFixtureList()) {
					assertEquals(value, getValue(fixture));
				}
			}
		}

		protected abstract float getValue(Fixture fixture);

		protected abstract void setValue(float value);
	}

	@Override
	protected void setUp() throws Exception {
		objects = new PhysicObjectMap(new World(PhysicSettings.World.DEFAULT_GRAVITY,
				PhysicSettings.World.IGNORE_SLEEPING_OBJECTS));
	}

	@Override
	protected void tearDown() throws Exception {
	}

	protected PhysicObject createPhysicObject(PhysicObject.Type type, float width, float height) {
		return createPhysicObject(type, createRectangleShape(width, height));
	}

	protected Shape createRectangleShape(float width, float height) {
		PolygonShape rectangle = new PolygonShape();
		rectangle.setAsBox(width / 2.0f, height / 2.0f);
		return rectangle;
	}

	protected PhysicObject createPhysicObject(PhysicObject.Type type, Shape shape) {
		PhysicObject physicObject = objects.get(new Sprite("TestSprite"));

		if (type == null) {
			physicObject.setType(Type.DYNAMIC);
		} else {
			physicObject.setType(type);
		}

		if (shape != null) {
			physicObject.setShape(new Shape[] { shape });
		}

		return physicObject;
	}

	protected PhysicObject createPhysicObject(PhysicObject.Type type) {
		return createPhysicObject(type, null);
	}

	protected PhysicObject createPhysicObject() {
		return createPhysicObject(null, null);
	}

	private Body getBody(PhysicObject physicObject) {
		return (Body) TestUtils.getPrivateField("body", physicObject, false);
	}

	private Shape[] getShapes(PhysicObject physicObject) {
		return (Shape[]) TestUtils.getPrivateField("shapes", physicObject, false);
	}

	private FixtureDef getFixtureDef(PhysicObject physicObject) {
		return (FixtureDef) TestUtils.getPrivateField("fixtureDef", physicObject, false);
	}

	public void testNullBody() {
		try {
			@SuppressWarnings("unused")
			PhysicObject objectWithNullBody = new PhysicObject(null);
			assertTrue(false);
		} catch (NullPointerException exception) {
			assertTrue(true);
		}
	}

	public void testInitialPhysicObjectProperties() {
		PhysicObject physicObject = createPhysicObject();
		Body body = getBody(physicObject);

		assertTrue(body.getFixtureList().isEmpty());

		FixtureDef fixtureDef = getFixtureDef(physicObject);

		assertEquals(PhysicSettings.Object.DEFAULT_DENSITY, fixtureDef.density);
		assertEquals(PhysicSettings.Object.DEFAULT_FRICTION, fixtureDef.friction);
		assertEquals(PhysicSettings.Object.DEFAULT_RESTITUTION, fixtureDef.restitution);
	}

	public void testInitialPhysicObjectEqualsNone() {
		PhysicObject physicObject = objects.get(new Sprite("TestSprite"));
		PhysicObject nonePhysicObject = createPhysicObject(Type.NONE);

		assertEquals(physicObject.type, nonePhysicObject.type);
		assertEquals(physicObject.body.getType(), nonePhysicObject.body.getType());

		assertEquals(getFixtureDef(physicObject).shape, getFixtureDef(nonePhysicObject).shape);

		assertEquals(getBody(physicObject).getFixtureList().size(), getBody(nonePhysicObject).getFixtureList().size());
	}

	public void testSetShape() {
		PhysicObject physicObject = createPhysicObject();
		Body body = getBody(physicObject);

		PolygonShape rectangle = (PolygonShape) createRectangleShape(5.0f, 5.0f);
		physicObject.setShape(new Shape[] { rectangle });

		assertFalse(body.getFixtureList().isEmpty());
		assertEquals(rectangle, physicObject.fixtureDef.shape);
		assertNotSame(rectangle, body.getFixtureList().get(0).getShape());
		assertEquals(4, rectangle.getVertexCount());
	}

	public void testSetNewShape() {
		PhysicObject physicObject = createPhysicObject();
		Body body = getBody(physicObject);

		Shape shape = new PolygonShape();
		physicObject.setShape(new Shape[] { shape });
		Shape fixtureShape = body.getFixtureList().get(0).getShape();

		Shape newShape = new PolygonShape();
		physicObject.setShape(new Shape[] { newShape });

		assertTrue(!body.getFixtureList().isEmpty());
		assertEquals(newShape, physicObject.fixtureDef.shape);
		assertNotSame(fixtureShape, body.getFixtureList().get(0).getShape());
	}

	public void testSetSameShape() {
		// TODO: Test setShape return;
		PhysicObject physicObject = createPhysicObject();
		Body body = getBody(physicObject);

		PolygonShape rectangle = (PolygonShape) createRectangleShape(5.0f, 5.0f);
		Shape[] shapes = new Shape[] { rectangle };
		physicObject.setShape(shapes);
		Shape[] physicObjectShapes = getShapes(physicObject);
		physicObject.setShape(shapes);

		assertTrue(!body.getFixtureList().isEmpty());
		assertEquals(rectangle, physicObject.fixtureDef.shape);
		assertEquals(physicObjectShapes, getShapes(physicObject));
		assertEquals(4, rectangle.getVertexCount());
	}

	public void testSetNullShapeRemovesAllFixtures() {
		PhysicObject physicObject = createPhysicObject();
		Body body = getBody(physicObject);

		PolygonShape rectangle = (PolygonShape) createRectangleShape(5.0f, 5.0f);
		physicObject.setShape(new Shape[] { rectangle });

		assertTrue(!body.getFixtureList().isEmpty());
		assertEquals(4, rectangle.getVertexCount());

		physicObject.setShape(null);
		assertEquals(null, getShapes(physicObject));
		assertTrue(body.getFixtureList().isEmpty());
	}

	public void testType() {
		PhysicObject physicObject = createPhysicObject();
		Body body = getBody(physicObject);

		physicObject.setType(Type.DYNAMIC);
		assertEquals(Type.DYNAMIC, physicObject.type);
		assertEquals(BodyType.DynamicBody, body.getType());
		assertTrue(body.isActive());

		physicObject.setType(Type.FIXED);
		assertEquals(Type.FIXED, physicObject.type);
		assertEquals(BodyType.KinematicBody, body.getType());
		assertTrue(body.isActive());

		physicObject.setType(Type.NONE);
		assertEquals(Type.NONE, physicObject.type);
		assertEquals(BodyType.StaticBody, body.getType());
		assertFalse(body.isActive());
	}

	public void testAngle() {
		for (PhysicObject.Type type : PhysicObject.Type.values()) {
			PhysicObject physicObject = createPhysicObject(type);
			assertEquals(0.0f, getBody(physicObject).getAngle());

			float[] degrees = { 45.0f, 66.66f, -90.0f, 500.0f };

			for (float angle : degrees) {
				float radian = PhysicWorldConverter.angleCatToBox2d(angle);
				physicObject.setAngle(radian);
				assertEquals(radian, getBody(physicObject).getAngle());
			}
		}
	}

	public void testPosition() {
		for (PhysicObject.Type type : PhysicObject.Type.values()) {
			PhysicObject physicObject = createPhysicObject(type);
			assertEquals(new Vector2(), getBody(physicObject).getPosition());

			Vector2[] positions = { new Vector2(12.34f, 56.78f), new Vector2(-87.65f, -43.21f) };
			for (Vector2 position : positions) {
				physicObject.setPosition(position.x, position.y);
				assertEquals(position, getBody(physicObject).getPosition());
			}
		}
	}

	public void testAngleAndPosition() {
		for (PhysicObject.Type type : PhysicObject.Type.values()) {
			PhysicObject physicObject = createPhysicObject(type);
			assertEquals(0.0f, getBody(physicObject).getAngle());
			assertEquals(new Vector2(), getBody(physicObject).getPosition());

			float angle = PhysicWorldConverter.angleCatToBox2d(13.56f);
			Vector2 position = new Vector2(12.34f, 56.78f);
			physicObject.setAngle(angle);
			physicObject.setPosition(position.x, position.y);

			assertEquals(angle, getBody(physicObject).getAngle());
			assertEquals(position, getBody(physicObject).getPosition());
		}
	}

	public void testFriction() {
		PhysicObject physicObject = createPhysicObject(Type.DYNAMIC, new PolygonShape());
		float[] friction = { 0.123f, -0.765f, 24.32f };

		FixtureProptertyTestTemplate frictionTemplate = new FixtureProptertyTestTemplate(physicObject, friction) {
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
		PhysicObject physicObject = createPhysicObject(Type.DYNAMIC, new PolygonShape());
		float[] restitution = { 0.123f, -0.765f, 24.32f };

		FixtureProptertyTestTemplate restitutionTemplate = new FixtureProptertyTestTemplate(physicObject, restitution) {
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

	public void testDensity() {
		PhysicObject physicObject = createPhysicObject(Type.DYNAMIC, new PolygonShape());
		float[] density = { 0.123f, -0.765f, 24.32f };

		FixtureProptertyTestTemplate densityTemplate = new FixtureProptertyTestTemplate(physicObject, density) {
			@Override
			protected void setValue(float value) {
				Class<?>[] parameterTypeList = { float.class };
				Object[] values = { value };
				TestUtils.invokeMethod(physicObject, "setDensity", parameterTypeList, values);
			}

			@Override
			protected float getValue(Fixture fixture) {
				return fixture.getDensity();
			}
		};
		densityTemplate.test();
	}

	public void testMass() {
		for (PhysicObject.Type type : PhysicObject.Type.values()) {
			PhysicObject physicObject = createPhysicObject(type, 5.0f, 5.0f);

			checkMassDependingOnType(physicObject, PhysicSettings.Object.DEFAULT_MASS);

			float[] masses = { 0.01f, 1.0f, 12345.0f };
			for (float mass : masses) {
				physicObject.setMass(mass);
				checkMassDependingOnType(physicObject, mass);
			}

			float[] massesResetedToOne = { 0.0f, -0.123f, -123.456f };
			for (float mass : massesResetedToOne) {
				physicObject.setMass(mass);
				checkMassDependingOnType(physicObject, 1.0f);
			}
		}
	}

	private void checkMassDependingOnType(PhysicObject physicObject, float expectedMass) {
		Body body = getBody(physicObject);
		float expectedBodyMass = expectedMass;
		float expectedPhysicObjectMass = expectedMass;

		if (body.getType() != BodyType.DynamicBody) {
			expectedBodyMass = 0.0f;
		}

		if (expectedMass <= 0.0f) {
			expectedPhysicObjectMass = 1.0f;

			if (body.getType() == BodyType.DynamicBody) {
				expectedBodyMass = 1.0f;
			}
		}

		assertEquals(expectedBodyMass, body.getMass());
		assertEquals(expectedPhysicObjectMass, physicObject.mass);
	}

	public void testMassWithShapeChange() {
		PhysicObject physicObject = createPhysicObject(Type.DYNAMIC, 5.0f, 5.0f);
		float mass = 5.0f;

		physicObject.setMass(mass);
		checkMassDependingOnType(physicObject, mass);

		physicObject.setShape(new Shape[] { createRectangleShape(7.0f, 7.0f) });
		checkMassDependingOnType(physicObject, mass);

		float newMass = 3.0f;
		physicObject.setMass(newMass);
		checkMassDependingOnType(physicObject, newMass);
	}

	public void testMassWithZeroShapeArea() {
		PhysicObject physicObject = createPhysicObject(Type.NONE, 0.0f, 0.0f);
		float mass = 1.2f;
		physicObject.setMass(mass);
		Body body = getBody(physicObject);

		assertEquals(0.0f, body.getMass());
		assertEquals(1.0f, physicObject.fixtureDef.density);

		physicObject.setType(Type.DYNAMIC);
		assertEquals(1.0f, body.getMass());
		assertEquals(mass, physicObject.fixtureDef.density);
	}
}
