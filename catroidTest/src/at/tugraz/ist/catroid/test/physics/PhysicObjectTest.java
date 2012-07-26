package at.tugraz.ist.catroid.test.physics;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.physics.PhysicObject;
import at.tugraz.ist.catroid.physics.PhysicObjectMap;
import at.tugraz.ist.catroid.physics.PhysicSettings;
import at.tugraz.ist.catroid.physics.PhysicWorldConverter;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicObjectTest extends AndroidTestCase {
	static {
		GdxNativesLoader.load();
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

		try {
			objectWithNullBody.setType(BodyType.StaticBody);
			assertTrue(false);
		} catch (NullPointerException exception) {
			assertTrue(true);
		}
	}

	public void testAngle() {
		PhysicObject object = objects.get(new Sprite("TestSprite"));
		assertEquals(0.0f, object.getBody().getAngle());

		float angle = PhysicWorldConverter.angleCatToBox2d(45.0f);
		object.setAngle(angle);
		assertEquals(angle, object.getBody().getAngle());

		angle = PhysicWorldConverter.angleCatToBox2d(500.0f);
		object.setAngle(angle);
		assertEquals(angle, object.getBody().getAngle());

		angle = PhysicWorldConverter.angleCatToBox2d(-90.0f);
		object.setAngle(angle);
		assertEquals(angle, object.getBody().getAngle());
	}
}
