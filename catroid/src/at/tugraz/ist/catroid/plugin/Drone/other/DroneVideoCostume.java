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
package at.tugraz.ist.catroid.plugin.Drone.other;

import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.Sprite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DroneVideoCostume extends Costume {
	private boolean firstStart = true;
	private int oldCameraOrientation = 0;

	public DroneVideoCostume(Sprite sprite) {
		super(sprite);
		this.touchable = true;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer) {
		return super.touchDown(x, y, pointer);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if (firstStart) {
			initializeTexture();
		} else {
			//DroneHandler.getInstance().getDrone()
			//		.renderVideoFrame(this.getRegion().getTexture().getTextureObjectHandle());

			//int newCameraOrientation = DroneHandler.getInstance().getDrone().getCameraOrientation();
			int newCameraOrientation = 0;
			if (oldCameraOrientation != newCameraOrientation) {
				oldCameraOrientation = newCameraOrientation;
				if (newCameraOrientation == 1) {
					this.getRegion().setRegion(0, 0, 176, 144);
				} else {
					this.getRegion().setRegion(0, 0, 320, 240);
				}
			}

			if (getRegion().getTexture() != null) {
				batch.setColor(color.r, color.g, color.b, color.a * this.alphaValue);
				if (scaleX == 1 && scaleY == 1 && rotation == 0) {
					batch.draw(this.getRegion(), x, y, width, height);
				} else {
					batch.draw(this.getRegion(), x, y, originX, originY, width, height, scaleX, scaleY, rotation);
				}
			}
		}
	}

	public void setImagePath(String path) {
		//TODO
	}

	@Override
	public void disposeTextures() {
		if (this.getRegion() != null && this.getRegion().getTexture() != null) {
			this.getRegion().getTexture().dispose();
		}
	}

	public void initializeTexture() {
		Pixmap pixmap = new Pixmap(320, 240, Format.RGB888);
		pixmap.setColor(Color.BLUE);
		pixmap.fill();
		Texture texture = new Texture(pixmap, false);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.setRegion(new TextureRegion(texture));
		this.width = this.getRegion().getRegionWidth();
		this.height = this.getRegion().getRegionHeight();
		this.x -= (this.width / 2f);
		this.y -= (this.height / 2f);
		this.originX = (this.width / 2f);
		this.originY = (this.height / 2f);
		pixmap.dispose();
		firstStart = false;
	}
}
