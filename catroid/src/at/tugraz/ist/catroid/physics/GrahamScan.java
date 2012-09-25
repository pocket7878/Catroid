/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.physics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class GrahamScan {
	private static Stack<Pixel> hull;
	private static int N;

	public static List<Pixel> run(List<Pixel> pixel) {
		hull = new Stack<Pixel>();
		N = pixel.size();
		// preprocess so that points[0] has lowest y-coordinate; break ties by x-coordinate
		// points[0] is an extreme point of the convex hull
		// (alternatively, could do easily in linear time)
		Pixel[] pixelArray = pixel.toArray(new Pixel[N]);
		Arrays.sort(pixelArray);

		// sort by polar angle with respect to base point points[0],
		// breaking ties by distance to points[0]
		Arrays.sort(pixelArray, 1, N, pixelArray[0].POLAR_ORDER);

		hull.push(pixelArray[0]); // p[0] is first extreme point

		// find index k1 of first point not equal to points[0]
		int k1;
		for (k1 = 1; k1 < N; k1++) {
			if (!pixelArray[0].equals(pixelArray[k1])) {
				break;
			}
		}
		if (k1 == N) {
			return null; // all points equal
		}

		// find index k2 of first point not collinear with points[0] and points[k1]
		int k2;
		for (k2 = k1 + 1; k2 < N; k2++) {
			if (Pixel.ccw(pixelArray[0], pixelArray[k1], pixelArray[k2]) != 0) {
				break;
			}
		}
		hull.push(pixelArray[k2 - 1]); // points[k2-1] is second extreme point

		// Graham scan; note that points[N-1] is extreme point different from points[0]
		for (int i = k2; i < N; i++) {
			Pixel top = hull.pop();
			while (Pixel.ccw(hull.peek(), top, pixelArray[i]) <= 0) {
				top = hull.pop();
			}
			hull.push(top);
			hull.push(pixelArray[i]);
		}

		List<Pixel> list = new ArrayList<Pixel>();
		for (Pixel p : hull()) {
			list.add(p);
		}

		assert isConvex();

		return list;

	}

	// return extreme points on convex hull in counterclockwise order as an Iterable
	public static Iterable<Pixel> hull() {
		Stack<Pixel> s = new Stack<Pixel>();
		for (Pixel p : hull) {
			s.push(p);
		}
		return s;
	}

	// check that boundary of hull is strictly convex
	private static boolean isConvex() {
		int N = hull.size();
		if (N <= 2) {
			return true;
		}

		Pixel[] points = new Pixel[N];
		int n = 0;
		for (Pixel p : hull()) {
			points[n++] = p;
		}

		for (int i = 0; i < N; i++) {
			if (Pixel.ccw(points[i], points[(i + 1) % N], points[(i + 2) % N]) <= 0) {
				return false;
			}
		}
		return true;
	}

}
