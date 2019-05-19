package com.example.mifans.eif.Activities;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
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
import com.example.mifans.eif.Tools.HttpUtil;
import com.example.mifans.eif.Tools.HttpUtilListener;
import com.example.mifans.eif.Tools.MyImageLoader;
import com.example.mifans.eif.other.ControlType;
import com.example.mifans.eif.other.RefreshReceiverType;
import com.example.mifans.eif.other.Songbean;

import org.json.JSONException;
import org.json.JSONObject;

public class MoreActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    TextView songNameTv;
    TextView singerNameTv;
    TextView lyrics;
    ImageView coverPic;
    ImageView moveBack;//前一首
    ImageView moveforward;//后一首
    ImageView pause;//暂停
    ImageView like;
    ImageView collect;
    Songbean songbean;
    ControlType controlType = null;
    private boolean isruning = true;
    IntentFilter intentFilter;
    RefreshReceiver refreshReceiver;
    SeekBar mseekBar;
    private MyDataBaseHelper databaseHelper = new MyDataBaseHelper(MoreActivity.this, "User.db", null, 1);
    SQLiteDatabase database;
    private int iscollect = 0;//
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        initViews();
        initSongInfo();
        clicks();

        cursor = database.query("Collect", new String[]{"songid"}, "songid = ?", new String[]{songbean.getId()}, null, null, null);
        if (cursor.moveToFirst()) {
            //cursor不为空说明收藏记录里存在该信息
            iscollect = 1;
        }
        if (iscollect == 1) {
            collect.setImageResource(R.drawable.ic_star_on);
        }

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
        intentFilter = new IntentFilter("refresh");
        refreshReceiver = new RefreshReceiver();
        registerReceiver(refreshReceiver, intentFilter);

        collect = findViewById(R.id.collect_ib);
        like = findViewById(R.id.like_ib);
        mseekBar = findViewById(R.id.seek_bar);
        //mseekBar.setOnSeekBarChangeListener(this);
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

    public void initSongInfo() {
        Intent intent = getIntent();
        songbean = (Songbean) intent.getSerializableExtra("info");
        songNameTv.setText(songbean.getSongName());
        singerNameTv.setText(songbean.getSingerName());
        MyImageLoader.with(MoreActivity.this).into(coverPic)
                .placeholder(R.drawable.ic_default_bottom_music_icon)
                .load(songbean.getCoverUrl());
        loadLyrics();
    }

    //加载歌词
    public void loadLyrics() {
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
                    loadLyrics();
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
        unregisterReceiver(refreshReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.move_back:
                controlType = ControlType.BACK;
                Intent back = new Intent("controlplay");
                back.putExtra("control", controlType);
                sendBroadcast(back);
                break;
            case R.id.move_forward:
                controlType = ControlType.FORWARD;
                Intent forward = new Intent("controlplay");
                forward.putExtra("control", controlType);
                sendBroadcast(forward);
                break;
            case R.id.play_running:
                SharedPreferences preferences = getSharedPreferences("reverse", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                isruning = preferences.getBoolean("runing?", true);
                if (isruning) {
                    editor.putBoolean("runing?", false);
                    editor.apply();
                    pause.setImageResource(R.drawable.ic_play_pause);
                    controlType = ControlType.PAUSE;
                    Intent mpause = new Intent("controlplay");
                    mpause.putExtra("control", controlType);
                    sendBroadcast(mpause);
                } else {
                    editor.putBoolean("runing?", true);
                    editor.apply();
                    pause.setImageResource(R.drawable.ic_play_running);
                    controlType = ControlType.CONTINUE;
                    Intent mcontinue = new Intent("controlplay");
                    mcontinue.putExtra("control", controlType);
                    sendBroadcast(mcontinue);
                }
                break;
            case R.id.like_ib:
                SharedPreferences preferencesLike = getSharedPreferences("reverse", MODE_PRIVATE);
                SharedPreferences.Editor editorLike = preferencesLike.edit();
                boolean islike = preferencesLike.getBoolean("like?", false);
                if (!islike) {
                    like.setImageResource(R.drawable.ic_like_on);
                    editorLike.putBoolean("like?", true);
                    editorLike.apply();
                } else {
                    like.setImageResource(R.drawable.ic_like_off_blue);
                    editorLike.putBoolean("like?", false);
                    editorLike.apply();
                }
                break;
            case R.id.collect_ib:

                if (cursor.moveToFirst()) {
                    iscollect = 1;
                }
                switch (iscollect) {
                    case 0:

                        collect.setImageResource(R.drawable.ic_star_on);
                        //如果收藏就将信息保存进collect table

                        ContentValues values = new ContentValues();
                        values.put("songid", songbean.getId());
                        values.put("songname", songbean.getSongName());
                        values.put("singer", songbean.getSingerName());
                        values.put("coverurl", songbean.getCoverUrl());
                        database.insert("Collect", null, values);
                        Toast.makeText(MoreActivity.this, "收藏成功，可前往我的收藏查看", Toast.LENGTH_SHORT).show();
//                    editor.putBoolean("collect?", false);
//                    editor.apply();
                        break;

                    case 1:
                        collect.setImageResource(R.drawable.ic_star_off_blue);
                        //取消收藏将该条信息从数据库删除
                        database = databaseHelper.getWritableDatabase();
                        //根据musicid删除数据
                        database.delete("Collect", "songid = ?", new String[]{songbean.getId()});
                        Toast.makeText(MoreActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
//                    editor.putBoolean("collect?", true);
//                    editor.apply();
                        break;
                    default:
                        break;
                }


            default:
                break;


        }

    }

//    //拖动seekbar控制进度
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//
//    }
//
//    @Override
//    //停止拖动时改变音乐进度
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        Intent intent = new Intent("controlplay");
//        intent.putExtra("control",ControlType.DRAG);
//        intent.putExtra("progress",seekBar.getProgress());
//        sendBroadcast(intent);
//    }

    class RefreshReceiver extends BroadcastReceiver {
        //切换上下曲刷新广播
        @Override
        public void onReceive(Context context, Intent intent) {
            //RefreshReceiverType refreshReceiverType = (RefreshReceiverType) intent.getSerializableExtra("refreshtype");
//            switch (refreshReceiverType){
//                case CUTOVER:
            songbean = (Songbean) intent.getSerializableExtra("refreshview");
            singerNameTv.setText(songbean.getSingerName());
            songNameTv.setText(songbean.getSongName());
            MyImageLoader.with(MoreActivity.this).into(coverPic)
                    .placeholder(R.drawable.ic_default_bottom_music_icon)
                    .load(songbean.getCoverUrl());
            loadLyrics();
//                    break;
//                case PREPARE:
//                    int max = intent.getIntExtra("musiclength",0);
//                    Log.d("musiclength", String.valueOf(max));
//                    mseekBar.setMax(max);
//                    break;
//                default:
//                    break;
//            }


        }
    }
}
