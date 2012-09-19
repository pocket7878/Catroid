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
package at.tugraz.ist.catroid.physics;

import java.util.HashMap;
import java.util.Map;

import at.tugraz.ist.catroid.common.CostumeData;

import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * @author robert
 * 
 */
public class PhysicShapeBuilder {

	private final Map<String, Shape[]> shapes;

	public PhysicShapeBuilder() {
		shapes = new HashMap<String, Shape[]>();
	}

	public Shape[] getShape(CostumeData costumeData, float scaleFactor) {
		int scale = (int) (scaleFactor * 10);
		String key = costumeData.getChecksum() + scale;

		if (shapes.containsKey(key)) {
			return shapes.get(key);
		}

		int[] imageResolution = costumeData.getResolution();
		float width = PhysicWorldConverter.lengthCatToBox2d(imageResolution[0] * scaleFactor);
		float height = PhysicWorldConverter.lengthCatToBox2d(imageResolution[1] * scaleFactor);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2f, height / 2f);

		Shape[] shapeArray = new Shape[] { shape };
		shapes.put(key, shapeArray);
		return shapeArray;
	}
}
