package com.app.itaptv.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.VerificationActivity;
import com.app.itaptv.structure.User;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.app.itaptv.activity.GiveAwayActivity.GIVE_AWAY_CODE;
import static com.app.itaptv.utils.Constant.KEY_MOBILE;

public class CampaignCustomFrag extends Fragment implements View.OnClickListener {

    View view;
    ImageView contentImage;
    ImageButton enrollBtn, noThanksBtn;
    User user;
    int campaignId;
    boolean showWinnerScreen;

    public CampaignCustomFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_campaign_regular, container, false);

        init();

        return view;
    }

    private void init() {

        user = LocalStorage.getUserData();

        contentImage = view.findViewById(R.id.iv_content_image_reg);
        enrollBtn = view.findViewById(R.id.btn_enroll_reg);
        noThanksBtn = view.findViewById(R.id.btn_no_thanks_reg);

        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.color.back_black);
        String url = getArguments().getString("url");
        campaignId = getArguments().getInt("campaignId");
        showWinnerScreen = getArguments().getBoolean("showWinnerScreen");

        Glide.with(this)
                .load(url)
                .apply(options)
                .into(contentImage);

        enrollBtn.setOnClickListener(this);
        noThanksBtn.setOnClickListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_enroll_reg:
                if (user.mobile == null || user.mobile.equals("")) {
                    startActivityForResult(new Intent(getActivity(), VerificationActivity.class).putExtra(VerificationActivity.TYPE_KEY, KEY_MOBILE), GIVE_AWAY_CODE);
                } else {
                    callEnrollApi();
                }
                break;

            case R.id.btn_no_thanks_reg:
                finishFlow();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GIVE_AWAY_CODE) {
            user = LocalStorage.getUserData();
            if (user.mobile != null && !user.mobile.equals("")) {
                callEnrollApi();
            }
        }
    }

    private void callEnrollApi() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("userid", LocalStorage.getUserId());
            params.put("mobile", user.mobile);
            params.put("campaignid", String.valueOf(campaignId));
            APIRequest apiRequest = new APIRequest(Url.GET_USER_ENROLL_CAMPAIGN, Request.Method.POST, params, null, getActivity());
            apiRequest.showLoader = true;

            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                handleGetUserEnrollResponse(response, error);

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGetUserEnrollResponse(String response, Exception error) {
        Log.d("User Enroll Campaign Response:", response);
        try {
            if (error != null) {
                showAlert(false, error.getMessage());
            } else {
                if (response != null) {
                    Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        //showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        boolean status = jsonArrayMsg.getBoolean("status");
                        if (status) {
                            String jsonObjectMessage = jsonArrayMsg.getString("message");
                            showAlert(true, jsonObjectMessage);
                        } else {
                            String message = jsonArrayMsg.getString("message");
                            showAlert(false, message);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(boolean isAlert, @Nullable String errorMessage) {
        if (isAlert) {
            AlertUtils.showAlert("", errorMessage, getString(R.string.ok), getActivity(), isPositiveAction -> {
                finishFlow();
            });
        } else {
            AlertUtils.showAlert("", errorMessage, getString(R.string.ok), getActivity(), null);
        }
    }

    private void finishFlow() {
        if (showWinnerScreen) {
            getActivity().finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra("USER_REFER", 0);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }

}