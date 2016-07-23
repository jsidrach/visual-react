package sneakycoders.visualreact.screens;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import sneakycoders.visualreact.R;
import sneakycoders.visualreact.utils.LevelsFactory;

public class Match extends AppCompatActivity {

    // Current state of the match
    private MatchState matchState;
    // Player 1 information
    private Player player1;
    // Player 2 information
    private Player player2;
    // Last player tap information
    private Player lastTap;
    // Remaining levels
    private List<String> remainingLevels;
    // Current level
    private String currentLevelId;
    // Show tips
    private boolean showTips;
    //
    // Views
    //
    // TODO: Change to specific class
    // Information about the level
    private View infoLevel;
    // Current level
    private View currentLevel;
    // Final standings
    private View finalStandings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate layout
        setContentView(R.layout.match);

        // Set view bindings
        infoLevel = findViewById(R.id.infoLevel);
        currentLevel = findViewById(R.id.currentLevel);
        finalStandings = findViewById(R.id.finalStandings);

        // Set players information bindings
        player1 = new Player(R.id.player1Score, R.id.player1TapText, R.id.player1LevelName, R.id.player1LevelDescription, R.id.player1Standing);
        player2 = new Player(R.id.player2Score, R.id.player2TapText, R.id.player2LevelName, R.id.player2LevelDescription, R.id.player2Standing);

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

        // Reset players scores
        player1.resetScore();
        player2.resetScore();

        // Set next state
        matchState = MatchState.SelectLevel;
        displayState();
    }

    private void displayState() {
        // Transition state, no UI changes needed
        if (matchState == MatchState.SelectLevel) {
            String nextLevelId = remainingLevels.remove(0);
            matchState = (showTips && !nextLevelId.equals(currentLevelId)) ? MatchState.InfoLevel : MatchState.PlayingLevel;
            currentLevelId = nextLevelId;
        }

        // New group of conditions so that select level proceeds
        if (matchState == MatchState.InfoLevel) {
            currentLevel.setVisibility(View.GONE);
            infoLevel.setVisibility(View.VISIBLE);
        } else if (matchState == MatchState.PlayingLevel) {
            infoLevel.setVisibility(View.GONE);
            currentLevel.setVisibility(View.VISIBLE);
            // TODO: Replace level
        } else if (matchState == MatchState.ResultLevel) {
            // bool result = currentLevel.solve();
            // player.setScoreOffset(result ? 1 : -1);
            // TODO: Set timer
            matchState = (remainingLevels.size() > 0) ? MatchState.SelectLevel : MatchState.FinalStandings;
            displayState();
        }
        // Final standings
        else {
            // TODO: Set strings, colors
            if (player1.getScore() != player2.getScore()) {
                boolean player1Wins = player1.getScore() > player2.getScore();
                Player winner = player1Wins ? player1 : player2;
                Player loser = player1Wins ? player2 : player1;
                winner.standingText.setText(R.string.standing_winner);
                loser.standingText.setText(R.string.standing_loser);
            } else {
            }
            currentLevel.setVisibility(View.GONE);
            finalStandings.setVisibility(View.VISIBLE);
        }

    }

    public void playerTap(View view) {
        if (matchState == MatchState.InfoLevel) {
            // Switch to playing level
            matchState = MatchState.PlayingLevel;
            displayState();
        } else if (matchState == MatchState.PlayingLevel) {
            // Store the player who tapped, switch to result
            lastTap = (view.getId() == R.id.player1Area) ? player1 : player2;
            matchState = MatchState.ResultLevel;
            displayState();
        }
    }

    public void switchToMainMenu(@SuppressWarnings("UnusedParameters") View view) {
        // Switch to the Main Menu screen
        Intent intent = new Intent(Match.this, MainMenu.class);
        startActivity(intent);
    }

    // States of the screen
    private enum MatchState {
        SelectLevel, InfoLevel, PlayingLevel, ResultLevel, FinalStandings
    }

    // Player information and data bindings
    private class Player {
        //
        // Views
        //
        // Score text
        final public TextView scoreText;
        // Tap to continue text
        final public TextView tapText;
        // Level name text
        final public TextView levelNameText;
        // Level description text
        final public TextView levelDescriptionText;
        // Final standing (winner/loser) text
        final public TextView standingText;
        // Score
        private int score;

        public Player(int scoreTextId, int tapTextId, int levelNameTextId, int levelDescriptionTextId, int standingTextId) {
            score = 0;
            scoreText = (TextView) findViewById(scoreTextId);
            tapText = (TextView) findViewById(tapTextId);
            levelNameText = (TextView) findViewById(levelNameTextId);
            levelDescriptionText = (TextView) findViewById(levelDescriptionTextId);
            standingText = (TextView) findViewById(standingTextId);
        }

        // Get the score
        public int getScore() {
            return score;
        }

        // Reset the score
        public void resetScore() {
            setScoreOffset(-score);
        }

        // Set score by specifying the offset
        public void setScoreOffset(int offset) {
            score += offset;
            scoreText.setText(NumberFormat.getIntegerInstance().format(score));
        }
    }
}
