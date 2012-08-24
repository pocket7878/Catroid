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
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.ui.fragment.FormulaEditorFragment;

public class GlideToBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	private Formula xDestination;
	private Formula yDestination;
	private Formula durationInSeconds;

	private transient View view;

	public GlideToBrick(Sprite sprite, int xDestinationValue, int yDestinationValue, int durationInMilliSecondsValue) {
		this.sprite = sprite;

		xDestination = new Formula(Integer.toString(xDestinationValue));
		yDestination = new Formula(Integer.toString(yDestinationValue));
		durationInSeconds = new Formula(Double.toString(durationInMilliSecondsValue / 1000.0));
	}

	public GlideToBrick(Sprite sprite, Formula xDestination, Formula yDestination, Formula durationInSeconds) {
		this.sprite = sprite;

		this.xDestination = xDestination;
		this.yDestination = yDestination;
		this.durationInSeconds = durationInSeconds;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {

		/* That's the way how an action is made */
		//		Action action = MoveBy.$(xDestination, yDestination, this.durationInMilliSeconds / 1000);
		//		final CountDownLatch latch = new CountDownLatch(1);
		//		action = action.setCompletionListener(new OnActionCompleted() {
		//			public void completed(Action action) {
		//				latch.countDown();
		//			}
		//		});
		//		sprite.costume.action(action);
		//		try {
		//			latch.await();
		//		} catch (InterruptedException e) {
		//		}
		int durationInMilliSeconds = (int) (durationInSeconds.interpretFloat() * 1000f);
		float xDestinationValue = xDestination.interpretFloat();
		float yDestinationValue = yDestination.interpretFloat();

		long startTime = System.currentTimeMillis();
		while (durationInMilliSeconds > 0) {
			if (!sprite.isAlive(Thread.currentThread())) {
				break;
			}
			long timeBeforeSleep = System.currentTimeMillis();
			int sleep = 33;
			while (System.currentTimeMillis() <= (timeBeforeSleep + sleep)) {

				if (sprite.isPaused) {
					sleep = (int) ((timeBeforeSleep + sleep) - System.currentTimeMillis());
					long milliSecondsBeforePause = System.currentTimeMillis();
					while (sprite.isPaused) {
						if (sprite.isFinished) {
							return;
						}
						Thread.yield();
					}
					timeBeforeSleep = System.currentTimeMillis();
					startTime += System.currentTimeMillis() - milliSecondsBeforePause;
				}

				Thread.yield();
			}
			long currentTime = System.currentTimeMillis();
			durationInMilliSeconds -= (int) (currentTime - startTime);
			updatePositions((int) (currentTime - startTime), durationInMilliSeconds, xDestinationValue,
					yDestinationValue);
			startTime = currentTime;
		}

		if (!sprite.isAlive(Thread.currentThread())) {
			// -stay at last position
		} else {
			sprite.costume.aquireXYWidthHeightLock();
			sprite.costume.setXYPosition(xDestinationValue, yDestinationValue);
			sprite.costume.releaseXYWidthHeightLock();
		}
	}

	private void updatePositions(int timePassed, int duration, float xDestinationValue, float yDestinationValue) {
		sprite.costume.aquireXYWidthHeightLock();
		float xPosition = sprite.costume.getXPosition();
		float yPosition = sprite.costume.getYPosition();

		xPosition += ((float) timePassed / duration) * (xDestinationValue - xPosition);
		yPosition += ((float) timePassed / duration) * (yDestinationValue - yPosition);

		sprite.costume.setXYPosition(xPosition, yPosition);
		sprite.costume.releaseXYWidthHeightLock();
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	public int getDurationInMilliSeconds() {
		return durationInSeconds.interpretInteger();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_glide_to, null);

		TextView textX = (TextView) view.findViewById(R.id.brick_glide_to_x_text_view);
		xDestination.setTextFieldId(R.id.brick_glide_to_x_edit_text);
		xDestination.refreshTextField(view);
		textX.setOnClickListener(this);

		TextView textY = (TextView) view.findViewById(R.id.brick_glide_to_y_text_view);
		yDestination.setTextFieldId(R.id.brick_glide_to_y_edit_text);
		yDestination.refreshTextField(view);
		textY.setOnClickListener(this);

		TextView textDuration = (TextView) view.findViewById(R.id.brick_glide_to_duration_text_view);
		durationInSeconds.refreshTextField(view);

		textDuration.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_glide_to, null);
	}

	@Override
	public Brick clone() {
		return new GlideToBrick(getSprite(), xDestination, yDestination, durationInSeconds);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.brick_glide_to_x_text_view:
				FormulaEditorFragment.showFragment(view, this, xDestination);
				break;

			case R.id.brick_glide_to_y_text_view:
				FormulaEditorFragment.showFragment(view, this, yDestination);
				break;

			case R.id.brick_glide_to_duration_text_view:
				FormulaEditorFragment.showFragment(view, this, durationInSeconds);
				break;
		}

	}

}
