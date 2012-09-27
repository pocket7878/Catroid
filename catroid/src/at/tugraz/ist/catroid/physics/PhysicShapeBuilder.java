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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

		Vector2[] vec = new Vector2[convexGrahamPoints.size()];

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

		Shape[] shapes2 = devideShape(vec);

		shapes.put(key, shapes2);

		return shapes2;
	}

	private Shape[] devideShape(Vector2[] convexpoints) {

		if (convexpoints.length < 9) {
			List<Vector2> x = Arrays.asList(convexpoints);
			Collections.reverse(x);

			PolygonShape polygon = new PolygonShape();
			polygon.set(x.toArray(new Vector2[x.size()]));
			return new Shape[] { polygon };
		}

		List<Shape> shapes = new ArrayList<Shape>(convexpoints.length / 6 + 1);
		List<Vector2> pointsPerShape = new ArrayList<Vector2>(8);

		Vector2 rome = convexpoints[0];
		int index = 1;
		while (index < convexpoints.length - 1) {
			int k = index + 7;

			int remainingPointsCount = convexpoints.length - index;
			if (remainingPointsCount > 7 && remainingPointsCount < 9) {
				k -= 3;
			}

			pointsPerShape.add(rome);
			for (; index < k && index < convexpoints.length; index++) {
				pointsPerShape.add(convexpoints[index]);
			}

			if (index < convexpoints.length) {
				index--;
			}
			Collections.reverse(pointsPerShape);

			PolygonShape polygon = new PolygonShape();
			polygon.set(pointsPerShape.toArray(new Vector2[pointsPerShape.size()]));
			shapes.add(polygon);

			pointsPerShape.clear();
		}

		return shapes.toArray(new Shape[shapes.size()]);
	}
}
