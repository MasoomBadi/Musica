package com.thegalaxysoftware.musica.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.View;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.prefs.ATECheckBoxPreference;
import com.afollestad.appthemeengine.prefs.ATEColorPreference;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.thegalaxysoftware.musica.Activities.DonateActivity;
import com.thegalaxysoftware.musica.Activities.SettingActivity;
import com.thegalaxysoftware.musica.Dialogs.LastFmLoginDialog;
import com.thegalaxysoftware.musica.LastFMApi.LastFmClient;
import com.thegalaxysoftware.musica.R;
import com.thegalaxysoftware.musica.Utils.Constants;
import com.thegalaxysoftware.musica.Utils.NavigationUtils;
import com.thegalaxysoftware.musica.Utils.PreferencesUtility;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String NOW_PLAYING_SELECTOR = "now_playing_selector";
    private static final String LASTFM_LOGIN = "lastfm_login";

    private static final String LOCKSCREEN = "show_albumart_lockscreen";
    private static final String XPOSED = "toggle_xposed_trackselector";

    private static final String KEY_ABOUT = "preference_about";
    private static final String KEY_SOURCE = "preference_source";
    private static final String KEY_THEME = "theme_preference";
    private static final String TOGGLE_ANIMATIONS = "toggle_animations";
    private static final String TOGGLE_SYSTEM_ANIMATIONS = "toggle_system_animations";
    private static final String KEY_START_PAGE = "start_page_preference";
    private boolean lastFMlogedin;

    private Preference nowPlayingSelector, lockscreen, xposed, lastFMlogin;

    private SwitchPreference toggleAnimations;
    private ListPreference themePreference, startPagePreference;
    private PreferencesUtility mPreferences;
    private String mAteKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        mPreferences = PreferencesUtility.getsInstance(getActivity());

        lockscreen = findPreference(LOCKSCREEN);
        nowPlayingSelector = findPreference(NOW_PLAYING_SELECTOR);

        xposed = findPreference(XPOSED);

        lastFMlogin = findPreference(LASTFM_LOGIN);
        updateLastFM();


        startPagePreference = (ListPreference) findPreference(KEY_START_PAGE);
        nowPlayingSelector.setIntent(NavigationUtils.getNavigateToStyleSelectorIntent(getActivity(), Constants.SETTINGS_STYLE_SELECTOR_NOWPLAYING));

        setPreferenceClickListeners();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    private void setPreferenceClickListeners() {
        startPagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                switch ((String) newValue) {
                    case "last_opened":
                        mPreferences.setLastOpenedAsStartPagePreference(true);
                        break;
                    case "songs":
                        mPreferences.setLastOpenedAsStartPagePreference(false);
                        mPreferences.setStartPageIndex(0);
                        break;

                    case "albums":
                        mPreferences.setLastOpenedAsStartPagePreference(false);
                        mPreferences.setStartPageIndex(1);
                        break;

                    case "artists":
                        mPreferences.setLastOpenedAsStartPagePreference(false);
                        mPreferences.setStartPageIndex(2);
                        break;
                }
                return true;
            }
        });

        findPreference("support_development").setIntent(new Intent(getActivity(), DonateActivity.class));

        lockscreen.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Bundle extras = new Bundle();
                extras.putBoolean("lockscreen", (boolean) newValue);
                mPreferences.updateService(extras);
                return true;
            }
        });

        xposed.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Bundle extras = new Bundle();
                extras.putBoolean("xtrack", (boolean) newValue);
                mPreferences.updateService(extras);
                return true;
            }
        });

        lastFMlogin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (lastFMlogedin) {
                    LastFmClient.getInstance(getActivity()).logout();
                    Bundle extras = new Bundle();
                    extras.putString("lf_token","logout");
                    extras.putString("lf_user",null);
                    mPreferences.updateService(extras);
                    updateLastFM();
                } else {
                    LastFmLoginDialog lastFmLoginDialog = new LastFmLoginDialog();
                    lastFmLoginDialog.setTargetFragment(SettingsFragment.this, 0);
                    lastFmLoginDialog.show(getFragmentManager(), LastFmLoginDialog.FRAGMENT_NAME);

                }
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        invalidateSettings();
        ATE.apply(view, mAteKey);
    }

    public void invalidateSettings() {
        mAteKey = ((SettingActivity) getActivity()).getATEKey();

        ATEColorPreference primaryColorPref = (ATEColorPreference) findPreference("primary_color");
        primaryColorPref.setColor(Config.primaryColor(getActivity(), mAteKey), Color.BLACK);
        primaryColorPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new ColorChooserDialog.Builder((SettingActivity) getActivity(), R.string.primary_color)
                        .preselect(Config.primaryColor(getActivity(), mAteKey))
                        .show();

                return true;
            }
        });

        ATEColorPreference accentColorpref = (ATEColorPreference) findPreference("accent_color");
        accentColorpref.setColor(Config.accentColor(getActivity(), mAteKey), Color.BLACK);
        accentColorpref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new ColorChooserDialog.Builder((SettingActivity) getActivity(), R.string.accent_color)
                        .preselect(Config.accentColor(getActivity(), mAteKey))
                        .show();
                return true;
            }
        });

        findPreference("dark_theme").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Config.markChanged(getActivity(), "light_theme");
                Config.markChanged(getActivity(), "dark_theme");

                getActivity().recreate();
                return true;
            }
        });

        final ATECheckBoxPreference statusBarPref = (ATECheckBoxPreference) findPreference("colored_status_bar");
        final ATECheckBoxPreference navBarPref = (ATECheckBoxPreference) findPreference("colored_nav_bar");

        statusBarPref.setChecked(Config.coloredStatusBar(getActivity(), mAteKey));
        statusBarPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ATE.config(getActivity(), mAteKey)
                        .coloredStatusBar((Boolean)newValue)
                        .apply(getActivity());
                return true;
            }
        });

        navBarPref.setChecked(Config.coloredNavigationBar(getActivity(), mAteKey));
        navBarPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ATE.config(getActivity(), mAteKey)
                        .coloredNavigationBar((Boolean) newValue)
                        .apply(getActivity());

                return true;
            }
        });
    }
    public void updateLastFM() {
        String username = LastFmClient.getInstance(getActivity()).getUsername();
        if (username != null) {
            lastFMlogedin = true;
            lastFMlogin.setTitle("Logout");
            lastFMlogin.setSummary(String.format(getString(R.string.lastfm_loged_in),username));
        } else {
            lastFMlogedin = false;
            lastFMlogin.setTitle("Login");
            lastFMlogin.setSummary(getString(R.string.lastfm_pref));
        }
    }
}
