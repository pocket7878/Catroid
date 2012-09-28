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

import java.util.Comparator;

import com.badlogic.gdx.math.Vector2;

public class Pixel implements Comparable<Pixel> {
	public static final Comparator<Pixel> X_ORDER = new XOrder();
	public static final Comparator<Pixel> Y_ORDER = new YOrder();
	public static final Comparator<Pixel> R_ORDER = new ROrder();

	public final Comparator<Pixel> POLAR_ORDER = new PolarOrder();
	public final Comparator<Pixel> ATAN2_ORDER = new Atan2Order();
	public final Comparator<Pixel> DISTANCE_TO_ORDER = new DistanceToOrder();

	public static final int RANGE = 1;
	public static final int NORTH = RANGE;
	public static final int SOUTH = -RANGE;
	public static final int EAST = RANGE;
	public static final int WEST = -RANGE;
	public static final int CENTRE = 0;
	public int x;
	public int y;
	private int nspX = -RANGE;
	private int nspY = 0;

	public Pixel(int x, int y) {
		this.x = x;
		this.y = y;
	}

	// return the x-coorindate of this point
	public double x() {
		return x;
	}

	// return the y-coorindate of this point
	public double y() {
		return y;
	}

	public Pixel add(Pixel p) {
		return new Pixel(x + p.x, y + p.y);
	}

	public Pixel sub(Pixel p) {
		return new Pixel(x - p.x, y - p.y);
	}

	public Vector2 toVec2() {
		return new Vector2(x, y);
	}

	public void setNSP(Pixel p) {
		nspX = p.x;
		nspY = p.y;
	}

	public Pixel clockwise() {
		Pixel nsp = clockwise(nspX, nspY);
		setNSP(nsp);
		return add(nsp);
	}

	public Pixel clockwise(int x, int y) {
		return clockwise(new Pixel(x, y));
	}

	public Pixel clockwise(Pixel sp) {
		if (sp.x == WEST) {
			if (sp.y == SOUTH) {
				return new Pixel(WEST, CENTRE);
			} else if (sp.y == CENTRE) {
				return new Pixel(WEST, NORTH);
			} else if (sp.y == NORTH) {
				return new Pixel(CENTRE, NORTH);
			}
		}
		if (sp.x == CENTRE) {
			if (sp.y == SOUTH) {
				return new Pixel(WEST, SOUTH);
			} else if (sp.y == NORTH) {
				return new Pixel(EAST, NORTH);
			}
		}
		if (sp.x == EAST) {
			if (sp.y == SOUTH) {
				return new Pixel(CENTRE, SOUTH);
			} else if (sp.y == CENTRE) {
				return new Pixel(EAST, SOUTH);
			} else if (sp.y == NORTH) {
				return new Pixel(EAST, CENTRE);
			}
		}
		return null;

	}

	public boolean equals(Pixel p) {
		if (x == p.x && y == p.y) {
			return true;
		}
		return false;
	}

	// return the radius of this point in polar coordinates
	public double r() {
		return Math.sqrt(x * x + y * y);
	}

	// return the angle of this point in polar coordinates
	// (between -pi/2 and pi/2)
	public double theta() {
		return Math.atan2(y, x);
	}

	// return the polar angle between this point and that point (between -pi and pi);
	// (0 if two points are equal)
	private double angleTo(Pixel that) {
		double dx = that.x - this.x;
		double dy = that.y - this.y;
		return Math.atan2(dy, dx);
	}

	// is a->b->c a counter-clockwise turn?
	// -1 if clockwise, +1 if counter-clockwise, 0 if collinear
	public static int ccw(Pixel a, Pixel b, Pixel c) {
		double area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
		if (area2 < 0) {
			return -1;
		} else if (area2 > 0) {
			return +1;
		} else {
			return 0;
		}
	}

	// twice signed area of a-b-c
	public static double area2(Pixel a, Pixel b, Pixel c) {
		return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
	}

	// return Euclidean distance between this point and that point
	public double distanceTo(Pixel that) {
		double dx = this.x - that.x;
		double dy = this.y - that.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	// return square of Euclidean distance between this point and that point
	public double distanceSquaredTo(Pixel that) {
		double dx = this.x - that.x;
		double dy = this.y - that.y;
		return dx * dx + dy * dy;
	}

	// compare by y-coordinate, breaking ties by x-coordinate
	@Override
	public int compareTo(Pixel that) {
		if (this.y < that.y) {
			return -1;
		}
		if (this.y > that.y) {
			return +1;
		}
		if (this.x < that.x) {
			return -1;
		}
		if (this.x > that.x) {
			return +1;
		}
		return 0;
	}

	// compare points according to their x-coordinate
	private static class XOrder implements Comparator<Pixel> {
		@Override
		public int compare(Pixel p, Pixel q) {
			if (p.x < q.x) {
				return -1;
			}
			if (p.x > q.x) {
				return +1;
			}
			return 0;
		}
	}

	// compare points according to their y-coordinate
	private static class YOrder implements Comparator<Pixel> {
		@Override
		public int compare(Pixel p, Pixel q) {
			if (p.y < q.y) {
				return -1;
			}
			if (p.y > q.y) {
				return +1;
			}
			return 0;
		}
	}

	// compare points according to their polar radius
	private static class ROrder implements Comparator<Pixel> {
		@Override
		public int compare(Pixel p, Pixel q) {
			double delta = (p.x * p.x + p.y * p.y) - (q.x * q.x + q.y * q.y);
			if (delta < 0) {
				return -1;
			}
			if (delta > 0) {
				return +1;
			}
			return 0;
		}
	}

	// compare other points relative to atan2 angle (bewteen -pi/2 and pi/2) they make with this Point
	private class Atan2Order implements Comparator<Pixel> {
		@Override
		public int compare(Pixel q1, Pixel q2) {
			double angle1 = angleTo(q1);
			double angle2 = angleTo(q2);
			if (angle1 < angle2) {
				return -1;
			} else if (angle1 > angle2) {
				return +1;
			} else {
				return 0;
			}
		}
	}

	// compare other points relative to polar angle (between 0 and 2pi) they make with this Point
	private class PolarOrder implements Comparator<Pixel> {
		@Override
		public int compare(Pixel q1, Pixel q2) {
			double dx1 = q1.x - x;
			double dy1 = q1.y - y;
			double dx2 = q2.x - x;
			double dy2 = q2.y - y;

			if (dy1 >= 0 && dy2 < 0) {
				return -1; // q1 above; q2 below
			} else if (dy2 >= 0 && dy1 < 0) {
				return +1; // q1 below; q2 above
			} else if (dy1 == 0 && dy2 == 0) { // 3-collinear and horizontal
				if (dx1 >= 0 && dx2 < 0) {
					return -1;
				} else if (dx2 >= 0 && dx1 < 0) {
					return +1;
				} else {
					return 0;
				}
			} else {
				return -ccw(Pixel.this, q1, q2); // both above or below
			}

			// Note: ccw() recomputes dx1, dy1, dx2, and dy2
		}
	}

	// compare points according to their distance to this point
	private class DistanceToOrder implements Comparator<Pixel> {
		@Override
		public int compare(Pixel p, Pixel q) {
			double dist1 = distanceSquaredTo(p);
			double dist2 = distanceSquaredTo(q);
			if (dist1 < dist2) {
				return -1;
			} else if (dist1 > dist2) {
				return +1;
			} else {
				return 0;
			}
		}
	}

	// does this point equal y?
	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (other.getClass() != this.getClass()) {
			return false;
		}
		Pixel that = (Pixel) other;
		return this.x == that.x && this.y == that.y;
	}

	// convert to string
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

}
