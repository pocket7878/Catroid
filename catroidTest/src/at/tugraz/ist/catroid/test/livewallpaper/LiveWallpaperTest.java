package at.tugraz.ist.catroid.test.livewallpaper;

import java.io.IOException;

import android.content.Intent;
import android.test.ServiceTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.StandardProjectHandler;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.livewallpaper.LiveWallpaper;
import at.tugraz.ist.catroid.livewallpaper.WallpaperCostume;

public class LiveWallpaperTest extends ServiceTestCase<LiveWallpaper> {

	private WallpaperCostume wallpaperCostume;

	public LiveWallpaperTest() {
		super(LiveWallpaper.class);
	}

	@Override
	public void setUp() {
		try {
			super.setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testDefaultProject() {

		//		NativeAppActivity.setContext(getContext());
		//		Project defaultProject = StorageHandler.getInstance().loadProject("default_project.xml");

		Project defaultProject;
		try {
			defaultProject = StandardProjectHandler.createAndSaveStandardProject(getContext());
			ProjectManager.getInstance().setProject(defaultProject);

			assertFalse(defaultProject == null);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.wallpaperCostume = WallpaperCostume.getInstance();

		Intent startIntent = new Intent();
		startIntent.setClass(getContext(), LiveWallpaper.class);
		startService(startIntent);

		CostumeData backgroundCostumeData = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(0)
				.getCostumeDataList().get(0);
		int backgroundPixel = backgroundCostumeData.getImageBitmap().getPixel(0, 0);
		int wallpaperBackgroundPixel = wallpaperCostume.getBackground().getPixel(0, 0);

		assertEquals("The background in the wallpaper is not the same as the default project background",
				backgroundPixel, wallpaperBackgroundPixel);

	}

}