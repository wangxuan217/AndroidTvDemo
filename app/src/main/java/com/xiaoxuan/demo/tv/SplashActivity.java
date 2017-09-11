package com.xiaoxuan.demo.tv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.xiaoxuan.demo.androidtvdemoxx.R;
import com.xiaoxuan.demo.tv.widget.view.CountDownProgress;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiaoxuan on 2017/9/11. 欢迎引导页
 */

public class SplashActivity extends Activity
{
    // 滚动流光控件
    private ShimmerTextView mShi_TextView;
    
    // 效果控制
    private Shimmer mShimmer;
    
    // 倒计时
    CountDownProgress mCountDownProgress;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mShi_TextView = (ShimmerTextView)findViewById(R.id.shimmer_tv);
        mCountDownProgress = (CountDownProgress)findViewById(R.id.countDownProgress);
        mCountDownProgress.startCountDownTime();
        if (mShimmer != null && mShimmer.isAnimating())
        {
            mShimmer.cancel();
        }
        else
        {
            mShimmer = new Shimmer();
            mShimmer.setDuration(8000);
            mShimmer.start(mShi_TextView);
        }
        // 设置延时跳转
        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                // execute the task
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 8000);
        
    }
}
