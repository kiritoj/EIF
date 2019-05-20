package com.example.mifans.eif.interfaces;

import com.example.mifans.eif.other.Songbean;

//更新歌曲的名称，歌手，封面等
public interface UpdateMusicInfo {
     void update(Songbean songbean);
     void error();

}
