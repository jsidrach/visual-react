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
public class Player {
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

        // Set initial score
        score = 0;

        // Blink animation
        blink = new AlphaAnimation(R.fraction.blink_min_alpha, R.fraction.blink_max_alpha);
        blink.setDuration(R.integer.blink_duration);
        blink.setFillAfter(false);
        blink.setRepeatMode(Animation.REVERSE);
        blink.setRepeatCount(Animation.INFINITE);

        // Get colors
        colorNeutralPrimary = ContextCompat.getColor(activity, R.color.colorNeutralPrimary);
        colorNeutralSecondary = ContextCompat.getColor(activity, R.color.colorNeutralSecondary);
        colorFailPrimary = ContextCompat.getColor(activity, R.color.colorFailPrimary);
        colorFailLight = ContextCompat.getColor(activity, R.color.colorFailLight);
        colorSuccessPrimary = ContextCompat.getColor(activity, R.color.colorSuccessPrimary);
        colorSuccessLight = ContextCompat.getColor(activity, R.color.colorSuccessLight);
        colorTiePrimary = ContextCompat.getColor(activity, R.color.colorTiePrimary);
        colorTieLight = ContextCompat.getColor(activity, R.color.colorTieLight);
    }

    public int getScore() {
        // Get the score
        return score;
    }

    public void reset() {
        // Reset the score
        setScoreOffset(-score);
        // Cancel animation
        blink.cancel();
    }

    private void setScoreOffset(int offset) {
        // Set score by specifying the offset
        score += offset;
        scoreText.setText(NumberFormat.getIntegerInstance().format(score));
    }

    public void setStateSelection() {
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
        // Set colors and text
        area.setBackgroundColor(colorFailPrimary);
        scoreText.setTextColor(colorFailLight);
        standingText.setTextColor(colorFailPrimary);
        standingText.setText(R.string.standing_loser);
    }

    public void setStateWinner() {
        // Set colors and text
        area.setBackgroundColor(colorSuccessPrimary);
        scoreText.setTextColor(colorSuccessLight);
        standingText.setTextColor(colorSuccessPrimary);
        standingText.setText(R.string.standing_winner);
        area.startAnimation(blink);
    }

    public void setStateTied() {
        // Set colors and text
        area.setBackgroundColor(colorTiePrimary);
        scoreText.setTextColor(colorTieLight);
        standingText.setTextColor(colorTiePrimary);
        standingText.setText(R.string.standing_tied);
        area.startAnimation(blink);
    }
}
