package com.xiaoxuan.demo.tv.widget.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.xiaoxuan.demo.androidtvdemoxx.R;

/**
 * 倒计时进度动画View
 */
public class CountDownProgress extends View
{
    
    private Paint circlePaint;
    
    private Paint progressPaint;
    
    private Paint textPaint;
    
    private RectF arcRecF;// 绘制圆弧的矩形区域
    
    private float currentAngle;
    
    private int circleRadius;// 圆半径
    
    private int circleStrokeWidth;// 圆框宽度
    
    private int circleColor;// 圆框颜色
    
    private int circleSolidColor;// 圆内部填充颜色
    
    private int progressColor;// 进度颜色
    
    private int progressWidth;// 进度条宽度，默认与 circleStrokeWidth 一致
    
    private int textColor;// 文字颜色
    
    private float textSize;// 文字大小
    
    private CharSequence text;// 文字
    
    private float startDegree;// 进度的起始角度
    
    private int countDownTime;// 倒计时长
    
    private ValueAnimator animator;
    
    private OnFinishListener finishListener;
    
    private Paint solidPaint;
    
    public CountDownProgress(Context context)
    {
        super(context);
        init(null);
    }
    
    public CountDownProgress(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }
    
    public CountDownProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CountDownProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }
    
    private void init(@Nullable AttributeSet attrs)
    {
        initAttr(attrs);
        setPaint();
        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cancel();
            }
        });
    }
    
    private void initAttr(@Nullable AttributeSet attrs)
    {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CountDownProgress);
        if (a != null)
        {
            // circleRadius = a.getDimensionPixelOffset(R.styleable.CountDownProgress_cdp_circle_radius, dp2px(17));
            circleSolidColor = a.getColor(R.styleable.CountDownProgress_cdp_circle_solid_color, 0xccaaaaaa);
            circleStrokeWidth =
                a.getDimensionPixelOffset(R.styleable.CountDownProgress_cdp_circle_stroke_width, dp2px(2));
            circleColor = a.getColor(R.styleable.CountDownProgress_cdp_circle_stroke_color, 0xffe0e0e0);
            progressColor = a.getColor(R.styleable.CountDownProgress_cdp_progress_color, 0xff5e5e5e);
            progressWidth =
                a.getDimensionPixelOffset(R.styleable.CountDownProgress_cdp_progress_width, circleStrokeWidth);
            textColor = a.getColor(R.styleable.CountDownProgress_cdp_text_color, 0xffffffff);
            textSize = a.getDimensionPixelSize(R.styleable.CountDownProgress_cdp_text_size, dp2px(14));
            text = a.getString(R.styleable.CountDownProgress_cdp_text);
            countDownTime = a.getInt(R.styleable.CountDownProgress_cdp_count_down_time, 5000);
            startDegree = a.getFloat(R.styleable.CountDownProgress_cdp_start_degree, 270f);
            if (TextUtils.isEmpty(text))
                text = "跳过";
            a.recycle();
        }
    }
    
    private void setPaint()
    {
        // 圆框画笔
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);// 抗锯齿
        circlePaint.setDither(true);// 防抖动
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(circleStrokeWidth);
        circlePaint.setColor(circleColor);
        // 内部填充色的内圆
        solidPaint = new Paint(circlePaint);
        solidPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        solidPaint.setStrokeWidth(circleStrokeWidth / 2f);
        solidPaint.setColor(circleSolidColor);
        // 进度条
        progressPaint = new Paint(circlePaint);
        progressPaint.setColor(progressColor);
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);// 设置笔刷样式
        // 文字
        textPaint = new Paint(circlePaint);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
    }
    
    private void setRecF()
    {
        arcRecF = new RectF(0, 0, circleRadius * 2, circleRadius * 2);
    }
    
    int width, height;
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY)
        {
            measureView();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        else
        {
            int min = Math.min(MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom());
            circleRadius = (min - circleStrokeWidth) / 2;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setRecF();
    }
    
    private void measureView()
    {
        float textWidth = textPaint.measureText(text.toString());
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        float textHeight = metrics.bottom - metrics.top;
        circleRadius =
            (int)Math.round((Math.sqrt(textWidth * textWidth + textHeight * textHeight) + circleStrokeWidth) / 2.0f);
        width = getPaddingLeft() + circleRadius * 2 + circleStrokeWidth + getPaddingRight();
        height = getPaddingTop() + circleRadius * 2 + circleStrokeWidth + getPaddingBottom();
    }
    
    public int dp2px(float dp)
    {
        Resources resources = getContext().getResources();
        float scale = resources.getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f * (dp >= 0 ? 1 : -1));
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getPaddingLeft() + circleStrokeWidth / 2.0f, getPaddingTop() + circleStrokeWidth / 2.0f);
        // 画圆
        canvas.drawCircle(circleRadius, circleRadius, circleRadius, circlePaint);
        canvas.drawCircle(circleRadius, circleRadius, circleRadius - circleStrokeWidth / 2.0f, solidPaint);
        // 画进度圆弧
        canvas.drawArc(arcRecF, startDegree, currentAngle * 360f, false, progressPaint);
        float textWidth = textPaint.measureText(text.toString());
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        float baseline = circleRadius + (metrics.descent - metrics.ascent) / 2 - metrics.descent;
        canvas.drawText(text.toString(), circleRadius - textWidth / 2, baseline, textPaint);
        
        canvas.restore();
    }
    
    public void startCountDownTime()
    {
        if (animator != null && animator.isRunning())
            return;
        animator = ValueAnimator.ofFloat(0, 1.0f);
        animator.setDuration(countDownTime);
        animator.setInterpolator(new LinearInterpolator());// 匀速
        animator.setRepeatCount(0);// 不循环，-1：无限循环
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                currentAngle = (float)animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                
            }
            
            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (finishListener != null)
                {
                    finishListener.onFinish();
                }
            }
            
            @Override
            public void onAnimationCancel(Animator animation)
            {
            }
            
            @Override
            public void onAnimationRepeat(Animator animation)
            {
                
            }
        });
        animator.start();
    }
    
    public void cancel()
    {
        if (animator != null)
        {
            animator.cancel();
        }
    }
    
    public void reset()
    {
        currentAngle = 0f;
        invalidate();
    }
    
    public CountDownProgress setFinishListener(OnFinishListener finishListener)
    {
        this.finishListener = finishListener;
        return this;
    }
    
    public interface OnFinishListener
    {
        void onFinish();
    }
    
    public CountDownProgress setTextColor(@ColorInt int textColor)
    {
        this.textColor = textColor;
        textPaint.setColor(textColor);
        return this;
    }
    
    /**
     * @param textSize dp
     * @return
     */
    public CountDownProgress setTextSize(float textSize)
    {
        this.textSize = dp2px(textSize);
        textPaint.setTextSize(this.textSize);
        requestLayout();
        return this;
    }
    
    public CountDownProgress setText(CharSequence text)
    {
        this.text = text;
        requestLayout();
        return this;
    }
    
    public CountDownProgress setCountDownTime(int countDownTime)
    {
        this.countDownTime = countDownTime;
        return this;
    }
}
