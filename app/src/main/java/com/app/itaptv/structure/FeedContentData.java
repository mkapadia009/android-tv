package com.app.itaptv.structure;

import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.app.itaptv.custom_interface.PlayableMedia;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.ExoUtil;
import com.app.itaptv.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by poonam on 30/8/18.
 */
@Entity(tableName = "playlist_details")
public class FeedContentData implements PlayableMedia, Parcelable {

    public String image = "";
    public String tvImage = "";
    public String contentType = "";
    public String url = "";
    public String licenceUrl = "";
    public String seriesId = "";
    public String seasonId = "";
    public String episodeId = "";
    public ItemData itemData = null;

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;
    @ColumnInfo(name = "postId")
    public String postId = "";
    public String postAuthor = "";
    public String postDate = "";
    public String postDateGmt = "";
    public String postContent = "";
    public String postTitle = "";
    public String postExcerpt = "";
    public String arrowLinking = "";
    public String buttonText = "";
    public String originalExcerpt = "";
    public String postStatus = "";
    public String commentStatus = "";
    public String pingStatus = "";
    public String postName = "";
    public String toPing = "";
    public String pinged = "";
    public String postModified = "";
    public String postModifiedGmt = "";
    public String postContentFiltered = "";
    public String postDrmProtected = "";
    public String guid = "";
    public String postType = "";
    public String postMimeType = "";
    public String filter = "";
    public String imgUrl = "";
    public String horizontalRectangleImg = "";
    public String channelImageUrl = "";
    public String channelName = "";
    public String playlistImageUrl = "";
    public String playlistName = "";
    public String googleShortUrl = "";
    public String averageRating = "";
    public String displayName = "";
    public String coins = "";
    public String seriesName = "";
    public String seasonName = "";
    public String episodeNumber = "";
    public String likesCount = "0";
    public String scheduledTime = "";
    public String mediaType = "";
    public String skippableInSecs = "";
    public boolean likeByMe = false;
    public boolean canIUse = false;
    public boolean iswatchlisted = false;
    public boolean isSelected = false;
    public boolean can_i_use = true;
    public boolean skippable = false;
    public String contentOrientation = "";
    public String reward = "";
    public boolean singleCredit = false;
    public boolean isCategoryCoin = false;
    public int categoryId;
    public boolean isFocused = false;
    public View view;
    public String tileShape = "";

    protected FeedContentData(Parcel in) {
        image = in.readString();
        tvImage = in.readString();
        contentType = in.readString();
        url = in.readString();
        licenceUrl = in.readString();
        seriesId = in.readString();
        seasonId = in.readString();
        episodeId = in.readString();
        id = in.readInt();
        postId = in.readString();
        postAuthor = in.readString();
        postDate = in.readString();
        postDateGmt = in.readString();
        postContent = in.readString();
        postTitle = in.readString();
        postExcerpt = in.readString();
        arrowLinking = in.readString();
        buttonText = in.readString();
        originalExcerpt = in.readString();
        postStatus = in.readString();
        commentStatus = in.readString();
        pingStatus = in.readString();
        postName = in.readString();
        toPing = in.readString();
        pinged = in.readString();
        postModified = in.readString();
        postModifiedGmt = in.readString();
        postContentFiltered = in.readString();
        postDrmProtected = in.readString();
        guid = in.readString();
        postType = in.readString();
        postMimeType = in.readString();
        filter = in.readString();
        imgUrl = in.readString();
        horizontalRectangleImg = in.readString();
        channelImageUrl = in.readString();
        channelName = in.readString();
        playlistImageUrl = in.readString();
        playlistName = in.readString();
        googleShortUrl = in.readString();
        averageRating = in.readString();
        displayName = in.readString();
        coins = in.readString();
        seriesName = in.readString();
        seasonName = in.readString();
        episodeNumber = in.readString();
        likesCount = in.readString();
        scheduledTime = in.readString();
        likeByMe = in.readByte() != 0;
        canIUse = in.readByte() != 0;
        iswatchlisted = in.readByte() != 0;
        isSelected = in.readByte() != 0;
        can_i_use = in.readByte() != 0;
        skippable = in.readByte() != 0;
        singleCredit = in.readByte() != 0;
        isEpisode = in.readByte() != 0;
        postParent = in.readInt();
        menuOrder = in.readInt();
        commentCount = in.readInt();
        ptype = in.readInt();
        trendingScore = in.readInt();
        flag = in.readInt();
        attachmentData = in.readParcelable(AttachmentData.class.getClassLoader());
        attachmentTeaserPromoData = in.readParcelable(AttachmentData.class.getClassLoader());
        arrayListTeaserData = in.createTypedArrayList(AttachmentData.CREATOR);
        questionData = in.readParcelable(QuestionData.class.getClassLoader());
        adFieldsData = in.readParcelable(AdFieldsData.class.getClassLoader());
        arrayListQuestionData = in.createTypedArrayList(QuestionData.CREATOR);
        adsArray = in.createTypedArrayList(FeedContentData.CREATOR);
        arrayListAdFieldsData = in.createTypedArrayList(AdFieldsData.CREATOR);
        eventsData = in.createTypedArrayList(EventsData.CREATOR);
        isAdLoaded = in.readByte() != 0;
        name = in.readString();
        slug = in.readString();
        taxonomy = in.readString();
        description = in.readString();
        termId = in.readInt();
        termGroup = in.readInt();
        termTaxonomyId = in.readInt();
        parent = in.readInt();
        count = in.readInt();
        feedContentType = in.readString();
        feedItemFrom = in.readString();
        feedTabType = in.readString();
        adType = in.readString();
        externalUrl = in.readString();
        feedPosition = in.readInt();
        songPosition = in.readInt();
        teaserDescription = in.readString();
        teaserImage = in.readString();
        seasonCount = in.readString();
        totalEpisodes = in.readString();
        customText = in.readString();
        totalItems = in.readString();
        duration = in.readString();
        mediaType = in.readString();
        skippableInSecs = in.readString();
        arrayListPriceData = in.createTypedArrayList(PriceData.CREATOR);
        arrayListSeasonData = in.createTypedArrayList(SeasonData.CREATOR);
        // arrayListPaidSeasonData = in.createTypedArrayList(PaidSeasonData.CREATOR);
        seasonData = in.readParcelable(SeasonData.class.getClassLoader());
        isAdMarkerUpdated = in.readByte() != 0;
        nextSeries = in.readParcelable(NextSeries.class.getClassLoader());
        isTrailer = in.readByte() != 0;
        contentOrientation = in.readString();
        reward = in.readString();
        isFocused = in.readByte() != 0;
    }

