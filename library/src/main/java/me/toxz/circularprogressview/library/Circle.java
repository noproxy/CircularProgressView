package me.toxz.circularprogressview.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

/**
 * Created by Carlos on 2015/4/15.
 */
class Circle extends View {
    private Paint mStrokePaint;
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

        rect = new RectF();

    }

    void setProgress(int progress) {
        sweepAngle = (float) (progress * 3.6);
    }


    public void reset() {
        //Resetting progress arc
        sweepAngle = 0;
        mDuration = 0;
        startMillis = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i("Circle", "widthMeasureSpec: " + widthMeasureSpec + ", heightMeasureSpec: " + heightMeasureSpec);
    }

    private RectF rect;
    private long startMillis;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int d = Math.min(getMeasuredWidth(), getMeasuredHeight());

        rect.set((getMeasuredWidth() - d + mCircularProgressView.getStrokeSize()) / 2,
                (getMeasuredHeight() - d + mCircularProgressView.getStrokeSize()) / 2,
                (getMeasuredWidth() + d - mCircularProgressView.getStrokeSize()) / 2,
                (getMeasuredHeight() + d - mCircularProgressView.getStrokeSize()) / 2);

        canvas.drawArc(rect, -90, sweepAngle, false, mStrokePaint);


        if (sweepAngle < 360) {
            if (mDuration > 0) {
                if (startMillis <= 0) startMillis = System.currentTimeMillis();
                sweepAngle = ((System.currentTimeMillis() - startMillis) / ((float) mDuration) * 360);
            }
            invalidate();
        } else {
            sweepAngle = 0;
            mCircularProgressView.finalAnimation();
        }
    }

    private long mDuration = 0;

    public void setDuration(long millis) {
        mDuration = millis;
    }


}
