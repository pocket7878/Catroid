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
package at.tugraz.ist.catroid.ui.dialogs;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.MyProjectsActivity;

public class SetDescriptionDialog extends TextDialog {

    public SetDescriptionDialog(MyProjectsActivity myProjectActivity) {
        super(myProjectActivity, myProjectActivity.getString(R.string.description), null);
        initKeyAndClickListener();
    }

    public void handleOkButton() {
        String description = (input.getText().toString());

        String currentProjectName = projectManager.getCurrentProject().getName();
        String projectToChangeName = ((MyProjectsActivity) activity).projectToEdit;

        if (projectToChangeName.equalsIgnoreCase(currentProjectName)) {
            setDescription(description);
            activity.dismissDialog(MyProjectsActivity.DIALOG_SET_DESCRIPTION);
            return;
        }

        projectManager.loadProject(projectToChangeName, activity, false);
        setDescription(description);
        projectManager.loadProject(currentProjectName, activity, false);

        activity.dismissDialog(MyProjectsActivity.DIALOG_SET_DESCRIPTION);
    }

    private void setDescription(String description) {
        projectManager.getCurrentProject().description = description;
        projectManager.saveProject();
    }

    private void initKeyAndClickListener() {
        dialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    handleOkButton();
                    return true;
                }
                return false;
            }
        });

        buttonPositive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOkButton();
            }
        });

        buttonNegative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.dismissDialog(MyProjectsActivity.DIALOG_SET_DESCRIPTION);
            }
        });
    }
}
