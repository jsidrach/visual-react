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
public class LevelSingular extends Level {
    // Cells in the X axis
    private int cellsX;
    // Cells in the Y axis
    private int cellsY;
    // Cells
    private FaceCell[][] cells;
    // Separators
    private RectF[] lines;
    // Sad face cell
    private SadFaceCell sadFace;
    // Paint and color
    private int backgroundColor;
    private Paint linesPaint;
    private Paint successPaint;
    // Determine result is success or fail
    private boolean result;
    // Handler
    private Handler handler;
    // Update function
    private Runnable updateCells;
    // View
    private LevelSingularView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set the handler
        handler = new Handler();

        // Number of cells in each axis
        cellsX = getResources().getInteger(R.integer.level_singular_cells_x);
        cellsY = getResources().getInteger(R.integer.level_singular_cells_y);

        // Initialize cells
        cells = new FaceCell[cellsX][cellsY];

        // Initialize separators
        lines = new RectF[cellsX + cellsY + 2];

        // Paints and colors
        backgroundColor = ContextCompat.getColor(getActivity(), R.color.neutral_dark);
        linesPaint = new Paint();
        linesPaint.setColor(ContextCompat.getColor(getActivity(), R.color.neutral_light));
        successPaint = new Paint();
        successPaint.setColor(ContextCompat.getColor(getActivity(), R.color.success_light));

        // Create View
        rootView = new LevelSingularView(getActivity());

