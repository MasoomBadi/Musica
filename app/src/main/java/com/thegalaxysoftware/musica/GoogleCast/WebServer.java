package com.thegalaxysoftware.musica.GoogleCast;

import android.content.Context;
import android.net.Uri;

import com.thegalaxysoftware.musica.Utils.Constants;
import com.thegalaxysoftware.musica.Utils.MusicaUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {

    private Context context;
    private Uri songUri, albumArtUri;

    public WebServer(Context context) {
        super(Constants.CAST_SERVER_PORT);
        this.context = context;
    }

    @Override
    public Response serve(String uri, Method method,
                          Map<String, String> headers,
                          Map<String, String> parms,
                          Map<String, String> files) {
        if (uri.contains("albumart")) {
            String albumId = parms.get("id");
            this.albumArtUri = MusicaUtils.getAlbumArtUri(Long.parseLong(albumId));

            if (albumArtUri != null) {
                String mediasend = "image/jpg";
                InputStream fisAlbumArt = null;

                try {
                    fisAlbumArt = context.getContentResolver().openInputStream(albumArtUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Response.Status st = Response.Status.OK;

                return newChunkedResponse(st, mediasend, fisAlbumArt);
            }
        } else if (uri.contains("song")) {
            String songId = parms.get("id");
            this.songUri = MusicaUtils.getSongUri(context, Long.parseLong(songId));

            if (songUri != null) {
                String mediasend = "audio/mp3";
                FileInputStream fisSong = null;
                File song = new File(songUri.getPath());

                try {
                    fisSong = new FileInputStream(song);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Response.Status st = Response.Status.OK;

                return newFixedLengthResponse(st, mediasend, fisSong, song.length());
            }
        }
        return newFixedLengthResponse("Error");
    }
}
