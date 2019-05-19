package com.example.mifans.eif.other;

import java.io.Serializable;

public class Songbean implements Serializable {
    String songName;
    String singerName;
    String coverUrl;
    String id;

    public Songbean(String songName, String singerName, String coverUrl,String id) {
        this.songName = songName;
        this.singerName = singerName;
        this.coverUrl = coverUrl;
        this.id = id;
    }
    public Songbean(){}

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
