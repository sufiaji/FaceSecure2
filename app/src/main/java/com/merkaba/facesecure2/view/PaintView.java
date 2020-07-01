package com.merkaba.facesecure2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;

public class PaintView extends View {
    // setup initial color
    private final int paintColor = Color.GREEN;
    // defines paint and canvas
    private Paint drawPaint;
    // stores next circle
    private Path path = new Path();

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
    }

    private void setupPaint() {
        // Setup paint with color and stroke styles
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(10);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, drawPaint);
    }

    public void clear() {
        path.reset();
        postInvalidate();
    }

    public void setPaintColor(int color) {
        drawPaint.setColor(color);
    }

    public void drawLine(float x0, float y0, float x1, float y1) {
        path.moveTo(x0, y0);
        path.lineTo(x1, y1);
    }

    public void drawRectangle(float[] leftTop, float[] rightTop, float[] rightBottom, float[] leftBottom) {
        drawLine(leftTop[0], leftTop[1], rightTop[0], rightTop[1]);
        drawLine(rightTop[0], rightTop[1], rightBottom[0], rightBottom[1]);
        drawLine(rightBottom[0], rightBottom[1], leftBottom[0], leftBottom[1]);
        drawLine(leftTop[0], leftTop[1], leftBottom[0], leftBottom[1]);
        postInvalidate();
    }

}
