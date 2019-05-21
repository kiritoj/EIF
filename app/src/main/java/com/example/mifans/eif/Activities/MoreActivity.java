package com.example.mifans.eif.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mifans.eif.R;
import com.example.mifans.eif.Service.MusicService;
import com.example.mifans.eif.Tools.HttpUtil;
import com.example.mifans.eif.Tools.HttpUtilListener;
import com.example.mifans.eif.Tools.MyImageLoader;
import com.example.mifans.eif.interfaces.UpdateMusicInfo;
import com.example.mifans.eif.interfaces.UpdateProgress;
import com.example.mifans.eif.other.ControlType;

import com.example.mifans.eif.other.Songbean;

import org.json.JSONException;
import org.json.JSONObject;

public class MoreActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener {

    public boolean islike = false;//点赞标志位
    private Toolbar toolbar;
    TextView songNameTv;
    TextView singerNameTv;
    TextView lyrics;
    TextView mcurrentTime;
    TextView mendTime;
    ImageView coverPic;
    ImageView moveBack;//前一首
    ImageView moveforward;//后一首
    ImageView pause;//暂停
    ImageView like;
    ImageView collect;
    private Songbean msongbean;
    ControlType controlType = null;
    private boolean isruning = true;
    SeekBar mseekBar;
    private MyDataBaseHelper databaseHelper = new MyDataBaseHelper(MoreActivity.this, "User.db", null, 1);
    SQLiteDatabase database;
    private boolean iscollect = false;
    Cursor cursor;
    private MusicService.ControlBinder controlBinder;
    UpdateMusicInfo updateMusicInfo = new UpdateMusicInfo() {
        @Override
        public void update(final Songbean songbean) {
            msongbean = songbean;
            cursor = database.query("Collect", new String[]{"songid"}, "songid = ?", new String[]{msongbean.getId()}, null, null, null);
            if (cursor.moveToFirst()) {
                //cursor不为空说明收藏记录里存在该信息
                iscollect = true;
                collect.setImageResource(R.drawable.ic_star_on);
            }else{
                collect.setImageResource(R.drawable.ic_star_off_blue);
                iscollect = false;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initSongInfo(songbean);
                }
            });
            loadLyrics(songbean);
            Intent refresh = new Intent("refreshMainActivity");
            sendBroadcast(refresh);
        }
        @Override
        public void error() {

        }
    };
    UpdateProgress updateProgress = new UpdateProgress() {
        @Override
        public void update(int profress, int max, String currentTime, String endTime) {
            mseekBar.setMax(max);
            mseekBar.setProgress(profress);
            mcurrentTime.setText(currentTime);
            mendTime.setText(endTime);
        }




        @Override
        public void error() {

        }
    };
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            controlBinder = (MusicService.ControlBinder) service;
            //服务绑定成功后更新界面
            if(msongbean==null) {
                controlBinder.getcurrent(updateMusicInfo);
                controlBinder.updateSeekBar(updateProgress, updateMusicInfo);
                isruning = controlBinder.isplaying();

                if (!isruning) {
                    pause.setImageResource(R.drawable.ic_play_pause);
                }
            }else {
                controlBinder.playSongBean(msongbean,updateMusicInfo);
                controlBinder.updateSeekBar(updateProgress,updateMusicInfo);
                iscollect = true;
                collect.setImageResource(R.drawable.ic_star_on);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        initViews();
        clicks();
        Intent intent1 = getIntent();
        msongbean = (Songbean) intent1.getSerializableExtra("songbean");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void clicks() {
        moveBack.setOnClickListener(this);
        moveforward.setOnClickListener(this);
        pause.setOnClickListener(this);
        like.setOnClickListener(this);
        collect.setOnClickListener(this);
    }

    private void initViews() {
        database = databaseHelper.getWritableDatabase();
        mcurrentTime = findViewById(R.id.current_time_tv);
        mendTime = findViewById(R.id.end_time_tv);

        collect = findViewById(R.id.collect_ib);
        like = findViewById(R.id.like_ib);
        mseekBar = findViewById(R.id.seek_bar);
        mseekBar.setOnSeekBarChangeListener(this);
        moveBack = findViewById(R.id.move_back);
        moveforward = findViewById(R.id.move_forward);
        pause = findViewById(R.id.play_running);
        lyrics = findViewById(R.id.lyrics_tv);
        songNameTv = findViewById(R.id.song_m_tv);
        singerNameTv = findViewById(R.id.singer_m_tv);
        coverPic = findViewById(R.id.song_pic);

        toolbar = findViewById(R.id.tool_bar_m);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    //初始化音乐信息
    public void initSongInfo(Songbean songbean) {

        songNameTv.setText(songbean.getSongName());
        singerNameTv.setText(songbean.getSingerName());
        MyImageLoader.with(MoreActivity.this).into(coverPic)
                .placeholder(R.drawable.ic_default_bottom_music_icon)
                .load(songbean.getCoverUrl());

    }

    //加载歌词
    public void loadLyrics(Songbean songbean) {
        HttpUtil.sendHttpRequest("http://elf.egos.hosigus.com/music/lyric/application/x-www-form-urlencoded?id=" + songbean.getId(), new HttpUtilListener() {
            @Override
            public void success(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject object = jsonObject.getJSONObject("lrc");
                    final String mlyrics = object.getString("lyric");
                    Log.d("ceshigeci", mlyrics);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lyrics.setText(mlyrics);
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void failed() {
                Log.d("loadLyrics", "MoreActivity.loadLyrics error");
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;

        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.move_back:
                if (controlBinder != null) {
                    controlBinder.playPre(updateMusicInfo);
                }
                isruning = true;
                pause.setImageResource(R.drawable.ic_play_running);
                break;
            case R.id.move_forward:
                if (controlBinder != null) {
                    controlBinder.playNext(updateMusicInfo);
                }
                isruning = true;
                pause.setImageResource(R.drawable.ic_play_running);
                break;
            case R.id.play_running:
                if (isruning) {
                    if (controlBinder != null) {
                        controlBinder.playPause();
                    }
                    pause.setImageResource(R.drawable.ic_play_pause);
                    isruning = false;
                } else {
                    if (controlBinder != null) {
                        controlBinder.playContinue();
                    }
                    pause.setImageResource(R.drawable.ic_play_running);
                    isruning = true;
                }
                break;
            case R.id.like_ib:

                if (!islike) {
                    like.setImageResource(R.drawable.ic_like_on);
                    islike = true;
                } else {
                    like.setImageResource(R.drawable.ic_like_off_blue);
                    islike = false;
                }
                break;
            case R.id.collect_ib:


                if (iscollect == false) {
                    collect.setImageResource(R.drawable.ic_star_on);
                    //如果收藏就将信息保存进collect table

                    ContentValues values = new ContentValues();
                    values.put("songid", msongbean.getId());
                    values.put("songname", msongbean.getSongName());
                    values.put("singer", msongbean.getSingerName());
                    values.put("coverurl", msongbean.getCoverUrl());
                    database.insert("Collect", null, values);
                    iscollect = true;
                    Toast.makeText(MoreActivity.this, "收藏成功，可前往我的收藏查看", Toast.LENGTH_SHORT).show();


                } else {
                    collect.setImageResource(R.drawable.ic_star_off_blue);
                    //取消收藏将该条信息从数据库删除
                    database = databaseHelper.getWritableDatabase();
                    //根据musicid删除数据
                    database.delete("Collect", "songid = ?", new String[]{msongbean.getId()});
                    Toast.makeText(MoreActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                    iscollect = false;

                }


        }



        class RefreshReceiver extends BroadcastReceiver {
            //切换上下曲刷新广播
            @Override
            public void onReceive(Context context, Intent intent) {


            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (controlBinder!=null){
            controlBinder.changeProgress(seekBar.getProgress());
        }
    }
}
