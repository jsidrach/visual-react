package sneakycoders.visualreact.screens;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

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

            // Get levels category
            PreferenceCategory category = (PreferenceCategory) findPreference(LEVELS_CATEGORY);
            // Get screen
            PreferenceScreen screen = this.getPreferenceScreen();

            // Add every level
            String[] levels = getResources().getStringArray(R.array.levels);

            // Package name
            String packageName = getActivity().getApplicationContext().getPackageName();

            // Selected levels
            List<Preference> levelsSelected = new ArrayList<>();

            // Add one checkbox for each level
            for (String level : levels) {
                CheckBoxPreference levelSelected = new CheckBoxPreference(screen.getContext());
                levelSelected.setDefaultValue(true);
                levelSelected.setKey("level_" + level + "_selected");
                levelSelected.setPersistent(true);
                levelSelected.setSummary(getString(getResources().getIdentifier("level_" + level + "_description", "string", packageName)));
                levelSelected.setTitle(getString(getResources().getIdentifier("level_" + level + "_name", "string", packageName)));
                category.addPreference(levelSelected);
                levelsSelected.add(levelSelected);
            }

            // Set preference screen
            setPreferenceScreen(screen);

            // Add dependencies
            for (Preference level : levelsSelected) {
                level.setDependency(LEVELS_CATEGORY);
            }

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
