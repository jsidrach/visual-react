package sneakycoders.visualreact.level.levels;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sneakycoders.visualreact.R;
import sneakycoders.visualreact.level.Level;

// Dynamically instantiated
@SuppressWarnings("unused")
public class LevelColor extends Level {
    // Flag to see if the result is success or not
    private boolean result;
    // Timer handler
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set initial state
        result = false;

        // Set handler
        handler = new Handler();

        // Countdown color
        final int color = getRandomColor();

        // Create view
        final View rootView = inflater.inflate(R.layout.level_color, container, false);

        // Countdown in milliseconds before switching the color
        int countdown = randomInt(R.integer.level_color_min_delay, R.integer.level_color_max_delay);

        // Set timer to change screen color
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                result = true;
                rootView.setBackgroundColor(color);
            }
        }, countdown);

        return rootView;
    }

    @Override
    public boolean onPlayerTap() {
        // Cancel callback
        handler.removeCallbacksAndMessages(null);

        // Return current result
        return result;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel callback
        handler.removeCallbacksAndMessages(null);
    }
}
