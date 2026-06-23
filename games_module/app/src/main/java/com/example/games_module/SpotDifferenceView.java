package com.example.games_module;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SpotDifferenceView extends View {
    interface OnDifferenceFoundListener {
        void onDifferenceFound(int foundCount, int totalCount);
    }

    private static final int BACKGROUND = Color.rgb(245, 240, 232);
    private static final int DARK = Color.rgb(42, 36, 32);
    private static final int TURQUOISE = Color.rgb(23, 162, 184);
    private static final int GOLD = Color.rgb(255, 215, 0);
    private static final int RED = Color.rgb(204, 0, 0);
    private static final int BLUE = Color.rgb(30, 58, 138);
    private static final int PAPER = Color.WHITE;

    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint foundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final boolean[] found = new boolean[4];
    private final RectF[] leftZones = new RectF[4];
    private final RectF[] rightZones = new RectF[4];

    private OnDifferenceFoundListener listener;

    public SpotDifferenceView(Context context) {
        super(context);
        init();
    }

    public SpotDifferenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        strokePaint.setColor(DARK);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(4f);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint.setColor(DARK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(32f);

        foundPaint.setColor(TURQUOISE);
        foundPaint.setStyle(Paint.Style.STROKE);
        foundPaint.setStrokeWidth(7f);
    }

    void setOnDifferenceFoundListener(OnDifferenceFoundListener listener) {
        this.listener = listener;
    }

    int getFoundCount() {
        int count = 0;
        for (boolean item : found) {
            if (item) {
                count++;
            }
        }
        return count;
    }

    int getTotalCount() {
        return found.length;
    }

    void reset() {
        for (int i = 0; i < found.length; i++) {
            found[i] = false;
        }
        invalidate();
        if (listener != null) {
            listener.onDifferenceFound(0, found.length);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(BACKGROUND);

        RectF leftPanel = new RectF(18f, 24f, getWidth() / 2f - 10f, getHeight() - 24f);
        RectF rightPanel = new RectF(getWidth() / 2f + 10f, 24f, getWidth() - 18f, getHeight() - 24f);

        drawPanel(canvas, leftPanel, "А");
        drawPanel(canvas, rightPanel, "Б");
        drawCostume(canvas, leftPanel, false);
        drawCostume(canvas, rightPanel, true);
        buildZones(leftPanel, rightPanel);

        for (int i = 0; i < found.length; i++) {
            if (found[i]) {
                canvas.drawOval(expand(leftZones[i], 10f), foundPaint);
                canvas.drawOval(expand(rightZones[i], 10f), foundPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return true;
        }

        for (int i = 0; i < found.length; i++) {
            if (!found[i] && contains(leftZones[i], event.getX(), event.getY()) || !found[i] && contains(rightZones[i], event.getX(), event.getY())) {
                found[i] = true;
                invalidate();
                if (listener != null) {
                    listener.onDifferenceFound(getFoundCount(), found.length);
                }
                return true;
            }
        }
        return true;
    }

    private void drawPanel(Canvas canvas, RectF panel, String label) {
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(PAPER);
        canvas.drawRoundRect(panel, 18f, 18f, fillPaint);
        canvas.drawRoundRect(panel, 18f, 18f, strokePaint);
        canvas.drawText(label, panel.centerX(), panel.top + 38f, textPaint);
    }

    private void drawCostume(Canvas canvas, RectF panel, boolean altered) {
        float w = panel.width();
        float h = panel.height();
        float left = panel.left;
        float top = panel.top + 44f;
        float cx = panel.centerX();

        Path kokoshnik = new Path();
        kokoshnik.moveTo(cx, top + h * 0.05f);
        kokoshnik.lineTo(left + w * 0.24f, top + h * 0.22f);
        kokoshnik.lineTo(left + w * 0.76f, top + h * 0.22f);
        kokoshnik.close();
        fillPaint.setColor(altered ? GOLD : TURQUOISE);
        canvas.drawPath(kokoshnik, fillPaint);
        canvas.drawPath(kokoshnik, strokePaint);

        RectF face = new RectF(left + w * 0.36f, top + h * 0.20f, left + w * 0.64f, top + h * 0.36f);
        fillPaint.setColor(Color.rgb(255, 232, 204));
        canvas.drawOval(face, fillPaint);
        canvas.drawOval(face, strokePaint);

        Path dress = new Path();
        dress.moveTo(left + w * 0.35f, top + h * 0.40f);
        dress.lineTo(left + w * 0.65f, top + h * 0.40f);
        dress.lineTo(left + w * 0.80f, top + h * 0.86f);
        dress.lineTo(left + w * 0.20f, top + h * 0.86f);
        dress.close();
        fillPaint.setColor(RED);
        canvas.drawPath(dress, fillPaint);
        canvas.drawPath(dress, strokePaint);

        RectF leftSleeve = new RectF(left + w * 0.14f, top + h * 0.43f, left + w * 0.38f, top + h * 0.58f);
        RectF rightSleeve = new RectF(left + w * 0.62f, top + h * 0.43f, left + w * 0.86f, top + h * 0.58f);
        fillPaint.setColor(PAPER);
        canvas.drawRoundRect(leftSleeve, 16f, 16f, fillPaint);
        canvas.drawRoundRect(rightSleeve, 16f, 16f, fillPaint);
        canvas.drawRoundRect(leftSleeve, 16f, 16f, strokePaint);
        canvas.drawRoundRect(rightSleeve, 16f, 16f, strokePaint);

        if (!altered) {
            fillPaint.setColor(BLUE);
            canvas.drawRect(left + w * 0.18f, top + h * 0.49f, left + w * 0.34f, top + h * 0.53f, fillPaint);
        }

        fillPaint.setColor(altered ? BLUE : GOLD);
        RectF belt = new RectF(left + w * 0.29f, top + h * 0.51f, left + w * 0.71f, top + h * 0.58f);
        canvas.drawRoundRect(belt, 10f, 10f, fillPaint);
        canvas.drawRoundRect(belt, 10f, 10f, strokePaint);

        fillPaint.setColor(BLUE);
        canvas.drawCircle(left + w * 0.43f, top + h * 0.39f, 7f, fillPaint);
        if (!altered) {
            canvas.drawCircle(left + w * 0.50f, top + h * 0.40f, 7f, fillPaint);
            canvas.drawCircle(left + w * 0.57f, top + h * 0.39f, 7f, fillPaint);
        }
    }

    private void buildZones(RectF leftPanel, RectF rightPanel) {
        fillZonesForPanel(leftPanel, leftZones);
        fillZonesForPanel(rightPanel, rightZones);
    }

    private void fillZonesForPanel(RectF panel, RectF[] zones) {
        float w = panel.width();
        float h = panel.height();
        float left = panel.left;
        float top = panel.top + 44f;

        zones[0] = new RectF(left + w * 0.22f, top + h * 0.04f, left + w * 0.78f, top + h * 0.24f);
        zones[1] = new RectF(left + w * 0.28f, top + h * 0.50f, left + w * 0.72f, top + h * 0.60f);
        zones[2] = new RectF(left + w * 0.14f, top + h * 0.43f, left + w * 0.38f, top + h * 0.58f);
        zones[3] = new RectF(left + w * 0.40f, top + h * 0.36f, left + w * 0.60f, top + h * 0.44f);
    }

    private boolean contains(RectF rect, float x, float y) {
        return rect != null && rect.contains(x, y);
    }

    private RectF expand(RectF rect, float value) {
        return new RectF(rect.left - value, rect.top - value, rect.right + value, rect.bottom + value);
    }
}
