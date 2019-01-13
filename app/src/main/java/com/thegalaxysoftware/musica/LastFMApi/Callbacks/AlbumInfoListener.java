package com.thegalaxysoftware.musica.LastFMApi.Callbacks;

import com.thegalaxysoftware.musica.LastFMApi.Models.LastfmAlbum;

public interface AlbumInfoListener {

    void albumInfoSuccess(LastfmAlbum album);

    void albumInfoFailed();
}
