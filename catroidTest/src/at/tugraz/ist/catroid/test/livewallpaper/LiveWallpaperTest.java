package at.tugraz.ist.catroid.test.livewallpaper;

import java.io.IOException;

import android.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ServiceTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.StandardProjectHandler;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.livewallpaper.LiveWallpaper;
import at.tugraz.ist.catroid.livewallpaper.WallpaperCostume;

public class LiveWallpaperTest extends ServiceTestCase<LiveWallpaper> {

	private WallpaperCostume wallpaperCostume;
	private Project defaultProject;
	private Bitmap backgroundBitmap;
	private Bitmap normalCostumeBitmap;

	public LiveWallpaperTest() {
		super(LiveWallpaper.class);
	}

	@Override
	public void setUp() {
		try {
			super.setUp();

			try {
				Values.SCREEN_WIDTH = 500;
				Values.SCREEN_HEIGHT = 1000;
				this.defaultProject = StandardProjectHandler.createAndSaveStandardProject(getContext());
				ProjectManager.getInstance().setProject(defaultProject);
				Resources resources = getContext().getResources(); 
				normalCostumeBitmap = BitmapFactory.decodeResource(resources, ));

			} catch (IOException e) {
				e.printStackTrace();
			}

			this.wallpaperCostume = WallpaperCostume.getInstance();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testDefaultProject() {

		//TODO: CHANGE THIS!
		CostumeData backgroundCostumeData = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getCostumeDataList().get(0);

		Brick brick = defaultProject.getSpriteList().get(0).getScript(0).getBrick(0);
		brick.executeLiveWallpaper();

		int backgroundPixel = backgroundCostumeData.getImageBitmap().getPixel(0, 0);
		int wallpaperBackgroundPixel = wallpaperCostume.getBackground().getPixel(0, 0);

		assertEquals("The background in the wallpaper is not the same as the default project background",
				backgroundPixel, wallpaperBackgroundPixel);

		//TODO: CHANGE THIS TOO!
		CostumeData normalCostumeData = defaultProject.getSpriteList().get(1).getCostumeDataList().get(0);

		brick = defaultProject.getSpriteList().get(1).getScript(0).getBrick(0);
		brick.executeLiveWallpaper();

		int costumePixel = normalCostumeData.getImageBitmap().getPixel(0, 0);
		int wallpaperCostumePixel = wallpaperCostume.getCostume().getPixel(0, 0);

		assertEquals("The costume in the wallpaper is not the same as the normal catroid costume", costumePixel,
				wallpaperCostumePixel);

	}

}