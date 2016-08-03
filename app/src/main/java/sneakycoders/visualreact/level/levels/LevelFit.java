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
public class LevelFit extends Level {
    // Shapes
    private RectF leftShape;
    private RectF rightShape;
    // Shape Types
    private ShapeType leftShapeType;
    private ShapeType rightShapeType;
    // Timer handler
    private Handler handler;
    // Update function (to resize the shapes)
    private Runnable updateShapes;
    // Stroke width
    private int strokeWidth;
    // Colors
    private int backgroundColor;
    private Paint leftShapePaint;
    private Paint rightShapePaint;
    // View
    private LevelFitView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set initial state
        handler = new Handler();
        leftShape = null;
        rightShape = null;

        // Choose shape combination
        leftShapeType = (randomInInterval(0.0, 1.0) < 0.5) ? ShapeType.Circle : ShapeType.Rectangle;
        rightShapeType = (randomInInterval(0.0, 1.0) < 0.5) ? ShapeType.Circle : ShapeType.Rectangle;

        // Set colors
        backgroundColor = ContextCompat.getColor(getActivity(), R.color.neutral_dark);
        Integer[] shapeColors = getRandomColors(2);
        leftShapePaint = new Paint();
        leftShapePaint.setStyle(Paint.Style.STROKE);
        leftShapePaint.setColor(shapeColors[0]);
        rightShapePaint = new Paint();
        rightShapePaint.setStyle(Paint.Style.STROKE);
        rightShapePaint.setColor(shapeColors[1]);

        // Create view
        rootView = new LevelFitView(getActivity());

