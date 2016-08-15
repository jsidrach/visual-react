package sneakycoders.visualreact.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import sneakycoders.visualreact.R;
import sneakycoders.visualreact.match.Match;
import sneakycoders.visualreact.preferences.Preferences;

public class Launcher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Inflate layout
        setContentView(R.layout.launcher);
    }

    public void switchToPreferences(@SuppressWarnings("UnusedParameters") View view) {
        // Switch to Preferences screen
        Intent intent = new Intent(Launcher.this, Preferences.class);
        startActivity(intent);
    }

    public void switchToMatch(@SuppressWarnings("UnusedParameters") View view) {
        // Switch to Match screen
        Intent intent = new Intent(Launcher.this, Match.class);
        startActivity(intent);
    }
}
