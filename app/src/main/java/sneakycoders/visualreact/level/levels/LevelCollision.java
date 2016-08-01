package sneakycoders.visualreact.level.levels;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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
public class LevelCollision extends Level {
    // (Possible) Shapes
    private Rect leftShapeRect;
    private RectF leftShapeCircle;
    private Rect rightShapeRect;
    private RectF rightShapeCircle;
    // Flag to see which shape should be drawn first
    private boolean drawLeftFirst;
    // Middle block
    private Rect middleBlock;
    // Timer handler
    private Handler handler;
    // Update function (to move the shapes)
    private Runnable updateShapes;
    // Colors
    private int backgroundColor;
    private Paint middleBlockPaint;
    private Paint firstShapePaint;
    private Paint secondShapePaint;
    // View
    private LevelCollisionView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set initial state
        handler = new Handler();
        leftShapeRect = null;
        leftShapeCircle = null;
        rightShapeRect = null;
        rightShapeCircle = null;

        // Set colors
        backgroundColor = ContextCompat.getColor(getActivity(), R.color.neutral_dark);
        middleBlockPaint = new Paint();
        middleBlockPaint.setColor(ContextCompat.getColor(getActivity(), R.color.neutral_secondary));
        Integer[] shapeColors = getRandomColors(2);
        firstShapePaint = new Paint();
        firstShapePaint.setColor(shapeColors[0]);
        secondShapePaint = new Paint();
        secondShapePaint.setColor(shapeColors[1]);

        // Create view
        rootView = new LevelCollisionView(getActivity());

