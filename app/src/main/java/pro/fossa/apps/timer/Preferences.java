package pro.fossa.apps.timer;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    // CheckBoxPreference night_mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            EditTextPreference prefix = (EditTextPreference) this.findPreference("PREFS_FILENAME_PREFIX");
            // night_mode = (CheckBoxPreference) this.findPreference("PREFS_NIGHT_MODE");

            prefix.setSummary(prefix.getText());

            // night_mode.setOnPreferenceChangeListener(this);
            // prefix.setOnPreferenceChangeListener(this);
    }
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals("PREFS_FILENAME_PREFIX")) {
			preference.setSummary(newValue.toString());
		}
		return true;
	}
}