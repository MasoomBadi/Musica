package com.thegalaxysoftware.musica.Activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.ATEActivity;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.thegalaxysoftware.musica.GoogleCast.SimpleSessionManagerListener;
import com.thegalaxysoftware.musica.GoogleCast.WebServer;
import com.thegalaxysoftware.musica.IMusicaService;
import com.thegalaxysoftware.musica.Listeners.MusicStateListener;
import com.thegalaxysoftware.musica.MusicPlayer;
import com.thegalaxysoftware.musica.MusicService;
import com.thegalaxysoftware.musica.R;
import com.thegalaxysoftware.musica.SlidingUpPanel.SlidingUpPanelLayout;
import com.thegalaxysoftware.musica.SubFragments.QuickControlsFragment;
import com.thegalaxysoftware.musica.Utils.Helpers;
import com.thegalaxysoftware.musica.Utils.MusicaUtils;
import com.thegalaxysoftware.musica.Utils.NavigationUtils;

import static com.thegalaxysoftware.musica.MusicPlayer.mService;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BaseActivity extends ATEActivity implements ServiceConnection, MusicStateListener {

    private final ArrayList<MusicStateListener> mMusicStateListener = new ArrayList<>();
    private MusicPlayer.ServiceToken mToken;
    private PlaybackStatus mPlaybackStatus;

    private CastSession mCastSession;
    private SessionManager mSessionManager;
    private final SessionManagerListener mSessionManagerListener =
            new SessionManagerListenerImpl();
    private WebServer castServer;

    public boolean playServicesAvailable = false;

    private class SessionManagerListenerImpl extends SimpleSessionManagerListener {
        @Override
        public void onSessionStarting(Session session) {
            super.onSessionStarting(session);
            startCastServer();
        }

        @Override
        public void onSessionStarted(Session session, String s) {
            invalidateOptionsMenu();
            mCastSession = mSessionManager.getCurrentCastSession();
            showCastMiniController();
        }

        @Override
        public void onSessionResumed(Session session, boolean b) {
            invalidateOptionsMenu();
            mCastSession = mSessionManager.getCurrentCastSession();
        }

        @Override
        public void onSessionEnded(Session session, int i) {
            mCastSession = null;
            hideCastMiniController();
            stopCastServer();
        }

        @Override
        public void onSessionResuming(Session session, String s) {
            super.onSessionResuming(session, s);
            startCastServer();
        }

        @Override
        public void onSessionSuspended(Session session, int i) {
            super.onSessionSuspended(session, i);
            stopCastServer();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToken = MusicPlayer.bindtoService(this, this);

        mPlaybackStatus = new PlaybackStatus(this);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        try {
            playServicesAvailable = GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
        } catch (Exception ignored) {

        }

        if (playServicesAvailable)
            initCast();

    }

    @Override
    protected void onStart() {
        super.onStart();

        final IntentFilter filter = new IntentFilter();

        filter.addAction(MusicService.PLAYSTATE_CHANGED);
        filter.addAction(MusicService.META_CHANGED);
        filter.addAction(MusicService.REFRESH);
        filter.addAction(MusicService.PLAYLIST_CHANGED);
        filter.addAction(MusicService.TRACK_ERROR);

        registerReceiver(mPlaybackStatus, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (playServicesAvailable) {
            mCastSession = mSessionManager.getCurrentCastSession();
            mSessionManager.addSessionManagerListener(mSessionManagerListener);
        }

        if (mService == null) {
            mToken = MusicPlayer.bindtoService(this, this);
        }

        onMetaChanged();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playServicesAvailable) {
            mSessionManager.removeSessionManagerListener(mSessionManagerListener);
            mCastSession = null;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = IMusicaService.Stub.asInterface(service);
        onMetaChanged();
    }

    private void initCast() {
        CastContext castContext = CastContext.getSharedInstance(this);
        mSessionManager = castContext.getSessionManager();
    }

    @Override
    public void onServiceDisconnected(final ComponentName name) {
        mService = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mToken != null) {
            MusicPlayer.unbindFromService(mToken);
            mToken = null;
        }

        try {
            unregisterReceiver(mPlaybackStatus);
        } catch (final Throwable e) {

        }
        mMusicStateListener.clear();
    }

    @Override
    public void restartLoader() {
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.restartLoader();
            }
        }
    }

    @Override
    public void onPlaylistChanged() {

        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onPlaylistChanged();
            }
        }
    }

    @Override
    public void onMetaChanged() {

        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onMetaChanged();
            }
        }
    }

    public void setMusicStateListenerListener(final MusicStateListener status) {
        if (status == this) {
            throw new UnsupportedOperationException("Override the method, don't add a listener");
        }
        if (status != null) {
            mMusicStateListener.add(status);
        }
    }

    public void removeMusicStateListenerListener(final MusicStateListener status) {
        if (status != null) {
            mMusicStateListener.remove(status);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        getMenuInflater().inflate(R.menu.menu_cast, menu);

        if (playServicesAvailable) {
            CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                    menu, R.id.media_route_menu_item);
        }

        if (!MusicaUtils.hasEffectsPanel(BaseActivity.this)) {
            menu.removeItem(R.id.action_equalizer);
        }
        ATE.applyMenu(this, getATEKey(), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;

            case R.id.action_settings:
                NavigationUtils.navigateToSettings(this);
                return true;

            case R.id.action_shuffle:
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.shuffleAll(BaseActivity.this);
                    }
                }, 80);

                return true;

            case R.id.action_search:
                NavigationUtils.navigateToSearch(this);
                return true;
            case R.id.action_equalizer:
                NavigationUtils.navigateToEqualizer(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    protected String getATEKey() {
        return Helpers.getATEKey(this);
    }

    public void setPanelSlideListeners(SlidingUpPanelLayout panelLayout) {
        panelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(1 - slideOffset);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(1);
            }

            @Override
            public void onPanelExpanded(View panel) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(0);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
    }

    private final static class PlaybackStatus extends BroadcastReceiver
    {
        private final WeakReference<BaseActivity> mReference;

        public PlaybackStatus(final BaseActivity activity) {
            mReference = new WeakReference<BaseActivity>(activity);
        }


        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            BaseActivity baseActivity = mReference.get();

            if(baseActivity != null)
            {
                if(action.equals(MusicService.META_CHANGED))
                {
                    baseActivity.onMetaChanged();
                }
                else if(action.equals(MusicService.PLAYSTATE_CHANGED))
                {

                }
                else if(action.equals(MusicService.REFRESH))
                {
                    baseActivity.restartLoader();
                }
                else if(action.equals(MusicService.PLAYLIST_CHANGED))
                {
                    baseActivity.onPlaylistChanged();
                }
                else if(action.equals(MusicService.TRACK_ERROR))
                {
                    @SuppressLint({"StringFormatInvalid", "LocalSuppress"})
                    final String errorMsg = context.getString(R.string.error_playing_track,
                            intent.getStringExtra(MusicService.TrackErrorExtra.TRACK_NAME));

                    Toast.makeText(baseActivity, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class initQuickControls extends AsyncTask<String, Void, String >
    {
        @Override
        protected String doInBackground(String... strings) {
            QuickControlsFragment fragment1 = new QuickControlsFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.quickcontrols_container, fragment1).commitAllowingStateLoss();

            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
        }

        @Override
        protected void onPreExecute() {
        }
    }

    public void showCastMiniController() {
        //implement by overriding in activities
    }

    public void hideCastMiniController() {
        //implement by overriding in activities
    }

    public CastSession getCastSession() {
        return mCastSession;
    }

    private void startCastServer() {
        castServer = new WebServer(this);
        try {
            castServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopCastServer() {
        if (castServer != null) {
            castServer.stop();
        }
    }
}
