package at.tugraz.ist.catroid.livewallpaper;

import android.service.wallpaper.WallpaperService;

public class LiveWallpaper extends WallpaperService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.service.wallpaper.WallpaperService#onCreateEngine()
	 */
	@Override
	public Engine onCreateEngine() {
		// TODO Auto-generated method stub
		return new CatWallEngine();
	}

	private class CatWallEngine extends Engine {

	}

	/**
	 * @return
	 */

}
