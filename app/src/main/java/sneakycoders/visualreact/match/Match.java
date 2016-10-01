package sneakycoders.visualreact.match;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

import sneakycoders.visualreact.R;
import sneakycoders.visualreact.launcher.Launcher;
import sneakycoders.visualreact.level.Level;
import sneakycoders.visualreact.level.LevelsFactory;

public class Match extends AppCompatActivity {
    // Current state of the match
    private State state;
    // Player 1
    private Player player1;
    // Player 2
    private Player player2;
    // Last player tap information
    private Player lastTap;
    // Remaining levels
    private List<String> remainingLevels;
    // Current level
    private String currentLevelId;
    // Show tips
    private boolean showTips;
    // Level information
    private View levelInfo;
    // Level container
    private View levelContainer;
    // Current level
    private Level currentLevel;
    // Final standings
    private View finalStandings;
    // Handler for callbacks
    private Handler handler;
    // Ad popup
    private InterstitialAd adPopup;
    // Probability of popup on exit/rematch
    private double probPopup;
    // Ad listeners
    private AdListener exitListener;
    private AdListener rematchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Inflate layout
        setContentView(R.layout.match);

        // Prevent screen from turning off
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Add banner ad if active
        AdView adView = (AdView) findViewById(R.id.adIngame);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("show_ads_levels", true)) {
            MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.ad_app_id));
            AdRequest adRequest = new AdRequest
                    .Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            adView.loadAd(adRequest);
        }
        // Remove ad from layout otherwise
        else {
            ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
            rootView.removeView(adView);
            adView.setVisibility(View.GONE);
        }

        // Probability of popup on exit/rematch
        probPopup = getResources().getFraction(R.fraction.match_popup_probability, 1, 1);

        // Add popup ad
        adPopup = new InterstitialAd(this);
        adPopup.setAdUnitId(getResources().getString(R.string.ad_postlevel_unit_id));
        loadPopup();

        // Set listeners for the popup
        rematchListener = new AdListener() {
            @Override
            public void onAdClosed() {
                state = State.LevelSelection;
                displayState();
                loadPopup();
            }
        };
        exitListener = new AdListener() {
            @Override
            public void onAdClosed() {
                switchToLauncher();
            }
        };

        // Set view bindings
        levelInfo = findViewById(R.id.level_info);
        levelContainer = findViewById(R.id.level_container);
        finalStandings = findViewById(R.id.final_standings);

        // Set players information bindings
        player1 = new Player(this, R.id.area_player_1, R.id.score_player_1, R.id.tap_text_player_1, R.id.level_name_player_1, R.id.level_description_player_1, R.id.standing_player_1);
        player2 = new Player(this, R.id.area_player_2, R.id.score_player_2, R.id.tap_text_player_2, R.id.level_name_player_2, R.id.level_description_player_2, R.id.standing_player_2);

        // Read show tips flag
        showTips = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("show_level_tips", false);

        // Set handler
        handler = new Handler();

        // Start match
        startMatch(null);
    }

    public void startMatch(View view) {
        // Levels sequence
        remainingLevels = LevelsFactory.getLevelsSequence(this);

        // Reset current level
        currentLevelId = "";
        currentLevel = null;

        // Reset players
        player1.reset();
        player2.reset();

        // Rematch
        if ((view != null) && (Math.random() < probPopup) && (adPopup.isLoaded())) {
            // Show add before continuing
            adPopup.setAdListener(rematchListener);
            adPopup.show();
        }
        // First match
        else {
            // Set initial state
            state = State.LevelSelection;
            displayState();
        }
    }

    private void displayState() {
        // Transition state, no UI changes needed
        if (state == State.LevelSelection) {
            // Select next level
            String nextLevelId = remainingLevels.remove(0);

            // Choose whether show tips or not
            state = (showTips && !nextLevelId.equals(currentLevelId)) ? State.LevelInfo : State.Level;
            currentLevelId = nextLevelId;

            // Set player states
            player1.setStateSelection();
            player2.setStateSelection();

            // Show/hide corresponding views
            finalStandings.setVisibility(View.GONE);
        }

        // New group of conditions so that level selection completes and falls in one of these first two
        // Level information
        if (state == State.LevelInfo) {
            // Set players states
            String levelName = LevelsFactory.getLevelName(currentLevelId, this);
            String levelDescription = LevelsFactory.getLevelDescription(currentLevelId, this);
            player1.setStateInfo(levelName, levelDescription);
            player2.setStateInfo(levelName, levelDescription);

            // Show/hide corresponding views
            levelContainer.setVisibility(View.GONE);
            levelInfo.setVisibility(View.VISIBLE);
        }
        // Playing a level
        else if (state == State.Level) {
            // Replace current level
            currentLevel = LevelsFactory.getLevel(currentLevelId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.level_container, currentLevel)
                    .commit();

            // Set players states
            player1.setStatePlaying();
            player2.setStatePlaying();

            // Show/hide corresponding views
            levelInfo.setVisibility(View.GONE);
            levelContainer.setVisibility(View.VISIBLE);
        }
        // Level result
        else if (state == State.LevelResult) {
            if (currentLevel.onPlayerTap()) {
                lastTap.setStateSuccess();
            } else {
                lastTap.setStateFail();
            }

            // Switch to next state after a delay
            delayNextState();
        }
        // Final standings
        else {
            // Winner/loser
            if (player1.getScore() != player2.getScore()) {
                boolean player1Wins = player1.getScore() > player2.getScore();
                Player winner = player1Wins ? player1 : player2;
                Player loser = player1Wins ? player2 : player1;
                winner.setStateWinner();
                loser.setStateLoser();
            }
            // Tie
            else {
                player1.setStateTied();
                player2.setStateTied();
            }

            // Show/hide corresponding views
            levelContainer.setVisibility(View.GONE);
            finalStandings.setVisibility(View.VISIBLE);
        }
    }

    public void playerTap(View view) {
        // Switch to level
        if (state == State.LevelInfo) {
            Player player = (view.getId() == R.id.area_player_1) ? player1 : player2;
            player.setReady(true);

            // Actually switch only if both players are ready
            if (player1.isReady() && player2.isReady()) {
                state = State.Level;
                displayState();
            }
        }
        // Store the player who tapped, switch to level result
        else if (state == State.Level) {
            lastTap = (view.getId() == R.id.area_player_1) ? player1 : player2;
            state = State.LevelResult;
            displayState();
        }
        // Do nothing in the rest of the cases
    }

    private void delayNextState() {
        handler.postDelayed(() -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(currentLevel)
                    .commit();
            currentLevel = null;
            state = (remainingLevels.size() > 0) ? State.LevelSelection : State.Standings;
            displayState();
        }, getResources().getInteger(R.integer.match_pause_between_levels));
    }

    @Override
    public void onUserLeaveHint() {
        // Remove all callbacks
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Continue where we left off
        if (state == State.LevelResult) {
            delayNextState();
        }
    }

    @Override
    public void onBackPressed() {
        // Prevent player from accidentally cancelling the match
        if (state != State.Standings) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.cancel_match_confirmation)
                    .setCancelable(false)
                    .setPositiveButton(R.string.cancel_match_yes, (dialog, id) -> leaveMatch())
                    .setNegativeButton(R.string.cancel_match_no, null)
                    .show();
        }
        // No confirmation dialog
        else {
            leaveMatch();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove all callbacks
        handler.removeCallbacksAndMessages(null);
    }

    // States of the match
    private enum State {
        LevelSelection, LevelInfo, Level, LevelResult, Standings
    }

    private void leaveMatch() {
        // Show ad before exiting
        if ((Math.random() < probPopup) && (adPopup.isLoaded())) {
            adPopup.setAdListener(exitListener);
            adPopup.show();
        }
        // Exit directly
        else {
            switchToLauncher();
        }
    }

    private void switchToLauncher() {
        // Switch to Launcher screen
        Intent intent = new Intent(Match.this, Launcher.class);
        startActivity(intent);
        finish();
    }

    private void loadPopup() {
        // Load a new ad for the popup
        AdRequest adRequest = new AdRequest
                .Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adPopup.loadAd(adRequest);
    }
}
