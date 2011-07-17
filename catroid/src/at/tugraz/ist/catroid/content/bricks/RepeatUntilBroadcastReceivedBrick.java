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
package at.tugraz.ist.catroid.content.bricks;

import java.util.concurrent.CountDownLatch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.IBroadcastReceiver;
import at.tugraz.ist.catroid.content.Sprite;

public class RepeatUntilBroadcastReceivedBrick extends LoopBeginBrick implements IBroadcastReceiver {
	private static final long serialVersionUID = 1L;
	private String broadcastMessage = "";

	public RepeatUntilBroadcastReceivedBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	@Override
	public void execute() {
		loopEndBrick.setTimesToRepeat(LoopEndBrick.FOREVER);
	}

	@Override
	public Brick clone() {
		return new RepeatUntilBroadcastReceivedBrick(getSprite());
	}

	public View getView(final Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_repeat_until_broadcast_received, null);

		final Spinner spinner = (Spinner) brickView.findViewById(R.id.broadcast_spinner);
		spinner.setAdapter(ProjectManager.getInstance().messageContainer.getMessageAdapter(context));
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			private boolean start = true;

			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if (start) {
					start = false;
					return;
				}
				String message = ((String) parent.getItemAtPosition(pos)).trim();

				if (message == context.getString(R.string.broadcast_nothing_selected)) {
					setBroadcastMessage("");
				} else {
					setBroadcastMessage(message);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		int pos = ProjectManager.getInstance().messageContainer.getPosOfMessageInAdapter(broadcastMessage);
		if (pos > 0) {
			spinner.setSelection(pos);
		}
		Button newBroadcastMessage = (Button) brickView.findViewById(R.id.broadcast_new_message);
		newBroadcastMessage.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				final EditText input = new EditText(context);

				builder.setView(input);
				builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String newMessage = (input.getText().toString()).trim();
						if (newMessage.length() == 0
								|| newMessage.equals(context.getString(R.string.broadcast_nothing_selected))) {
							dialog.cancel();
							return;
						}
						setBroadcastMessage(newMessage);
						int pos = ProjectManager.getInstance().messageContainer.getPosOfMessageInAdapter(newMessage);
						spinner.setSelection(pos);
					}
				});
				builder.setNegativeButton(context.getString(R.string.cancel_button),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});

				AlertDialog alertDialog = builder.create();
				alertDialog.setOnShowListener(new OnShowListener() {
					public void onShow(DialogInterface dialog) {
						InputMethodManager inputManager = (InputMethodManager) context
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						inputManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
					}
				});
				alertDialog.show();
			}
		});

		spinner.setFocusable(false);
		newBroadcastMessage.setFocusable(false);
		return brickView;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_repeat_until_broadcast_received, null);
		return view;
	}

	public void setBroadcastMessage(String broadcastMessage) {
		ProjectManager.getInstance().messageContainer.deleteReceiver(this.broadcastMessage, this);
		this.broadcastMessage = broadcastMessage;
		ProjectManager.getInstance().messageContainer.addMessage(this.broadcastMessage, this);
	}

	public String getBroadcastMessage() {
		return broadcastMessage;
	}

	public void executeBroadcast(final CountDownLatch simultaneousStart) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					simultaneousStart.await();
				} catch (InterruptedException e) {
				}
				loopEndBrick.setTimesToRepeat(0);
			}
		});
		t.start();
	}

	public void executeBroadcastWait(final CountDownLatch simultaneousStart, final CountDownLatch wait) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					simultaneousStart.await();
				} catch (InterruptedException e) {
				}
				loopEndBrick.setTimesToRepeat(0);
				loopEndBrick.setLoopEndCountDownLatch(wait);
			}
		});
		t.start();
	}
}