package com.thegalaxysoftware.musica.Dataloaders;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.thegalaxysoftware.musica.BeanClass.Playlist;
import com.thegalaxysoftware.musica.Utils.MusicaUtils;

import java.util.ArrayList;
import java.util.List;

public class PlaylistLoader {

    static ArrayList<Playlist> mPlaylistList;
    private static Cursor mCursor;

    public static List<Playlist> getPlaylists(Context context, boolean defaultIncluded) {
        mPlaylistList = new ArrayList<>();

        if(defaultIncluded)
            makeDefaultPlaylists(context);

        mCursor = makePlaylistCursor(context);

        if (mCursor != null && mCursor.moveToFirst()) {
            do {

                final long id = mCursor.getLong(0);

                final String name = mCursor.getString(1);

                final int songCount = MusicaUtils.getSongCountForPlaylist(context, id);

                final Playlist playlist = new Playlist(id, name, songCount);

                mPlaylistList.add(playlist);
            } while (mCursor.moveToNext());
        }
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mPlaylistList;
    }

    private static void makeDefaultPlaylists(Context context)
    {
        final Resources resources = context.getResources();

        final Playlist lastAdded = new Playlist(MusicaUtils.PlaylistType.LastAdded.mId,
                resources.getString(MusicaUtils.PlaylistType.LastAdded.mTitleId), -1);
        mPlaylistList.add(lastAdded);

        final Playlist recentlyPlayed = new Playlist(MusicaUtils.PlaylistType.RecentlyPlayed.mId,
                resources.getString(MusicaUtils.PlaylistType.RecentlyPlayed.mTitleId), -1);
        mPlaylistList.add(recentlyPlayed);

        final Playlist topTracks = new Playlist(MusicaUtils.PlaylistType.TopTracks.mId,
                resources.getString(MusicaUtils.PlaylistType.TopTracks.mTitleId), -1);
        mPlaylistList.add(topTracks);

    }

    public static final Cursor makePlaylistCursor(final Context context) {
        return context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{
                        BaseColumns._ID,
                        MediaStore.Audio.PlaylistsColumns.NAME
                }, null, null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
    }

    public static void deletePlaylists(Context context, long playlistId) {
        Uri localUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("_id IN (");
        localStringBuilder.append((playlistId));
        localStringBuilder.append(")");
        context.getContentResolver().delete(localUri, localStringBuilder.toString(), null);
    }
}
