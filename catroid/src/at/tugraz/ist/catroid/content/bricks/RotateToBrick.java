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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.text.InputType;
import android.util.FloatMath;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.stage.StageListener.PreStageMode;
import at.tugraz.ist.catroid.utils.Utils;

public class RotateToBrick implements Brick, OnClickListener {

	private static final long serialVersionUID = 1L;

	private Sprite sprite;
	private float degrees;
	private float degreeOffset = 90f;

	public RotateToBrick(Sprite sprite, float degrees) {
		this.sprite = sprite;
		this.degrees = degrees;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
		sprite.costume.rotation = -degrees + degreeOffset;
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {

		View view = View.inflate(context, R.layout.brick_rotate_to, null);
		TextView rotationTextView = (TextView) view.findViewById(R.id.brick_rotate_to_text_view);
		EditText rotationEditText = (EditText) view.findViewById(R.id.brick_rotate_to_edit_text);
		rotationEditText.setText(String.valueOf(degrees));

		rotationTextView.setVisibility(View.GONE);
		rotationEditText.setVisibility(View.VISIBLE);
		rotationEditText.setOnClickListener(this);

		ImageButton editInPrestageButton = (ImageButton) view.findViewById(R.id.imageButtonEditRotation);
		editInPrestageButton.setVisibility(View.VISIBLE);
		editInPrestageButton.setOnClickListener(this);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_rotate_to, null);
	}

	@Override
	public Brick clone() {
		return new RotateToBrick(getSprite(), degrees);
	}

	public void onClick(final View view) {
		final Context context = view.getContext();

		if (view.getId() == R.id.imageButtonEditRotation) {
			ProjectManager.getInstance().setPreStageBrick(this, PreStageMode.ROTATION);
			Intent intent = new Intent(context, StageActivity.class);
			intent.setAction(Intent.ACTION_EDIT);
			context.startActivity(intent);
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			final EditText input = new EditText(context);
			input.setText(String.valueOf(degrees));
			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			input.setSelectAllOnFocus(true);
			dialog.setView(input);
			dialog.setOnCancelListener((OnCancelListener) context);
			dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					try {
						degrees = Float.parseFloat(input.getText().toString());
					} catch (NumberFormatException exception) {
						Toast.makeText(context, R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
					}
					dialog.cancel();
				}
			});
			dialog.setNeutralButton(context.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			AlertDialog finishedDialog = dialog.create();
			finishedDialog.setOnShowListener(Utils.getBrickDialogOnClickListener(context, input));

			finishedDialog.show();
		}

	}

	public void updateValuesFromCostume() {
		this.degrees = FloatMath.floor(-sprite.costume.rotation + degreeOffset) % 360;
		if (this.degrees <= -180F) {
			this.degrees += 360F;
		}
	}

}
