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
package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;

public class TakeScreenshotInformationDialog extends Dialog {
	private Context context;

	public TakeScreenshotInformationDialog(Context context) {
		super(context, R.style.Sherlock___Theme_Dialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.dialog_take_screenshot);
		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);

		setTitle(context.getString(R.string.take_screenshot_title));
		setCanceledOnTouchOutside(true);

		TextView helpTextView = (TextView) findViewById(R.id.dialog_take_screenshot_help);

		helpTextView.setText(context.getString(R.string.take_screenshot_help));

		/*
		 * SpannableString ss = new SpannableString(context.getString(R.string.take_screenshot_help));
		 * Drawable d = context.getResources().getDrawable(R.drawable.ic_screenshot_information);
		 * d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
		 * ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
		 * ss.setSpan(span, 25, 30, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		 * helpTextView.setText(ss);
		 */
		;

		TextView InformationTextView = (TextView) findViewById(R.id.dialog_take_screenshot_information);
		InformationTextView.setText(context.getString(R.string.take_screenshot_information));

		Button okButton = (Button) findViewById(R.id.dialog_take_screenshot_ok_button);
		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}
