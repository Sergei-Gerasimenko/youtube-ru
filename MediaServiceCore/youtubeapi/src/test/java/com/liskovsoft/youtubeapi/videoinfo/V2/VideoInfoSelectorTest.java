package com.liskovsoft.youtubeapi.videoinfo.V2;

import com.liskovsoft.youtubeapi.common.helpers.AppClient;
import com.liskovsoft.youtubeapi.videoinfo.models.VideoInfo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class VideoInfoSelectorTest {
    private static final AppClient[] CLIENTS = {AppClient.WEB_EMBED, AppClient.VISIONOS, AppClient.TV_DOWNGRADED};

    @Test
    public void keepsAuthenticatedDenialInsteadOfReturningNull() {
        VideoInfo anonymous = response(true, false);
        VideoInfo signed = response(true, true);
        List<AppClient> calls = new ArrayList<>();
        VideoInfo selected = VideoInfoSelector.select(CLIENTS, CLIENTS[0], client -> {
            calls.add(client);
            return client == AppClient.TV_DOWNGRADED ? signed : anonymous;
        });
        assertSame(signed, selected);
        assertEquals(Arrays.asList(CLIENTS), calls);
    }

    @Test
    public void laterPlayableClientWinsOverRestriction() {
        VideoInfo restricted = response(true, false);
        VideoInfo playable = response(false, true);
        assertSame(playable, VideoInfoSelector.select(CLIENTS, CLIENTS[0], client ->
                client == AppClient.TV_DOWNGRADED ? playable : restricted));
    }

    @Test
    public void rotatesClientsOnceWithoutRepeatingRequests() {
        List<AppClient> calls = new ArrayList<>();
        assertNull(VideoInfoSelector.select(CLIENTS, AppClient.VISIONOS, client -> {
            calls.add(client);
            return null;
        }));
        assertEquals(Arrays.asList(AppClient.VISIONOS, AppClient.TV_DOWNGRADED, AppClient.WEB_EMBED), calls);
    }

    @Test
    public void playableResponseStopsClientRotation() {
        VideoInfo playable = response(false, false);
        List<AppClient> calls = new ArrayList<>();
        assertSame(playable, VideoInfoSelector.select(CLIENTS, CLIENTS[0], client -> {
            calls.add(client);
            return playable;
        }));
        assertEquals(1, calls.size());
    }

    private static VideoInfo response(boolean unplayable, boolean auth) {
        VideoInfo result = new VideoInfo() {
            @Override
            public boolean isUnplayable() {
                return unplayable;
            }
        };
        result.setAuth(auth);
        return result;
    }
}
