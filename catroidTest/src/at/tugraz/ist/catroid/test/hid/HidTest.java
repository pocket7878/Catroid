package at.tugraz.ist.catroid.test.hid;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.hid.HidBluetooth;

public class HidTest extends AndroidTestCase {

	private HidBluetooth hid;

	@Override
	protected void setUp() throws Exception {
		hid = HidBluetooth.getInstance();

	}

	/*
	 * public void testResources() {
	 * Integer keyCode = new Integer(0);
	 * System.out.println(keyCode.intValue());
	 * hid.interpretKey(keyCode, 0, 1);
	 * 
	 * //assertEquals("was isch los?", 4, keyCode.intValue());
	 * }
	 */

	public void testInterpretKey() {

		Integer keyCode = Integer.valueOf(0);
		int spinnerIndex = 3;
		int keyXmlId = 0x7f070008;

		boolean ret = hid.interpretKey(keyCode, spinnerIndex, keyXmlId);

		assertEquals("Key is no modifier", false, ret);
		assertEquals("Wrong keycode", 6, keyCode.intValue());

		keyCode = Integer.valueOf(0);
		spinnerIndex = 36;

		ret = hid.interpretKey(keyCode, spinnerIndex, keyXmlId);

		assertEquals("Key is modifier", true, ret);
		assertEquals("Wrong keycode", 224, keyCode.intValue());
	}
}
