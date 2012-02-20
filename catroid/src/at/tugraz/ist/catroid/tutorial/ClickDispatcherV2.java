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
import android.graphics.Point;
import android.util.Log;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.tutorial.tasks.Task;
import at.tugraz.ist.catroid.tutorial.tasks.TaskNotification;

/**
 * @author faxxe
 * 
 */
public class ClickDispatcherV2 {
	private Activity activity;
	private Task.Notification currentNotification;

	private LayoutExaminer le;

	public ClickDispatcherV2() {
		le = new LayoutExaminer();
	}

	/**
	 * 
	 */
	public ClickDispatcherV2(String str) {
		// TODO Auto-generated constructor stub

	}

	public void processNotification(TaskNotification task) {
		if (task.getNotificationType() == Task.Notification.CURRENT_PROJECT_BUTTON) {
			dispatchMainMenu();
		}
		if (task.getNotificationType() == Task.Notification.PROJECT_LIST_ITEM) {
			if (task.getNotificationString() != null) {
				dispatchProjectListItem(Integer.parseInt(task.getNotificationString()));
			} else {
				dispatchProjectListItem(0);
			}

		}
		if (task.getNotificationType() == Task.Notification.TAB_SCRIPTS) {
			dispatchScripts("Scripts");
		}
		if (task.getNotificationType() == Task.Notification.TAB_COSTUMES) {
			dispatchScripts("Costumes");
		}
		if (task.getNotificationType() == Task.Notification.TAB_SOUNDS) {
			dispatchScripts("Sounds");
		}
		if (task.getNotificationType() == Task.Notification.SOUNDS_ADD_SOUND) {
			dispatchAddSounds();
		}
		if (task.getNotificationType() == Task.Notification.SCRIPTS_ADD_BRICK) {
			dispatchAddSounds();
		}
		if (task.getNotificationType() == Task.Notification.PROJECT_ADD_SPRITE) {
			dispatchAddSounds();
		}
		if (task.getNotificationType() == Task.Notification.BRICK_ADD_DIALOG) {
			Log.i("faxxe", "BRICK_ADD_DIALOG");
			dispatchAddBrick();
		}
		if (task.getNotificationType() == Task.Notification.BRICK_CATEGORY_DIALOG) {
			Log.i("faxxe", "BRICK_CATEGORY_DIALOG");
			dispatchBrickCategories();
		}
		if (task.getNotificationType() == Task.Notification.BRICK_CATEGORY_DIALOG) {
			Log.i("faxxe", "BRICK_CATEGORY_DIALOG");
		}

	}

	private void dispatchAddBrick() {
		CloudController co = new CloudController();
		co.show();
		co.fadeTo(200, 400);
	}

	private void dispatchBrickCategories() {
		CloudController co = new CloudController();
		co.show();
		co.fadeTo(200, 200);
	}

	public void dispatchAddSounds() {
		Point point = le.getButtonCenterCoordinates(R.id.btn_action_add_sprite);
		CloudController co = new CloudController();
		co.show();
		co.fadeTo(point.x, point.y);
	}

	public void dispatchScripts(String type) {
		//		Point point = le.getButtonCenterCoordinates(R.layout.activity_scripttab);

		Point point = le.getTabCenterCoordinates(type);
		CloudController co = new CloudController();
		co.show();
		co.fadeTo(point.x, point.y);
	}

	public void dispatchProjectListItem(int itemNr) {
		Point point = le.getListItemCenter(itemNr);

		CloudController co = new CloudController();

		co.show();
		co.fadeTo(point.x, point.y);

	}

	public void dispatchMainMenu() {

		Point point = le.getButtonCenterCoordinates(R.id.current_project_button);

		CloudController co = new CloudController();

		co.show();
		co.fadeTo(point.x, point.y);

	}

}
