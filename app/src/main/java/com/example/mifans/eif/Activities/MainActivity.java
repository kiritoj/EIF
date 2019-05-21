package com.example.mifans.eif.Activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
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
import com.example.mifans.eif.Service.MusicService;
import com.example.mifans.eif.Tools.HttpUtil;
import com.example.mifans.eif.Tools.HttpUtilListener;
import com.example.mifans.eif.Tools.MyImageLoader;
import com.example.mifans.eif.interfaces.UpdateMusicInfo;

import com.example.mifans.eif.other.SongType;
import com.example.mifans.eif.other.Songbean;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static final int CHOOSE_PICTURE = 2;//相册选择图片
    public static final String baseUrlLyrics = "http://elf.egos.hosigus.com/music/lyric/application/x-www-form-urlencoded?id=";
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
    Songbean msongBean = null;
    int currenttime = 0;//动画进行的时间
    IntentFilter intentFilter;
    RefreshReceiver receiver;
    private MusicService.ControlBinder controlBinder;
    UpdateMusicInfo updateMusicInfo = new UpdateMusicInfo() {
        @Override
        public void update(final Songbean songbean) {
            msongBean = songbean;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadinfo(songbean);
                }
            });
            loadlyrics(songbean);
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
            controlBinder.getsongId(SongType.HAPPY, updateMusicInfo);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定播放音乐服务
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        initViews();
        //各控件的点击事件
        click();
        initinfo();
//        注册控制广播
        intentFilter = new IntentFilter();
        intentFilter.addAction("refreshMainActivity");
        receiver = new RefreshReceiver();
        registerReceiver(receiver, intentFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initViews() {

        toolbarTitle = findViewById(R.id.toolbar_title);
        lyrics = findViewById(R.id.hide_lyrics);
        scrollView = findViewById(R.id.scro_lyrics);
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
        valueAnimator = ObjectAnimator.ofFloat(songCover, "rotation", 0, 360);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setDuration(9000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
    }

    private void click() {
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

    //加载首页的三个view
    public void loadinfo(Songbean songbean) {
        if (songbean != null) {
            song.setText(songbean.getSongName());
            singer.setText(songbean.getSingerName());
            MyImageLoader.with(MainActivity.this).into(songCover)
                    .placeholder(R.drawable.ic_default_bottom_music_icon)
                    .load(songbean.getCoverUrl());
        }

    }

    //加载歌词
    private void loadlyrics(Songbean songbean) {
        if (songbean != null) {
            HttpUtil.sendHttpRequest(baseUrlLyrics + songbean.getId(), new HttpUtilListener() {
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
        }
    }

    //初始化头像昵称信息
    private void initinfo() {
        SharedPreferences preferences = getSharedPreferences("name", MODE_PRIVATE);
        if (preferences != null) {
            name.setText(preferences.getString("nickname", "点击次数修改昵称"));
            String path = preferences.getString("head", null);
            if (path != null) {
                Log.d("myinitinfo", path);
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                head.setImageBitmap(bitmap);
            } else {
                Log.d("myinitinfo", "path为空");
                head.setImageResource(R.drawable.ic_default_bottom_music_icon);
            }
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
                Intent intent = new Intent(MainActivity.this, MoreActivity.class);
                //intent.putExtra("songid", String.valueOf(msongBean.getId()));
                startActivity(intent);
                break;
            case R.id.happy:
                if (controlBinder != null) {
                    controlBinder.cutSongBy(SongType.HAPPY, updateMusicInfo);
                }
                break;
            case R.id.unhappy:
                if (controlBinder != null) {
                    controlBinder.cutSongBy(SongType.UNHAPPY, updateMusicInfo);
                }
                break;
            case R.id.clam:
                if (controlBinder != null) {
                    controlBinder.cutSongBy(SongType.CLAM, updateMusicInfo);
                }
                break;
            case R.id.exciting:
                if (controlBinder != null) {
                    controlBinder.cutSongBy(SongType.UNHAPPY, updateMusicInfo);
                }
                break;
            case R.id.cir_cle:
                toolbarTitle.setText(msongBean.getSongName());
                songCover.setVisibility(View.GONE);
                song.setVisibility(View.GONE);
                singer.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

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
        unbindService(connection);
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
                Toast.makeText(MainActivity.this, "测试", Toast.LENGTH_SHORT).show();
                break;
            //广场
            case R.id.comments_plaza:
                break;
            case R.id.my_collection:
                startActivity(new Intent(MainActivity.this, CollectActivity.class));
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

    //音乐详情界面切换上下曲后发出广播通知主界面更新
    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (controlBinder!=null){
                controlBinder.getcurrent(updateMusicInfo);
            }

        }

    }


}
