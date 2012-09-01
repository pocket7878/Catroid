package at.tugraz.ist.catroid.test.content.brick;

import junit.framework.TestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetFrictionBrick;
import at.tugraz.ist.catroid.physics.PhysicObject;
import at.tugraz.ist.catroid.physics.PhysicWorld;

public class SetFrictionBrickTest extends TestCase {
	private float friction = 1.0f;
	private PhysicWorld physicWorld;
	private Sprite sprite;
	private SetFrictionBrick frictionBrick;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = new Sprite("testSprite");
		physicWorld = new PhysicWorldMock();
		frictionBrick = new SetFrictionBrick(physicWorld, sprite, friction);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		sprite = null;
		physicWorld = null;
		frictionBrick = null;
	}

	public void testRequiredResources() {
		assertEquals(frictionBrick.getRequiredResources(), Brick.NO_RESOURCES);
	}

	public void testGetSprite() {
		assertEquals(frictionBrick.getSprite(), sprite);
	}

	public void testClone() {
		Brick clone = frictionBrick.clone();
		assertEquals(frictionBrick.getSprite(), clone.getSprite());
		assertEquals(frictionBrick.getRequiredResources(), clone.getRequiredResources());
	}

	public void testExecution() {
		assertFalse(((PhysicWorldMock) physicWorld).wasExecuted());
		frictionBrick.execute();
		assertTrue(((PhysicWorldMock) physicWorld).wasExecuted());
	}

	public void testNullSprite() {
		frictionBrick = new SetFrictionBrick(null, sprite, friction);
		try {
			frictionBrick.execute();
			fail("Execution of SetFrictionBrick with null Sprite did not cause a "
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
		public void setFriction(float friction) {
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
