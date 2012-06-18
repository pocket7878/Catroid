package at.tugraz.ist.catroid.physics;

import com.badlogic.gdx.math.Vector2;

class PhysicWorldConverter {

	// Ratio of pixels to meters
	static float RATIO = 40;

	public static float LengthFromCatroidToBox2D(float x) {
		return x / RATIO;
	}

	public static Vector2 Vector2FromCatroidToBox2D(Vector2 x) {
		return new Vector2(x.x / RATIO, x.y / RATIO);
	}

	public static Vector2 Vector2FromBox2DToCatroid(Vector2 x) {
		return new Vector2(x.x * RATIO, x.y * RATIO);
	}

}
