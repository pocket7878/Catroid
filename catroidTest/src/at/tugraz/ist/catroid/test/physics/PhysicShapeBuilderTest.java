package at.tugraz.ist.catroid.test.physics;

import java.io.File;
import java.io.IOException;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.physics.PhysicShapeBuilder;
import at.tugraz.ist.catroid.physics.PhysicWorldConverter;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class PhysicShapeBuilderTest extends AndroidTestCase {

	private static final int IMAGE_FILE_ID = R.raw.icon;

	private PhysicShapeBuilder builder;
	private final String projectName = "testProject";
	private File testImage;
	private CostumeData costumeData;
	private Costume costume;

	@Override
	public void setUp() throws Exception {

		createProject();

		costumeData = new CostumeData();
		costumeData.setCostumeFilename(testImage.getName());
		costumeData.setCostumeName("CostumeName");

		Sprite sprite = new Sprite("");
		costume = new Costume(sprite);
		costume.setCostumeData(costumeData);
		builder = new PhysicShapeBuilder();

	}

	@Override
	protected void tearDown() throws Exception {
		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
		if (testImage != null && testImage.exists()) {
			testImage.delete();
		}
	}

	public void testRectangle() {

		Shape[] shapes = builder.createShape(costume.getCostumeData());
		PolygonShape polyShape = (PolygonShape) shapes[0];//[19/67]

		float[][] points = getRectPointsOfImage();

		Vector2 tempVertex = new Vector2();
		for (int i = 0; i < polyShape.getVertexCount(); i++) {
			polyShape.getVertex(i, tempVertex);
			float x = tempVertex.x;
			float y = tempVertex.y;
			assertEquals(points[i][0], x);
			assertEquals(points[i][1], y);
		}
	}

	private float[][] getRectPointsOfImage() {
		int image_x = costume.getCostumeData().getResolution()[0];
		int image_y = costume.getCostumeData().getResolution()[1];
		float image_x_box2d = PhysicWorldConverter.lengthCatToBox2d(image_x);
		float image_y_box2d = PhysicWorldConverter.lengthCatToBox2d(image_y);
		float image_x_box2d_coord = image_x_box2d / 2;
		float image_y_box2d_coord = image_y_box2d / 2;
		float[][] points = new float[4][2];

		points[0][0] = -image_x_box2d_coord;
		points[0][1] = -image_y_box2d_coord;

		points[1][0] = +image_x_box2d_coord;
		points[1][1] = -image_y_box2d_coord;

		points[2][0] = +image_x_box2d_coord;
		points[2][1] = +image_y_box2d_coord;

		points[3][0] = -image_x_box2d_coord;
		points[3][1] = +image_y_box2d_coord;

		return points;

	}

	private void createProject() throws IOException {
		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		Project project = new Project(getContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(this.projectName, "testImage.png", IMAGE_FILE_ID, getContext(),
				TestUtils.TYPE_IMAGE_FILE);

	}

}
