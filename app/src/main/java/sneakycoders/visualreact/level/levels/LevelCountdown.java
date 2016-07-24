package sneakycoders.visualreact.level.levels;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.annotation.Dimension;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import sneakycoders.visualreact.R;
import sneakycoders.visualreact.level.Level;
import sneakycoders.visualreact.level.LevelsFactory;

public class LevelCountdown extends Level {
    // Counter
    private int counter;
    // Countdown timer
    private CountDownTimer countDownTimer;
    // Flag to see if the result is success or not
    private boolean result;
    // Total time in milliseconds
    private long totalTime;
    // Elapsed time
    private long elapsedTime;
    // Player countdowns
    private TextView player1Countdown;
    private TextView player2Countdown;
    // Colors
    private int successColor;
    private int failColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Assign variables
        result = false;

        // Create view
        final View rootView = inflater.inflate(R.layout.level_countdown, container, false);

        // Get countdowns views and set the color
        player1Countdown = (TextView) rootView.findViewById(R.id.levelCountdownPlayer1);
        player2Countdown = (TextView) rootView.findViewById(R.id.levelCountdownPlayer2);
        int color = LevelsFactory.getRandomColor(getActivity());
        player1Countdown.setTextColor(color);
        player2Countdown.setTextColor(color);

        // Set colors
        successColor = ContextCompat.getColor(getActivity(), R.color.colorSuccessPrimary);
        failColor = ContextCompat.getColor(getActivity(), R.color.colorFailPrimary);

        // Get parameters
        int minStart = getResources().getInteger(R.integer.level_countdown_min_start);
        int maxStart = getResources().getInteger(R.integer.level_countdown_max_start);
        final int start = getRandomInt(minStart, maxStart);
        int minStep = getResources().getInteger(R.integer.level_countdown_min_step);
        int maxStep = getResources().getInteger(R.integer.level_countdown_max_step);
        final int step = getRandomInt(minStep, maxStep);
        int minHide = getResources().getInteger(R.integer.level_countdown_min_hide);
        int maxHideOffset = getResources().getInteger(R.integer.level_countdown_max_hide_offset);
        final int hide = getRandomInt(minHide, start - maxHideOffset);

        // Set starting values
        counter = start;
        player1Countdown.setText(NumberFormat.getIntegerInstance().format(counter));
        player2Countdown.setText(NumberFormat.getIntegerInstance().format(counter));

        // Set countdown timer
        totalTime = start * step;
        countDownTimer = new CountDownTimer(totalTime, step) {
            public void onTick(long msUntilFinished) {
                if (counter >= hide) {
                    // Visible countdown
                    player1Countdown.setText(NumberFormat.getIntegerInstance().format(counter));
                    player2Countdown.setText(NumberFormat.getIntegerInstance().format(counter));
                } else if (counter == (hide - 1)) {
                    // Hide countdown
                    player1Countdown.setVisibility(View.INVISIBLE);
                    player2Countdown.setVisibility(View.INVISIBLE);
                }
                counter--;
            }

            public void onFinish() {
                result = true;
            }
        }.start();
        elapsedTime = SystemClock.elapsedRealtime();

        return rootView;
    }

    @Override
    public boolean result() {
        // Calculate time offset
        double timeOffset = (totalTime - (SystemClock.elapsedRealtime() - elapsedTime)) / 1000.0f;

        // Cancel countdown
        countDownTimer.cancel();

        // Assign time offset
        NumberFormat f = new DecimalFormat("+#;-#");
        f.setMinimumFractionDigits(2);
        f.setMaximumFractionDigits(2);
        String timeOffsetStr = f.format(timeOffset);
        player1Countdown.setText(timeOffsetStr);
        player2Countdown.setText(timeOffsetStr);

        // Set color and size
        int color = result ? successColor : failColor;
        player1Countdown.setTextColor(color);
        player2Countdown.setTextColor(color);
        player1Countdown.setTextSize(Dimension.SP, 60);
        player2Countdown.setTextSize(Dimension.SP, 60);

        // Set visibility
        player1Countdown.setVisibility(View.VISIBLE);
        player2Countdown.setVisibility(View.VISIBLE);

        // Return current result
        return result;
    }
}
