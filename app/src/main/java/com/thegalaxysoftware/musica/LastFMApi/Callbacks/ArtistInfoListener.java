package com.thegalaxysoftware.musica.LastFMApi.Callbacks;

import com.thegalaxysoftware.musica.LastFMApi.Models.LastfmArtist;

public interface ArtistInfoListener {

    void artistInfoSucess(LastfmArtist artist);

    void artistInfoFailed();
}
