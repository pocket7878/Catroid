package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetMassBrick;
import at.tugraz.ist.catroid.physics.PhysicWorld;

public class SetMassBrickTest extends AndroidTestCase {

	private float mass = 10.50f;
	private PhysicWorld physicWorld;
	private Sprite sprite;
	private SetMassBrick setMassBrick;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = new Sprite("testSprite");
		physicWorld = new PhysicWorldMock();
		setMassBrick = new SetMassBrick(physicWorld, sprite, mass);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		sprite = null;
		physicWorld = null;
		setMassBrick = null;
	}

	public void testRequiredResources() {
		assertEquals(setMassBrick.getRequiredResources(), Brick.NO_RESOURCES);
	}

	public void testGetSprite() {
		assertEquals(setMassBrick.getSprite(), sprite);
	}

	public void testClone() {
		Brick clone = setMassBrick.clone();
		assertEquals(setMassBrick.getSprite(), clone.getSprite());
		assertEquals(setMassBrick.getRequiredResources(), clone.getRequiredResources());
	}

	public void testExecution() {
		assertFalse(((PhysicWorldMock) physicWorld).wasExecuted());
		setMassBrick.execute();
		assertTrue(((PhysicWorldMock) physicWorld).wasExecuted());
	}

	public void testNullSprite() {
		setMassBrick = new SetMassBrick(null, sprite, mass);
		try {
			setMassBrick.execute();
			fail("Execution of ChangeXByBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

	@SuppressWarnings("serial")
	class PhysicWorldMock extends PhysicWorld {

		public boolean executed;

		public PhysicWorldMock() {
			executed = false;
		}

		public boolean wasExecuted() {
			return executed;
		}

		public void setMass(Sprite sprite, float mass) {
			executed = true;
		}

	}

}
