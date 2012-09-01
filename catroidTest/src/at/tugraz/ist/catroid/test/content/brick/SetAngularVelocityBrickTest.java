package at.tugraz.ist.catroid.test.content.brick;

import junit.framework.TestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetAngularVelocityBrick;
import at.tugraz.ist.catroid.physics.PhysicObject;
import at.tugraz.ist.catroid.physics.PhysicWorld;

public class SetAngularVelocityBrickTest extends TestCase {
	private float degrees = 3.50f;
	private PhysicWorld physicWorld;
	private Sprite sprite;
	private SetAngularVelocityBrick angularVelocityBrick;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = new Sprite("testSprite");
		physicWorld = new PhysicWorldMock();
		angularVelocityBrick = new SetAngularVelocityBrick(physicWorld, sprite, degrees);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		sprite = null;
		physicWorld = null;
		angularVelocityBrick = null;
	}

	public void testRequiredResources() {
		assertEquals(angularVelocityBrick.getRequiredResources(), Brick.NO_RESOURCES);
	}

	public void testGetSprite() {
		assertEquals(angularVelocityBrick.getSprite(), sprite);
	}

	public void testClone() {
		Brick clone = angularVelocityBrick.clone();
		assertEquals(angularVelocityBrick.getSprite(), clone.getSprite());
		assertEquals(angularVelocityBrick.getRequiredResources(), clone.getRequiredResources());
	}

	public void testExecution() {
		assertFalse(((PhysicWorldMock) physicWorld).wasExecuted());
		angularVelocityBrick.execute();
		assertTrue(((PhysicWorldMock) physicWorld).wasExecuted());
	}

	public void testNullSprite() {
		angularVelocityBrick = new SetAngularVelocityBrick(null, sprite, degrees);
		try {
			angularVelocityBrick.execute();
			fail("Execution of SetAngularVelocityBrick with null Sprite did not cause a "
					+ "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

	@SuppressWarnings("serial")
	class PhysicWorldMock extends PhysicWorld {

		private PhysicObjectMock phyMockObj;

		public PhysicWorldMock() {
			phyMockObj = new PhysicObjectMock();
		}

		public boolean wasExecuted() {
			return phyMockObj.wasExecuted();
		}

		@Override
		public PhysicObject getPhysicObject(Sprite sprite) {
			return phyMockObj;
		}
	}

	@SuppressWarnings("serial")
	class PhysicObjectMock extends PhysicObject {

		public boolean executed;

		public PhysicObjectMock() {
			super(null);
			executed = false;
		}

		@Override
		public void setAngularVelocity(float radian) {
			executed = true;
		}

		public boolean wasExecuted() {
			return executed;
		}

		@Override
		public void setType(Type type) {
		}

	}

}
