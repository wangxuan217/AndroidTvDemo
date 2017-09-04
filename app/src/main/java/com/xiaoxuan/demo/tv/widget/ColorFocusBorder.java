package com.xiaoxuan.demo.tv.widget;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.ViewGroup;

public class ColorFocusBorder extends AbsFocusBorder
{
    private Paint mShadowPaint;
    
    private Paint mBorderPaint;
    
    private int mShadowColor = Color.RED;
    
    private float mShadowWidth = 20f;
    
    private int mBorderColor = Color.DKGRAY;
    
    private float mBorderWidth = 2f;
    
    private float mRoundRadius = 0;
    
    private ObjectAnimator mRoundRadiusAnimator;
    
    private ColorFocusBorder(Context context, int shimmerColor, long shimmerDuration, boolean isShimmerAnim,
        long animDuration, RectF paddingOfsetRectF, int shadowColor, float shadowWidth, int borderColor,
        float borderWidth)
    {
        super(context, shimmerColor, shimmerDuration, isShimmerAnim, animDuration, paddingOfsetRectF);
        this.mShadowColor = shadowColor;
        this.mShadowWidth = shadowWidth;
        this.mBorderColor = borderColor;
        this.mBorderWidth = borderWidth;
        
        final float padding = mShadowWidth + mBorderWidth;
        mPaddingRectF.set(padding, padding, padding, padding);
        initPaint();
    }
    
    private void initPaint()
    {
        mShadowPaint = new Paint();
        mShadowPaint.setColor(mShadowColor);
        mShadowPaint.setDither(true); // 抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰 by xiaoxuan 可以屏蔽
        mShadowPaint.setMaskFilter(new BlurMaskFilter(mShadowWidth, BlurMaskFilter.Blur.OUTER));
        mBorderPaint = new Paint();
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setMaskFilter(new BlurMaskFilter(0.5f, BlurMaskFilter.Blur.NORMAL));
    }
    
    protected void setRoundRadius(float roundRadius)
    {
        if (mRoundRadius != roundRadius)
        {
            mRoundRadius = roundRadius;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
    
    @Override
    public float getRoundRadius()
    {
        return mRoundRadius;
    }
    
    @Override
    List<Animator> getTogetherAnimators(float newX, float newY, int newWidth, int newHeight,
        AbsFocusBorder.Options options)
    {
        if (options instanceof Options)
        {
            final Options rawOptions = (Options)options;
            List<Animator> animators = new ArrayList<>();
            animators.add(getRoundRadiusAnimator(rawOptions.roundRadius));
            return animators;
        }
        return null;
    }
    
    @Override
    List<Animator> getSequentiallyAnimators(float newX, float newY, int newWidth, int newHeight,
        AbsFocusBorder.Options options)
    {
        return null;
    }
    
    private ObjectAnimator getRoundRadiusAnimator(float roundRadius)
    {
        if (null == mRoundRadiusAnimator)
        {
            mRoundRadiusAnimator = ObjectAnimator.ofFloat(this, "roundRadius", getRoundRadius(), roundRadius);
        }
        else
        {
            mRoundRadiusAnimator.setFloatValues(getRoundRadius(), roundRadius);
        }
        return mRoundRadiusAnimator;
    }
    
    /**
     * 绘制外发光阴影
     * 
     * @param canvas
     */
    private void onDrawShadow(Canvas canvas)
    {
        if (mShadowWidth > 0)
        {
            canvas.save();
            // 裁剪处理(使阴影矩形框内变为透明)
            if (mRoundRadius > 0)
            {
                canvas.clipRect(0, 0, getWidth(), getHeight());
                mTempRectF.set(mFrameRectF);
                mTempRectF.inset(mRoundRadius / 2f, mRoundRadius / 2f);
                canvas.clipRect(mTempRectF, Region.Op.DIFFERENCE);
            }
            // 绘制外发光阴影效果
            canvas.drawRoundRect(mFrameRectF, mRoundRadius, mRoundRadius, mShadowPaint);
            canvas.restore();
        }
    }
    
    /**
     * 绘制边框
     * 
     * @param canvas
     */
    private void onDrawBorder(Canvas canvas)
    {
        if (mBorderWidth > 0)
        {
            canvas.save();
            mTempRectF.set(mFrameRectF);
            canvas.drawRoundRect(mTempRectF, mRoundRadius, mRoundRadius, mBorderPaint);
            canvas.restore();
        }
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        onDrawShadow(canvas);
        onDrawBorder(canvas);
        super.onDraw(canvas);
    }
    
    public static class Options extends AbsFocusBorder.Options
    {
        private float roundRadius = 0f;
        
        Options()
        {
            super();
        }
        
        private static class OptionsHolder
        {
            private static final Options INSTANCE = new Options();
        }
        
        public static Options get(float scaleX, float scaleY, float roundRadius)
        {
            OptionsHolder.INSTANCE.scaleX = scaleX;
            OptionsHolder.INSTANCE.scaleY = scaleY;
            OptionsHolder.INSTANCE.roundRadius = roundRadius;
            return OptionsHolder.INSTANCE;
        }
        
    }
    
    public final static class Builder extends AbsFocusBorder.Builder
    {
        private int mShadowColor = 0;
        
        private float mShadowWidth = 0f;
        
        private int mBorderColor = 0;
        
        private float mBorderWidth = 0f;
        
        public Builder shadowColor(int color)
        {
            mShadowColor = color;
            return this;
        }
        
        public Builder shadowWidth(float pxWidth)
        {
            mShadowWidth = pxWidth;
            return this;
        }
        
        public Builder shadowWidth(int unit, float width)
        {
            mShadowWidth = TypedValue.applyDimension(unit, width, Resources.getSystem().getDisplayMetrics());
            return this;
        }
        
        public Builder borderColor(int color)
        {
            mBorderColor = color;
            return this;
        }
        
        public Builder borderWidth(float pxWidth)
        {
            mBorderWidth = pxWidth;
            return this;
        }
        
        public Builder borderWidth(int unit, float width)
        {
            mBorderWidth = TypedValue.applyDimension(unit, width, Resources.getSystem().getDisplayMetrics());
            return this;
        }
        
        @Override
        public FocusBorder build(Activity activity)
        {
            if (null == activity)
            {
                throw new NullPointerException("The activity cannot be null");
            }
            ViewGroup parent = (ViewGroup)activity.findViewById(android.R.id.content);
            return build(parent);
        }
        
        @Override
        public FocusBorder build(ViewGroup parent)
        {
            if (null == parent)
            {
                throw new NullPointerException("The FocusBorder parent cannot be null");
            }
            final ColorFocusBorder boriderView =
                new ColorFocusBorder(parent.getContext(), mShimmerColor, mShimmerDuration, mIsShimmerAnim,
                    mAnimDuration, mPaddingOfsetRectF, mShadowColor, mShadowWidth, mBorderColor, mBorderWidth);
            final ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(1, 1);
            parent.addView(boriderView, lp);
            return boriderView;
        }
    }
}
