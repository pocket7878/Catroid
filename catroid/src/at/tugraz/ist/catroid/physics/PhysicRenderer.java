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

import com.badlogic.gdx.graphics.Color;
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
	public ShapeRenderer renderer = null;
	public static PhysicRenderer instance = new PhysicRenderer();
	public List<PolygonShape> shapes = new ArrayList<PolygonShape>();
	private Color[] color = new Color[] { new Color(0, 1, 0, 1), new Color(1, 1, 0, 1), new Color(1, 0, 0, 1),
			new Color(1, 0, 1, 1), new Color(0, 0, 1, 1), new Color(0, 1, 1, 1), new Color(1, 1, 1, 1),
			new Color(1, 0.5f, 0, 1) };

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

	public void render(Matrix4 perspectiveMatrix) {
		renderer.setProjectionMatrix(perspectiveMatrix);

		renderer.identity();
		for (PolygonShape shape : shapes) {
			draw(shape);
		}
	}

	private void draw(PolygonShape polygon) {
		renderer.begin(ShapeType.Line);

		float multiplier = 4.0f;
		Vector2 from = new Vector2();
		Vector2 to = new Vector2();
		int index = 0;
		for (index = 0; index < polygon.getVertexCount() - 1; index++) {
			polygon.getVertex(index, from);
			polygon.getVertex(index + 1, to);
			from.mul(multiplier);
			to.mul(multiplier);
			drawLine(from, to, index);
		}
		from = new Vector2(to);
		polygon.getVertex(0, to);
		to.mul(multiplier);
		drawLine(from, to, index);

		renderer.end();
	}

	private void drawLine(Vector2 from, Vector2 to, int index) {
		renderer.setColor(color[index]);
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
