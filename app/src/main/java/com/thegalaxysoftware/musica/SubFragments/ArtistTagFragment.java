package com.thegalaxysoftware.musica.SubFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thegalaxysoftware.musica.R;

public class ArtistTagFragment extends Fragment {

    private static final String ARG_PAGE_NUMBER = "pageNumber";

    public static ArtistTagFragment newInstance(int pageNumber) {
        ArtistTagFragment fragment = new ArtistTagFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_tag, container, false);
        return rootView;
    }
}
