package com.thegalaxysoftware.musica.BeanClass;

public class Album {

    public final long artistId;
    public final String artistName;
    public final long id;
    public final int songCount;
    public final String title;
    public final int year;

    public Album()
    {
        this.id = -1;
        this.title = "";
        this.artistName = "";
        this.artistId = -1;
        this.songCount = -1;
        this.year = -1;
    }

    public Album(long _id, String _title, String _artistName, long _artistId, int _songCount, int _year) {
        this.artistId = _artistId;
        this.artistName = _artistName;
        this.id = _id;
        this.songCount = _songCount;
        this.title = _title;
        this.year = _year;
    }
}
