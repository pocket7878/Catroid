package at.tugraz.ist.catroid.test.livewallpaper;

import android.content.res.Resources;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.StandardProjectHandler;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.livewallpaper.WallpaperCostume;

public class LiveWallpaperTest extends InstrumentationTestCase {
	private WallpaperCostume wallpaperCostume;
	private Project defaultProject;

	public LiveWallpaperTest() {

	}

	@Override
	protected void setUp() {

		try {
			super.setUp();
			this.defaultProject = StandardProjectHandler.createAndSaveStandardProject(
					Resources.getSystem().getString(R.string.default_project_name), null);
			this.wallpaperCostume = WallpaperCostume.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testDefaultProject() {
		assertFalse(defaultProject == null);
		CostumeData backgroundCostumeData = ProjectManager.getInstance().getCurrentSprite().getCostumeDataList().get(0);
		int backgroundPixel = backgroundCostumeData.getImageBitmap().getPixel(0, 0);
		int wallpaperBackgroundPixel = wallpaperCostume.getBackground().getPixel(0, 0);

		assertEquals("The background in the wallpaper is not the same as the default project background",
				backgroundPixel, wallpaperBackgroundPixel);

	}

}