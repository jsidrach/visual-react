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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sneakycoders.visualreact.R;
import sneakycoders.visualreact.level.Level;

@SuppressWarnings("unused")
public class LevelLined extends Level{
    // Cells in the X axis and Y axis
    private int nCells;
    // Cells
    private RectF[][] cells;
    private Paint[][] cellPaints;
    // Shape types in cells
    private shapeType[][] cellTypes;
    // Rectangles to draw cross
    private RectF[][][] crossRect;
    // Cells that's been filled
    private List<Integer> filled;
    //Cells that's not been filled
    private List<Integer> notFilled;
    // Colors
    private int backgroundColor;
    private Paint successPaint;
    private Paint circlePaint;
    private Paint crossPaint;
    // Separator parameter
    private RectF[] lines;
    private Paint linePaint;
    // Handler
    private Handler handler;
    // Update function
    private Runnable updateCells;
    // View
    private LevelLinedView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Initialize
        handler = new Handler();

        // Get parameters
        nCells = getResources().getInteger(R.integer.level_lined_cells);

        // Colors
        backgroundColor = ContextCompat.getColor(getActivity(), R.color.neutral_dark);
        Integer[] colors = getRandomDistinctiveColors(2);

        // Paints
        circlePaint = new Paint();
        circlePaint.setColor(colors[0]);
        circlePaint.setStyle(Paint.Style.STROKE);
        crossPaint = new Paint();
        crossPaint.setColor(colors[1]);
        crossPaint.setStyle(Paint.Style.STROKE);
        successPaint = new Paint();
        successPaint.setColor(ContextCompat.getColor(getActivity(), R.color.success_primary));
        successPaint.setStyle(Paint.Style.STROKE);
        linePaint = new Paint();
        linePaint.setColor(ContextCompat.getColor(getActivity(), R.color.neutral_light));

        // Initialize cells
        cells = new RectF[nCells][nCells];
        cellPaints = new Paint[nCells][nCells];
        crossRect = new RectF[nCells][nCells][2];
        cellTypes = new shapeType[nCells][nCells];

        // Initialize Lists to store fill information
        filled = new ArrayList<>();
        notFilled = new ArrayList<>();
        for (int i = 0; i < nCells * nCells; i++) {
            notFilled.add(i);
        }

        // Initialize separators
        lines = new RectF[2 * (nCells - 1)];

