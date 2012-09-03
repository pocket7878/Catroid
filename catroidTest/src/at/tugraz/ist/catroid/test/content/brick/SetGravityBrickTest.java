package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetGravityBrick;
import at.tugraz.ist.catroid.physics.PhysicWorld;

import com.badlogic.gdx.math.Vector2;

public class SetGravityBrickTest extends AndroidTestCase {

	private float xValue = 3.50f;
	private float yValue = 5.50f;
	private Vector2 gravity = new Vector2(xValue, yValue);
	private PhysicWorld physicWorld;
	private Sprite sprite;
	private SetGravityBrick setGtravityBrick;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = new Sprite("testSprite");
		physicWorld = new PhysicWorldMock();
		setGtravityBrick = new SetGravityBrick(physicWorld, sprite, gravity);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		sprite = null;
		physicWorld = null;
		setGtravityBrick = null;
	}

	public void testRequiredResources() {
		assertEquals(setGtravityBrick.getRequiredResources(), Brick.NO_RESOURCES);
	}

	public void testGetSprite() {
		assertEquals(setGtravityBrick.getSprite(), sprite);
	}

	public void testClone() {
		Brick clone = setGtravityBrick.clone();
		assertEquals(setGtravityBrick.getSprite(), clone.getSprite());
		assertEquals(setGtravityBrick.getRequiredResources(), clone.getRequiredResources());
	}

	public void testExecution() {
		assertFalse(((PhysicWorldMock) physicWorld).wasExecuted());
		setGtravityBrick.execute();
		assertTrue(((PhysicWorldMock) physicWorld).wasExecuted());
	}

	public void testNullSprite() {
		setGtravityBrick = new SetGravityBrick(null, sprite, gravity);
		try {
			setGtravityBrick.execute();
			fail("Execution of SetGravityBrick with null Sprite did not cause a " + "NullPointerException to be thrown");
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

		@Override
		public void setGravity(Sprite sprite, Vector2 v) {
			executed = true;
		}

	}
}
