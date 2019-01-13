package com.thegalaxysoftware.musica.Fragments;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.thegalaxysoftware.musica.Adapters.SearchLyricsAdapter;
import com.thegalaxysoftware.musica.BeanClass.SearchLyricsResult;
import com.thegalaxysoftware.musica.MusicPlayer;
import com.thegalaxysoftware.musica.MusicaApp;
import com.thegalaxysoftware.musica.R;
import com.thegalaxysoftware.musica.SubFragments.DisplaySearchedLyrics;
import com.thegalaxysoftware.musica.Utils.ConnectivityReciever;
import com.thegalaxysoftware.musica.Utils.MusicaUtils;
import com.thegalaxysoftware.musica.Widgets.DividerItemDecoration;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LyricsFragment extends Fragment implements ConnectivityReciever.ConnectivityReceiverListener {

    private String lyrics = "";
    private Toolbar toolbar;
    private View rootView;
    private String songName, songArtist;
    SearchLyricsAdapter adapter;
    ScrollView mainContent;
    MaterialIconView refreshLayout;
    RecyclerView fetchedResults;
    ProgressBar progressBar;
    boolean isConnected;
    int isExecuted = 0;
    LinearLayout noconnectivity;
    TextView lyricSongName, lyricSongArtist;
    private String seachURL, finalizedUrl, onlyldsongName, onlyldsongArtist, link;
    private String uppartition = "<!-- Usage of azlyrics.com content by any third-party lyrics provider is prohibited by our licensing agreement. Sorry about that. -->";
    private String downparition = "<!-- MxM banner -->";

    ArrayList<SearchLyricsResult> searchedResult;
    private String[] abslinks, suggestedsongs, songartist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_lyrics, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        lyricSongName = (TextView) rootView.findViewById(R.id.lyrics_songname);
        lyricSongArtist = (TextView) rootView.findViewById(R.id.lyrics_artistname);
        noconnectivity = (LinearLayout) rootView.findViewById(R.id.no_connectivity);
        mainContent = (ScrollView) rootView.findViewById(R.id.lyrics);
        fetchedResults = (RecyclerView) rootView.findViewById(R.id.lyrics_search_result_display);
        refreshLayout = (MaterialIconView) rootView.findViewById(R.id.refresh_layout);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);


        fetchedResults.setAdapter(adapter);
        noconnectivity.setVisibility(View.GONE);
        fetchedResults.setVisibility(View.GONE);


        searchedResult = new ArrayList<>();

        fetchedResults.setLayoutManager(new LinearLayoutManager(getActivity()));
        fetchedResults.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        fetchedResults.setItemAnimator(null);

        adapter = new SearchLyricsAdapter(getActivity(), searchedResult, new SearchLyricsAdapter.SearchOnClickListener() {
            @Override
            public void onViewClicked(int pos) {

                Bundle args = new Bundle();
                args.putString("songName", searchedResult.get(pos).getSongName());
                args.putString("songArtist", searchedResult.get(pos).getLyricArtist());
                args.putString("Link", searchedResult.get(pos).getLinkRef());

                DisplaySearchedLyrics searchedLyrics = new DisplaySearchedLyrics();
                searchedLyrics.setArguments(args);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.fragment_container, searchedLyrics).addToBackStack(getClass().getName()).commit();

            }
        });


        isConnected = ConnectivityReciever.isConnected();
        if (isConnected) {
            noconnectivity.setVisibility(View.GONE);
            loadLyric();
        } else {
            noconnectivity.setVisibility(View.VISIBLE);
        }

        refreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(LyricsFragment.this).attach(LyricsFragment.this).commit();
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

    private void loadLyric() {
        final View lyricsView = rootView.findViewById(R.id.lyrics);

        final TextView lyricsTextView = (TextView) lyricsView.findViewById(R.id.lyrics_text);

        if (MusicPlayer.getTrackName() != null) {
            lyricSongName.setText(MusicPlayer.getTrackName());
            lyricSongArtist.setText("By : " + MusicPlayer.getArtistName());
        } else {
            lyricSongName.setText("No Track Selected");
            lyricSongArtist.setText("");
        }

        if (MusicPlayer.getTrackName() != null && MusicPlayer.getArtistName() != null) {
            songName = MusicPlayer.getTrackName().toLowerCase();
            songArtist = MusicPlayer.getArtistName().toLowerCase();

            if (songArtist.equals("<unknown>")) {
                songArtist = "";
            }
            onlyldsongName = songName.replaceAll("[^a-zA-Z0-9]", "");

            onlyldsongArtist = songArtist.replaceAll("[^a-zA-Z0-9]", "");

            if (onlyldsongName != null && !onlyldsongArtist.equals("")) {

                mainContent.setVisibility(View.VISIBLE);
                fetchedResults.setVisibility(View.GONE);

                finalizedUrl = "https://www.azlyrics.com/lyrics/" + onlyldsongArtist + "/" + onlyldsongName + ".html";

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        if (lyrics.length() == 0) {
                            lyrics = "No Lyrics Found, Sorry :(";
                        }
                        lyricsTextView.setText(lyrics);
                        Log.e("Lyric", lyrics.length() + "");
                        isExecuted = 1;
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {

                            lyrics = Jsoup.connect(finalizedUrl).get().html();
                            lyrics = lyrics.split(uppartition)[1];
                            lyrics = lyrics.split(downparition)[0];
                            lyrics = lyrics.replace("<br>", "").replace("</br>", "").replace("</div", "").replace("<", "").replace(">", ""
                                    .replace("<i>", "").replace("</i>", "").replace("<b>", "").replace("</b>", ""));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();

            } else {
                mainContent.setVisibility(View.GONE);
                fetchedResults.setVisibility(View.VISIBLE);


                if (MusicPlayer.getTrackName() != null) {
                    songName = MusicPlayer.getTrackName().toLowerCase();
                }
                onlyldsongName = songName.replace(" ", "+").replace("'", "%27");
                seachURL = "https://search.azlyrics.com/search.php?q=" + onlyldsongName;

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        progressBar.setVisibility(View.GONE);
                        if (searchedResult.size() >= 2) {
                            searchedResult.remove(0);
                            int size = searchedResult.size();
                            searchedResult.remove(size - 1);
                            fetchedResults.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        if(searchedResult.size() == 0)
                        {
                            mainContent.setVisibility(View.VISIBLE);
                            fetchedResults.setVisibility(View.GONE);

                            lyrics = "No results found, Sorry :(";
                            lyricsTextView.setText(lyrics);
                        }
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Document doc = Jsoup.connect(seachURL).get();
                            Log.e("DOC", doc.childNodeSize() + "");
                            Element table = doc.select("table").get(0);
                            Elements rows = table.select("tr");

                            abslinks = new String[rows.size()];
                            suggestedsongs = new String[rows.size()];
                            songartist = new String[rows.size()];

                            Log.d("Size", rows.size() + "");

                            for (int i = 0; i < rows.size(); i++) {
                                SearchLyricsResult lyricsResult = new SearchLyricsResult();

                                Element linkurl = rows.get(i).select("a").first();
                                Elements title = rows.get(i).select("b");
                                Element titleName = title.first();
                                Element artistName = title.last();

                                link = linkurl.attr("abs:href");

                                if (i == 0 && i == rows.size()) {

                                } else {
                                    suggestedsongs[i] = titleName.toString().replace("<b>", "").replace("</b>", "");
                                    songartist[i] = artistName.toString().replace("<b>", "").replace("</b>", "");
                                    abslinks[i] = link;
                                }

                                lyricsResult.setSongName(suggestedsongs[i]);
                                lyricsResult.setLyricArtist(songartist[i]);
                                lyricsResult.setLinkRef(abslinks[i]);

                                searchedResult.add(lyricsResult);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Oopsy, TimeOut.. Try Again later.", Toast.LENGTH_SHORT).show();
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
            }
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}
