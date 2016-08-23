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
public class LevelPassage extends Level {
    // Shapes
    private RectF[] passages;
    private RectF[] lines;
    private RectF verticalPassage;
    // Number of passages
    private int numPassages;
    // Timer handler
    private Handler handler;
    // Update function (to move the passages)
    private Runnable updateShapes;
    // Colors
    private Paint[] linesColors;
    private Paint passageColor;
    private Paint verticalPassageColor;
    private int backgroundColor;
    // View
    private LevelPassageView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set the handler
        handler = new Handler();

        // Number of passages
        numPassages = getResources().getInteger(R.integer.level_passage_num_passages);

        // Create shapes arrays
        lines = new RectF[numPassages];
        passages = new RectF[numPassages];

        // Initialize vertical passage to null, will be filled when a player taps
        verticalPassage = null;

        // Set colors
        Integer[] randomColors = getRandomColors(numPassages);
        linesColors = new Paint[numPassages];
        for (int i = 0; i < numPassages; i++) {
            linesColors[i] = new Paint();
            linesColors[i].setColor(randomColors[i]);
        }
        backgroundColor = ContextCompat.getColor(getActivity(), R.color.neutral_dark);
        passageColor = new Paint();
        passageColor.setColor(backgroundColor);
        verticalPassageColor = new Paint();
        verticalPassageColor.setColor(successLightColor);

        // Create view
        rootView = new LevelPassageView(getActivity());

        return rootView;
    }

    @Override
    public boolean onPlayerTap() {
        // Stop animation
        handler.removeCallbacksAndMessages(null);

        // Try to find a vertical passage
        boolean result;
        float minX = passages[0].left;
        float maxX = passages[0].right;
        for (int i = 1; i < numPassages; i++) {
            minX = Math.max(minX, passages[i].left);
            maxX = Math.min(maxX, passages[i].right);
        }
        minX = (float) Math.ceil(minX);
        maxX = (float) Math.floor(maxX);

        // Check that there exists a valid passage
        if (minX < maxX) {
            result = true;
            verticalPassage = new RectF(minX, 0, maxX, rootView.getMeasuredHeight());
        } else {
            result = false;
        }

        // Redraw
        rootView.invalidate();

        return result;
    }

    private void initializeShapes() {
        // Screen size
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();

        // Lines
        float lineHeight = height * getResources().getFraction(R.fraction.level_passage_line_height, 1, 1);
        float lineSeparation = height / (float) numPassages;
        float linesStartingPoint = (lineSeparation - lineHeight) / 2.0f;
        for (int i = 0; i < numPassages; ++i) {
            float top = linesStartingPoint + (i * lineSeparation);
            lines[i] = new RectF(0, top, width, top + lineHeight);
        }

        // Set the delay before the passage is available
        int delay = randomInt(R.integer.level_passage_min_delay, R.integer.level_passage_max_delay);

        // Set the delay between updates
        final int updateDelay = 1000 / getResources().getInteger(R.integer.level_passage_frames_per_second);

        // Constants for the passages
        float minCommonPoint = height * getResources().getFraction(R.fraction.level_passage_max_passage_width, 1, 1);
        float maxCommonPoint = height - minCommonPoint;
        float commonPoint = randomInInterval(minCommonPoint, maxCommonPoint);
        final float marginLeft = height * getResources().getFraction(R.fraction.level_passage_margin, 1, 1);
        final float marginRight = width - marginLeft;

        // Create passages
        final float[] passagesDistEachUpdate = new float[numPassages];
        for (int i = 0; i < numPassages; ++i) {
            // Initialize passage
            float passageWidth = height * randomFloat(R.fraction.level_passage_min_passage_width, R.fraction.level_passage_max_passage_width);
            float startingPoint = ((i % 2) == 0) ? marginLeft : marginRight - passageWidth;
            passages[i] = new RectF(startingPoint, lines[i].top, startingPoint + passageWidth, lines[i].bottom);

            // Randomize path
            float shortestDistanceBeforeCommonPoint = commonPoint - (startingPoint + (passageWidth / 2.0f));
            int trips = randomInt(R.integer.level_passage_min_trips, R.integer.level_passage_max_trips);
            float direction = randomBoolean() ? 1.0f : -1.0f;
            float distanceBeforeCommonPoint = shortestDistanceBeforeCommonPoint + direction * trips * (marginRight - marginLeft - passageWidth);
            passagesDistEachUpdate[i] = updateDelay * distanceBeforeCommonPoint / delay;
        }

        // Set the movement
        updateShapes = new Runnable() {
            @Override
            public void run() {
                // Move shapes
                for (int i = 0; i < numPassages; ++i) {
                    float dist = passagesDistEachUpdate[i];
                    passages[i].offset(dist, 0);

                    // Turn back if needed
                    if (passages[i].left < marginLeft) {
                        passagesDistEachUpdate[i] = -dist;
                        passages[i].offset(-dist, 0);
                        passages[i].offset(passages[i].left - marginLeft, 0);
                    } else if (passages[i].right > marginRight) {
                        passagesDistEachUpdate[i] = -dist;
                        passages[i].offset(-dist, 0);
                        passages[i].offset(passages[i].right - marginRight, 0);
                    }
                }

                // Redraw
                rootView.invalidate();

                // Update again after the delay
                handler.postDelayed(updateShapes, updateDelay);
            }
        };

        // Set timer to call the movement function
        handler.postDelayed(updateShapes, updateDelay);

        // Redraw
        rootView.invalidate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Cancel animation
        handler.removeCallbacksAndMessages(null);
    }

    public class LevelPassageView extends View {
        public LevelPassageView(Context c) {
            super(c);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // Uninitialized
            if (passages[0] == null) {
                initializeShapes();
            }
            // Playing
            else {
                // Set background color
                canvas.drawColor(backgroundColor);

                // Draw lines and passages
                for (int i = 0; i < numPassages; i++) {
                    canvas.drawRect(lines[i], linesColors[i]);
                    canvas.drawRect(passages[i], passageColor);
                }

                // Draw vertical passage if needed
                if (verticalPassage != null) {
                    canvas.drawRect(verticalPassage, verticalPassageColor);
                }
            }
        }
    }
}
