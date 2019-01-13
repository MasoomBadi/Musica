package com.thegalaxysoftware.musica.BeanClass;

public class Song {

    public final long albumId;
    public final String albumName;
    public final long artistId;
    public final String artistName;
    public final int duration;
    public final long id;
    public final String title;
    public final int trackNumber;

    public Song()
    {
        this.id = -1;
        this.albumId = -1;
        this.artistId = -1;
        this.duration = -1;
        this.artistName = "";
        this.albumName = "";
        this.title = "";
        this.trackNumber = -1;
    }

    public Song(long _id, long _albumId, long _artistId, String _title, String _artistName, String _albumName, int _duration, int _trackNumber) {
        this.albumId = _albumId;
        this.albumName = _albumName;
        this.artistId = _artistId;
        this.artistName = _artistName;
        this.duration = _duration;
        this.id = _id;
        this.title = _title;
        this.trackNumber = _trackNumber;
    }
}
