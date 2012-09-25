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
import java.util.List;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * @author robert
 * 
 */
public class PhysicRenderer {
	private final ShapeRenderer renderer = new ShapeRenderer();
	public final List<Vector2[]> shapes = new ArrayList<Vector2[]>();

	private static PhysicRenderer instance = null;

	public static synchronized PhysicRenderer getInstance() {
		if (instance == null) {
			instance = new PhysicRenderer();
		}
		return instance;
	}

	//	public void render(Matrix4 perspectiveMatrix, Collection<Body> bodies) {
	//		renderer.setProjectionMatrix(perspectiveMatrix);
	//
	//		for (Body body : bodies) {
	//			renderer.identity();
	//			Vector2 translation = PhysicWorldConverter.vecBox2dToCat(body.getPosition());
	//			renderer.translate(translation.x, translation.y, 0.f);
	//			float rotation = PhysicWorldConverter.angleBox2dToCat(body.getAngle());
	//			renderer.rotate(0.f, 0.f, 1.f, rotation);
	//			for (Fixture fixture : body.getFixtureList()) {
	//				switch (fixture.getType()) {
	//					case Chain:
	//					case Edge:
	//						System.out.println("Unsupported Fixture Type " + fixture.getType() + ".");
	//						break;
	//					case Circle:
	//						draw((CircleShape) fixture.getShape());
	//						break;
	//					case Polygon:
	//						draw((PolygonShape) fixture.getShape());
	//						break;
	//				}
	//			}
	//		}
	//	}
	//
	public void render(Matrix4 perspectiveMatrix) {
		renderer.setProjectionMatrix(perspectiveMatrix);

		//		for (PolygonShape shape : shapes) {
		//			renderer.identity();
		//			draw(shape);
		//		}

		for (Vector2[] points : shapes) {
			renderer.identity();
			renderer.begin(ShapeType.Line);
			draw(points);
			renderer.end();
		}
	}

	private void draw(Vector2[] points) {
		int index;
		for (index = 0; index < points.length - 1; index++) {
			drawLine(points[index], points[index + 1]);
		}
		drawLine(points[index], points[0]);
	}

	private void draw(PolygonShape polygon) {
		renderer.begin(ShapeType.Line);

		Vector2 from = new Vector2();
		Vector2 to = new Vector2();
		for (int index = 0; index < polygon.getVertexCount() - 1; index++) {
			polygon.getVertex(index, from);
			polygon.getVertex(index + 1, to);
			drawLine(from, to);
		}
		from = to.cpy();
		polygon.getVertex(0, to);
		drawLine(from, to);

		renderer.end();
	}

	private void drawLine(Vector2 from, Vector2 to) {
		from = PhysicWorldConverter.vecBox2dToCat(from);
		to = PhysicWorldConverter.vecBox2dToCat(to);
		renderer.line(from.x, from.y, to.x, to.y);
	}

	private void draw(CircleShape circle) {
		System.out.println("Rendering circle");
		Vector2 center = PhysicWorldConverter.vecBox2dToCat(circle.getPosition());
		renderer.circle(center.x, center.y, circle.getRadius());
	}
}