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
    // Screen size
    private float halfWidth;
    private float halfHeight;
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

        // Do not allow two circles
        if ((leftShapeType == ShapeType.Circle) && (rightShapeType == ShapeType.Circle)) {
            if (randomInInterval(0.0, 1.0) < 0.5) {
                leftShapeType = ShapeType.Rectangle;
            } else {
                rightShapeType = ShapeType.Rectangle;
            }
        }

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
        leftShape.offsetTo(halfWidth - (leftShape.width() / 2.0f), halfHeight - (leftShape.height() / 2.0f));
        rightShape.offsetTo(halfWidth - (rightShape.width() / 2.0f), halfHeight - (rightShape.height() / 2.0f));

        // Force redraw
        rootView.invalidate();

        // Check if one shape fits into the other one
        return shapesFit();
    }

    private boolean shapesFit() {
        // Take into account the stroke width (half of it is inside, half outside)
        int diff = strokeWidth / 2;

        // Adjust sizes
        RectF leftShapeIn = new RectF(leftShape.left - diff, leftShape.top - diff, leftShape.right + diff, leftShape.bottom + diff);
        RectF leftShapeOut = new RectF(leftShape.left + diff, leftShape.top + diff, leftShape.right - diff, leftShape.bottom - diff);
        RectF rightShapeIn = new RectF(rightShape.left - diff, rightShape.top - diff, rightShape.right + diff, rightShape.bottom + diff);
        RectF rightShapeOut = new RectF(rightShape.left + diff, rightShape.top + diff, rightShape.right - diff, rightShape.bottom - diff);

        // Check if one shape fits into the other one
        if ((leftShapeType == ShapeType.Rectangle) && (rightShapeType == ShapeType.Rectangle)) {
            return (leftShapeOut.contains(rightShapeIn) || rightShapeOut.contains(leftShapeIn));
        } else {
            boolean leftShapeIsCircle = (leftShapeType == ShapeType.Circle);
            RectF circleIn = leftShapeIsCircle ? leftShapeIn : rightShapeIn;
            RectF circleOut = leftShapeIsCircle ? leftShapeOut : rightShapeOut;
            RectF rectangleIn = leftShapeIsCircle ? rightShapeIn : leftShapeIn;
            RectF rectangleOut = leftShapeIsCircle ? rightShapeOut : leftShapeOut;

            // Calculate distance between center of circle and the farthest corner of the rectangle
            float circleOutRadius = circleOut.width() / 2;
            float distX = Math.max(circleOut.centerX() - rectangleIn.left, rectangleIn.right - circleOut.centerX());
            float distY = Math.max(circleOut.centerY() - rectangleIn.top, rectangleIn.bottom - circleOut.centerY());
            boolean rectangleInCircle = ((circleOutRadius * circleOutRadius) >= ((distX * distX) + (distY * distY)));

            return (rectangleInCircle || rectangleOut.contains(circleIn));
        }
    }

    private void initializeShapes() {
        // Screen size
        float width = rootView.getMeasuredWidth();
        float height = rootView.getMeasuredHeight();
        halfWidth = width / 2.0f;
        halfHeight = height / 2.0f;

        // Set stroke width
        strokeWidth = (int) (height * getResources().getFraction(R.fraction.level_fit_stroke_width, 1, 1));
        leftShapePaint.setStrokeWidth(strokeWidth);
        rightShapePaint.setStrokeWidth(strokeWidth);

        // Original and resized shapes
        final float originalLeftShapeWidth;
        final float originalLeftShapeHeight;
        final float resizedLeftShapeWidth;
        final float resizedLeftShapeHeight;
        final float originalRightShapeWidth;
        final float originalRightShapeHeight;
        final float resizedRightShapeWidth;
        final float resizedRightShapeHeight;

        // Choose dimensions of the left shape
        // TODO: Make dimensions that do fit but have room to change
        if (leftShapeType == ShapeType.Circle) {
            // Circle
        } else {
            // Rectangle
        }

        // Choose dimensions of the right shape
        if (rightShapeType == ShapeType.Circle) {
            // Circle
        } else {
            // Rectangle
        }
        originalLeftShapeWidth = 200;
        originalLeftShapeHeight = 200;
        resizedLeftShapeWidth = 400;
        resizedLeftShapeHeight = 400;
        originalRightShapeWidth = 250;
        originalRightShapeHeight = 250;
        resizedRightShapeWidth = 100;
        resizedRightShapeHeight = 100;

        // Create the left and right shapes
        leftShape = new RectF(
                (halfWidth - originalLeftShapeWidth) / 2.0f,
                halfHeight - (originalLeftShapeHeight / 2.0f),
                (halfWidth + originalLeftShapeWidth) / 2.0f,
                halfHeight + (originalLeftShapeHeight / 2.0f));
        rightShape = new RectF(
                ((3.0f * halfWidth) - originalRightShapeWidth) / 2.0f,
                halfHeight - (originalRightShapeHeight / 2.0f),
                ((3.0f * halfWidth) + originalRightShapeWidth) / 2.0f,
                halfHeight + (originalRightShapeHeight / 2.0f));

        // Set the movement
        final int delay = 1000 / getResources().getInteger(R.integer.level_fit_frames_per_second);
        final long resizeTime = randomInt(R.integer.level_fit_min_resize_time, R.integer.level_fit_max_resize_time);
        final long startTime = System.currentTimeMillis();
        updateShapes = new Runnable() {
            @Override
            public void run() {
                // Time since we started the animation, modulo two times resizeTime
                // First we scale up then down
                long totalResizeTime = 2 * resizeTime;
                long elapsedTime = (System.currentTimeMillis() - startTime) % totalResizeTime;

                // Calculate offset in percentage (from 0% to 100%)
                float offset = ((elapsedTime < resizeTime) ? elapsedTime : (totalResizeTime - elapsedTime)) / (float) resizeTime;
                float leftShapeWidth = (1.0f - offset) * originalLeftShapeWidth + offset * resizedLeftShapeWidth;
                float leftShapeHeight = (1.0f - offset) * originalLeftShapeHeight + offset * resizedLeftShapeHeight;
                float rightShapeWidth = (1.0f - offset) * originalRightShapeWidth + offset * resizedRightShapeWidth;
                float rightShapeHeight = (1.0f - offset) * originalRightShapeHeight + offset * resizedRightShapeHeight;

                // Resize and center shapes
                leftShape.set(
                        (halfWidth - leftShapeWidth) / 2.0f,
                        halfHeight - (leftShapeHeight / 2.0f),
                        (halfWidth + leftShapeWidth) / 2.0f,
                        halfHeight + (leftShapeHeight / 2.0f));
                rightShape.set(
                        ((3.0f * halfWidth) - rightShapeWidth) / 2.0f,
                        halfHeight - (rightShapeHeight / 2.0f),
                        ((3.0f * halfWidth) + rightShapeWidth) / 2.0f,
                        halfHeight + (rightShapeHeight / 2.0f));

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
