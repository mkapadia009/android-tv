package com.app.itaptv.roomDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MediaDAO {

    // Playlist Queries
    @Insert
    void savePlaylist(Playlist playlist);

    @Delete
    void deletePlaylist(List<Playlist> playlist);

    @Query("SELECT * from playlist")
    List<Playlist> getPlaylist();

    @Query("SELECT * from playlist")
    Playlist getPlaylistlll();

    @Update
    void updatePlaylist(Playlist playlist);

    // Player Duration Queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void savePlayerDuration(MediaDuration mediaDuration);

    @Query("SELECT * from MediaDuration where id == (:id) limit 1")
    List<MediaDuration> getMediaDurationData(String id);

    @Query("SELECT * from MediaDuration")
    List<MediaDuration> getAllMediaDurationData();

    @Update
    void updatePlayerDuration(MediaDuration mediaDuration);

    @Delete
    void deletePlayerDuration(List<MediaDuration> mediaDurations);

    //Analytics Queries
    @Insert
    void saveAnalytics(AnalyticsData analyticsData);

    @Delete
    void deleteAnalytics(List<AnalyticsData> analyticsData);

    @Query("SELECT * from AnalyticsData")
    List<AnalyticsData> getAnalytics();

    @Query("SELECT * from AnalyticsData")
    AnalyticsData getAnalyticsAll();

    @Update
    void updateAnalytics(AnalyticsData analyticsData);
}
