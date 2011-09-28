package at.tugraz.ist.catroid.ui;

import android.app.Activity;
import android.content.Intent;
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
import at.tugraz.ist.catroid.utils.ImageEditing;

public class RotationActivity extends Activity {
	private OnTouchListener touchListener;
	private Point centerOfScreen;
	private Point verticalReferencePoint;
	private int angle;
	private Bitmap bitmap;
	private Bitmap originalBitmap;

	TransformationsView rotationView;
	String resultFieldName = "RotationAngle";

	//TODO @ integration: CONSTS aus values und consts nehmen
	private static final int SCREEN_HEIGHT = 800;
	private static final int SCREEN_WIDTH = 480;
	private static final int MAX_REL_COORDINATES = 1000;

	public RotationActivity() {
		centerOfScreen = new Point(200, 200);
		verticalReferencePoint = new Point(200, 100);
		angle = 0;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rotationView = new TransformationsView(this);

		setContentView(R.layout.rotation_activity);

		LinearLayout layout = (LinearLayout) findViewById(R.id.drag_n_drop_area);
		layout.addView(rotationView);

		touchListener = new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				setNewAngle(v, event);
				return true;
			}
		};

		EditText angleEditText = (EditText) findViewById(R.id.edit_angle);
		angleEditText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				switch (event.getKeyCode()) {
					case KeyEvent.KEYCODE_ENTER:
						setNewAngle(Integer.valueOf(v.getText().toString()).intValue());
						return true;
					default:
						break;
				}
				return false;
			}
		});

		Button performTransformationButton = (Button) findViewById(R.id.button_perform_transformation);
		performTransformationButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResultAndReturn();
			}
		});

		originalBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.catroid_logo);
		rotationView.setBitmap(originalBitmap);

		rotationView.setPosition(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
		centerOfScreen.set(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
		verticalReferencePoint.set(SCREEN_WIDTH / 2, 0);

		rotationView.setOnTouchListener(touchListener);

		updateEditTextView();
	}

	private void setNewAngle(View v, MotionEvent event) {
		Point chosenPoint = new Point((int) event.getX(), (int) event.getY());

		calculateAngle(chosenPoint);

		updateEditTextView();
		updateBitmap();
	}

	private void setNewAngle(int newAngle) {
		angle = newAngle;
		updateBitmap();
	}

	private void calculateAngle(Point chosenPoint) {
		int referenceX = verticalReferencePoint.x - centerOfScreen.x;
		int referenceY = verticalReferencePoint.y - centerOfScreen.y;

		int chosenX = chosenPoint.x - centerOfScreen.x;
		int chosenY = chosenPoint.y - centerOfScreen.y;

		double nominator = referenceX * chosenX + referenceY * chosenY;
		double denominator = Math.sqrt((Math.pow(referenceX, 2) + Math.pow(referenceY, 2))
				* (Math.pow(chosenX, 2) + Math.pow(chosenY, 2)));

		double cosine = nominator / denominator;
		double actualAngle = Math.acos(cosine);
		actualAngle = Math.toDegrees(actualAngle);

		if (chosenX < 0) {
			actualAngle = -actualAngle;
		}

		angle = (int) actualAngle;
	}

	private void updateBitmap() {
		bitmap = ImageEditing.rotateBitmap(originalBitmap, angle);
		rotationView.setBitmap(bitmap);
		rotationView.invalidate();
	}

	private void updateEditTextView() {
		EditText angleEdit = (EditText) findViewById(R.id.edit_angle);
		angleEdit.setText(Integer.toString(angle));
	}

	private void setResultAndReturn() {
		final Intent response = new Intent();
		response.putExtra(resultFieldName, angle);
		setResult(Activity.RESULT_OK, response);

		finish();
	}
}
