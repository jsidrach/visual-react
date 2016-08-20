package sneakycoders.visualreact.level.levels;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import sneakycoders.visualreact.R;
import sneakycoders.visualreact.level.Level;

// Dynamically instantiated
@SuppressWarnings("unused")
public class LevelLabyrinth extends Level {
    // Cells in the X axis
    private int cellsX;
    // Cells in the Y axis
    private int cellsY;
    // Extremes coordinates (coordinate in the Y axis)
    private int leftExtremeY;
    private int rightExtremeY;
    // Cells
    private PathCell[][] cells;
    // Lists of cells that connect both extremes
    private List<PathCell> pathCells;
    // List of random cells
    private List<PathCell> randomCells;
    // Shapes to identify extremes
    private RectF leftExtremeShape;
    private RectF rightExtremeShape;
    // Colors
    private Paint[][] cellsPaints;
    private Paint extremesPaint;
    private Paint successPaint;
    private Paint failPaint;
    private int backgroundColor;
    // Timer handler
    private Handler handler;
    // Update function (to update the path cells)
    private Runnable updateCells;
    // View
    private LevelLabyrinthView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set colors
        int pathColor = getRandomColor();
        extremesPaint = new Paint();
        extremesPaint.setColor(failColor);
        successPaint = new Paint();
        successPaint.setColor(successColor);
        failPaint = new Paint();
        failPaint.setColor(failColor);
        backgroundColor = ContextCompat.getColor(getActivity(), R.color.neutral_dark);

        // Number of cells in each axis
        cellsX = getResources().getInteger(R.integer.level_labyrinth_cells_x);
        cellsY = getResources().getInteger(R.integer.level_labyrinth_cells_y);

        // Y coordinate of the extremes
        leftExtremeY = randomInInterval(0, cellsY - 1);
        rightExtremeY = randomInInterval(0, cellsY - 1);

        // Create lists and matrices
        pathCells = new ArrayList<>();
        randomCells = new ArrayList<>();
        cells = new PathCell[cellsX][cellsY];
        cellsPaints = new Paint[cellsX][cellsY];

