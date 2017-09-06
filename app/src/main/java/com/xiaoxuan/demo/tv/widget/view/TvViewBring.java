package com.xiaoxuan.demo.tv.widget.view;

import android.view.View;
import android.view.ViewGroup;

public class TvViewBring
{
    
    private int position = 0;
    
    public TvViewBring()
    {
    }
    
    public TvViewBring(ViewGroup vg)
    {
        vg.setClipChildren(false);
        vg.setClipToPadding(false);
    }
    
    public void bringChildToFront(ViewGroup vg, View child)
    {
        position = vg.indexOfChild(child);
        if (position != -1)
        {
            vg.postInvalidate();
        }
    }
    
    /**
     * 此函数 dispatchDraw 中调用. <br>
     * 原理就是和最后一个要绘制的view，交换了位置. <br>
     * 因为dispatchDraw最后一个绘制的view是在最上层的. <br>
     * 这样就避免了使用 bringToFront 导致焦点错乱问题. <br>
     */
    public int getChildDrawingOrder(int childCount, int i)
    {
        if (position < 0)
        {
            return i;
        }
        if (i < (childCount - 1))
        {
            if (position == i)
                i = childCount - 1;
        }
        else
        {
            if (position < childCount)
                i = position;
        }
        return i;
    }
    
}
