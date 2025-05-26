package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.itaptv.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by poonam on 18/9/18.
 */

public class GameData implements Parcelable {
    public String feedGameId = "";

    public String id = "";
    public String postAuthor = "";
    public String postContent = "";
    public String postTitle = "";
    public String postStatus = "";
    public String commentStatus = "";
    public String pingStatus = "";
    public String postName = "";
    public String postType = "";
    public String commentCount = "";
    public String imageUrl = "";
    public String rectangle_image = "";
    public String backgroundImage = "";
    public String quizType = "";
    public String bonusPoints = "0";
    public String entryFees = "0";
    public String start = "";
    public String end = "";
    public String colorPrimary = "";
    public String colorSecondary = "";
    public String colorBox = "";
    public String noOfQuestions = "";
    public String singlePlayerTimer = "";
    public String tvChannelName = "";
    public String tvLogo = "";
    public String description = "";
    public String couponImg = "";
    public String coupon = "";
    public String image = "";
    public String webviewUrl = "";
    public QuestionData questionData = null;
    public ArrayList<QuestionData> arrayquestionData = new ArrayList<>();
    public boolean playedGame = false;
    public boolean winnersDeclared = false;
    public String announceWinnerAt = "";

    public static final String QUIZE_TYPE_LIVE = "live";
    public static final String QUIZE_TYPE_TURN_BASED = "turn_based";
    public static final String QUIZE_TYPE_HUNTER_GAMES = "hunter_games";

    public static final String GAME_ID_ONGOING_LIVE = "ongoing_live";
    public static final String GAME_ID_TRIVIA = "trivia";
    public static final String GAME_ID_UPCOMING_LIVE = "upcoming_live";
    public static final String GAME_ID_COMPLETED_LIVE = "completed_live";


    public static String sourceDateFormat = "yyyy-MM-dd hh:mm:ss";
    public static String sourceDateFormat1 = "yyyy-MM-dd HH:mm:ss";
    public static String destDateFormat = "MMM dd, hh:mm a";
    public static String defaultDateFormat = "dd-MM-yyyy";
    public static String dateFormat = "MMM dd";
    public static String timeFormat = "hh:mm a";

    public static final int REQUEST_CODE_GAME = 123;

    public GameData(){}

