package com.xiaoxuan.demo.tv.widget;

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
public class TimeView extends TextView
{
    Calendar mCalendar;
    
    private final static String format = "hh:mm";
    
    private FormatChangeObserver mFormatChangeObserver;
    
    private Runnable mTicker;
    
    private Handler mHandler;
    
    private boolean mTickerStopped = false;
    
    String mFormat;
    
    public TimeView(Context context)
    {
        super(context);
        initClock(context);
    }
    
    public TimeView(Context context, AttributeSet attrs)
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
    
    /**
     * Pulls 12/24 mode from system settings
     */
    @SuppressWarnings("unused")
    private boolean get24HourMode()
    {
        return DateFormat.is24HourFormat(getContext());
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
