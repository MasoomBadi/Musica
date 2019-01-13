package com.thegalaxysoftware.musica.Activities;

import android.media.AudioManager;
import android.os.Bundle;

import com.afollestad.appthemeengine.ATEActivity;
import com.thegalaxysoftware.musica.Utils.Helpers;

public class BaseThemedActivity extends ATEActivity {

    public String getATEKey()
    {
        return Helpers.getATEKey(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
}
