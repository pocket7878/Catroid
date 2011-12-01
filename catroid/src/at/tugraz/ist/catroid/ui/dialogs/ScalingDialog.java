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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.TransformationsView;

public class ScalingDialog extends Dialog {
	private Bitmap originalBitmap;
	private Bitmap bitmap;
	private Point centerOfScreen;
	private double seekbarValue;
	private double seekbarMinimumValue;
	private double seekbarMaximumValue;
	private double maximumScalingFactor;
	private double minimumScalingFactor;
	private double scaleInPercent;
	private double scaleFactor;

	private static final int SCREEN_HEIGHT = Values.SCREEN_HEIGHT;
	private static final int SCREEN_WIDTH = Values.SCREEN_WIDTH;

	private TransformationsView scalingView;
	private Context context;
	private EditText scaleEdit;
	private Button okButton;

	public ScalingDialog(Context context, EditText view, Button okButton, String imagePath) {
		super(context, R.style.settings_activity);
		this.context = context;
		this.scaleEdit = view;
		this.okButton = okButton;

		//int[] dimensions = ImageEditing.getImageDimensions(imagePath);
		//originalBitmap = ImageEditing.getBitmap(imagePath, dimensions[0], dimensions[1]);

		scaleInPercent = Math.round(Double.parseDouble(view.getText().toString()));
		scaleFactor = scaleInPercent / 100;

		//has to be changed according to given parameters
		minimumScalingFactor = 5;
		maximumScalingFactor = 350;

		seekbarMinimumValue = 5;
		seekbarMaximumValue = maximumScalingFactor;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		scalingView = new TransformationsView(this.context);

		setContentView(R.layout.scaling_dialog);

		LinearLayout layout = (LinearLayout) findViewById(R.id.drag_n_drop_area);
		layout.addView(scalingView);
		LinearLayout layout2 = (LinearLayout) findViewById(R.id.scaling_controls_layout);
		layout2.addView(scaleEdit);
		layout2.addView(okButton);

		scaleEdit.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				setNewScale(Double.parseDouble(scaleEdit.getText().toString()));
				return false;
			}
		});

		SeekBar scalingSeekBar = (SeekBar) findViewById(R.id.edit_scale_seekbar);
		scalingSeekBar.setMax((int) seekbarMaximumValue);
		scalingSeekBar.setProgress((int) scaleInPercent);
		scalingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress < seekbarMinimumValue) {
					seekbarValue = seekbarMinimumValue;
				} else {
					seekbarValue = progress;
				}

				setNewScale(seekbarValue);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		originalBitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.catroid);
		scalingView.setBitmap(originalBitmap);

		scalingView.setPosition(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);

		centerOfScreen = new Point(0, 0);
		centerOfScreen.set(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);

		scalingView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				setNewScale(v, event);
				return true;
			}
		});
		updateImageScale();
		updateViews();
	}

	private void setNewScale(View v, MotionEvent event) {
		Point chosenPoint = new Point((int) event.getX(), (int) event.getY());

		calculateScalingFactor(chosenPoint);

		updateViews();
		updateImageScale();
	}

	private void setNewScale(double newScale) {
		if (newScale < minimumScalingFactor) {
			newScale = minimumScalingFactor;
		} else if (newScale > maximumScalingFactor) {
			newScale = maximumScalingFactor;
		}

		scaleFactor = newScale / 100;
		scaleInPercent = newScale;
		updateViews();
		updateImageScale();
	}

	private void calculateScalingFactor(Point chosenPoint) {
		int imageHeight = originalBitmap.getHeight();

		double referenceY = imageHeight;
		double chosenY = Math.abs(chosenPoint.y - centerOfScreen.y);

		scaleFactor = chosenY / referenceY * 2;
		scaleInPercent = scaleFactor * 100;
	}

	private void updateImageScale() {
		bitmap = ImageEditing.scaleBitmap(originalBitmap, (int) (originalBitmap.getHeight() * scaleFactor),
				(int) (originalBitmap.getWidth() * scaleFactor));
		scalingView.setBitmap(bitmap);
		scalingView.invalidate();
	}

	private void updateViews() {
		scaleEdit.setText(Double.toString(Math.round(scaleInPercent)));

		SeekBar seekbar = (SeekBar) findViewById(R.id.edit_scale_seekbar);
		seekbar.setProgress((int) scaleInPercent);
	}
}
