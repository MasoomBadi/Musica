package com.thegalaxysoftware.musica.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.cast.framework.CastSession;
import com.thegalaxysoftware.musica.Activities.BaseActivity;
import com.thegalaxysoftware.musica.BeanClass.Song;
import com.thegalaxysoftware.musica.GoogleCast.MCastHelper;
import com.thegalaxysoftware.musica.MusicPlayer;
import com.thegalaxysoftware.musica.Utils.MusicaUtils;
import com.thegalaxysoftware.musica.Utils.NavigationUtils;

import java.util.List;

public class BaseSongAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {


    @NonNull
    @Override
    public V onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull V v, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        public ItemHolder(View view) {
            super(view);
        }
    }

    public void playAll(final Activity context, final long[] list, int position,
                        final long sourceId, final MusicaUtils.IdType sourceType,
                        final boolean forceShuffle, final Song currentSong, boolean navigateNowPlaying) {
        if (context instanceof BaseActivity) {
            CastSession castSession = ((BaseActivity) context).getCastSession();
            if (castSession != null) {
                navigateNowPlaying = false;
                MCastHelper.startCasting(castSession, currentSong);
            } else {
                MusicPlayer.playAll(context, list, position, -1, MusicaUtils.IdType.NA, false);
            }
        } else {
            MusicPlayer.playAll(context, list, position, -1, MusicaUtils.IdType.NA, false);
        }

        if (navigateNowPlaying) {
            NavigationUtils.navigateToNowplaying(context, true);
        }
    }

    public void removeSongAt(int i) {

    }

    public void updateDataSet(List<Song> arraylist) {

    }

}