        // Initialize cell paints
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                cellsPaints[i][j] = new Paint();
                cellsPaints[i][j].setColor(pathColor);
            }
        }

        // Set the handler
        handler = new Handler();

        // Create view
        rootView = new LevelLabyrinthView(getActivity());

        return rootView;
    }

    @Override
    public boolean onPlayerTap() {
        // Cancel timers
        handler.removeCallbacksAndMessages(null);

        // Find path
        List<Pair<Integer, Integer>> path = followPath(true);
        boolean result = (path.size() != 0) && (path.get(path.size() - 1).second == rightExtremeY);

        // Start from the right extreme to identify what to paint with the color for fail
        if (!result) {
            path.addAll(followPath(false));
        }

        // Color the path depending whether it connects both extremes or not
        Paint pathPaint = result ? successPaint : failPaint;
        for (Pair<Integer, Integer> p : path) {
            cellsPaints[p.first][p.second] = pathPaint;
        }

        // Change extremes color
        extremesPaint = pathPaint;

        // Redraw
        rootView.invalidate();

        // Success extremes are connected
        return result;
    }

    private List<Pair<Integer, Integer>> followPath(boolean leftToRight) {
        // Path from extreme to extreme
        List<Pair<Integer, Integer>> path = new ArrayList<>();

        // Starting point
        int x = leftToRight ? 0 : (cellsX - 1);
        int y = leftToRight ? leftExtremeY : rightExtremeY;

        // Current direction
        Direction direction = leftToRight ? Direction.Right : Direction.Left;

        // Follow the path as long as possible
        boolean endOfPath = false;
        while (!endOfPath) {
            int xPrev = x;
            int yPrev = y;
            PathType pathType = cells[x][y].pathType;
            // To the right cell
            if (direction == Direction.Right) {
                if (pathType == PathType.Horizontal) {
                    x++;
                } else if (pathType == PathType.TopLeft) {
                    direction = Direction.Top;
                    y--;
                } else if (pathType == PathType.BottomLeft) {
                    direction = Direction.Bottom;
                    y++;
                } else {
                    endOfPath = true;
                }
            }
            // To the left cell
            else if (direction == Direction.Left) {
                if (pathType == PathType.Horizontal) {
                    x--;
                } else if (pathType == PathType.TopRight) {
                    direction = Direction.Top;
                    y--;
                } else if (pathType == PathType.BottomRight) {
                    direction = Direction.Bottom;
                    y++;
                } else {
                    endOfPath = true;
                }
            }
            // To the top cell
            else if (direction == Direction.Top) {
                if (pathType == PathType.Vertical) {
                    y--;
                } else if (pathType == PathType.BottomLeft) {
                    direction = Direction.Left;
                    x--;
                } else if (pathType == PathType.BottomRight) {
                    direction = Direction.Right;
                    x++;
                } else {
                    endOfPath = true;
                }
            }
            // To the bottom cell
            else {
                if (pathType == PathType.Vertical) {
                    y++;
                } else if (pathType == PathType.TopLeft) {
                    direction = Direction.Left;
                    x--;
                } else if (pathType == PathType.TopRight) {
                    direction = Direction.Right;
                    x++;
                } else {
                    endOfPath = true;
                }
            }

            // Add new pair if it is valid
            if (!endOfPath) {
                path.add(new Pair<>(xPrev, yPrev));
            }

            // Check boundaries
            if ((x < 0) || (x >= cellsX) || (y < 0) || (y >= cellsY)) {
                endOfPath = true;
            }
        }

        return path;
    }

    private void initializeCells() {
        // Screen and cell sizes
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();
        float cellWidth = width / (float) cellsX;
        float cellHeight = height / (float) cellsY;
        float pathWidth = cellWidth * getResources().getFraction(R.fraction.level_labyrinth_path_width, 1, 1);

        // Set extremes
        float extremesWidth = pathWidth * 2.0f;
        leftExtremeShape = new RectF(-extremesWidth / 2.0f, leftExtremeY * cellHeight + ((cellHeight - extremesWidth) / 2.0f),
                extremesWidth / 2.0f, leftExtremeY * cellHeight + ((cellHeight + extremesWidth) / 2.0f));
        rightExtremeShape = new RectF(width - (extremesWidth / 2.0f), rightExtremeY * cellHeight + ((cellHeight - extremesWidth) / 2.0f),
                width + (extremesWidth / 2.0f), rightExtremeY * cellHeight + ((cellHeight + extremesWidth) / 2.0f));

        // Meeting point (in the X axis) for the left to right and right to left paths
        int xConnection = randomInInterval(0, cellsX - 1);

        // Save the path cells
        pathCells = new ArrayList<>();

        // Create path
        // Left to right
        int x = -1;
        int y = leftExtremeY;
        Direction direction = Direction.Right;
        while (x < xConnection) {
            PathType pathType;
            double p = Math.random();

            // To the right
            if (direction == Direction.Right) {
                // Switch to the top
                if ((p < 0.33) && (y > 1)) {
                    pathType = PathType.TopLeft;
                    direction = Direction.Top;
                }
                // Switch to the bottom
                else if ((p < 0.66) && (y < (cellsY - 2))) {
                    pathType = PathType.BottomLeft;
                    direction = Direction.Bottom;
                }
                // Continue to the right
                else {
                    pathType = PathType.Horizontal;
                }

                // Move to the right
                x++;
            }
            // To the top
            else if (direction == Direction.Top) {
                // Continue to the top
                if ((p < 0.33) && (y > 1)) {
                    pathType = PathType.Vertical;
                }
                // Switch to the right
                else {
                    pathType = PathType.BottomRight;
                    direction = Direction.Right;
                }

                // Move to the top
                y--;
            }
            // To the bottom
            else {
                // Continue to the bottom
                if ((p < 0.33) && (y < (cellsY - 2))) {
                    pathType = PathType.Vertical;
                }
                // Switch to the right
                else {
                    pathType = PathType.TopRight;
                    direction = Direction.Right;
                }

                // Move to the bottom
                y++;
            }

            // Create path cell
            cells[x][y] = new PathCell(x * cellWidth, y * cellHeight, cellWidth, cellHeight, pathWidth, pathType);
            pathCells.add(cells[x][y]);
        }

        // Save the endpoint in the Y axis for the left to right path
        int endFirstPath = y;

        // Right to left
        x = cellsX;
        y = rightExtremeY;
        direction = Direction.Left;
        while (x > xConnection) {
            PathType pathType;
            double p = Math.random();

            // To the left
            if (direction == Direction.Left) {
                // Switch to the top
                if ((p < 0.33) && (y > 1)) {
                    pathType = PathType.TopRight;
                    direction = Direction.Top;
                }
                // Switch to the bottom
                else if ((p < 0.66) && (y < (cellsY - 2))) {
                    pathType = PathType.BottomRight;
                    direction = Direction.Bottom;
                }
                // Continue to the right
                else {
                    pathType = PathType.Horizontal;
                }

                // Move to the left
                x--;
            }
            // To the top
            else if (direction == Direction.Top) {
                // Continue to the top
                if ((p < 0.33) && (y > 1)) {
                    pathType = PathType.Vertical;
                }
                // Switch to the left
                else {
                    pathType = PathType.BottomLeft;
                    direction = Direction.Left;
                }

                // Move to the top
                y--;
            }
            // To the bottom
            else {
                // Continue to the bottom
                if ((p < 0.33) && (y < (cellsY - 2))) {
                    pathType = PathType.Vertical;
                }
                // Switch to the left
                else {
                    pathType = PathType.TopLeft;
                    direction = Direction.Left;
                }

                // Move to the bottom
                y++;
            }

            // Create the path cell
            cells[x][y] = new PathCell(x * cellWidth, y * cellHeight, cellWidth, cellHeight, pathWidth, pathType);
            pathCells.add(cells[x][y]);
        }

        // Connection point
        // Aligned
        if (endFirstPath == y) {
            cells[xConnection][y] = new PathCell(x * cellWidth, y * cellHeight, cellWidth, cellHeight, pathWidth, PathType.Horizontal);
            pathCells.add(cells[xConnection][y]);
        }
        // Not aligned
        else {
            // Left is up
            if (endFirstPath < y) {
                cells[xConnection][endFirstPath] = new PathCell(x * cellWidth, endFirstPath * cellHeight, cellWidth, cellHeight, pathWidth, PathType.BottomLeft);
                pathCells.add(cells[xConnection][endFirstPath]);
                cells[xConnection][y] = new PathCell(x * cellWidth, y * cellHeight, cellWidth, cellHeight, pathWidth, PathType.TopRight);
                pathCells.add(cells[xConnection][y]);
                y--;
            }
            // Right is up
            else {
                cells[xConnection][endFirstPath] = new PathCell(x * cellWidth, endFirstPath * cellHeight, cellWidth, cellHeight, pathWidth, PathType.TopLeft);
                pathCells.add(cells[xConnection][endFirstPath]);
                cells[xConnection][y] = new PathCell(x * cellWidth, y * cellHeight, cellWidth, cellHeight, pathWidth, PathType.BottomRight);
                pathCells.add(cells[xConnection][y]);
                y++;
            }

            // Vertical path between both paths
            int movement = (y < endFirstPath) ? 1 : -1;
            while (y != endFirstPath) {
                cells[xConnection][y] = new PathCell(x * cellWidth, y * cellHeight, cellWidth, cellHeight, pathWidth, PathType.Vertical);
                pathCells.add(cells[xConnection][y]);
                y += movement;
            }
        }

        // Create the rest of the cells
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                // Check that it is not used in the path
                if (cells[i][j] == null) {
                    // Create random path cell
                    cells[i][j] = new PathCell(i * cellWidth, j * cellHeight, cellWidth, cellHeight, pathWidth, null);
                    randomCells.add(cells[i][j]);
                }
            }
        }

        // Set the update function
        final int delay = randomInt(R.integer.level_labyrinth_min_delay, R.integer.level_labyrinth_max_delay);
        final float pRandomCell = randomFloat(R.fraction.level_labyrinth_min_update_random, R.fraction.level_labyrinth_max_update_random);
        updateCells = new Runnable() {
            @Override
            public void run() {
                // Cell to be updated
                PathCell cell;

                // Select a random cell
                if ((Math.random() < pRandomCell) || (pathCells.size() == 0)) {
                    cell = randomCells.get(randomInInterval(0, randomCells.size() - 1));
                }
                // Select and remove one of the remaining path cells
                else {
                    cell = pathCells.remove(randomInInterval(0, pathCells.size() - 1));
                }

                // Update cell type
                cell.updatePathType();

                // Redraw
                rootView.invalidate();

                // Update again after the delay
                handler.postDelayed(updateCells, delay);
            }
        };

        // Set timer to call the update function
        handler.postDelayed(updateCells, delay);

        // Redraw
        rootView.invalidate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel timers
        handler.removeCallbacksAndMessages(null);
    }

    // Possible directions
    private enum Direction {
        Left, Right, Top, Bottom
    }

    // Types of cell paths
    private enum PathType {
        Horizontal, Vertical, BottomLeft, BottomRight, TopLeft, TopRight
    }

    // Class for the path cells
    private class PathCell {
        // Real and current path type (real is null if it is a random cell)
        private final PathType realPathType;
        public PathType pathType;
        // Underlying shapes to paint the path cell
        public RectF left;
        public RectF top;
        public RectF right;
        public RectF bottom;

        public PathCell(float left, float top, float width, float height, float pathWidth, PathType realPathType) {
            // Calculate inner extremes
            float centerX = left + (width / 2.0f);
            float centerY = top + (height / 2.0f);
            float halfPathWidth = pathWidth / 2.0f;
            float innerLeft = centerX - halfPathWidth;
            float innerTop = centerY - halfPathWidth;
            float innerRight = centerX + halfPathWidth;
            float innerBottom = centerY + halfPathWidth;

            // Initialize underlying shapes
            this.left = new RectF(left, innerTop, innerRight, innerBottom);
            this.top = new RectF(innerLeft, top, innerRight, innerBottom);
            this.right = new RectF(innerLeft, innerTop, left + width, innerBottom);
            this.bottom = new RectF(innerLeft, innerTop, innerRight, top + height);

            // Update first so every cell is initially random
            updatePathType();
            this.realPathType = realPathType;
        }

        public void updatePathType() {
            // Switch to the real PathType (if exists) or generate a random one
            pathType = (realPathType == null) ?
                    PathType.values()[randomInInterval(0, PathType.values().length - 1)]
                    : realPathType;
        }

        public void draw(Canvas canvas, Paint paint) {
            // Horizontal path
            if (pathType == PathType.Horizontal) {
                canvas.drawRect(left, paint);
                canvas.drawRect(right, paint);
            }
            // Vertical path
            else if (pathType == PathType.Vertical) {
                canvas.drawRect(top, paint);
                canvas.drawRect(bottom, paint);
            }
            // Bottom left corner
            else if (pathType == PathType.BottomLeft) {
                canvas.drawRect(left, paint);
                canvas.drawRect(bottom, paint);
            }
            // Bottom right corner
            else if (pathType == PathType.BottomRight) {
                canvas.drawRect(right, paint);
                canvas.drawRect(bottom, paint);
            }
            // Top left corner
            else if (pathType == PathType.TopLeft) {
                canvas.drawRect(top, paint);
                canvas.drawRect(left, paint);
            }
            // Top right corner
            else {
                canvas.drawRect(top, paint);
                canvas.drawRect(right, paint);
            }
        }
    }

    public class LevelLabyrinthView extends View {
        public LevelLabyrinthView(Context c) {
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

                // Draw cells
                for (int i = 0; i < cellsX; i++) {
                    for (int j = 0; j < cellsY; j++) {
                        cells[i][j].draw(canvas, cellsPaints[i][j]);
                    }
                }

                // Draw extremes
                canvas.drawOval(leftExtremeShape, extremesPaint);
                canvas.drawOval(rightExtremeShape, extremesPaint);
            }
        }
    }
}
