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
package at.tugraz.ist.catroid.test.livewallpaper;

import java.io.IOException;

import android.graphics.Bitmap;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.StandardProjectHandler;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ChangeBrightnessBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeGhostEffectBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeSizeByNBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeXByBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;
import at.tugraz.ist.catroid.content.bricks.ClearGraphicEffectBrick;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.GlideToBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.IfOnEdgeBounceBrick;
import at.tugraz.ist.catroid.content.bricks.NextCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.SetBrightnessBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetGhostEffectBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.SetYBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.livewallpaper.WallpaperCostume;
import at.tugraz.ist.catroid.livewallpaper.WallpaperHelper;

public class LiveWallpaperTest extends AndroidTestCase {

	private WallpaperHelper wallpaperHelper;

	private Project defaultProject;
	private Sprite backgroundSprite;
	private Sprite catroidSprite;

	private Bitmap backgroundBitmap;
	private Bitmap normalCatBitmap;
	private Bitmap banzaiCatBitmap;
	private Bitmap chasireCatBitmap;

	@Override
	public void setUp() {
		try {
			super.setUp();
			createDefaultProjectAndInitMembers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void tearDown() {
		try {
			super.tearDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createDefaultProjectAndInitMembers() {

		try {
			Values.SCREEN_WIDTH = 480;
			Values.SCREEN_HEIGHT = 800;
			this.defaultProject = StandardProjectHandler.createAndSaveStandardProject(getContext());
			ProjectManager.getInstance().setProject(defaultProject);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.catroidSprite = defaultProject.getSpriteList().get(1);
		this.backgroundSprite = defaultProject.getSpriteList().get(0);

		this.backgroundBitmap = backgroundSprite.getCostumeDataList().get(0).getImageBitmap();
		this.normalCatBitmap = catroidSprite.getCostumeDataList().get(0).getImageBitmap();
		this.banzaiCatBitmap = catroidSprite.getCostumeDataList().get(1).getImageBitmap();
		this.chasireCatBitmap = catroidSprite.getCostumeDataList().get(2).getImageBitmap();

		this.wallpaperHelper = WallpaperHelper.getInstance();
		wallpaperHelper.setProject(defaultProject);
	}

	public boolean sameBitmaps(Bitmap first, Bitmap second) {

		if (first.getWidth() != second.getWidth() || first.getHeight() != second.getHeight()) {
			return false;
		}

		for (int width = 0; width < first.getWidth(); width++) {
			for (int height = 0; height < first.getHeight(); height++) {
				if (first.getPixel(width, height) != second.getPixel(width, height)) {
					return false;
				}
			}
		}

		return true;

	}

	public void testSetCostumeBrick() {

		Brick brick = backgroundSprite.getScript(0).getBrick(0);
		assertTrue("This brick should be an instance of SetCostumeBrick but it's not", brick instanceof SetCostumeBrick);
		brick.executeLiveWallpaper();
		WallpaperCostume wallpaperCostume = backgroundSprite.getWallpaperCostume();

		assertTrue("The isBackground flag was not set", wallpaperCostume.isBackground());
		assertTrue("The background in the wallpaper is not the same as the default project background",
				sameBitmaps(backgroundBitmap, wallpaperCostume.getCostume()));

		brick = catroidSprite.getScript(0).getBrick(0);
		assertTrue("This brick should be an instance of SetCostumeBrick but it's not", brick instanceof SetCostumeBrick);
		brick.executeLiveWallpaper();
		wallpaperCostume = catroidSprite.getWallpaperCostume();
		assertTrue("Expected normalCat but was " + wallpaperCostume.getCostumeData().getCostumeName(),
				sameBitmaps(normalCatBitmap, wallpaperCostume.getCostume()));

	}

	public void testNextCostumeBrick() {
		Brick brick = new NextCostumeBrick(catroidSprite);

		brick.executeLiveWallpaper();
		WallpaperCostume wallpaperCostume = catroidSprite.getWallpaperCostume();
		assertTrue("Expected normalCat but was " + wallpaperCostume.getCostumeData().getCostumeName(),
				sameBitmaps(normalCatBitmap, wallpaperCostume.getCostume()));

		brick.executeLiveWallpaper();
		assertTrue("Expected banzaiCat but was " + wallpaperCostume.getCostumeData().getCostumeName(),
				sameBitmaps(banzaiCatBitmap, wallpaperCostume.getCostume()));

		brick.executeLiveWallpaper();
		assertTrue("Expected chasireCat but was " + wallpaperCostume.getCostumeData().getCostumeName(),
				sameBitmaps(chasireCatBitmap, wallpaperCostume.getCostume()));

		brick.executeLiveWallpaper();
		assertTrue("Expected normalCat but was " + wallpaperCostume.getCostumeData().getCostumeName(),
				sameBitmaps(normalCatBitmap, wallpaperCostume.getCostume()));

	}

	public void testHideAndShowBricks() {
		Brick brick = new HideBrick(catroidSprite);
		brick.executeLiveWallpaper();
		WallpaperCostume wallpaperCostume = catroidSprite.getWallpaperCostume();
		assertTrue("The costume was not hidden!", wallpaperCostume.isCostumeHidden());

		brick = new ShowBrick(catroidSprite);
		brick.executeLiveWallpaper();
		assertFalse("The costume was not shown!", wallpaperCostume.isCostumeHidden());
	}

	public void testSetXBrick() {
		WallpaperCostume wallpaperCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(
				0));
		int xPosition = 60;
		Brick brick = new SetXBrick(catroidSprite, xPosition);
		brick.executeLiveWallpaper();
		float x = wallpaperHelper.getCenterXCoord() + xPosition - (wallpaperCostume.getCostume().getWidth() / 2);

		assertEquals("The x coordinate was not set properly", x, wallpaperCostume.getTop());

	}

	public void testSetYBrick() {
		WallpaperCostume wallpaperCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(
				0));
		int yPosition = 60;
		Brick brick = new SetYBrick(catroidSprite, yPosition);
		brick.executeLiveWallpaper();
		float y = wallpaperHelper.getCenterYCoord() - yPosition - (wallpaperCostume.getCostume().getHeight() / 2);

		assertEquals("The x coordinate was not set properly", y, wallpaperCostume.getLeft());

	}

	public void testPlaceAtBrick() {
		WallpaperCostume wallpaperCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(
				0));

		Brick brick = new PlaceAtBrick(catroidSprite, 28, 36);
		brick.executeLiveWallpaper();
		float x = wallpaperHelper.getCenterXCoord() - (wallpaperCostume.getCostume().getWidth() / 2) + 28;
		float y = wallpaperHelper.getCenterYCoord() - (wallpaperCostume.getCostume().getHeight() / 2) - 36;

		assertEquals("The x coordinate was not set properly", x, wallpaperCostume.getTop());
		assertEquals("The y coordinate was not set properly", y, wallpaperCostume.getLeft());

	}

	public void testChangeXByBrick() {
		int startingX = 50;
		int movingX = 85;

		WallpaperCostume wallpaperCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(
				0));
		wallpaperCostume.setX(startingX);

		Brick brick = new ChangeXByBrick(catroidSprite, movingX);
		brick.executeLiveWallpaper();

		float x = wallpaperHelper.getCenterXCoord() - (wallpaperCostume.getCostume().getWidth() / 2) + startingX
				+ movingX;

		assertEquals("The x coordinate was not set properly", x, wallpaperCostume.getTop());

	}

	public void testChangeYByBrick() {
		int startingY = 50;
		int movingY = 85;
		WallpaperCostume wallpaperCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(
				0));
		wallpaperCostume.setY(startingY);

		Brick brick = new ChangeYByBrick(catroidSprite, movingY);
		brick.executeLiveWallpaper();

		float y = wallpaperHelper.getCenterYCoord() - (wallpaperCostume.getCostume().getHeight() / 2) - startingY
				- movingY;

		assertEquals("The y coordinate was not set properly", y, wallpaperCostume.getLeft());

	}

