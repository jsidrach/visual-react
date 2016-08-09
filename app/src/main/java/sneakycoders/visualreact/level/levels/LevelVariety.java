package sneakycoders.visualreact.level.levels;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.Set;

import sneakycoders.visualreact.R;
import sneakycoders.visualreact.level.Level;

@SuppressWarnings("unused")
public class LevelVariety extends Level {
    // Number of Cells in the X axis
    private int cellsX;
    // Number of Cells in the Y axis
    private int cellsY;
    // Cells
    private RectF[][] cells;
    // Colors of different cell
    private Paint[][] paints;
    //Colors
    private int backgroundColor;
    // Timer handler
    private Handler handler;
    // Update function (to update the cells)
    private Runnable updateCells;
    // View
    private LevelVarietyView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set colors
        backgroundColor = ContextCompat.getColor(getActivity(), R.color.neutral_dark);

        // Get parameters
        cellsX = getResources().getInteger(R.integer.level_variety_cells_x);
        cellsY = getResources().getInteger(R.integer.level_variety_cells_y);

        // Initialize cells
        cells = new RectF[cellsX][cellsY];

        // Initialize cell's colors
        paints = new Paint[cellsX][cellsY];
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                paints[i][j] = new Paint();
                paints[i][j].setColor(backgroundColor);
            }
        }

        // Set the handler
        handler = new Handler();

        final int delay = randomInt(R.integer.level_variety_min_delay, R.integer.level_variety_max_delay);
        // Set the update function
        updateCells = new Runnable() {
            @Override
            public void run() {
                // Cell to be changed
                int changeX = randomInInterval(0, cellsX - 1);
                int changeY = randomInInterval(0, cellsY - 1);
                // Original color
                Paint originalColor = paints[changeX][changeY];
                // Color to be filled
                int changeColor = getRandomDistinctiveColor();
                // Update color
                paints[changeX][changeY].setColor(changeColor);

                // Redraw
                rootView.invalidate();

                // Update again after the delay
                handler.postDelayed(updateCells, delay);
            }
        };


        // Set Timer to call the update function
        handler.postDelayed(updateCells, delay);

        // Create View
        rootView = new LevelVarietyView(getActivity());

        return rootView;
    }

    @Override
    public boolean onPlayerTap() {
        // Cancel timers
        handler.removeCallbacksAndMessages(null);

        // Create a set to store colors
        Set<Integer> colorSet = new HashSet<>();
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                if (paints[i][j].getColor() != backgroundColor) {
                    colorSet.add(paints[i][j].getColor());
                }
            }
        }

        // Return result
        return (colorSet.size() >= 5);
    }

    private void initializeCells() {
        // Screen Size
        float screenWidth = rootView.getWidth();
        float screenHeight = rootView.getHeight();

        // Cell size
        float cellWidth = screenWidth / cellsX;
        float cellHeight = screenHeight / cellsY;

        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                cells[i][j] = new RectF(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight);
            }
        }

        // Redraw
        rootView.invalidate();
    }

    public class LevelVarietyView extends View {
        public LevelVarietyView(Context c) {
            super(c);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // Uninitialized
            if (cells[0][0] == null) {
                initializeCells();
            }
            // Playing
            else {
                // Fill the cells with lighter color first
                for (int i = 0; i < cellsX; i++) {
                    for (int j = 0; j < cellsY; j++) {
                        canvas.drawRect(cells[i][j], paints[i][j]);
                    }
                }
            }
        }
    }
}
