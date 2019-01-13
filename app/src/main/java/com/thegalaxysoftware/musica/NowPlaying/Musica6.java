package com.thegalaxysoftware.musica.NowPlaying;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thegalaxysoftware.musica.R;

public class Musica6 extends BaseNowPlayingFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_musica6, container, false);

        setMusicStateListener();
        setSongDetails(rootView);

        return rootView;
    }
}
