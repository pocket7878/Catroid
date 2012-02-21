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
		switch (task.getNotificationType()) {
			case CURRENT_PROJECT_BUTTON:
				dispatchButton(R.id.current_project_button);
				break;
			case PROJECT_LIST_ITEM:
				if (task.getNotificationString() != null) {
					dispatchProjectListItem(Integer.parseInt(task.getNotificationString()));
				} else {
					dispatchProjectListItem(0);
				}
				break;
			case TAB_SCRIPTS:
				dispatchScripts("Scripts");
				break;
			case TAB_COSTUMES:
				dispatchScripts("Costumes");
				break;
			case TAB_SOUNDS:
				dispatchScripts("Sounds");
				break;
			case SOUNDS_ADD_SOUND:
			case SCRIPTS_ADD_BRICK:
			case PROJECT_ADD_SPRITE:
				dispatchButton(R.id.btn_action_add_sprite);
				break;
			case BRICK_ADD_DIALOG:
				Log.i("faxxe", "BRICK_ADD_DIALOG");
				if (task.getNotificationString() != null) {
					dispatchAddBrick(Integer.parseInt(task.getNotificationString()));
				} else {
					dispatchAddBrick(0);
				}
				break;
			case BRICK_CATEGORY_DIALOG:
				if (task.getNotificationString() != null) {
					dispatchBrickCategories(Integer.parseInt(task.getNotificationString()));
				} else {
					dispatchBrickCategories(0);
				}
				break;
			case PROJECT_STAGE_BUTTON:
				dispatchButton(R.id.btn_action_play);
				break;
		}
	}

	private void dispatchAddBrick(int itemNr) {
		ClickableArea ca = le.examineAddBrickDialog(itemNr);
		CloudController co = new CloudController();
		co.show();
		co.fadeTo(ca);
	}

	private void dispatchBrickCategories(int itemNr) {
		ClickableArea ca = le.examineCategoryBrickDialog(itemNr);
		CloudController co = new CloudController();
		co.show();
		co.fadeTo(ca);
	}

	public void dispatchAddSounds() {
		ClickableArea ca = le.getButtonCenterCoordinates(R.id.btn_action_add_sprite);
		CloudController co = new CloudController();
		co.show();
		co.fadeTo(ca);
	}

	public void dispatchScripts(String type) {
		ClickableArea ca = le.getTabCenterCoordinates(type);
		CloudController co = new CloudController();
		co.show();
		co.fadeTo(ca);
	}

	public void dispatchProjectListItem(int itemNr) {
		ClickableArea ca = le.getListItemCenter(itemNr);
		CloudController co = new CloudController();
		co.show();
		co.fadeTo(ca);

	}

	//	public void dispatchMainMenu() {
	//		ClickableArea ca = le.getButtonCenterCoordinates(R.id.current_project_button);
	//		CloudController co = new CloudController();
	//		co.show();
	//		co.fadeTo(ca);
	//	}

	public void dispatchButton(int button) {
		ClickableArea ca = le.getButtonCenterCoordinates(button);
		CloudController co = new CloudController();
		co.show();
		co.fadeTo(ca);
	}

}
