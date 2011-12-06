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
package at.tugraz.ist.catroid.tutorial;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.tutorial.tasks.Task;

/**
 * @author gnu
 * 
 */
public class ClickDispatcher {

	Context context;
	ControlPanel panel;
	Task.Notification currentNotification;
	String notificationValue;
	MotionEvent downTheRoad;
	MotionEvent second;
	boolean scroll;
	boolean flag;

	ClickDispatcher(Context context, ControlPanel panel) {
		this.panel = panel;
		this.context = context;
		scroll = false;
		flag = false;
	}

	public void setCurrentNotification(Task.Notification currentNotification, String notificationValue) {
		this.currentNotification = currentNotification;
		this.notificationValue = notificationValue;
	}

	public void dispatchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			downTheRoad = MotionEvent.obtain(ev);
		}
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			if (flag == false) {
				second = MotionEvent.obtain(ev);
				flag = true;
				return;
			}
			if (scroll == false) {
				scroll = true;
				dispatchEventReally(downTheRoad);
				dispatchEventReally(second);
				dispatchEventReally(ev);
				Log.i("faxxe", "moove!");
			} else {
				Log.i("faxxe", "moove!");
				dispatchEventReally(ev);
			}
		}
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			if (scroll == false) {
				dispatchEventReally(downTheRoad);
			}
			dispatchEventReally(ev);
			scroll = false;
			flag = false;
		}

	}

	public void dispatchEventReally(MotionEvent ev) {

		if (scroll == true) {
			Activity dings = (Activity) context;
			Log.i("faxxe", "dispatching scroll dings" + ev.getAction() + " argl");
			dings.dispatchTouchEvent(ev);
			return;
		}
		int x = (int) ev.getX();
		int y = (int) ev.getY();

		if (panel != null) {
			Rect bounds = panel.getPanelBounds();
			//check if event coordinates are the coordinates of the control panel buttons
			if (x >= bounds.left && x <= bounds.right) {
				if (y >= bounds.top && y <= bounds.bottom) {
					dispatchPanel(ev);
					return;
				}
			}
		}

		if (currentNotification == null) {
			return;
		}

		if (currentNotification == Task.Notification.CURRENT_PROJECT_BUTTON) {
			if (context.getClass().getName().compareTo("at.tugraz.ist.catroid.ui.MainMenuActivity") != 0) {
				// TODO: This should never ever happen, something went terribly wrong: how to deal with this?
				return;
			}

			dispatchMainMenu(ev);
			return;
		}

		if (currentNotification == Task.Notification.PROJECT_ADD_SPRITE
				|| currentNotification == Task.Notification.PROJECT_HOME_BUTTON
				|| currentNotification == Task.Notification.PROJECT_LIST_ITEM
				|| currentNotification == Task.Notification.PROJECT_STAGE_BUTTON) {
			if (context.getClass().getName().compareTo("at.tugraz.ist.catroid.ui.ProjectActivity") != 0) {
				// TODO: This should never ever happen, something went terribly wrong: how to deal with this?
				return;
			}
			dispatchProject(ev);
			return;
		}

		if (currentNotification == Task.Notification.SCRIPTS_ADD_BRICK
				|| currentNotification == Task.Notification.SCRIPTS_TO_COSTUMES
				|| currentNotification == Task.Notification.SCRIPTS_TO_SOUNDS) {
			if (context.getClass().getName().compareTo("at.tugraz.ist.catroid.ui.ScriptActivity") != 0) {
				// TODO: This should never ever happen, something went terribly wrong: how to deal with this?
			}
			dispatchSkript(ev);
			return;
		}

		return;
	}

	public void dispatchScrollEvent(MotionEvent ev) {
		Activity currentActivity = (Activity) context;
		currentActivity.dispatchTouchEvent(ev);

		Log.i("faxxe", "dispatch scrolling event...");
		return;
	}

	public void dispatchPanel(MotionEvent ev) {
		//unterscheide buttons play, pause, forward, backward
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		Rect bounds = panel.getPanelBounds();

		//		check if event coordinates are the coordinates of the control panel buttons
		if (x >= bounds.left && x <= bounds.left + 50) {
			if (y >= bounds.top && y <= bounds.bottom) {
				Tutorial.getInstance(null).resumeTutorial();
				//panel.pressPlay();
			}
		}
		bounds.left = bounds.left + 70;

		if (x >= bounds.left && x <= bounds.left + 50) {
			if (y >= bounds.top && y <= bounds.bottom) {
				Tutorial.getInstance(null).pauseTutorial();
				//Toast toast = Toast.makeText(context, "PAUSE", Toast.LENGTH_SHORT);
				//toast.show();
			}
		}

		bounds.left = bounds.left + 70;

		if (x >= bounds.left && x <= bounds.left + 50) {
			if (y >= bounds.top && y <= bounds.bottom) {
				Toast toast = Toast.makeText(context, "FORWARD", Toast.LENGTH_SHORT);
				toast.show();
			}
		}

		bounds.left = bounds.left + 70;

		if (x >= bounds.left && x <= bounds.left + 50) {
			if (y >= bounds.top && y <= bounds.bottom) {
				Toast toast = Toast.makeText(context, "BACKWARD", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	public void dispatchSkript(MotionEvent ev) {
		Activity currentActivity = (Activity) context;
		Activity parentActivity = currentActivity.getParent();
		ImageButton lila = (ImageButton) parentActivity.findViewById(R.id.btn_action_add_sprite);

		int x = lila.getLeft();
		int y = lila.getTop();
		int maxx = x + lila.getHeight();
		int maxy = y + lila.getWidth();
		Log.i("faxxe", x + " " + y + " " + maxx + " " + maxy);
		Log.i("faxxe", ev.getX() + " " + ev.getY());
		Dialog currentDialog = Tutorial.getInstance(null).getDialog();
		if (currentDialog == null) {
			parentActivity.dispatchTouchEvent(ev);
		} else {
			currentDialog.dispatchTouchEvent(ev);
		}

		//		if (ev.getX() > x && ev.getY() > y && ev.getX() < maxx && ev.getY() < maxy && currentDialog == null) {
		//			dongs.dispatchTouchEvent(ev);
		//		}
		//		Log.i("faxxe", "schauma ob a einegeht!");
		//		if (Tutorial.getInstance(null).getDialog() != null) {
		//			Log.i("faxxe", "geht eh eine!");
		//			Tutorial.getInstance(null).getDialog().dispatchTouchEvent(ev);
		//			//Tutorial.getInstance(null).setDialog(null);
		//		}
	}

	public void dispatchProject(MotionEvent ev) {
		Activity currentActivity = (Activity) context;

		if (currentNotification == Task.Notification.PROJECT_ADD_SPRITE) {
			ImageButton addSpriteButton = (ImageButton) currentActivity.findViewById(R.id.btn_action_add_sprite);
			if (isImageButtonClicked(ev, addSpriteButton)) {
				currentActivity.dispatchTouchEvent(ev);
			}
		}

		if (currentNotification == Task.Notification.PROJECT_HOME_BUTTON) {

		}

		if (currentNotification == Task.Notification.PROJECT_STAGE_BUTTON) {

		}

		if (currentNotification == Task.Notification.PROJECT_LIST_ITEM) {
			ListActivity listActivity = (ListActivity) context;
			ListView listView = listActivity.getListView();

			// TODO: There has to be added a possibility to address specific entries of the list
			if (isListItemClicked(ev, listView, Integer.parseInt(notificationValue)) != -1) {
				listView.dispatchTouchEvent(ev);
			}
		}
	}

	public int isListItemClicked(MotionEvent event, ListView listView, int index) {
		event.setLocation(event.getX(), event.getY() - 100); // please anyone find out the real height of the titlebar!
		int y = listView.getChildAt(index).getTop();
		int x = listView.getChildAt(index).getLeft();
		int maxx = listView.getChildAt(index).getRight();
		int maxy = listView.getChildAt(index).getBottom();
		Log.i("faxxe", "touched!" + x + " " + y + " " + maxx + " " + maxy + " " + event.getX() + " " + event.getY());

		if (event.getX() < maxx && event.getX() > x && event.getY() < maxy && event.getY() > y) {
			Log.i("faxxe",
					"irgendwos" + x + " " + y + " " + maxx + " " + maxy + " " + event.getX() + " " + event.getY());
			listView.dispatchTouchEvent(event);
		}

		return -1;
	}

	public void dispatchMainMenu(MotionEvent ev) {
		Log.i("faxxe", "mainmenudispatcher");
		Activity currentActivity = (Activity) context;
		Button currentProjectButton = (Button) currentActivity.findViewById(R.id.current_project_button);
		Button aboutButton = (Button) currentActivity.findViewById(R.id.about_catroid_button);
		Button webButton = (Button) currentActivity.findViewById(R.id.web_button);
		Button newProjectButton = (Button) currentActivity.findViewById(R.id.new_project_button);

		if (currentNotification == Task.Notification.CURRENT_PROJECT_BUTTON) {
			if (isButtonClicked(ev, currentProjectButton)) {
				currentActivity.dispatchTouchEvent(ev);
			}
		}

		//		if (isButtonClicked(ev, tutorialButton)) {
		//			currentActivity.dispatchTouchEvent(ev);
		//		}
	}

	public boolean isButtonClicked(MotionEvent event, Button button) {
		int location[] = new int[2];
		button.getLocationOnScreen(location);
		int width = button.getWidth();
		int height = button.getHeight();

		if (event.getX() > location[0] && event.getX() < location[0] + width && event.getY() > location[1]
				&& event.getY() < location[1] + height) {
			return (true);
		} else {
			return (false);
		}
	}

	public boolean isImageButtonClicked(MotionEvent event, ImageButton button) {
		int location[] = new int[2];
		button.getLocationOnScreen(location);
		int width = button.getWidth();
		int height = button.getHeight();

		if (event.getX() > location[0] && event.getX() < location[0] + width && event.getY() > location[1]
				&& event.getY() < location[1] + height) {
			return (true);
		} else {
			return (false);
		}
	}

}
