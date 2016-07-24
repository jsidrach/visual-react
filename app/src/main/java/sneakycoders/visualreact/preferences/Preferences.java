package sneakycoders.visualreact.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import java.util.List;

import sneakycoders.visualreact.R;
import sneakycoders.visualreact.level.LevelsFactory;

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
            // Get context
            Context context = getActivity();
            // Level ids
            List<String> levels = LevelsFactory.getLevelIds(context);
            // Get levels category
            PreferenceCategory category = (PreferenceCategory) findPreference(LEVELS_CATEGORY);
            // Get screen
            PreferenceScreen screen = this.getPreferenceScreen();

            // Add one checkbox for each level
            for (String level : levels) {
                CheckBoxPreference levelSelected = new CheckBoxPreference(screen.getContext());
                levelSelected.setKey(LevelsFactory.getLevelKey(level));
                levelSelected.setTitle(LevelsFactory.getLevelName(level, context));
                levelSelected.setSummary(LevelsFactory.getLevelDescription(level, context));
                levelSelected.setDefaultValue(true);
                levelSelected.setPersistent(true);
                category.addPreference(levelSelected);
            }

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

        private void preventNoLevelsSelected() {
            // Context
            Context context = getActivity();
            // Selected level ids
            List<String> selectedLevelIds = LevelsFactory.getSelectedLevelIds(context);

            // Prevent the user from deselecting every level
            // If only one level is selected, disable it so that the user cannot deselect it
            if (selectedLevelIds.size() == 1) {
                String levelKey = LevelsFactory.getLevelKey(selectedLevelIds.get(0));
                findPreference(levelKey).setEnabled(false);
            }
            // If two levels are selected, enable both to make sure we don't leave one disabled
            else if (selectedLevelIds.size() == 2) {
                for (String selectedLevelId : selectedLevelIds) {
                    String levelKey = LevelsFactory.getLevelKey(selectedLevelId);
                    findPreference(levelKey).setEnabled(true);
                }
            }
        }
    }
}
