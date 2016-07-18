package sneakycoders.visualreact.screens;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sneakycoders.visualreact.R;

public class Preferences extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();
    }

    public static class PreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        // Key of the levels category
        private static final String LEVELS_CATEGORY = "levels_preferences";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            // Prevent no level being selected
            preventNoLevelsSelected(getPreferenceScreen().getSharedPreferences());
        }

        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // Find dependency of the changed preference
            String preferenceCategoryKey = findPreference(key).getDependency();

            // If changed preference is a selected level
            if (LEVELS_CATEGORY.equals(preferenceCategoryKey)) {
                preventNoLevelsSelected(sharedPreferences);
            }
        }

        // Prevent no level being selected
        private void preventNoLevelsSelected(SharedPreferences sharedPreferences) {
            // Selected levels
            List<Preference> selectedLevels = new ArrayList<>();

            // Loop through all preferences
            Map<String, ?> keys = sharedPreferences.getAll();
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                // Store selected levels
                Preference entryPreference = findPreference(entry.getKey());
                if (entryPreference != null && LEVELS_CATEGORY.equals(entryPreference.getDependency()) && entry.getValue().equals(true)) {
                    // Optimization: if we already have 3 selected levels (2 + new one), return
                    if (selectedLevels.size() == 2) {
                        return;
                    }

                    selectedLevels.add(entryPreference);
                }
            }

            // If only one level is selected, disable it so that the user cannot deselect it
            if (selectedLevels.size() == 1) {
                selectedLevels.get(0).setEnabled(false);
            }
            // If two levels are selected, enable both to make sure we don't leave one disabled
            else if (selectedLevels.size() == 2) {
                for (Preference selectedPreference : selectedLevels) {
                    selectedPreference.setEnabled(true);
                }
            }
        }
    }
}
