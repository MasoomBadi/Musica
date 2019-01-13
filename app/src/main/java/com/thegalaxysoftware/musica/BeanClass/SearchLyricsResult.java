package com.thegalaxysoftware.musica.BeanClass;

public class SearchLyricsResult {

    public String linkRef;
    public String songName;
    public String lyricArtist;

    public SearchLyricsResult()
    {
        linkRef = "";
        songName = "";
        lyricArtist = "";
    }

    public SearchLyricsResult(String linkRef, String songName, String lyricArtist) {
        this.linkRef = linkRef;
        this.songName = songName;
        this.lyricArtist = lyricArtist;
    }

    public String getLinkRef() {
        return linkRef;
    }

    public String getSongName() {
        return songName;
    }

    public String getLyricArtist() {
        return lyricArtist;
    }

    public void setLinkRef(String linkRef) {
        this.linkRef = linkRef;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setLyricArtist(String lyricArtist) {
        this.lyricArtist = lyricArtist;
    }
}
