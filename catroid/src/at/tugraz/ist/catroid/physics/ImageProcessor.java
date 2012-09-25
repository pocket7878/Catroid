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
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;

public class ImageProcessor {

	public static List<Pixel> points;
	private static Pixmap pixmap;

	public static List<Pixel> getShape(String filePath) {
		points = new ArrayList<Pixel>();
		pixmap = new Pixmap(Gdx.files.absolute(filePath));
		proceed();
		List<Pixel> convexpoints = GrahamScan.run(points);
		return convexpoints;
	}

	public static int proceed() {
		if (pixmap != null) {
			for (int cx = 0; cx < pixmap.getWidth(); cx++) {
				for (int cy = 0; cy < pixmap.getHeight(); cy += 2) {
					if (isBoundary(cx, cy)) {
						traverseBoundary(cx, cy);
						return 0;
					}
				}
			}
		} else {
			System.out.println("IMAGEPROCESSOR FAILD - no Image to proceed");
		}
		return 0;
	}

	public static boolean isBoundary(int x, int y) {
		// If this point doesn't have alpha we're not interested
		if (!pointHasAlpha(x, y)) {
			return false;
		}
		// Look at the 8 adjoining points, if at least one has alpha then 
		// it's a boundary
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (!pointHasAlpha(x + i, y + j)) {
					return true;
				}
			}
		}

		// If this point has alpha and all of the adjoining points
		// have alpha then it's not a boundary
		return false;
	}

	public static boolean pointHasAlpha(int x, int y) {
		if (x < 0 || x > (pixmap.getWidth() - 1) || y < 0 || y > (pixmap.getHeight() - 1)) {
			return false;
		} else {
			if (hasAlpha(x, y)) {
				return true;
			}
			return false;
		}
	}

	public static boolean hasAlpha(int x, int y) {
		int pixel = pixmap.getPixel(x, y);
		int alpha = pixel & 0xff;

		if (alpha > 0) {
			return true;
		}
		return false;
	}

	public static void traverseBoundary(int x, int y) {
		Pixel start = new Pixel(x, y);
		Pixel next = start;
		Pixel old = new Pixel(-1, -1);
		points.add(start);
		// Set arbitary limit to length of circumference
		int scope = (pixmap.getWidth() + pixmap.getHeight()) * 3;
		// Abbruchbedingung Winkel -----   ????
		for (int i = 0; i < 100000; i++) {
			// Save off the last position
			old = next;

			// Find the next clockwise boundary pixel
			next = findNextBounearyPoint(next);

			// Set the search start point as the previous pixel
			// to avoid loops and continue traversing the shape
			// in the same direction i.e.
			// edge:  x  first point     second point  34x
			//       x            23x                  2x
			//      x             1x                   1
			// In this diagram the numbers represent the order
			// in which the neighboring points are tested. We
			// Always start with the previous point found.
			next.setNSP(old.sub(next));
			// When we get back to the start break the loop 
			if (next.equals(start)) {
				break;
			}
			points.add(next);
		}
	}

	public static Pixel findNextBounearyPoint(Pixel p) {
		// Move clockwise around from previous point until we
		// hit another boundary cell
		for (int i = 0; i < 9; i++) {
			// Find the next pixel clockwise - the pixel remembers the last
			// position tested and will increment to the next position
			Pixel next = p.clockwise();
			if (isBoundary(next.x, next.y)) {
				return next;
			}
		}
		System.out.println("findNextBoundaryPoint :" + "Error next point not found");
		return null;

	}

}