        return rootView;
    }

    @Override
    public boolean onPlayerTap() {
        // Cancel timers
        handler.removeCallbacksAndMessages(null);

        // Redraw if sad face is shown
        if (result) {
            // Change to success color
            sadFace.backgroundPaint = successPaint;

            // Redraw
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

    private void initializeCells() {
        // Screen size
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();

        // Cell size
        final float cellWidth = (float) width / cellsX;
        final float cellHeight = (float) height / cellsY;
        final float cellSize = Math.min(cellWidth, cellHeight);

        // Starting point on x axis and y axis
        final float leftX = cellWidth > cellSize ? (width - cellsX * cellSize) / 2.0f : 0.0f;
        final float topY = cellHeight > cellSize ? (height - cellsY * cellSize) / 2.0f : 0.0f;

        // Margins
        final float eyeMarginToX = cellSize * getResources().getFraction(R.fraction.level_singular_margin_eye_x, 1, 1);
        final float eyeMarginToY = cellSize * getResources().getFraction(R.fraction.level_singular_margin_eye_y, 1, 1);

        // Lengths and widths of rectangles
        final float eyeLength = cellSize * getResources().getFraction(R.fraction.level_singular_eye_length, 1, 1);
        final float eyeWidth = cellSize * getResources().getFraction(R.fraction.level_singular_eye_width, 1, 1);
        final float mouthLength = cellSize * getResources().getFraction(R.fraction.level_singular_mouth_length, 1, 1);

        // Width of separators
        final float lineWidth = cellSize * getResources().getFraction(R.fraction.level_singular_line_width, 1, 1);

        // Background color for cells
        final Integer[] colorCells = getRandomColors(cellsX * cellsY);

        // Vertical separators
        for (int i = 0; i < cellsX + 1; i++) {
            float left = Math.max(0.0f, leftX + i * cellSize - lineWidth / 2.0f);
            float right = left + lineWidth;
            lines[i] = new RectF(left, topY, right, topY + cellSize * cellsY);
        }

        // Horizontal separators
        for (int j = cellsX + 1; j < lines.length; j++) {
            float top = Math.max(0.0f, topY + (j - cellsX - 1) * cellSize - lineWidth / 2.0f);
            float bottom = top + lineWidth / 2.0f;
            lines[j] = new RectF(leftX, top, leftX + cellsX * cellSize, bottom);
        }

        // Choose a random cell to change to sad face
        final int sadFaceX = randomInInterval(0, cellsX - 1);
        final int sadFaceY = randomInInterval(0, cellsY - 1);

        // Initialize cells
        for (int i = 0; i < cellsX; i++) {
            for (int j = 0; j < cellsY; j++) {
                // Face paint and mouth paint
                Paint facePaint = new Paint();
                Paint mouthPaint = new Paint();
                mouthPaint.setStyle(Paint.Style.STROKE);
                mouthPaint.setStrokeWidth(cellSize * getResources().getFraction(R.fraction.level_singular_stroke_width, 1, 1));

                // Assign color to paints
                int color = colorCells[i * cellsY + j];
                facePaint.setColor(color);
                mouthPaint.setColor(color);

                // Left and top
                float left = leftX + cellSize * i;
                float top = topY + cellSize * j;

                // Center of the cell
                float centerX = left + cellWidth / 2.0f;
                float centerY = top + cellHeight / 2.0f;

                // Eyes
                float eyeTop = centerY - eyeMarginToY - eyeWidth;
                float eyeBottom = centerY - eyeMarginToY;
                RectF leftEye = new RectF(centerX - eyeMarginToX - eyeLength, eyeTop, centerX - eyeMarginToX, eyeBottom);
                RectF rightEye = new RectF(centerX + eyeMarginToX, eyeTop, centerX + eyeMarginToX + eyeLength, eyeBottom);

                // Mouth
                RectF mouth = new RectF(centerX - mouthLength / 2.0f, centerY - mouthLength / 2.0f, centerX + mouthLength / 2.0f, centerY + mouthLength / 2.0f);

                // Create the cell
                cells[i][j] = new FaceCell(facePaint, mouthPaint, centerX, centerY, leftEye, rightEye, mouth);

                // Create the sad face cell
                if ((i == sadFaceX) && (j == sadFaceY)) {
                    // Background rectangle
                    RectF background = new RectF(left, top, left + cellSize, top + cellSize);

                    // Sad mouth
                    RectF sadMouth = new RectF(centerX - mouthLength / 2.0f, centerY + mouthLength / 6.0f, centerX + mouthLength / 2.0f, centerY + 7 * mouthLength / 6.0f);

                    // Create sad face
                    sadFace = new SadFaceCell(facePaint, mouthPaint, centerX, centerY, background, leftEye, rightEye, mouth, sadMouth);
                }
            }
        }

        // Timer schedule time
        long timerChangeFace = randomInInterval(getResources().getInteger(R.integer.level_singular_min_timer_delay), getResources().getInteger(R.integer.level_singular_max_timer_delay));

        // Change one cell to sad face
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Change the cell to sad face
                cells[sadFaceX][sadFaceY] = sadFace;
                sadFace.changeOrientation();

                // Change result to true
                result = true;

                // Redraw
                rootView.invalidate();
            }
        }, timerChangeFace);

        // Update cells
        final int delay = randomInInterval(getResources().getInteger(R.integer.level_singular_min_delay), getResources().getInteger(R.integer.level_singular_max_delay));
        updateCells = new Runnable() {
            @Override
            public void run() {
                // Select one cell
                int randomX = randomInInterval(0, cellsX - 1);
                int randomY = randomInInterval(0, cellsY - 1);

                // Rotate
                cells[randomX][randomY].changeOrientation();

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

    public class FaceCell {
        // Rectangles that form the face
        public RectF leftEye;
        public RectF rightEye;
        public RectF mouth;
        // Face paint
        public Paint facePaint;
        // Mouth paint
        public Paint mouthPaint;
        // Center of the cell
        public float centerX;
        public float centerY;
        // Orientation of the face
        public int orientation;

        public FaceCell(Paint facePaint, Paint mouthPaint, float centerX, float centerY, RectF leftEye, RectF rightEye, RectF mouth) {
            // Set paints
            this.facePaint = facePaint;
            this.mouthPaint = mouthPaint;

            // Center of the cell
            this.centerX = centerX;
            this.centerY = centerY;

            // Eyes
            this.leftEye = leftEye;
            this.rightEye = rightEye;

            // Mouth
            this.mouth = mouth;

            // Orientation
            orientation = 90 * randomInInterval(0, 3);
        }

        // Change to a random orientation
        public void changeOrientation() {
            this.orientation = 90 * randomInInterval(0, 3);
        }

        public void draw(Canvas canvas) {
            // Rotate the canvas
            canvas.save();
            canvas.rotate(this.orientation, centerX, centerY);

            // Draw left eye and right eye
            canvas.drawRect(leftEye, facePaint);
            canvas.drawRect(rightEye, facePaint);

            // Draw smiley mouth
            canvas.drawArc(mouth, 10.0f, 170.0f, false, mouthPaint);

            // Restore canvas
            canvas.restore();
        }
    }

    public class SadFaceCell extends FaceCell {
        // Background rectangle
        public RectF background;
        // Background paint
        public Paint backgroundPaint;
        // Sad mouth
        private RectF sadMouth;

        public SadFaceCell(Paint facePaint, Paint mouthPaint, float centerX, float centerY, RectF background, RectF leftEye, RectF rightEye, RectF mouth, RectF sadMouth) {
            super(facePaint, mouthPaint, centerX, centerY, leftEye, rightEye, mouth);

            // Set background rectangle
            this.background = background;
            backgroundPaint = new Paint();
            backgroundPaint.setColor(backgroundColor);

            // Set sad face
            this.sadMouth = sadMouth;
        }

        @Override
        public void draw(Canvas canvas) {
            // Rotate the canvas
            canvas.save();
            canvas.rotate(orientation, centerX, centerY);

            // Draw background rectangle
            canvas.drawRect(background, backgroundPaint);

            // Draw left eye and right eye
            canvas.drawRect(leftEye, facePaint);
            canvas.drawRect(rightEye, facePaint);

            // Draw sad mouth
            canvas.drawArc(sadMouth, 195.0f, 150.0f, false, mouthPaint);

            // Restore canvas
            canvas.restore();
        }
    }

    public class LevelSingularView extends View {
        public LevelSingularView(Context c) {
            super(c);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // Initializing
            if (cells[0][0] == null) {
                initializeCells();
            }
            // Playing
            else {
                // Set background color
                canvas.drawColor(backgroundColor);

                // Draw faces
                for (int i = 0; i < cellsX; i++) {
                    for (int j = 0; j < cellsY; j++) {
                        cells[i][j].draw(canvas);
                    }
                }

                // Draw separators
                for (RectF line : lines) {
                    canvas.drawRect(line, linesPaint);
                }
            }
        }
    }
}
