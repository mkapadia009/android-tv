package com.app.itaptv.roomDatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.app.itaptv.structure.FeedContentData;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "Playlist")
public class Playlist {

    @PrimaryKey(autoGenerate = true)
    int id;
    int position;
    List<FeedContentData> playListItems;
    public String postId;
    public boolean iswatchlisted;
    public String feed_id;
    public String page_type;
    int totalNumberOfRecords;
    public String currentSeasonId;

    public String getCurrentSeasonId() {
        return currentSeasonId;
    }

    public void setCurrentSeasonId(String currentSeasonId) {
        this.currentSeasonId = currentSeasonId;
    }

    public int getTotalNumberOfRecords() {
        return totalNumberOfRecords;
    }

    public void setTotalNumberOfRecords(int totalNumberOfRecords) {
        this.totalNumberOfRecords = totalNumberOfRecords;
    }

    public FeedContentData getFeedContentDataSeries() {
        return feedContentDataSeries;
    }

    public void setFeedContentDataSeries(FeedContentData feedContentDataSeries) {
        this.feedContentDataSeries = feedContentDataSeries;
    }

    public FeedContentData feedContentDataSeries;
    public List<FeedContentData> arrayListContentData = new ArrayList<>();
    public List<FeedContentData> arrayListEpisodeData = new ArrayList<>();
    public List<FeedContentData> arrayListPlaylistData = new ArrayList<>();

    public List<FeedContentData> getArrayListContentData() {
        return arrayListContentData;
    }

    public void setArrayListContentData(List<FeedContentData> arrayListContentData) {
        this.arrayListContentData = arrayListContentData;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public List<FeedContentData> getArrayListEpisodeData() {
        return arrayListEpisodeData;
    }

    public void setArrayListEpisodeData(ArrayList<FeedContentData> arrayListEpisodeData) {
        this.arrayListEpisodeData = arrayListEpisodeData;
    }

    public List<FeedContentData> getArrayListPlaylistData() {
        return arrayListPlaylistData;
    }

    public void setArrayListPlaylistData(ArrayList<FeedContentData> arrayListPlaylistData) {
        this.arrayListPlaylistData = arrayListPlaylistData;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
    }

    public String getPage_type() {
        return page_type;
    }

    public void setPage_type(String page_type) {
        this.page_type = page_type;
    }

    public void setPlayListItems(List<FeedContentData> playListItems) {
        this.playListItems = playListItems;
    }

    public List<FeedContentData> getPlayListItems() {
        return playListItems;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isIswatchlisted() {
        return iswatchlisted;
    }

    public void setIswatchlisted(boolean iswatchlisted) {
        this.iswatchlisted = iswatchlisted;
    }
}
