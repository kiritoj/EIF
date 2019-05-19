package com.example.mifans.eif.Activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mifans.eif.CustomizeView.CircleView;
import com.example.mifans.eif.CustomizeView.MyEditView;
import com.example.mifans.eif.R;
import com.example.mifans.eif.Tools.HttpUtil;
import com.example.mifans.eif.Tools.HttpUtilListener;
import com.example.mifans.eif.Tools.MyImageLoader;
import com.example.mifans.eif.other.ControlType;
import com.example.mifans.eif.other.Myplayer;
import com.example.mifans.eif.other.RefreshReceiverType;
import com.example.mifans.eif.other.SongType;
import com.example.mifans.eif.other.Songbean;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static final int CHOOSE_PICTURE = 2;//相册选择图片
    private Toolbar toolbar;
    private NavigationView navigationView;
    DrawerLayout drawerLayout;
    ImageView head;
    ImageView more;
    ImageView happy;
    ImageView unhappy;
    ImageView clam;
    ImageView exciting;
    TextView name;
    TextView song;//歌曲名
    TextView singer;//歌手名
    TextView toolbarTitle;
    TextView lyrics;
    AlertDialog dialogName;
    CircleView songCover;
    ScrollView scrollView;
    ValueAnimator valueAnimator;
    List<Songbean> happysongs = new ArrayList<>();
    List<Songbean> unhappysongs = new ArrayList<>();
    List<Songbean> clamsongs = new ArrayList<>();
    List<Songbean> excitingsongs = new ArrayList<>();
    List<Songbean> temp = new ArrayList<>();
    private int happyindex = -1;
    private int unhappyindex = -1;
    private int clamindex = -1;
    private int excitingindex = -1;
    MediaPlayer player;
    SongType type = SongType.HAPPY;//开始进入默认为HAPPY类型的音乐
    Songbean msongBean = null;
    int currenttime=0;//动画进行的时间
    int currenttimemusic;//
    IntentFilter intentFilter;
    ControlPlayReceiver receiver;
    RefreshReceiverType refreshReceiverType = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("testlength", String.valueOf(happysongs.size()));
        initViews();
        initinfo();
        //各控件的点击事件
        click();
        getsongId(type);
        //注册控制广播
        intentFilter = new IntentFilter();
        intentFilter.addAction("controlplay");
        receiver = new ControlPlayReceiver();
        registerReceiver(receiver,intentFilter);

    }

    private void click(){
        lyrics.setOnClickListener(this);
        songCover.setOnClickListener(this);
        happy.setOnClickListener(this);
        unhappy.setOnClickListener(this);
        clam.setOnClickListener(this);
        exciting.setOnClickListener(this);
        name.setOnClickListener(this);
        head.setOnClickListener(this);
        more.setOnClickListener(this);
        happy = findViewById(R.id.happy);
        unhappy = findViewById(R.id.unhappy);
        clam = findViewById(R.id.clam);
        exciting = findViewById(R.id.exciting);
    }
    private void initViews() {

        toolbarTitle = findViewById(R.id.toolbar_title);
        lyrics = findViewById(R.id.hide_lyrics);
        scrollView = findViewById(R.id.scro_lyrics);
        player = new MediaPlayer();
//        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                Intent intent = new Intent("refresh");
//                intent.putExtra("refreshtype",RefreshReceiverType.PREPARE);
//                intent.putExtra("musiclength",mp.getDuration());
//                Log.d("musiclength", String.valueOf(mp.getDuration()));
//                sendBroadcast(intent);
//            }
//        });
        more = findViewById(R.id.more);
        happy = findViewById(R.id.happy);
        unhappy = findViewById(R.id.unhappy);
        clam = findViewById(R.id.clam);
        exciting = findViewById(R.id.exciting);
        song = findViewById(R.id.song);
        singer = findViewById(R.id.singer);
        songCover = findViewById(R.id.cir_cle);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.tool_bar_m);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        navigationView = findViewById(R.id.navigation);
        ColorStateList csl = getResources().getColorStateList(R.color.nav_menu_color);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemTextColor(csl);

        View headview = navigationView.getHeaderView(0);
        head = headview.findViewById(R.id.head);
        name = headview.findViewById(R.id.name);

        //状态栏白底黑字
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        valueAnimator = ObjectAnimator.ofFloat(songCover,"rotation",0,360);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setDuration(9000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
    }
    //加载首页的三个view
    public void loadinfo(Songbean songbean){
        if(songbean!=null){
            song.setText(songbean.getSongName());
            singer.setText(songbean.getSingerName());
            MyImageLoader.with(MainActivity.this).into(songCover)
                    .placeholder(R.drawable.ic_default_bottom_music_icon)
                    .load(songbean.getCoverUrl());
        }

    }

    private void initinfo() {
        SharedPreferences preferences = getSharedPreferences("name", MODE_PRIVATE);
        if (preferences != null) {
            name.setText(preferences.getString("nickname", "点击次数修改昵称"));
            String path = preferences.getString("head",null);
            if(path!=null){
                Log.d("myinitinfo",path);
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                head.setImageBitmap(bitmap);
            }else {
                Log.d("myinitinfo","path为空");
                head.setImageResource(R.drawable.ic_default_bottom_music_icon);
            }
        }
    }
    //获取歌单id
    public void getsongId(final SongType songType){
        HttpUtil.sendHttpRequest(
                "http://elf.egos.hosigus.com/getSongListID.php?type=" + songType.name()
                , new HttpUtilListener() {
                    @Override
                    public void success(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONObject songid = object.getJSONObject("data");
                            String id = songid.getString("id");
                            Log.d("testid",id);
                            getsongs(id,songType);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void failed() {
                        getsongId(songType);
                    }
                });

    }
    //通过id获取歌单所有歌曲
    public void getsongs(String id, final SongType type){
        HttpUtil.sendHttpRequest("http://elf.egos.hosigus.com/music/playlist/detail/application/x-www-form-urlencoded?id="+id, new HttpUtilListener() {
            @Override
            public void success(String response) {
                try {

                    Log.d("jsontest",response);
                    JSONObject object = new JSONObject(response);
                    JSONObject object1 = object.getJSONObject("playlist");
                    JSONArray array = object1.getJSONArray("tracks");
                    //Log.d("jsontest","测试");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        String songname = jsonObject.getString("name");
                        String id = jsonObject.getString("id");
                        JSONArray sinerinfo = jsonObject.getJSONArray("ar");
                        String singername = sinerinfo.getJSONObject(0).getString("name");
                        String coverurl = jsonObject.getJSONObject("al").getString("picUrl");
                        Log.d("jsontest1",songname+"==="+id+"==="+singername+"==="+coverurl);
                        switch (type){
                            case HAPPY:
                                happysongs.add(new Songbean(songname,singername,coverurl,id));
                                break;
                            case UNHAPPY:
                                unhappysongs.add(new Songbean(songname,singername,coverurl,id));
                                break;
                            case CLAM:
                                clamsongs.add(new Songbean(songname,singername,coverurl,id));
                                break;
                            case EXCITING:
                                excitingsongs.add(new Songbean(songname,singername,coverurl,id));
                                break;
                            default:
                                break;
                        }
                    }
                    randomlyPlay(type);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("testhttp", "失败");
                }
            }

            @Override
            public void failed() {
                Log.d("testhttp", "失败");
            }
        });
    }
    //随机播放某个歌单的歌曲
    public void randomlyPlay(SongType type){
        Random number = new Random();
        Songbean songbean = null;
        switch (type){
            case HAPPY:

                happyindex = number.nextInt(happysongs.size());
                songbean = happysongs.get(happyindex);

                break;
            case UNHAPPY:
                unhappyindex = number.nextInt(unhappysongs.size());
                songbean = unhappysongs.get(unhappyindex);

                break;
            case CLAM:
                clamindex = number.nextInt(clamsongs.size());
                songbean = clamsongs.get(clamindex);

                break;
            case EXCITING:
                excitingindex = number.nextInt(excitingsongs.size());
                songbean = excitingsongs.get(excitingindex);

                break;
            default:
                break;

        }
        if(songbean!=null){
            msongBean = songbean;
            Log.d("testsongbean",songbean.getSongName()+"==="+songbean.getSingerName()+"==="+songbean.getId()+"==="+songbean.getCoverUrl());
            try {

                player.setDataSource("http://music.163.com/song/media/outer/url?id="+songbean.getId()+".mp3");
                player.prepare();
                player.start();
                //更新界面

                final Songbean finalSongbean = songbean;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyImageLoader.with(MainActivity.this).into(songCover)
                                .placeholder(R.drawable.ic_default_bottom_music_icon)
                                .load(finalSongbean.getCoverUrl());
                        song.setText(finalSongbean.getSongName());
                        singer.setText(finalSongbean.getSingerName());
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.d("testsongbean","songbean为空");
        }
    }



    //更改昵称
    private void changedialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.edit_view, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        final MyEditView editView = view.findViewById(R.id.edit_name);
        builder.setCancelable(true);
        builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name.setText(editView.getText());
                //存储昵称
                SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
                editor.putString("nickname", String.valueOf(editView.getText()));
                editor.apply();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogName = builder.show();
    }

    //选取头像
    private void selectPic() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PICTURE);
    }

    //view点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.name:
                changedialog();
                break;
            case R.id.head:
                //申请读写权限
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    selectPic();
                }
                break;
            case R.id.more:
                Intent intent = new Intent(MainActivity.this,MoreActivity.class);
                intent.putExtra("info",msongBean);
                startActivity(intent);
                break;
            case R.id.happy:
                type = SongType.HAPPY;
                player.pause();
                player.reset();
                if(happysongs.size() > 0){
                    msongBean = happysongs.get(happyindex);
                    loadinfo(msongBean);
                    try {
                        player.setDataSource("http://music.163.com/song/media/outer/url?id="+msongBean.getId()+".mp3");
                        player.prepare();
                        player.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    getsongId(type);
                }
                break;
            case R.id.unhappy:
                type = SongType.UNHAPPY;
                player.pause();
                player.reset();
                if(unhappysongs.size() > 0){
                    msongBean = unhappysongs.get(unhappyindex);
                    loadinfo(msongBean);
                    try {
                        player.setDataSource("http://music.163.com/song/media/outer/url?id="+msongBean.getId()+".mp3");
                        player.prepare();
                        player.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    getsongId(type);
                }
                break;
            case R.id.clam:
                type = SongType.CLAM;
                player.pause();
                player.reset();
                if(unhappysongs.size() > 0){

                    msongBean = clamsongs.get(clamindex);
                    loadinfo(msongBean);

                    try {
                        player.setDataSource("http://music.163.com/song/media/outer/url?id="+msongBean.getId()+".mp3");
                        player.prepare();
                        player.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    getsongId(type);
                }
                break;
            case R.id.exciting:
                type = SongType.EXCITING;
                player.pause();
                player.reset();
                if(unhappysongs.size() > 0){
                    msongBean = excitingsongs.get(excitingindex);
                    loadinfo(msongBean);
                    try {
                        player.setDataSource("http://music.163.com/song/media/outer/url?id="+msongBean.getId()+".mp3");
                        player.prepare();
                        player.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    getsongId(type);
                }
                break;
            case R.id.cir_cle:
                toolbarTitle.setText(msongBean.getSongName());
                songCover.setVisibility(View.GONE);
                song.setVisibility(View.GONE);
                singer.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                HttpUtil.sendHttpRequest("http://elf.egos.hosigus.com/music/lyric/application/x-www-form-urlencoded?id=" + msongBean.getId(), new HttpUtilListener() {
                    @Override
                    public void success(final String response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lyrics.setText(response);
                            }
                        });
                    }

                    @Override
                    public void failed() {

                    }
                });
                break;
            case R.id.hide_lyrics:
                toolbarTitle.setText("EIF");
                scrollView.setVisibility(View.GONE);
                songCover.setVisibility(View.VISIBLE);
                song.setVisibility(View.VISIBLE);
                singer.setVisibility(View.VISIBLE);

            default:
                break;


        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(playreceiver);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.slideOut_nav:
                drawerLayout.openDrawer(GravityCompat.END);
                break;
            default:
                break;
        }
        return true;
    }

    //navigation菜单项的点击事件
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            //日推
            case R.id.daily_recommend:
                Toast.makeText(MainActivity.this,"测试",Toast.LENGTH_SHORT).show();
                break;
            //广场
            case R.id.comments_plaza:
                break;
            case R.id.my_collection:
                startActivity(new Intent(MainActivity.this,CollectActivity.class));
                break;
            case R.id.setting:
                break;
            default:
                break;

        }
        return false;
    }

    @Override
    //授权
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPic();
                } else {
                    Toast.makeText(this, "您取消了授权，该功能无法使用", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {

            case CHOOSE_PICTURE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    head.setImageURI(uri);
                    save(data);


                }
                break;
            default:
                break;
        }

    }

    //保存头像
    private void save(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
        editor.putString("head", imagePath);
        editor.apply();



    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    class ControlPlayReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int index = 0;
            ControlType controlType = (ControlType) intent.getSerializableExtra("control");
            switch (controlType){

                case BACK:
                    player.pause();
                    player.reset();
                    switch (type){
                        case HAPPY:
                            temp = happysongs;
                            index = (happyindex == 0)?happysongs.size()-1:happyindex-1;
                            happyindex = index;
                            break;
                        case UNHAPPY:
                            temp = unhappysongs;
                            index = (unhappyindex == 0)?unhappysongs.size()-1:unhappyindex-1;
                            unhappyindex = index;
                            break;
                        case EXCITING:
                            temp = excitingsongs;
                            index = (excitingindex == 0)?excitingsongs.size()-1:excitingindex-1;
                            excitingindex = index;
                            break;
                        case CLAM:
                            temp = clamsongs;
                            index = (clamindex == 0)?clamsongs.size()-1:clamindex-1;
                            clamindex = index;
                            break;

                        default:
                            break;

                    }
                    try {
                        msongBean = temp.get(index);
                        loadinfo(msongBean);
                        Intent intent1 = new Intent("refresh");
                        //refreshReceiverType = RefreshReceiverType.CUTOVER;
                        intent1.putExtra("refreshview",msongBean);
                       // intent1.putExtra("refreshtype",RefreshReceiverType.CUTOVER);
                        sendBroadcast(intent1);
                        player.setDataSource("http://music.163.com/song/media/outer/url?id="+msongBean.getId()+".mp3");
                        player.prepare();
                        player.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case FORWARD:
                    player.pause();
                    player.reset();
                    switch (type){
                        case HAPPY:
                            temp = happysongs;
                            index = (happyindex == happysongs.size()-1)?0:happyindex+1;
                            happyindex = index;
                            break;
                        case UNHAPPY:
                            temp = unhappysongs;
                            index = (unhappyindex == unhappysongs.size()-1)?0:unhappyindex+1;
                            unhappyindex = index;
                            break;
                        case EXCITING:
                            temp = excitingsongs;
                            index = (excitingindex == excitingsongs.size()-1)?0:excitingindex+1;
                            excitingindex = index;
                            break;
                        case CLAM:
                            temp = clamsongs;
                            index = (clamindex == clamsongs.size()-1)?0:clamindex+1;
                            clamindex = index;
                            break;
                        default:
                            break;

                    }
                    try {
                        msongBean = temp.get(index);
                        Intent intent1 = new Intent("refresh");

                        intent1.putExtra("refreshview",msongBean);
                        //intent1.putExtra("refreshtype",RefreshReceiverType.CUTOVER);
                        sendBroadcast(intent1);
                        loadinfo(msongBean);
                        player.setDataSource("http://music.163.com/song/media/outer/url?id="+msongBean.getId()+".mp3");
                        player.prepare();
                        player.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case PAUSE:
                    if(player.isPlaying()){
                        player.pause();
                        currenttimemusic = player.getCurrentPosition();
                        Log.d("getCurrentPosition", String.valueOf(currenttimemusic));
                        songCover.animauseP();
                        valueAnimator.pause();
                        //currenttime = (int) valueAnimator.getCurrentPlayTime();

                    }
                    break;
                case CONTINUE:
                    if (!player.isPlaying()){
                        player.start();
                        //player.seekTo(currenttimemusic);
                        songCover.animcontinue(currenttime);
                        valueAnimator.setCurrentPlayTime(currenttime);
                        valueAnimator.start();

                    }
//                case DRAG:
//                    int progress = intent.getIntExtra("progress",0);
//                    player.seekTo(progress);
//                    break;
                default:
                    break;


            }
        }
    }



}
