package com.xiaoxuan.demo.tv.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.xiaoxuan.demo.androidtvdemoxx.R;

/**
 * @author xiaoxuan 2017.09.04 布局工具类
 */
public class RoundImpl
{
    
    private float mRadius;
    
    private float mTopLeftRadius;
    
    private float mTopRightRadius;
    
    private float mBottomLeftRadius;
    
    private float mBottomRightRadius;
    
    private Paint mPaint;
    
    private Path mPath;
    
    private RoundedView mView;
    
    public RoundImpl(RoundedView view, Context context, AttributeSet attrs, int defStyle)
    {
        init(context, attrs, defStyle);
        mView = view;
    }
    
    private void init(Context context, AttributeSet attrs, int defStyle)
    {
        
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mPath = new Path();
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImpl, defStyle, 0);
        mBottomLeftRadius = a.getDimension(R.styleable.RoundImpl_bottomLeftRadius, -1);
        mBottomRightRadius = a.getDimension(R.styleable.RoundImpl_bottomRightRadius, -1);
        mTopLeftRadius = a.getDimension(R.styleable.RoundImpl_topLeftRadius, -1);
        mTopRightRadius = a.getDimension(R.styleable.RoundImpl_topRightRadius, -1);
        mRadius = a.getDimension(R.styleable.RoundImpl_radius, -1);
        
        if (mRadius > 0)
        {
            if (mBottomLeftRadius < 0)
                mBottomLeftRadius = mRadius;
            if (mBottomRightRadius < 0)
                mBottomRightRadius = mRadius;
            if (mTopLeftRadius < 0)
                mTopLeftRadius = mRadius;
            if (mTopRightRadius < 0)
                mTopRightRadius = mRadius;
        }
        
        a.recycle();
        
    }
    
    public interface RoundedView
    {
        
        public void drawSuper(Canvas canvas);
        
        public Context getContext();
        
        public RoundImpl getRoundImpl();
        
        public int getWidth();
        
        public int getHeight();
    }
    
    public void draw(Canvas canvas)
    {
        
        if (mBottomLeftRadius <= 0 && mBottomRightRadius <= 0 && mTopRightRadius <= 0 && mTopLeftRadius <= 0)
        {
            mView.drawSuper(canvas);
            return;
        }
        
        int width = mView.getWidth();
        int height = mView.getHeight();
        
        int count = canvas.save();
        int count2 = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        
        addRoundPath(width, height);
        mView.drawSuper(canvas);
        canvas.drawPath(mPath, mPaint);
        canvas.restoreToCount(count2);
        canvas.restoreToCount(count);
        
    }
    
    private void addRoundPath(int width, int height)
    {
        
        if (mTopLeftRadius > 0)
        {
            Path topLeftPath = new Path();
            topLeftPath.moveTo(0, mTopLeftRadius);
            topLeftPath.lineTo(0, 0);
            topLeftPath.lineTo(mTopLeftRadius, 0);
            RectF arc = new RectF(0, 0, mTopLeftRadius * 2, mTopLeftRadius * 2);
            topLeftPath.arcTo(arc, -90, -90);
            topLeftPath.close();
            mPath.addPath(topLeftPath);
        }
        
        if (mTopRightRadius > 0)
        {
            Path topRightPath = new Path();
            topRightPath.moveTo(width, mTopRightRadius);
            topRightPath.lineTo(width, 0);
            topRightPath.lineTo(width - mTopRightRadius, 0);
            RectF arc = new RectF(width - mTopRightRadius * 2, 0, width, mTopRightRadius * 2);
            topRightPath.arcTo(arc, -90, 90);
            topRightPath.close();
            mPath.addPath(topRightPath);
        }
        
        if (mBottomLeftRadius > 0)
        {
            Path bottomLeftPath = new Path();
            bottomLeftPath.moveTo(0, height - mBottomLeftRadius);
            bottomLeftPath.lineTo(0, height);
            bottomLeftPath.lineTo(mBottomLeftRadius, height);
            RectF arc = new RectF(0, height - mBottomLeftRadius * 2, mBottomLeftRadius * 2, height);
            bottomLeftPath.arcTo(arc, 90, 90);
            bottomLeftPath.close();
            mPath.addPath(bottomLeftPath);
        }
        
        if (mBottomRightRadius > 0)
        {
            Path bottomRightPath = new Path();
            bottomRightPath.moveTo(width - mBottomRightRadius, height);
            bottomRightPath.lineTo(width, height);
            bottomRightPath.lineTo(width, height - mBottomRightRadius);
            RectF arc = new RectF(width - mBottomRightRadius * 2, height - mBottomRightRadius * 2, width, height);
            bottomRightPath.arcTo(arc, 0, 90);
            bottomRightPath.close();
            
            mPath.addPath(bottomRightPath);
        }
    }
}
