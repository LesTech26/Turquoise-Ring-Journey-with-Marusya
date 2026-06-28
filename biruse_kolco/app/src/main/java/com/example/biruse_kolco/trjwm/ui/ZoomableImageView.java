package com.example.biruse_kolco.trjwm.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {
    private static final float MIN_SCALE = 1.0f;
    private static final float MAX_SCALE = 4.0f;

    private final Matrix matrix = new Matrix();
    private final float[] values = new float[9];
    private ScaleGestureDetector scaleDetector;
    private PointF last = new PointF();
    private float scale = 1f;
    private int mode = NONE;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    public ZoomableImageView(Context context) {
        super(context);
        init(context);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        fitCenter();
    }

    private void fitCenter() {
        if (getDrawable() == null) return;
        float viewWidth = getWidth();
        float viewHeight = getHeight();
        float drawableWidth = getDrawable().getIntrinsicWidth();
        float drawableHeight = getDrawable().getIntrinsicHeight();
        if (viewWidth == 0 || viewHeight == 0 || drawableWidth == 0 || drawableHeight == 0) return;
        float scaleX = viewWidth / drawableWidth;
        float scaleY = viewHeight / drawableHeight;
        float initialScale = Math.min(scaleX, scaleY);
        matrix.reset();
        matrix.postScale(initialScale, initialScale);
        float dx = (viewWidth - drawableWidth * initialScale) / 2f;
        float dy = (viewHeight - drawableHeight * initialScale) / 2f;
        matrix.postTranslate(dx, dy);
        setImageMatrix(matrix);
        scale = 1f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        PointF current = new PointF(event.getX(), event.getY());

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                last.set(current);
                mode = DRAG;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG && scale > MIN_SCALE) {
                    float dx = current.x - last.x;
                    float dy = current.y - last.y;
                    matrix.postTranslate(dx, dy);
                    fixTranslation();
                    last.set(current);
                    setImageMatrix(matrix);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mode = NONE;
                break;
        }
        return true;
    }

    private void fixTranslation() {
        matrix.getValues(values);
        float transX = values[Matrix.MTRANS_X];
        float transY = values[Matrix.MTRANS_Y];
        float scaleX = values[Matrix.MSCALE_X];
        float scaleY = values[Matrix.MSCALE_Y];

        float viewWidth = getWidth();
        float viewHeight = getHeight();
        float drawableWidth = getDrawable().getIntrinsicWidth() * scaleX;
        float drawableHeight = getDrawable().getIntrinsicHeight() * scaleY;

        float maxTransX = 0;
        float maxTransY = 0;
        float minTransX = viewWidth - drawableWidth;
        float minTransY = viewHeight - drawableHeight;

        if (drawableWidth <= viewWidth) {
            transX = (viewWidth - drawableWidth) / 2f;
        } else {
            if (transX > maxTransX) transX = maxTransX;
            if (transX < minTransX) transX = minTransX;
        }

        if (drawableHeight <= viewHeight) {
            transY = (viewHeight - drawableHeight) / 2f;
        } else {
            if (transY > maxTransY) transY = maxTransY;
            if (transY < minTransY) transY = minTransY;
        }

        matrix.setValues(values);
        matrix.postTranslate(transX - values[Matrix.MTRANS_X], transY - values[Matrix.MTRANS_Y]);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float factor = detector.getScaleFactor();
            float newScale = scale * factor;
            if (newScale < MIN_SCALE) {
                factor = MIN_SCALE / scale;
                scale = MIN_SCALE;
            } else if (newScale > MAX_SCALE) {
                factor = MAX_SCALE / scale;
                scale = MAX_SCALE;
            } else {
                scale = newScale;
            }
            matrix.postScale(factor, factor, detector.getFocusX(), detector.getFocusY());
            fixTranslation();
            setImageMatrix(matrix);
            invalidate();
            mode = ZOOM;
            return true;
        }
    }
}