    public static final Creator<FeedContentData> CREATOR = new Creator<FeedContentData>() {
        @Override
        public FeedContentData createFromParcel(Parcel in) {
            return new FeedContentData(in);
        }

        @Override
        public FeedContentData[] newArray(int size) {
            return new FeedContentData[size];
        }
    };

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isEpisode = false;
    public int postParent;
    public int menuOrder;
    public int commentCount;// String from API
    public int ptype; // String from API
    public int trendingScore; //String from API
    public int flag; // String from API
    public AttachmentData attachmentData = null;
    public AttachmentData attachmentTeaserPromoData = null;
    public ArrayList<AttachmentData> arrayListTeaserData = new ArrayList<>();
    public QuestionData questionData = null;
    public ArrayList<QuestionData> arrayListQuestionData = new ArrayList<>();
    public ArrayList<FeedContentData> adsArray = new ArrayList<>();
    public ArrayList<EventsData> eventsData = new ArrayList<>();
    public AdFieldsData adFieldsData = null;
    public ArrayList<AdFieldsData> arrayListAdFieldsData = new ArrayList<>();
    public boolean isAdLoaded = false;

    public String name = "";
    public String slug = "";
    public String taxonomy = "";
    public String description = "";
    public int termId;
    public int termGroup;
    public int termTaxonomyId;
    public int parent;
    public int count;

    public String feedContentType = "";
    public String feedItemFrom = "";
    public String feedTabType = "";
    public String adType = "";
    public String externalUrl = "";
    public int feedPosition;
    public int songPosition;


    // Buy Tab additional data
    public String teaserDescription = "";
    //public String teaserPromo = "";
    public String teaserImage = "";
    public String seasonCount = "";
    public String totalEpisodes = "";
    public String customText = "";
    public String totalItems = "";
    public String duration = "";

    public ArrayList<PriceData> arrayListPriceData = new ArrayList<>();
    public ArrayList<SeasonData> arrayListSeasonData = new ArrayList<>();
    // public ArrayList<PaidSeasonData> arrayListPaidSeasonData = new ArrayList<>();
    public SeasonData seasonData;

    public static String TAG = "FeedContentData";

    // Content types
    public static final String CONTENT_TYPE_CATEGORIES = "categories";
    public static final String CONTENT_TYPE_AUDIO_POSTS = "audio_posts";
    public static final String CONTENT_TYPE_ORIGINALS_LIST = "originals_list";
    public static final String CONTENT_TYPE_STREAMS = "streams";
    public static final String CONTENT_TYPE_POSTS = "posts";
    public static final String CONTENT_TYPE_CHANNEL = "channel";
    public static final String CONTENT_TYPE_EPISODE = "episode";
    public static final String CONTENT_TYPE_SEASON = "season";
    public static final String CONTENT_TYPE_LIVE = "live";
    public static final String CONTENT_TYPE_ALL = "all";
    public static final String CONTENT_TYPE_AD = "ad";
    public static final String CONTENT_TYPE_URL = "url";
    public static final String CONTENT_TYPE_AUDIO_POST = "audio_post";
    public static final String CONTENT_TYPE_PLAYLIST = "playlist";
    public static final String CONTENT_TYPE_GAME = "trivia";
    public static final String CONTENT_TYPE_ESPORTS = "esports";

    // Sub Type
    public static final String TYPE_SUBCATEGORY = "subcategory";
    public static final String TYPE_ORIGINALS = "originals";

    // Post Type
    public static final String POST_TYPE_POST = "post";
    public static final String POST_TYPE_STREAM = "stream";
    public static final String POST_TYPE_ORIGINALS = "originals";
    public static final String POST_TYPE_SEASON = "season";
    public static final String POST_TYPE_EPISODE = "episode";
    public static final String POST_TYPE_PLAYLIST = "playlist";
    public static final String POST_TYPE_AD = "ad";

