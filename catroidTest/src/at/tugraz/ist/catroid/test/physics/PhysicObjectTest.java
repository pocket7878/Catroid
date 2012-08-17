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

	private Body getBody(PhysicObject physicObject) {
		return (Body) TestUtils.getPrivateField("body", physicObject, false);
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

	public void testAngle() {
		PhysicObject physicObject = createPhysicObject();
		assertEquals(0.0f, getBody(physicObject).getAngle());

		float[] degrees = { 45.0f, 66.66f, -90.0f, 500.0f };

		for (float angle : degrees) {
			float radian = PhysicWorldConverter.angleCatToBox2d(angle);
			physicObject.setAngle(radian);
			assertEquals(radian, getBody(physicObject).getAngle());
		}
	}

	public void testPosition() {
		PhysicObject physicObject = createPhysicObject();
		assertEquals(new Vector2(), getBody(physicObject).getPosition());

		Vector2[] positions = { new Vector2(12.34f, 56.78f), new Vector2(-87.65f, -43.21f) };
		for (Vector2 position : positions) {
			physicObject.setPosition(position.x, position.y);
			assertEquals(position, getBody(physicObject).getPosition());
		}
	}

	public void testAngleAndPosition() {
		PhysicObject physicObject = createPhysicObject();
		assertEquals(0.0f, getBody(physicObject).getAngle());
		assertEquals(new Vector2(), getBody(physicObject).getPosition());

		float angle = PhysicWorldConverter.angleCatToBox2d(13.56f);
		Vector2 position = new Vector2(12.34f, 56.78f);
		physicObject.setAngle(angle);
		physicObject.setPosition(position.x, position.y);

		assertEquals(angle, getBody(physicObject).getAngle());
		assertEquals(position, getBody(physicObject).getPosition());
	}

	public void testDensity() {
		PhysicObject physicObject = createPhysicObject();
		physicObject.setType(Type.DYNAMIC);
		PolygonShape shape = new PolygonShape();
		physicObject.setShape(shape);
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
		assertEquals(1.0f, getBody(physicObject).getMass());

		float[] masses = { 0.01f, 1.0f, 123456.0f };
		for (float mass : masses) {
			physicObject.setMass(mass);
			assertEquals(mass, getBody(physicObject).getMass());
		}

		float[] massesResetedToZero = { 0.0f, -0.123f, -123.456f };
		for (float mass : massesResetedToZero) {
			physicObject.setMass(mass);
			assertEquals(1.0f, getBody(physicObject).getMass());
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
		assertTrue(getBody(physicObject).getFixtureList().size() == 1);

		Shape shape = getBody(physicObject).getFixtureList().get(0).getShape();
		assertEquals(Shape.Type.Polygon, shape.getType());
		assertEquals(1, shape.getChildCount());
		assertEquals(0.01f, shape.getRadius());

		PolygonShape polygonShape = new PolygonShape();
		assertEquals(0, polygonShape.getVertexCount());

		// TODO: Much more testing coming soon!
	}
}
