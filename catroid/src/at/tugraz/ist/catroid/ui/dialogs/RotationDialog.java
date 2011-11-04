package at.tugraz.ist.catroid.ui.dialogs;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.TransformationsView;

public class RotationDialog extends Dialog {
	private Point centerOfScreen;
	private Point referencePoint;
	private double angle;
	private Bitmap bitmap;
	private Bitmap originalBitmap;

	TransformationsView rotationView;
	Context context;
	EditText angleEdit;
	Button okButton;
	String resultFieldName = "RotationAngle";

	public RotationDialog(Context context, EditText view, Button okButton) {
		super(context, R.style.settings_activity);
		this.context = context;
		this.angleEdit = view;
		this.okButton = okButton;

		centerOfScreen = new Point();
		referencePoint = new Point();
		angle = roundToOneDecimal(Double.parseDouble(view.getText().toString()));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rotationView = new TransformationsView(this.getContext());

		setContentView(R.layout.rotation_dialog);

		LinearLayout layout = (LinearLayout) findViewById(R.id.drag_n_drop_area);
		layout.addView(rotationView);
		LinearLayout layout2 = (LinearLayout) findViewById(R.id.rotation_controls_layout);
		layout2.addView(angleEdit);
		layout2.addView(okButton);

		angleEdit.setOnEditorActionListener(new OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				setNewAngle(Double.parseDouble(angleEdit.getText().toString()));
				return true;
			}
		});

		originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.catroid);
		updateBitmap();
		rotationView.setBitmap(bitmap);

		rotationView.setPosition(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		centerOfScreen.set(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		referencePoint.set(Values.SCREEN_WIDTH / 2, 0);

		rotationView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				setNewAngle(v, event);
				return true;
			}
		});

		updateEditTextView();
	}

	private void setNewAngle(View v, MotionEvent event) {
		Point chosenPoint = new Point((int) event.getX(), (int) event.getY());

		calculateAngle(chosenPoint);

		updateEditTextView();
		updateBitmap();
	}

	private void setNewAngle(double newAngle) {
		angle = roundToOneDecimal(newAngle);
		updateBitmap();
	}

	private void calculateAngle(Point chosenPoint) {
		int referenceX = referencePoint.x - centerOfScreen.x;
		int referenceY = referencePoint.y - centerOfScreen.y;

		int chosenX = chosenPoint.x - centerOfScreen.x;
		int chosenY = chosenPoint.y - centerOfScreen.y;

		double nominator = referenceX * chosenX + referenceY * chosenY;
		double denominator = Math.sqrt((Math.pow(referenceX, 2) + Math.pow(referenceY, 2))
				* (Math.pow(chosenX, 2) + Math.pow(chosenY, 2)));

		double cosine = nominator / denominator;
		double actualAngle = Math.acos(cosine);
		actualAngle = Math.toDegrees(actualAngle);

		if (chosenX < 0) {
			actualAngle = (360 - actualAngle);
		}

		angle = roundToOneDecimal(actualAngle);
	}

	private void updateBitmap() {
		bitmap = ImageEditing.rotateBitmap(originalBitmap, (float) angle);
		rotationView.setBitmap(bitmap);
		rotationView.invalidate();
	}

	private void updateEditTextView() {
		angleEdit.setText(Double.toString(angle));
	}

	private double roundToOneDecimal(double nonRounded) {
		BigDecimal bd = new BigDecimal(nonRounded).setScale(1, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