        return rootView;
    }

    @Override
    public boolean onPlayerTap() {
        // Stop animation
        handler.removeCallbacksAndMessages(null);

        // Remove middle block
        middleBlock = null;

        // Force redraw
        rootView.invalidate();

        // Check collision
        return checkCollision();
    }

    private boolean checkCollision() {
        // Two circles
        if ((leftShapeCircle != null) && (rightShapeCircle != null)) {
            // Calculate centers and radius
            double centerLeftX = leftShapeCircle.left + (leftShapeCircle.width() / 2.0);
            double centerLeftY = leftShapeCircle.top + (leftShapeCircle.height() / 2.0);
            double centerRightX = rightShapeCircle.left + (rightShapeCircle.width() / 2.0);
            double centerRightY = rightShapeCircle.top + (rightShapeCircle.height() / 2.0);
            double distCentersX = centerLeftX - centerRightX;
            double distCentersY = centerLeftY - centerRightY;
            double distCentersSq = distCentersX * distCentersX + distCentersY * distCentersY;
            double radiusLeft = leftShapeCircle.width() / 2.0;
            double radiusRight = rightShapeCircle.width() / 2.0;

            // Collide if sum of radius is less than the distance between centers
            double sumRadius = radiusLeft + radiusRight;
            return (distCentersSq <= (sumRadius * sumRadius));
        }
        // Two rectangles
        else if ((leftShapeRect != null) && (rightShapeRect != null)) {
            return Rect.intersects(leftShapeRect, rightShapeRect);
        }
        // Circle and rectangle
        else {
            // Calculate center and radius
            RectF circle = (leftShapeCircle == null) ? rightShapeCircle : leftShapeCircle;
            double centerX = circle.left + (circle.width() / 2.0);
            double centerY = circle.top + (circle.height() / 2.0);
            double radius = circle.width() / 2.0;

            // Select rectangle
            Rect rect = (leftShapeRect == null) ? rightShapeRect : leftShapeRect;

            // Find the closest point to the circle within the rectangle
            // Limit closestX to be in [rect.left, rect.right]
            double closestX = ((centerX >= rect.left) && (centerX <= rect.right)) ? centerX : (centerX < rect.left ? rect.left : rect.right);

            // Limit closestY to be in [rect.top, rect.bottom]
            double closestY = ((centerY >= rect.top) && (centerY <= rect.bottom)) ? centerY : (centerY < rect.top ? rect.top : rect.bottom);

            // Calculate the distance between the circle's center and this closest point
            double distX = centerX - closestX;
            double distY = centerY - closestY;
            double distSq = (distX * distX) + (distY * distY);

            // Collide if the distance is less than the circle's radius
            return (distSq <= (radius * radius));
        }
    }

    private void initializeShapes() {
        // Screen size
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();

        // Margin from extremes
        int margin = (int) (width * getResources().getFraction(R.fraction.level_collision_margin_extremes, 1, 1));

        // Save the original starting points
        final int leftShapeStart;
        final int rightShapeStart;

        // Save the distances each shape can travel (in the X axis)
        final int leftTotalDistance;
        final int rightTotalDistance;

        // Create left shape
        int leftDim;
        if (randomInInterval(0.0, 1.0) <= 0.5) {
            // Circle
            int radius = (int) (height * randomDouble(R.fraction.level_collision_min_radius, R.fraction.level_collision_max_radius));
            int top = (height / 2) - radius;
            int side = 2 * radius;
            leftShapeStart = margin;
            leftTotalDistance = width - (2 * margin) - side;
            leftShapeCircle = new RectF(leftShapeStart, top, margin + side, top + side);
            leftDim = side;
        } else {
            // Rectangle
            int sideX = (int) (height * randomDouble(R.fraction.level_collision_min_side, R.fraction.level_collision_max_side));
            int sideY = (int) (height * randomDouble(R.fraction.level_collision_min_side, R.fraction.level_collision_max_side));
            int top = (height - sideY) / 2;
            leftShapeStart = margin;
            leftTotalDistance = width - (2 * margin) - sideX;
            leftShapeRect = new Rect(leftShapeStart, top, margin + sideX, top + sideY);
            leftDim = Math.min(sideX, sideY);
        }

        // Create right shape
        int rightDim;
        if (randomInInterval(0.0, 1.0) <= 0.5) {
            // Circle
            int radius = (int) (height * randomDouble(R.fraction.level_collision_min_radius, R.fraction.level_collision_max_radius));
            int top = (height / 2) - radius;
            int side = 2 * radius;
            rightShapeStart = width - margin - side;
            rightTotalDistance = width - (2 * margin) - side;
            rightShapeCircle = new RectF(rightShapeStart, top, width - margin, top + side);
            rightDim = side;
        } else {
            // Rectangle
            int sideX = (int) (height * randomDouble(R.fraction.level_collision_min_side, R.fraction.level_collision_max_side));
            int sideY = (int) (height * randomDouble(R.fraction.level_collision_min_side, R.fraction.level_collision_max_side));
            int top = (height - sideY) / 2;
            rightShapeStart = width - margin - sideX;
            rightTotalDistance = width - (2 * margin) - sideX;
            rightShapeRect = new Rect(rightShapeStart, top, width - margin, top + sideY);
            rightDim = Math.min(sideX, sideY);
        }

        // Small variations on Y axis, while ensuring collision
        double dimYLeft = (leftShapeRect == null) ? leftShapeCircle.height() : leftShapeRect.height();
        double dimYRight = (rightShapeRect == null) ? rightShapeCircle.height() : rightShapeRect.height();
        double dimY = (dimYLeft + dimYRight) / 2;
        double maxDiffY = dimY * (1.0 - getResources().getFraction(R.fraction.level_collision_min_shape_collision_height, 1, 1));

        // Calculate variation, with random Y axis direction
        int variationY = (int) (randomInInterval(-maxDiffY, maxDiffY) / 2.0);
        if (leftShapeRect != null) {
            leftShapeRect.offset(0, variationY);
        } else {
            leftShapeCircle.offset(0, variationY);
        }
        if (rightShapeRect != null) {
            rightShapeRect.offset(0, -variationY);
        } else {
            rightShapeCircle.offset(0, -variationY);
        }

        // Set the movement
        final int delay = 1000 / getResources().getInteger(R.integer.level_collision_frames_per_second);
        final long movingTime = randomInt(R.integer.level_collision_min_moving_time, R.integer.level_collision_max_moving_time);
        final long startTime = System.currentTimeMillis();
        updateShapes = new Runnable() {
            @Override
            public void run() {
                // Time since we started the animation, modulo two times movingTime
                // First we go straight, then backwards
                long roundTripTime = 2 * movingTime;
                long elapsedTime = (System.currentTimeMillis() - startTime) % roundTripTime;

                // Calculate offset in percentage (from -100% to 100%)
                double offset = ((elapsedTime < movingTime) ? elapsedTime : (roundTripTime - elapsedTime)) / (double) movingTime;
                int leftNewTop = (int) (leftShapeStart + offset * leftTotalDistance);
                if (leftShapeCircle != null) {
                    leftShapeCircle.offsetTo(leftNewTop, leftShapeCircle.top);
                } else {
                    leftShapeRect.offsetTo(leftNewTop, leftShapeRect.top);
                }
                int rightNewTop = (int) (rightShapeStart - offset * rightTotalDistance);
                if (rightShapeCircle != null) {
                    rightShapeCircle.offsetTo(rightNewTop, rightShapeCircle.top);
                } else {
                    rightShapeRect.offsetTo(rightNewTop, rightShapeRect.top);
                }

                // Update view
                rootView.invalidate();

                // Update again after the delay
                handler.postDelayed(updateShapes, delay);
            }
        };

        // Set timer to call the movement function
        handler.postDelayed(updateShapes, delay);

        // Choose preference so that the small shape is always visible
        drawLeftFirst = (leftDim >= rightDim);

        // Create middle block
        double blockWidth = width * randomDouble(R.fraction.level_collision_min_block_width, R.fraction.level_collision_max_block_width);
        middleBlock = new Rect((int) ((width - blockWidth) / 2.0), 0, (int) ((width + blockWidth) / 2.0), height);

        // Force redraw
        rootView.invalidate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel animation
        handler.removeCallbacksAndMessages(null);
    }

    public class LevelCollisionView extends View {
        public LevelCollisionView(Context c) {
            super(c);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // Uninitialized
            if ((leftShapeRect == null) && (leftShapeCircle == null)) {
                initializeShapes();
            }
            // Playing
            else {
                // Set background color
                canvas.drawColor(backgroundColor);

                // Draw shapes in order
                Rect firstRect = drawLeftFirst ? leftShapeRect : rightShapeRect;
                RectF firstCircle = drawLeftFirst ? leftShapeCircle : rightShapeCircle;
                Rect secondRect = drawLeftFirst ? rightShapeRect : leftShapeRect;
                RectF secondCircle = drawLeftFirst ? rightShapeCircle : leftShapeCircle;
                if (firstRect != null) {
                    canvas.drawRect(firstRect, firstShapePaint);
                } else {
                    canvas.drawOval(firstCircle, firstShapePaint);
                }
                if (secondRect != null) {
                    canvas.drawRect(secondRect, secondShapePaint);
                } else {
                    canvas.drawOval(secondCircle, secondShapePaint);
                }

                // Draw middle block if necessary
                if (middleBlock != null) {
                    canvas.drawRect(middleBlock, middleBlockPaint);
                }
            }
        }
    }
}
