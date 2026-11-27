/*
 * Projet : FarNDone (FnD)
 * Fichier : HalftoneView.java
 * Description : Vue personnalisée : trackpad en points halftone qui réagit au doigt
 * Responsable(s) :
 * - [NOM] Youness
 * - [NOM] Taha
 */
package com.farndone.android;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

public class HalftoneView extends View {

    public interface OnPadListener {
        void onPadMoved(float x, float y);
    }

    private static final int COLUMNS = 13;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float focusX = -1, focusY = -1;   // -1 = centre de la vue
    private ValueAnimator backToCenter;
    private OnPadListener listener;

    public HalftoneView(Context context) { this(context, null); }

    public HalftoneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(ContextCompat.getColor(context, R.color.blue));
    }

    public void setOnPadListener(OnPadListener l) { listener = l; }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth(), h = getHeight();
        float pad = dp(14);
        float aw = w - 2 * pad, ah = h - 2 * pad;
        float step = aw / COLUMNS;
        int rows = Math.max(1, (int) (ah / step));
        float fx = focusX < 0 ? w / 2f : focusX;
        float fy = focusY < 0 ? h / 2f : focusY;
        float reach = 0.7f * Math.min(w, h);
        float offY = pad + (ah - rows * step) / 2f;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                float cx = pad + (c + 0.5f) * step;
                float cy = offY + (r + 0.5f) * step;
                float d = (float) Math.hypot(cx - fx, cy - fy);
                float t = Math.max(0f, 1f - d / reach);
                float radius = step * 0.5f * (0.08f + 0.92f * (float) Math.pow(t, 1.5));
                paint.setAlpha((int) (255 * (0.45f + 0.55f * t)));
                canvas.drawCircle(cx, cy, radius, paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (backToCenter != null) backToCenter.cancel();
                getParent().requestDisallowInterceptTouchEvent(true);
                // pas de break : on traite aussi la position
            case MotionEvent.ACTION_MOVE:
                focusX = Math.max(0, Math.min(getWidth(), e.getX()));
                focusY = Math.max(0, Math.min(getHeight(), e.getY()));
                if (listener != null) listener.onPadMoved(focusX, focusY);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                performClick();
                // pas de break
            case MotionEvent.ACTION_CANCEL:
                animateBack();
                return true;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean performClick() { return super.performClick(); }

    private void animateBack() {
        final float sx = focusX, sy = focusY, ex = getWidth() / 2f, ey = getHeight() / 2f;
        backToCenter = ValueAnimator.ofFloat(0f, 1f);
        backToCenter.setDuration(350);
        backToCenter.addUpdateListener(a -> {
            float f = (float) a.getAnimatedValue();
            focusX = sx + (ex - sx) * f;
            focusY = sy + (ey - sy) * f;
            invalidate();
        });
        backToCenter.start();
    }

    private float dp(float v) { return v * getResources().getDisplayMetrics().density; }
}