/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.exception.InterruptedRuntimeException;
import at.tugraz.ist.catroid.ui.dialogs.EditDoubleDialog;

public class VibrateBrick implements Brick, OnDismissListener {
	private static final long serialVersionUID = 1L;
	private int timeToVibrateInMilliseconds;
	private Sprite sprite;
	public Vibrator vibrator;
	private Context internalContext;

	public VibrateBrick(Sprite sprite, int timeToVibrateInMilliseconds) {
		Log.d("MyActivity", "Vibratetime " + timeToVibrateInMilliseconds);
		this.timeToVibrateInMilliseconds = timeToVibrateInMilliseconds;
		this.sprite = sprite;
		internalContext = null;
	}

	public void execute() {
		long startTime = 0;

		Log.d("MyActivity", "While reached");
		try {

			startTime = System.currentTimeMillis();
			vibrator = (Vibrator) internalContext.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(timeToVibrateInMilliseconds);
			Thread.sleep(timeToVibrateInMilliseconds);

		} catch (InterruptedException e) {
			vibrator.cancel();
			timeToVibrateInMilliseconds = timeToVibrateInMilliseconds
						- (int) (System.currentTimeMillis() - startTime);
			throw new InterruptedRuntimeException("WaitBrick was interrupted", e);
		}

	}

	public Sprite getSprite() {
		return sprite;
	}

	public long getVibrateTime() {
		return timeToVibrateInMilliseconds;
	}

	public void stopVibrate() {
		long temp = getVibrateTime();
		Log.d("MyActivity", "STOP Vibrate" + temp);
		vibrator = (Vibrator) internalContext.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.cancel();
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		internalContext = context;
		View view = inflater.inflate(R.layout.construction_brick_vibrate, null);

		EditText edit = (EditText) view.findViewById(R.id.InputValueEditText);
		edit.setText((timeToVibrateInMilliseconds / 1000.0) + "");

		EditDoubleDialog dialog = new EditDoubleDialog(context, edit, timeToVibrateInMilliseconds / 1000.0);
		dialog.setOnDismissListener(this);
		dialog.setOnCancelListener((OnCancelListener) context);

		edit.setOnClickListener(dialog);

		return view;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		internalContext = context;
		View view = inflater.inflate(R.layout.toolbox_brick_vibrate, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new VibrateBrick(getSprite(), timeToVibrateInMilliseconds);
	}

	public void onDismiss(DialogInterface dialog) {
		Log.d("MyActivity", "ONDISMISS REACHED");
		stopVibrate();
		timeToVibrateInMilliseconds = (int) Math.round(((EditDoubleDialog) dialog).getValue() * 1000);
		dialog.cancel();

	}

	public void onPause() {
		Log.d("MyActivity", "ONPAUSE REACHED");
	}

	public void onCancel() {
		Log.d("MyActivity", "ONCANCEL REACHED");
	}

	public void onStop() {
		Log.d("MyActivity", "ONSTOP REACHED");
	}
}
