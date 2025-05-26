package com.app.itaptv.holder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.structure.LiveNowData;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LiveNowViewListHolder extends KRecyclerViewHolder {

    static DisplayMetrics displaymetrics;
    View view;
    ImageView imageBanner;
    TextView tvTitle, tvTime, tvScheduledTimeAndDuration, tvDescription;
    CardView row_upcomming;
    Button btnAskQuestion, btnRemindMe;
    LinearLayout llAskAQuestion, llRemindMe;
    String streamDate;
    boolean flag;

    public LiveNowViewListHolder(View itemView, boolean flags) {
        super(itemView);
        view = itemView;
        imageBanner = itemView.findViewById(R.id.ivImage);
        tvTitle = itemView.findViewById(R.id.tvStoreName);
        tvScheduledTimeAndDuration = itemView.findViewById(R.id.tvTime);
        tvDescription = itemView.findViewById(R.id.tvDescription);
        row_upcomming = itemView.findViewById(R.id.row_upcomming);
        btnAskQuestion = itemView.findViewById(R.id.btAskAQuestion);
        btnRemindMe = itemView.findViewById(R.id.btRemindMe);
        llAskAQuestion = itemView.findViewById(R.id.llAskAQuestion);
        llRemindMe = itemView.findViewById(R.id.llRemindMe);
        flag = flags;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);
        if (itemObject instanceof LiveNowData) {
            tvTitle.setText(((LiveNowData) itemObject).title);
            streamDate = Utility.formatDate("YYYY-MM-DD HH:mm:ss", "EE MMM dd, hh:mm a", ((LiveNowData) itemObject).time).replace("AM", "am").replace("PM", "pm");
            tvScheduledTimeAndDuration.setText(streamDate + " | " + ((LiveNowData) itemObject).duration + "mins");
            tvDescription.setText(((LiveNowData) itemObject).description);

            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                displaymetrics = new DisplayMetrics();
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            }
            if (flag) {
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                //if you need fix imageview height
                int devicewidth = (int) (displaymetrics.widthPixels);
                int int_width = (int) (devicewidth / 1.2);
                imageBanner.getLayoutParams().width = int_width;
                imageBanner.getLayoutParams().height = (int) (int_width / 2);
                row_upcomming.getLayoutParams().width = int_width;
            } else {
                /*((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                //if you need fix imageview height
                int devicewidth = (int) (displaymetrics.widthPixels);
                imageBanner.getLayoutParams().width = devicewidth;
                imageBanner.getLayoutParams().height = (int) (devicewidth / 2);
                row_upcomming.getLayoutParams().width = devicewidth;*/
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                //if you need fix imageview height
                int devicewidth = (int) (displaymetrics.widthPixels);
                int int_width = (int) (devicewidth / 1.1);
                imageBanner.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                imageBanner.getLayoutParams().height = (int) (int_width / 2);
                row_upcomming.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            }
            Glide.with(context)
                    .load(((LiveNowData) itemObject).imageUrl)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(200,200)
                    .placeholder(R.color.tab_grey)
                    .into(imageBanner);

            llAskAQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAskAQuestionDialog(context, ((LiveNowData) itemObject).title, ((LiveNowData) itemObject).id);
                }
            });
            llRemindMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showRemindMeDialog(context, ((LiveNowData) itemObject).title, ((LiveNowData) itemObject).id);
                }
            });
        }
    }


    private void showAskAQuestionDialog(Context context, String title, int id) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_ask_a_question, null);

        final TextView tv_message = (TextView) view.findViewById(R.id.tvtitle);
        final EditText et_remark = (EditText) view.findViewById(R.id.et_remark);
        Button bt_cancel = (Button) view.findViewById(R.id.btcancel);
        Button bt_send = (Button) view.findViewById(R.id.btsend);

        tv_message.setText(title);
        final Dialog alertDialogBuilder = new Dialog(context);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setContentView(view);
        //final Dialog alertDialog_decline = alertDialogBuilder.create();
        alertDialogBuilder.show();

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogBuilder.dismiss();
            }
        });
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s_remark = et_remark.getText().toString();
                sendQuestion(context, s_remark, id);
                alertDialogBuilder.dismiss();
            }
        });
    }

    private void sendQuestion(Context context, String remark, int id) {
        Map<String, String> params = new HashMap<>();
        params.put("message", remark);
        params.put("ID", String.valueOf(id));
        Log.d("params", params.toString());
        APIRequest apiRequest = new APIRequest(Url.ASK_QUESTION, Request.Method.POST, params, null, context);
        apiRequest.showLoader = false;
        APIManager.request(apiRequest, new APIResponse() {
            @Override
            public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                handleaskquestionResponce(response, error, statusCode);
            }
        });
    }

    private void handleaskquestionResponce(String response, Exception error, int statusCode) {
        try {

            if (error != null) {
                // showError(error.getMessage());

            } else {
                if (response != null) {
                    Log.e("response", response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;

                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                    }
                }
            }


        } catch (JSONException e) {

        }

    }


    private void showRemindMeDialog(Context context, String title, int id) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_remind_me, null);

        final TextView tv_message = (TextView) view.findViewById(R.id.tvtitle);
        RadioGroup radioGroup = view.findViewById(R.id.rg);
        Button bt_cancel = (Button) view.findViewById(R.id.btcancel);
        Button bt_send = (Button) view.findViewById(R.id.btsend);

        tv_message.setText(title);
        final Dialog alertDialogBuilder = new Dialog(context);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setContentView(view);
        alertDialogBuilder.show();

        final int[] minutes = {15};
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_15min:
                        minutes[0] = 15;
                        break;
                    case R.id.rb_30min:
                        minutes[0] = 30;
                        break;
                    case R.id.rb_60min:
                        minutes[0] = 60;
                        break;
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogBuilder.dismiss();
            }
        });
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReminder(context, id, minutes[0]);
                alertDialogBuilder.dismiss();
            }
        });
    }

    private void sendReminder(Context context, int id, int minute) {
        Map<String, String> params = new HashMap<>();
        params.put("minutes", String.valueOf(minute));
        params.put("ID", String.valueOf(id));
        Log.d("params", params.toString());
        APIRequest apiRequest = new APIRequest(Url.REMIND_ME, Request.Method.POST, params, null, context);
        apiRequest.showLoader = false;
        APIManager.request(apiRequest, new APIResponse() {
            @Override
            public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                handleRemindMeResponce(response, error, statusCode);
            }
        });
    }

    private void handleRemindMeResponce(String response, Exception error, int statusCode) {
        try {

            if (error != null) {
                // showError(error.getMessage());

            } else {
                if (response != null) {
                    Log.e("response", response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;

                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                    }
                }
            }


        } catch (JSONException e) {

        }

    }
}
