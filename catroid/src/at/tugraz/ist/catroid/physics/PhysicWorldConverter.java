package at.tugraz.ist.catroid.physics;

import com.badlogic.gdx.math.Vector2;

class PhysicWorldConverter {

	// Ratio of pixels to meters
	private static float RATIO = 40;

	public static Vector2 VectorFromCatroidToBox2D(Vector2 catroidVec) {
		return new Vector2(catroidVec.x / RATIO, catroidVec.y / RATIO);
	}

	public static Vector2 CoordsFromBox2DToCatroid(float x, float y) {
		Vector2 coords = new Vector2(x * RATIO, y * RATIO);

		return coords;
	}

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
