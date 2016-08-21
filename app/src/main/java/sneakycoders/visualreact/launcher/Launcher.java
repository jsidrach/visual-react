package sneakycoders.visualreact.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

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

        // Add banner ad
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.ad_app_id));
        AdView adView = (AdView) findViewById(R.id.adLauncher);
        AdRequest adRequest = new AdRequest
                .Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
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
