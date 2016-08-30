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

import sneakycoders.visualreact.R;
import sneakycoders.visualreact.level.Level;

// Dynamically instantiated
@SuppressWarnings("unused")
public class LevelLight extends Level {
    // Cells in the X axis
    private int cellsX;
    // Cells in the Y axis
    private int cellsY;
    // Light cells counter
    private int totalLightCells;
    // Cells
    private RectF[][] cells;
    // Light Cells
    private boolean[][] lightCells;
    // Middle line
    private RectF middleLine;
    // Colors
    private Paint cellPaint;
    private Paint successPaint;
    private Paint failPaint;
    private int backgroundColor;
    // Flag to see the state of the level
    private State state;
    // Probability of changing from dark to light on each update
    private float darkToLight;
    // Probability of changing from light to dark on each update
    private float lightToDark;
    // Timer handler
    private Handler handler;
    // Update function (to update the cells)
    private Runnable updateCells;
    // View
    private LevelLightView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set initial state
        state = State.Uninitialized;
        totalLightCells = 0;

        // Set colors
        cellPaint = new Paint();
        cellPaint.setColor(getRandomColor());
        successPaint = new Paint();
        successPaint.setColor(successLightColor);
        failPaint = new Paint();
        failPaint.setColor(failLightColor);
        backgroundColor = ContextCompat.getColor(getActivity(), R.color.neutral_dark);

        // Number of cells in each axis
        cellsX = getResources().getInteger(R.integer.level_light_cells_x);
        cellsY = getResources().getInteger(R.integer.level_light_cells_y);

        // Transition probabilities
        darkToLight = randomFloat(R.fraction.level_light_min_dark_to_light, R.fraction.level_light_max_dark_to_light);
        lightToDark = randomFloat(R.fraction.level_light_min_light_to_dark, R.fraction.level_light_max_light_to_dark);

        // Create matrices
        cells = new RectF[cellsX][cellsY];
        lightCells = new boolean[cellsX][cellsY];

        // Set the handler
        handler = new Handler();

        // Create view
        rootView = new LevelLightView(getActivity());

        return rootView;
    }

    private boolean moreCellsLightThanDark() {
        return (totalLightCells > ((cellsX * cellsY) / 2));
    }

    @Override
    public boolean onPlayerTap() {
        // Cancel timers
        handler.removeCallbacksAndMessages(null);

        // Set state
        state = State.Result;

        // Redraw
        rootView.invalidate();

        // Success if more than half of the cells are light
        return moreCellsLightThanDark();
    }

    private void initializeCells() {
        // Screen and cell sizes
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();
        float cellWidth = width / (float) cellsX;
        float cellHeight = height / (float) cellsY;

        // Create rectangles
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                // Create rectangle
                cells[i][j] = new RectF(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight);
            }
        }

        // Create middle line
        RectF middleCell = cells[(cellsX / 2) - 1][0];
        float lineSemiWidth = 0.5f * width * getResources().getFraction(R.fraction.level_light_middle_line_width, 1, 1);
        middleLine = new RectF(middleCell.right - lineSemiWidth, 0, middleCell.right + lineSemiWidth, height);

        // Set the update function
        final int delay = randomInt(R.integer.level_light_min_delay, R.integer.level_light_max_delay);
        updateCells = () -> {
            int i = randomInInterval(0, cellsX - 1);
            int j = randomInInterval(0, cellsY - 1);
            double r = Math.random();
            // Light cell
            if (lightCells[i][j]) {
                if (r <= lightToDark) {
                    lightCells[i][j] = false;
                    totalLightCells--;

                    // Redraw
                    rootView.invalidate();
                }
            }
            // Dark cell
            else {
                if (r <= darkToLight) {
                    lightCells[i][j] = true;
                    totalLightCells++;

                    // Redraw
                    rootView.invalidate();
                }
            }

            // Update again after the delay
            handler.postDelayed(updateCells, delay);
        };

        // Set timer to call the update function
        handler.postDelayed(updateCells, delay);

        // Set the state
        state = State.Playing;

        // Redraw
        rootView.invalidate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel timers
        handler.removeCallbacksAndMessages(null);
    }

    // States of the level
    private enum State {
        Uninitialized, Playing, Result
    }

    public class LevelLightView extends View {
        public LevelLightView(Context c) {
            super(c);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // Uninitialized
            if (state == State.Uninitialized) {
                initializeCells();
            }
            // Playing
            else if (state == State.Playing) {
                // Set background color
                canvas.drawColor(backgroundColor);

                // Draw light cells
                for (int i = 0; i < cellsX; i++) {
                    for (int j = 0; j < cellsY; j++) {
                        if (lightCells[i][j]) {
                            canvas.drawRect(cells[i][j], cellPaint);
                        }
                    }
                }
            }
            // Result
            else {
                // Set background color
                canvas.drawColor(backgroundColor);

                // Group cells by color
                int counter = totalLightCells;
                for (int i = 0; (i < cellsX) && (counter > 0); i++) {
                    for (int j = 0; (j < cellsY) && (counter > 0); j++) {
                        canvas.drawRect(cells[i][j], cellPaint);
                        counter--;
                    }
                }

                // Draw middle line
                Paint paint = moreCellsLightThanDark() ? successPaint : failPaint;
                canvas.drawRect(middleLine, paint);
            }
        }
    }
}
