package com.app.itaptv.activity;

import androidx.annotation.Nullable;

import com.app.itaptv.structure.FeedData;

public class YouTubeDataSource {

    public @Nullable
    FeedData feedData;

    /*public @NonNull
    ArrayList<FeedContentData> feedContentDataList = new ArrayList<>();*/

    private YouTubeDataSource() {
    }

    private static YouTubeDataSource dataSource;

    public static synchronized YouTubeDataSource getInstance() {
        if (dataSource == null) {
            dataSource = new YouTubeDataSource();
        }
        return dataSource;
    }

}
