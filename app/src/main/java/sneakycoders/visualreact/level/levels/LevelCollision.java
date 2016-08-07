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
public class LevelCollision extends Level {
    // Shapes
    private RectF firstShape;
    private RectF secondShape;
    // Shape Types
    private ShapeType firstShapeType;
    private ShapeType secondShapeType;
    // Middle block
    private RectF middleBlock;
    // Timer handler
    private Handler handler;
    // Update function (to move the shapes)
    private Runnable updateShapes;
    // Colors
    private Paint firstShapePaint;
    private Paint secondShapePaint;
    private Paint middleBlockPaint;
    private int backgroundColor;
    // View
    private LevelCollisionView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set initial state
        handler = new Handler();
        firstShape = null;
        secondShape = null;

        // Set colors
        Integer[] shapeColors = getRandomColors(2);
        firstShapePaint = new Paint();
        firstShapePaint.setColor(shapeColors[0]);
        secondShapePaint = new Paint();
        secondShapePaint.setColor(shapeColors[1]);
        middleBlockPaint = new Paint();
        middleBlockPaint.setColor(ContextCompat.getColor(getActivity(), R.color.neutral_light));
        backgroundColor = ContextCompat.getColor(getActivity(), R.color.neutral_dark);

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
        return shapesCollide();
    }

    private boolean shapesCollide() {
        // Two circles
        if ((firstShapeType == ShapeType.Circle) && (secondShapeType == ShapeType.Circle)) {
            // Calculate centers and radius
            float distCentersX = firstShape.centerX() - secondShape.centerX();
            float distCentersY = firstShape.centerY() - secondShape.centerY();
            float distCentersSq = distCentersX * distCentersX + distCentersY * distCentersY;
            float radiusLeft = firstShape.width() / 2.0f;
            float radiusRight = secondShape.width() / 2.0f;

            // Collide if sum of radius is less than the distance between centers
            float sumRadius = radiusLeft + radiusRight;
            return (distCentersSq <= (sumRadius * sumRadius));
        }
        // Two rectangles
        else if ((firstShapeType == ShapeType.Rectangle) && (secondShapeType == ShapeType.Rectangle)) {
            return RectF.intersects(firstShape, secondShape);
        }
        // Circle and rectangle
        else {
            // Calculate center and radius
            boolean firstIsCircle = (firstShapeType == ShapeType.Circle);
            RectF circle = firstIsCircle ? firstShape : secondShape;
            float centerX = circle.centerX();
            float centerY = circle.centerY();
            float radius = circle.width() / 2.0f;

            // Select rectangle
            RectF rect = firstIsCircle ? secondShape : firstShape;

            // Find the closest point to the circle within the rectangle
            // Closest to centerX in [rect.left, rect.right]
            float closestX = ((centerX >= rect.left) && (centerX <= rect.right)) ? centerX : (centerX < rect.left ? rect.left : rect.right);

            // Closest to centerY in [rect.top, rect.bottom]
            float closestY = ((centerY >= rect.top) && (centerY <= rect.bottom)) ? centerY : (centerY < rect.top ? rect.top : rect.bottom);

            // Calculate the distance between the circle's center and this closest point
            float distX = centerX - closestX;
            float distY = centerY - closestY;
            float distSq = (distX * distX) + (distY * distY);

            // Collide if the distance is less than the circle's radius
            return (distSq <= (radius * radius));
        }
    }

    private void initializeShapes() {
        // Screen size
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();

        // Margin from extremes
        float margin = width * getResources().getFraction(R.fraction.level_collision_margin_extremes, 1, 1);

        // Dimension variables (reused for both shapes)
        float shapeWidth;
        float shapeHeight;
        float top;

        // Shape local variables (before choosing which one is rendered first)
        final RectF leftShape;
        final RectF rightShape;

        // Choose shape combination
        ShapeType leftShapeType = randomBoolean() ? ShapeType.Circle : ShapeType.Rectangle;
        ShapeType rightShapeType = randomBoolean() ? ShapeType.Circle : ShapeType.Rectangle;

        // Choose dimensions of the left shape
        if (leftShapeType == ShapeType.Circle) {
            // Circle
            float diameter = 2.0f * height * randomFloat(R.fraction.level_collision_min_radius, R.fraction.level_collision_max_radius);
            shapeWidth = diameter;
            shapeHeight = diameter;
            top = (height - diameter) / 2.0f;
        } else {
            // Rectangle
            shapeWidth = height * randomFloat(R.fraction.level_collision_min_side, R.fraction.level_collision_max_side);
            shapeHeight = height * randomFloat(R.fraction.level_collision_min_side, R.fraction.level_collision_max_side);
            top = (height - shapeHeight) / 2.0f;
        }

        // Save the starting point and distance the left shape can travel (in the X axis)
        final float leftShapeStart = margin;
        final float leftTotalDistance = width - (2.0f * margin) - shapeWidth;

        // Create the left shape
        leftShape = new RectF(margin, top, margin + shapeWidth, top + shapeHeight);

        // Choose dimensions of the right shape
        if (rightShapeType == ShapeType.Circle) {
            // Circle
            float diameter = 2.0f * height * randomFloat(R.fraction.level_collision_min_radius, R.fraction.level_collision_max_radius);
            shapeWidth = diameter;
            shapeHeight = diameter;
            top = (height - diameter) / 2.0f;
        } else {
            // Rectangle
            shapeWidth = height * randomFloat(R.fraction.level_collision_min_side, R.fraction.level_collision_max_side);
            shapeHeight = height * randomFloat(R.fraction.level_collision_min_side, R.fraction.level_collision_max_side);
            top = (height - shapeHeight) / 2.0f;
        }

        // Save the starting point and distance the left shape can travel (in the X axis)
        final float rightShapeStart = width - margin - shapeWidth;
        final float rightTotalDistance = width - (2.0f * margin) - shapeWidth;

        // Create the right shape
        rightShape = new RectF(rightShapeStart, top, width - margin, top + shapeHeight);

        // Small variations on height, while ensuring collision
        float avgHeight = (leftShape.height() + rightShape.height()) / 2.0f;
        float maxDiffHeight = avgHeight * (1.0f - getResources().getFraction(R.fraction.level_collision_min_shape_collision_height, 1, 1));

        // Calculate variation, with random Y axis direction
        float variationHeight = randomInInterval(-maxDiffHeight, maxDiffHeight) / 2.0f;
        leftShape.offset(0, variationHeight);
        rightShape.offset(0, -variationHeight);

        // Set the movement
        final int delay = 1000 / getResources().getInteger(R.integer.level_collision_frames_per_second);
        final long moveTime = randomInt(R.integer.level_collision_min_move_time, R.integer.level_collision_max_move_time);
        final long startTime = System.currentTimeMillis();
        updateShapes = new Runnable() {
            @Override
            public void run() {
                // Time since we started the animation, modulo two times moveTime
                // First we go straight, then backwards
                long roundTripTime = 2 * moveTime;
                long elapsedTime = (System.currentTimeMillis() - startTime) % roundTripTime;

                // Calculate offset in percentage (from 0% to 100%)
                float offset = ((elapsedTime < moveTime) ? elapsedTime : (roundTripTime - elapsedTime)) / (float) moveTime;
                float leftNewTop = leftShapeStart + offset * leftTotalDistance;
                leftShape.offsetTo(leftNewTop, leftShape.top);
                float rightNewTop = rightShapeStart - offset * rightTotalDistance;
                rightShape.offsetTo(rightNewTop, rightShape.top);

                // Update view
                rootView.invalidate();

                // Update again after the delay
                handler.postDelayed(updateShapes, delay);
            }
        };

        // Choose which one is drawn first so that the small shape is always visible
        boolean drawLeftFirst = (Math.min(leftShape.width(), leftShape.height()) >= Math.min(rightShape.width(), rightShape.height()));
        firstShape = drawLeftFirst ? leftShape : rightShape;
        secondShape = drawLeftFirst ? rightShape : leftShape;
        firstShapeType = drawLeftFirst ? leftShapeType : rightShapeType;
        secondShapeType = drawLeftFirst ? rightShapeType : leftShapeType;

        // Set timer to call the movement function
        handler.postDelayed(updateShapes, delay);

        // Create middle block
        float blockWidth = width * randomFloat(R.fraction.level_collision_min_block_width, R.fraction.level_collision_max_block_width);
        middleBlock = new RectF((width - blockWidth) / 2.0f, 0, (width + blockWidth) / 2.0f, height);

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

    public class LevelCollisionView extends View {
        public LevelCollisionView(Context c) {
            super(c);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // Uninitialized
            if ((firstShape == null) || (secondShape == null)) {
                initializeShapes();
            }
            // Playing
            else {
                // Set background color
                canvas.drawColor(backgroundColor);

                // Draw shapes in order
                if (firstShapeType == ShapeType.Circle) {
                    canvas.drawOval(firstShape, firstShapePaint);
                } else {
                    canvas.drawRect(firstShape, firstShapePaint);
                }
                if (secondShapeType == ShapeType.Circle) {
                    canvas.drawOval(secondShape, secondShapePaint);
                } else {
                    canvas.drawRect(secondShape, secondShapePaint);
                }

                // Draw middle block if necessary
                if (middleBlock != null) {
                    canvas.drawRect(middleBlock, middleBlockPaint);
                }
            }
        }
    }
}
