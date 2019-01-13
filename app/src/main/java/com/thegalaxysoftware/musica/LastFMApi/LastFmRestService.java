package com.thegalaxysoftware.musica.LastFMApi;

import com.thegalaxysoftware.musica.LastFMApi.Models.AlbumInfo;
import com.thegalaxysoftware.musica.LastFMApi.Models.ArtistInfo;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

public interface LastFmRestService {

    String BASE_PARAMETERS_ALBUM = "/?method=album.getinfo&api_key=2bdcbe359b44119baba1012e53231e97&format=json";
    String BASE_PARAMETERS_ARTIST = "/?method=artist.getinfo&api_key=2bdcbe359b44119baba1012e53231e97&format=json";

    @Headers("Cache-Control: public")
    @GET(BASE_PARAMETERS_ALBUM)
    void getAlbumInfo(@Query("artist") String artist, @Query("album") String album, Callback<AlbumInfo> callback);

    @Headers("Cache-Control: public")
    @GET(BASE_PARAMETERS_ARTIST)
    void getArtistInfo(@Query("artist") String artist, Callback<ArtistInfo> callback);
}
