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
public class LevelHole extends Level {
    // Result
    private boolean result;
    // Shapes
    private RectF leftCircle;
    private RectF rightCircle;
    private RectF hole;
    private RectF middleBlock;
    // Screen size
    private float halfWidth;
    private float halfHeight;
    // Timer handler
    private Handler handler;
    // Update function (to resize the circles)
    private Runnable updateShapes;
    // Colors
    private Paint circlePaint;
    private Paint holePaint;
    private Paint middleBlockPaint;
    private int backgroundColor;
    // View
    private LevelHoleView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set initial state
        result = false;
        leftCircle = null;
        rightCircle = null;

        // Set handler
        handler = new Handler();

        // Set colors
        circlePaint = new Paint();
        circlePaint.setColor(getRandomColor());
        middleBlockPaint = new Paint();
        middleBlockPaint.setColor(ContextCompat.getColor(getActivity(), R.color.neutral_light));
        backgroundColor = ContextCompat.getColor(getActivity(), R.color.neutral_dark);
        holePaint = new Paint();
        holePaint.setColor(backgroundColor);

        // Create view
        rootView = new LevelHoleView(getActivity());

        return rootView;
    }

    @Override
    public boolean onPlayerTap() {
        // Stop animation
        handler.removeCallbacksAndMessages(null);

        // Regroup shapes in the middle
        leftCircle.offsetTo(halfWidth - (leftCircle.width() / 2.0f), halfHeight - (leftCircle.height() / 2.0f));
        rightCircle.offsetTo(halfWidth - (rightCircle.width() / 2.0f), halfHeight - (rightCircle.height() / 2.0f));

        // Check if the circle is smaller than the hole
        result = (hole.width() >= leftCircle.width());

        // Paint the hole as a circle if the circle is bigger than the hole
        if (!result) {
            holePaint = circlePaint;
        }

        // Redraw
        rootView.invalidate();

        // Check if the circle is smaller than the hole
        return result;
    }

    private void initializeShapes() {
        // Screen size
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();
        halfWidth = width / 2.0f;
        halfHeight = height / 2.0f;

        // Set hole dimensions
        float holeRadius = height * randomFloat(R.fraction.level_hole_min_hole_radius, R.fraction.level_hole_max_hole_radius);
        float middleBlockHalfWidth = holeRadius * getResources().getFraction(R.fraction.level_hole_block_width, 1, 1);

        // Create hole and middle block
        hole = new RectF(halfWidth - holeRadius, halfHeight - holeRadius, halfWidth + holeRadius, halfHeight + holeRadius);
        middleBlock = new RectF(halfWidth - middleBlockHalfWidth, 0, halfWidth + middleBlockHalfWidth, height);

        // Set circles diameters
        final float originalCircleDiameter = 2.0f * height * randomFloat(R.fraction.level_hole_min_circle_radius, R.fraction.level_hole_max_circle_radius);
        final float resizedCircleDiameter = 2.0f * holeRadius * (1.0f - getResources().getFraction(R.fraction.level_hole_margin, 1, 1));

        // Create circles
        final float centerLeftCircleX = (halfWidth - middleBlockHalfWidth) / 2.0f;
        final float centerRightCircleX = width - (halfWidth - middleBlockHalfWidth) / 2.0f;
        leftCircle = new RectF(
                centerLeftCircleX - (originalCircleDiameter / 2.0f),
                halfHeight - (originalCircleDiameter / 2.0f),
                centerLeftCircleX + (originalCircleDiameter / 2.0f),
                halfHeight + (originalCircleDiameter / 2.0f));
        rightCircle = new RectF(
                centerRightCircleX - (originalCircleDiameter / 2.0f),
                halfHeight - (originalCircleDiameter / 2.0f),
                centerRightCircleX + (originalCircleDiameter / 2.0f),
                halfHeight + (originalCircleDiameter / 2.0f));

        // Set the movement
        final int delay = 1000 / getResources().getInteger(R.integer.level_hole_frames_per_second);
        final long resizeTime = randomInt(R.integer.level_hole_min_resize_time, R.integer.level_hole_max_resize_time);
        final long startTime = System.currentTimeMillis();
        updateShapes = () -> {
            // Time since we started the animation, modulo two times resizeTime
            // First we scale down then up
            long totalResizeTime = 2 * resizeTime;
            long elapsedTime = (System.currentTimeMillis() - startTime) % totalResizeTime;

            // Calculate offset in percentage (from 0% to 100%)
            float offset = ((elapsedTime < resizeTime) ? elapsedTime : (totalResizeTime - elapsedTime)) / (float) resizeTime;
            float circleDiameter = (1.0f - offset) * originalCircleDiameter + offset * resizedCircleDiameter;

            // Resize and center shapes
            leftCircle.set(
                    centerLeftCircleX - (circleDiameter / 2.0f),
                    halfHeight - (circleDiameter / 2.0f),
                    centerLeftCircleX + (circleDiameter / 2.0f),
                    halfHeight + (circleDiameter / 2.0f));
            rightCircle.set(
                    centerRightCircleX - (circleDiameter / 2.0f),
                    halfHeight - (circleDiameter / 2.0f),
                    centerRightCircleX + (circleDiameter / 2.0f),
                    halfHeight + (circleDiameter / 2.0f));

            // Redraw
            rootView.invalidate();

            // Update again after the delay
            handler.postDelayed(updateShapes, delay);
        };

        // Set timer to call the movement function
        handler.postDelayed(updateShapes, delay);

        // Redraw
        rootView.invalidate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel animation
        handler.removeCallbacksAndMessages(null);
    }

    private class LevelHoleView extends View {
        public LevelHoleView(Context c) {
            super(c);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // Uninitialized
            if ((leftCircle == null) || (rightCircle == null)) {
                initializeShapes();
            }
            // Playing
            else {
                // Set background color
                canvas.drawColor(backgroundColor);

                // Draw shapes
                canvas.drawOval(rightCircle, circlePaint);
                canvas.drawOval(leftCircle, circlePaint);
                canvas.drawRect(middleBlock, middleBlockPaint);
                canvas.drawOval(hole, holePaint);

                // Repaint circle in case we are in result phase and the circle is smaller than the hole
                if (result) {
                    canvas.drawOval(rightCircle, circlePaint);
                }
            }
        }
    }
}
