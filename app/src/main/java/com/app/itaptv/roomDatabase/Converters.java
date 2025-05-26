package com.app.itaptv.roomDatabase;

import android.content.Intent;

import androidx.room.TypeConverter;

import com.app.itaptv.MyApp;
import com.app.itaptv.structure.AttachmentData;
import com.app.itaptv.structure.DeviceDetailsData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.FlagCommentData;
import com.app.itaptv.structure.ItemData;
import com.app.itaptv.structure.LocationData;
import com.app.itaptv.structure.PriceData;
import com.app.itaptv.structure.QuestionData;
import com.app.itaptv.structure.SeasonData;
import com.app.itaptv.structure.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Converters {

    @TypeConverter
    public static AttachmentData fromAttachmentData(String value) {
        Type listType = new TypeToken<AttachmentData>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String toAttachmentData(AttachmentData list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static QuestionData fromQuestionData(String value) {
        Type listType = new TypeToken<AttachmentData>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String toSeasonData(QuestionData list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static SeasonData fromSeasonData(String value) {
        Type listType = new TypeToken<SeasonData>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String toSeasonData(SeasonData list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static ItemData fromItemData(String value) {
        Type listType = new TypeToken<ItemData>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String toItemData(ItemData list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static FeedContentData fromFeedContentData(String feedContentData) {
        Type listType = new TypeToken<FeedContentData>() {
        }.getType();
        return new Gson().fromJson(feedContentData, listType);
    }

    @TypeConverter
    public static String toFeedContentData(FeedContentData feedContentData) {

        /*Gson gson = new Gson();
        String json = gson.toJson(feedContentData);
        return json;*/
        if (feedContentData == null) {
            return (null);
        }
        String json = "";
        try {
            Gson gson = new Gson();
            json = gson.toJson(feedContentData);
        }catch (Exception exception){
            MyApp.getAppContext().sendBroadcast(new Intent("CRASH"));
        }
        return json;
    }

    @TypeConverter // note this annotation
    public String fromQuestionDataList(ArrayList<QuestionData> questionData) {
        if (questionData == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<QuestionData>>() {
        }.getType();
        String json = gson.toJson(questionData, type);
        return json;
    }

    @TypeConverter // note this annotation
    public ArrayList<QuestionData> toQuestionDataList(String questionDataString) {
        if (questionDataString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<QuestionData>>() {
        }.getType();
        ArrayList<QuestionData> questionDataList = gson.fromJson(questionDataString, type);
        return questionDataList;
    }

    @TypeConverter // note this annotation
    public String fromFeedContentData(List<FeedContentData> feedContentData) {
        if (feedContentData == null) {
            return (null);
        }
        String json = "";
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<FeedContentData>>() {
            }.getType();
         json= gson.toJson(feedContentData, type);
        }catch (Exception exception){
            //Utility.showError("Unable to complete operation. Please try again later", MyApp.getAppContext());
            //Toast.makeText(MyApp.getAppContext(), "Unable to complete operation. Please try again later", Toast.LENGTH_SHORT).show();
            MyApp.getAppContext().sendBroadcast(new Intent("CRASH"));
        }
        return json;
    }

    @TypeConverter // note this annotation
    public List<FeedContentData> toFeedContentData(String feedContentString) {
        /*if (feedContentString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<FeedContentData>>() {
        }.getType();
        List<FeedContentData> feedContentData = gson.fromJson(feedContentString, type);
        return feedContentData;*/
        if (feedContentString == null) {
            return (null);
        }
        List<FeedContentData> feedContentData = new ArrayList<>();
        try {
            if (!feedContentString.isEmpty()) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<FeedContentData>>() {
                }.getType();
                feedContentData = gson.fromJson(feedContentString, type);
            }
        }catch (Exception exception){
            MyApp.getAppContext().sendBroadcast(new Intent("CRASH"));
        }
        return feedContentData;
    }

    @TypeConverter // note this annotation
    public String fromAttachmentDataList(ArrayList<AttachmentData> attachmentData) {
        if (attachmentData == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<AttachmentData>>() {
        }.getType();
        String json = gson.toJson(attachmentData, type);
        return json;
    }

    @TypeConverter // note this annotation
    public ArrayList<AttachmentData> toAttachmentDataList(String attachmentDataString) {
        if (attachmentDataString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<AttachmentData>>() {
        }.getType();
        ArrayList<AttachmentData> attachmentDataList = gson.fromJson(attachmentDataString, type);
        return attachmentDataList;
    }

    @TypeConverter // note this annotation
    public String fromPriceDataList(ArrayList<PriceData> priceData) {
        if (priceData == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<PriceData>>() {
        }.getType();
        String json = gson.toJson(priceData, type);
        return json;
    }

    @TypeConverter // note this annotation
    public ArrayList<PriceData> toPriceDataList(String priceDataString) {
        if (priceDataString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<PriceData>>() {
        }.getType();
        ArrayList<PriceData> priceDataList = gson.fromJson(priceDataString, type);
        return priceDataList;
    }

    @TypeConverter // note this annotation
    public String fromSeasonDataList(ArrayList<SeasonData> seasonData) {
        if (seasonData == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<SeasonData>>() {
        }.getType();
        String json = gson.toJson(seasonData, type);
        return json;
    }

    @TypeConverter // note this annotation
    public ArrayList<SeasonData> toSeasonDataList(String SeasonDataString) {
        if (SeasonDataString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SeasonData>>() {
        }.getType();
        ArrayList<SeasonData> seasonDataList = gson.fromJson(SeasonDataString, type);
        return seasonDataList;
    }

    @TypeConverter // note this annotation
    public String fromflagCommentDataList(ArrayList<FlagCommentData> flagCommentData) {
        if (flagCommentData == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<FlagCommentData>>() {
        }.getType();
        String json = gson.toJson(flagCommentData, type);
        return json;
    }

    @TypeConverter // note this annotation
    public ArrayList<FlagCommentData> toflagCommentDataList(String flagCommentDataString) {
        if (flagCommentDataString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<FlagCommentData>>() {
        }.getType();
        ArrayList<FlagCommentData> flagCommentDataList = gson.fromJson(flagCommentDataString, type);
        return flagCommentDataList;
    }


    @TypeConverter // note this annotation
    public static String jsonformat(Map<String, String> s) {
        if (s == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        String json = gson.toJson(s, type);
        return json;
    }

    @TypeConverter
    public static LocationData fromLocationData(String value) {
        Type listType = new TypeToken<LocationData>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String toLocationData(LocationData list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static DeviceDetailsData fromDeviceDetailsData(String value) {
        Type listType = new TypeToken<DeviceDetailsData>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String toDeviceDetailsData(DeviceDetailsData list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static User fromUserData(String value) {
        Type listType = new TypeToken<User>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String toUserData(User list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}