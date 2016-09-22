package sneakycoders.visualreact.match;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import java.text.NumberFormat;

import sneakycoders.visualreact.R;

// Player information and data bindings
class Player {
    // Player area
    final private View area;
    // Score text
    final private TextView scoreText;
    // Tap to continue text
    final private TextView tapText;
    // Level name text
    final private TextView levelNameText;
    // Level description text
    final private TextView levelDescriptionText;
    // Final standing (winner/loser) text
    final private TextView standingText;
    // Blink animation
    final private Animation blink;
    // Colors
    final private int colorNeutralPrimary;
    final private int colorNeutralSecondary;
    final private int colorFailPrimary;
    final private int colorFailLight;
    final private int colorSuccessPrimary;
    final private int colorSuccessLight;
    final private int colorTiePrimary;
    final private int colorTieLight;
    // Player is ready
    private boolean ready;
    // Score
    private int score;

    public Player(Activity activity, int area, int scoreText, int tapText, int levelNameText, int levelDescriptionText, int standingText) {
        // Views
        this.area = activity.findViewById(area);
        this.scoreText = (TextView) activity.findViewById(scoreText);
        this.tapText = (TextView) activity.findViewById(tapText);
        this.levelNameText = (TextView) activity.findViewById(levelNameText);
        this.levelDescriptionText = (TextView) activity.findViewById(levelDescriptionText);
        this.standingText = (TextView) activity.findViewById(standingText);

        // Set initial state
        score = 0;
        setReady(false);

        // Blink animation
        blink = new AlphaAnimation(
                activity.getResources().getFraction(R.fraction.match_blink_min_alpha, 1, 1),
                activity.getResources().getFraction(R.fraction.match_blink_max_alpha, 1, 1));
        blink.setDuration(activity.getResources().getInteger(R.integer.match_blink_duration));
        blink.setFillAfter(false);
        blink.setRepeatMode(Animation.REVERSE);
        blink.setRepeatCount(Animation.INFINITE);

        // Get colors
        colorNeutralPrimary = ContextCompat.getColor(activity, R.color.neutral_primary);
        colorNeutralSecondary = ContextCompat.getColor(activity, R.color.neutral_light);
        colorFailPrimary = ContextCompat.getColor(activity, R.color.fail_primary);
        colorFailLight = ContextCompat.getColor(activity, R.color.fail_light);
        colorSuccessPrimary = ContextCompat.getColor(activity, R.color.success_primary);
        colorSuccessLight = ContextCompat.getColor(activity, R.color.success_light);
        colorTiePrimary = ContextCompat.getColor(activity, R.color.tie_primary);
        colorTieLight = ContextCompat.getColor(activity, R.color.tie_light);
    }

    public int getScore() {
        // Get the score
        return score;
    }

    public void reset() {
        // Reset the state
        setScoreOffset(-score);
        setReady(false);

        // Cancel animation
        blink.cancel();
    }

    private void setScoreOffset(int offset) {
        // Set score by specifying the offset
        score += offset;
        scoreText.setText(NumberFormat.getIntegerInstance().format(score));
    }

    public void setReady(boolean ready) {
        this.ready = ready;
        tapText.setText(ready ? R.string.player_ready : R.string.player_not_ready);
    }

    public boolean isReady() {
        return ready;
    }

    public void setStateSelection() {
        // Set player as not ready
        setReady(false);

        // Set colors
        area.setBackgroundColor(colorNeutralPrimary);
        scoreText.setTextColor(colorNeutralSecondary);
    }

    public void setStateInfo(String levelName, String levelDescription) {
        // Set the level name/description
        levelNameText.setText(levelName);
        levelDescriptionText.setText(levelDescription);

        // Show 'tap to start level' text
        tapText.setVisibility(View.VISIBLE);
    }

    public void setStatePlaying() {
        // Hide 'tap to start level' text
        tapText.setVisibility(View.GONE);
    }

    public void setStateFail() {
        // Decrease score
        setScoreOffset(-1);

        // Set colors
        area.setBackgroundColor(colorFailPrimary);
        scoreText.setTextColor(colorFailLight);
    }

    public void setStateSuccess() {
        // Increase score
        setScoreOffset(1);

        // Set colors
        area.setBackgroundColor(colorSuccessPrimary);
        scoreText.setTextColor(colorSuccessLight);
    }

    public void setStateLoser() {
        // Set colors and text (loser of the match)
        area.setBackgroundColor(colorFailPrimary);
        scoreText.setTextColor(colorFailLight);
        standingText.setTextColor(colorFailPrimary);
        standingText.setText(R.string.standing_loser);
    }

    public void setStateWinner() {
        // Set colors and text (winner of the match)
        area.setBackgroundColor(colorSuccessPrimary);
        scoreText.setTextColor(colorSuccessLight);
        standingText.setTextColor(colorSuccessPrimary);
        standingText.setText(R.string.standing_winner);
        area.startAnimation(blink);
        standingText.startAnimation(blink);
    }

    public void setStateTied() {
        // Set colors and text (match resulted in a tie)
        area.setBackgroundColor(colorTiePrimary);
        scoreText.setTextColor(colorTieLight);
        standingText.setTextColor(colorTiePrimary);
        standingText.setText(R.string.standing_tied);
        area.startAnimation(blink);
        standingText.startAnimation(blink);
    }
}
