package com.thegalaxysoftware.musica.NowPlaying;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.thegalaxysoftware.musica.MusicPlayer;
import com.thegalaxysoftware.musica.MusicService;
import com.thegalaxysoftware.musica.R;
import com.thegalaxysoftware.musica.Utils.ImageUtils;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;


public class Musica4 extends BaseNowPlayingFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_musica4, container, false);

        setMusicStateListener();
        setSongDetails(rootView);

        return rootView;
    }
}
