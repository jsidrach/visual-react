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
import sneakycoders.visualreact.levels.LevelsFactory;

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
    // Current level
    // TODO: Change to base abstract class, with solve() method
    private View currentLevel;
    // Final standings
    private View finalStandings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout
        setContentView(R.layout.match);

        // Set view bindings
        levelInfo = findViewById(R.id.infoLevel);
        currentLevel = findViewById(R.id.currentLevel);
        finalStandings = findViewById(R.id.finalStandings);

        // Set players information bindings
        player1 = new Player(this, R.id.player1Area, R.id.player1Score, R.id.player1TapText, R.id.player1LevelName, R.id.player1LevelDescription, R.id.player1Standing);
        player2 = new Player(this, R.id.player2Area, R.id.player2Score, R.id.player2TapText, R.id.player2LevelName, R.id.player2LevelDescription, R.id.player2Standing);

        // Read show tips flag
        showTips = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("show_level_tips", false);

        // Start match
        startMatch(null);
    }

    public void startMatch(@SuppressWarnings("UnusedParameters") View view) {
        // Levels sequence
        remainingLevels = LevelsFactory.getLevelsSequence(this);

        // Reset current level
        currentLevelId = "";

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
            currentLevel.setVisibility(View.GONE);
            levelInfo.setVisibility(View.VISIBLE);
        } else if (state == State.Level) {
            // TODO: Replace level fragment
            // Set players states
            player1.setStatePlaying();
            player2.setStatePlaying();

            // Show/hide corresponding views
            levelInfo.setVisibility(View.GONE);
            currentLevel.setVisibility(View.VISIBLE);
        } else if (state == State.LevelResult) {
            // boolean result = currentLevel.solve();
            boolean result = true;
            if (result) {
                lastTap.setStateSuccess();
            } else {
                lastTap.setStateFail();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    state = (remainingLevels.size() > 0) ? State.LevelSelection : State.Standings;
                    displayState();
                }
            }, R.integer.pause_between_levels);
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
            currentLevel.setVisibility(View.GONE);
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
            lastTap = (view.getId() == R.id.player1Area) ? player1 : player2;
            state = State.LevelResult;
            displayState();
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

    // States of the match
    private enum State {
        LevelSelection, LevelInfo, Level, LevelResult, Standings
    }
}
