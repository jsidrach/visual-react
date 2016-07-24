package sneakycoders.visualreact.level.levels;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sneakycoders.visualreact.R;
import sneakycoders.visualreact.level.Level;
import sneakycoders.visualreact.level.LevelsFactory;

public class LevelColor extends Level {
    // Handler for the countdown
    private Handler handler;
    // Flag to see if the result is success or not
    private boolean result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Assign variables
        handler = new Handler();
        result = false;

        // Countdown in milliseconds before switching the color
        int minDelay = getResources().getInteger(R.integer.level_color_min_delay);
        int maxDelay = getResources().getInteger(R.integer.level_color_max_delay);
        int countdown = getRandomInt(minDelay, maxDelay);

        // Create view
        final View rootView = inflater.inflate(R.layout.level_color, container, false);

        // Color
        final int color = LevelsFactory.getRandomColor(getActivity());

        // Set timer to change screen color
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rootView.setBackgroundColor(color);
                result = true;
            }
        }, countdown);

        return rootView;
    }

    @Override
    public boolean result() {
        // Remove all callbacks
        handler.removeCallbacksAndMessages(null);
        // Return current result
        return result;
    }
}
