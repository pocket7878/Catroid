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
package at.tugraz.ist.catroid.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.adapter.SoundAdapter;
import at.tugraz.ist.catroid.utils.ActivityHelper;
import at.tugraz.ist.catroid.utils.Utils;

public class SoundActivity extends ListActivity {

	private class CopyAudioFilesTask extends AsyncTask<String, Void, File> {
		private ProgressDialog mDialog = new ProgressDialog(SoundActivity.this);

		@Override
		protected void onPreExecute() {
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mDialog.setTitle(getString(R.string.loading));
			mDialog.show();
		}

		@Override
		protected File doInBackground(String... path) {
			File file = null;
			try {
				file = StorageHandler.getInstance().copySoundFile(path[0]);
			} catch (IOException e) {
				Log.e("CATROID", "Cannot load sound.", e);
			}
			return file;
		}

		@Override
		protected void onPostExecute(File file) {
			mDialog.dismiss();

			if (file != null) {
				String fileName = file.getName();
				String soundTitle = fileName.substring(fileName.indexOf('_') + 1, fileName.lastIndexOf('.'));
				updateSoundAdapter(soundTitle, fileName);
			} else {
				Utils.displayErrorMessage(SoundActivity.this, getString(R.string.error_load_sound));
			}
		}
	}

	private final int REQUEST_SELECT_MUSIC = 0;
	public MediaPlayer mediaPlayer;
	private ArrayList<SoundInfo> soundInfoList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sound);
		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();

		setListAdapter(new SoundAdapter(this, R.layout.activity_sound_soundlist_item, soundInfoList));
	}

	@Override
	protected void onStart() {
		super.onStart();
		mediaPlayer = new MediaPlayer();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.checkForSdCard(this)) {
			return;
		}

		stopSound();
		reloadAdapter();

		//change actionbar:
		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getParent();
		ActivityHelper activityHelper = scriptTabActivity.activityHelper;
		if (activityHelper != null) {
			//set new functionality for actionbar add button:
			activityHelper.changeClickListener(R.id.btn_action_add_button, createAddSoundClickListener());
			//set new icon for actionbar plus button:
			activityHelper.changeButtonIcon(R.id.btn_action_add_button, R.drawable.ic_music);
		}

	}

	private View.OnClickListener createAddSoundClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("audio/*");
				startActivityForResult(Intent.createChooser(intent, getString(R.string.sound_select_source)),
						REQUEST_SELECT_MUSIC);
			}
		};
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopSound();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mediaPlayer.reset();
		mediaPlayer.release();
		mediaPlayer = null;
	}

	private void updateSoundAdapter(String title, String fileName) {
		title = Utils.getUniqueSoundName(title);

		SoundInfo newSoundInfo = new SoundInfo();
		newSoundInfo.setTitle(title);
		newSoundInfo.setSoundFileName(fileName);
		soundInfoList.add(newSoundInfo);
		((SoundAdapter) getListAdapter()).notifyDataSetChanged();

		//scroll down the list to the new item:
		{
			final ListView listView = getListView();
			listView.post(new Runnable() {
				public void run() {
					listView.setSelection(listView.getCount() - 1);
				}
			});
		}
	}

	public void pauseSound(SoundInfo soundInfo) {
		mediaPlayer.pause();
		soundInfo.isPlaying = false;
	}

	private void stopSound() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}

		for (int i = 0; i < soundInfoList.size(); i++) {
			soundInfoList.get(i).isPlaying = false;
		}
	}

	private void startSound(SoundInfo soundInfo) {
		if (soundInfo.isPlaying) {
			return;
		}
		try {
			mediaPlayer.reset();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setDataSource(soundInfo.getAbsolutePath());
			mediaPlayer.prepare();
			mediaPlayer.start();
			soundInfo.isPlaying = true;
		} catch (IOException e) {
			Log.e("CATROID", "Cannot start sound.", e);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SELECT_MUSIC) {
			String audioPath = "";
			Uri audioUri = data.getData();
			String[] filePathColumn = { MediaStore.Audio.Media.DATA };
			Cursor cursor = managedQuery(audioUri, filePathColumn, null, null, null);

			if (cursor != null) {
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				audioPath = cursor.getString(columnIndex);
			} else {
				audioPath = audioUri.getPath();
			}
			if (audioPath.equalsIgnoreCase("")) {
				Utils.displayErrorMessage(this, this.getString(R.string.error_load_sound));
			} else {
				new CopyAudioFilesTask().execute(audioPath);
			}
		}
	}

	private void reloadAdapter() {
		this.soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		setListAdapter(new SoundAdapter(this, R.layout.activity_sound_soundlist_item, soundInfoList));
		((SoundAdapter) getListAdapter()).notifyDataSetChanged();
	}

	// Does not rename the actual file, only the title in the SoundInfo
	public void handleSoundRenameButton(View v) {
		int position = (Integer) v.getTag();
		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getParent();
		scriptTabActivity.selectedSoundInfo = soundInfoList.get(position);
		scriptTabActivity.showDialog(ScriptTabActivity.DIALOG_RENAME_SOUND);
	}

	public void handlePlaySoundButton(View v) {
		final int position = (Integer) v.getTag();
		final SoundInfo soundInfo = soundInfoList.get(position);

		stopSound();
		if (!soundInfo.isPlaying) {
			startSound(soundInfo);
		}

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				soundInfo.isPlaying = false;
				((SoundAdapter) getListAdapter()).notifyDataSetChanged();
			}
		});

		((SoundAdapter) getListAdapter()).notifyDataSetChanged();
	}

	public void handlePauseSoundButton(View v) {
		final int position = (Integer) v.getTag();
		pauseSound(soundInfoList.get(position));
		((SoundAdapter) getListAdapter()).notifyDataSetChanged();
	}

	public void handleDeleteSoundButton(View v) {
		final int position = (Integer) v.getTag();
		stopSound();
		ScriptTabActivity scriptTabActivity = (ScriptTabActivity) getParent();
		scriptTabActivity.selectedSoundInfo = soundInfoList.get(position);
		scriptTabActivity.selectedPosition = position;
		scriptTabActivity.showDialog(ScriptTabActivity.DIALOG_DELETE_SOUND);
	}
}
