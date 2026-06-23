package com.example.games_module;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ColoringView extends View {
    static final String TOOL_FILL = "fill";
    static final String TOOL_BRUSH = "brush";
    static final String TOOL_ERASER = "eraser";

    private static final int BACKGROUND = Color.rgb(245, 240, 232);
    private static final int PAPER = Color.WHITE;
    private static final int OUTLINE = Color.rgb(42, 36, 32);

    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint brushPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<Zone> zones = new ArrayList<>();
    private final List<BrushPoint> brushPoints = new ArrayList<>();

    private String tool = TOOL_FILL;
    private int selectedColor = Color.rgb(23, 162, 184);

    public ColoringView(Context context) {
        super(context);
        init();
    }

    public ColoringView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        outlinePaint.setColor(OUTLINE);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(5f);
        outlinePaint.setStrokeJoin(Paint.Join.ROUND);
        outlinePaint.setStrokeCap(Paint.Cap.ROUND);

        fillPaint.setStyle(Paint.Style.FILL);

        brushPaint.setStyle(Paint.Style.FILL);
    }

    void setSelectedColor(int color) {
        selectedColor = color;
    }

    void setTool(String newTool) {
        tool = newTool;
    }

    int getColoredZoneCount() {
        int count = 0;
        for (Zone zone : zones) {
            if (zone.fillColor != PAPER) {
                count++;
            }
        }
        return count;
    }

    int getZoneCount() {
        return zones.size();
    }

    void resetTemplate() {
        for (Zone zone : zones) {
            zone.fillColor = PAPER;
        }
        brushPoints.clear();
        invalidate();
    }

    Bitmap exportBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        buildTemplate(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(BACKGROUND);

        for (Zone zone : zones) {
            fillPaint.setColor(zone.fillColor);
            canvas.drawPath(zone.path, fillPaint);
        }

        for (BrushPoint point : brushPoints) {
            brushPaint.setColor(point.color);
            canvas.drawCircle(point.position.x, point.position.y, point.radius, brushPaint);
        }

        for (Zone zone : zones) {
            canvas.drawPath(zone.path, outlinePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() != MotionEvent.ACTION_MOVE) {
            return true;
        }

        if (TOOL_BRUSH.equals(tool)) {
            brushPoints.add(new BrushPoint(event.getX(), event.getY(), selectedColor, 14f));
            invalidate();
            return true;
        }

        if (TOOL_ERASER.equals(tool) && event.getAction() == MotionEvent.ACTION_MOVE) {
            brushPoints.add(new BrushPoint(event.getX(), event.getY(), PAPER, 24f));
            invalidate();
            return true;
        }

        Zone zone = findZone(event.getX(), event.getY());
        if (zone == null) {
            return true;
        }

        zone.fillColor = TOOL_ERASER.equals(tool) ? PAPER : selectedColor;
        invalidate();
        return true;
    }

    private Zone findZone(float x, float y) {
        for (int i = zones.size() - 1; i >= 0; i--) {
            Zone zone = zones.get(i);
            if (zone.region.contains((int) x, (int) y)) {
                return zone;
            }
        }
        return null;
    }

    private void buildTemplate(int width, int height) {
        zones.clear();
        if (width == 0 || height == 0) {
            return;
        }

        float cx = width / 2f;
        float top = height * 0.08f;
        float headTop = height * 0.22f;
        float headBottom = height * 0.38f;
        float shoulder = height * 0.42f;
        float hem = height * 0.87f;
        Region clip = new Region(0, 0, width, height);

        Path kokoshnik = new Path();
        kokoshnik.moveTo(cx, top);
        kokoshnik.cubicTo(width * 0.28f, height * 0.10f, width * 0.24f, height * 0.26f, width * 0.34f, height * 0.30f);
        kokoshnik.lineTo(width * 0.66f, height * 0.30f);
        kokoshnik.cubicTo(width * 0.76f, height * 0.26f, width * 0.72f, height * 0.10f, cx, top);
        kokoshnik.close();
        zones.add(new Zone(kokoshnik, clip));

        Path face = new Path();
        face.addOval(new RectF(width * 0.36f, headTop, width * 0.64f, headBottom), Path.Direction.CW);
        zones.add(new Zone(face, clip));

        Path leftSleeve = new Path();
        leftSleeve.moveTo(width * 0.32f, shoulder);
        leftSleeve.lineTo(width * 0.17f, height * 0.60f);
        leftSleeve.lineTo(width * 0.27f, height * 0.66f);
        leftSleeve.lineTo(width * 0.42f, height * 0.49f);
        leftSleeve.close();
        zones.add(new Zone(leftSleeve, clip));

        Path rightSleeve = new Path();
        rightSleeve.moveTo(width * 0.68f, shoulder);
        rightSleeve.lineTo(width * 0.83f, height * 0.60f);
        rightSleeve.lineTo(width * 0.73f, height * 0.66f);
        rightSleeve.lineTo(width * 0.58f, height * 0.49f);
        rightSleeve.close();
        zones.add(new Zone(rightSleeve, clip));

        Path sarafan = new Path();
        sarafan.moveTo(width * 0.36f, shoulder);
        sarafan.lineTo(width * 0.64f, shoulder);
        sarafan.lineTo(width * 0.78f, hem);
        sarafan.lineTo(width * 0.22f, hem);
        sarafan.close();
        zones.add(new Zone(sarafan, clip));

        Path apron = new Path();
        apron.moveTo(width * 0.42f, height * 0.54f);
        apron.lineTo(width * 0.58f, height * 0.54f);
        apron.lineTo(width * 0.63f, height * 0.82f);
        apron.lineTo(width * 0.37f, height * 0.82f);
        apron.close();
        zones.add(new Zone(apron, clip));

        Path belt = new Path();
        belt.addRoundRect(new RectF(width * 0.31f, height * 0.49f, width * 0.69f, height * 0.56f), 12f, 12f, Path.Direction.CW);
        zones.add(new Zone(belt, clip));

        Path leftBoot = new Path();
        leftBoot.addRoundRect(new RectF(width * 0.32f, height * 0.88f, width * 0.46f, height * 0.96f), 12f, 12f, Path.Direction.CW);
        zones.add(new Zone(leftBoot, clip));

        Path rightBoot = new Path();
        rightBoot.addRoundRect(new RectF(width * 0.54f, height * 0.88f, width * 0.68f, height * 0.96f), 12f, 12f, Path.Direction.CW);
        zones.add(new Zone(rightBoot, clip));
    }

    private static final class Zone {
        final Path path;
        final Region region = new Region();
        int fillColor = PAPER;

        Zone(Path path, Region clip) {
            this.path = path;
            region.setPath(path, clip);
        }
    }

    private static final class BrushPoint {
        final PointF position;
        final int color;
        final float radius;

        BrushPoint(float x, float y, int color, float radius) {
            this.position = new PointF(x, y);
            this.color = color;
            this.radius = radius;
        }
    }
}
