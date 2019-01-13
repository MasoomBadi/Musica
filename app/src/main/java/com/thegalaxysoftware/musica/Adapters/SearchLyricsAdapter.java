package com.thegalaxysoftware.musica.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.thegalaxysoftware.musica.BeanClass.SearchLyricsResult;
import com.thegalaxysoftware.musica.Fragments.LyricsFragment;
import com.thegalaxysoftware.musica.Fragments.PlaylistFragment;
import com.thegalaxysoftware.musica.R;

import java.util.ArrayList;
import java.util.List;

public class SearchLyricsAdapter extends RecyclerView.Adapter<SearchLyricsAdapter.ItemHolder> {

    private ArrayList<SearchLyricsResult> resultList;
    private Activity mContext;
    SearchOnClickListener onClickListener;

    public SearchLyricsAdapter(Activity mContext, ArrayList<SearchLyricsResult> mlist, SearchOnClickListener listener)
    {
        this.mContext = mContext;
        this.resultList = mlist;
        this.onClickListener = listener;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_lyrics_result,null);
        ItemHolder m1 = new ItemHolder(view);
        return m1;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int position) {
        SearchLyricsResult localItem = resultList.get(position);

        itemHolder.songname.setText(localItem.songName);
        itemHolder.artistsname.setText("By - " +localItem.lyricArtist);
    }

    @Override
    public int getItemCount() {
        return (null != resultList ? resultList.size() : 0);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        protected TextView songname, artistsname;

        public ItemHolder(View view)
        {
            super(view);
            this.songname = (TextView)view.findViewById(R.id.searchlyrics_songname);
            this.artistsname = (TextView)view.findViewById(R.id.searchlyrics_artist);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onViewClicked(getAdapterPosition());
                }
            });

        }
    }

    public interface SearchOnClickListener
    {
       void onViewClicked(int pos);
    }
}
