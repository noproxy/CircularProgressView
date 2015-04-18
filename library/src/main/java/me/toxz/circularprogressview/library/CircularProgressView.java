package me.toxz.circularprogressview.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Carlos on 2015/4/15.
 */
public class CircularProgressView extends RelativeLayout {
    private int mStokeColor = R.color.cpv_default_stroke_color;
    private Circle mCircleView;
    private ImageView mFillView;
    private float mStrokeSize = getResources().getDimension(R.dimen.cpv_default_stroke_size);
    private int mStartDrawableMargins = (int) getResources().getDimension(R.dimen.cpv_default_drawable_margins);
    private int mProgressDrawableMargins = mStartDrawableMargins;
    private int mEndDrawableMargins = mStartDrawableMargins;
    private int mClickedAnimationDelay = 0;
    private Drawable mStartDrawable = getResources().getDrawable(R.drawable.cpv_default_start_drawable);
    private Drawable mProgressDrawable = getResources().getDrawable(R.drawable.cpv_default_progress_drawable);
    private Drawable mEndDrawable = getResources().getDrawable(R.drawable.cpv_default_end_drawable);
    private boolean isStartAnimationAuto = true;
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
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.cpv_CircularProgressView, defStyle, 0);
        mStokeColor = a.getColor(R.styleable.cpv_CircularProgressView_cpv_strokeColor, mStokeColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mStrokeSize = a.getDimension(R.styleable.cpv_CircularProgressView_cpv_strokeSize, mStrokeSize);
        mClickedAnimationDelay = a.getInt(R.styleable.cpv_CircularProgressView_cpv_clickAnimationDelayMillis, mClickedAnimationDelay);

        mStartDrawableMargins = (int) a.getDimension(R.styleable.cpv_CircularProgressView_cpv_startDrawableMargins, mStartDrawableMargins);
        mProgressDrawableMargins = (int) a.getDimension(R.styleable.cpv_CircularProgressView_cpv_progressDrawableMargins, mProgressDrawableMargins);
        mEndDrawableMargins = (int) a.getDimension(R.styleable.cpv_CircularProgressView_cpv_endDrawableMargins, mEndDrawableMargins);

        isStartAnimationAuto = a.getBoolean(R.styleable.cpv_CircularProgressView_cpv_startAnimationAuto, isStartAnimationAuto);

        if (a.hasValue(R.styleable.cpv_CircularProgressView_cpv_startDrawable)) {
            mStartDrawable = a.getDrawable(
                    R.styleable.cpv_CircularProgressView_cpv_startDrawable);
            mStartDrawable.setCallback(this);
        }
        if (a.hasValue(R.styleable.cpv_CircularProgressView_cpv_progressDrawable)) {
            mProgressDrawable = a.getDrawable(
                    R.styleable.cpv_CircularProgressView_cpv_progressDrawable);
            mProgressDrawable.setCallback(this);
        }
        if (a.hasValue(R.styleable.cpv_CircularProgressView_cpv_endDrawable)) {
            mEndDrawable = a.getDrawable(
                    R.styleable.cpv_CircularProgressView_cpv_endDrawable);
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
        ScaleAnimation scaleOut = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
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
                wrapLayoutParams.setMargins(mProgressDrawableMargins, mProgressDrawableMargins, mProgressDrawableMargins, mProgressDrawableMargins);
                mCenterImage.setLayoutParams(wrapLayoutParams);
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
                wrapLayoutParams.setMargins(mEndDrawableMargins, mEndDrawableMargins, mEndDrawableMargins, mEndDrawableMargins);
                mCenterImage.setLayoutParams(wrapLayoutParams);
                mCenterImage.setImageDrawable(mEndDrawable);
                mCenterImage.setVisibility(VISIBLE);
                mStatus = Status.END;
                mCenterImage.startAnimation(in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        allOut = new AnimationSet(true);
        allOut.setInterpolator(new AccelerateDecelerateInterpolator());
        allOut.addAnimation(fadeOut);
        allOut.addAnimation(scaleOut);
        allOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                wrapLayoutParams.setMargins(mStartDrawableMargins, mStartDrawableMargins, mStartDrawableMargins, mStartDrawableMargins);
                mCenterImage.setLayoutParams(wrapLayoutParams);
                mCenterImage.setImageDrawable(mStartDrawable);
                mFillView.setVisibility(INVISIBLE);
                CircularProgressView.this.startAnimation(allIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        allIn = new AnimationSet(true);
        allIn.setInterpolator(new AccelerateDecelerateInterpolator());
        allIn.addAnimation(scaleIn);
        allIn.addAnimation(fadeIn);
        allIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mStatus = Status.START;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setStartAnimationAuto(boolean isStartAnimationAuto) {
        this.isStartAnimationAuto = isStartAnimationAuto;
    }

    public void setStrokeSize(float strokeSize) {
        this.mStrokeSize = strokeSize;
    }

    public void setClickedAnimationDelay(int clickedAnimationDelay) {
        this.mClickedAnimationDelay = clickedAnimationDelay;
    }

    private AnimationSet allOut;
    private AnimationSet allIn;

    RelativeLayout.LayoutParams wrapLayoutParams;

    private void initView() {
        Log.i("CircularProgressView", "width: " + getWidth() + ", height: " + getHeight());
        Log.i("CircularProgressView", "measureWidth: " + getMeasuredWidth() + ", measureHeight: " + getMeasuredHeight());
        wrapLayoutParams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        wrapLayoutParams.addRule(CENTER_IN_PARENT);
        wrapLayoutParams.setMargins(mStartDrawableMargins, mStartDrawableMargins, mStartDrawableMargins, mStartDrawableMargins);

        mCenterImage.setImageDrawable(mStartDrawable);
        mCenterImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mStatus = Status.START;
        this.addView(mCenterImage, wrapLayoutParams);
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
                if (isStartAnimationAuto) {
                    startAnimation();
                }
                break;
            case PROGRESS:
                break;
            case END:
                break;
            case CREATING:
                mStatus = Status.START;
                break;
            default:
                break;
        }
    }

    boolean isFirstDraw = true;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (isFirstDraw) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.addRule(CENTER_IN_PARENT);
            int width = getWidth(), height = getHeight();
            int l = Math.min(width, height);

            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bgCircleBitmap = Bitmap.createBitmap(l, l, conf);
            Canvas bgCircleCanvas = new Canvas(bgCircleBitmap);

            RectF rect0 = new RectF(0, 0, l, l);
            bgCircleCanvas.drawArc(rect0, 0, 360, false, mFillPaint);
            mFillView.setImageBitmap(bgCircleBitmap);

            mCircleView.setVisibility(INVISIBLE);
            mFillView.setVisibility(INVISIBLE);

            this.addView(mFillView, 0, lp);

            lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            lp.addRule(CENTER_IN_PARENT);
            this.addView(mCircleView, lp);
            isFirstDraw = false;
        }
        super.dispatchDraw(canvas);
    }

    /**
     * set progress percentage. If progress is more than 100 will cause animation to endDrawable.
     * @param progress percentage
     */
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

    /**
     * start animation, it must be called on ui thread.
     * <p>
     * By default, it will be called automatically when clicked. Only call it yourself if you has set cpv_startAnimationAuto="false".
     */
    public void startAnimation() {
        mFillView.setVisibility(INVISIBLE);
        mCircleView.setVisibility(INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCenterImage.startAnimation(out);
            }
        }, mClickedAnimationDelay);
    }

    /**
     * reset CircularProgressView to origin status.
     */
    public void reset() {
        // Responsible for resetting the state of view when Stop is clicked
        mCircleView.reset();
        mProgress = 0;
        mCircleView.setVisibility(View.INVISIBLE);
        wrapLayoutParams.setMargins(mStartDrawableMargins, mStartDrawableMargins, mStartDrawableMargins, mStartDrawableMargins);
        mCenterImage.setLayoutParams(wrapLayoutParams);
        mCenterImage.setImageDrawable(mStartDrawable);
        mFillView.setVisibility(INVISIBLE);
        mCenterImage.clearAnimation();
        mStatus = Status.CREATING;
    }

    /**
     * reset CircularProgressView to origin status. This process has an animation.
     */
    public void resetSmoothly() {
        mCircleView.reset();
        mProgress = 0;
        mCircleView.setVisibility(View.INVISIBLE);
        mStatus = Status.CREATING;
        mCenterImage.clearAnimation();
        this.startAnimation(allOut);

    }

    public enum Status {CREATING, START, PROGRESS, END}

    /**
     * set progress animation durations.
     *
     * @param millis the duration.
     */
    public void setDuration(long millis) {
        mCircleView.setDuration(millis);
    }

    public interface OnStatusListener {
        void onStatus(Status status);
    }

}
