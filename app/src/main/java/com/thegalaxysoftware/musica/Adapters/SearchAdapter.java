package com.thegalaxysoftware.musica.Adapters;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.thegalaxysoftware.musica.BeanClass.Album;
import com.thegalaxysoftware.musica.BeanClass.Artist;
import com.thegalaxysoftware.musica.BeanClass.Song;
import com.thegalaxysoftware.musica.Dialogs.AddPlaylistDialog;
import com.thegalaxysoftware.musica.LastFMApi.Callbacks.ArtistInfoListener;
import com.thegalaxysoftware.musica.LastFMApi.LastFmClient;
import com.thegalaxysoftware.musica.LastFMApi.Models.ArtistQuery;
import com.thegalaxysoftware.musica.LastFMApi.Models.LastfmArtist;
import com.thegalaxysoftware.musica.MusicPlayer;
import com.thegalaxysoftware.musica.R;
import com.thegalaxysoftware.musica.Utils.MusicaUtils;
import com.thegalaxysoftware.musica.Utils.NavigationUtils;

import java.util.Collections;
import java.util.List;

public class SearchAdapter extends BaseSongAdapter<SearchAdapter.ItemHolder> {

    private Activity mContext;
    private List searchResults = Collections.emptyList();

    public SearchAdapter(Activity context) {
        this.mContext = context;

    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case 0:
                View v0 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, null);
                ItemHolder m10 = new ItemHolder(v0);
                return m10;

