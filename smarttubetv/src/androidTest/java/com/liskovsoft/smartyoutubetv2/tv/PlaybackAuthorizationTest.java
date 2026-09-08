package com.liskovsoft.smartyoutubetv2.tv;

import android.os.Bundle;

import androidx.test.platform.app.InstrumentationRegistry;

import com.liskovsoft.googlecommon.common.helpers.DefaultHeaders;
import com.liskovsoft.mediaserviceinterfaces.data.MediaFormat;
import com.liskovsoft.mediaserviceinterfaces.data.MediaItemFormatInfo;
import com.liskovsoft.sharedutils.okhttp.OkHttpManager;
import com.liskovsoft.youtubeapi.service.YouTubeMediaItemService;
import com.liskovsoft.youtubeapi.service.YouTubeSignInService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

/** Live checks: pass -e videoId VIDEO_ID after signing in; -e probeStreams true also checks DASH bytes. */
public class PlaybackAuthorizationTest {
    @Test
    public void selectedAccountReceivesPlayableFormatInfo() {
        MediaItemFormatInfo info = loadVideo();
        assertTrue("No usable stream in player response", info.containsDashFormats() || info.containsSabrFormats()
                || info.containsHlsUrl() || info.containsUrlFormats());
    }

    @Test
    public void selectedDashStreamsDownloadAudioAndVideo() throws Exception {
        assumeNotNull(InstrumentationRegistry.getArguments().getString("probeStreams"));
        MediaItemFormatInfo info = loadVideo();
        assertNotNull("No DASH formats to probe", info.getAdaptiveFormats());
        MediaFormat audio = null;
        MediaFormat video = null;
        for (MediaFormat format : info.getAdaptiveFormats()) {
            if (format.getUrl() == null || format.getMimeType() == null) continue;
            if (audio == null && format.getMimeType().startsWith("audio/")) audio = format;
            if (video == null && format.getMimeType().startsWith("video/")) video = format;
        }
        assertNotNull("No audio stream", audio);
        assertNotNull("No video stream", video);
        OkHttpClient http = OkHttpManager.instance().getClient().newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false).build();
        checkMediaBytes(http, audio);
        checkMediaBytes(http, video);
    }

    private static void checkMediaBytes(OkHttpClient http, MediaFormat format) throws Exception {
        // Match the User-Agent used by ExoMediaSourceFactory.
        try (Response response = http.newCall(new Request.Builder().url(format.getUrl())
                .header("User-Agent", DefaultHeaders.APP_USER_AGENT)
                .header("Range", "bytes=0-1023").build()).execute()) {
            report(format.getMimeType() + ": HTTP=" + response.code());
            assertTrue("Media server returned HTTP " + response.code(), response.isSuccessful());
            assertNotNull("Empty media response", response.body());
            assertTrue("Missing media bytes", response.body().source().request(1));
            String contentType = response.header("Content-Type");
            assertTrue("Unexpected media content type: " + contentType,
                    contentType != null && contentType.startsWith(format.getMimeType().split("/")[0] + "/"));
        }
    }

    private static MediaItemFormatInfo loadVideo() {
        String videoId = InstrumentationRegistry.getArguments().getString("videoId");
        assumeNotNull(videoId);
        YouTubeSignInService signIn = YouTubeSignInService.instance();
        assertTrue("Sign in before running an authenticated playback check", signIn.isSigned());
        signIn.checkAuth();
        YouTubeMediaItemService.instance().invalidateCache();
        MediaItemFormatInfo info = YouTubeMediaItemService.instance().getFormatInfo(videoId);
        assertNotNull("No player response", info);
        assertFalse(info.getPlayabilityReason(), info.isUnplayable());
        report("Selected client: " + info.getClientInfo().getClientName()
                + ", DASH=" + info.containsDashFormats() + ", SABR=" + info.containsSabrFormats());
        return info;
    }

    private static void report(String text) {
        Bundle status = new Bundle();
        status.putString("stream", "\n" + text + "\n");
        InstrumentationRegistry.getInstrumentation().sendStatus(0, status);
    }
}
