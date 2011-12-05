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

import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;

import com.thoughtworks.xstream.XStream;

/**
 * @author gnu
 * 
 */
public class XmlHandler {
	private LessonCollection lessonCollection;

	XmlHandler(Context context) {
		XStream xstream = new XStream();
		AssetManager assetManager = context.getAssets();
		try {
			InputStream inputStream = assetManager.open("tutorial.xml");
			lessonCollection = (LessonCollection) xstream.fromXML(inputStream);
		} catch (Exception e) {
		}

		// Do no irgendwen vom Kernteam fragen, wieso XStream in allen Arrays den ersten 
		// Eintrag dupliziert.

		lessonCollection.cleanAfterXML();
	}

	LessonCollection getLessonCollection() {
		return (lessonCollection);
	}

}
