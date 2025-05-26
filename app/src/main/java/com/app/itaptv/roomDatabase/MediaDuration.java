package com.app.itaptv.roomDatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "MediaDuration")
public class MediaDuration {

    @PrimaryKey
    @NonNull
    private String id;
    private long duration;
    private long playedDuration;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long totalDuration) {
        this.duration = totalDuration;
    }

    public long getPlayedDuration() {
        return playedDuration;
    }

    public void setPlayedDuration(long playedDuration) {
        this.playedDuration = playedDuration;
    }

}