            case 1:
                View v1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_search, null);
                ItemHolder m11 = new ItemHolder(v1);
                return m11;

            case 2:
                View v2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist, null);
                ItemHolder m12 = new ItemHolder(v2);
                return m12;

            case 10:
                View v10 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_section_header, null);
                ItemHolder m110 = new ItemHolder(v10);
                return m110;

            default:
                View v3 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, null);
                ItemHolder m13 = new ItemHolder(v3);
                return m13;

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder itemHolder, int i) {
        switch (getItemViewType(i)) {
            case 0:
                Song song = (Song) searchResults.get(i);
                itemHolder.title.setText(song.title);
                itemHolder.songartist.setText(song.albumName);

                ImageLoader.getInstance().displayImage(MusicaUtils.getAlbumArtUri(song.albumId).toString(), itemHolder.albumArt,
                        new DisplayImageOptions.Builder().cacheInMemory(true)
                                .cacheOnDisk(true)
                                .showImageOnFail(R.drawable.defaultmusic)
                                .resetViewBeforeLoading(true)
                                .displayer(new FadeInBitmapDisplayer(400))
                                .build());

                setOnPopupMenuListener(itemHolder, i);
                break;

            case 1:
                Album album = (Album) searchResults.get(i);
                itemHolder.albumtitle.setText(album.title);
                itemHolder.albumartist.setText(album.artistName);

                ImageLoader.getInstance().displayImage(MusicaUtils.getAlbumArtUri(album.id).toString(), itemHolder.albumArt,
                        new DisplayImageOptions.Builder().cacheOnDisk(true)
                                .cacheOnDisk(true)
                                .showImageOnFail(R.drawable.defaultmusic)
                                .resetViewBeforeLoading(true)
                                .displayer(new FadeInBitmapDisplayer(400))
                                .build());

                break;

            case 2:
                Artist artist = (Artist) searchResults.get(i);
                itemHolder.artisttitle.setText(artist.name);
                String albumNmber = MusicaUtils.makeLabel(mContext, R.plurals.Nalbums, artist.albumCount);
                String songCount = MusicaUtils.makeLabel(mContext, R.plurals.Nsongs, artist.songCount);
                itemHolder.albumsongcount.setText(MusicaUtils.makeCombinedString(mContext, albumNmber, songCount));
                LastFmClient.getInstance(mContext).getArtistInfo(new ArtistQuery(artist.name), new ArtistInfoListener() {
                    @Override
                    public void artistInfoSucess(LastfmArtist artist) {
                        if (artist != null && itemHolder.artistImage != null) {
                            ImageLoader.getInstance().displayImage(artist.mArtwork.get(1).mUrl, itemHolder.artistImage,
                                    new DisplayImageOptions.Builder().cacheInMemory(true)
                                            .cacheOnDisk(true)
                                            .showImageOnFail(R.drawable.defaultmusic)
                                            .resetViewBeforeLoading(true)
                                            .displayer(new FadeInBitmapDisplayer(400))
                                            .build());
                        }
                    }

                    @Override
                    public void artistInfoFailed() {

                    }
                });
                break;

            case 10:
                itemHolder.sectionHeader.setText((String ) searchResults.get(i));

            case 3:
                break;
        }
    }

    @Override
    public void onViewRecycled(@NonNull ItemHolder holder) {
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position)
    {
        itemHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu menu = new PopupMenu(mContext,v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        long[] song = new long[1];
                        song[0] = ((Song) searchResults.get(position)).id;

                        switch (item.getItemId())
                        {
                            case R.id.popup_song_play:
                                MusicPlayer.playAll(mContext, song, 0,-1, MusicaUtils.IdType.NA,false);
                                break;

                            case R.id.popup_song_play_next:
                                MusicPlayer.playNext(mContext, song, -1, MusicaUtils.IdType.NA);
                                break;

                            case R.id.popup_song_goto_album:
                                NavigationUtils.navigateToAlbum(mContext, ((Song) searchResults.get(position)).albumId, null);
                                break;

                            case R.id.popup_song_goto_artist:
                                NavigationUtils.navigateToArtist(mContext, ((Song) searchResults.get(position)).artistId, null);
                                break;

                            case R.id.popup_song_addto_queue:
                                MusicPlayer.addToQueue(mContext, song, -1, MusicaUtils.IdType.NA);
                                break;

                            case R.id.popup_song_addto_playlist:
                                AddPlaylistDialog.newInstance(((Song) searchResults.get(position))).show(((AppCompatActivity) mContext).getSupportFragmentManager(), "ADD_PLAYLIST");
                                break;

                        }
                        return false;
                    }
                });

                menu.inflate(R.menu.popup_song);

                menu.getMenu().findItem(R.id.popup_song_delete).setVisible(false);
                menu.getMenu().findItem(R.id.popup_song_share).setVisible(false);

                menu.show();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (searchResults.get(position) instanceof Song)
            return 0;
        if (searchResults.get(position) instanceof Album)
            return 1;
        if (searchResults.get(position) instanceof Artist)
            return 2;
        if (searchResults.get(position) instanceof String)
            return 10;
        return 3;
    }

    public void updateSearchResults(List searchResults)
    {
        this.searchResults = searchResults;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title, songartist, albumtitle, artisttitle, albumartist, albumsongcount, sectionHeader;
        protected ImageView albumArt, artistImage, menu;

        public ItemHolder(View view) {
            super(view);

            this.title = (TextView) view.findViewById(R.id.song_title);
            this.songartist = (TextView) view.findViewById(R.id.song_artist);
            this.albumsongcount = (TextView) view.findViewById(R.id.album_song_count);
            this.artisttitle = (TextView) view.findViewById(R.id.artist_name);
            this.albumtitle = (TextView) view.findViewById(R.id.album_title);
            this.albumartist = (TextView) view.findViewById(R.id.album_artist);
            this.albumArt = (ImageView) view.findViewById(R.id.albumArt);
            this.artistImage = (ImageView) view.findViewById(R.id.artistImage);
            this.menu = (ImageView) view.findViewById(R.id.popup_menu);

            this.sectionHeader = (TextView) view.findViewById(R.id.section_header);


            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (getItemViewType())
            {
                case 0:
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            long[] ret = new long[1];
                            ret[0] = ((Song) searchResults.get(getAdapterPosition())).id;
                            playAll(mContext, ret, 0, -1, MusicaUtils.IdType.NA,
                                    false, (Song) searchResults.get(getAdapterPosition()), false);
                        }
                    },100);
                    break;

                case 1:
                    NavigationUtils.goToAlbum(mContext, ((Album) searchResults.get(getAdapterPosition())).id);
                    break;

                case 2:
                    NavigationUtils.goToArtist(mContext, ((Artist) searchResults.get(getAdapterPosition())).id);
                    break;

                case 3:
                    break;

                case 10:
                    break;
            }
        }
    }
}
