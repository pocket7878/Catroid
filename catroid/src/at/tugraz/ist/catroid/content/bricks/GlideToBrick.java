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
import android.widget.EditText;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.ui.dialogs.FormulaEditorDialog;

public class GlideToBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	private Formula xDestinationFormula;
	private Formula yDestinationFormula;
	private Formula durationInSecondsFormula;

	private transient View view;

	public GlideToBrick(Sprite sprite, int xDestination, int yDestination, int durationInMilliSeconds) {
		this.sprite = sprite;

		xDestinationFormula = new Formula(Integer.toString(xDestination));
		yDestinationFormula = new Formula(Integer.toString(yDestination));
		durationInSecondsFormula = new Formula(Double.toString(durationInMilliSeconds / 1000.0));
	}

	public GlideToBrick(Sprite sprite, Formula xDestination, Formula yDestination, Formula durationInMilliSeconds) {
		this.sprite = sprite;

		xDestinationFormula = xDestination;
		yDestinationFormula = yDestination;
		durationInSecondsFormula = durationInMilliSeconds;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {

		Double temp = durationInSecondsFormula.interpret() * 1000;
		int durationInMilliSeconds = temp.intValue();
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
		long startTime = System.currentTimeMillis();
		int duration = durationInMilliSeconds;
		while (duration > 0) {
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
			duration -= (int) (currentTime - startTime);
			updatePositions((int) (currentTime - startTime), duration);
			startTime = currentTime;
		}

		if (!sprite.isAlive(Thread.currentThread())) {
			// -stay at last position
		} else {
			double xDest = xDestinationFormula.interpret();
			double yDest = yDestinationFormula.interpret();
			sprite.costume.aquireXYWidthHeightLock();
			sprite.costume.setXYPosition((float) xDest, (float) yDest);
			sprite.costume.releaseXYWidthHeightLock();
		}
	}

	private void updatePositions(int timePassed, int duration) {
		sprite.costume.aquireXYWidthHeightLock();
		float xPosition = sprite.costume.getXPosition();
		float yPosition = sprite.costume.getYPosition();

		xPosition += ((float) timePassed / duration) * (xDestinationFormula.interpret() - xPosition);
		yPosition += ((float) timePassed / duration) * (yDestinationFormula.interpret() - yPosition);

		sprite.costume.setXYPosition(xPosition, yPosition);
		sprite.costume.releaseXYWidthHeightLock();
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	public int getDurationInMilliSeconds() {
		return durationInSecondsFormula.interpret().intValue();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_glide_to, null);

		TextView textX = (TextView) view.findViewById(R.id.brick_glide_to_x_text_view);
		EditText editX = (EditText) view.findViewById(R.id.brick_glide_to_x_edit_text);
		xDestinationFormula.setTextFieldId(R.id.brick_glide_to_x_edit_text);
		xDestinationFormula.refreshTextField(view);
		editX.setOnClickListener(this);

		TextView textY = (TextView) view.findViewById(R.id.brick_glide_to_y_text_view);
		EditText editY = (EditText) view.findViewById(R.id.brick_glide_to_y_edit_text);
		yDestinationFormula.setTextFieldId(R.id.brick_glide_to_y_edit_text);
		yDestinationFormula.refreshTextField(view);
		editY.setOnClickListener(this);

		TextView textDuration = (TextView) view.findViewById(R.id.brick_glide_to_duration_text_view);
		EditText editDuration = (EditText) view.findViewById(R.id.brick_glide_to_duration_edit_text);
		durationInSecondsFormula.setTextFieldId(R.id.brick_glide_to_duration_edit_text);
		durationInSecondsFormula.refreshTextField(view);

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);
		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_glide_to, null);
	}

	@Override
	public Brick clone() {
		return new GlideToBrick(getSprite(), xDestinationFormula, yDestinationFormula, durationInSecondsFormula);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.brick_glide_to_x_edit_text:
				FormulaEditorDialog.showDialog(view, this, xDestinationFormula);
				break;

			case R.id.brick_glide_to_y_edit_text:
				FormulaEditorDialog.showDialog(view, this, yDestinationFormula);
				break;

			case R.id.brick_glide_to_duration_edit_text:
				FormulaEditorDialog.showDialog(view, this, durationInSecondsFormula);
				break;
		}

	}

}
