package com.app.itaptv.custom_interface;

import com.app.itaptv.structure.FeedContentData;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackException;

/**
 * Created by poonam on 5/10/18.
 */

public interface PlaybackStateListener {
    void adStarted();

    void adCompleted();

    void songChanged(int position, FeedContentData media,String context_id,String context,String iswatched);

    void playerStateChanged(boolean playWhenReady, int state);

    void playerError(PlaybackException error);

    void playerError(ExoPlaybackException error);

    void playerClosed();
}
