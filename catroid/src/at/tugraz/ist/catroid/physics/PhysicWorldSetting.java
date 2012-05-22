package at.tugraz.ist.catroid.physics;

import com.badlogic.gdx.math.Vector2;

public final class PhysicWorldSetting {

	protected final static float timeStep = 1.0f / 60.0f;
	protected final static int velocityIterations = 5;
	protected final static int positionIterations = 5;
	protected final static int defaultmass = 10;
	protected final static Vector2 defaultgravity = new Vector2(0, -10);
	protected final static boolean ignoreSleepingObjects = false;
}
