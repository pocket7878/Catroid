package at.tugraz.ist.catroid.test.content.brick;

import junit.framework.TestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetBounceFactorBrick;
import at.tugraz.ist.catroid.physics.PhysicObject;
import at.tugraz.ist.catroid.physics.PhysicWorld;
import at.tugraz.ist.catroid.test.utils.TestUtils;

public class SetBounceFactorBrickTest extends TestCase {
	private float bounceFactor = 35f;
	private PhysicWorld physicWorld;
	private Sprite sprite;
	private SetBounceFactorBrick bounceFactorBrick;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = new Sprite("testSprite");
		physicWorld = new PhysicWorldMock();
		bounceFactorBrick = new SetBounceFactorBrick(physicWorld, sprite, bounceFactor);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		sprite = null;
		physicWorld = null;
		bounceFactorBrick = null;
	}

	public void testRequiredResources() {
		assertEquals(bounceFactorBrick.getRequiredResources(), Brick.NO_RESOURCES);
	}

	public void testGetSprite() {
		assertEquals(bounceFactorBrick.getSprite(), sprite);
	}

	public void testClone() {
		Brick clone = bounceFactorBrick.clone();
		assertEquals(bounceFactorBrick.getSprite(), clone.getSprite());
		assertEquals(bounceFactorBrick.getRequiredResources(), clone.getRequiredResources());
		assertEquals(TestUtils.getPrivateField("bounceFactor", bounceFactorBrick, false),
				TestUtils.getPrivateField("bounceFactor", clone, false));
	}

	public void testExecution() {
		assertFalse(((PhysicWorldMock) physicWorld).wasExecuted());
		bounceFactorBrick.execute();
		assertTrue(((PhysicWorldMock) physicWorld).wasExecuted());
	}

	public void testNullSprite() {
		bounceFactorBrick = new SetBounceFactorBrick(null, sprite, bounceFactor);
		try {
			bounceFactorBrick.execute();
			fail("Execution of SetBounceFactorBrick with null Sprite did not cause a "
					+ "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

	public void testValue() {
		float physicObjectBounceFactor = (Float) TestUtils.getPrivateField("bounceFactor", bounceFactorBrick, false);
		assertEquals(bounceFactor, physicObjectBounceFactor);
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

	class PhysicObjectMock extends PhysicObject {

		public boolean executed;

		public PhysicObjectMock() {
			super(null);
			executed = false;
		}

		@Override
		public void setRestitution(float restitution) {
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