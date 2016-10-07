package sneakycoders.visualreact.level.levels;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import sneakycoders.visualreact.R;
import sneakycoders.visualreact.level.Level;

// Dynamically instantiated
@SuppressWarnings("unused")
public class LevelPair extends Level {
    // Number of shapes on the screen initially for each row (top and bottom)
    private int shapesPerRow;
    // Flag to add random shapes instead of unique ones
    private boolean uniqueShapes;
    // Sequences of shapes
    private List<BaseShape> topShapes;
    private List<BaseShape> bottomShapes;
    private List<BaseShape> unusedShapes;
    // Paints and colors
    private int backgroundColor;
    // Handler
    private Handler handler;
    // Update functions
    private Runnable updateShapes;
    // View
    private LevelPairView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Add distinctive shapes
        uniqueShapes = true;

        // Set the handler
        handler = new Handler();

        // Number of shapes on the screen
        shapesPerRow = getResources().getInteger(R.integer.level_pair_shapes_per_row);

        // Sequences of shapes
        topShapes = new ArrayList<>();
        bottomShapes = new ArrayList<>();
        unusedShapes = new ArrayList<>();

        // Paints and colors
        backgroundColor = ContextCompat.getColor(getActivity(), R.color.neutral_dark);

        // Create view
        rootView = new LevelPairView(getActivity());

