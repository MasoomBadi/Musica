package com.thegalaxysoftware.musica.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thegalaxysoftware.musica.Adapters.ArtistSongAdapter;
import com.thegalaxysoftware.musica.BeanClass.Song;
import com.thegalaxysoftware.musica.Dataloaders.ArtistSongLoader;
import com.thegalaxysoftware.musica.R;
import com.thegalaxysoftware.musica.Utils.Constants;
import com.thegalaxysoftware.musica.Widgets.DividerItemDecoration;

import java.util.ArrayList;

public class ArtistMusicFragment extends Fragment {

    public static RecyclerView songsRecyclerview;
    private long artistID = -1;
    private ArtistSongAdapter mSongAdapter;

    public static ArtistMusicFragment newInstance(long id) {
        ArtistMusicFragment fragment = new ArtistMusicFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ARTIST_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistID = getArguments().getLong(Constants.ARTIST_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_artist_music, container, false);

        songsRecyclerview = (RecyclerView) rootView.findViewById(R.id.recycler_view_songs);
        setUpSongs();
        return rootView;
    }

    private void setUpSongs() {
        songsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<Song> songList;
        songList = ArtistSongLoader.getSongsForArtist(getActivity(), artistID);

        songList.add(0, new Song(-1, -1, -1, "dummy", "dummy", "dummy", -1, -1));

        mSongAdapter = new ArtistSongAdapter(getActivity(), songList, artistID);
        songsRecyclerview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        songsRecyclerview.setAdapter(mSongAdapter);
    }
}
