package com.thegalaxysoftware.musica.SubFragments;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.thegalaxysoftware.musica.MusicaApp;
import com.thegalaxysoftware.musica.R;
import com.thegalaxysoftware.musica.Utils.ConnectivityReciever;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.jsoup.Jsoup;

import java.io.IOException;

public class DisplaySearchedLyrics extends Fragment implements ConnectivityReciever.ConnectivityReceiverListener {

    private String songName, songArtist, songLink;
    private String songlyrics = "";
    private TextView tv_songName, tv_songArtist, lyrics;
    private LinearLayout noconnectivity;
    private MaterialIconView refreshLayout;
    ProgressBar progressBar;
    View lyricview;
    private Toolbar toolbar;
    boolean isConnected;

    private String uppartition = "<!-- Usage of azlyrics.com content by any third-party lyrics provider is prohibited by our licensing agreement. Sorry about that. -->";
    private String downparition = "<!-- MxM banner -->";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lyrics, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);

        tv_songName = (TextView) rootView.findViewById(R.id.lyrics_songname);
        tv_songArtist = (TextView) rootView.findViewById(R.id.lyrics_artistname);
        lyrics = (TextView) rootView.findViewById(R.id.lyrics_text);
        lyricview = rootView.findViewById(R.id.lyrics);

        noconnectivity = (LinearLayout) rootView.findViewById(R.id.no_connectivity);
        refreshLayout = (MaterialIconView) rootView.findViewById(R.id.refresh_layout);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);

        isConnected = ConnectivityReciever.isConnected();

        Bundle args = getArguments();
        if (args != null) {
            songName = args.getString("songName");
            songArtist = args.getString("songArtist");
            songLink = args.getString("Link");

            tv_songName.setText(songName);
            tv_songArtist.setText("By - " + songArtist);
        } else {
            tv_songName.setText("No songs selected.");
            tv_songArtist.setText("By - ");
        }

        if (isConnected) {
            noconnectivity.setVisibility(View.GONE);
            new loadLyrics().execute();
        } else {
            noconnectivity.setVisibility(View.VISIBLE);
        }

        refreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(DisplaySearchedLyrics.this).attach(DisplaySearchedLyrics.this).commit();
            }
        });

        setupToolbar();
        return rootView;
    }

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Lyrics");
    }

    public class loadLyrics extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            if(songlyrics.length() == 0)
            {
                songlyrics = "No Lyrics Found, Sorry :(";
            }
            else
            {
                lyrics.setText(songlyrics);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                songlyrics = Jsoup.connect(songLink).get().html();
                songlyrics = songlyrics.split(uppartition)[1];
                songlyrics = songlyrics.split(downparition)[0];
                songlyrics = songlyrics.replace("<br>", "").replace("</br>", "").replace("</div", "").replace("<", "").replace(">", ""
                .replace("<i>","").replace("</i>","").replace("<b>","").replace("</b>",""));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Oops! Something is wrong.", Toast.LENGTH_SHORT).show();
            }

            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        ConnectivityReciever connectivityReciever = new ConnectivityReciever();
        getActivity().registerReceiver(connectivityReciever, intentFilter);
        MusicaApp.getmInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
