package com.xiaoxuan.demo.tv;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoxuan.demo.androidtvdemoxx.R;
import com.xiaoxuan.demo.tv.adapter.BaseRecyclerViewAdapter;
import com.xiaoxuan.demo.tv.adapter.BaseRecyclerViewHolder;
import com.xiaoxuan.demo.tv.brdge.RecyclerViewBridge;
import com.xiaoxuan.demo.tv.modle.RecentMediaStorage;
import com.xiaoxuan.demo.tv.modle.Settings;
import com.xiaoxuan.demo.tv.modle.VideoBean;
import com.xiaoxuan.demo.tv.widget.CustomMediaController;
import com.xiaoxuan.demo.tv.widget.view.IjkVideoView;
import com.xiaoxuan.demo.tv.widget.layout.TvLinearLayoutManager;
import com.xiaoxuan.demo.tv.widget.view.MainUpView;
import com.xiaoxuan.demo.tv.widget.OnChildSelectedListener;
import com.xiaoxuan.demo.tv.widget.view.TvRecyclerView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

/**
 * 直播播放器
 */
public class LiveVideoActivity extends AppCompatActivity
    implements TracksFragment.ITrackHolder, View.OnFocusChangeListener
{
    private static final String TAG = "LiveVideoActivity";
    
    private String mVideoPath;
    
    private Uri mVideoUri;
    
    private CustomMediaController customMediaController;
    
    private IjkVideoView mVideoView;
    
    private TvRecyclerView videoList;
    
    private ViewGroup mRightDrawer;
    
    private TextView tips, liveName;
    
    /** 播放指示器 **/
    private int playIndex = 0;
    
    private View oldView;
    
    private MyAdapter myAdapter;
    
    private MainUpView mainUpView1;
    
    private RecyclerViewBridge mRecyclerViewBridge;
    
    private Settings mSettings;
    
    private boolean mBackPressed;
    
    private List<VideoBean> datas = new ArrayList<>();;
    
    private String[] names = new String[] {"香港电影", "综艺频道", "高清音乐", "动作电影", "电影", "周星驰", "成龙", "喜剧", "儿歌", "LIVE生活"};
    
    private String[] urls = new String[] {
        "http://live.gslb.letv.com/gslb?stream_id=lb_hkmovie_1300&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=letv&expect=1",
        "http://live.gslb.letv.com/gslb?stream_id=lb_ent_1300&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=letv&expect=1",
        "http://live.gslb.letv.com/gslb?stream_id=lb_music_1300&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=letv&expect=1",
        "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_dzdy_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1",
        "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_movie_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1",
        "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_zxc_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1",
        "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_cl_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1",
        "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_comedy_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1",
        "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_erge_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1",
        "http://live.gslb.letv.com/gslb?tag=live&stream_id=lb_livemusic_720p&tag=live&ext=m3u8&sign=live_tv&platid=10&splatid=1009&format=C1S&expect=1"};
    
    public static Intent newIntent(Context context, String videoPath, String videoTitle, int index)
    {
        Intent intent = new Intent(context, LiveVideoActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("videoTitle", videoTitle);
        intent.putExtra("index", index);
        return intent;
    }
    
    public static void intentTo(Context context, String videoPath, String videoTitle, int index)
    {
        context.startActivity(newIntent(context, videoPath, videoTitle, index));
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_video);
        initTiemData();
        initVideoList();
        mSettings = new Settings(this);
        
        // handle arguments
        mVideoPath = getIntent().getStringExtra("videoPath");
        playIndex = getIntent().getIntExtra("index", 0);
        mVideoPath = urls[playIndex];
        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(mVideoPath))
        {
            new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
        }
        
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        tips = (TextView)findViewById(R.id.tips);
        liveName = (TextView)findViewById(R.id.liveName);
        mVideoView = (IjkVideoView)findViewById(R.id.video_view);
        playVideo(mVideoPath, playIndex);
    }
    
    public void initTiemData()
    {
        for (int i = 0; i < 10; i++)
        {
            VideoBean videoBean = new VideoBean();
            videoBean.setTvName(names[i]);
            videoBean.setTvUrl(urls[i]);
            datas.add(videoBean);
        }
    }
    
    private void initVideoList()
    {
        videoList = (TvRecyclerView)findViewById(R.id.videoList);
        
        mainUpView1 = (MainUpView)findViewById(R.id.mainUpView);
        mainUpView1.setEffectBridge(new RecyclerViewBridge());
        mRecyclerViewBridge = (RecyclerViewBridge)mainUpView1.getEffectBridge();
        mRecyclerViewBridge.setUpRectResource(R.drawable.item_rectangle);
        mRecyclerViewBridge.setTranDurAnimTime(200);
        mRecyclerViewBridge.setShadowResource(R.drawable.item_shadow);
        
        TvLinearLayoutManager linearLayoutManager = new TvLinearLayoutManager(LiveVideoActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        videoList.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOnChildSelectedListener(new OnChildSelectedListener()
        {
            @Override
            public void onChildSelected(RecyclerView parent, View focusview, int position, int dy)
            {
                focusview.bringToFront();
                if (oldView == null)
                {
                    Log.d("danxx", "oldView == null");
                }
                mRecyclerViewBridge.setFocusView(focusview, oldView, 1.1f);
                oldView = focusview;
            }
        });
        
        myAdapter = new MyAdapter();
        myAdapter.setData(datas);
        videoList.setAdapter(myAdapter);
        videoList.setFocusable(false);
        myAdapter.notifyDataSetChanged();
        myAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(int position, Object data)
            {
                String url = ((VideoBean)data).getTvUrl();
                playVideo(url, position);
                if (videoList.getVisibility() == View.VISIBLE)
                {
                    videoList.setVisibility(View.INVISIBLE);
                    tips.setVisibility(View.VISIBLE);
                    /** 隐藏焦点 **/
                    mRecyclerViewBridge.setVisibleWidget(true);
                }
            }
            
            @Override
            public void onItemLongClick(int position, Object data)
            {
                
            }
        });
    }
    
    /**
     * 播放视频
     * 
     * @param url 直播地址
     */
    private void playVideo(String url, int index)
    {
        playIndex = index;
        liveName.setText(myAdapter.getItemData(playIndex).getTvName());
        if (url != null)
        {
            mVideoView.pause();
            mVideoView.setVideoPath(url);
        }
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
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (KeyEvent.KEYCODE_DPAD_CENTER == keyCode || KeyEvent.KEYCODE_ENTER == keyCode)
        {
            if (videoList.getVisibility() != View.VISIBLE)
            {
                videoList.setVisibility(View.VISIBLE);
                tips.setVisibility(View.INVISIBLE);
                mRecyclerViewBridge.setVisibleWidget(false);
                videoList.requestFocus();
            }
        }
        else if (KeyEvent.KEYCODE_BACK == keyCode)
        {
            if (videoList.getVisibility() == View.VISIBLE)
            {
                videoList.setVisibility(View.INVISIBLE);
                tips.setVisibility(View.VISIBLE);
                mRecyclerViewBridge.setVisibleWidget(true);
                return true;
            }
        }
        else if (KeyEvent.KEYCODE_MENU == keyCode)
        {
            if (videoList.getVisibility() != View.VISIBLE)
            {
                videoList.setVisibility(View.VISIBLE);
                tips.setVisibility(View.INVISIBLE);
                videoList.requestFocus();
                mRecyclerViewBridge.setVisibleWidget(false);
            }
        }
        return super.onKeyDown(keyCode, event);
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
    
    /**
     * Called when the focus state of a view has changed.
     *
     * @param v The view whose state has changed.
     * @param hasFocus The new focus state of v.
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        mRecyclerViewBridge.setFocusView(v, oldView, 1.0f);
        oldView = v;
    }
    
    class MyAdapter extends BaseRecyclerViewAdapter<VideoBean>
    {
        
        @Override
        protected BaseRecyclerViewHolder createItem(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(LiveVideoActivity.this).inflate(R.layout.item_live, null);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }
        
        @Override
        protected void bindData(BaseRecyclerViewHolder holder, int position)
        {
            ((MyViewHolder)holder).name.setText(getItemData(position).getTvName());
        }
        
        class MyViewHolder extends BaseRecyclerViewHolder
        {
            TextView name;
            
            public MyViewHolder(View itemView)
            {
                super(itemView);
                name = (TextView)itemView.findViewById(R.id.name);
            }
            
            @Override
            protected View getView()
            {
                return null;
            }
        }
    }
}
