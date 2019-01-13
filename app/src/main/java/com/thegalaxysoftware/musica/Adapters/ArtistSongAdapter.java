package com.thegalaxysoftware.musica.Adapters;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.thegalaxysoftware.musica.BeanClass.Song;
import com.thegalaxysoftware.musica.Dataloaders.ArtistAlbumLoader;
import com.thegalaxysoftware.musica.Dialogs.AddPlaylistDialog;
import com.thegalaxysoftware.musica.MusicPlayer;
import com.thegalaxysoftware.musica.R;
import com.thegalaxysoftware.musica.Utils.MusicaUtils;
import com.thegalaxysoftware.musica.Utils.NavigationUtils;

import java.util.ArrayList;
import java.util.List;

public class ArtistSongAdapter extends BaseSongAdapter<ArtistSongAdapter.ItemHolder> {

    private List<Song> arraylist;
    private Activity mContext;
    private long artistID;
    private long[] songIDs;

    public ArtistSongAdapter(Activity context, List<Song> arraylist, long artistID) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.artistID = artistID;
        this.songIDs = getSongIds();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == 0)
        {
            View v0 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.artist_detail_albums_header, null);
            ItemHolder m1 = new ItemHolder(v0);
            return  m1;
        }
        else
        {
            View v2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist_song, null);
            ItemHolder m2 = new ItemHolder(v2);
            return m2;
        }
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        if(getItemViewType(i) == 0)
        {
            setUpAlbums(itemHolder.albumsRecyclerView);
        }
        else
        {
            Song localItem = arraylist.get(i);
            itemHolder.title.setText(localItem.title);
            itemHolder.album.setText(localItem.albumName);

            ImageLoader.getInstance().displayImage(MusicaUtils.getAlbumArtUri(localItem.albumId).toString(),
                    itemHolder.albumArt, new DisplayImageOptions.Builder()
                    .cacheInMemory(true).showImageOnLoading(R.drawable.defaultmusic).resetViewBeforeLoading(true).build());
            setOnPopupMenuListener(itemHolder, i - 1);
        }
    }

    @Override
    public void onViewRecycled(ItemHolder itemHolder) {
        if (itemHolder.getItemViewType() == 0)
            clearExtraSpacingBetweenCards(itemHolder.albumsRecyclerView);
    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position)
    {
        itemHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu menu = new PopupMenu(mContext, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.popup_song_play:
                                MusicPlayer.playAll(mContext, songIDs, position + 1, -1, MusicaUtils.IdType.NA, false);
                                break;

                            case R.id.popup_song_play_next:
                                long[] ids = new long[1];
                                ids[0] = arraylist.get(position + 1).id;
                                MusicPlayer.playNext(mContext, ids, -1, MusicaUtils.IdType.NA);
                                break;

                            case R.id.popup_song_goto_album:
                                NavigationUtils.goToAlbum(mContext, arraylist.get(position + 1).albumId);
                                break;

                            case R.id.popup_song_goto_artist:
                                NavigationUtils.goToArtist(mContext, arraylist.get(position + 1).artistId);
                                break;

                            case R.id.popup_song_addto_queue:
                                long[] id = new long[1];
                                id[0] = arraylist.get(position + 1).id;
                                MusicPlayer.addToQueue(mContext, id, -1, MusicaUtils.IdType.NA);
                                break;

                            case R.id.popup_song_addto_playlist:
                                AddPlaylistDialog.newInstance(arraylist.get(position + 1)).show(((AppCompatActivity) mContext).getSupportFragmentManager(), "ADD_PLAYLIST");
                                break;

                            case R.id.popup_song_share:
                                MusicaUtils.shareTrack(mContext, arraylist.get(position + 1).id);
                                break;

                            case R.id.popup_song_delete:
                                long[] deleteIds = {arraylist.get(position + 1).id};
                                MusicaUtils.showDeleteDialog(mContext,arraylist.get(position + 1).title, deleteIds, ArtistSongAdapter.this, position + 1);
                                break;
                        }
                        return false;
                    }
                });

                menu.inflate(R.menu.popup_song);
                menu.show();
            }
        });
    }

    private void setUpAlbums(RecyclerView albumsRecyclerview)
    {
        albumsRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        albumsRecyclerview.setHasFixedSize(true);

        int spacingInPixels = mContext.getResources().getDimensionPixelSize(R.dimen.spacing_card);
        albumsRecyclerview.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        albumsRecyclerview.setNestedScrollingEnabled(false);

        ArtistAlbumAdapter mAlbumAdapter = new ArtistAlbumAdapter(mContext, ArtistAlbumLoader.getAlbumsForArtist(mContext, artistID));
        albumsRecyclerview.setAdapter(mAlbumAdapter);
    }

    private void clearExtraSpacingBetweenCards(RecyclerView albumsRecyclerview)
    {
       int spacingInPixelstoClear = -(mContext.getResources().getDimensionPixelSize(R.dimen.spacing_card));
       albumsRecyclerview.addItemDecoration(new SpacesItemDecoration(spacingInPixelstoClear));
    }

    public long[] getSongIds() {
        List<Song> actualArraylist = new ArrayList<Song>(arraylist);
        long[] ret = new long[actualArraylist.size()];
        for (int i = 0; i < actualArraylist.size(); i++) {
            ret[i] = actualArraylist.get(i).id;
        }
        return ret;
    }

    @Override
    public void removeSongAt(int i) {
        arraylist.remove(i);
        updateDataSet(arraylist);
    }

    @Override
    public void updateDataSet(List<Song> arraylist) {
        this.arraylist = arraylist;
        this.songIDs = getSongIds();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;

        if(position == 0)
        {
            viewType = 0;
        }
        else viewType = 1;
        return viewType;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        protected TextView title, album;
        protected ImageView albumArt, menu;
        protected RecyclerView albumsRecyclerView;

        public ItemHolder(View view) {
            super(view);

            this.albumsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_album);

            this.title = (TextView) view.findViewById(R.id.song_title);
            this.album = (TextView) view.findViewById(R.id.song_album);
            this.albumArt = (ImageView) view.findViewById(R.id.albumArt);
            this.menu = (ImageView) view.findViewById(R.id.popup_menu);


            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playAll(mContext, songIDs, getAdapterPosition(), artistID,
                            MusicaUtils.IdType.Artist, false,
                            arraylist.get(getAdapterPosition()), true);
                }
            }, 100);

        }
    }
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
        }
    }
}