    // Texonomy
    public static final String TAXONOMY_SEASONS = "seasons";
    public static final String TAXONOMY_CATEGORY = "category";

    // Singular
    public static final String CONTENT_TYPE_ALL_M_ORIGINALS = "originals";
    public static final String CONTENT_TYPE_ALL_M_STREAM = "stream";
    public static final String CONTENT_TYPE_ALL_M_SEASON = "season";
    public static final String CONTENT_TYPE_ALL_M_EPISODE = "episode";
    public static final String CONTENT_TYPE_ALL_M_AUDIO_POST = "audio_post";
    public static final String CONTENT_TYPE_ALL_M_POST = "post";
    public static final String CONTENT_TYPE_CATEGORY = "category";
    public static final String CONTENT_TYPE_YOUTUBE = "youtube_content";


    // Ad types
    public static final String AD_TYPE_EXTERNAL = "external";
    public static final String AD_TYPE_IN_APP = "inApp";


    public static final String MEDIA_TYPE_IMAGE = "image";
    public static final String MEDIA_TYPE_VIDEO = "video";
    public static final String MEDIA_TYPE_SLIDER = "slider";


    public boolean isAdMarkerUpdated = false;
    public NextSeries nextSeries = null;
    public boolean isTrailer = false;

    /**
     * If Bucket's content type is post, audio posts, stream or season then set the Post Data.
     * ---------------------------------------------------------------------------------------
     * If Bucket's content type is categories then set Taxonomy Data.
     * ---------------------------------------------------------------------------------------
     * If Bucket's content type is all then check Bucket's item_from key. If it is auto then set Post Data.
     * Else if it is manual then check content type of a specific content within that bucket.
     * If it is season then set Taxonomy Data
     * Else set Post Data for all other content types.
     *
     * @param contentType content type of FeedData (Bucket)
     * @param itemsFrom   whether item is auto or manually generated
     * @param jsonObject  json object of the contents array of the Feed
     */
    public FeedContentData(String tabType, String contentType, String itemsFrom, JSONObject jsonObject, int feedPosition) {
        feedTabType = tabType;
        feedContentType = contentType;
        feedItemFrom = itemsFrom;
        this.feedPosition = feedPosition;
        switch (contentType) {
            case CONTENT_TYPE_POSTS:
            case CONTENT_TYPE_CHANNEL:
            case CONTENT_TYPE_AUDIO_POSTS:
            case CONTENT_TYPE_STREAMS:
            case CONTENT_TYPE_SEASON:
            case CONTENT_TYPE_ORIGINALS_LIST:
            case TYPE_SUBCATEGORY:
            case TYPE_ORIGINALS:
            case CONTENT_TYPE_AD:
                setPostData(jsonObject);
                break;


            case CONTENT_TYPE_CATEGORIES:
                setTaxonomyData(jsonObject);
                break;


            case CONTENT_TYPE_ALL:
                if (itemsFrom.equals(FeedData.ITEMS_FROM_AUTO)) {
                    setPostData(jsonObject);
                } else if (itemsFrom.equals(FeedData.ITEMS_FROM_MANUAL)) {
                    setAllManualData(jsonObject);

                }
                break;
            default:
                setSliderData(jsonObject);
        }

    }

    public FeedContentData(JSONObject jsonObject) {
        setPostData(jsonObject);
    }

    public FeedContentData(JSONObject jsonObject, int songPosition) {
        this.songPosition = songPosition;
        setPostData(jsonObject);
    }

    public FeedContentData(JSONObject jsonObject, String feedTabType) {
        this.feedTabType = feedTabType;
        setPostData(jsonObject);
    }

    public FeedContentData(JSONObject channelJsonObject, String channelImageUrl, String channelName, boolean isPlaylist) {
        if (isPlaylist) {
            this.playlistImageUrl = channelImageUrl;
            this.playlistName = channelName;
        } else {
            this.channelImageUrl = channelImageUrl;
            this.channelName = channelName;
        }
        setPostData(channelJsonObject);
    }


    public FeedContentData() {

    }

