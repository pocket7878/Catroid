package at.tugraz.ist.catroid.ui.dialogs;

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
import at.tugraz.ist.catroid.utils.TransformationsView;

public class TranslationDialog extends Dialog {
	//TODO bitmap wird nicht in genau der mitte gezeichnet

	private Point devicePosition;
	private Point virtualPosition; // thats the position you choose within catroid stage
	private Point minPosition;
	private Point maxPosition;
	private Bitmap bitmap;
	private Bitmap originalBitmap;

	private TransformationsView translationView;
	private Context context;
	private EditText xEdit;
	private EditText yEdit;
	private Button okButton;

	//TODO @ integration: CONSTS aus values und consts nehmen
	private static final int SCREEN_HEIGHT = 800;
	private static final int SCREEN_WIDTH = 480;
	private static final int MAX_REL_COORDINATES = 1000;

	public TranslationDialog(Context context, EditText viewX, EditText viewY, Button okButton) {
		super(context, R.style.settings_activity);
		this.context = context;
		this.xEdit = viewX;
		this.yEdit = viewY;
		this.okButton = okButton;

		//control after here
		devicePosition = new Point(0, 0);
		virtualPosition = new Point(0, 0);
		minPosition = new Point(0, 0);
		maxPosition = new Point(SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		translationView = new TransformationsView(this.getContext());

		setContentView(R.layout.translation_dialog);

		LinearLayout layout = (LinearLayout) findViewById(R.id.drag_n_drop_area);
		layout.addView(translationView);
		LinearLayout layout2 = (LinearLayout) findViewById(R.id.translation_controls_layout);
		layout2.addView(xEdit);
		layout2.addView(yEdit);
		layout2.addView(okButton);

		translationView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				setNewPosition(v, event);
				return true;
			}
		});

		xEdit.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				setNewXPosition(Integer.valueOf(v.getText().toString()).intValue());
				return true;
			}
		});

		yEdit.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				setNewYPosition(Integer.valueOf(v.getText().toString()).intValue());
				return true;
			}
		});

		originalBitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.catroid);
		translationView.setBitmap(originalBitmap);

		minPosition.set(0, 0);
		maxPosition.set(SCREEN_WIDTH, SCREEN_HEIGHT);

		updateEditTextViews();
	}

	private void setNewPosition(View v, MotionEvent event) {
		int xPosition = (int) event.getX();
		int yPosition = (int) event.getY();

		devicePosition.set(xPosition, yPosition);
		calculateVirtualPosition();

		setImagePosition();

		updateEditTextViews();
		setImagePosition();
	}

	private void setNewXPosition(int xCoord) {
		devicePosition.x = (int) ((SCREEN_WIDTH / 2f) - ((SCREEN_WIDTH / (2f * MAX_REL_COORDINATES)) * xCoord));
		calculateVirtualPosition();
		setImagePosition();
	}

	private void setNewYPosition(int yCoord) {
		devicePosition.y = (int) ((SCREEN_HEIGHT / 2f) - ((SCREEN_HEIGHT / (2f * MAX_REL_COORDINATES)) * yCoord));
		calculateVirtualPosition();
		setImagePosition();
	}

	private void calculateVirtualPosition() {
		virtualPosition.x = Math.round((devicePosition.x - (SCREEN_WIDTH / 2f))
				/ ((SCREEN_WIDTH / (2f * MAX_REL_COORDINATES))));

		virtualPosition.y = Math.round((devicePosition.y - (SCREEN_HEIGHT / 2f))
				/ ((SCREEN_HEIGHT / (2f * MAX_REL_COORDINATES))));

		updateEditTextViews();
	}

	private void calculateDevicePosition() {
		//TODO
		//am beginn wenn virtuelle position angegeben wird m�ssen
		//die device coordinaten neu berechnet werden
	}

	private void setImagePosition() {
		//TODO what about maximal virtual position?
		if (devicePosition.x > maxPosition.x) {
			devicePosition.x = maxPosition.x;
		}
		if (devicePosition.y > maxPosition.y) {
			devicePosition.y = maxPosition.y;
		}

		if (devicePosition.x < minPosition.x) {
			devicePosition.x = minPosition.x;
		}
		if (devicePosition.y < minPosition.y) {
			devicePosition.y = minPosition.y;
		}

		calculateVirtualPosition();

		translationView.setPosition(devicePosition.x, devicePosition.y);
		translationView.invalidate();
	}

	private void updateEditTextViews() {
		xEdit.setText(Integer.toString(virtualPosition.x));
		yEdit.setText(Integer.toString(virtualPosition.y));
	}
}
