package at.tugraz.ist.catroid.test.hid;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.hid.HidBluetooth;
import at.tugraz.ist.catroid.hid.KeyCode;

public class HidTest extends AndroidTestCase {

	private HidBluetooth hid;

	@Override
	protected void setUp() throws Exception {
		hid = HidBluetooth.getInstance();

	}

	public void testInterpretKey() {
		Context context = getContext();
		KeyCode key = hid.interpretKey(context, 0, R.array.key_code_array);
		assertEquals("Test 'a'-key", 4, key.getKeyCode());
		assertEquals("test 'a'-key no modifier", false, key.isModifier());

		key = hid.interpretKey(context, 40, R.array.key_code_array);
		assertEquals("Test 'ALT_RIGHT'-key", 230, key.getKeyCode());
		assertEquals("test 'ALT_RIGHT'-key is modifier", true, key.isModifier());

	}

	public void testGenerateHidCode() {
		//Context context = getContext();
		//KeyCode interpretValueKey = hid.interpretKey(context, 0, R.array.key_code_array);
		//KeyCode interpretModifierKey = hid.interpretKey(context, 40, R.array.key_code_array);

		KeyCode valueKey = new KeyCode(false, 4);
		KeyCode modifierKey = new KeyCode(true, 230);

		Collection<KeyCode> keyList = new ArrayList<KeyCode>();
		keyList.add(valueKey);
		keyList.add(modifierKey);
		int[] returnArr = hid.generateHidCode(keyList);
		assertEquals("Test index 0 value", 161, returnArr[0]);
		assertEquals("Test modifier value", 230, returnArr[2]);
		assertEquals("Test key value", 4, returnArr[4]);

		KeyCode valueKey1 = new KeyCode(false, 8);
		KeyCode modifierKey1 = new KeyCode(true, 230);
		KeyCode modifierKey2 = new KeyCode(true, 224);

		keyList = new ArrayList<KeyCode>();
		keyList.add(valueKey1);
		keyList.add(modifierKey1);
		keyList.add(modifierKey2);

		returnArr = hid.generateHidCode(keyList);
		assertEquals("Test index 0 value", 161, returnArr[0]);
		assertEquals("Test modifier value", 230 | 224, returnArr[2]);
		assertEquals("Test key value", 8, returnArr[4]);

		keyList = new ArrayList<KeyCode>();
		keyList.add(new KeyCode(false, 5));
		keyList.add(new KeyCode(false, 8));
		keyList.add(new KeyCode(false, 16));
		keyList.add(new KeyCode(false, 27));
		keyList.add(new KeyCode(false, 30));
		keyList.add(new KeyCode(false, 39));
		keyList.add(new KeyCode(false, 6));
		keyList.add(new KeyCode(false, 9));
		keyList.add(new KeyCode(false, 9));

		returnArr = hid.generateHidCode(keyList);
		assertEquals("Test index 0 value", 161, returnArr[0]);
		assertEquals("Test index 1 value", 1, returnArr[1]);
		assertEquals("Test index 4 value", 5, returnArr[4]);
		assertEquals("Test index 5 value", 8, returnArr[5]);
		assertEquals("Test index 6 value", 16, returnArr[6]);
		assertEquals("Test index 7 value", 27, returnArr[7]);
		assertEquals("Test index 8 value", 30, returnArr[8]);
		assertEquals("Test index 9 value", 39, returnArr[9]);

	}
}
