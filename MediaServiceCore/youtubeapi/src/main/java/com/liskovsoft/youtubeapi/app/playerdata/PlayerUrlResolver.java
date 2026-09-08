package com.liskovsoft.youtubeapi.app.playerdata;

import com.liskovsoft.youtubeapi.common.helpers.AppClient;

public final class PlayerUrlResolver {
    private static final String TV_PLAYER_PATH = "/tv-player-";
    private static final String TV_TCL_PLAYER_PATH = "/tv-player-es6-tcl.vflset/tv-player-es6-tcl.js";
    private static final String MAIN_PLAYER_PATH = "/player_ias.vflset/en_US/base.js";

    private PlayerUrlResolver() {
    }

    public static String resolve(String playerUrl, AppClient client) {
        if (playerUrl == null || client == null) {
            return playerUrl;
        }

        int playerVariantStart = playerUrl.indexOf(TV_PLAYER_PATH);
        if (playerVariantStart < 0 && client.isTVClient()) {
            playerVariantStart = playerUrl.indexOf("/player_");
        }
        if (playerVariantStart < 0) {
            return playerUrl;
        }
        return playerUrl.substring(0, playerVariantStart)
                + (client.isTVClient() ? TV_TCL_PLAYER_PATH : MAIN_PLAYER_PATH);
    }
}