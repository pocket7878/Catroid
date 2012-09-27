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
import java.util.List;
import java.util.Map;

import at.tugraz.ist.catroid.common.CostumeData;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class PhysicShapeBuilder {

	private final Map<String, Shape[]> shapes;
	float width;
	float height;

	public PhysicShapeBuilder() {
		shapes = new HashMap<String, Shape[]>();
	}

	public Shape[] getShape(CostumeData costumeData, float scaleFactor) {

		int scale = (int) (scaleFactor * 10);
		String key = costumeData.getChecksum() + scale;

		if (shapes.containsKey(key)) {
			return shapes.get(key);
		}

		List<Pixel> convexGrahamPoints = ImageProcessor.getShape(costumeData.getAbsolutePath());

		int[] size = costumeData.getResolution();
		width = size[0];
		height = size[1];

		Vector2[] vec = new Vector2[convexGrahamPoints.size() + 1];

		for (int i = 0; i < convexGrahamPoints.size(); i++) {
			Pixel pixel = convexGrahamPoints.get(i);
			vec[i] = PhysicWorldConverter.vecCatToBox2d(new Vector2((float) pixel.x() - (width / 2), height
					- (float) pixel.y() - height / 2));
		}

		Vector2[] x = new Vector2[ImageProcessor.points.size()];
		for (int index = 0; index < x.length; index++) {
			Pixel pixel = ImageProcessor.points.get(index);
			x[index] = PhysicWorldConverter.vecCatToBox2d(new Vector2((float) pixel.x() - (width / 2), height
					- (float) pixel.y() - height / 2));
		}

		//PhysicRenderer.getInstance().shapes.add(x);
		//PhysicRenderer.getInstance().shapes.add(vec);

		vec[convexGrahamPoints.size()] = vec[0];

		Shape[] shapes2 = devideShape(vec, getCenter(vec));

		shapes.put(key, shapes2);

		return shapes2;
	}

	private Vector2 getCenter(Vector2[] vertices) {
		Vector2 min = new Vector2(vertices[0]);
		Vector2 max = new Vector2(vertices[0]);
		for (Vector2 vertex : vertices) {
			if (vertex.x < min.x) {
				min.x = vertex.x;
			} else if (vertex.x > max.x) {
				max.x = vertex.x;
			}

			if (vertex.y < min.y) {
				min.y = vertex.y;
			} else if (vertex.y > max.y) {
				max.y = vertex.y;
			}
		}

		return new Vector2((max.x + min.x) / 2.0f, (max.y + min.y) / 2.0f);
	}

	private Shape[] devideShape(Vector2[] convexpoints, Vector2 center) {

		int size = convexpoints.length / 6;
		if (convexpoints.length % 6 > 0) {
			size += 1;
		}

		Shape[] shapes = new Shape[size];

		Vector2 start = convexpoints[0];
		PolygonShape tempshape;
		int containersize = 8;
		Vector2[] tempVertices;

		for (int j = 0; j < convexpoints.length; j += 6) {

			tempshape = new PolygonShape();
			int i = 0;

			if (convexpoints.length - j > 5) {
				tempVertices = new Vector2[containersize];
				i = 7;

			} else {
				tempVertices = new Vector2[convexpoints.length - j + 1];
				i = convexpoints.length - j;

				if (i < 2) {
					Shape[] shapesSizeMinusOne = new Shape[shapes.length - 1];
					for (int index = 0; index < shapesSizeMinusOne.length; index++) {
						shapesSizeMinusOne[index] = shapes[index];
					}
					shapes = shapesSizeMinusOne;
					break;
				}
			}

			tempVertices[0] = start;
			int k;
			for (k = 1; k < i; k++) {
				tempVertices[k] = convexpoints[j + k];
			}
			tempVertices[k] = center;

			start = tempVertices[k - 1];

			Vector2[] reverse = new Vector2[tempVertices.length];
			int l = tempVertices.length - 1;
			for (Vector2 vector : tempVertices) {
				reverse[l] = vector;
				l--;
			}

			tempshape.set(reverse);
			shapes[j / 6] = tempshape;
			PhysicRenderer.instance.shapes.add((PolygonShape) shapes[0]);
		}
		return shapes;
	}

}
