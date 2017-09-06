package com.xiaoxuan.demo.tv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoxuan.demo.androidtvdemoxx.R;
import com.xiaoxuan.demo.tv.widget.shimmer.BorderView;
import com.xiaoxuan.demo.tv.widget.shimmer.FocusBorder;
import com.xiaoxuan.demo.tv.widget.view.IjkVideoView;
import com.xiaoxuan.demo.tv.widget.view.MarqueeText;
import com.xiaoxuan.demo.tv.widget.layout.RoundedFrameLayout;
import com.xiaoxuan.demo.tv.widget.view.ScrollTextView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

/**
 * @author xiaoxuan 2017.9.4 首页
 */
public class MainActivity extends AppCompatActivity
    implements View.OnFocusChangeListener, FocusBorder.OnFocusCallback, TracksFragment.ITrackHolder
{
    // 滚动字幕控件
    private MarqueeText mt1;
    
    // 滚动字幕控件
    private MarqueeText mt2;
    
    // 滚动字幕控件
    private MarqueeText mt3;
    
    // 滚动字幕控件
    private MarqueeText mt4;
    
    // 滚动字幕控件
    private MarqueeText mt5;
    
    // 滚动字幕控件
    private MarqueeText mt6;
    
    // 流光特效
    private FocusBorder mFocusBorder;
    
    // 有线网络连接图标
    private ImageView mNetConnectImg;
    
    // wifi连接图标
    private ImageView mWifiImg;
    
    // 连接类型 0-wifi 1-net
    private boolean isNetType;
    
    private IjkVideoView mVideoView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNetConnectImg = (ImageView)findViewById(R.id.iv_net);
        mWifiImg = (ImageView)findViewById(R.id.iv_wifi);
        mVideoView = (IjkVideoView)findViewById(R.id.video_view);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
        // 实例话流光特效控件
        mFocusBorder = new FocusBorder.Builder().asColor()
            .shadowWidth(TypedValue.COMPLEX_UNIT_DIP, 18f)
            .borderColor(getResources().getColor(R.color.white))
            .build(this);
        // 绑定流光特效回调
        mFocusBorder.boundGlobalFocusListener(this);
        BorderView border = new BorderView(this);
        border.setBackgroundResource(R.drawable.border_drawable);
        ViewGroup list = (ViewGroup)findViewById(R.id.tv_relativelayout_list);
        border.attachTo(list);
        final RoundedFrameLayout rf1 = (RoundedFrameLayout)findViewById(R.id.rf_1);
        rf1.setOnFocusChangeListener(this);
        // 确保首页首项获取焦点
        rf1.post(new Runnable()
        {
            @Override
            public void run()
            {
                rf1.setFocusable(true);
                rf1.requestFocus();
            }
        });
        RoundedFrameLayout rf2 = (RoundedFrameLayout)findViewById(R.id.rf_2);
        rf2.setOnFocusChangeListener(this);
        RoundedFrameLayout rf3 = (RoundedFrameLayout)findViewById(R.id.rf_3);
        rf3.setOnFocusChangeListener(this);
        RoundedFrameLayout rf4 = (RoundedFrameLayout)findViewById(R.id.rf_4);
        rf4.setOnFocusChangeListener(this);
        RoundedFrameLayout rf5 = (RoundedFrameLayout)findViewById(R.id.rf_5);
        rf5.setOnFocusChangeListener(this);
        RoundedFrameLayout rf6 = (RoundedFrameLayout)findViewById(R.id.rf_6);
        rf6.setOnFocusChangeListener(this);
        mt1 = (MarqueeText)findViewById(R.id.mt_1);
        mt2 = (MarqueeText)findViewById(R.id.mt_2);
        mt3 = (MarqueeText)findViewById(R.id.mt_3);
        mt4 = (MarqueeText)findViewById(R.id.mt_4);
        mt5 = (MarqueeText)findViewById(R.id.mt_5);
        mt6 = (MarqueeText)findViewById(R.id.mt_6);
        ScrollTextView scrollingView2 = (ScrollTextView)findViewById(R.id.sv_message);
        scrollingView2.setText(
            "欢迎加入AndroidTV应用开发者qq群，群号:257251953，我们的前身就是著名的神马tv视频launcher的开发群，我们也是业内首个开源tv launcher源代码的团体，目前群内拥有业内许多优秀的开发者，如果您是做机顶盒（DVB、OTT、TVOS）、电视、交互媒体、以及各种android、android tv 的爱好者均可以加入我们，期待您的加入   ---作者---小轩  2017.09.04 ");
        scrollingView2.setClickable(true);
        scrollingView2.setSpeed(2);
        scrollingView2.setTimes(1314);
        playVideo(
            "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_zxc_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1");
    }
    
    @Override
    public void onFocusChange(View view, boolean hasFocus)
    {
        switch (view.getId())
        {
            case R.id.rf_1:
                scrollAnimation(hasFocus, mt1);
                break;
            case R.id.rf_2:
                scrollAnimation(hasFocus, mt2);
                break;
            case R.id.rf_3:
                scrollAnimation(hasFocus, mt3);
                break;
            case R.id.rf_4:
                scrollAnimation(hasFocus, mt4);
                break;
            case R.id.rf_5:
                scrollAnimation(hasFocus, mt5);
                break;
            case R.id.rf_6:
                scrollAnimation(hasFocus, mt6);
                break;
            default:
                break;
        }
    }
    
    @Override
    public FocusBorder.Options onFocus(View oldFocus, View newFocus)
    {
        if (newFocus != null && oldFocus != null)
        {
            switch (newFocus.getId())
            {
                case R.id.rf_1:
                    return FocusBorder.OptionsFactory.get(1.1f, 1.1f, 0);
                case R.id.rf_2:
                    return FocusBorder.OptionsFactory.get(1.1f, 1.1f, 0);
                case R.id.rf_3:
                    return FocusBorder.OptionsFactory.get(1.1f, 1.1f, 0);
                case R.id.rf_4:
                    return FocusBorder.OptionsFactory.get(1.1f, 1.1f, 0);
                case R.id.rf_5:
                    return FocusBorder.OptionsFactory.get(1.1f, 1.1f, 0);
                case R.id.rf_6:
                    return FocusBorder.OptionsFactory.get(1.1f, 1.1f, 0);
                default:
                    break;
            }
            mFocusBorder.setVisible(false);
        }
        return null;
    }
    
    // 滚动动画实例
    private void scrollAnimation(boolean hasFocus, MarqueeText view)
    {
        if (hasFocus)
        {
            view.startScroll();
        }
        else
        {
            view.stopScroll();
        }
    }
    
    /**
     * 播放视频
     *
     * @param url 直播地址
     */
    private void playVideo(String url)
    {
        if (url != null)
        {
            mVideoView.pause();
            mVideoView.setVideoPath(url);
        }
        else
        {
            finish();
            return;
        }
        mVideoView.start();
    }
    
    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
            {
                ConnectivityManager connectMgr = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected())
                {
                    // 没有网络
                    mNetConnectImg.setVisibility(View.INVISIBLE);
                    isNetType = false;
                }
                else
                {
                    mNetConnectImg.setVisibility(View.VISIBLE);
                    isNetType = true;
                }
            }
            // wifi图标
            else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
            {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED))
                {
                    mWifiImg.setVisibility(View.INVISIBLE);
                }
                else if (info.getState().equals(NetworkInfo.State.CONNECTED))
                {
                    if (isNetType)
                    {
                        mWifiImg.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        mWifiImg.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    };
    
    @Override
    protected void onStop()
    {
        super.onStop();
        
        if (!mVideoView.isBackgroundPlayEnabled())
        {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        }
        else
        {
            mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }
    
    @Override
    public ITrackInfo[] getTrackInfo()
    {
        if (mVideoView == null)
            return null;
        
        return mVideoView.getTrackInfo();
    }
    
    @Override
    public void selectTrack(int stream)
    {
        mVideoView.selectTrack(stream);
    }
    
    @Override
    public void deselectTrack(int stream)
    {
        mVideoView.deselectTrack(stream);
    }
    
    @Override
    public int getSelectedTrack(int trackType)
    {
        if (mVideoView == null)
            return -1;
        
        return mVideoView.getSelectedTrack(trackType);
    }
    
    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.activity_up_in, R.anim.activity_up_out);
    }
}
