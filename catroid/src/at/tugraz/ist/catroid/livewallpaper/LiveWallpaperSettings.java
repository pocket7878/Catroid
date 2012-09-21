package at.tugraz.ist.catroid.livewallpaper;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
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

}
