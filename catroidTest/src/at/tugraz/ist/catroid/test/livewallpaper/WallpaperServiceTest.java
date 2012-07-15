package at.tugraz.ist.catroid.test.livewallpaper;

import android.service.wallpaper.WallpaperService.Engine;
import android.test.ServiceTestCase;
import at.tugraz.ist.catroid.livewallpaper.LiveWallpaper;

public class WallpaperServiceTest extends ServiceTestCase<LiveWallpaper> {

	public WallpaperServiceTest(Class<LiveWallpaper> serviceClass) {
		super(serviceClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setUp() throws Exception {
		LiveWallpaper lwp = new LiveWallpaper();
		Engine engine = lwp.onCreateEngine();
		assertEquals("returned null", engine);
		super.setUp();
	}

}
