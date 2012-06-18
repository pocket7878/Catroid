package at.tugraz.ist.catroid.physics;

import com.badlogic.gdx.math.Vector2;

public final class PhysicWorldSetting {

	protected final static float timeStep = 1.0f / 30.0f;
	protected final static int defaultmass = 1;
	protected final static boolean surroundingBox = true;
	protected final static int surroundingBoxFrameWidth = 6;
	public final static Vector2 defaultgravity = new Vector2(0, -10);
	public final static boolean ignoreSleepingObjects = false;
	public final static int velocityIterations = 10;
	public final static int positionIterations = 10;

}
