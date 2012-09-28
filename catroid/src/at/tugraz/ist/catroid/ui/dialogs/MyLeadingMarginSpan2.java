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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class MyLeadingMarginSpan2 implements LeadingMarginSpan.LeadingMarginSpan2 {
	private int margin;
	private int lines;

	MyLeadingMarginSpan2(int lines, int margin) {
		this.margin = margin;
		this.lines = lines;
	}

	/* Возвращает значение, на которе должен быть добавлен отступ */
	@Override
	public int getLeadingMargin(boolean first) {
		if (first) {
			/*
			 * Данный отступ будет применен к количеству строк
			 * возвращаемых getLeadingMarginLineCount()
			 */
			return margin;
		} else {
			// Отступ для всех остальных строк
			return 0;
		}
	}

	@Override
	public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom,
			CharSequence text, int start, int end, boolean first, Layout layout) {
	}

	@Override
	public int getLeadingMarginLineCount() {
		return lines;
	}

};