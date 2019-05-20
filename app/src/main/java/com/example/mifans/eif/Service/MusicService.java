package com.example.mifans.eif.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.mifans.eif.Activities.MainActivity;
import com.example.mifans.eif.R;
import com.example.mifans.eif.Tools.HttpUtil;
import com.example.mifans.eif.Tools.HttpUtilListener;
import com.example.mifans.eif.Tools.MyImageLoader;
import com.example.mifans.eif.interfaces.UpdateMusicInfo;
import com.example.mifans.eif.other.SongType;
import com.example.mifans.eif.other.Songbean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicService extends Service {
    List<Songbean> happysongs = new ArrayList<>();
    List<Songbean> unhappysongs = new ArrayList<>();
    List<Songbean> clamsongs = new ArrayList<>();
    List<Songbean> excitingsongs = new ArrayList<>();
    //记录当前播放的每种心情的歌单的下标
    private int happyindex = -1;
    private int unhappyindex = -1;
    private int clamindex = -1;
    private int excitingindex = -1;
    SongType type = SongType.HAPPY;//开始进入默认为HAPPY类型的音乐
    Songbean msongBean = null;
    List<Songbean> temp = new ArrayList<>();
    MediaPlayer player;
    ControlBinder binder = new ControlBinder();
    UpdateMusicInfo updateMusicInfo;
    public MusicService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }
    //获取心情歌单id



    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public class ControlBinder extends Binder {
        //获取歌曲总长度
        public int getMusicLength() {
            return player.getDuration();
        }
        //获取当前正在播放的歌曲实例
        public void getcurrent(UpdateMusicInfo updateMusicInfo){
            updateMusicInfo.update(msongBean);
        }
        //根据心情切歌
        public void cutSongBy(SongType type,UpdateMusicInfo updateMusicInfo){
            player.reset();
            switch (type){
                case HAPPY:
                    if (happysongs.size()>0){
                        randomlyPlay(type,updateMusicInfo);
                    }else {
                        getsongId(type,updateMusicInfo);
                    }
                    break;
                case UNHAPPY:
                    if (unhappysongs.size()>0){
                        randomlyPlay(type,updateMusicInfo);
                    }else {
                        getsongId(type,updateMusicInfo);
                    }
                    break;
                case CLAM:
                    if (clamsongs.size()>0){
                        randomlyPlay(type,updateMusicInfo);
                    }else {
                        getsongId(type,updateMusicInfo);
                    }
                    break;
                case EXCITING:
                    if (excitingsongs.size()>0){
                        randomlyPlay(type,updateMusicInfo);
                    }else {
                        getsongId(type,updateMusicInfo);
                    }
                    break;
            }
        }


        public void getsongId(final SongType songType, final UpdateMusicInfo musicInfo) {
            HttpUtil.sendHttpRequest(
                    "http://elf.egos.hosigus.com/getSongListID.php?type=" + songType.name()
                    , new HttpUtilListener() {
                        @Override
                        public void success(String response) {
                            try {
                                JSONObject object = new JSONObject(response);
                                JSONObject songid = object.getJSONObject("data");
                                String id = songid.getString("id");
                                Log.d("testid", id);
                                getsongs(id, songType, musicInfo);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void failed() {

                        }
                    });

        }

        //使用心情歌单的id获取对应歌单的全部歌曲
        public void getsongs(String id, final SongType type, final UpdateMusicInfo updateMusicInfo) {
            HttpUtil.sendHttpRequest("http://elf.egos.hosigus.com/music/playlist/detail/application/x-www-form-urlencoded?id=" + id, new HttpUtilListener() {
                @Override
                public void success(String response) {
                    try {

                        Log.d("jsontest", response);
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
                            Log.d("jsontest1", songname + "===" + id + "===" + singername + "===" + coverurl);
                            switch (type) {
                                case HAPPY:
                                    happysongs.add(new Songbean(songname, singername, coverurl, id));
                                    break;
                                case UNHAPPY:
                                    unhappysongs.add(new Songbean(songname, singername, coverurl, id));
                                    break;
                                case CLAM:
                                    clamsongs.add(new Songbean(songname, singername, coverurl, id));
                                    break;
                                case EXCITING:
                                    excitingsongs.add(new Songbean(songname, singername, coverurl, id));
                                    break;
                                default:
                                    break;
                            }
                        }
                        randomlyPlay(type, updateMusicInfo);
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
        public void randomlyPlay(SongType type, UpdateMusicInfo updateMusicInfo) {
            Random number = new Random();
            Songbean songbean = null;
            switch (type) {
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
            if (songbean != null) {
                //更新主界面和播放界面的音乐信息
                updateMusicInfo.update(songbean);
                msongBean = songbean;
                Log.d("testsongbean", songbean.getSongName() + "===" + songbean.getSingerName() + "===" + songbean.getId() + "===" + songbean.getCoverUrl());
                try {

                    player.setDataSource("http://music.163.com/song/media/outer/url?id=" + songbean.getId() + ".mp3");
                    player.prepare();
                    player.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("testsongbean", "songbean为空");
            }
        }
    }
}
