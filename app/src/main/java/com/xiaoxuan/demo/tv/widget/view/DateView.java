package com.xiaoxuan.demo.tv.widget.view;

import java.util.Calendar;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by mac on 17-6-2.
 *
 * @author xiaoxuan
 */
public class DateView extends TextView
{
    Calendar mCalendar;
    
    private final static String format = "yyyy/MM/dd\nE";
    
    private FormatChangeObserver mFormatChangeObserver;
    
    private Runnable mTicker;
    
    private Handler mHandler;
    
    private boolean mTickerStopped = false;
    
    String mFormat;
    
    public DateView(Context context)
    {
        super(context);
        initClock(context);
    }
    
    public DateView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initClock(context);
    }
    
    private void initClock(Context context)
    {
        if (mCalendar == null)
        {
            mCalendar = Calendar.getInstance();
        }
        
        mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(Settings.System.CONTENT_URI,
            true,
            mFormatChangeObserver);
        
        setFormat();
    }
    
    @Override
    protected void onAttachedToWindow()
    {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();
        
        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable()
        {
            @Override
            public void run()
            {
                if (mTickerStopped)
                    return;
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                setText(DateFormat.format(mFormat, mCalendar));
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }
    
    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }
    
    private void setFormat()
    {
        mFormat = format;
    }
    
    private class FormatChangeObserver extends ContentObserver
    {
        public FormatChangeObserver()
        {
            super(new Handler());
        }
        
        @Override
        public void onChange(boolean selfChange)
        {
            setFormat();
        }
    }
}
