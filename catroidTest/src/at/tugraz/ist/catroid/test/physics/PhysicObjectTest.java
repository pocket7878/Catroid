package at.tugraz.ist.catroid.test.physics;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.physics.PhysicObject;
import at.tugraz.ist.catroid.physics.PhysicObject.Type;
import at.tugraz.ist.catroid.physics.PhysicObjectMap;
import at.tugraz.ist.catroid.physics.PhysicSettings;
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

	private PhysicObjectMap objects = new PhysicObjectMap(new World(PhysicSettings.World.DEFAULT_GRAVITY,
			PhysicSettings.World.IGNORE_SLEEPING_OBJECTS));

	@Override
	protected void setUp() throws Exception {
	}

	@Override
	protected void tearDown() throws Exception {
	}

	public void testNullBody() {
		try {
			@SuppressWarnings("unused")
			PhysicObject objectWithNullBody = new PhysicObject(null);
			assertTrue(false);
		} catch (NullPointerException exception) {
		}
	}

	public void testDefaultValues() {
		PhysicObject physicObject = createPhysicObject();
		assertEquals(Type.NONE, getType(physicObject));
		assertEquals(PhysicSettings.Object.DEFAULT_MASS, getMass(physicObject));

		Body body = getBody(physicObject);
		assertFalse(body.isActive());
		assertTrue(body.getFixtureList().isEmpty());

		FixtureDef fixtureDef = getFixtureDef(physicObject);
		assertEquals(PhysicSettings.Object.DEFAULT_DENSITY, fixtureDef.density);
		assertEquals(PhysicSettings.Object.DEFAULT_FRICTION, fixtureDef.friction);
		assertEquals(PhysicSettings.Object.DEFAULT_RESTITUTION, fixtureDef.restitution);
	}

	public void testSetShape() {
		PhysicObject physicObject = createPhysicObject();
		PolygonShape[] rectangle = new PolygonShape[] { createRectanglePolygonShape(5.0f, 5.0f) };
		physicObject.setShape(rectangle);

		checkIfShapesAreTheSameAsInPhysicObject(rectangle, getBody(physicObject));
	}

	public void testSetNewShape() {
		PhysicObject physicObject = createPhysicObject();
		Shape[] shape = new PolygonShape[] { createRectanglePolygonShape(5.0f, 5.0f) };
		physicObject.setShape(shape);

		Body body = getBody(physicObject);
		PolygonShape[] newShape = new PolygonShape[] { createRectanglePolygonShape(1.0f, 2.0f) };
		physicObject.setShape(newShape);

		assertSame(newShape, getShapes(physicObject));
		checkIfShapesAreTheSameAsInPhysicObject(newShape, body);
	}

	public void testSetSameShape() {
		PhysicObject physicObject = createPhysicObject();
		Body body = getBody(physicObject);

		Shape[] rectangle = new Shape[] { createRectanglePolygonShape(5.0f, 5.0f) };
		physicObject.setShape(rectangle);
		assertFalse(body.getFixtureList().isEmpty());

		List<Fixture> fixturesBeforeReset = new ArrayList<Fixture>(body.getFixtureList());
		physicObject.setShape(rectangle);
		List<Fixture> fixturesAfterReset = new ArrayList<Fixture>(body.getFixtureList());

		assertEquals(fixturesBeforeReset, fixturesAfterReset);
	}

	public void testSetNullShapeRemovesAllFixtures() {
		PhysicObject physicObject = createPhysicObject();
		Body body = getBody(physicObject);

		physicObject.setShape(new Shape[] { createRectanglePolygonShape(5.0f, 5.0f) });
		assertFalse(body.getFixtureList().isEmpty());

		physicObject.setShape(null);
		assertEquals(null, getShapes(physicObject));
		assertTrue(body.getFixtureList().isEmpty());
	}

	public void testSetShapeUpdatesDensityButNotMass() {
		PhysicObject physicObject = createPhysicObject();
		physicObject.setShape(new Shape[] { createRectanglePolygonShape(5.0f, 5.0f) });
		Body body = getBody(physicObject);

		float oldDensity = getFixtureDef(physicObject).density;
		float oldMass = body.getMass();

		physicObject.setShape(new Shape[] { createRectanglePolygonShape(111.0f, 111.0f) });

		assertNotSame(oldDensity, getFixtureDef(physicObject).density);
		assertEquals(oldMass, body.getMass());
	}

	public void testSetType() {
		PhysicObject physicObject = createPhysicObject();
		Body body = getBody(physicObject);

		physicObject.setType(Type.FIXED);
		assertEquals(Type.FIXED, getType(physicObject));
		assertEquals(BodyType.KinematicBody, body.getType());
		assertTrue(body.isActive());

		physicObject.setType(Type.DYNAMIC);
		assertEquals(Type.DYNAMIC, getType(physicObject));
		assertEquals(BodyType.DynamicBody, body.getType());
		assertTrue(body.isActive());

		physicObject.setType(Type.NONE);
		assertEquals(Type.NONE, getType(physicObject));
		assertEquals(BodyType.KinematicBody, body.getType());
		assertFalse(body.isActive());
	}

	public void testSetTypeDynamicUpdatesMass() {
		PhysicObject physicObject = createPhysicObject(Type.NONE);
		Body body = getBody(physicObject);

		float rectangeSize = 10.0f;
		physicObject.setShape(new Shape[] { createRectanglePolygonShape(rectangeSize, rectangeSize) });

		float mass = 128.0f;
		physicObject.setMass(mass);
		assertEquals(0.0f, body.getMass());

		physicObject.setType(Type.DYNAMIC);
		assertEquals(mass, body.getMass());
	}

	public void testAngle() {
		for (Type type : Type.values()) {
			PhysicObject physicObject = createPhysicObject(type);
			assertEquals(0.0f, getBody(physicObject).getAngle());

			float[] radians = { 0.1f, 3.14f, -1.0f, 10.0f };

			for (float angle : radians) {
				physicObject.setAngle(angle);
				assertEquals(angle, getBody(physicObject).getAngle());
			}
		}
	}

	public void testPosition() {
		for (Type type : Type.values()) {
			PhysicObject physicObject = createPhysicObject(type);
			assertEquals(new Vector2(), getBody(physicObject).getPosition());

			Vector2[] positions = { new Vector2(12.34f, 56.78f), new Vector2(-87.65f, -43.21f) };
			for (Vector2 position : positions) {
				physicObject.setXYPosition(position.x, position.y);
				assertEquals(position, getBody(physicObject).getPosition());
			}

			for (Vector2 position : positions) {
				physicObject.setXYPosition(position);
				assertEquals(position, getBody(physicObject).getPosition());
			}
		}
	}

	public void testAngleAndPosition() {
		for (Type type : Type.values()) {
			PhysicObject physicObject = createPhysicObject(type);
			assertEquals(0.0f, getBody(physicObject).getAngle());
			assertEquals(new Vector2(), getBody(physicObject).getPosition());

			float angle = 1.56f;
			Vector2 position = new Vector2(12.34f, 56.78f);
			physicObject.setAngle(angle);
			physicObject.setXYPosition(position.x, position.y);

			assertEquals(angle, getBody(physicObject).getAngle());
			assertEquals(position, getBody(physicObject).getPosition());
		}
	}

	public void testSetDensity() {
		for (Type type : Type.values()) {
			PhysicObject physicObject = createPhysicObject(type);
			physicObject.setShape(new Shape[] { new PolygonShape(), new PolygonShape() });
			float[] densityValues = { 0.123f, -0.765f, 24.32f };

			FixtureProptertyTestTemplate densityTemplate = new FixtureProptertyTestTemplate(physicObject, densityValues) {
				@Override
				protected void setValue(float value) {
					Class<?>[] parameterTypeList = { float.class };
					Object[] values = { value };
					TestUtils.invokeMethod(physicObject, "setDensity", parameterTypeList, values);
				}

				@Override
				protected float getFixtureValue(Fixture fixture) {
					return fixture.getDensity();
				}

				@Override
				protected float getFixtureDefValue() {
					return getFixtureDef(physicObject).density;
				}
			};
			densityTemplate.test();
		}
	}

	public void testSetDensityUpdatesMassData() {
		PhysicObject physicObject = createPhysicObject(Type.DYNAMIC, 5.0f, 5.0f);
		Body body = getBody(physicObject);

		float oldMass = body.getMass();
		float density = 12.0f;
		assertNotSame(density, getFixtureDef(physicObject).density);

		Class<?>[] parameterTypeList = { float.class };
		Object[] values = { density };
		TestUtils.invokeMethod(physicObject, "setDensity", parameterTypeList, values);

		assertNotSame(oldMass, body.getMass());
	}

	public void testSetDensityAtMassChange() {
		PhysicObject physicObject = createPhysicObject(Type.DYNAMIC);
		Body body = getBody(physicObject);

		float rectangleSize = 24.0f;
		float[] masses = { PhysicSettings.Object.MIN_MASS, 1.0f, 24.0f };

		physicObject.setShape(new Shape[] { createRectanglePolygonShape(rectangleSize, rectangleSize) });
		for (float mass : masses) {
			physicObject.setMass(mass);
			float actualDensity = body.getMass() / (rectangleSize * rectangleSize);
			assertEquals(getFixtureDef(physicObject).density, actualDensity);
		}
	}

	public void testSetFriction() {
		for (Type type : Type.values()) {
			PhysicObject physicObject = createPhysicObject(type);
			physicObject.setShape(new Shape[] { new PolygonShape(), new PolygonShape() });

			float[] frictionValues = { 0.123f, -0.765f, 24.32f };
			FixtureProptertyTestTemplate frictionTemplate = new FixtureProptertyTestTemplate(physicObject,
					frictionValues) {
				@Override
				protected void setValue(float value) {
					physicObject.setFriction(value);
				}

				@Override
				protected float getFixtureValue(Fixture fixture) {
					return fixture.getFriction();
				}

				@Override
				protected float getFixtureDefValue() {
					return getFixtureDef(physicObject).friction;
				}
			};
			frictionTemplate.test();
		}
	}

	public void testSetBounceFactor() {
		for (Type type : Type.values()) {
			PhysicObject physicObject = createPhysicObject(type);
			physicObject.setShape(new Shape[] { new PolygonShape(), new PolygonShape() });
			float[] bounceFactors = { 0.123f, -0.765f, 24.32f };

			FixtureProptertyTestTemplate restitutionTemplate = new FixtureProptertyTestTemplate(physicObject,
					bounceFactors) {
				@Override
				protected void setValue(float value) {
					physicObject.setBounceFactor(value);
				}

				@Override
				protected float getFixtureValue(Fixture fixture) {
					return fixture.getRestitution();
				}

				@Override
				protected float getFixtureDefValue() {
					return getFixtureDef(physicObject).restitution;
				}
			};
			restitutionTemplate.test();
		}
	}

	public void testMass() {
		for (Type type : Type.values()) {
			PhysicObject physicObject = createPhysicObject(type, 5.0f, 5.0f);
			Body body = getBody(physicObject);

			checkBodyMassDependingOnType(type, body, PhysicSettings.Object.DEFAULT_MASS);
			assertEquals(PhysicSettings.Object.DEFAULT_MASS, getMass(physicObject));

			float[] masses = { PhysicSettings.Object.MIN_MASS, 0.01f, 1.0f, 12345.0f };
			for (float mass : masses) {
				physicObject.setMass(mass);
				checkBodyMassDependingOnType(type, body, mass);
				assertEquals(mass, getMass(physicObject));
			}

			float[] massesResetedToMinMass = { PhysicSettings.Object.MIN_MASS / 10.0f, 0.0f, -1.0f };
			for (float mass : massesResetedToMinMass) {
				physicObject.setMass(mass);
				checkBodyMassDependingOnType(type, body, PhysicSettings.Object.MIN_MASS);
				assertEquals(PhysicSettings.Object.MIN_MASS, getMass(physicObject));
			}
		}
	}

	private void checkBodyMassDependingOnType(Type type, Body body, float expectedBodyMass) {
		if (type != Type.DYNAMIC) {
			expectedBodyMass = 0.0f;
		}
		assertEquals(expectedBodyMass, body.getMass());
	}

	public void testMassWithNoShapeArea() {
		PhysicObject[] physicObjects = { createPhysicObject(Type.DYNAMIC), createPhysicObject(Type.DYNAMIC, 0.0f, 0.0f) };

		for (PhysicObject physicObject : physicObjects) {
			Body body = getBody(physicObject);

			float oldMass = body.getMass();
			float mass = 1.2f;
			assertNotSame(oldMass, mass);

			physicObject.setMass(mass);
			assertEquals(oldMass, body.getMass());
			assertEquals(mass, getMass(physicObject));
		}
	}

	public void testSetRotationSpeed() {
		PhysicObject physicObject = createPhysicObject(Type.DYNAMIC);
		Body body = getBody(physicObject);

		assertEquals(0.0f, body.getAngularVelocity());
		float rotationSpeed = 1.23f;
		physicObject.setRotationSpeed(rotationSpeed);
		assertEquals(rotationSpeed, body.getAngularVelocity());
	}

	public void testSetVelocity() {
		PhysicObject physicObject = createPhysicObject(Type.DYNAMIC);
		Body body = getBody(physicObject);

		assertEquals(new Vector2(), body.getLinearVelocity());
		Vector2 velocity = new Vector2(1.23f, 4.56f);
		physicObject.setVelocity(velocity);
		assertEquals(velocity, body.getLinearVelocity());
	}

	// SANTA'S LITTLE HELPERS :)

	// Creating Physic Objects helper methods.
	protected PhysicObject createPhysicObject(PhysicObject.Type type, float width, float height) {
		return createPhysicObject(type, createRectanglePolygonShape(width, height));
	}

	protected PolygonShape createRectanglePolygonShape(float width, float height) {
		PolygonShape rectangle = new PolygonShape();
		rectangle.setAsBox(width / 2.0f, height / 2.0f);
		return rectangle;
	}

	protected PhysicObject createPhysicObject(PhysicObject.Type type, Shape shape) {
		PhysicObject physicObject = objects.get(new Sprite("TestSprite"));

		if (type != null) {
			physicObject.setType(type).release();
		}

		if (shape != null) {
			physicObject.setShape(new Shape[] { shape });
		}

		return physicObject;
	}

	// Private member helper methods.
	protected PhysicObject createPhysicObject(PhysicObject.Type type) {
		return createPhysicObject(type, null);
	}

	protected PhysicObject createPhysicObject() {
		return createPhysicObject(null, null);
	}

	private Body getBody(PhysicObject physicObject) {
		return (Body) TestUtils.getPrivateField("body", physicObject, false);
	}

	private Type getType(PhysicObject physicObject) {
		return (Type) TestUtils.getPrivateField("type", physicObject, false);
	}

	private float getMass(PhysicObject physicObject) {
		return (Float) TestUtils.getPrivateField("mass", physicObject, false);
	}

	private Shape[] getShapes(PhysicObject physicObject) {
		return (Shape[]) TestUtils.getPrivateField("shapes", physicObject, false);
	}

	private FixtureDef getFixtureDef(PhysicObject physicObject) {
		return (FixtureDef) TestUtils.getPrivateField("fixtureDef", physicObject, false);
	}

	// Fixture property helper.
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
				assertEquals(value, getFixtureDefValue());
				for (Fixture fixture : getBody(physicObject).getFixtureList()) {
					assertEquals(value, getFixtureValue(fixture));
				}
			}
		}

		protected abstract float getFixtureValue(Fixture fixture);

		protected abstract float getFixtureDefValue();

		protected abstract void setValue(float value);
	}

	// ... and other helpers
	private void checkIfShapesAreTheSameAsInPhysicObject(PolygonShape[] shapes, Body body) {
		List<Fixture> fixtures = body.getFixtureList();
		assertEquals(shapes.length, body.getFixtureList().size());
		if (body.getFixtureList().isEmpty()) {
			return;
		}

		PolygonShape currentShape;
		PolygonShape currentPhysicObjectShape;
		for (int shapeIndex = 0; shapeIndex < shapes.length; shapeIndex++) {
			currentShape = shapes[shapeIndex];
			currentPhysicObjectShape = (PolygonShape) fixtures.get(shapeIndex).getShape();
			assertNotSame(currentShape, currentPhysicObjectShape);

			Vector2 expectedVertex = new Vector2();
			Vector2 actualVertex = new Vector2();
			assertEquals(currentShape.getVertexCount(), currentPhysicObjectShape.getVertexCount());
			for (int vertexIndex = 0; vertexIndex < currentShape.getVertexCount(); vertexIndex++) {
				currentShape.getVertex(vertexIndex, expectedVertex);
				currentPhysicObjectShape.getVertex(vertexIndex, actualVertex);
				assertEquals(expectedVertex, actualVertex);
			}
		}
	}
}
