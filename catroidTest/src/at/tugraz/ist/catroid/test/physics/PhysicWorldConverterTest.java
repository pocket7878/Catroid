package at.tugraz.ist.catroid.test.physics;

import junit.framework.Assert;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.physics.PhysicSettings;
import at.tugraz.ist.catroid.physics.PhysicWorldConverter;

import com.badlogic.gdx.math.Vector2;

public class PhysicWorldConverterTest extends AndroidTestCase {

	private float ratio = PhysicSettings.World.RATIO;

	@Override
	public void setUp() {
	}

	@Override
	public void tearDown() {
	}

	public void testAngleConversion() {
		float angle = 0.0f;
		Assert.assertEquals(angle, PhysicWorldConverter.angleBox2dToCat(angle));
		Assert.assertEquals(angle, PhysicWorldConverter.angleCatToBox2d(angle));

		Assert.assertEquals((float) (Math.PI / 2.0), PhysicWorldConverter.angleCatToBox2d(90.0f));
		Assert.assertEquals((float) Math.PI, PhysicWorldConverter.angleCatToBox2d(180.0f));
		Assert.assertEquals(90.0f, PhysicWorldConverter.angleBox2dToCat((float) (Math.PI / 2.0)));
		Assert.assertEquals(180.0f, PhysicWorldConverter.angleBox2dToCat((float) Math.PI));

		float[] angles = { 123.456f, -123.456f, 1024.0f };
		for (float currentAngle : angles) {
			Assert.assertEquals((float) Math.toDegrees(currentAngle),
					PhysicWorldConverter.angleBox2dToCat(currentAngle));
			Assert.assertEquals((float) Math.toRadians(currentAngle),
					PhysicWorldConverter.angleCatToBox2d(currentAngle));
		}
	}

	public void testLengthConversion() {
		float length = 0.0f;
		Assert.assertEquals(length, PhysicWorldConverter.lengthBox2dToCat(length));
		Assert.assertEquals(length, PhysicWorldConverter.lengthCatToBox2d(length));

		float[] lengths = { 123.456f, -654.321f };
		for (float currentLength : lengths) {
			Assert.assertEquals(currentLength * ratio, PhysicWorldConverter.lengthBox2dToCat(currentLength));
			Assert.assertEquals(currentLength / ratio, PhysicWorldConverter.lengthCatToBox2d(currentLength));
		}
	}

	public void testVectorConversation() {
		Vector2 vector = new Vector2();
		Assert.assertEquals(vector, PhysicWorldConverter.vecBox2dToCat(vector));
		Assert.assertEquals(vector, PhysicWorldConverter.vecCatToBox2d(vector));

		Vector2[] vectors = { new Vector2(123.456f, 123.456f), new Vector2(654.321f, -123.456f),
				new Vector2(-654.321f, 0.0f), new Vector2(-123.456f, -654.321f) };

		Vector2 expected;
		for (Vector2 currentVector : vectors) {
			expected = new Vector2(currentVector.x * ratio, currentVector.y * ratio);
			Assert.assertEquals(expected, PhysicWorldConverter.vecBox2dToCat(currentVector));

			expected = new Vector2(currentVector.x / ratio, currentVector.y / ratio);
			Assert.assertEquals(expected, PhysicWorldConverter.vecCatToBox2d(currentVector));
		}
	}
}
