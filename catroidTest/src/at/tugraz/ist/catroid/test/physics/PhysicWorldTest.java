package at.tugraz.ist.catroid.test.physics;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.physics.PhysicSettings;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicWorldTest extends AndroidTestCase {
	// TODO: Test it!
	static {
		GdxNativesLoader.load();
	}

	private World world;

	@Override
	public void setUp() {
		world = new World(PhysicSettings.World.DEFAULT_GRAVITY, PhysicSettings.World.IGNORE_SLEEPING_OBJECTS);
	}

	@Override
	public void tearDown() {
		world = null;
	}

}
