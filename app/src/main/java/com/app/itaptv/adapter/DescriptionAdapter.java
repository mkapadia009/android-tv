package com.app.itaptv.adapter;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.Nullable;

import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.utils.Log;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import java.io.IOException;
import java.net.URL;

/**
 * Created by poonam on 7/9/18.
 */

public class DescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter {

    Context context;
    Object object;

    public DescriptionAdapter() {
    }

    public DescriptionAdapter(Context context, Object object) {
        this.context = context;
        this.object = object;
    }

    @Override
    public String getCurrentContentTitle(Player player) {
        if (object instanceof FeedContentData) {
            return ((FeedContentData) object).postTitle;
        }
        return null;
    }

    @Nullable
    @Override
    public PendingIntent createCurrentContentIntent(Player player) {
        return null;
    }

    @Nullable
    @Override
    public String getCurrentContentText(Player player) {
        return null;
    }

    @Nullable
    @Override
    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
        return null;

    }

    private Bitmap setLargeIcon() {
        if (object instanceof FeedContentData) {
            FeedContentData feedContentData = (FeedContentData) object;
            Bitmap image = null;
            try {
                URL url = new URL(feedContentData.imgUrl);
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                Log.e("Exception", e.getMessage());
            }
            return image;
        }
        return null;
    }
}
