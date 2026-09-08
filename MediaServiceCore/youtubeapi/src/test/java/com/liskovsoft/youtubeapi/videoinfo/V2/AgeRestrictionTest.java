package com.liskovsoft.youtubeapi.videoinfo.V2;

import com.liskovsoft.youtubeapi.service.data.YouTubeMediaItemFormatInfo;
import com.liskovsoft.youtubeapi.videoinfo.models.VideoInfo;
import com.liskovsoft.youtubeapi.videoinfo.models.formats.RegularVideoFormat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 28)
public class AgeRestrictionTest {
    @Test
    public void allAgeDenialsRemainUnplayableEvenWithLeftoverFormats() throws Exception {
        for (String status : new String[] {"LOGIN_REQUIRED", "AGE_CHECK_REQUIRED",
                "AGE_VERIFICATION_REQUIRED", "CONTENT_CHECK_REQUIRED"}) {
            VideoInfo info = new VideoInfo();
            set(info, "mPlayabilityStatus", status);
            assertTrue(status, info.isAgeRestricted());
            assertTrue(status, info.isUnplayable());

            set(info, "mRegularFormats", Collections.singletonList(new RegularVideoFormat()));
            assertTrue(status, YouTubeMediaItemFormatInfo.from(info).isUnplayable());
        }
    }

    private static void set(VideoInfo info, String fieldName, Object value) throws Exception {
        Field field = VideoInfo.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(info, value);
    }
}
