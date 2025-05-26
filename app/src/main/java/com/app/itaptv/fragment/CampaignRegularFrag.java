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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.VerificationActivity;
import com.app.itaptv.structure.User;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.app.itaptv.activity.GiveAwayActivity.GIVE_AWAY_CODE;
import static com.app.itaptv.utils.Constant.KEY_MOBILE;

public class CampaignRegularFrag extends Fragment implements View.OnClickListener {

    TextView title, subTitle, description, noThanks;
    ImageView contentImage;
    Button enrollBtn;
    View view;
    User user;
    int campaignId;
    boolean showWinnerScreen;

    public CampaignRegularFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_campaign_custom, container, false);

        init();
        return view;
    }

    private void init() {

        user = LocalStorage.getUserData();

        title = view.findViewById(R.id.tv_title_cust);
        subTitle = view.findViewById(R.id.tv_subTitle_cust);
        description = view.findViewById(R.id.tv_description_cust);
        noThanks = view.findViewById(R.id.tv_noThanks_cust);
        contentImage = view.findViewById(R.id.iv_content_image_cust);
        enrollBtn = view.findViewById(R.id.btn_enroll_cust);

        if (getArguments() != null) {

            title.setText(getArguments().getString("title"));
            subTitle.setText(getArguments().getString("subTitle"));
            description.setText(getArguments().getString("description"));
            campaignId = getArguments().getInt("campaignId");
            showWinnerScreen = getArguments().getBoolean("showWinnerScreen");

            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.color.back_black);
            String url = getArguments().getString("url");
            Glide.with(this)
                    .load(url)
                    .apply(options)
                    .into(contentImage);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
                        showAlert(false, message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        String jsonObjectMessage = jsonArrayMsg.getString("message");
                        showAlert(true, jsonObjectMessage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(boolean isAlert, @Nullable String errorMessage) {
        AlertUtils.showAlert(isAlert ? Constant.ALERT_TITLE : Constant.ERROR_TITLE, errorMessage, getString(R.string.ok), getActivity(), isPositiveAction -> {
            finishFlow();
        });
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
