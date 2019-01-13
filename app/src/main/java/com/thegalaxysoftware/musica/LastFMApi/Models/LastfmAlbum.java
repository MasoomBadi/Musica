package com.thegalaxysoftware.musica.LastFMApi.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmAlbum {

    private static final String IMAGE = "image";

    @SerializedName(IMAGE)
    public List<Artwork> mArtwork;

}