        return rootView;
    }

    @Override
    public boolean onPlayerTap() {
        // Cancel callback
        handler.removeCallbacksAndMessages(null);

        // Get the width of the screen
        int width = rootView.getMeasuredWidth();

        // Create a map to store the number of different shapes
        Map<ShapeType, Integer> shapeTypes = new EnumMap<>(ShapeType.class);

        // Concatenate top and bottom list
        List<BaseShape> shapes = new ArrayList<>(topShapes);
        shapes.addAll(bottomShapes);

        // Success if there are more than one shape of the same type
        boolean result = false;

        // Count the number of different shapes
        for (BaseShape shape : shapes) {
            if (!shape.isOutOfScreen(width)) {
                ShapeType shapeType = shape.getShapeType();
                if (shapeTypes.containsKey(shapeType)) {
                    shapeTypes.put(shapeType, shapeTypes.get(shapeType) + 1);
                    result = true;
                } else {
                    shapeTypes.put(shapeType, 1);
                }
            }
        }

        // Change the color of the repeated shapes
        for (BaseShape shape : shapes) {
            if ((!shape.isOutOfScreen(width)) && (shapeTypes.get(shape.getShapeType()) > 1)) {
                shape.setColor(successColor);
            }
        }

        // Redraw
        if (result) {
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

    private void initializeShapes() {
        // Screen size
        final int width = rootView.getMeasuredWidth();
        final int height = rootView.getMeasuredHeight();

        // Distance between upper and lower rectangles
        // Margin (top, middle, bottom)
        float margin = height * getResources().getFraction(R.fraction.level_pair_vertical_margin, 1, 1);

        // Rectangle to the right of screen where shapes are initialized
        final float cellWidth = width / ((float) shapesPerRow);
        final float cellHeight = (height - 3.0f * margin) / 2.0f;
        final float padding = Math.min(cellWidth, cellHeight) * getResources().getFraction(R.fraction.level_pair_inside_padding, 1, 1);

        // Starting points for the new shapes
        final float topStartX = -cellWidth;
        final float topStartY = margin;
        final float bottomStartX = (float) width;
        final float bottomStartY = height - margin - cellHeight;

        // Store the initial shapes
        // Note: there must at least (2 * (shapesPerRow + 1)) unique shapes in allShapes
        final ShapeType[] shapeTypes = ShapeType.values();
        final int numUniqueShapes = shapeTypes.length;
        Integer[] colors = getRandomDistinctiveColors(numUniqueShapes);
        for (int i = 0; i < numUniqueShapes; i++) {
            unusedShapes.add(createShape(shapeTypes[i], cellWidth, cellHeight, padding, colors[i]));
        }

        // Randomly choose shapes for top and bottom
        Collections.shuffle(unusedShapes);
        for (int i = 0; i < shapesPerRow; i++) {
            // Select random shapes and move them to the right position
            BaseShape topShape = unusedShapes.remove(0);
            topShape.offsetTo(i * cellWidth, topStartY);
            BaseShape bottomShape = unusedShapes.remove(0);
            bottomShape.offsetTo(i * cellWidth, bottomStartY);

            // Put shapes in the lists
            topShapes.add(topShape);
            bottomShapes.add(bottomShape);
        }

        // Movement parameters
        final int delay = 1000 / getResources().getInteger(R.integer.level_pair_frames_per_second);
        final int travelTime = randomInt(R.integer.level_pair_minimum_travel_time, R.integer.level_pair_maximum_travel_time);
        final float dx = width * delay / (float) travelTime;

        // Set movement
        updateShapes = () -> {
            // Update the position of the top shapes
            for (BaseShape shape : topShapes) {
                shape.offset(dx, 0);
            }

            // Update the position of the bottom shapes
            for (BaseShape shape : bottomShapes) {
                shape.offset(-dx, 0);
            }

            // Remove top shape if needed
            if (topShapes.get(topShapes.size() - 1).isOutOfScreen(width)) {
                // Remove shape
                BaseShape removed = topShapes.remove(topShapes.size() - 1);
                if (uniqueShapes) {
                    unusedShapes.add(removed);
                }
            }

            // Add a new top shape if needed
            if (topShapes.get(0).left() >= 0) {
                BaseShape shape = uniqueShapes ?
                        unusedShapes.remove(randomInInterval(0, unusedShapes.size() - 1))
                        : createShape(shapeTypes[randomInInterval(0, numUniqueShapes - 1)], cellWidth, cellHeight, padding, getRandomDistinctiveColor());

                // Move the shape to its starting position
                shape.offsetTo(topStartX, topStartY);

                // Add shape
                topShapes.add(0, shape);
            }

            // Remove bottom shape if needed
            if (bottomShapes.get(0).isOutOfScreen(width)) {
                // Remove shape
                BaseShape removed = bottomShapes.remove(0);
                if (uniqueShapes) {
                    unusedShapes.add(removed);
                }
            }

            // Add a new bottom shape if needed
            if (bottomShapes.get(bottomShapes.size() - 1).right() <= width) {
                BaseShape shape = uniqueShapes ?
                        unusedShapes.remove(randomInInterval(0, unusedShapes.size() - 1))
                        : createShape(shapeTypes[randomInInterval(0, numUniqueShapes - 1)], cellWidth, cellHeight, padding, getRandomDistinctiveColor());

                // Move the shape to its starting position
                shape.offsetTo(bottomStartX, bottomStartY);

                // Add shape
                bottomShapes.add(shape);
            }

            // Redraw
            rootView.invalidate();

            // Update again after the delay
            handler.postDelayed(updateShapes, delay);
        };

        // Set timer to update the shapes
        handler.postDelayed(updateShapes, delay);

        // Switch to random shapes eventually
        final int timeBeforeRandomShapes = randomInt(R.integer.level_pair_minimum_time_before_random_shapes, R.integer.level_pair_maximum_time_before_random_shapes);
        handler.postDelayed(() -> uniqueShapes = false, timeBeforeRandomShapes);

        // Redraw
        rootView.invalidate();
    }

    // Shape types
    private enum ShapeType {
        Rectangle, Square, Circle, Oval, CrossTwo, CrossThree, CrossFour, CrossFive, CrossSix, EquilateralTriangle, RightTriangle, Arrow, Moon
    }

    public BaseShape createShape(ShapeType shapeType, float cellWidth, float cellHeight, float padding, int color) {
        if (shapeType == ShapeType.Rectangle) {
            return new Rectangle(cellWidth, cellHeight, padding, color);
        } else if (shapeType == ShapeType.Square) {
            return new Square(cellWidth, cellHeight, padding, color);
        } else if (shapeType == ShapeType.Circle) {
            return new Circle(cellWidth, cellHeight, padding, color);
        } else if (shapeType == ShapeType.Oval) {
            return new Oval(cellWidth, cellHeight, padding, color);
        } else if (shapeType == ShapeType.CrossTwo) {
            return new Cross(cellWidth, cellHeight, padding, 2, color);
        } else if (shapeType == ShapeType.CrossThree) {
            return new Cross(cellWidth, cellHeight, padding, 3, color);
        } else if (shapeType == ShapeType.CrossFour) {
            return new Cross(cellWidth, cellHeight, padding, 4, color);
        } else if (shapeType == ShapeType.CrossFive) {
            return new Cross(cellWidth, cellHeight, padding, 5, color);
        } else if (shapeType == ShapeType.CrossSix) {
            return new Cross(cellWidth, cellHeight, padding, 6, color);
        } else if (shapeType == ShapeType.EquilateralTriangle) {
            return new EquilateralTriangle(cellWidth, cellHeight, padding, color);
        } else if (shapeType == ShapeType.RightTriangle) {
            return new RightTriangle(cellWidth, cellHeight, padding, color);
        } else if (shapeType == ShapeType.Arrow) {
            return new Arrow(cellWidth, cellHeight, padding, color);
        } else {
            return new Moon(cellWidth, cellHeight, padding, color);
        }
    }

    private abstract class BaseShape {
        // Shape's paint
        protected Paint paint;
        // Shape's outside box - the real shape is centered its box
        protected RectF box;

        protected BaseShape(float cellWidth, float cellHeight, int color) {
            this.box = new RectF(0, 0, cellWidth, cellHeight);
            paint = new Paint();
            paint.setColor(color);
        }

        protected abstract ShapeType getShapeType();

        public abstract void draw(Canvas canvas);

        public void setColor(int color) {
            paint.setColor(color);
        }

        public void offset(float dx, float dy) {
            box.offset(dx, dy);
        }

        public void offsetTo(float x, float y) {
            float dx = x - box.left;
            float dy = y - box.top;
            this.offset(dx, dy);
        }

        public float left() {
            return box.left;
        }

        public float right() {
            return box.right;
        }

        public abstract boolean isOutOfScreen(int width);
    }

    private class Rectangle extends BaseShape {
        // Rectangle
        protected RectF rectangle;

        protected Rectangle(float cellWidth, float cellHeight, float width, float height, int color) {
            super(cellWidth, cellHeight, color);
            rectangle = new RectF(
                    (cellWidth - width) / 2.0f, (cellHeight - height) / 2.0f,
                    (cellWidth + width) / 2.0f, (cellHeight + height) / 2.0f);
        }

        public Rectangle(float cellWidth, float cellHeight, float padding, int color) {
            this(
                    cellWidth, cellHeight,
                    cellWidth * getResources().getFraction(R.fraction.level_pair_rectangle_width, 1, 1) - 2.0f * padding, cellHeight - 2.0f * padding,
                    color);
        }

        @Override
        protected ShapeType getShapeType() {
            return ShapeType.Rectangle;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawRect(rectangle, paint);
        }

        @Override
        public void offset(float dx, float dy) {
            super.offset(dx, dy);
            rectangle.offset(dx, dy);
        }

        @Override
        public boolean isOutOfScreen(int width) {
            return ((rectangle.right < 0) || (rectangle.left >= width));
        }
    }

    private class Square extends Rectangle {
        public Square(float cellWidth, float cellHeight, float padding, int color) {
            super(
                    cellWidth, cellHeight, Math.min(cellWidth, cellHeight) - 2.0f * padding,
                    Math.min(cellWidth, cellHeight) - 2.0f * padding,
                    color);
        }

        @Override
        protected ShapeType getShapeType() {
            return ShapeType.Square;
        }
    }

    private class Circle extends Square {
        public Circle(float cellWidth, float cellHeight, float padding, int circleColor) {
            super(cellWidth, cellHeight, padding, circleColor);
        }

        @Override
        protected ShapeType getShapeType() {
            return ShapeType.Circle;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawOval(rectangle, paint);
        }
    }

    private class Oval extends Rectangle {
        public Oval(float cellWidth, float cellHeight, float padding, int ovalColor) {
            super(cellWidth, cellHeight, padding, ovalColor);
        }

        @Override
        protected ShapeType getShapeType() {
            return ShapeType.Oval;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawOval(rectangle, paint);
        }
    }

    private class Cross extends BaseShape {
        // Line that forms the cross
        private RectF line;
        // Number of lines
        private int numLines;
        // Difference of angle between lines
        private float angleMultiplier;

        public Cross(float cellWidth, float cellHeight, float padding, int numLines, int color) {
            super(cellWidth, cellHeight, color);

            // Draw parameters
            this.numLines = numLines;
            angleMultiplier = 180.0f / numLines;

            // Create the line
            float size = Math.min(cellWidth, cellHeight) * getResources().getFraction(R.fraction.level_pair_cross_size, 1, 1);
            line = new RectF(padding, (cellHeight - size) / 2.0f, cellWidth - size, (cellHeight + size) / 2.0f);
        }

        @Override
        protected ShapeType getShapeType() {
            if (numLines == 2) {
                return ShapeType.CrossTwo;
            } else if (numLines == 3) {
                return ShapeType.CrossThree;
            } else if (numLines == 4) {
                return ShapeType.CrossFour;
            } else if (numLines == 5) {
                return ShapeType.CrossFive;
            } else {
                return ShapeType.CrossSix;
            }
        }

        @Override
        public void draw(Canvas canvas) {
            // Draw the lines
            for (int i = 0; i < numLines; i++) {
                canvas.save();
                canvas.rotate(angleMultiplier * i, line.centerX(), line.centerY());
                canvas.drawRect(line, paint);
                canvas.restore();
            }
        }

        @Override
        public void offset(float dx, float dy) {
            super.offset(dx, dy);
            line.offset(dx, dy);
        }

        @Override
        public boolean isOutOfScreen(int width) {
            return ((line.right < 0) || (line.left >= width));
        }
    }

    private abstract class Triangle extends BaseShape {
        // Path that forms the triangle
        protected Path path;
        // Vertices of the triangle
        protected float leftX;
        protected float leftY;
        protected float rightX;
        protected float rightY;
        protected float topX;
        protected float topY;

        protected Triangle(float cellWidth, float cellHeight, int color) {
            super(cellWidth, cellHeight, color);
        }

        protected void setPath() {
            path = new Path();
            path.moveTo(leftX, leftY);
            path.lineTo(topX, topY);
            path.lineTo(rightX, rightY);
            path.lineTo(leftX, leftY);
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public void offset(float dx, float dy) {
            super.offset(dx, dy);
            path.offset(dx, dy);
            leftX += dx;
            leftY += dy;
            topX += dx;
            topY += dy;
            rightX += dx;
            rightY += dy;
        }

        @Override
        public boolean isOutOfScreen(int width) {
            return ((rightX <= 0) || (leftX >= width));
        }
    }

    private class EquilateralTriangle extends Triangle {
        public EquilateralTriangle(float cellWidth, float cellHeight, float padding, int color) {
            super(cellWidth, cellHeight, color);

            // Vertices
            float size = Math.min(cellWidth, cellHeight) - 2.0f * padding;
            leftX = (cellWidth - size) / 2.0f;
            rightX = (cellWidth + size) / 2.0f;
            leftY = (cellHeight / 2.0f) + (float) (size / (2.0f * Math.sqrt(3.0)));
            rightY = leftY;
            topX = cellWidth / 2.0f;
            topY = (cellHeight / 2.0f) - (float) (size / Math.sqrt(3.0));

            // Center
            float height = leftY - topY;
            float diff = (cellHeight - height) / 2.0f - topY;
            leftY += diff;
            topY += diff;
            rightY += diff;

            // Path
            setPath();
        }

        @Override
        protected ShapeType getShapeType() {
            return ShapeType.EquilateralTriangle;
        }
    }

    private class RightTriangle extends Triangle {
        public RightTriangle(float cellWidth, float cellHeight, float padding, int color) {
            super(cellWidth, cellHeight, color);

            // Vertices of the right triangle
            float width = cellWidth - 2.0f * padding;
            float height = cellHeight - 2.0f * padding;
            leftX = (cellWidth - width) / 2.0f;
            topX = leftX;
            rightX = (cellWidth + width) / 2.0f;
            leftY = (cellHeight + height) / 2.0f;
            rightY = leftY;
            topY = (cellHeight - height) / 2.0f;

            // Path
            setPath();
        }

        @Override
        protected ShapeType getShapeType() {
            return ShapeType.RightTriangle;
        }
    }

    private class Arrow extends BaseShape {
        // Head of the arrow
        private EquilateralTriangle head;
        // Tail of the arrow
        private RectF tail;

        public Arrow(float cellWidth, float cellHeight, float padding, int color) {
            super(cellWidth, cellHeight, color);

            // Head
            float height = cellHeight - 2.0f * padding;
            float headHeight = height * getResources().getFraction(R.fraction.level_pair_arrow_head_height, 1, 1);
            head = new EquilateralTriangle(cellWidth - 2.0f * padding, headHeight, 0, color);
            head.offset(padding, padding);

            // Tail
            float tailWidth = (cellWidth - 2.0f * padding) * getResources().getFraction(R.fraction.level_pair_arrow_tail_width, 1, 1);
            tail = new RectF((cellWidth - tailWidth) / 2.0f, headHeight, (cellWidth + tailWidth) / 2.0f, padding + height);
        }

        @Override
        protected ShapeType getShapeType() {
            return ShapeType.Arrow;
        }

        @Override
        public void setColor(int color) {
            super.setColor(color);
            head.setColor(color);
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawRect(tail, paint);
            head.draw(canvas);
        }

        @Override
        public void offset(float dx, float dy) {
            super.offset(dx, dy);
            head.offset(dx, dy);
            tail.offset(dx, dy);
        }

        @Override
        public boolean isOutOfScreen(int width) {
            return head.isOutOfScreen(width);
        }
    }

    private class Moon extends BaseShape {
        // Outer arc rectangle
        private RectF outerRect;
        // Moon's path
        private Path path;

        public Moon(float cellWidth, float cellHeight, float padding, int color) {
            super(cellWidth, cellHeight, color);

            // Rectangles
            float width = cellWidth - 2.0f * padding;
            float innerWidth = cellWidth * getResources().getFraction(R.fraction.level_pair_moon_inner_width, 1, 1);
            RectF innerRect = new RectF((cellWidth - innerWidth) / 2.0f, padding, (cellWidth + innerWidth) / 2.0f, cellHeight - padding);
            outerRect = new RectF((cellWidth - width) / 2.0f, padding, (cellWidth + width) / 2.0f, cellHeight - padding);

            // Center
            float diff = -innerWidth / 2.0f;
            innerRect.offset(diff, 0);
            outerRect.offset(diff, 0);

            // Path
            path = new Path();
            path.addArc(innerRect, 270, 180);
            path.arcTo(outerRect, 90, -180);
        }

        @Override
        protected ShapeType getShapeType() {
            return ShapeType.Moon;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public void offset(float dx, float dy) {
            super.offset(dx, dy);
            path.offset(dx, dy);
            outerRect.offset(dx, dy);
        }

        @Override
        public boolean isOutOfScreen(int width) {
            return ((outerRect.right <= 0) || (outerRect.left > width));
        }
    }

    private class LevelPairView extends View {
        public LevelPairView(Context c) {
            super(c);
        }

        @Override
        public void onDraw(Canvas canvas) {
            // Uninitialized
            if (topShapes.size() == 0) {
                initializeShapes();
            }
            // Playing
            else {
                // Set background color
                canvas.drawColor(backgroundColor);

                // Draw shapes
                // Top
                for (BaseShape topShape : topShapes) {
                    topShape.draw(canvas);
                }

                // Bottom
                for (BaseShape bottomShape : bottomShapes) {
                    bottomShape.draw(canvas);
                }
            }
        }
    }
}
