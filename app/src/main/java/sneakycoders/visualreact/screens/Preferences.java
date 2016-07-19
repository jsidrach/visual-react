package sneakycoders.visualreact.screens;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

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
        // Resource identifier formats
        private static final String LEVEL_ID_FORMAT = "level_{0}_selected";
        private static final String LEVEL_NAME_FORMAT = "level_{0}_name";
        private static final String LEVEL_DESCRIPTION_FORMAT = "level_{0}_description";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            // Get levels category
            PreferenceCategory category = (PreferenceCategory) findPreference(LEVELS_CATEGORY);
            // Get screen
            PreferenceScreen screen = this.getPreferenceScreen();

            // Level ids
            String[] levels = getResources().getStringArray(R.array.levels);

            // Package name
            String packageName = getActivity().getApplicationContext().getPackageName();

            // Add one checkbox for each level
            for (String level : levels) {
                String levelId = MessageFormat.format(LEVEL_ID_FORMAT, level);
                String levelName = MessageFormat.format(LEVEL_NAME_FORMAT, level);
                String levelDescription = MessageFormat.format(LEVEL_DESCRIPTION_FORMAT, level);
                CheckBoxPreference levelSelected = new CheckBoxPreference(screen.getContext());
                levelSelected.setDefaultValue(true);
                levelSelected.setKey(levelId);
                levelSelected.setPersistent(true);
                levelSelected.setSummary(getString(getResources().getIdentifier(levelDescription, "string", packageName)));
                levelSelected.setTitle(getString(getResources().getIdentifier(levelName, "string", packageName)));
                category.addPreference(levelSelected);
            }

            // Set preference screen
            setPreferenceScreen(screen);

            // Prevent no level being selected
            preventNoLevelsSelected();
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
            // Prevent no level being selected
            preventNoLevelsSelected();
        }

        // Prevent no level being selected
        private void preventNoLevelsSelected() {
            // Selected levels
            List<Preference> selectedLevels = new ArrayList<>();

            // Level ids
            String[] levels = getResources().getStringArray(R.array.levels);

            // Loop through all preferences
            for (String level : levels) {
                // Store selected levels
                String levelId = MessageFormat.format(LEVEL_ID_FORMAT, level);
                CheckBoxPreference levelPreference = (CheckBoxPreference) findPreference(levelId);
                if (levelPreference.isChecked()) {
                    // Optimization: if we already have 3 selected levels (2 + new one), return
                    if (selectedLevels.size() == 2) {
                        return;
                    }

                    selectedLevels.add(levelPreference);
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
