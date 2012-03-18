package at.tugraz.ist.catroid.test.physics;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.physics.PhysicWorld;

public class PhysicWorldTest extends AndroidTestCase {

	public static void SingletonTest() {

		assertEquals(PhysicWorld.getInstance(), PhysicWorld.getInstance());
		assertTrue(PhysicWorld.getInstance().isActive());
		PhysicWorld.getInstance().clearWorld();
		assertFalse(PhysicWorld.getInstance().isActive());
		assertTrue(PhysicWorld.getInstance().isActive());
	}
}
