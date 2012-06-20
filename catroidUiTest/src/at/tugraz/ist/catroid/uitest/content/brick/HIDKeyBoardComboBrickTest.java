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
package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.ArrayList;

import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.HIDComboBrick;
import at.tugraz.ist.catroid.content.bricks.HIDComboEndBrick;
import at.tugraz.ist.catroid.content.bricks.HIDKeyBoardButtonBrick;
import at.tugraz.ist.catroid.content.bricks.SetBrightnessBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;

import com.jayway.android.robotium.solo.Solo;

public class HIDKeyBoardComboBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private ArrayList<Brick> startbrickListToCheck;
	private ArrayList<Brick> firstbrickListToCheck;
	Script startScript;
	Script firstScript;
	private Sprite firstSprite;

	public HIDKeyBoardComboBrickTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject("testProject");
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		super.tearDown();
	}

	private ArrayList<Integer> getListItemYPositions() {
		ArrayList<Integer> yPositionList = new ArrayList<Integer>();
		ListView listView = solo.getCurrentListViews().get(0);
		for (int i = 0; i < listView.getChildCount(); ++i) {
			View currentViewInList = listView.getChildAt(i);

			Rect globalVisibleRect = new Rect();
			currentViewInList.getGlobalVisibleRect(globalVisibleRect);
			int middleYPos = globalVisibleRect.top + globalVisibleRect.height() / 2;
			yPositionList.add(middleYPos);
		}

		return yPositionList;
	}

	private void longClickAndDrag(final float xFrom, final float yFrom, final float xTo, final float yTo,
			final int steps) {
		Handler handler = new Handler(getActivity().getMainLooper());

		handler.post(new Runnable() {

			public void run() {
				MotionEvent downEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_DOWN, xFrom, yFrom, 0);
				getActivity().dispatchTouchEvent(downEvent);
			}
		});

		solo.sleep(ViewConfiguration.getLongPressTimeout() + 20);

		handler.post(new Runnable() {
			public void run() {

				for (int i = 0; i <= steps; i++) {
					float x = xFrom + (((xTo - xFrom) / steps) * i);
					float y = yFrom + (((yTo - yFrom) / steps) * i);
					MotionEvent moveEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
							MotionEvent.ACTION_MOVE, x, y, 0);
					getActivity().dispatchTouchEvent(moveEvent);
					solo.sleep(20);
				}
			}
		});

		solo.sleep(steps * 20 + 20);

		handler.post(new Runnable() {

			public void run() {
				MotionEvent upEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_UP, xTo, yTo, 0);
				getActivity().dispatchTouchEvent(upEvent);
			}
		});

	}

	@Smoke
	public void testMoveKeyBoardBrickToCombo() {

		ArrayList<Integer> yPositionList = getListItemYPositions();
		assertTrue("Test project brick list smaller than expected. Maybe Display to small for all bricks?",
				yPositionList.size() == 6);
		longClickAndDrag(10, yPositionList.get(1), 10, (yPositionList.get(2)) + 5, 20);
		solo.sleep(2000);
		yPositionList = getListItemYPositions();
		BrickAdapter adapter = ((ScriptActivity) getActivity().getCurrentActivity()).getAdapter();
		assertTrue("KeyBrick is not in the Combo", adapter.getItem(2) instanceof HIDKeyBoardButtonBrick);

		yPositionList = getListItemYPositions();
		longClickAndDrag(10, yPositionList.get(5), 10, yPositionList.get(3) - 5, 20);
		adapter = ((ScriptActivity) getActivity().getCurrentActivity()).getAdapter();
		assertTrue("BrightnessBrick is in the Combo", !(adapter.getItem(3) instanceof SetBrightnessBrick));
		//
		//		int numberOfBricks = ProjectManager.getInstance().getCurrentScript().getBrickList().size();
		//		longClickAndDrag(10, yPositionList.get(6), 10, yPositionList.get(2), 20);
		//
		//		assertTrue("Number of Bricks inside Script hasn't changed", (numberOfBricks + 1) == ProjectManager
		//				.getInstance().getCurrentScript().getBrickList().size());
		//
		//		Adapter adapter = ((ScriptActivity) getActivity().getCurrentActivity()).getAdapter();
		//
		//		assertEquals("Incorrect Brick after dragging over Script",
		//				(Brick) adapter.getItem(2) instanceof HIDKeyBoardButtonBrick, true);
		//		yPositionList = getListItemYPositions();
		//		longClickAndDrag(10, yPositionList.get(7), 10, yPositionList.get(2), 20);
		//
		//		assertEquals("Number of Bricks inside ComboBrick has changed. Wait Brick in Combo should not be possible.",
		//				(Brick) adapter.getItem(2) instanceof HIDKeyBoardButtonBrick, true);
		//
		//		adapter = ((ScriptActivity) getActivity().getCurrentActivity()).getAdapter();
		//
		//		assertEquals("Incorrect Brick should be at the end of the Combo",
		//				(Brick) adapter.getItem(4) instanceof WaitBrick, true);

	}

	private void createProject(String projectName) {

		Project project = new Project(null, projectName);
		firstSprite = new Sprite("cat");

		startScript = new StartScript(firstSprite);
		firstScript = new WhenScript(firstSprite);

		startbrickListToCheck = new ArrayList<Brick>();
		startbrickListToCheck.add(new HIDKeyBoardButtonBrick(firstSprite));
		HIDComboBrick startCombo = new HIDComboBrick(firstSprite);
		HIDComboEndBrick startEnd = new HIDComboEndBrick(firstSprite, startCombo);
		startCombo.setLoopEndBrick(startEnd);
		startbrickListToCheck.add(startCombo);
		startbrickListToCheck.add(startEnd);

		firstbrickListToCheck = new ArrayList<Brick>();
		SetBrightnessBrick brightBrick = new SetBrightnessBrick(firstSprite, 100);
		firstbrickListToCheck.add(brightBrick);

		// adding Bricks: ----------------
		for (Brick brick : startbrickListToCheck) {
			startScript.addBrick(brick);
		}

		for (Brick brick : firstbrickListToCheck) {
			firstScript.addBrick(brick);
		}

		// -------------------------------

		firstSprite.addScript(startScript);
		firstSprite.addScript(firstScript);

		project.addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
	}
}
