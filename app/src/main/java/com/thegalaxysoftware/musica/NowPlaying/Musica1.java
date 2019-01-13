package com.thegalaxysoftware.musica.NowPlaying;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.thegalaxysoftware.musica.Adapters.SlidingQueueAdapter;
import com.thegalaxysoftware.musica.MusicPlayer;
import com.thegalaxysoftware.musica.MusicService;
import com.thegalaxysoftware.musica.R;
import com.thegalaxysoftware.musica.Utils.ImageUtils;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class Musica1 extends BaseNowPlayingFragment {

    ImageView mBlurredArt;
    SlidingQueueAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_musica1, container, false);

        setMusicStateListener();
        setSongDetails(rootView);

        mBlurredArt = (ImageView) rootView.findViewById(R.id.album_art_blurred);
        initGestures(mBlurredArt);

        setupSlidingQueue();

        return rootView;
    }

    @Override
    public void updateShuffleState() {
        if(shuffle != null && getActivity() != null)
        {
            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                    .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
                    .setSizeDp(30);

            if(MusicPlayer.getShuffleMode() == 0)
            {
                builder.setColor(Color.WHITE);
            }
            else
            {
                builder.setColor(accentColor);
            }

            shuffle.setImageDrawable(builder.build());
            shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicPlayer.cycleShuffle();
                    updateShuffleState();
                    updateRepeatState();
                }
            });
        }
    }

    @Override
    public void updateRepeatState() {
        if (repeat != null && getActivity() != null) {
            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                    .setSizeDp(30);

            if (MusicPlayer.getRepeatMode() == 0) {
                builder.setColor(Color.WHITE);
            } else builder.setColor(accentColor);

            if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_NONE) {
                builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
                builder.setColor(Color.WHITE);
            } else if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_CURRENT) {
                builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE);
                builder.setColor(accentColor);
            } else if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_ALL) {
                builder.setColor(accentColor);
                builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
            }

            repeat.setImageDrawable(builder.build());
            repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MusicPlayer.cycleRepeat();
                    updateRepeatState();
                    updateShuffleState();
                }
            });
        }
    }

    @Override
    public void doAlbumArtStuff(Bitmap loadedImage) {
        setBlurredAlbumArt blurredAlbumArt = new setBlurredAlbumArt();
        blurredAlbumArt.execute(loadedImage);
    }

    private void setupSlidingQueue() {
    }

    private class setBlurredAlbumArt extends AsyncTask<Bitmap, Void, Drawable>
    {
        @Override
        protected Drawable doInBackground(Bitmap... loadedImages) {

            Drawable drawable = null;
            try {
                drawable = ImageUtils.createBlurredImageFromBitmap(loadedImages[0], getActivity(), 12);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if(result != null)
            {
                if(mBlurredArt.getDrawable() != null)
                {
                    final TransitionDrawable td = new TransitionDrawable(new Drawable[]{mBlurredArt.getDrawable(), result});
                    mBlurredArt.setImageDrawable(td);
                    td.startTransition(200);
                }
                else
                {
                    mBlurredArt.setImageDrawable(result);
                }
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }
}
