package com.app.itaptv.structure;

import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotificationData {

    public String status = "";
    public String title = "";
    public String time = "";
    public String date = "";
    public String meassage = "";
    public String page = "";
    public String gameId = "";
    public String img_url = "";
    public String ID = "";
    public String SeriesId = "";
    public String SeasonId = "";
    public GameData gameData = null;
    public FeedContentData feedContentData = null;
    public CouponData couponData = null;
    public String meassageId = "";


    Calendar cal_today = Calendar.getInstance(); // today
    Calendar cal_yesterday = Calendar.getInstance();
    String n_df_source = "yyyy-MM-dd HH:mm:ss";
    String n_df_source_date = "yyyy-MM-dd";
    String n_df_destination_date = "MMM dd";
    String n_df_source_time = "H:mm";
    String n_df_destination_time = "hh:mm a";

    public NotificationData(JSONObject result) {
        try {
            this.status = result.has("status") ? result.getString("status") : "";
            this.title = result.has("notification") ? result.getJSONObject("notification").has("title") ? result.getJSONObject("notification").getString("title") : "" : "";
            this.meassage = result.has("notification") ? result.getJSONObject("notification").has("message") ? result.getJSONObject("notification").getString("message") : "" : "";
            cal_yesterday.add(Calendar.DAY_OF_YEAR, -1);

            if(result.has("result")){
                JSONObject objectResult = result.getJSONObject("result");
                if(objectResult.has("message_id")){
                    meassageId = objectResult.getString("message_id");
                }
            }

            if (result.has("created_at")) {
                setDateTime(result.getString("created_at"));
            }

            this.img_url = result.has("notification") ? result.getJSONObject("notification").has("img_url") ? result.getJSONObject("notification").getString("img_url") : "" : "";

            JSONObject data = result.has("notification") ? result.getJSONObject("notification").has("data") ? result.getJSONObject("notification").getJSONObject("data") : new JSONObject() : new JSONObject();
            if (data.length() > 0) {
                this.page = data.has("page") ? data.getString("page") : "";
                switch (this.page) {
                    case Constant.LUCKY_WINNER:
                        this.gameId = data.has("gameId") ? data.getString("gameId") : "";
                        break;
                    case Constant.PLAYLIST:
                        this.ID = data.has("ID") ? data.getString("ID") : "";
                        this.feedContentData = data.has("post") ? new FeedContentData(data.getJSONObject("post"), -1) : null;
                        break;
                    case Constant.GAME:
                        this.ID = data.has("ID") ? data.getString("ID") : "";
                        this.gameData = data.has("game") ? new GameData(data.getJSONObject("game"), ID) : null;
                        break;
                    case Constant.EPISODE:
                        this.ID = data.has("ID") ? data.getString("ID") : "";
                        JSONObject jsonObject = data.has("post") ? data.getJSONObject("post") : new JSONObject();
                        this.SeriesId = jsonObject.has("series") ? jsonObject.getString("series") : "";
                        this.SeasonId = jsonObject.has("season") ? jsonObject.getString("") : "season";
                        this.feedContentData = data.has("series") ? new FeedContentData(data.getJSONObject("series"), -1) : null;
                        break;
                    case Constant.GAME_LIST:
                        this.gameId = data.has("gameId") ? data.getString("gameId") : "";
                        this.ID = data.has("ID") ? data.getString("ID") : "";
                        break;
                    case Constant.COUPONS:
                        this.ID = data.has("ID") ? data.getString("ID") : "";
                        this.couponData = data.has("post") ? new CouponData(data.getJSONObject("post")) : null;
                        break;
                    case Constant.ORIGINALS:
                        this.ID = data.has("ID") ? data.getString("ID") : "";
                        this.feedContentData = data.has("post") ? new FeedContentData(data.getJSONObject("post"), -1) : null;
                        break;
                    case Constant.COUPON_LISTING:
                        this.ID = data.has("ID") ? data.getString("ID") : "";
                        break;
                    case Constant.CHANNEL:
                        this.ID = data.has("ID") ? data.getString("ID") : "";
                        this.feedContentData = data.has("post") ? new FeedContentData(data.getJSONObject("post"), -1) : null;
                        break;
                    case Constant.LEADERBOARD:
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDateTime(String created_at) {
        SimpleDateFormat sourceTimeFormat = new SimpleDateFormat(n_df_source);
        Date dt_notification_time = null;
        try {
            dt_notification_time = sourceTimeFormat.parse(created_at);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dt_notification_time); // your date

            // If notification date = today's date
            if (cal_today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    && cal_today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
                String n_time = created_at.substring(11, 16);
                String n_formatted_time = Utility.formatDate(n_df_source_time, n_df_destination_time, n_time);
                this.time = n_formatted_time;
                this.date = "";
            }
            // If notification date = yesterday's date
            else if (cal_yesterday.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    && cal_yesterday.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
                String n_time = created_at.substring(11, 16);
                String n_formatted_time = Utility.formatDate(n_df_source_time, n_df_destination_time, n_time);
                this.time = n_formatted_time;
                this.date = "Yesterday";
            }
            // If notification date = day before yesterday
            else {
                String n_time = created_at.substring(11, 16);
                String n_formatted_time = Utility.formatDate(n_df_source_time, n_df_destination_time, n_time);
                this.time = n_formatted_time;
                String n_date = created_at.substring(0, 10);
                String n_formatted_date = Utility.formatDate(n_df_source_date, n_df_destination_date, n_date);
                this.date = n_formatted_date;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
