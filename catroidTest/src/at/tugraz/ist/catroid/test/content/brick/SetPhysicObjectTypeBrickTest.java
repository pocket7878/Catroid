package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetPhysicObjectTypeBrick;
import at.tugraz.ist.catroid.physics.PhysicObject;
import at.tugraz.ist.catroid.physics.PhysicWorld;

public class SetPhysicObjectTypeBrickTest extends AndroidTestCase {

	private PhysicObject.Type type;
	private PhysicWorld physicWorld;
	private Sprite sprite;
	private SetPhysicObjectTypeBrick setPhysicObjectTypeBrickTest;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = new Sprite("testSprite");
		physicWorld = new PhysicWorldMock();
		setPhysicObjectTypeBrickTest = new SetPhysicObjectTypeBrick(physicWorld, sprite, type);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		sprite = null;
		physicWorld = null;
		setPhysicObjectTypeBrickTest = null;
	}

	public void testRequiredResources() {
		assertEquals(setPhysicObjectTypeBrickTest.getRequiredResources(), Brick.NO_RESOURCES);
	}

	public void testGetSprite() {
		assertEquals(setPhysicObjectTypeBrickTest.getSprite(), sprite);
	}

	public void testClone() {
		Brick clone = setPhysicObjectTypeBrickTest.clone();
		assertEquals(setPhysicObjectTypeBrickTest.getSprite(), clone.getSprite());
		assertEquals(setPhysicObjectTypeBrickTest.getRequiredResources(), clone.getRequiredResources());
	}

	public void testExecution() {
		assertFalse(((PhysicWorldMock) physicWorld).wasExecuted());
		setPhysicObjectTypeBrickTest.execute();
		assertTrue(((PhysicWorldMock) physicWorld).wasExecuted());
	}

	public void testNullSprite() {
		setPhysicObjectTypeBrickTest = new SetPhysicObjectTypeBrick(null, sprite, type);
		try {
			setPhysicObjectTypeBrickTest.execute();
			fail("Execution of SetPhysicObjectTypeBrick with null Sprite did not cause a "
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

	class PhysicObjectMock extends PhysicObject {

		public boolean executed;

		public PhysicObjectMock() {
			super(null);
			executed = false;
		}

		@Override
		public void setType(Type type) {
			executed = true;
		}

		public boolean wasExecuted() {
			return executed;
		}
	}

}