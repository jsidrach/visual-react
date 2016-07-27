package sneakycoders.visualreact.match;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;

import sneakycoders.visualreact.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout
        setContentView(R.layout.match);

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

    public void startMatch(@SuppressWarnings("UnusedParameters") View view) {
        // Levels sequence
        remainingLevels = LevelsFactory.getLevelsSequence(this);

        // Reset current level
        currentLevelId = "";
        currentLevel = null;

        // Reset players
        player1.reset();
        player2.reset();

        // Set initial state
        state = State.LevelSelection;
        displayState();
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

        // New group of conditions so that level selection ends in one of these
        if (state == State.LevelInfo) {
            // Set players states
            String levelName = LevelsFactory.getLevelName(currentLevelId, this);
            String levelDescription = LevelsFactory.getLevelDescription(currentLevelId, this);
            player1.setStateInfo(levelName, levelDescription);
            player2.setStateInfo(levelName, levelDescription);

            // Show/hide corresponding views
            levelContainer.setVisibility(View.GONE);
            levelInfo.setVisibility(View.VISIBLE);
        } else if (state == State.Level) {
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
        } else if (state == State.LevelResult) {
            if (currentLevel.result()) {
                lastTap.setStateSuccess();
            } else {
                lastTap.setStateFail();
            }
            // Switch to next state after a delay
            delayNextState();
        }
        // Final standings
        else {
            if (player1.getScore() != player2.getScore()) {
                // Choose winner/loser
                boolean player1Wins = player1.getScore() > player2.getScore();
                Player winner = player1Wins ? player1 : player2;
                Player loser = player1Wins ? player2 : player1;
                winner.setStateWinner();
                loser.setStateLoser();
            } else {
                // Tie
                player1.setStateTied();
                player2.setStateTied();
            }

            // Show/hide corresponding views
            levelContainer.setVisibility(View.GONE);
            finalStandings.setVisibility(View.VISIBLE);
        }
    }

    public void playerTap(View view) {
        if (state == State.LevelInfo) {
            // Switch to level
            state = State.Level;
            displayState();
        } else if (state == State.Level) {
            // Store the player who tapped, switch to level result
            lastTap = (view.getId() == R.id.area_player_1) ? player1 : player2;
            state = State.LevelResult;
            displayState();
        }
    }

    private void delayNextState() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(currentLevel)
                        .commit();
                currentLevel = null;
                state = (remainingLevels.size() > 0) ? State.LevelSelection : State.Standings;
                displayState();
            }
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
                    .setPositiveButton(R.string.cancel_match_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Match.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.cancel_match_no, null)
                    .show();
        } else {
            super.onBackPressed();
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
}
