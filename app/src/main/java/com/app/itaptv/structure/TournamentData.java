package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TournamentData implements Parcelable {

    public String id = "";
    public String name = "";
    public String start_date = "";
    public String tournament_start_date = "";
    public String end_date = "";
    public String tournament_end_date = "";
    public String entry_price = "";
    public String coin_amount = "";
    public String coin_earnings = "";
    public String registration_reward = "";
    public String payment_type = "";
    public String min = "";
    public String max = "";
    public String tournament_type = "";
    public String attachment = "";
    public String square_img = "";
    public String horizontal_rectangle = "";
    public String vertical_rectangle = "";
    public String circle_img = "";
    public String telegram_link = "";
    public boolean can_register = false;
    public List<String> rules = new ArrayList<>();

    protected TournamentData(Parcel in) {
        id = in.readString();
        name = in.readString();
        start_date = in.readString();
        end_date = in.readString();
        tournament_start_date = in.readString();
        tournament_end_date = in.readString();
        entry_price = in.readString();
        coin_amount = in.readString();
        coin_earnings = in.readString();
        registration_reward = in.readString();
        payment_type = in.readString();
        min = in.readString();
        max = in.readString();
        tournament_type = in.readString();
        attachment = in.readString();
        square_img = in.readString();
        horizontal_rectangle = in.readString();
        vertical_rectangle = in.readString();
        circle_img = in.readString();
        telegram_link = in.readString();
        can_register = Boolean.parseBoolean(in.readString());
    }

    public static final Creator<TournamentData> CREATOR = new Creator<TournamentData>() {
        @Override
        public TournamentData createFromParcel(Parcel in) {
            return new TournamentData(in);
        }

        @Override
        public TournamentData[] newArray(int size) {
            return new TournamentData[size];
        }
    };

    public TournamentData(JSONObject jsonObject) {
        setTournamentData(jsonObject);
    }

    public void setTournamentData(JSONObject jsonObject) {
        try {
            if (jsonObject.has("ID")) {
                id = jsonObject.getString("ID");
            }

            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }
            if (jsonObject.has("start_date")) {
                start_date = jsonObject.getString("start_date");
            }
            if (jsonObject.has("end_date")) {
                end_date = jsonObject.getString("end_date");
            }
            if (jsonObject.has("touranament_start_date")) {
                tournament_start_date = jsonObject.getString("touranament_start_date");
            }
            if (jsonObject.has("touranament_end_date")) {
                tournament_end_date = jsonObject.getString("touranament_end_date");
            }
            if (jsonObject.has("entry_price")) {
                entry_price = jsonObject.getString("entry_price");
            }
            if (jsonObject.has("coin_amount")) {
                coin_amount = jsonObject.getString("coin_amount");
            }
            if (jsonObject.has("coin_earnings")) {
                coin_earnings = jsonObject.getString("coin_earnings");
            }
            if (jsonObject.has("registration_reward")) {
                registration_reward = jsonObject.getString("registration_reward");
            }

            if (jsonObject.has("payment_type")) {
                payment_type = jsonObject.getString("payment_type");
            }

            if (jsonObject.has("min")) {
                min = jsonObject.getString("min");
            }

            if (jsonObject.has("max")) {
                max = jsonObject.getString("max");
            }

            if (jsonObject.has("tournament_type")) {
                tournament_type = jsonObject.getString("tournament_type");
            }

            if (jsonObject.has("attachment")) {
                attachment = jsonObject.getString("attachment");
            }
            if (jsonObject.has("square_img")) {
                square_img = jsonObject.getString("square_img");
            }
            if (jsonObject.has("horizontal_rectangle")) {
                horizontal_rectangle = jsonObject.getString("horizontal_rectangle");
            }
            if (jsonObject.has("vertical_rectangle")) {
                vertical_rectangle = jsonObject.getString("vertical_rectangle");
            }
            if (jsonObject.has("circle_img")) {
                circle_img = jsonObject.getString("circle_img");
            }

            if (jsonObject.has("telegram_link")) {
                telegram_link = jsonObject.getString("telegram_link");
            }

            if (jsonObject.has("rules")) {
                JSONArray jsonArray = jsonObject.getJSONArray("rules");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String rule = (String) jsonArray.get(i);
                    rules.add(rule);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(start_date);
        parcel.writeString(end_date);
        parcel.writeString(tournament_start_date);
        parcel.writeString(tournament_end_date);
        parcel.writeString(entry_price);
        parcel.writeString(coin_amount);
        parcel.writeString(coin_earnings);
        parcel.writeString(registration_reward);
        parcel.writeString(payment_type);
        parcel.writeString(min);
        parcel.writeString(max);
        parcel.writeString(tournament_type);
        parcel.writeString(attachment);
        parcel.writeString(square_img);
        parcel.writeString(horizontal_rectangle);
        parcel.writeString(vertical_rectangle);
        parcel.writeString(circle_img);
        parcel.writeString(telegram_link);
        parcel.writeString(String.valueOf(can_register));
    }
}