        rootView = new LevelLinedView(getActivity());
        return rootView;
    }

    @Override
    public boolean onPlayerTap() {
        // Stop animation
        handler.removeCallbacksAndMessages(null);

        // Determine whether there are three same colors in a line

        // Number of qualified lines
        int nLines = 0;

        // Number of filled cells
        int nFilledCells;

        // Check horizontally from the first line to the last line
        for (int i = 0; i < nCells; i++) {
            // Create a set to store the types in the row
            Set<shapeType> types = new HashSet<>();
            nFilledCells = 0;

            // Get the number of filled cells and types of cells in the row
            for (int j = 0; (j < nCells) && (filled.contains(nCells * i + j)); j++) {
                nFilledCells++;
                types.add(cellTypes[i][j]);
            }

            // Check if all types are the same when the row is filled
            if ((nFilledCells == nCells) && (types.size() == 1)) {
                nLines++;
                // Set the paint color to success paint
                for (int j = 0; j < nCells; j++) {
                    cellPaints[i][j] = successPaint;
                }
            }
        }

        // Check vertically from first column to last column
        for (int i = 0; i < nCells; i++) {
            Set<shapeType> types = new HashSet<>();
            nFilledCells = 0;

            for (int j = 0; (j < nCells) && (filled.contains(nCells * j + i)); j++) {
                nFilledCells++;
                types.add(cellTypes[j][i]);
            }

            if ((nFilledCells == nCells) && (types.size() == 1)) {
                nLines++;
                for (int j = 0; j < nCells; j++) {
                    cellPaints[j][i] = successPaint;
                }
            }
        }

        // Check diagonal from left to right
        Set<shapeType> typesLtoR = new HashSet<>();
        nFilledCells = 0;

        for (int i = 0; (i < nCells) && (filled.contains(nCells * i + i)); i++) {
            nFilledCells++;
            typesLtoR.add(cellTypes[i][i]);
        }

        if ((nFilledCells == nCells) && (typesLtoR.size() == 1)) {
            nLines++;
            for (int i = 0; i < nCells; i++) {
                cellPaints[i][i] = successPaint;
            }
        }

        // Check diagonal from right to left
        Set<shapeType> typesRtoL = new HashSet<>();
        nFilledCells = 0;

        for (int i = 0; (i < nCells) && (filled.contains(i * nCells + nCells - 1 - i)); i++) {
            nFilledCells++;
            typesRtoL.add(cellTypes[i][nCells - 1 - i]);
        }

        if ((nFilledCells == nCells) && (typesRtoL.size() == 1)) {
            nLines++;
            for (int i = 0; i < nCells; i++) {
                cellPaints[i][nCells - 1 - i] = successPaint;
            }
        }

        // Check if there are connected lines of same shape types
        boolean result = (nLines > 0);

        // Redraw if there are such lines
        if (result) {
            rootView.invalidate();
        }

        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel timers
        handler.removeCallbacksAndMessages(null);
    }

    private enum shapeType {
        Circle, Cross
    }

    private void initializeCells() {
        // Screen size
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();

        // Size of the grid
        float squareWidth = Math.min(width, height);
        float resizedSquareWidth = squareWidth * (1.0f - getResources().getFraction(R.fraction.level_lined_grid_width, 1, 1));
        float gridSize = resizedSquareWidth / nCells;

        // Width of separator
        float halfSeparatorWidth = gridSize * getResources().getFraction(R.fraction.level_lined_separator_width, 1, 1);

        // Set stroke width of the circles
        float strokeMargin = gridSize * getResources().getFraction(R.fraction.level_lined_stroke_width, 1, 1);
        circlePaint.setStrokeWidth(strokeMargin);
        crossPaint.setStrokeWidth(strokeMargin);
        successPaint.setStrokeWidth(2.0f * strokeMargin);

        // Bounds of the grid
        float startLeft = (width - resizedSquareWidth) / 2.0f;
        float startTop = (height - resizedSquareWidth) / 2.0f;

        // Initialize separators
        float left = startLeft + gridSize - halfSeparatorWidth;
        float top = startTop + gridSize - halfSeparatorWidth;

        // Set vertical separators
        for (int i = 0; i < nCells - 1; i++) {
            float lineLeft = left + i * gridSize;
            float lineRight = lineLeft + 2 * halfSeparatorWidth;
            float lineBottom = startTop + nCells * gridSize;
            lines[i] = new RectF(lineLeft, startTop, lineRight, lineBottom);
        }

        // Set horizontal separators
        for (int i = nCells - 1; i < lines.length; i++) {
            float lineTop = top + (i - nCells + 1) * gridSize;
            float lineRight = startLeft + nCells * gridSize;
            float lineBottom = lineTop + 2 * halfSeparatorWidth;
            lines[i] = new RectF(startLeft, lineTop, lineRight, lineBottom);
        }

        // Margin of the cell to the grid
        float cellMargin = gridSize * getResources().getFraction(R.fraction.level_lined_cell_width, 1, 1);

        // Size of actual cells
        float cellSize = gridSize - 2.0f * cellMargin;

        // Length and width of rectangles of cross
        float crossLength = (float) Math.sqrt(2.0) * cellSize * getResources().getFraction(R.fraction.level_lined_cross_length, 1, 1);
        float crossWidth = crossLength * getResources().getFraction(R.fraction.level_lined_rect_width, 1, 1);

        // Distances of the edges of rectangle to edges of cell
        float distHorizontal = (crossLength - cellSize) / 2.0f;
        float distVertical = (cellSize - crossWidth) / 2.0f;

        // Initialize cells and crosses
        for (int i = 0; i < nCells; i++) {
            for (int j = 0; j < nCells; j++) {
                // Cells
                float cellLeft = startLeft + cellMargin + i * gridSize;
                float cellTop = startTop + cellMargin + j * gridSize;
                float cellRight = startLeft + (i + 1) * gridSize - cellMargin;
                float cellBottom = startTop + (j + 1) * gridSize - cellMargin;
                cells[i][j] = new RectF(cellLeft, cellTop, cellRight, cellBottom);

                // Crosses
                crossRect[i][j][0] = new RectF(cellLeft - distHorizontal, cellTop + distVertical, cellRight + distHorizontal, cellBottom - distVertical);
                crossRect[i][j][1] = new RectF(cellLeft + distVertical, cellTop - distHorizontal, cellRight - distVertical, cellBottom + distHorizontal);
            }
        }

        // Update cells
        final int delay = randomInt(R.integer.level_lined_min_delay, R.integer.level_lined_max_delay);
        updateCells = new Runnable() {
            @Override
            public void run() {
                // All cells are filled
                if (notFilled.size() == 0) {
                    // Swap filled and notFilled
                    notFilled = filled;
                    filled = new ArrayList<>();
                }
                // More cells to be filled
                else {
                    // Cell to be filled
                    int cellInd = randomInInterval(0, notFilled.size() - 1);
                    int cellPos = notFilled.get(cellInd);

                    // Which type to fill into the cell
                    boolean isCircle = randomBoolean();

                    // Update the grid
                    filled.add(cellPos);
                    notFilled.remove(cellInd);
                    int row = cellPos / nCells;
                    int col = cellPos % nCells;
                    cellPaints[row][col] = isCircle ? circlePaint : crossPaint;
                    cellTypes[row][col] = isCircle ? shapeType.Circle : shapeType.Cross;
                }

                // Update view
                rootView.invalidate();

                // Update again after the delay
                handler.postDelayed(updateCells, delay);
            }
        };


        // Set timer to call the update function
        handler.postDelayed(updateCells, delay);

        // Force redraw
        rootView.invalidate();
    }



    public class LevelLinedView extends View {
        public LevelLinedView(Context c) {
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
                // Set background color
                canvas.drawColor(backgroundColor);

                // Draw separators
                for (RectF rect : lines) {
                    canvas.drawRect(rect, linePaint);
                }

                // Draw shapes
                for (int index : filled) {
                    int row = index / nCells;
                    int col = index % nCells;

                    // Draw circle
                    if (cellTypes[row][col] == shapeType.Circle) {
                        canvas.drawOval(cells[row][col], cellPaints[row][col]);
                    }
                    // Draw cross
                    else {
                        // Get the two rectangles to draw
                        RectF rectHorizontal = crossRect[row][col][0];
                        RectF rectVertical = crossRect[row][col][1];

                        // Rotate the canvas around the center of rectangles
                        canvas.save();
                        canvas.rotate(45, rectHorizontal.centerX(), rectHorizontal.centerY());

                        // Draw cross
                        canvas.drawRect(rectHorizontal, cellPaints[row][col]);
                        canvas.drawRect(rectVertical, cellPaints[row][col]);

                        // Restore canvas
                        canvas.restore();
                    }

                }
            }
        }
    }
}