    public GameData(JSONObject jsonObject, String gameId) {
        feedGameId = gameId;
        try {
            Log.e("GameData", jsonObject.toString());
            if (jsonObject.has("ID")) {
                id = jsonObject.getString("ID");
            }

            if (jsonObject.has("post_author")) {
                postAuthor = jsonObject.getString("post_author");
            }

            if (jsonObject.has("post_content")) {
                postContent = jsonObject.getString("post_content");
            }

            if (jsonObject.has("post_title")) {
                postTitle = jsonObject.getString("post_title");
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

            if (jsonObject.has("post_type")) {
                postType = jsonObject.getString("post_type");
            }

            if (jsonObject.has("comment_count")) {
                commentCount = jsonObject.getString("comment_count");
            }

            if (jsonObject.has("imgUrl")) {
                imageUrl = jsonObject.getString("imgUrl");
            }

            if (jsonObject.has("recatangle_image")) {
                rectangle_image = jsonObject.getString("recatangle_image");
            }

            if (jsonObject.has("background_image")) {
                backgroundImage = jsonObject.getString("background_image");
            }

            if (jsonObject.has("quiz_type")) {
                quizType = jsonObject.getString("quiz_type");
            }

            if (jsonObject.has("bonus_points")) {
                bonusPoints = jsonObject.getString("bonus_points");
            }
            if (jsonObject.has("entry_fees")) {
                entryFees = jsonObject.getString("entry_fees");
            }

            if (jsonObject.has("start")) {
                start = jsonObject.getString("start");
            }

            if (jsonObject.has("end")) {
                end = jsonObject.getString("end");
            }

            if (jsonObject.has("color")) {
                JSONObject jsonObjectColor = jsonObject.getJSONObject("color");
                if (jsonObjectColor.has("primary")) {
                    colorPrimary = jsonObjectColor.getString("primary");
                }

                if (jsonObjectColor.has("secondary")) {
                    colorSecondary = jsonObjectColor.getString("secondary");
                }

                if (jsonObjectColor.has("box")) {
                    colorBox = jsonObjectColor.getString("box");
                }
            }

            if (jsonObject.has("no_of_questions")) {
                noOfQuestions = jsonObject.getString("no_of_questions");
            }

            if (jsonObject.has("single_player_timer")) {
                singlePlayerTimer = jsonObject.getString("single_player_timer");
            }

            if (jsonObject.has("tv_channel_name")) {
                tvChannelName = jsonObject.getString("tv_channel_name");
            }

            if (jsonObject.has("tv_logo")) {
                tvLogo = jsonObject.getString("tv_logo");
            }

            if (jsonObject.has("description")) {
                description = jsonObject.getString("description");
            }

            if (jsonObject.has("couponImg")) {
                couponImg = jsonObject.getString("couponImg");
            }

            if (jsonObject.has("coupon")) {
                coupon = jsonObject.getString("coupon");
            }

            if (jsonObject.has("image")) {
                image = jsonObject.getString("image");
            }

            if (jsonObject.has("q")) {
                JSONArray jsonArrayq = jsonObject.getJSONArray("q");
                /*JSONObject jsonObject1 = jsonArrayq.getJSONObject(0);
                JSONArray jArr = jsonObject1.getJSONArray("question");*/
                for (int i = 0; i < jsonArrayq.length(); i++){
                    JSONObject jsonObject1 = jsonArrayq.getJSONObject(i);
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("question");
                    questionData = new QuestionData(jsonObject2);
                    arrayquestionData.add(questionData);
                }
                /*if (jArr.length() > 0) {
                    Object obj = jArr.get(0);
                    JSONArray toBeParsed = new JSONArray();
                    if (obj instanceof JSONArray) {
                        toBeParsed = (JSONArray) obj;
                    }
                    if (obj instanceof JSONObject) {
                        toBeParsed = jArr;
                    }
                    if (toBeParsed.length() > 0) {
                        for (int i = 0; i < toBeParsed.length(); i++) {
                            JSONObject jsonObjectq = toBeParsed.getJSONObject(i);
                            //if (jsonObjectq.has("question")) {
                                questionData = new QuestionData(jsonObjectq);
                                arrayquestionData.add(questionData);
                            //}
                        }
                    }
                }*/
            }

            if (jsonObject.has("played_game")) {
                playedGame = jsonObject.getBoolean("played_game");
            }

            if (jsonObject.has("announce_winner_at")) {
                announceWinnerAt = jsonObject.getString("announce_winner_at");
            }

            if (jsonObject.has("winners_declared")) {
                winnersDeclared = jsonObject.getBoolean("winners_declared");
            }
            if (jsonObject.has("webview_url")) {
                webviewUrl = jsonObject.getString("webview_url");
            }
        } catch (JSONException e) {
            Log.d("GameData", e.toString());
        }
    }

    protected GameData(Parcel in) {
        id = in.readString();
        postAuthor = in.readString();
        postContent = in.readString();
        postTitle = in.readString();
        postStatus = in.readString();
        commentStatus = in.readString();
        pingStatus = in.readString();
        postName = in.readString();
        postType = in.readString();
        commentCount = in.readString();
        imageUrl = in.readString();
        rectangle_image = in.readString();
        backgroundImage = in.readString();
        quizType = in.readString();
        bonusPoints = in.readString();
        entryFees = in.readString();
        start = in.readString();
        end = in.readString();
        colorPrimary = in.readString();
        colorSecondary = in.readString();
        colorBox = in.readString();
        in.readTypedList(arrayquestionData, QuestionData.CREATOR);
        playedGame = in.readByte() != 0;
        announceWinnerAt = in.readString();
        winnersDeclared = in.readByte() != 0;
        noOfQuestions = in.readString();
        singlePlayerTimer = in.readString();
        tvChannelName = in.readString();
        tvLogo = in.readString();
        description = in.readString();
        couponImg = in.readString();
        coupon = in.readString();
        image = in.readString();
    }

    public static final Creator<GameData> CREATOR = new Creator<GameData>() {
        @Override
        public GameData createFromParcel(Parcel in) {
            return new GameData(in);
        }

        @Override
        public GameData[] newArray(int size) {
            return new GameData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(postAuthor);
        parcel.writeString(postContent);
        parcel.writeString(postTitle);
        parcel.writeString(postStatus);
        parcel.writeString(commentStatus);
        parcel.writeString(pingStatus);
        parcel.writeString(postName);
        parcel.writeString(postType);
        parcel.writeString(commentCount);
        parcel.writeString(imageUrl);
        parcel.writeString(rectangle_image);
        parcel.writeString(backgroundImage);
        parcel.writeString(quizType);
        parcel.writeString(bonusPoints);
        parcel.writeString(entryFees);
        parcel.writeString(start);
        parcel.writeString(end);
        parcel.writeString(colorPrimary);
        parcel.writeString(colorSecondary);
        parcel.writeString(colorBox);
        parcel.writeTypedList(arrayquestionData);
        parcel.writeByte((byte) (playedGame ? 1 : 0));
        parcel.writeString(announceWinnerAt);
        parcel.writeByte((byte) (winnersDeclared ? 1 : 0));
        parcel.writeString(noOfQuestions);
        parcel.writeString(singlePlayerTimer);
        parcel.writeString(tvChannelName);
        parcel.writeString(tvLogo);
        parcel.writeString(description);
        parcel.writeString(couponImg);
        parcel.writeString(coupon);
        parcel.writeString(image);
    }
}
