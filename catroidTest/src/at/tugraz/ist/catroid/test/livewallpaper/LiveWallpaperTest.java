package at.tugraz.ist.catroid.test.livewallpaper;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.livewallpaper.WallpaperCostume;

public class LiveWallpaperTest extends InstrumentationTestCase {
	private WallpaperCostume wallpaperCostume;
	private ProjectManager projectManager;

	public LiveWallpaperTest() {
		wallpaperCostume = WallpaperCostume.getInstance();
		projectManager = ProjectManager.getInstance();

	}

	public void testDefaultProject() {

		createStandardProject();
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
	}

	public void createStandardProject() {

	}

}
