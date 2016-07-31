package sneakycoders.visualreact.level.levels;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
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
    // Middle block
    private Rect middleBlock;
    // Colors
    private int backgroundColor;
    private Paint middleBlockPaint;
    private Paint leftShapePaint;
    private Paint rightShapePaint;
    // View
    private LevelCollisionView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set initial state
        leftShapeRect = null;
        leftShapeCircle = null;
        rightShapeRect = null;
        rightShapeCircle = null;

        // Set colors
        backgroundColor = ContextCompat.getColor(getActivity(), R.color.neutral_dark);
        middleBlockPaint = new Paint();
        middleBlockPaint.setColor(ContextCompat.getColor(getActivity(), R.color.neutral_secondary));
        Integer[] shapeColors = getRandomColors(2);
        leftShapePaint = new Paint();
        leftShapePaint.setColor(shapeColors[0]);
        rightShapePaint = new Paint();
        rightShapePaint.setColor(shapeColors[1]);

        // Create view
        rootView = new LevelCollisionView(getActivity());

        return rootView;
    }

    @Override
    public boolean onPlayerTap() {
        // Stop animation and remove middle block

        middleBlock = null;

        // TODO: move items

        // Force redraw
        rootView.invalidate();

        // Check collision
        return checkCollision();
    }

    private boolean checkCollision() {
        // Two circles
        if ((leftShapeCircle != null) && (rightShapeCircle != null)) {
            // Calculate centers and radius
            double centerLeftX = leftShapeCircle.left + (leftShapeCircle.right / 2);
            double centerLeftY = leftShapeCircle.top + (leftShapeCircle.bottom / 2);
            double centerRightX = rightShapeCircle.left + (rightShapeCircle.right / 2);
            double centerRightY = rightShapeCircle.top + (rightShapeCircle.bottom / 2);
            double distCentersX = centerLeftX - centerRightX;
            double distCentersY = centerLeftY - centerRightY;
            double distCentersSq = distCentersX * distCentersX + distCentersY * distCentersY;
            double radiusLeft = (leftShapeCircle.right - leftShapeCircle.left) / 2;
            double radiusRight = (rightShapeCircle.right - rightShapeCircle.left) / 2;

            // Collide if sum of radius is less than the distance between centers
            double sumRadius = radiusLeft + radiusRight;
            return (distCentersSq <= (sumRadius * sumRadius));
        }
        // Two rectangles
        else if ((leftShapeRect != null) && (rightShapeRect != null)) {
            return leftShapeRect.intersect(rightShapeRect);
        }
        // Circle and rectangle
        else {
            // Calculate center and radius
            RectF circle = (leftShapeCircle == null) ? rightShapeCircle : leftShapeCircle;
            double centerX = leftShapeCircle.left + (circle.right / 2);
            double centerY = circle.top + (circle.bottom / 2);
            double radius = (circle.right - circle.left) / 2;

            // Find the closest point to the circle within the rectangle
            Rect rect = (leftShapeRect == null) ? rightShapeRect : leftShapeRect;

            // Limit closestX to be in [rect.left, rect.right]
            double closestX = ((centerX >= rect.left) && (centerX <= rect.right)) ? centerX : (centerX < rect.left ? rect.left : rect.right);

            // Limit closestY to be in [rect.top, rect.bottom]
            double closestY = ((centerY >= rect.top) && (centerY <= rect.bottom)) ? centerX : (centerY < rect.top ? rect.top : rect.bottom);

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

        /*
        private Rect leftShapeRect;
        private RectF leftShapeCircle;
        private Rect rightShapeRect;
        private RectF rightShapeCircle;

        // Radius (as percentage of the screen height)
        private double leftRadius;
        // X Axis side length (as percentage of the screen height)
        private double sideX;
        // Y Axis side length (as percentage of the screen height)
        private double sideY;
        // Time in milliseconds it takes the left shape to go extreme to extreme horizontally
        private int leftDelay;
        // Time in milliseconds it takes the right shape to go extreme to extreme horizontally
        private int rightDelay;

        // Get parameters
        radius = randomDouble(R.fraction.level_collision_min_radius, R.fraction.level_collision_max_radius);
        sideX = randomDouble(R.fraction.level_collision_min_side, R.fraction.level_collision_max_side);
        sideY = randomDouble(R.fraction.level_collision_min_side, R.fraction.level_collision_max_side);
        leftDelay = randomInt(R.integer.level_collision_min_delay, R.integer.level_collision_max_delay);*/

        // Create shapes
        leftShapeCircle = new RectF(10, 10, 10, 10);
        // TODO

        // Create middle block
        double blockWidth = width * randomDouble(R.fraction.level_collision_min_block_width, R.fraction.level_collision_max_block_width);
        middleBlock = new Rect((int) ((width - blockWidth) / 2), 0, (int) ((width + blockWidth) / 2), height);

        // Force redraw
        rootView.invalidate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel animation
        // TODO
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

                // Draw shapes
                // TODO: Animation?
                if (leftShapeRect != null) {
                    //canvas.drawRect(leftShapeRect, leftShapePaint);
                } else {
                    //canvas.drawOval(leftShapeCircle, leftShapePaint);
                }
                if (rightShapeRect != null) {
                    //canvas.drawRect(rightShapeRect, rightShapePaint);
                } else {
                    //canvas.drawOval(rightShapeCircle, rightShapePaint);
                }

                // Draw middle block if necessary
                if (middleBlock != null) {
                    canvas.drawRect(middleBlock, middleBlockPaint);
                }
            }
        }
    }
}
