package at.tugraz.ist.catdroid.hid.server.robots;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class RobotMapper {

	public static final byte STRG = 0x01;
	public static final byte SHIFT_L = 0x02;
	public static final byte SHIFT_R = 0x20;
	public static final byte ALT_L = 0x04;
	public static final byte ALT_R = 0x40;

	private Properties prop;

	public RobotMapper() throws IOException {
		prop = new Properties();
		prop.load(new FileInputStream(new File("keys.properties")));
	}

	public Integer lookup(byte hid) {
		String s = "" + hid;
		if (prop.containsKey(s))
			return Integer.parseInt(prop.getProperty(s));
		else
			return null;
	}

	private void extractModifier(byte modifier, ArrayList<Integer> list) {
		byte strg = (byte) (modifier & STRG);
		byte shift_l = (byte) (modifier & SHIFT_L);
		byte shift_r = (byte) (modifier & SHIFT_R);
		byte alt_l = (byte) (modifier & ALT_L);
		byte alt_r = (byte) (modifier & ALT_R);
		
		if (strg == STRG) {
			list.add(KeyEvent.VK_CONTROL);
		}
		if (shift_l == SHIFT_L) {
			list.add(KeyEvent.VK_SHIFT);
		}
		if (shift_r == SHIFT_R) {
			list.add(KeyEvent.VK_SHIFT);
		}
		if (alt_l == ALT_L) {
			list.add(KeyEvent.VK_ALT);
		}
		if (alt_r == ALT_R) {
			list.add(KeyEvent.VK_ALT_GRAPH);
		}
	}

	public ArrayList<Integer> getKeyList(byte[] hid) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		Integer in = null;
		
		extractModifier(hid[2], list);
		
		for (int i = 4; i < hid.length; i++) {
			in = lookup(hid[i]);

			if (in != null) {
				list.add(in);
			}
		}

		return list;
	}
}
