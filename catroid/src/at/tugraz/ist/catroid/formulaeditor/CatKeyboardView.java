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
/*
 * Copyright (C) 2008-2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package at.tugraz.ist.catroid.formulaeditor;

import java.util.Locale;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.dialogs.ChooseCostumeVariableFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class CatKeyboardView extends KeyboardView implements KeyboardView.OnKeyboardActionListener, OnClickListener {

	static final int NUMBER_KEYBOARD = 1;
	static final int FUNCTION_KEYBOARD = 0;
	static final int SENSOR_KEYBOARD = 2;

	private FormulaEditorEditText editText;

	private CatKeyboard symbolsNumbers;
	private CatKeyboard symbolsFunctions;
	private CatKeyboard symbolsSensors;
	private Context context;
	private ChooseCostumeVariableFragment chooseSpriteVariablesFragment;
	private RelativeLayout swipeBar;

	public CatKeyboardView(Context context, AttributeSet attrs) {

		super(context, attrs);
		this.context = context;
		setOnKeyboardActionListener(this);
		this.editText = null;

		this.symbolsNumbers = null;

		if (Locale.getDefault().getDisplayLanguage().contentEquals(Locale.GERMAN.getDisplayLanguage())) {
			this.symbolsNumbers = new CatKeyboard(this.getContext(), R.xml.symbols_de_numbers);
			this.symbolsFunctions = new CatKeyboard(this.getContext(), R.xml.symbols_de_functions);
			this.symbolsSensors = new CatKeyboard(this.getContext(), R.xml.symbols_de_sensors);

			//Log.i("info", "FormulaEditorDialog.onCreate() - DisplayLanguage is DE");

		} else {//if (Locale.getDefault().getDisplayLanguage().contentEquals(Locale.ENGLISH.getDisplayLanguage())) {
			this.symbolsNumbers = new CatKeyboard(this.getContext(), R.xml.symbols_eng_numbers);
			this.symbolsFunctions = new CatKeyboard(this.getContext(), R.xml.symbols_eng_functions);
			this.symbolsSensors = new CatKeyboard(this.getContext(), R.xml.symbols_eng_sensors);

		}

		this.setKeyboard(symbolsNumbers);

		if (((SherlockFragmentActivity) context).getSupportFragmentManager().findFragmentByTag(
				"chooseSpriteVariablesDialogFragment") == null) {
			this.chooseSpriteVariablesFragment = ChooseCostumeVariableFragment
					.newInstance(android.R.string.dialog_alert_title);
			this.chooseSpriteVariablesFragment.setCatKeyboardView(this);
		} else {
			this.chooseSpriteVariablesFragment = (ChooseCostumeVariableFragment) ((SherlockFragmentActivity) context)
					.getSupportFragmentManager().findFragmentByTag("chooseSpriteVariablesDialogFragment");
			this.chooseSpriteVariablesFragment.setCatKeyboardView(this);
		}

		//		LayoutParams relative = new LayoutParams(source);
		//		this.symbols.setShifted(false);
		//		this.symbols_shifted.setShifted(true);
		//		this.setBackgroundColor(0xFF6103);
		//		this.awakenScrollBars();
		//
		//		ArrayList<Key> keys = (ArrayList<Key>) this.symbols.getKeys();
		//
		//				for (int i = 0; i < keys.size(); i++) {
		//					Key key = keys.get(i);
		//					key.iconPreview = key.icon;
		//					key.popupCharacters = key.label;
		//				}

		//    public CatKeyboardView(Context context, AttributeSet attrs, int defStyle) {
		//        super(context, attrs, defStyle);

		Log.i("info", "CatKeyboardView()-Constructor");
	}

	//	@Override
	//	protected void onRestoreInstanceState(Parcelable state) {
	//		Log.i("info", "CatKeyboardView.onRestoreInstanceState()");
	//
	//		this.chooseSpriteVariablesFragment.setCatKeyboardView(this);
	//
	//		super.onRestoreInstanceState(state);
	//	}

	private void setSwipeBarBackground(int position) {
		//int color = context.getResources().getColor(R.color.formula_editor_background);
		//int colors[] = { color, 0x0066CC };
		Drawable background = null;
		int width = swipeBar.getWidth();
		switch (position) {
			case FUNCTION_KEYBOARD:
				//		int colors[] = { color, 0x0066CC };
				background = context.getResources().getDrawable(R.drawable.formula_editor_keyboard_tab_bar_right);
				break;
			case SENSOR_KEYBOARD:
				//		int colors2[] = { color, 0x00FFFF };
				background = context.getResources().getDrawable(R.drawable.formula_editor_keyboard_tab_bar_center);
				break;
			case NUMBER_KEYBOARD:
				//		int colors3[] = { color, 0xF0C6E2 };
				background = context.getResources().getDrawable(R.drawable.formula_editor_keyboard_tab_bar_left);
				break;
			default:

		}
		swipeBar.setBackgroundDrawable(null);
		swipeBar.setBackgroundDrawable(background);

		//swipeBar.invalidate();
		//test_view.invalidate();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.formula_editor_caption_center:
				setSwipeBarBackground(NUMBER_KEYBOARD);
				this.setKeyboard(this.symbolsNumbers);
				break;
			case R.id.formula_editor_caption_left:
				setSwipeBarBackground(SENSOR_KEYBOARD);
				this.setKeyboard(this.symbolsSensors);
				break;
			case R.id.formula_editor_caption_right:
				setSwipeBarBackground(FUNCTION_KEYBOARD);
				this.setKeyboard(this.symbolsFunctions);
				break;
		}
	}

	public void init(FormulaEditorEditText editText, View swipeBar) {
		this.editText = editText;
		this.swipeBar = (RelativeLayout) swipeBar;
		setSwipeBarBackground(1);
	}

	//	public void setCurrentBrick(Brick currentBrick) {
	//		this.currentBrick = currentBrick;
	//
	//		if (this.currentBrick.toString().startsWith("at.tugraz.ist.catroid.content.bricks.NXTMotorActionBri")
	//				|| this.currentBrick.toString().startsWith("at.tugraz.catroid.content.bricks.NXTPlayToneBri")) {
	//			Log.i("info", "SeekBar Bricket selected:");
	//			ArrayList<Key> sensorKeys = (ArrayList<Key>) this.symbolsSensors.getKeys();
	//			Key slider = sensorKeys.get(sensorKeys.size() - 3);
	//			slider.label = new String("Slider");
	//			slider.codes[0] = CatKeyEvent.KEYCODE_SENSOR7;
	//
	//		}
	//	}

	@Override
	protected boolean onLongPress(Key key) {
		return super.onLongPress(key);
	}

	@Override
	public void onKey(int primaryCode, int[] keyCodes) {

		CatKeyEvent cKE = null;

		switch (primaryCode) {
			case KeyEvent.KEYCODE_0:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_0));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_1:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_1));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_2:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_2));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_3:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_3));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_4:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_4));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_5:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_5));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_6:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_6));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_7:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_7));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_8:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_8));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_9:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_9));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_DEL:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_COMMA:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_COMMA));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_PLUS:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_PLUS));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_MINUS:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MINUS));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_STAR:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_STAR));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_SLASH:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SLASH));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_PERIOD:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_PERIOD));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_POWER:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_POWER));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case KeyEvent.KEYCODE_SHIFT_RIGHT:
				this.swipeRight();
				break;
			case KeyEvent.KEYCODE_SHIFT_LEFT:
				this.swipeLeft();
				break;
			case CatKeyEvent.KEYCODE_SPACE:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SPACE));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SIN:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SIN));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_COS:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_COS));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_TAN:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_TAN));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_LN:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_LN));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_LOG:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_LOG));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_PI:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_PI));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SQUAREROOT:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SQUAREROOT));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_EULER:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_EULER));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_RANDOM:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_RANDOM));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_ABS:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_ABS));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_ROUND:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_ROUND));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SENSOR1:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SENSOR1));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SENSOR2:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SENSOR2));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SENSOR3:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SENSOR3));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SENSOR4:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SENSOR4));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SENSOR5:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SENSOR5));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SENSOR6:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SENSOR6));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_SENSOR7:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_SENSOR7));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_BRACKET:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_BRACKET));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_COSTUME_BUTTON:
				this.chooseSpriteVariablesFragment.show(
						((SherlockFragmentActivity) context).getSupportFragmentManager(),
						"chooseSpriteVariablesDialogFragment");
				break;
			case CatKeyEvent.KEYCODE_COSTUME_X:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_COSTUME_X));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_COSTUME_Y:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_COSTUME_Y));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_COSTUME_GHOSTEFFECT:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_COSTUME_GHOSTEFFECT));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_COSTUME_BRIGTHNESS:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_COSTUME_BRIGTHNESS));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_COSTUME_SIZE:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_COSTUME_SIZE));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_COSTUME_ROTATION:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_COSTUME_ROTATION));
				editText.checkAndModifyKeyInput(cKE);
				break;
			case CatKeyEvent.KEYCODE_COSTUME_LAYER:
				cKE = new CatKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, CatKeyEvent.KEYCODE_COSTUME_LAYER));
				editText.checkAndModifyKeyInput(cKE);
				break;
		}

	}

	//	private void handleKeyboardChange() {
	//		if (this.getKeyboard() == this.symbolsNumbers) {
	//			this.setKeyboard(this.symbolsFunctions);
	//			return;
	//		}
	//		if (this.getKeyboard() == this.symbolsFunctions) {
	//			this.setKeyboard(this.symbolsSensors);
	//			return;
	//		}
	//		if (this.getKeyboard() == this.symbolsSensors) {
	//			this.setKeyboard(this.symbolsNumbers);
	//			return;
	//		}
	//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			default:
				return super.onKeyDown(keyCode, event);

		}

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//Log.i("info", "CatKeyboarView.onKeyUp(), keyCode:" + String.valueOf(keyCode));
		return super.onKeyUp(keyCode, event);

	}

	@Override
	public void swipeDown() {

		//		super.swipeDown();
	}

	@Override
	public void swipeLeft() {
		//Log.i("info", "swipeRight()");

		if (this.getKeyboard() == this.symbolsNumbers) {
			this.setKeyboard(this.symbolsSensors);
			setSwipeBarBackground(SENSOR_KEYBOARD);
			return;
		}
		if (this.getKeyboard() == this.symbolsFunctions) {
			this.setKeyboard(this.symbolsNumbers);
			setSwipeBarBackground(NUMBER_KEYBOARD);
			return;
		}
		if (this.getKeyboard() == this.symbolsSensors) {
			this.setKeyboard(this.symbolsFunctions);
			setSwipeBarBackground(FUNCTION_KEYBOARD);
			return;
		}

	}

	@Override
	public void swipeRight() {

		if (this.getKeyboard() == this.symbolsNumbers) {
			this.setKeyboard(this.symbolsFunctions);
			setSwipeBarBackground(FUNCTION_KEYBOARD);
			return;
		}
		if (this.getKeyboard() == this.symbolsFunctions) {
			this.setKeyboard(this.symbolsSensors);
			setSwipeBarBackground(SENSOR_KEYBOARD);
			return;
		}
		if (this.getKeyboard() == this.symbolsSensors) {
			this.setKeyboard(this.symbolsNumbers);
			setSwipeBarBackground(NUMBER_KEYBOARD);
			return;
		}
	}

	@Override
	public void swipeUp() {

		//		super.swipeUp();
	}

	@Override
	public void onPress(int primaryCode) {
		//		Log.i("info", "CatKeybaordView.onPress(): " + primaryCode);

	}

	@Override
	public void onRelease(int primaryCode) {
		//		Log.i("info", "CatKeybaordView.onRelease(): " + primaryCode);

	}

	@Override
	public void onText(CharSequence text) {
		//		Log.i("info", "CatKeybaordView.onText(): ");

	}
}
