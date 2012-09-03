package at.tugraz.ist.catroid.test.physics;

import java.util.List;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.physics.PhysicObject;
import at.tugraz.ist.catroid.physics.PhysicObject.Type;
import at.tugraz.ist.catroid.physics.PhysicObjectMap;
import at.tugraz.ist.catroid.physics.PhysicSettings;
import at.tugraz.ist.catroid.test.utils.TestUtils;

import com.badlogic.gdx.physics.box2d.Body;
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

		assertEquals(PhysicSettings.Object.DEFAULT_DENSITY, fixtureDef.density);
		assertEquals(PhysicSettings.Object.DEFAULT_FRICTION, fixtureDef.friction);
		assertEquals(PhysicSettings.Object.DEFAULT_RESTITUTION, fixtureDef.restitution);
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

}
