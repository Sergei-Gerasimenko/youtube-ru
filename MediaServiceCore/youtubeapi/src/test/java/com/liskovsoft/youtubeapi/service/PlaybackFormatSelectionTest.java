package com.liskovsoft.youtubeapi.service;

import com.liskovsoft.mediaserviceinterfaces.data.MediaItemFormatInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 28)
public class PlaybackFormatSelectionTest {
    @Test
    public void nullPrimaryTriesFallback() {
        FakeService service = new FakeService(null, format(false));
        assertSame(service.fallback, service.getFormatInfo("video"));
        assertEquals(1, service.fallbackCalls);
    }

    @Test
    public void restrictionDoesNotPoisonFallbackOrCache() {
        FakeService service = new FakeService(format(true), format(false));
        assertSame(service.fallback, service.getFormatInfo("video"));
        assertSame(service.fallback, service.getFormatInfo("video"));
        assertEquals(1, service.primaryCalls);
        assertEquals(1, service.fallbackCalls);
    }

    @Test
    public void playablePrimarySkipsFallback() {
        FakeService service = new FakeService(format(false), format(false));
        assertSame(service.primary, service.getFormatInfo("video"));
        assertEquals(0, service.fallbackCalls);
    }

    @Test
    public void failedFallbackPreservesOriginalRestriction() {
        FakeService service = new FakeService(format(true), format(true));
        assertSame(service.primary, service.getFormatInfo("video"));
        service.fallback = null;
        assertSame(service.primary, service.getFormatInfo("video"));
        assertEquals(2, service.primaryCalls);
    }

    @Test
    public void allNullResponsesRemainTransportFailure() {
        assertNull(new FakeService(null, null).getFormatInfo("video"));
    }

    @Test
    public void fallbackExceptionDoesNotTurnRestrictionIntoNetworkRetry() {
        FakeService service = new FakeService(format(true), null);
        service.fallbackError = new IllegalStateException("Fallback unavailable");
        assertSame(service.primary, service.getFormatInfo("video"));
    }

    @Test(expected = IllegalStateException.class)
    public void transportErrorIsPropagatedWhenNoServerResponseExists() {
        FakeService service = new FakeService(null, null);
        service.fallbackError = new IllegalStateException("Offline");
        service.getFormatInfo("video");
    }

    @Test
    public void invalidatingCacheRequestsFreshStreams() {
        FakeService service = new FakeService(format(false), null);
        service.getFormatInfo("video");
        service.invalidateCache();
        service.getFormatInfo("video");
        assertEquals(2, service.primaryCalls);
    }

    private static MediaItemFormatInfo format(boolean unplayable) {
        return (MediaItemFormatInfo) Proxy.newProxyInstance(MediaItemFormatInfo.class.getClassLoader(),
                new Class<?>[] {MediaItemFormatInfo.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "isUnplayable": return unplayable;
                        case "isCacheActual": return true;
                        case "getVideoId": return "video";
                        case "setClickTrackingParams": return null;
                        default: throw new AssertionError(method.getName());
                    }
                });
    }

    private static class FakeService extends YouTubeMediaItemService {
        final MediaItemFormatInfo primary;
        MediaItemFormatInfo fallback;
        RuntimeException fallbackError;
        int primaryCalls;
        int fallbackCalls;

        FakeService(MediaItemFormatInfo primary, MediaItemFormatInfo fallback) {
            this.primary = primary;
            this.fallback = fallback;
        }

        @Override
        MediaItemFormatInfo getFormatInfoLegacy(String videoId, String params) {
            primaryCalls++;
            return primary;
        }

        @Override
        MediaItemFormatInfo getFormatInfoInnertube(String videoId, String params) {
            fallbackCalls++;
            if (fallbackError != null) {
                throw fallbackError;
            }
            return fallback;
        }
    }
}
