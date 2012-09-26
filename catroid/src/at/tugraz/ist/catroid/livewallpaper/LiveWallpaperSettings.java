package at.tugraz.ist.catroid.livewallpaper;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.dialogs.AboutDialog;
import at.tugraz.ist.catroid.ui.dialogs.ProjectInformationDialog;

public class LiveWallpaperSettings extends PreferenceActivity {

	Context context;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		addPreferencesFromResource(R.xml.livewallpapersettings);
		handleLicencePreference();
		handleProjectInformation();
		handleSoundPreference();

	}

	@SuppressWarnings("deprecation")
	private void handleLicencePreference() {
		Preference licence = findPreference(getResources().getString(R.string.lwp_about_catroid));

		licence.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				AboutDialog aboutDialog = new AboutDialog(context);
				aboutDialog.show();
				return false;
			}
		});

	}

	private void handleProjectInformation() {
		@SuppressWarnings("deprecation")
		Preference projectInformation = findPreference(getResources().getString(R.string.lwp_project_information));

		projectInformation.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				ProjectInformationDialog projectInformationDialog = new ProjectInformationDialog(context);
				projectInformationDialog.show();
				return false;
			}

		});

	}

	private void handleSoundPreference() {
		@SuppressWarnings("deprecation")
		final CheckBoxPreference allowSounds = (CheckBoxPreference) findPreference(getResources().getString(
				R.string.lwp_sound_control));

		allowSounds.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (newValue.toString().equals("true")) {
					WallpaperHelper.getInstance().setSoundAllowed(true);
					allowSounds.setChecked(true);
				} else {
					WallpaperHelper.getInstance().setSoundAllowed(false);
					allowSounds.setChecked(false);
				}
				return false;
			}
		});
	}

}
