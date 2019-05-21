package com.example.mifans.eif.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.mifans.eif.Activities.MainActivity;
import com.example.mifans.eif.R;
import com.example.mifans.eif.Tools.HttpUtil;
import com.example.mifans.eif.Tools.HttpUtilListener;
import com.example.mifans.eif.Tools.MyImageLoader;
import com.example.mifans.eif.interfaces.UpdateMusicInfo;
import com.example.mifans.eif.interfaces.UpdateProgress;
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
    //使用hander更新进度条
    private Handler handler = new Handler();

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
        player.stop();
        player.release();
    }

    public class ControlBinder extends Binder {
        //获取歌曲总长度
        public int getMusicLength() {
            return player.getDuration();
        }

        //获取当前正在播放的歌曲实例
        public void getcurrent(UpdateMusicInfo updateMusicInfo) {
            updateMusicInfo.update(msongBean);
        }
        //是否正在播放
        public boolean isplaying(){
            return player.isPlaying();
        }
        //播放下一曲
        public void playNext(UpdateMusicInfo updateMusicInfo) {
            player.reset();
            int index = 0;
            switch (type) {
                case HAPPY:
                    temp = happysongs;
                    index = (happyindex == 0) ? happysongs.size() - 1 : happyindex - 1;
                    happyindex = index;
                    break;
                case UNHAPPY:
                    temp = unhappysongs;
                    index = (unhappyindex == 0) ? unhappysongs.size() - 1 : unhappyindex - 1;
                    unhappyindex = index;
                    break;
                case EXCITING:
                    temp = excitingsongs;
                    index = (excitingindex == 0) ? excitingsongs.size() - 1 : excitingindex - 1;
                    excitingindex = index;
                    break;
                case CLAM:
                    temp = clamsongs;
                    index = (clamindex == 0) ? clamsongs.size() - 1 : clamindex - 1;
                    clamindex = index;
                    break;

                default:
                    break;

            }
            try {
                msongBean = temp.get(index);
                updateMusicInfo.update(msongBean);
                player.setDataSource("http://music.163.com/song/media/outer/url?id=" + msongBean.getId() + ".mp3");
                player.prepare();
                player.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //播放上一首
        public void playPre(UpdateMusicInfo updateMusicInfo) {
            int index = 0;
            player.reset();
            switch (type) {
                case HAPPY:
                    temp = happysongs;
                    index = (happyindex == happysongs.size() - 1) ? 0 : happyindex + 1;
                    happyindex = index;
                    break;
                case UNHAPPY:
                    temp = unhappysongs;
                    index = (unhappyindex == unhappysongs.size() - 1) ? 0 : unhappyindex + 1;
                    unhappyindex = index;
                    break;
                case EXCITING:
                    temp = excitingsongs;
                    index = (excitingindex == excitingsongs.size() - 1) ? 0 : excitingindex + 1;
                    excitingindex = index;
                    break;
                case CLAM:
                    temp = clamsongs;
                    index = (clamindex == clamsongs.size() - 1) ? 0 : clamindex + 1;
                    clamindex = index;
                    break;
                default:
                    break;

            }
            try {
                msongBean = temp.get(index);
                updateMusicInfo.update(msongBean);
                player.setDataSource("http://music.163.com/song/media/outer/url?id=" + msongBean.getId() + ".mp3");
                player.prepare();
                player.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //暂停播放
        public void playPause(){
            if(player.isPlaying()) {
                player.pause();
            }
        }
        //继续播放
        public void playContinue(){
            if (!player.isPlaying()){
                player.start();
            }
        }
        //根据心情切歌
        public void cutSongBy(SongType type, UpdateMusicInfo updateMusicInfo) {
            player.reset();
            switch (type) {
                case HAPPY:
                    if (happysongs.size() > 0) {
                        randomlyPlay(type, updateMusicInfo);
                    } else {
                        getsongId(type, updateMusicInfo);
                    }
                    break;
                case UNHAPPY:
                    if (unhappysongs.size() > 0) {
                        randomlyPlay(type, updateMusicInfo);
                    } else {
                        getsongId(type, updateMusicInfo);
                    }
                    break;
                case CLAM:
                    if (clamsongs.size() > 0) {
                        randomlyPlay(type, updateMusicInfo);
                    } else {
                        getsongId(type, updateMusicInfo);
                    }
                    break;
                case EXCITING:
                    if (excitingsongs.size() > 0) {
                        randomlyPlay(type, updateMusicInfo);
                    } else {
                        getsongId(type, updateMusicInfo);
                    }
                    break;
            }
        }
        //更新进度条
        public void updateSeekBar(final UpdateProgress updateProgress, final UpdateMusicInfo updateMusicInfo){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    handler.postDelayed(this,1000);//每秒更新一次
                    if(player.getCurrentPosition() < player.getDuration()) {
                        int playcurrent = (int) Math.round(player.getCurrentPosition() / 1000.0);
                        int length = (int) Math.round(player.getDuration() / 1000.0);
                        String currentTime = String.format("%02d:%02d", playcurrent / 60, playcurrent % 60);
                        String endtime = String.format("%02d:%02d", length / 60, length % 60);
                        updateProgress.update(player.getCurrentPosition(), player.getDuration(), currentTime, endtime);
                    }else{
                        playNext(updateMusicInfo);
                    }

                }
            };
            handler.post(runnable);
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
                msongBean = songbean;
                Log.d("testsongbean", songbean.getSongName() + "===" + songbean.getSingerName() + "===" + songbean.getId() + "===" + songbean.getCoverUrl());
                try {
                    player.setDataSource("http://music.163.com/song/media/outer/url?id=" + songbean.getId() + ".mp3");
                    player.prepare();
                    player.start();
                    updateMusicInfo.update(songbean);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("testsongbean", "songbean为空");
            }
        }
        //拖拽音乐进度
        public void changeProgress(int progress){
            player.seekTo(progress);
        }
        //播放收藏的的音乐
        public void playSongBean(Songbean songbean,UpdateMusicInfo updateMusicInfo){
            msongBean = songbean;
            player.reset();
            try {
                player.setDataSource("http://music.163.com/song/media/outer/url?id=" + songbean.getId() + ".mp3");
                player.prepare();
                player.start();
                updateMusicInfo.update(songbean);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }
}
