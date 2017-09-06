package com.xiaoxuan.demo.tv;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.xiaoxuan.demo.androidtvdemoxx.R;
import com.xiaoxuan.demo.tv.modle.RecentMediaStorage;
import com.xiaoxuan.demo.tv.modle.Settings;
import com.xiaoxuan.demo.tv.widget.CustomMediaController;
import com.xiaoxuan.demo.tv.widget.view.IjkVideoView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

/**
 * 全屏播放器
 */
public class FullScreenVideoActivity extends AppCompatActivity implements TracksFragment.ITrackHolder
{
    private static final String TAG = "FullScreenVideoActivity";
    
    private String mVideoPath;
    
    private Uri mVideoUri;
    
    private CustomMediaController customMediaController;
    
    private IjkVideoView mVideoView;
    
    private TextView mToastTextView;
    
    private TableLayout mHudView;
    
    private DrawerLayout mDrawerLayout;
    
    private ViewGroup mRightDrawer;
    
    private Settings mSettings;
    
    private boolean mBackPressed;
    
    public static Intent newIntent(Context context, String videoPath, String videoTitle)
    {
        Intent intent = new Intent(context, FullScreenVideoActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("videoTitle", videoTitle);
        return intent;
    }
    
    public static void intentTo(Context context, String videoPath, String videoTitle)
    {
        context.startActivity(newIntent(context, videoPath, videoTitle));
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        
        mSettings = new Settings(this);
        
        // handle arguments
        mVideoPath = getIntent().getStringExtra("videoPath");
        
        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(intentAction))
        {
            if (intentAction.equals(Intent.ACTION_VIEW))
            {
                mVideoPath = intent.getDataString();
            }
            else if (intentAction.equals(Intent.ACTION_SEND))
            {
                mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                {
                    String scheme = mVideoUri.getScheme();
                    if (TextUtils.isEmpty(scheme))
                    {
                        Log.e(TAG, "Null unknown ccheme\n");
                        finish();
                        return;
                    }
                    if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE))
                    {
                        mVideoPath = mVideoUri.getPath();
                    }
                    else if (scheme.equals(ContentResolver.SCHEME_CONTENT))
                    {
                        Log.e(TAG, "Can not resolve content below Android-ICS\n");
                        finish();
                        return;
                    }
                    else
                    {
                        Log.e(TAG, "Unknown scheme " + scheme + "\n");
                        finish();
                        return;
                    }
                }
            }
        }
        
        if (!TextUtils.isEmpty(mVideoPath))
        {
            new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
        }
        
        // init UI
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        ActionBar actionBar = getSupportActionBar();
        customMediaController = new CustomMediaController(this, false);
        customMediaController.setSupportActionBar(actionBar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        mToastTextView = (TextView)findViewById(R.id.toast_text_view);
        mHudView = (TableLayout)findViewById(R.id.hud_view);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mRightDrawer = (ViewGroup)findViewById(R.id.right_drawer);
        
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        
        mVideoView = (IjkVideoView)findViewById(R.id.video_view);
        mVideoView.setMediaController(customMediaController);
        mVideoView.setHudView(mHudView);
        // prefer mVideoPath
        if (mVideoPath != null)
            mVideoView.setVideoPath(mVideoPath);
        else if (mVideoUri != null)
            mVideoView.setVideoURI(mVideoUri);
        else
        {
            Log.e(TAG, "Null Data Source\n");
            finish();
            return;
        }
        mVideoView.start();
    }
    
    @Override
    public void onBackPressed()
    {
        mBackPressed = true;
        finish();
        super.onBackPressed();
    }
    
    @Override
    protected void onStop()
    {
        super.onStop();
        
        if (mBackPressed || !mVideoView.isBackgroundPlayEnabled())
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
