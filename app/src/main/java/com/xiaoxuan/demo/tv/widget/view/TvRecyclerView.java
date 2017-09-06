package com.xiaoxuan.demo.tv.widget.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TvRecyclerView extends RecyclerView
{
    
    public TvRecyclerView(Context context)
    {
        super(context);
        init(context);
    }
    
    public TvRecyclerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public TvRecyclerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }
    
    TvViewBring mWidgetTvViewBring;
    
    private void init(Context context)
    {
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setHasFixedSize(true);
        setWillNotDraw(true);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setChildrenDrawingOrderEnabled(true);
        mWidgetTvViewBring = new TvViewBring(this);
    }
    
    @Override
    public void bringChildToFront(View child)
    {
        mWidgetTvViewBring.bringChildToFront(this, child);
    }
    
    @Override
    protected int getChildDrawingOrder(int childCount, int i)
    {
        Log.d("danxx", "childCount-->" + childCount);
        Log.d("danxx", "i-->" + i);
        return mWidgetTvViewBring.getChildDrawingOrder(childCount, i);
    }
    
}
