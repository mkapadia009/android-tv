package com.app.itaptv.structure;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class ParticipantData implements Parcelable {

    public String id="";
    public String user_id="";
    public String game_id="";
    public String team_name="";
    public String username="";
    public String mobile_no="";
    public String is_leader="";
    public String tournament_id="";
    public boolean isPaid;

    public ParticipantData(JSONObject jsonObject){
        setParticipantData(jsonObject);
    }

    protected ParticipantData(Parcel in) {
        id = in.readString();
        user_id = in.readString();
        game_id = in.readString();
        team_name = in.readString();
        username = in.readString();
        mobile_no = in.readString();
        is_leader = in.readString();
        tournament_id = in.readString();
        isPaid = Boolean.parseBoolean(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user_id);
        dest.writeString(game_id);
        dest.writeString(team_name);
        dest.writeString(username);
        dest.writeString(mobile_no);
        dest.writeString(is_leader);
        dest.writeString(tournament_id);
        dest.writeString(String.valueOf(isPaid));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParticipantData> CREATOR = new Creator<ParticipantData>() {
        @Override
        public ParticipantData createFromParcel(Parcel in) {
            return new ParticipantData(in);
        }

        @Override
        public ParticipantData[] newArray(int size) {
            return new ParticipantData[size];
        }
    };

    public void setParticipantData(JSONObject jsonObject){
        try {
            if (jsonObject.has("_id")) {
                JSONObject _id = jsonObject.getJSONObject("_id");
                id=_id.getString("$oid");

            }
            if (jsonObject.has("user_id")) {
                user_id = jsonObject.getString("user_id");
            }
            if (jsonObject.has("game_id")) {
                game_id = jsonObject.getString("game_id");
            }
            if (jsonObject.has("team_name")) {
                team_name = jsonObject.getString("team_name");
            }
            if (jsonObject.has("username")) {
                username = jsonObject.getString("username");
            }
            if (jsonObject.has("mobile_no")) {
                mobile_no = jsonObject.getString("mobile_no");
            }
            if (jsonObject.has("is_leader")) {
                is_leader = jsonObject.getString("is_leader");
            }

            if (jsonObject.has("tournament_id")) {
                tournament_id= jsonObject.getString("tournament_id");
            }

            if (jsonObject.has("is_paid")) {
                isPaid= jsonObject.getBoolean("is_paid");
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
