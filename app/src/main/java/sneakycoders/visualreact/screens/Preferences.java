package sneakycoders.visualreact.screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.google.common.collect.Lists;
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

        // A HashSet to store the keys of all the level names
       private static final List<String> LEVEL_KEYS = Lists.newArrayList("level_color_selected", "level_light_selected",
                                                                          "level_countdown_selected", "level_collision_selected",
                                                                          "level_connection_selected");

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
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
            int count = 0;   // how many levels are selected
            List<Preference> LEVEL_GAMES = Lists.newArrayList(); // a list to store all the checkbox preferences
            for (String level_key: LEVEL_KEYS) {
                LEVEL_GAMES.add(findPreference(level_key));
            }
            if (LEVEL_KEYS.contains((key))) {
                for (Preference preference : LEVEL_GAMES) {
                    if (preference instanceof CheckBoxPreference && ((CheckBoxPreference) preference).isChecked()) {
                        count++;
                    }
                }
                switch (count) {
                    case 0:
                        CheckBoxPreference disabled = (CheckBoxPreference) findPreference(key);

                        // show a dialog to remind user at least one checkbox must be selected
                        AlertDialog warning = new AlertDialog.Builder(this.getActivity()).create();
                        warning.setTitle("WARNING!");
                        warning.setCancelable(true);
                        warning.setMessage("You have to choose at least one level!");
                        warning.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        // set the checkbox to checked and disable it
                        disabled.setChecked(true);
                        disabled.setEnabled(false);
                        warning.show();
                        break;

                    default:
                        for (Preference preference: LEVEL_GAMES) {
                            preference.setEnabled(true);
                        }
                        break;
                }
            }
        }
    }
}
