package at.tugraz.ist.catroid.test.physics;

import junit.framework.TestCase;
import at.tugraz.ist.catroid.physics.PhysicThread;
import at.tugraz.ist.catroid.physics.PhysicWorld;

public class PhysicThreadTest extends TestCase {

	PhysicThread phyThread;
	PhysicWorld physicWorld;

	@Override
	protected void setUp() throws Exception {
		physicWorld = new PhysicWorld();
		phyThread = new PhysicThread(physicWorld);
	}

	@Override
	protected void tearDown() throws Exception {
		phyThread.finish();
		physicWorld = null;
		phyThread = null;
	}

	public void testActivity() {
		assertFalse(phyThread.isActive());
		assertFalse(phyThread.isRunning());

		phyThread.start();
		assertTrue(phyThread.isActive());
		assertTrue(phyThread.isRunning());

		phyThread.pause();
		assertTrue(phyThread.isActive());
		assertFalse(phyThread.isRunning());

		phyThread.resume();
		assertTrue(phyThread.isActive());
		assertTrue(phyThread.isRunning());
	}
}
