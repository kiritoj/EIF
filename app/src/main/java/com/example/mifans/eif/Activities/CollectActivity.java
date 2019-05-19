package com.example.mifans.eif.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mifans.eif.R;
import com.example.mifans.eif.other.RecyclerAdapter;
import com.example.mifans.eif.other.Songbean;

import java.util.ArrayList;
import java.util.List;

public class CollectActivity extends AppCompatActivity {
    private Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerAdapter songsAdapter;
    SwipeRefreshLayout refreshLayout;
    private List<Songbean> songsList = new ArrayList<Songbean>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        initViews();



    }
    public void initViews(){
        refreshLayout = findViewById(R.id.mycollect_refresh);
        recyclerView = findViewById(R.id.mycollect_recycleview);
        toolbar = findViewById(R.id.tool_bar_c);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        initCollectedNews();
        songsAdapter = new RecyclerAdapter(songsList,this);
        recyclerView.setAdapter(songsAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.fenge));
        recyclerView.addItemDecoration(divider);
        LinearLayoutManager manager = new LinearLayoutManager(CollectActivity.this);
        recyclerView.setLayoutManager(manager);
        refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //清除收藏列表，产生新的收藏列表
                songsList.clear();

                initCollectedNews();
                songsAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
                Toast.makeText(CollectActivity.this, "收藏列表已刷新成功", Toast.LENGTH_SHORT).show();


            }
        });
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
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
    public void initCollectedNews(){
        //从数据库加载已经收藏的新闻
        MyDataBaseHelper databaseHelper = new MyDataBaseHelper(CollectActivity.this, "User.db", null, 1);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Cursor cursor = database.query("Collect",null,null,null,null,null,null);
        if (cursor.moveToLast()){
            //从后往前遍历，最后收藏的处于最顶层
            do {
                String songid = cursor.getString(cursor.getColumnIndex("songid"));
                String songname = cursor.getString(cursor.getColumnIndex("songname"));
                String singer = cursor.getString(cursor.getColumnIndex("singer"));
                String coverurl = cursor.getString(cursor.getColumnIndex("coverurl"));
                Songbean news = new Songbean(songname,singer,coverurl,songid);
                songsList.add(news);

            }while (cursor.moveToPrevious());
        }
    }
}
