package at.tugraz.ist.catroid.test.physics;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.physics.PhysicWorldSetting;

import com.badlogic.gdx.physics.box2d.World;

public class PhysicWorldTest extends AndroidTestCase {

	private World world;

	@Override
	public void setUp() {
		world = new World(PhysicWorldSetting.defaultgravity, PhysicWorldSetting.ignoreSleepingObjects);
	}

	@Override
	public void tearDown() {
		world = null;
	}

}
