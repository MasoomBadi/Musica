package com.thegalaxysoftware.musica.Dataloaders;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.thegalaxysoftware.musica.BeanClass.Song;
import com.thegalaxysoftware.musica.Utils.PreferencesUtility;

import java.util.ArrayList;

public class AlbumSongLoader {

    private static final long[] sEmptyList = new long[0];

    public static ArrayList<Song> getSongsForAlbum(Context context, long albumID) {

        Cursor cursor = makeAlbumSongCursor(context, albumID);
        ArrayList arrayList = new ArrayList();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                long id = cursor.getLong(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String album = cursor.getString(3);
                int duration = cursor.getInt(4);
                int trackNumber = cursor.getInt(5);
                while (trackNumber >= 1000) {
                    trackNumber -= 1000;
                }
                long artistId = cursor.getInt(6);
                long albumId = albumID;

                arrayList.add(new Song(id, albumId, artistId, title, artist, album, duration, trackNumber));
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }

    public static Cursor makeAlbumSongCursor(Context context, long albumID) {
        ContentResolver contentResolver = context.getContentResolver();
        final String albumSongSortOrder = PreferencesUtility.getsInstance(context).getAlbumSongSortOrder();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String string = "is_music=1 AND title != '' AND album_id=" + albumID;
        Cursor cursor = contentResolver.query(uri, new String[]{"_id", "title", "artist", "album", "duration", "track", "artist_id"}, string, null, albumSongSortOrder);
        return cursor;
    }
}
