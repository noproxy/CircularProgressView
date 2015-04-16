package me.toxz.circularprogressview.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Carlos on 2015/4/15.
 */
class Circle extends View {
    private Paint mStrokePaint;
    private RectF rect;
    private int pix = 0;
    private float sweepAngle;


    private CircularProgressView mCircularProgressView;


    public Circle(Context context, CircularProgressView circularProgressView) {
        super(context);
        mCircularProgressView = circularProgressView;
        init();
    }

    private void init() {
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setColor(getResources().getColor(mCircularProgressView.getStokeColor()));
        mStrokePaint.setStrokeWidth(mCircularProgressView.getStrokeSize());
        mStrokePaint.setStyle(Paint.Style.STROKE);

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        float scarea = width * height;
        pix = (int) Math.sqrt(scarea * 0.0217);

        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(Color.rgb(0, 161, 234));  //Edit this to change progress arc color.
        mStrokePaint.setStrokeWidth(7);

        float startx = (float) (pix * 0.05);
        float endx = (float) (pix * 0.95);
        float starty = (float) (pix * 0.05);
        float endy = (float) (pix * 0.95);
        rect = new RectF(startx, starty, endx, endy);

    }

    void setProgress(int progress) {
        sweepAngle = (float) (progress * 3.6);
    }

    private int flag = 0;

    public void reset() {
        //Resetting progress arc
        sweepAngle = 0;
        flag = 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = pix;
        int desiredHeight = pix;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;


        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }


        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }


        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rect, -90, sweepAngle, false, mStrokePaint);
        if (sweepAngle < 360 && flag == 0) {
            invalidate();
        } else if (flag == 1) {
            sweepAngle = 0;
            flag = 0;
            invalidate();
        } else {
            sweepAngle = 0;
            mCircularProgressView.finalAnimation();
        }
    }

}