        return rootView;
    }

    @Override
    public boolean onPlayerTap() {
        // Stop animation
        handler.removeCallbacksAndMessages(null);

        // Regroup shapes in the middle
        float halfWidth = rootView.getMeasuredWidth() / 2.0f;
        float halfHeight = rootView.getMeasuredHeight() / 2.0f;
        leftShape.offsetTo(halfWidth - (leftShape.width() / 2.0f), halfHeight - (leftShape.height() / 2.0f));
        rightShape.offsetTo(halfWidth - (rightShape.width() / 2.0f), halfHeight - (rightShape.height() / 2.0f));

        // Force redraw
        rootView.invalidate();

        // Check if one shape fits into the other one
        return checkFit();
    }

    private boolean checkFit() {
        // Take into account the stroke width (half of it is inside, half outside)
        RectF leftShapeIn = new RectF();
        RectF leftShapeOut = new RectF();
        RectF rightShapeIn = new RectF();
        RectF rightShapeOut = new RectF();

        // Adjust sizes
        int diff = strokeWidth / 2;
        leftShapeIn.set(leftShape.left - diff, leftShape.top - diff, leftShape.right + diff, leftShape.bottom + diff);
        leftShapeOut.set(leftShape.left + diff, leftShape.top + diff, leftShape.right - diff, leftShape.bottom - diff);
        rightShapeIn.set(rightShape.left - diff, rightShape.top - diff, rightShape.right + diff, rightShape.bottom + diff);
        rightShapeOut.set(rightShape.left + diff, rightShape.top + diff, rightShape.right - diff, rightShape.bottom - diff);

        // Check if one shape fits into the other one
        return (leftShapeOut.contains(rightShapeIn) || rightShapeOut.contains(leftShapeIn));
    }

    private void initializeShapes() {
        // Screen size
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();

        // Set stroke width
        strokeWidth = (int) (height * getResources().getFraction(R.fraction.level_fit_stroke_width, 1, 1));
        leftShapePaint.setStrokeWidth(strokeWidth);
        rightShapePaint.setStrokeWidth(strokeWidth);

        // TODO: Make dimensions that do fit but have room to change
        // Margin from extremes
        int margin = (int) (width * getResources().getFraction(R.fraction.level_collision_margin_extremes, 1, 1));

        // Dimension variables (reused for both shapes)
        int sideX;
        int sideY;
        int top;

        // Choose dimensions of the left shape
        if (leftShapeType == ShapeType.Circle) {
            // Circle
            int radius = (int) (height * randomDouble(R.fraction.level_collision_min_radius, R.fraction.level_collision_max_radius));
            sideX = 2 * radius;
            sideY = sideX;
            top = (height / 2) - radius;
        } else {
            // Rectangle
            sideX = (int) (height * randomDouble(R.fraction.level_collision_min_side, R.fraction.level_collision_max_side));
            sideY = (int) (height * randomDouble(R.fraction.level_collision_min_side, R.fraction.level_collision_max_side));
            top = (height - sideY) / 2;
        }

        // Save the starting point and distance the left shape can travel (in the X axis)
        final int leftShapeStart = margin;
        final int leftTotalDistance = width - (2 * margin) - sideX;

        // Create the left shape
        leftShape = new RectF(margin, top, margin + sideX, top + sideY);

        // Choose dimensions of the right shape
        if (rightShapeType == ShapeType.Circle) {
            // Circle
            int radius = (int) (height * randomDouble(R.fraction.level_collision_min_radius, R.fraction.level_collision_max_radius));
            sideX = 2 * radius;
            sideY = sideX;
            top = (height / 2) - radius;
        } else {
            // Rectangle
            sideX = (int) (height * randomDouble(R.fraction.level_collision_min_side, R.fraction.level_collision_max_side));
            sideY = (int) (height * randomDouble(R.fraction.level_collision_min_side, R.fraction.level_collision_max_side));
            top = (height - sideY) / 2;
        }

        // Save the starting point and distance the left shape can travel (in the X axis)
        final int rightShapeStart = width - margin - sideX;
        final int rightTotalDistance = width - (2 * margin) - sideX;

        // Create the right shape
        rightShape = new RectF(rightShapeStart, top, width - margin, top + sideY);

        // Set the movement
        final int delay = 1000 / getResources().getInteger(R.integer.level_fit_frames_per_second);
        final long resizeTime = randomInt(R.integer.level_fit_min_resize_time, R.integer.level_fit_max_resize_time);
        final long startTime = System.currentTimeMillis();
        updateShapes = new Runnable() {
            @Override
            public void run() {
                // Time since we started the animation, modulo two times resizeTime
                // First we increase then decrease
                long totalResizeTime = 2 * resizeTime;
                long elapsedTime = (System.currentTimeMillis() - startTime) % totalResizeTime;

                // Calculate offset in percentage (from -100% to 100%)
                double offset = ((elapsedTime < resizeTime) ? elapsedTime : (totalResizeTime - elapsedTime)) / (double) resizeTime;
                int leftNewTop = (int) (leftShapeStart + offset * leftTotalDistance);
                leftShape.offsetTo(leftNewTop, leftShape.top);
                int rightNewTop = (int) (rightShapeStart - offset * rightTotalDistance);
                rightShape.offsetTo(rightNewTop, rightShape.top);

                // Update view
                rootView.invalidate();

                // Update again after the delay
                handler.postDelayed(updateShapes, delay);
            }
        };

        // Set timer to call the movement function
        handler.postDelayed(updateShapes, delay);

        // Force redraw
        rootView.invalidate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel animation
        handler.removeCallbacksAndMessages(null);
    }

    // Shape combinations
    private enum ShapeType {
        Circle, Rectangle
    }

    public class LevelFitView extends View {
        public LevelFitView(Context c) {
            super(c);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // Uninitialized
            if ((leftShape == null) || (rightShape == null)) {
                initializeShapes();
            }
            // Playing
            else {
                // Set background color
                canvas.drawColor(backgroundColor);

                // Draw shapes
                if (leftShapeType == ShapeType.Circle) {
                    canvas.drawOval(leftShape, leftShapePaint);
                } else {
                    canvas.drawRect(leftShape, leftShapePaint);
                }
                if (rightShapeType == ShapeType.Circle) {
                    canvas.drawOval(rightShape, rightShapePaint);
                } else {
                    canvas.drawRect(rightShape, rightShapePaint);
                }
            }
        }
    }
}
