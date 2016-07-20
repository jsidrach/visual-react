package sneakycoders.visualreact.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import sneakycoders.visualreact.R;

// Factory to deal with levels
public class LevelsFactory {
    // Resource identifier formats
    private static final String LEVEL_KEY_FORMAT = "level_{0}_selected";
    private static final String LEVEL_NAME_FORMAT = "level_{0}_name";
    private static final String LEVEL_DESCRIPTION_FORMAT = "level_{0}_description";

    public static String[] getSelectedLevelIds(Context c) {
        // Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        // Level ids
        String[] levelIds = getLevelIds(c);
        // Selected level ids
        List<String> selectedLevelIds = new ArrayList<>();

        // Filter selected ids
        for (String level : levelIds) {
            String levelKey = LevelsFactory.getLevelKey(level);
            if (preferences.getBoolean(levelKey, false)) {
                selectedLevelIds.add(level);
            }
        }
        return selectedLevelIds.toArray(new String[selectedLevelIds.size()]);
    }

    public static String[] getLevelIds(Context c) {
        return c.getResources().getStringArray(R.array.levels);
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
