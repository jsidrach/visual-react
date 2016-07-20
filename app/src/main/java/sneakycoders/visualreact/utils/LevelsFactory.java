package sneakycoders.visualreact.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import sneakycoders.visualreact.R;

// Factory to deal with levels
public class LevelsFactory {
    // Resource identifier formats
    private static final String LEVEL_KEY_FORMAT = "level_{0}_selected";
    private static final String LEVEL_NAME_FORMAT = "level_{0}_name";
    private static final String LEVEL_DESCRIPTION_FORMAT = "level_{0}_description";

    public static List<String> getLevelsSequence(Context c) {
        // Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);

        // Selected levels
        List<String> selectedLevels = getSelectedLevelIds(c);
        // Random order
        Collections.shuffle(selectedLevels);
        // Prevent repeating sequences of levels if not every level has been used yet
        List<String> usedLevels = new ArrayList<>();

        // Calculate number of rounds
        int levelsPerMatch = preferences.getInt("levels_per_match", 1);
        int roundsPerLevel = preferences.getInt("rounds_per_level", 1);
        int totalRounds = levelsPerMatch * roundsPerLevel;
        List<String> roundsSequence = new ArrayList<>();


        // Fill the levels sequence
        while (roundsSequence.size() != totalRounds) {
            // Reset lists if needed
            if (selectedLevels.size() == 0) {
                selectedLevels = new ArrayList<>(usedLevels);
                // Random order
                Collections.shuffle(selectedLevels);
                // Prevent same level being the last of one sequence and first of the next one
                if (selectedLevels.get(0).equals(usedLevels.get(usedLevels.size() - 1))) {
                    String last = selectedLevels.remove(0);
                    selectedLevels.add(last);
                }
                usedLevels = new ArrayList<>();
            }

            // Keep track of added levels
            String level = selectedLevels.remove(0);
            usedLevels.add(level);

            // Add new rounds of selected level
            for (int i = 0; i < roundsPerLevel; i++) {
                roundsSequence.add(level);
            }
        }

        return roundsSequence;
    }

    public static List<String> getSelectedLevelIds(Context c) {
        // Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        // Level ids
        List<String> levelIds = getLevelIds(c);
        // Selected level ids
        List<String> selectedLevelIds = new ArrayList<>();

        // Filter selected ids
        for (String level : levelIds) {
            String levelKey = LevelsFactory.getLevelKey(level);
            if (preferences.getBoolean(levelKey, false)) {
                selectedLevelIds.add(level);
            }
        }

        return selectedLevelIds;
    }

    public static List<String> getLevelIds(Context c) {
        return new ArrayList<>(Arrays.asList(c.getResources().getStringArray(R.array.levels)));
    }

    // public static LevelFragment getLevelFragment(String id)

    public static String getLevelKey(String id) {
        return MessageFormat.format(LEVEL_KEY_FORMAT, id);
    }

    public static String getLevelName(String id, Context c) {
        String nameId = MessageFormat.format(LEVEL_NAME_FORMAT, id);
        return getStringResource(nameId, c);
    }

    public static String getLevelDescription(String id, Context c) {
        String descriptionId = MessageFormat.format(LEVEL_DESCRIPTION_FORMAT, id);
        return getStringResource(descriptionId, c);
    }

    private static String getStringResource(String id, Context c) {
        return c.getString(c.getResources().getIdentifier(id, "string", c.getApplicationContext().getPackageName()));
    }
}
