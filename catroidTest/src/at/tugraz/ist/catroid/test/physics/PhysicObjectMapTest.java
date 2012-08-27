package at.tugraz.ist.catroid.test.physics;

import java.util.Map;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.physics.PhysicObject;
import at.tugraz.ist.catroid.physics.PhysicObjectMap;
import at.tugraz.ist.catroid.physics.PhysicSettings;
import at.tugraz.ist.catroid.test.utils.TestUtils;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicObjectMapTest extends AndroidTestCase {
	static {
		GdxNativesLoader.load();
	}

	public void testMap() {
		PhysicObjectMap objects = new PhysicObjectMap(new World(PhysicSettings.World.DEFAULT_GRAVITY,
				PhysicSettings.World.IGNORE_SLEEPING_OBJECTS));
		@SuppressWarnings("unchecked")
		Map<Sprite, PhysicObject> objectMap = (Map<Sprite, PhysicObject>) TestUtils.getPrivateField("objects", objects,
				false);

		assertNotNull(objectMap);
		assertTrue(objectMap.isEmpty());

		Sprite sprite = new Sprite("TestSprite");
		PhysicObject physicObject = objects.get(sprite);

		assertEquals(1, objectMap.size());
		assertTrue(objectMap.containsKey(sprite));
		assertTrue(objectMap.containsValue(physicObject));
		assertEquals(physicObject, objects.get(sprite));

		objects.get(new Sprite("TestSprite"));
		assertEquals(2, objectMap.size());
	}
}
