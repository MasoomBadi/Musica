package com.thegalaxysoftware.musica.LastFMApi.Models;

import com.google.gson.annotations.SerializedName;

public class ArtistInfo {

    private static final String ARTIST = "artist";

    @SerializedName(ARTIST)
    public LastfmArtist mArtist;
}