    // Slider object
    public void setSliderData(JSONObject jsonObject) {
        try {
            if (jsonObject.has("image")) {
                image = jsonObject.getString("image");
            }
            if (jsonObject.has("tv_image")) {
                tvImage = jsonObject.getString("tv_image");
            }

            if (jsonObject.has("content_type")) {
                contentType = jsonObject.getString("content_type");
            }

            if (jsonObject.has("url")) {
                url = jsonObject.getString("url");
            }

            /*if (jsonObject.has("series")) {
                seriesId = jsonObject.getInt("series");
            }

            if (jsonObject.has("select_season")) {
                JSONObject jsonObjectseries = jsonObject.getJSONObject("select_season");
                if (jsonObjectseries.has("series")) {
                    seriesId = jsonObjectseries.getInt("series");
                }
                if (jsonObjectseries.has("season")) {
                    seasonId = jsonObjectseries.getInt("season");
                }
            }

            if (jsonObject.has("select_episode")) {
                JSONObject jsonObjectepisode = jsonObject.getJSONObject("select_episode");
                if (jsonObjectepisode.has("series")) {
                    seriesId = jsonObjectepisode.getInt("series");
                }
                if (jsonObjectepisode.has("season")) {
                    seasonId = jsonObjectepisode.getInt("season");
                }
                if (jsonObjectepisode.has("episode")) {
                    episodeId = jsonObjectepisode.getInt("episode");
                }
            }*/
            setPostData(jsonObject);

            /*if (jsonObject.has("item")) {
                JSONObject jsonObjectItem = jsonObject.getJSONObject("item");
                if (jsonObjectItem != null) {
                    itemData = new ItemData(jsonObjectItem);
                }
            }*/
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());

        }
    }

    // Post object
    private void setPostData(JSONObject jsonObject) {

        try {
            if (jsonObject.has("ID")) {
                postId = jsonObject.getString("ID");
            }

            if (jsonObject.has("content_orientation")) {
                contentOrientation = jsonObject.getString("content_orientation");
            }

            if (jsonObject.has("post_author")) {
                postAuthor = jsonObject.getString("post_author");
            }

            if (jsonObject.has("post_date_gmt")) {
                postDateGmt = jsonObject.getString("post_date_gmt");
            }

            if (jsonObject.has("post_content")) {
                postContent = jsonObject.getString("post_content");
            }

            if (jsonObject.has("post_title")) {
                postTitle = jsonObject.getString("post_title");
            }

            if (jsonObject.has("post_excerpt")) {
                postExcerpt = jsonObject.getString("post_excerpt");
            }

            if (jsonObject.has("arrow_linking")) {
                arrowLinking = jsonObject.getString("arrow_linking");
            }

            if (jsonObject.has("button_text")) {
                buttonText = jsonObject.getString("button_text");
            }

            if (jsonObject.has("original_excerpt")) {
                originalExcerpt = jsonObject.getString("original_excerpt");
            }

            if (jsonObject.has("post_status")) {
                postStatus = jsonObject.getString("post_status");
            }

            if (jsonObject.has("comment_status")) {
                commentStatus = jsonObject.getString("comment_status");
            }

            if (jsonObject.has("ping_status")) {
                pingStatus = jsonObject.getString("ping_status");
            }

            if (jsonObject.has("post_name")) {
                postName = jsonObject.getString("post_name");
            }

            if (jsonObject.has("to_ping")) {
                toPing = jsonObject.getString("to_ping");
            }

            if (jsonObject.has("pinged")) {
                pinged = jsonObject.getString("pinged");
            }

            if (jsonObject.has("post_modified")) {
                postModified = jsonObject.getString("post_modified");
            }

            if (jsonObject.has("post_modified_gmt")) {
                postModifiedGmt = jsonObject.getString("post_modified_gmt");
            }

            if (jsonObject.has("post_content_filtered")) {
                postContentFiltered = jsonObject.getString("post_content_filtered");
            }

            if (jsonObject.has("post_drm_protected")) {
                postDrmProtected = jsonObject.getString("post_drm_protected");
            }

            if (jsonObject.has("guid")) {
                guid = jsonObject.getString("guid");
            }

            if (jsonObject.has("post_type")) {
                postType = jsonObject.getString("post_type");
            }

            if (jsonObject.has("post_mime_type")) {
                postMimeType = jsonObject.getString("post_mime_type");
            }

            if (jsonObject.has("filter")) {
                filter = jsonObject.getString("filter");
            }

            if (jsonObject.has("imgUrl")) {
                imgUrl = channelImageUrl.equals("") ? jsonObject.getString("imgUrl") : channelImageUrl;
            }

            if (jsonObject.has("horizontal_rectangle")) {
                Object value = jsonObject.get("horizontal_rectangle");
                if (value instanceof String) {
                    horizontalRectangleImg = jsonObject.getString("horizontal_rectangle");
                }
            }

            if (jsonObject.has("google_short_url")) {
                googleShortUrl = jsonObject.getString("google_short_url");
            }

            if (jsonObject.has("average_rating")) {
                averageRating = jsonObject.getString("average_rating");
            }

            if (jsonObject.has("ad_type")) {
                adType = jsonObject.getString("ad_type");
            }

            if (jsonObject.has("content_type")) {
                contentType = jsonObject.getString("content_type");
            }

            if (jsonObject.has("external_url")) {
                externalUrl = jsonObject.getString("external_url");
            }

            if (jsonObject.has("coins")) {
                coins = jsonObject.getString("coins");
            }

            if (jsonObject.has("series")) {
                seriesName = jsonObject.getString("series");
            }

            if (jsonObject.has("season")) {
                Object object = jsonObject.get("season");
                if (object instanceof JSONObject) {
                    JSONObject jsonObjectSeason = jsonObject.getJSONObject("season");
                    seasonData = new SeasonData(jsonObjectSeason);

                } else {
                    seasonName = jsonObject.getString("season");
                }
            }

            if (jsonObject.has("next_series")) {
                try {
                    if (!jsonObject.isNull("next_series")) {
                        nextSeries = new NextSeries(jsonObject.getJSONObject("next_series"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (jsonObject.has("episode_number")) {
                episodeNumber = jsonObject.getString("episode_number");
            }


            if (jsonObject.has("post_parent")) {
                postParent = jsonObject.getInt("post_parent");
            }

            if (jsonObject.has("menu_order")) {
                menuOrder = jsonObject.getInt("menu_order");
            }

            if (jsonObject.has("comment_count")) {
                commentCount = getValue(jsonObject.get("comment_count"));
            }

            if (jsonObject.has("ptype")) {
                ptype = getValue(jsonObject.get("ptype"));
            }

            if (jsonObject.has("url")) {
                url = jsonObject.getString("url");
            }

            if (jsonObject.has("trending_score")) {
                trendingScore = getValue(jsonObject.get("trending_score"));
            }

            if (jsonObject.has("flag")) {
                flag = getValue(jsonObject.get("flag"));
            }

            if (jsonObject.has("author")) {
                if (jsonObject.getJSONObject("author").has("display_name")) {
                    displayName = jsonObject.getJSONObject("author").getString("display_name");
                }

            }

            if (jsonObject.has("attachment")) {
                try {
                    attachmentData = new AttachmentData(jsonObject.getJSONObject("attachment"));
                } catch (Exception e) {

                }
            }

            if (jsonObject.has("questions")) {
                if (!jsonObject.isNull("questions")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("questions");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        questionData = new QuestionData(jsonArray.getJSONObject(i));
                        arrayListQuestionData.add(questionData);
                    }
                }
            }

            if (jsonObject.has("like_count")) {
                likesCount = jsonObject.getString("like_count").equalsIgnoreCase("") ? "0" : jsonObject.getString("like_count");
            }

            if (jsonObject.has("liked_by_me")) {
                likeByMe = jsonObject.getBoolean("liked_by_me");
            }


            if (jsonObject.has("can_i_use")) {
                canIUse = jsonObject.getBoolean("can_i_use");
            }

            if (jsonObject.has("select_season")) {
                JSONObject jsonObjectseries = jsonObject.getJSONObject("select_season");
                if (jsonObjectseries.has("series")) {
                    seriesId = jsonObjectseries.getString("series");
                }
                if (jsonObjectseries.has("season")) {
                    seasonId = jsonObjectseries.getString("season");
                }
            }

            if (jsonObject.has("select_episode")) {
                isEpisode = true;
                JSONObject jsonObjectepisode = jsonObject.getJSONObject("select_episode");
                if (jsonObjectepisode.has("series")) {
                    seriesId = jsonObjectepisode.getString("series");
                }
                if (jsonObjectepisode.has("season")) {
                    seasonId = jsonObjectepisode.getString("season");
                }
                if (jsonObjectepisode.has("episode")) {
                    episodeId = jsonObjectepisode.getString("episode");
                }
            }

            if (jsonObject.has("series")) {
                if (!isEpisode) {
                    seriesId = jsonObject.getString("series");
                }
            }

            // Buy Data

            if (jsonObject.has("payment")) {
                JSONObject jsonObjectPayment = jsonObject.getJSONObject("payment");
                if (jsonObjectPayment.has("description")) {
                    teaserDescription = jsonObjectPayment.getString("description");
                }
                if (jsonObjectPayment.has("teaser_promo")) {
                    attachmentTeaserPromoData = new AttachmentData(jsonObjectPayment.getJSONObject("teaser_promo"));
                    arrayListTeaserData.add(attachmentTeaserPromoData);
                    //teaserPromo = jsonObjectPayment.getString("teaser_promo");
                }
                if (jsonObjectPayment.has("teaser_image")) {
                    teaserImage = jsonObjectPayment.getString("teaser_image");
                }
                if (jsonObjectPayment.has("pricing")) {
                    try {
                        JSONArray jsonArrayPricing = jsonObjectPayment.getJSONArray("pricing");
                        for (int i = 0; i < jsonArrayPricing.length(); i++) {
                            PriceData priceData = new PriceData(jsonArrayPricing.getJSONObject(i));
                            arrayListPriceData.add(priceData);
                        }
                    }catch (Exception e){

                    }
                }
            }

            if (jsonObject.has("season_count")) {
                seasonCount = jsonObject.getString("season_count");
            }
            if (jsonObject.has("total_episodes")) {
                totalEpisodes = jsonObject.getString("total_episodes");
            }
            if (jsonObject.has("total_items")) {
                totalItems = jsonObject.getString("total_items");
            }
            if (jsonObject.has("custom_text")) {
                customText = jsonObject.getString("custom_text");
            }

           /* if (jsonObject.has("paid_seasons")) {
                try {
                    JSONArray jsonArraySeasons = jsonObject.getJSONArray("paid_seasons");
                    for (int i = 0; i < jsonArraySeasons.length(); i++) {
                        PaidSeasonData paidSeasonData = new PaidSeasonData(jsonArraySeasons.getJSONObject(i));
                        arrayListPaidSeasonData.add(paidSeasonData);
                    }
                }catch (Exception e){

                }
            }*/

            if (jsonObject.has("seasons")) {
                JSONArray jsonArraySeasons = jsonObject.getJSONArray("seasons");
                for (int i = 0; i < jsonArraySeasons.length(); i++) {
                    SeasonData seasonData = new SeasonData(jsonArraySeasons.getJSONObject(i));
                    arrayListSeasonData.add(seasonData);
                }
            }

            if (jsonObject.has("duration")) {
                duration = jsonObject.getString("duration");
            }

            if (jsonObject.has("scheduled_time")) {
                scheduledTime = jsonObject.getString("scheduled_time");
            }

            if (jsonObject.has("description")) {
                description = jsonObject.getString("description");
            }

            if (jsonObject.has("is_watchlisted")) {
                iswatchlisted = jsonObject.getBoolean("is_watchlisted");
            }

            if (jsonObject.has("can_i_use")) {
                can_i_use = jsonObject.getBoolean("can_i_use");
            }
            if (jsonObject.has("skippable")) {
                skippable = jsonObject.getBoolean("skippable");
            }
            if (jsonObject.has("single_credit")) {
                singleCredit = jsonObject.getBoolean("single_credit");
            }
            if (jsonObject.has("is_trailer")) {
                isTrailer = jsonObject.getBoolean("is_trailer");
            }

            if (jsonObject.has("media_type")) {
                mediaType = jsonObject.getString("media_type");
            }
            if (jsonObject.has("skippable_in_secs")) {
                skippableInSecs = jsonObject.getString("skippable_in_secs");
            }

            if (jsonObject.has("reward")) {
                reward = jsonObject.getString("reward");
            }

            try {
                if (jsonObject.has("events")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("events");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        EventsData eventData = new EventsData(jsonArray.getJSONObject(i));
                        eventsData.add(eventData);
                    }
                }
            } catch (Exception e) {

            }

            if (mediaType.equalsIgnoreCase(MEDIA_TYPE_IMAGE) || mediaType.equalsIgnoreCase(MEDIA_TYPE_VIDEO)) {
                if (jsonObject.has("ad_fields")) {
                    adFieldsData = new AdFieldsData(jsonObject.getJSONObject("ad_fields"), postId);
                }
            } else if (mediaType.equalsIgnoreCase(MEDIA_TYPE_SLIDER)) {
                if (!jsonObject.isNull("multiads_fields")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("multiads_fields");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        adFieldsData = new AdFieldsData(jsonArray.getJSONObject(i), postId);
                        arrayListAdFieldsData.add(adFieldsData);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Taxonomy object
    private void setTaxonomyData(JSONObject jsonObject) {

        try {
            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }

            if (jsonObject.has("can_i_use")) {
                canIUse = jsonObject.getBoolean("can_i_use");
            }

            if (jsonObject.has("slug")) {
                slug = jsonObject.getString("slug");
            }

            if (jsonObject.has("taxonomy")) {
                taxonomy = jsonObject.getString("taxonomy");
            }

            if (jsonObject.has("description")) {
                description = jsonObject.getString("description");
            }

            if (jsonObject.has("filter")) {
                filter = jsonObject.getString("filter");
            }

            if (jsonObject.has("imgUrl")) {
                imgUrl = jsonObject.getString("imgUrl");
            }

            if (jsonObject.has("horizontal_rectangle")) {
                Object value = jsonObject.get("horizontal_rectangle");
                if (value instanceof String) {
                    horizontalRectangleImg = jsonObject.getString("horizontal_rectangle");
                }
            }

            if (jsonObject.has("content_type")) {
                contentType = jsonObject.getString("content_type");
            }

            if (jsonObject.has("term_id")) {
                termId = jsonObject.getInt("term_id");
            }

            if (jsonObject.has("term_group")) {
                termGroup = jsonObject.getInt("term_group");
            }

            if (jsonObject.has("term_taxonomy_id")) {
                termTaxonomyId = jsonObject.getInt("term_taxonomy_id");
            }

            if (jsonObject.has("parent")) {
                parent = jsonObject.getInt("parent");
            }

            if (jsonObject.has("count")) {
                count = jsonObject.getInt("count");
            }
            if (jsonObject.has("author")) {
                if (jsonObject.getJSONObject("author").has("display_name")) {
                    displayName = jsonObject.getJSONObject("author").getString("display_name");
                }

            }

            if (jsonObject.has("select_season")) {
                JSONObject jsonObjectseries = jsonObject.getJSONObject("select_season");
                if (jsonObjectseries.has("series")) {
                    seriesId = jsonObjectseries.getString("series");
                }
                if (jsonObjectseries.has("season")) {
                    seasonId = jsonObjectseries.getString("season");
                }
            }

            // Buy Data

            if (jsonObject.has("payment")) {
                JSONObject jsonObjectPayment = jsonObject.getJSONObject("payment");
                if (jsonObjectPayment.has("description")) {
                    teaserDescription = jsonObjectPayment.getString("description");
                }
                if (jsonObjectPayment.has("teaser_promo")) {
                    attachmentTeaserPromoData = new AttachmentData(jsonObjectPayment.getJSONObject("teaser_promo"));
                    arrayListTeaserData.add(attachmentTeaserPromoData);
                    //teaserPromo = jsonObjectPayment.getString("teaser_promo");
                }
                if (jsonObjectPayment.has("teaser_image")) {
                    teaserImage = jsonObjectPayment.getString("teaser_image");
                }
                if (jsonObjectPayment.has("pricing")) {
                    try {
                        JSONArray jsonArrayPricing = jsonObjectPayment.getJSONArray("pricing");
                        for (int i = 0; i < jsonArrayPricing.length(); i++) {
                            PriceData priceData = new PriceData(jsonArrayPricing.getJSONObject(i));
                            arrayListPriceData.add(priceData);
                        }
                    } catch (Exception e) {

                    }
                }
            }

            if (jsonObject.has("post_title")) {
                postTitle = jsonObject.getString("post_title");
            }

            if (jsonObject.has("post_type")) {
                postType = jsonObject.getString("post_type");
            }
            if (jsonObject.has("total_episodes")) {
                totalEpisodes = jsonObject.getString("total_episodes");
            }

            if (jsonObject.has("seasons")) {
                JSONArray jsonArraySeasons = jsonObject.getJSONArray("seasons");
                for (int i = 0; i < jsonArraySeasons.length(); i++) {
                    SeasonData seasonData = new SeasonData(jsonArraySeasons.getJSONObject(i));
                    arrayListSeasonData.add(seasonData);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }


    private void setAllManualData(JSONObject jsonObject) {
        try {
            if (jsonObject.has("content_type")) {
                if (jsonObject.getString("content_type").equals(CONTENT_TYPE_SEASON)) {
                    setTaxonomyData(jsonObject);
                } else {
                    setPostData(jsonObject);
                }
            }
        } catch (JSONException e) {

        }
    }

    // Returns object as integer value
    private int getValue(Object object) {
        if (object instanceof String) {
            if (object.equals("")) {
                return 0;
            }
            return Integer.parseInt((String) object);
        } else if (object instanceof Integer) {
            return (int) object;
        }
        return 0;
    }

    @Override
    public String getMediaId() {
        if (attachmentData != null) {
            return String.valueOf(attachmentData.id);
        }
        return null;
    }

    @Override
    public String getMediaTitle() {
        if (attachmentData != null) {
            return postTitle;
        }
        return null;
    }

    @Override
    public String getMediaDesc() {
        if (attachmentData != null) {
            return postContent;
        }
        return null;
    }

    @Override
    public String getMediaImageUrl() {
        if (attachmentData != null) {
            return imgUrl;
        }
        return null;
    }

    @Override
    public String getMediaUrl() {
        //return "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd";
        //return "https://itap-uploads.s3.ap-south-1.amazonaws.com/dash/05032020134645312017/playlist.mpd"; // no licence required.
        //return "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd"; // no licence required.
        //return "https://storage.googleapis.com/wvmedia/cenc/hevc/tears/tears.mpd"; // licence required.
        if (attachmentData != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && postDrmProtected.equalsIgnoreCase(Constant.YES)) {
                if (!attachmentData.wv_hls_url.isEmpty()) {
                    return attachmentData.wv_hls_url;
                } else if (!attachmentData.hls_url.isEmpty()) {
                    return attachmentData.hls_url;
                } else {
                    return attachmentData.url;
                }
            } else {
                if (!attachmentData.hls_url.isEmpty()) {
                    return attachmentData.hls_url;
                } else {
                    return attachmentData.url;
                }
            }
        }
        return null;
    }

    @Override
    public String getTeaserUrl() {
        if (attachmentTeaserPromoData != null) {
            if (!attachmentTeaserPromoData.hls_url.isEmpty() && !attachmentTeaserPromoData.hls_url.equalsIgnoreCase("false")) {
                return attachmentTeaserPromoData.hls_url;
            } else {
                return attachmentTeaserPromoData.url;
            }
        }
        return null;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
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


    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostExcerpt() {
        return postExcerpt;
    }

    public void setPostExcerpt(String postExcerpt) {
        this.postExcerpt = postExcerpt;
    }

    public String getOriginalExcerpt() {
        return originalExcerpt;
    }

    public void setOriginalExcerpt(String originalExcerpt) {
        this.originalExcerpt = originalExcerpt;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    {
        this.filter = filter;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }


    public boolean isIswatchlisted() {
        return iswatchlisted;
    }

    public void setIswatchlisted(boolean iswatchlisted) {
        this.iswatchlisted = iswatchlisted;
    }

    public boolean isEpisode() {
        return isEpisode;
    }

    public void setEpisode(boolean episode) {
        isEpisode = episode;
    }


    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public AttachmentData getAttachmentData() {
        return attachmentData;
    }

    public void setAttachmentData(AttachmentData attachmentData) {
        this.attachmentData = attachmentData;
    }

    public QuestionData getQuestionData() {
        return questionData;
    }

    public void setQuestionData(QuestionData questionData) {
        this.questionData = questionData;
    }

    public AdFieldsData getAdFieldsData() {
        return adFieldsData;
    }

    public void setAdFieldsData(AdFieldsData adFieldsData) {
        this.adFieldsData = adFieldsData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    /*public String getTeaserPromo() {
        return teaserPromo;
    }

    public void setTeaserPromo(String teaserPromo) {
        this.teaserPromo = teaserPromo;
    }*/


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    public static String getTAG() {
        return TAG;
    }

    public static void setTAG(String TAG) {
        FeedContentData.TAG = TAG;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(image);
        parcel.writeString(tvImage);
        parcel.writeString(contentType);
        parcel.writeString(url);
        parcel.writeString(licenceUrl);
        parcel.writeString(seriesId);
        parcel.writeString(seasonId);
        parcel.writeString(episodeId);
        parcel.writeInt(id);
        parcel.writeString(postId);
        parcel.writeString(postAuthor);
        parcel.writeString(postDate);
        parcel.writeString(postDateGmt);
        parcel.writeString(postContent);
        parcel.writeString(postTitle);
        parcel.writeString(postExcerpt);
        parcel.writeString(arrowLinking);
        parcel.writeString(buttonText);
        parcel.writeString(originalExcerpt);
        parcel.writeString(postStatus);
        parcel.writeString(commentStatus);
        parcel.writeString(pingStatus);
        parcel.writeString(postName);
        parcel.writeString(toPing);
        parcel.writeString(pinged);
        parcel.writeString(postModified);
        parcel.writeString(postModifiedGmt);
        parcel.writeString(postContentFiltered);
        parcel.writeString(postDrmProtected);
        parcel.writeString(guid);
        parcel.writeString(postType);
        parcel.writeString(postMimeType);
        parcel.writeString(filter);
        parcel.writeString(imgUrl);
        parcel.writeString(horizontalRectangleImg);
        parcel.writeString(channelImageUrl);
        parcel.writeString(channelName);
        parcel.writeString(playlistImageUrl);
        parcel.writeString(playlistName);
        parcel.writeString(googleShortUrl);
        parcel.writeString(averageRating);
        parcel.writeString(displayName);
        parcel.writeString(coins);
        parcel.writeString(seriesName);
        parcel.writeString(seasonName);
        parcel.writeString(episodeNumber);
        parcel.writeString(likesCount);
        parcel.writeString(scheduledTime);
        parcel.writeByte((byte) (likeByMe ? 1 : 0));
        parcel.writeByte((byte) (canIUse ? 1 : 0));
        parcel.writeByte((byte) (iswatchlisted ? 1 : 0));
        parcel.writeByte((byte) (isSelected ? 1 : 0));
        parcel.writeByte((byte) (can_i_use ? 1 : 0));
        parcel.writeByte((byte) (skippable ? 1 : 0));
        parcel.writeByte((byte) (singleCredit ? 1 : 0));
        parcel.writeByte((byte) (isEpisode ? 1 : 0));
        parcel.writeInt(postParent);
        parcel.writeInt(menuOrder);
        parcel.writeInt(commentCount);
        parcel.writeInt(ptype);
        parcel.writeInt(trendingScore);
        parcel.writeInt(flag);
        parcel.writeParcelable(attachmentData, i);
        parcel.writeParcelable(attachmentTeaserPromoData, i);
        parcel.writeTypedList(arrayListTeaserData);
        parcel.writeParcelable(questionData, i);
        parcel.writeParcelable(adFieldsData, i);
        parcel.writeTypedList(arrayListQuestionData);
        parcel.writeTypedList(adsArray);
        parcel.writeTypedList(arrayListAdFieldsData);
        parcel.writeTypedList(eventsData);
        parcel.writeByte((byte) (isAdLoaded ? 1 : 0));
        parcel.writeString(name);
        parcel.writeString(slug);
        parcel.writeString(taxonomy);
        parcel.writeString(description);
        parcel.writeInt(termId);
        parcel.writeInt(termGroup);
        parcel.writeInt(termTaxonomyId);
        parcel.writeInt(parent);
        parcel.writeInt(count);
        parcel.writeString(feedContentType);
        parcel.writeString(feedItemFrom);
        parcel.writeString(feedTabType);
        parcel.writeString(adType);
        parcel.writeString(externalUrl);
        parcel.writeInt(feedPosition);
        parcel.writeInt(songPosition);
        parcel.writeString(teaserDescription);
        parcel.writeString(teaserImage);
        parcel.writeString(seasonCount);
        parcel.writeString(totalEpisodes);
        parcel.writeString(customText);
        parcel.writeString(totalItems);
        parcel.writeString(duration);
        parcel.writeString(mediaType);
        parcel.writeString(skippableInSecs);
        parcel.writeTypedList(arrayListPriceData);
        parcel.writeTypedList(arrayListSeasonData);
        // parcel.writeTypedList(arrayListPaidSeasonData);
        parcel.writeParcelable(seasonData, i);
        parcel.writeByte((byte) (isAdMarkerUpdated ? 1 : 0));
        parcel.writeParcelable(nextSeries, i);
        parcel.writeByte((byte) (isTrailer ? 1 : 0));
        parcel.writeString(contentOrientation);
        parcel.writeString(reward);
        parcel.writeByte((byte) (isFocused ? 1 : 0));
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }

    public String getIdForLicence() {
        Uri uri = Uri.parse(getMediaUrl());
        if (uri != null && ExoUtil.isDash(uri)) {
            String[] paths = uri.getPath().split("/");
            return paths[paths.length - 2];
        }
        return "";
    }
}
