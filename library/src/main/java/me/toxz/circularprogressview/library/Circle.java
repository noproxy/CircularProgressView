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

    private int flag = 0;

    public void reset() {
        //Resetting progress arc
        sweepAngle = 0;
        flag = 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i("Circle", "widthMeasureSpec: " + widthMeasureSpec + ", heightMeasureSpec: " + heightMeasureSpec);
    }

    private RectF rect;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rect.set(0 + mCircularProgressView.getStrokeSize() / 2, 0 + mCircularProgressView.getStrokeSize() / 2, getMeasuredWidth() - mCircularProgressView.getStrokeSize() / 2, getMeasuredHeight() - mCircularProgressView.getStrokeSize() / 2);

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
