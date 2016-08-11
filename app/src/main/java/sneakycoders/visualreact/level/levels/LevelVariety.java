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
    // Number of cells in the X axis
    private int cellsX;
    // Number of cells in the Y axis
    private int cellsY;
    // Cells
    private RectF[][] cellsPaints;
    // Colors of different cell
    private Paint[][] paints;
    //Colors
    private int backgroundColor;
    // Timer handler
    private Handler handler;
    // Update function (to update the cellsPaints)
    private Runnable updateCells;
    // View
    private LevelVarietyView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set background color
        backgroundColor = ContextCompat.getColor(getActivity(), R.color.neutral_dark);

        // Get number of cells in each axis
        cellsX = getResources().getInteger(R.integer.level_variety_cells_x);
        cellsY = getResources().getInteger(R.integer.level_variety_cells_y);

        // Create matrices
        cellsPaints = new RectF[cellsX][cellsY];
        paints = new Paint[cellsX][cellsY];

        // Initialize cell's colors
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                paints[i][j] = new Paint();
                paints[i][j].setColor(backgroundColor);
            }
        }

        // Set the handler
        handler = new Handler();

        // Set the update function
        final int delay = randomInt(R.integer.level_variety_min_delay, R.integer.level_variety_max_delay);
        updateCells = new Runnable() {
            @Override
            public void run() {
                // Cell to be updated
                int changeX = randomInInterval(0, cellsX - 1);
                int changeY = randomInInterval(0, cellsY - 1);

                // Update color
                paints[changeX][changeY].setColor(getRandomDistinctiveColor());

                // Redraw
                rootView.invalidate();

                // Update again after the delay
                handler.postDelayed(updateCells, delay);
            }
        };

        // Set timer to call the update function
        handler.postDelayed(updateCells, delay);

        // Create View
        rootView = new LevelVarietyView(getActivity());

        return rootView;
    }

    @Override
    public boolean onPlayerTap() {
        // Cancel timers
        handler.removeCallbacksAndMessages(null);

        // Store colors in a set to know the number of different colors
        Set<Integer> colorSet = new HashSet<>();
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                if (paints[i][j].getColor() != backgroundColor) {
                    colorSet.add(paints[i][j].getColor());
                }
            }
        }

        // Success if there are at least 5 different colors
        return (colorSet.size() >= 5);
    }

    private void initializeCells() {
        // Screen size
        int screenWidth = rootView.getWidth();
        int screenHeight = rootView.getHeight();

        // Cell size
        float cellWidth = screenWidth / (float) cellsX;
        float cellHeight = screenHeight / (float) cellsY;

        // Create cells
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                cellsPaints[i][j] = new RectF(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight);
            }
        }

        // Redraw
        rootView.invalidate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel timers
        handler.removeCallbacksAndMessages(null);
    }

    public class LevelVarietyView extends View {
        public LevelVarietyView(Context c) {
            super(c);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // Uninitialized
            if (cellsPaints[0][0] == null) {
                initializeCells();
            }
            // Playing
            else {
                // Draw the cells
                for (int i = 0; i < cellsX; i++) {
                    for (int j = 0; j < cellsY; j++) {
                        canvas.drawRect(cellsPaints[i][j], paints[i][j]);
                    }
                }
            }
        }
    }
}
