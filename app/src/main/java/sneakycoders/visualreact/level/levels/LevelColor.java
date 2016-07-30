package sneakycoders.visualreact.level.levels;

import android.os.Bundle;
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
    // View
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Assign variables
        result = false;

        // Countdown in milliseconds before switching the color
        int minDelay = getResources().getInteger(R.integer.level_color_min_delay);
        int maxDelay = getResources().getInteger(R.integer.level_color_max_delay);
        int countdown = getRandomInt(minDelay, maxDelay);

        // Create view
        rootView = inflater.inflate(R.layout.level_color, container, false);

        // Color
        final int color = getRandomColor();

        // Set timer to change screen color
        rootView.postDelayed(new Runnable() {
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
        // Remove all callbacks
        rootView.removeCallbacks(null);

        // Return current result
        return result;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel countdown
        rootView.removeCallbacks(null);
    }
}