	public void testSetSizeToBrick() {
		WallpaperCostume wallpaperCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(
				0));
		int oldWidth = wallpaperCostume.getCostume().getWidth();
		int oldHeight = wallpaperCostume.getCostume().getHeight();

		double size = 50.0;

		Brick brick = new SetSizeToBrick(catroidSprite, size);
		brick.executeLiveWallpaper();

		int newWidth = wallpaperCostume.getCostume().getWidth();
		int newHeight = wallpaperCostume.getCostume().getHeight();

		int expectedNewWidth = (int) (oldWidth * (size / 100));
		int expectedNewHeight = (int) (oldHeight * (size / 100));

		assertEquals("The costume has not been scaled properly: width differs", newWidth, expectedNewWidth);
		assertEquals("The Costume has not been scaled properly: height differs", newHeight, expectedNewHeight);

	}

	public void changeSetSizeByBrick() {
		WallpaperCostume wallpaperCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(
				0));
		int oldWidth = wallpaperCostume.getCostume().getWidth();
		int oldHeight = wallpaperCostume.getCostume().getHeight();

		double size = 20.0;

		Brick brick = new ChangeSizeByNBrick(catroidSprite, size);
		brick.executeLiveWallpaper();

		int newWidth = wallpaperCostume.getCostume().getWidth();
		int newHeight = wallpaperCostume.getCostume().getHeight();

		int expectedNewWidth = (int) (oldWidth + (oldWidth * (size / 100)));
		int expectedNewHeight = (int) (oldHeight + (oldHeight * (size / 100)));

		assertEquals("The costume has not been scaled properly: width differs", newWidth, expectedNewWidth);
		assertEquals("The Costume has not been scaled properly: height differs", newHeight, expectedNewHeight);

	}

	public void testSetBrightnessBrick() {
		WallpaperCostume wallpaperCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(
				0));
		int initialBrightness = 50;
		Brick brick = new SetBrightnessBrick(catroidSprite, initialBrightness);
		brick.executeLiveWallpaper();
		int brightness = (int) (wallpaperCostume.getBrightness() * 100f);
		assertEquals("The brightness was not set properly", brightness, initialBrightness);
	}

	public void testSetGhostEffectBrick() {

		WallpaperCostume wallpaperCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(
				0));
		int initialAlpha = 50;
		Brick brick = new SetGhostEffectBrick(catroidSprite, initialAlpha);
		brick.executeLiveWallpaper();
		int alpha = (int) (wallpaperCostume.getAlphaValue() * 100f);
		assertEquals("The alpha value was not set properly", alpha, initialAlpha);
	}

	public void testChangeBrightnessBrick() {
		WallpaperCostume wallpaperCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(
				0));
		float initialBrightness1 = 30;
		float initialBrightness2 = 20;
		Brick brick1 = new ChangeBrightnessBrick(catroidSprite, initialBrightness1);
		Brick brick2 = new ChangeBrightnessBrick(catroidSprite, initialBrightness2);
		brick1.executeLiveWallpaper();
		brick2.executeLiveWallpaper();

		float brightness = -(100 - (wallpaperCostume.getBrightness() * 100));

		assertEquals("The brightness was not set properly", brightness, initialBrightness1 + initialBrightness2);
	}

	public void testChangeGhostEffectBrick() {
		WallpaperCostume wallpaperCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(
				0));
		float initialAlpha1 = 50;
		float initialAlpha2 = -20;
		Brick brick1 = new ChangeGhostEffectBrick(catroidSprite, initialAlpha1);
		Brick brick2 = new ChangeGhostEffectBrick(catroidSprite, initialAlpha2);
		brick1.executeLiveWallpaper();
		brick2.executeLiveWallpaper();

		float alpha = 100 - (wallpaperCostume.getAlphaValue() * 100f);

		assertEquals("The alpha value was not set properly", alpha, initialAlpha1 + initialAlpha2);
	}

	public void testComeToFrontBrick() {
		WallpaperCostume backgroundCostume = new WallpaperCostume(backgroundSprite, backgroundSprite
				.getCostumeDataList().get(0));
		WallpaperCostume catroidCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(0));

		int backgroundPosition = backgroundCostume.getzPosition();
		int catroidPosition = catroidCostume.getzPosition();

		assertTrue("The position parameter has not been initialized properly", backgroundPosition == 0
				&& catroidPosition == 1);

		Brick brick = new ComeToFrontBrick(backgroundSprite);
		brick.executeLiveWallpaper();

		backgroundPosition = backgroundCostume.getzPosition();
		catroidPosition = catroidCostume.getzPosition();

		assertTrue("The position parameter has not been set properly", backgroundPosition == 1 && catroidPosition == 0);
	}

	public void testClearGraphicEffectBrick() {
		WallpaperCostume wallpaperCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(
				0));
		int initialAlpha = 50;
		int initialBrightness = 50;
		Brick brick1 = new SetBrightnessBrick(catroidSprite, initialBrightness);
		Brick brick2 = new SetGhostEffectBrick(catroidSprite, initialAlpha);
		brick1.executeLiveWallpaper();
		brick2.executeLiveWallpaper();

		int alpha = (int) (wallpaperCostume.getAlphaValue() * 100f);
		assertEquals("The alpha value was not set properly", alpha, initialAlpha);

		int brightness = (int) (wallpaperCostume.getBrightness() * 100f);
		assertEquals("The brightness was not set properly", brightness, initialBrightness);

		Brick brick3 = new ClearGraphicEffectBrick(catroidSprite);
		brick3.executeLiveWallpaper();

		assertEquals("Ghost Effect was not cleared", 1f, wallpaperCostume.getAlphaValue());
		assertEquals("Brightness was not cleared", 1f, wallpaperCostume.getBrightness());
	}

	public void testGlideToEffectBrick() {
		WallpaperCostume wallpaperCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(
				0));

		int xDestination = 100;
		int yDestination = 100;
		int duration = 3;

		Brick glideBrick = new GlideToBrick(catroidSprite, xDestination, yDestination, duration);
		glideBrick.executeLiveWallpaper();

		int x = wallpaperCostume.getX();
		int y = wallpaperCostume.getY();

		assertEquals("Object is not on the desired x destination", x, xDestination);
		assertEquals("Object is not on the desired y destination", y, yDestination);
	}

	public void testIfOnEdgeBounceBrick() {
		WallpaperCostume wallpaperCostume = new WallpaperCostume(catroidSprite, catroidSprite.getCostumeDataList().get(
				0));

		int xDestination = 500;

		Brick changeXBrick = new ChangeXByBrick(catroidSprite, xDestination);
		Brick ifOnEdgeBounceBrick = new IfOnEdgeBounceBrick(catroidSprite);
		changeXBrick.executeLiveWallpaper();
		ifOnEdgeBounceBrick.executeLiveWallpaper();

		int settedX = wallpaperCostume.getX();
		int x = (Values.SCREEN_WIDTH / 2) - (wallpaperCostume.getCostume().getWidth() / 2);

		assertEquals("Object is not on the edge of the screen", x, settedX);

	}
}