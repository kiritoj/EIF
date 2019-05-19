package com.example.mifans.eif.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.example.mifans.eif.CustomizeView.CircleView;
import com.example.mifans.eif.R;
import com.example.mifans.eif.Tools.MyImageLoader;
import com.example.mifans.eif.other.Myplayer;
import com.example.mifans.eif.other.SongType;

import java.io.IOException;

public class Main2Activity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button button = findViewById(R.id.tesbutton);
        final MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource("http://music.163.com/song/media/outer/url?id=424060342.mp3");
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        final CircleView circleView = findViewById(R.id.testpic);
//        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_mood_unhappy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MyImageLoader.with(Main2Activity.this).into(circleView).placeholder(R.drawable.ic_default_bottom_music_icon).load("https://p3.pstatp.com/thumb/2b600071a34863276d1");
                player.start();
               //);
//
            }
        });
    }


}
