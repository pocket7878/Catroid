package at.tugraz.ist.catroid.test.physics;

import java.util.List;

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
import com.badlogic.gdx.physics.box2d.EdgeShape;
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

	private abstract class FixtureTemplate {
		protected final PhysicObject physicObject;
		protected final float[] values;

		public FixtureTemplate(PhysicObject physicObject, float[] values) {
			this.physicObject = physicObject;
			this.values = values;
		}

		public void test() {
			assertTrue(values.length > 0);
			System.out.println("LOG: " + getBody(physicObject).getFixtureList());
			assertFalse(getBody(physicObject).getFixtureList().isEmpty());

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

	//	private class PhysicObjectAdapter {
	//		private final PhysicObject physicObject;
	//
	//		public PhysicObjectAdapter(PhysicObject physicObject) {
	//			this.physicObject = physicObject;
	//		}
	//
	//		public Shape getFixtureDefShape() {
	//			return getFixtureDef(physicObject).shape;
	//		}
	//		
	//		public Shape get
	//		
	//		public void setShape(Shape shape) {
	//			physicObject.setShape(shape);
	//		}
	//		
	//		public void setType(Type type) {
	//			physicObject.setType(type);
	//		}
	//	}

	@Override
	protected void setUp() throws Exception {
		objects = new PhysicObjectMap(new World(PhysicSettings.World.DEFAULT_GRAVITY,
				PhysicSettings.World.IGNORE_SLEEPING_OBJECTS));
	}

	@Override
	protected void tearDown() throws Exception {
	}

	protected PhysicObject createPhysicObject(PhysicObject.Type type, Shape shape) {
		PhysicObject physicObject = objects.get(new Sprite("TestSprite"));

		if (type != null) {
			physicObject.setType(type);
		}

		if (shape != null) {
			physicObject.setShape(shape);
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

	private FixtureDef getFixtureDef(PhysicObject physicObject) {
		return (FixtureDef) TestUtils.getPrivateField("fixtureDef", physicObject, false);
	}

	private Fixture getFixture(PhysicObject physicObject) {
		List<Fixture> fixtures = getBody(physicObject).getFixtureList();
		assertEquals(1, fixtures.size());
		return fixtures.get(0);
	}

	public void testPhysicObjectProperties() {
		PhysicObject physicObject = createPhysicObject();
		Body body = getBody(physicObject);

		assertTrue(body.getFixtureList().isEmpty());

		FixtureDef fixtureDef = getFixtureDef(physicObject);

		assertEquals(1.0f, fixtureDef.density);
		assertEquals(0.2f, fixtureDef.friction);
		assertEquals(0.0f, fixtureDef.restitution);
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

	public void testPhysicObjectEqualsNone() {
		PhysicObject physicObject = createPhysicObject();
		PhysicObject nonePhysicObject = createPhysicObject(Type.NONE);

		assertEquals(physicObject.type, nonePhysicObject.type);
		assertEquals(physicObject.body.getType(), nonePhysicObject.body.getType());

		assertEquals(getFixtureDef(physicObject).shape, getFixtureDef(nonePhysicObject).shape);

		assertEquals(getBody(physicObject).getFixtureList().size(), getBody(nonePhysicObject).getFixtureList().size());
	}

	//	public void testShapeAndTypeChange() {
	//		for (Type from : Type.values()) {
	//			for (Type to : Type.values()) {
	//				PhysicObject physicObject = createPhysicObject();
	//				physicObject.setType(from);
	//				physicObject.setType(to);
	//
	//				PhysicObject physicObject2 = createPhysicObject();
	//				physicObject2.setType(from);
	//				Shape shape = new PolygonShape();
	//				physicObject2.setShape(shape);
	//				physicObject2.setType(to);
	//			}
	//		}
	//	}
	//
	//	public void testTypeChangeWithoutShape() {
	//		PhysicObject physicObject = createPhysicObject();
	//		Body body = getBody(physicObject);
	//
	//		assertEquals(Type.NONE, physicObject.type);
	//		assertEquals(BodyType.StaticBody, body.getType());
	//
	//		applyAndCheckTypeChangeWithoutShape(physicObject, Type.DYNAMIC, BodyType.DynamicBody);
	//		applyAndCheckTypeChangeWithoutShape(physicObject, Type.FIXED, BodyType.KinematicBody);
	//		applyAndCheckTypeChangeWithoutShape(physicObject, Type.NONE, BodyType.StaticBody);
	//	}
	//
	//	private void applyAndCheckTypeChangeWithoutShape(PhysicObject physicObject, Type toType, BodyType expectedBodyType) {
	//		physicObject.setType(toType);
	//		assertEquals(toType, physicObject.type);
	//		assertEquals(expectedBodyType, getBody(physicObject).getType());
	//	}
	//
	//	public void testTypeChangeWithShape() {
	//		applyTypeTransitionCheck(Type.DYNAMIC, Type.DYNAMIC, new PolygonShape());
	//		applyTypeTransitionCheck(Type.DYNAMIC, Type.FIXED, new PolygonShape());
	//		applyTypeTransitionCheck(Type.DYNAMIC, Type.NONE, new PolygonShape());
	//
	//		applyTypeTransitionCheck(Type.FIXED, Type.DYNAMIC, new PolygonShape());
	//		applyTypeTransitionCheck(Type.FIXED, Type.FIXED, new PolygonShape());
	//		applyTypeTransitionCheck(Type.FIXED, Type.NONE, new PolygonShape());
	//
	//		applyTypeTransitionCheck(Type.NONE, Type.DYNAMIC, new PolygonShape());
	//		applyTypeTransitionCheck(Type.NONE, Type.FIXED, new PolygonShape());
	//		applyTypeTransitionCheck(Type.NONE, Type.NONE, new PolygonShape());
	//	}
	//
	//	public void applyTypeTransitionCheck(Type from, Type to, Shape shape) {
	//		// TODO: Test!
	//
	//		if (from == to) {
	//
	//		}
	//
	//		PhysicObject physicObject = createPhysicObject();
	//		FixtureDef fixtureDef = getFixtureDef(physicObject);
	//		Body body = getBody(physicObject);
	//	}
	//
	//	public void testPhysicObjectShape() {
	//		// TODO: Test!
	//		PhysicObject physicObject = createPhysicObject();
	//		FixtureDef fixtureDef = getFixtureDef(physicObject);
	//
	//		assertNull(fixtureDef.shape);
	//	}
	//
	//	public void testDynamicPhysicObjectWithShapeChange() {
	//		PhysicObject physicObject = createPhysicObject();
	//		FixtureDef fixtureDef = getFixtureDef(physicObject);
	//		Body body = getBody(physicObject);
	//
	//		physicObject.setType(Type.DYNAMIC);
	//		Shape shape = new PolygonShape();
	//		physicObject.setShape(shape);
	//
	//		assertEquals(shape, fixtureDef.shape);
	//		assertTrue(body.getFixtureList().size() == 1);
	//
	//		Fixture fixture = getFixture(physicObject);
	//		physicObject.setShape(shape);
	//
	//		assertEquals(shape, fixtureDef.shape);
	//		assertTrue(body.getFixtureList().size() == 1);
	//		assertEquals(fixture, getFixture(physicObject));
	//
	//		Shape anotherShape = new PolygonShape();
	//		physicObject.setShape(anotherShape);
	//
	//		assertNotSame(shape, fixtureDef.shape);
	//		assertTrue(body.getFixtureList().size() == 1);
	//		assertNotSame(fixture, getFixture(physicObject));
	//	}

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
		PhysicObject physicObject = createPhysicObject(Type.DYNAMIC, new PolygonShape());
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
		for (PhysicObject.Type type : PhysicObject.Type.values()) {
			PhysicObject physicObject = createPhysicObject(type);
			Body body = getBody(physicObject);
			assertEquals((physicObject.type == Type.DYNAMIC) ? 1.0f : 0.0f, body.getMass());

			float[] masses = { 0.01f, 1.0f, 123456.0f };
			for (float mass : masses) {
				physicObject.setMass(mass);
				assertEquals((physicObject.type == Type.DYNAMIC) ? mass : 0.0f, body.getMass());
			}

			float[] massesResetedToZero = { 0.0f, -0.123f, -123.456f };
			for (float mass : massesResetedToZero) {
				physicObject.setMass(mass);
				assertEquals((physicObject.type == Type.DYNAMIC) ? 1.0f : 0.0f, body.getMass());
			}
		}
	}

	public void testMassWithShapeChange() {
		PhysicObject physicObject = createPhysicObject(Type.DYNAMIC);
		float mass = 5.0f;

		physicObject.setMass(mass);
		checkMass(physicObject, mass);

		physicObject.setShape(new PolygonShape());
		checkMass(physicObject, mass);

		physicObject.setShape(new EdgeShape());
		checkMass(physicObject, mass);

		float newMass = 3.0f;
		physicObject.setMass(newMass);
		checkMass(physicObject, newMass);
	}

	private void checkMass(PhysicObject physicObject, float expectedMass) {
		assertEquals(expectedMass, physicObject.mass);
		assertEquals(expectedMass, getBody(physicObject).getMass());
	}
}
