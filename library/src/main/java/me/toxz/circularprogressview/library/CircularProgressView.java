package me.toxz.circularprogressview.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.*;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Carlos on 2015/4/15.
 */
public class CircularProgressView extends FrameLayout {
    private int mStokeColor = R.color.default_stroke_color;
    private Circle mCircleView;
    private ImageView mFillView;
    private float mStrokeSize = getResources().getDimension(R.dimen.default_stroke_size);
    private int mDrawableMargins = (int) getResources().getDimension(R.dimen.default_drawable_margins);
    private Drawable mStartDrawable = getResources().getDrawable(R.drawable.default_start_drawable);
    private Drawable mProgressDrawable = getResources().getDrawable(R.drawable.default_progress_drawable);
    private Drawable mEndDrawable = getResources().getDrawable(R.drawable.default_end_drawable);
    private Paint mFillPaint;
    private ImageView mCenterImage;
    private AnimationSet in, out;
    private ScaleAnimation newScaleIn;
    private Status mStatus = Status.CREATING;
    private OnStatusListener mListener;
    private int mProgress = 0;

    public CircularProgressView(Context context) {
        super(context);
        init(null, 0);
    }

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircularProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public int getStokeColor() {
        return mStokeColor;
    }

    public float getStrokeSize() {
        return mStrokeSize;
    }

    private void init(AttributeSet attrs, int defStyle) {
        initResource(attrs, defStyle);
        initDrawable();
        initPaint();
        initAnimation();
        initView();

    }

    private void initResource(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircularProgressView, defStyle, 0);
        mStokeColor = a.getColor(R.styleable.CircularProgressView_strokeColor, mStokeColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mStrokeSize = a.getDimension(R.styleable.CircularProgressView_strokeSize, mStrokeSize);
        mDrawableMargins = (int) a.getDimension(R.styleable.CircularProgressView_drawableMargins, mDrawableMargins);

        if (a.hasValue(R.styleable.CircularProgressView_startDrawable)) {
            mStartDrawable = a.getDrawable(
                    R.styleable.CircularProgressView_startDrawable);
            mStartDrawable.setCallback(this);
        }
        if (a.hasValue(R.styleable.CircularProgressView_progressDrawable)) {
            mProgressDrawable = a.getDrawable(
                    R.styleable.CircularProgressView_progressDrawable);
            mProgressDrawable.setCallback(this);
        }
        if (a.hasValue(R.styleable.CircularProgressView_endDrawable)) {
            mEndDrawable = a.getDrawable(
                    R.styleable.CircularProgressView_endDrawable);
            mEndDrawable.setCallback(this);
        }

        a.recycle();
    }

    private void initDrawable() {
        mCircleView = new Circle(getContext(), this);
        mCircleView.setClickable(false);
        mCenterImage = new ImageView(getContext());
        mCenterImage.setClickable(false);
        mFillView = new ImageView(getContext());
        mFillView.setClickable(false);

        this.setClickable(true);
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClick();
            }
        });
    }

    private void initPaint() {
        // Set up Paint object
        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setColor(getResources().getColor(mStokeColor));
        mFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    private void initAnimation() {


        ScaleAnimation scaleIn = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ScaleAnimation scaleOut = new ScaleAnimation(1.0f, 3.0f, 1.0f, 3.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        newScaleIn = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleIn.setDuration(150);
        scaleOut.setDuration(150);
        newScaleIn.setDuration(200);

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeIn.setDuration(150);
        fadeOut.setDuration(150);

        in = new AnimationSet(true);
        out = new AnimationSet(true);
        in.setInterpolator(new AccelerateDecelerateInterpolator());
        out.setInterpolator(new AccelerateDecelerateInterpolator());

        in.addAnimation(scaleIn);
        in.addAnimation(fadeIn);
        out.addAnimation(fadeOut);
        out.addAnimation(scaleOut);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCenterImage.setVisibility(INVISIBLE);
                mCenterImage.setImageDrawable(mProgressDrawable);
                mCenterImage.setVisibility(VISIBLE);
                mCenterImage.startAnimation(in);
                mCircleView.setVisibility(VISIBLE);
                mStatus = Status.PROGRESS;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        newScaleIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCircleView.setVisibility(INVISIBLE);
                mCenterImage.setImageDrawable(mEndDrawable);
                mCenterImage.setVisibility(VISIBLE);
                mStatus = Status.END;
                mCenterImage.startAnimation(in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void initView() {
        Log.i("CircularProgressView", "width: " + getWidth() + ", height: " + getHeight());
        Log.i("CircularProgressView", "measureWidth: " + getMeasuredWidth() + ", measureHeight: " + getMeasuredHeight());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(mDrawableMargins, mDrawableMargins, mDrawableMargins, mDrawableMargins);

        mCenterImage.setImageDrawable(mStartDrawable);
        mStatus = Status.START;
        this.addView(mCenterImage, lp);
    }


    @Override
    public void setOnClickListener(OnClickListener l) {
        throw new UnsupportedOperationException("You must use setOnStateListener(OnStatusListener) instead.");
    }

    public void setOnStateListener(OnStatusListener listener) {
        mListener = listener;
    }

    private void handleClick() {
        if (mListener != null) {
            mListener.onStatus(mStatus);
        }
        switch (mStatus) {
            case START:
                startAnimation();
                break;
            case PROGRESS:
                break;
            case END:
                break;
            default:
                break;
        }
    }

    boolean isFirstDraw = true;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (isFirstDraw) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            int width = getWidth(), height = getHeight();
            int r = Math.min(width, height);

            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bgCircleBitmap = Bitmap.createBitmap(r, r, conf);
            Canvas bgCircleCanvas = new Canvas(bgCircleBitmap);

            RectF rect0 = new RectF(0, 0, r, r);
            bgCircleCanvas.drawArc(rect0, 0, 360, false, mFillPaint);
            mFillView.setImageBitmap(bgCircleBitmap);

            mCircleView.setVisibility(INVISIBLE);
            mFillView.setVisibility(INVISIBLE);

            this.addView(mFillView, 0, lp);
            this.addView(mCircleView, lp);
            isFirstDraw = false;
        }
        super.dispatchDraw(canvas);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        mCircleView.setProgress(progress);
    }

    public int getProgress() {
        return mProgress;
    }

    void finalAnimation() {
        mCenterImage.setVisibility(INVISIBLE);
        mFillView.setVisibility(VISIBLE);
        mFillView.startAnimation(newScaleIn);
    }

    private void startAnimation() {
        mFillView.setVisibility(INVISIBLE);
        mCircleView.setVisibility(INVISIBLE);
        mCenterImage.startAnimation(out);
    }

    public void reset() {
        // Responsible for resetting the state of view when Stop is clicked
        this.setProgress(0);
        mCircleView.reset();
        mCircleView.setVisibility(View.INVISIBLE);
        mCenterImage.setImageDrawable(mStartDrawable);
        mStatus = Status.START;

    }

    public enum Status {CREATING, START, PROGRESS, END}

    public interface OnStatusListener {
        void onStatus(Status status);
    }

}
