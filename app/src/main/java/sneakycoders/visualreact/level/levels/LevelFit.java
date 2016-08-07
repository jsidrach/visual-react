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
    private float strokeWidth;
    // Colors
    private Paint leftShapePaint;
    private Paint rightShapePaint;
    private int backgroundColor;
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
        leftShapeType = randomBoolean() ? ShapeType.Circle : ShapeType.Rectangle;
        rightShapeType = randomBoolean() ? ShapeType.Circle : ShapeType.Rectangle;

        // Do not allow two circles, make it two rectangles
        if ((leftShapeType == ShapeType.Circle) && (rightShapeType == ShapeType.Circle)) {
            leftShapeType = ShapeType.Rectangle;
            rightShapeType = ShapeType.Rectangle;
        }

        // Set colors
        Integer[] shapeColors = getRandomColors(2);
        leftShapePaint = new Paint();
        leftShapePaint.setStyle(Paint.Style.STROKE);
        leftShapePaint.setColor(shapeColors[0]);
        rightShapePaint = new Paint();
        rightShapePaint.setStyle(Paint.Style.STROKE);
        rightShapePaint.setColor(shapeColors[1]);
        backgroundColor = ContextCompat.getColor(getActivity(), R.color.neutral_dark);

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
        float diff = strokeWidth / 2.0f;

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
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();
        halfWidth = width / 2.0f;
        halfHeight = height / 2.0f;

        // Set stroke width
        strokeWidth = height * getResources().getFraction(R.fraction.level_fit_stroke_width, 1, 1);
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

        // Choose dimensions
        float margin = getResources().getFraction(R.fraction.level_fit_margin, 1, 1);
        if (((leftShapeType == ShapeType.Circle) && (rightShapeType == ShapeType.Rectangle))
                || ((leftShapeType == ShapeType.Rectangle) && (rightShapeType == ShapeType.Circle))) {
            // Random original rectangle
            float originalRectangleWidth = height * randomFloat(R.fraction.level_fit_min_side, R.fraction.level_fit_max_side);
            float originalRectangleHeight = height * randomFloat(R.fraction.level_fit_min_side, R.fraction.level_fit_max_side);

            // Circle radius between rectangle sides
            float originalDiameter = randomInInterval(originalRectangleWidth, originalRectangleHeight);

            // Random resized square
            float resizedSquareSide = height * randomFloat(R.fraction.level_fit_min_side, R.fraction.level_fit_max_side);

            // Fit circle inside square
            float resizedDiameter;
            if (randomBoolean()) {
                resizedDiameter = resizedSquareSide - height * margin;
            }
            // Fit square inside circle
            else {
                resizedDiameter = (float) ((Math.sqrt(2.0) * resizedSquareSide) + height * margin);
            }

            // Assign dimensions
            if (leftShapeType == ShapeType.Circle) {
                originalLeftShapeWidth = originalDiameter;
                originalLeftShapeHeight = originalDiameter;
                resizedLeftShapeWidth = resizedDiameter;
                resizedLeftShapeHeight = resizedDiameter;
                originalRightShapeWidth = originalRectangleWidth;
                originalRightShapeHeight = originalRectangleHeight;
                resizedRightShapeWidth = resizedSquareSide;
                resizedRightShapeHeight = resizedSquareSide;
            } else {
                originalLeftShapeWidth = originalRectangleWidth;
                originalLeftShapeHeight = originalRectangleHeight;
                resizedLeftShapeWidth = resizedSquareSide;
                resizedLeftShapeHeight = resizedSquareSide;
                originalRightShapeWidth = originalDiameter;
                originalRightShapeHeight = originalDiameter;
                resizedRightShapeWidth = resizedDiameter;
                resizedRightShapeHeight = resizedDiameter;
            }
        } else {
            // Random original rectangles
            float originalOuterRectangleWidth = height * randomFloat(R.fraction.level_fit_min_side, R.fraction.level_fit_max_side);
            float originalOuterRectangleHeight = height * randomFloat(R.fraction.level_fit_min_side, R.fraction.level_fit_max_side);
            float originalInnerRectangleWidth;
            float originalInnerRectangleHeight;
            float minDiff = height * getResources().getFraction(R.fraction.level_fit_min_diff, 1, 1);
            if (randomBoolean()) {
                originalInnerRectangleWidth = randomInInterval(originalOuterRectangleWidth + (minDiff / 2.0f), height * getResources().getFraction(R.fraction.level_fit_max_side, 1, 1));
                originalInnerRectangleHeight = randomInInterval(minDiff, originalOuterRectangleHeight - (minDiff / 2.0f));
            } else {
                originalInnerRectangleWidth = randomInInterval(minDiff, originalOuterRectangleWidth - minDiff);
                originalInnerRectangleHeight = randomInInterval(originalOuterRectangleHeight + (minDiff / 2.0f), height * getResources().getFraction(R.fraction.level_fit_max_side, 1, 1));

            }

            // Random outer resized square
            float resizedOuterSquareSide = height * randomFloat(R.fraction.level_fit_min_side, R.fraction.level_fit_max_side);

            // Inner resized square
            float resizedInnerSquareSide = resizedOuterSquareSide - height * margin;

            // Assign dimensions
            if (randomBoolean()) {
                originalLeftShapeWidth = originalOuterRectangleWidth;
                originalLeftShapeHeight = originalOuterRectangleHeight;
                originalRightShapeWidth = originalInnerRectangleWidth;
                originalRightShapeHeight = originalInnerRectangleHeight;
            } else {
                originalLeftShapeWidth = originalInnerRectangleWidth;
                originalLeftShapeHeight = originalInnerRectangleHeight;
                originalRightShapeWidth = originalOuterRectangleWidth;
                originalRightShapeHeight = originalOuterRectangleHeight;
            }
            if (randomBoolean()) {
                resizedLeftShapeWidth = resizedOuterSquareSide;
                resizedLeftShapeHeight = resizedOuterSquareSide;
                resizedRightShapeWidth = resizedInnerSquareSide;
                resizedRightShapeHeight = resizedInnerSquareSide;
            } else {
                resizedLeftShapeWidth = resizedInnerSquareSide;
                resizedLeftShapeHeight = resizedInnerSquareSide;
                resizedRightShapeWidth = resizedOuterSquareSide;
                resizedRightShapeHeight = resizedOuterSquareSide;
            }
        }

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
