package com.thegalaxysoftware.musica.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thegalaxysoftware.musica.BeanClass.Artist;
import com.thegalaxysoftware.musica.Dataloaders.ArtistLoader;
import com.thegalaxysoftware.musica.LastFMApi.Callbacks.ArtistInfoListener;
import com.thegalaxysoftware.musica.LastFMApi.LastFmClient;
import com.thegalaxysoftware.musica.LastFMApi.Models.ArtistQuery;
import com.thegalaxysoftware.musica.LastFMApi.Models.LastfmArtist;
import com.thegalaxysoftware.musica.R;
import com.thegalaxysoftware.musica.SubFragments.ArtistTagFragment;
import com.thegalaxysoftware.musica.Utils.Constants;
import com.thegalaxysoftware.musica.Widgets.MultiViewPager;

public class ArtistBioFragment extends Fragment {

    long artistID = -1;

    public static ArtistBioFragment newInstance(long id) {
        ArtistBioFragment fragment = new ArtistBioFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ARTIST_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistID = getArguments().getLong(Constants.ARTIST_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_bio, container, false);

        Artist artist = ArtistLoader.getArtist(getActivity(), artistID);

        LastFmClient.getInstance(getActivity()).getArtistInfo(new ArtistQuery(artist.name), new ArtistInfoListener() {
            @Override
            public void artistInfoSucess(LastfmArtist artist) {

            }

            @Override
            public void artistInfoFailed() {
            }
        });

        final MultiViewPager pager = (MultiViewPager) rootView.findViewById(R.id.tagspager);

        final FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return ArtistTagFragment.newInstance(i);
            }

            @Override
            public int getCount() {
                return 20;
            }
        };
        pager.setAdapter(adapter);

        return rootView;
    }
}
