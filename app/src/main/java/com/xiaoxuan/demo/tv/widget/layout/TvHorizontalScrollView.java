package com.xiaoxuan.demo.tv.widget.layout;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import com.xiaoxuan.demo.androidtvdemoxx.R;

public class TvHorizontalScrollView extends HorizontalScrollView
{
    
    public TvHorizontalScrollView(Context context)
    {
        this(context, null, 0);
    }
    
    public TvHorizontalScrollView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }
    
    public TvHorizontalScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect)
    {
        if (getChildCount() == 0)
            return 0;
        
        int width = getWidth();
        int screenLeft = getScrollX();
        int screenRight = screenLeft + width;
        
        int fadingEdge = this.getResources().getDimensionPixelSize(R.dimen.fading_edge);
        
        if (rect.left > 0)
        {
            screenLeft += fadingEdge;
        }
        
        if (rect.right < getChildAt(0).getWidth())
        {
            screenRight -= fadingEdge;
        }
        
        int scrollXDelta = 0;
        
        if (rect.right > screenRight && rect.left > screenLeft)
        {
            if (rect.width() > width)
            {
                scrollXDelta += (rect.left - screenLeft);
            }
            else
            {
                scrollXDelta += (rect.right - screenRight);
            }
            
            int right = getChildAt(0).getRight();
            int distanceToRight = right - screenRight;
            scrollXDelta = Math.min(scrollXDelta, distanceToRight);
            
        }
        else if (rect.left < screenLeft && rect.right < screenRight)
        {
            if (rect.width() > width)
            {
                scrollXDelta -= (screenRight - rect.right);
            }
            else
            {
                scrollXDelta -= (screenLeft - rect.left);
            }
            
            scrollXDelta = Math.max(scrollXDelta, -getScrollX());
        }
        return scrollXDelta;
    }
    
}
