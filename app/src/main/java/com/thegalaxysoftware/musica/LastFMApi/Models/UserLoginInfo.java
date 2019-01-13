package com.thegalaxysoftware.musica.LastFMApi.Models;

import com.google.gson.annotations.SerializedName;

public class UserLoginInfo {

    private static final String SESSION = "session";

    @SerializedName(SESSION)
    public LastfmUserSession mSession;

}
