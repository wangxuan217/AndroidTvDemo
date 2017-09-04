package com.xiaoxuan.demo.tv.widget;

import java.util.List;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.ViewGroup;

public class DrawableFocusBorder extends AbsFocusBorder
{
    private Drawable mBorderDrawable;
    
    private DrawableFocusBorder(Context context, int shimmerColor, long shimmerDuration, boolean isShimmerAnim,
        long animDuration, RectF paddingOfsetRectF, Drawable borderDrawable)
    {
        super(context, shimmerColor, shimmerDuration, isShimmerAnim, animDuration, paddingOfsetRectF);
        
        this.mBorderDrawable = borderDrawable;
        final Rect paddingRect = new Rect();
        mBorderDrawable.getPadding(paddingRect);
        mPaddingRectF.set(paddingRect);
        
        if (Build.VERSION.SDK_INT >= 16)
        {
            setBackground(mBorderDrawable);
        }
        else
        {
            setBackgroundDrawable(mBorderDrawable);
        }
    }
    
    @Override
    public float getRoundRadius()
    {
        return 0;
    }
    
    @Override
    List<Animator> getTogetherAnimators(float newX, float newY, int newWidth, int newHeight,
        AbsFocusBorder.Options options)
    {
        return null;
    }
    
    @Override
    List<Animator> getSequentiallyAnimators(float newX, float newY, int newWidth, int newHeight,
        AbsFocusBorder.Options options)
    {
        return null;
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }
    
    public final static class Builder extends AbsFocusBorder.Builder
    {
        private int mBorderResId = 0;
        
        private Drawable mBorderDrawable = null;
        
        public Builder borderResId(int resid)
        {
            mBorderResId = resid;
            return this;
        }
        
        public Builder borderDrawable(Drawable drawable)
        {
            mBorderDrawable = drawable;
            return this;
        }
        
        @Override
        public FocusBorder build(Activity activity)
        {
            if (null == activity)
            {
                throw new NullPointerException("The activity cannot be null");
            }
            if (null == mBorderDrawable && mBorderResId == 0)
            {
                throw new RuntimeException("The activity cannot be null");
            }
            final ViewGroup parent = (ViewGroup)activity.findViewById(android.R.id.content);
            return build(parent);
        }
        
        @Override
        public FocusBorder build(ViewGroup parent)
        {
            if (null == parent)
            {
                throw new NullPointerException("The FocusBorder parent cannot be null");
            }
            final Drawable drawable = null != mBorderDrawable ? mBorderDrawable
                : Build.VERSION.SDK_INT >= 21 ? parent.getContext().getDrawable(mBorderResId)
                    : parent.getContext().getResources().getDrawable(mBorderResId);
            final DrawableFocusBorder boriderView = new DrawableFocusBorder(parent.getContext(), mShimmerColor,
                mShimmerDuration, mIsShimmerAnim, mAnimDuration, mPaddingOfsetRectF, drawable);
            final ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(1, 1);
            parent.addView(boriderView, lp);
            return boriderView;
        }
    }
}
