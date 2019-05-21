package com.example.mifans.eif.interfaces;

//更新播放进度
public interface UpdateProgress {
    void update(int profress,int max,String currentTime,String endTime);
    void error();
}
