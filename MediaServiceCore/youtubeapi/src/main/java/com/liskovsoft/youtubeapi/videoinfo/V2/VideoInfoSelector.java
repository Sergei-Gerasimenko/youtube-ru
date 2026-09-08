package com.liskovsoft.youtubeapi.videoinfo.V2;

import com.liskovsoft.youtubeapi.common.helpers.AppClient;
import com.liskovsoft.youtubeapi.videoinfo.models.VideoInfo;

import java.util.Arrays;

final class VideoInfoSelector {
    interface Loader {
        VideoInfo load(AppClient client);
    }

    private VideoInfoSelector() {
    }

    static VideoInfo select(AppClient[] clients, AppClient firstClient, Loader loader) {
        int start = Math.max(0, Arrays.asList(clients).indexOf(firstClient));
        VideoInfo regularFallback = null;
        VideoInfo unavailable = null;

        for (int i = 0; i < clients.length; i++) {
            VideoInfo result = loader.load(clients[(start + i) % clients.length]);
            if (result == null) {
                continue;
            }
            if (!result.isUnplayable()) {
                return result;
            }
            if (regularFallback == null && result.getRegularFormats() != null && !result.getRegularFormats().isEmpty()
                    && !result.isAgeRestricted() && !result.isVisibilityRestricted() && !result.isUnknownRestricted()) {
                regularFallback = result;
            }
            // Prefer the authenticated response's access restriction.
            if (unavailable == null || (!unavailable.isAuth() && result.isAuth())) {
                unavailable = result;
            }
        }

        // Return the denial so the caller does not retry it as a network failure.
        return regularFallback != null ? regularFallback : unavailable;
    }
}
