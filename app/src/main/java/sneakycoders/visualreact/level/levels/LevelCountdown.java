package sneakycoders.visualreact.level.levels;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import sneakycoders.visualreact.R;
import sneakycoders.visualreact.level.Level;

// Dynamically instantiated
@SuppressWarnings("unused")
public class LevelCountdown extends Level {
    // Counter
    private int counter;
    // Countdown timer
    private CountDownTimer countDownTimer;
    // Flag to see if the result is success or not
    private boolean result;
    // Total time in milliseconds of the countdown
    private long totalTime;
    // Elapsed time since the countdown started
    private long elapsedTime;
    // Player countdowns
    private TextView player1Countdown;
    private TextView player2Countdown;
    // View
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set initial state
        result = false;

        // Create view
        rootView = inflater.inflate(R.layout.level_countdown, container, false);

        // Get countdowns views and set the color
        player1Countdown = (TextView) rootView.findViewById(R.id.level_countdown_player_1);
        player2Countdown = (TextView) rootView.findViewById(R.id.level_countdown_player_2);
        int color = getRandomColor();
        player1Countdown.setTextColor(color);
        player2Countdown.setTextColor(color);

        // Get parameters
        final int start = randomInt(R.integer.level_countdown_min_start, R.integer.level_countdown_max_start);
        final int step = randomInt(R.integer.level_countdown_min_delay, R.integer.level_countdown_max_delay);
        int minHide = getResources().getInteger(R.integer.level_countdown_min_hide);
        int maxHideOffset = getResources().getInteger(R.integer.level_countdown_max_hide_offset);
        final int hide = randomInInterval(minHide, start - maxHideOffset);

        // Set starting values
        counter = start;
        player1Countdown.setText(NumberFormat.getIntegerInstance().format(counter));
        player2Countdown.setText(NumberFormat.getIntegerInstance().format(counter));

        // Set countdown timer
        totalTime = start * step;
        countDownTimer = new CountDownTimer(totalTime, step) {
            @Override
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

            @Override
            public void onFinish() {
                result = true;
            }
        }.start();
        elapsedTime = SystemClock.elapsedRealtime();

        return rootView;
    }

    @Override
    public boolean onPlayerTap() {
        // Check initialization
        if (rootView == null) {
            return false;
        }

        // Calculate time offset
        float timeOffset = (totalTime - (SystemClock.elapsedRealtime() - elapsedTime)) / 1000.0f;

        // Cancel countdown
        countDownTimer.cancel();

        // Assign time offset
        NumberFormat f = new DecimalFormat("+0.00;-0.00");
        String timeOffsetStr = f.format(timeOffset);
        player1Countdown.setText(timeOffsetStr);
        player2Countdown.setText(timeOffsetStr);

        // Set color and size
        int color = result ? successColor : failColor;
        player1Countdown.setTextColor(color);
        player2Countdown.setTextColor(color);
        player1Countdown.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_MEDIUM));
        player2Countdown.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_MEDIUM));

        // Set visibility
        player1Countdown.setVisibility(View.VISIBLE);
        player2Countdown.setVisibility(View.VISIBLE);

        // Return current result
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel countdown
        countDownTimer.cancel();
    }
}
