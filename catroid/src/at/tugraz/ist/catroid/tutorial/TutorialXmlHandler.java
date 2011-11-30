/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.tutorial;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;
import at.tugraz.ist.catroid.R;

/**
 * @author faxxe
 * 
 */
public class TutorialXmlHandler {
	private static final boolean DEBUG = false;
	//private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	//private DocumentBuilder db;
	//private Document doc;
	private Context context;
	private ArrayList<String> actions = new ArrayList<String>();
	private ArrayList<String> attribute = new ArrayList<String>();
	ArrayList[] code = new ArrayList[] { actions, attribute };
	//private int i = 0;
	private int id = 0;
	private String name = "";

	public ArrayList[] getActions() {
		return code;
	}

	public TutorialXmlHandler(Context con) {
		this.context = con;
		Activity act = (Activity) con;
		name = act.getLocalClassName();
		doXmlThing();
	}

	public void startFrom(TutorialState state) {
	}

	public void doXmlThing() {
		Log.i("faxxe", "Activity: " + name);
		if (0 == name.compareTo("ui.MainMenuActivity")) {
			if (DEBUG) {
				id = R.xml.mainmenututorial_debug;
			} else {
				id = R.xml.mainmenututorial;
			}
		} else if (0 == name.compareTo("ui.ProjectActivity")) {
			//inflate for more xmls.. switch/case?
			if (DEBUG) {
				id = R.xml.projecttutorial_debug;
			} else {
				id = R.xml.projecttutorial;
			}
		} else if (0 == name.compareTo("ui.ScriptActivity")) {
			if (DEBUG) {
				id = R.xml.scripttab_debug;
			} else {
				id = R.xml.scripttab;
			}
		} else if (0 == name.compareTo("ui.CostumeActivity")) {
			if (DEBUG) {
				id = R.xml.costume_debug;
			} else {
				id = R.xml.costume;
			}
		} else {
			id = 0;
		}

		XmlResourceParser xpp = context.getResources().getXml(id);

		try {
			//xpp.next();
			Log.d("faxxe", " " + xpp.getEventType());
			int eventType = xpp.getEventType();
			xpp.next();
			xpp.next();
			StringBuffer stringBuffer = new StringBuffer();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					stringBuffer.append("\nStart_Tag: " + xpp.getName());
					actions.add(xpp.getName());
				} else if (eventType == XmlPullParser.TEXT) {
					stringBuffer.append("\nTEXT: " + xpp.getText());
					attribute.add(xpp.getText());
				}
				eventType = xpp.next();
			}
			//Log.d("faxxe", stringBuffer.toString());

		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
