package com.app.itaptv.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.fragment.CampaignCustomFrag;
import com.app.itaptv.fragment.CampaignRegularFrag;
import com.app.itaptv.structure.User;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.app.itaptv.activity.LoginActivity.REQUEST_CONTEST_WINNERS;

public class GiveAwayActivity extends AppCompatActivity {

    public static final String FRAG_TYPE_REGULAR = "regular";
    public static final String FRAG_TYPE_CUSTOM = "custom";
    public static final int GIVE_AWAY_CODE = 27;

    String TAG = String.valueOf(GiveAwayActivity.class);
    ProgressBar progressBar;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_away);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
    }

    public void init() {

        user = LocalStorage.getUserData();
        setupDataFromApi();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GIVE_AWAY_CODE) {
            user = LocalStorage.getUserData();
            if (user.mobile != null && !user.mobile.equals("")) {
                setupDataFromApi();
            }
        } else if (requestCode == REQUEST_CONTEST_WINNERS) {
            startActivityForResult(new Intent(this, PastContestWinners.class), 1);
            //overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        }
    }

    public void setupDataFromApi() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("userid", LocalStorage.getUserId());
            //params.put("mobile", "123456789");
            //params.put("mobile", user.mobile);
            APIRequest apiRequest = new APIRequest(Url.GET_CAMPAIGN, Request.Method.POST, params, null, this);
            apiRequest.showLoader = true;

            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                handleGetCampaignResponse(response, error);

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGetCampaignResponse(String response, Exception error) {
        try {
            if (error != null) {
                Log.d(TAG, error.getMessage());
                // showError(error.getMessage());
                Intent intent = new Intent();
                intent.putExtra("USER_REFER", 1);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                if (response != null) {
                    Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showAlert(true, message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        boolean showWinnerScreen = jsonArrayMsg.has("is_winner") ? jsonArrayMsg.getBoolean("is_winner") : false;
                        boolean status = jsonArrayMsg.getBoolean("status");
                        if (status) {
                            JSONObject jsonObjectContents = jsonArrayMsg.getJSONObject("data");
                            JSONObject jsonImageContent = jsonObjectContents.getJSONObject("upload_custom_image");
                            int campaignId = jsonObjectContents.getInt("ID");
                            String fragType = jsonObjectContents.has("campaign_type") ? jsonObjectContents.getString("campaign_type") : "";
                            if (fragType.equals(FRAG_TYPE_REGULAR)) {
                                String title = jsonObjectContents.has("post_title") ? jsonObjectContents.getString("post_title") : "";
                                String subTitle = jsonObjectContents.has("sub_title") ? jsonObjectContents.getString("sub_title") : "";
                                String description = jsonObjectContents.has("description") ? jsonObjectContents.getString("description") : "";
                                String url = jsonImageContent.has("url") ? jsonImageContent.getString("url") : "";
                                openFragTypeRegular(campaignId, title, subTitle, description, url, showWinnerScreen);
                            } else if (fragType.equals(FRAG_TYPE_CUSTOM)) {
                                String url = jsonImageContent.has("url") ? jsonImageContent.getString("url") : "";
                                openFragTypeCustom(campaignId, url, showWinnerScreen);
                            }
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
            AlertUtils.showAlert(Constant.ALERT_TITLE, errorMessage, getString(R.string.ok), this, isPositiveAction -> {
                finishFlow();
            });
        } else {
            finishFlow();
        }
    }

    private void finishFlow() {
        Intent intent = new Intent();
        intent.putExtra("USER_REFER", 0);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void openFragTypeCustom(int campaignId, String url, boolean showWinnerScreen) {
        CampaignCustomFrag campaignCustomFrag = new CampaignCustomFrag();
        Bundle bundle = new Bundle();
        bundle.putInt("campaignId", campaignId);
        bundle.putString("url", url);
        bundle.putBoolean("showWinnerScreen", showWinnerScreen);
        campaignCustomFrag.setArguments(bundle);
        openFragment(campaignCustomFrag);
    }

    public void openFragTypeRegular(int campaignId, String title, String subTitle, String description, String url, boolean showWinnerScreen) {
        CampaignRegularFrag campaignRegularFrag = new CampaignRegularFrag();
        Bundle bundle = new Bundle();
        bundle.putInt("campaignId", campaignId);
        bundle.putString("title", title);
        bundle.putString("subTitle", subTitle);
        bundle.putString("description", description);
        bundle.putString("url", url);
        bundle.putBoolean("showWinnerScreen", showWinnerScreen);
        campaignRegularFrag.setArguments(bundle);
        openFragment(campaignRegularFrag);
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frag_give_away_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}