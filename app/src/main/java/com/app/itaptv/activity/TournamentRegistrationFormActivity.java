package com.app.itaptv.activity;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.structure.TournamentData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TournamentRegistrationFormActivity extends BaseActivity {

    public static String CONTENT_DATA = "contentData";
    public static final String REQUEST_CODE_EXTRA = "requestCodeExtra";
    public static int REQUEST_CODE_ESPORTS = 500;

    //FeedContentData feedContentData;
    String postId = "";

    ProgressBar progressBar;
    LinearLayout llTeaser;
    ImageView ivTeaserImage;
    TextView tvTournamentName;
    TextView tvTournamentStartDate;
    TextView tvTournamentEndDate;
    TextView tvRegistrationStartDate;
    TextView tvRegistrationEndDate;
    TextView tvEntryFees;
    TextView tvOr;
    TextView tvFree;
    TextView tvCoinsForEntry;
    TextView tvPrize;
    TextView tvRegistrationReward;
    TextView tvRegistrationNotAllowed;
    Button btCreateTeam;
    Button btJoinTeam;
    LinearLayout llTelegramLink;
    View llprogressbar;
    RelativeLayout rlRules;
    LinearLayout llRules;
    LinearLayout llParent;
    ImageView ivCollapsed;
    ImageView ivExpand;
    AppBarLayout appBarLayoutTeaser;
    TournamentData tournamentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_registration_form);
        /*if (getIntent() != null) {
            feedContentData = (FeedContentData) getIntent().getParcelableExtra(CONTENT_DATA);
        }*/
        // feedContentData= LocalStorage.getEsportsFeedData();
        postId = LocalStorage.getValue(LocalStorage.KEY_ESPORTS_ID, "", String.class);
        ;
        init();
    }

    public void init() {
        progressBar = findViewById(R.id.progressBar);
        llprogressbar = findViewById(R.id.llprogressbar);
        llParent = findViewById(R.id.llParent);
        progressBar.setVisibility(View.VISIBLE);
        llprogressbar.setVisibility(View.VISIBLE);
        llParent.setVisibility(View.GONE);
        llTeaser = findViewById(R.id.llTeaser);
        ivTeaserImage = findViewById(R.id.ivTeaserImage);
        tvTournamentName = findViewById(R.id.tvTournamentName);
        tvTournamentStartDate = findViewById(R.id.tvTournamentStartDate);
        tvTournamentEndDate = findViewById(R.id.tvTournamentEndDate);
        tvRegistrationStartDate = findViewById(R.id.tvRegistrationStartDate);
        tvRegistrationEndDate = findViewById(R.id.tvRegistrationEndDate);
        tvEntryFees = findViewById(R.id.tvEntryFees);
        tvOr = findViewById(R.id.tvOr);
        tvFree = findViewById(R.id.tvFree);
        tvCoinsForEntry = findViewById(R.id.tvCoinsForEntry);
        tvPrize = findViewById(R.id.tvPrize);
        tvRegistrationReward = findViewById(R.id.tvRegistrationReward);
        tvRegistrationNotAllowed = findViewById(R.id.tvRegistrationNotAllowed);
        btCreateTeam = findViewById(R.id.btCreateTeam);
        btJoinTeam = findViewById(R.id.btJoinTeam);
        llTelegramLink = findViewById(R.id.llTelegramLink);
        rlRules = findViewById(R.id.rlRules);
        llRules = findViewById(R.id.llRules);
        ivCollapsed = findViewById(R.id.ivCollapsed);
        ivExpand = findViewById(R.id.ivExpand);
        appBarLayoutTeaser = findViewById(R.id.appBarLayout);

        llTeaser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playTeaser();
            }
        });

        btCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tournamentData != null) {
                    startActivity(new Intent(getApplicationContext(), CreateTeamActivity.class).putExtra("requiredcoins", tournamentData.coin_amount));
                    finish();
                }
            }
        });

        btJoinTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tournamentData != null) {
                    startActivity(new Intent(getApplicationContext(), JoinTeamActivity.class).putExtra("requiredcoins", tournamentData.coin_amount));
                    finish();
                }
            }
        });

        llTelegramLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tournamentData != null) {
                    if (!tournamentData.telegram_link.isEmpty()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tournamentData.telegram_link));
                        startActivity(intent);
                    }
                }
            }
        });

        rlRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int visibility = llRules.getVisibility();
                if (visibility == 0) {
                    llRules.setVisibility(View.GONE);
                    ivExpand.setVisibility(View.GONE);
                    ivCollapsed.setVisibility(View.VISIBLE);
                } else {
                    llRules.setVisibility(View.VISIBLE);
                    ivExpand.setVisibility(View.VISIBLE);
                    ivCollapsed.setVisibility(View.GONE);
                }
            }
        });

        getUserRegisteredTournamentInfo();
    }

    public void playTeaser() {
        HomeActivity.getInstance().pausePlayer();
        if (tournamentData != null) {
            if (!tournamentData.attachment.isEmpty()) {
                //startActivity(new Intent(this, ESportsPlayerActivity.class).putExtra(CONTENT_DATA, feedContentData));
                startActivityForResult(new Intent(this, TeaserActivity.class).putExtra(CONTENT_DATA, tournamentData.attachment).putExtra(REQUEST_CODE_EXTRA, REQUEST_CODE_ESPORTS), REQUEST_CODE_ESPORTS);
            }
        }
    }

    public void getUserRegisteredTournamentInfo() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.GET_USER_REGISTERED_TOURNAMENT_INFO + "&tournament_id=" + postId,
                    Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                progressBar.setVisibility(View.GONE);
                llprogressbar.setVisibility(View.GONE);
                handleUserRegisteredTournamentInfo(response, error);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleUserRegisteredTournamentInfo(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        JSONObject jsonArrayTournament = jsonArrayMsg.getJSONObject("tournament");
                        tournamentData = new TournamentData(jsonArrayTournament);
                        LocalStorage.saveEsportsData(tournamentData);
                        Log.i("TAG", "" + tournamentData);
                        boolean canRegister = jsonArrayMsg.has("can_register") ? jsonArrayMsg.getBoolean("can_register") : false;
                        tournamentData.can_register = canRegister;
                        updateUI(tournamentData);
                    }
                }
            }
        } catch (JSONException e) {

        }
    }

    public void updateUI(TournamentData tournamentData) {
        llParent.setVisibility(View.VISIBLE);
        if (tournamentData.attachment != null && !tournamentData.attachment.isEmpty() && !tournamentData.attachment.equalsIgnoreCase("null")) {
            appBarLayoutTeaser.setVisibility(View.VISIBLE);
        } else {
            appBarLayoutTeaser.setVisibility(View.GONE);
        }
        if (tournamentData.tournament_type.equalsIgnoreCase("paid")) {
            tvFree.setVisibility(View.GONE);
            tvCoinsForEntry.setVisibility(View.VISIBLE);
            tvEntryFees.setVisibility(View.VISIBLE);
            tvOr.setVisibility(View.VISIBLE);

            tvCoinsForEntry.setText(tournamentData.coin_amount);
            tvEntryFees.setText("₹" + tournamentData.entry_price);
        } else if (tournamentData.tournament_type.equalsIgnoreCase("free")) {
            tvFree.setVisibility(View.VISIBLE);
            tvCoinsForEntry.setVisibility(View.GONE);
            tvEntryFees.setVisibility(View.GONE);
            tvOr.setVisibility(View.GONE);
        }

        if (!tournamentData.horizontal_rectangle.isEmpty()) {
            Glide.with(getApplicationContext())
                    .load(tournamentData.horizontal_rectangle)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivTeaserImage);
        }

        tvPrize.setText(tournamentData.coin_earnings);
        tvRegistrationReward.setText(tournamentData.registration_reward);
        tvTournamentName.setText(tournamentData.name);
        tvTournamentStartDate.setText(Utility.formatDate("yyyy-MM-dd hh:mm:ss", "dd MMM yyyy, hh:mm aa", tournamentData.tournament_start_date));
        tvTournamentEndDate.setText(Utility.formatDate("yyyy-MM-dd hh:mm:ss", "dd MMM yyyy, hh:mm aa", tournamentData.tournament_end_date));
        tvRegistrationStartDate.setText(Utility.formatDate("yyyy-MM-dd hh:mm:ss", "dd MMM yyyy, hh:mm aa", tournamentData.start_date));
        tvRegistrationEndDate.setText(Utility.formatDate("yyyy-MM-dd hh:mm:ss", "dd MMM yyyy, hh:mm aa", tournamentData.end_date));

        if (tournamentData.can_register) {
            tvRegistrationNotAllowed.setVisibility(View.GONE);
            btCreateTeam.setVisibility(View.VISIBLE);
            btJoinTeam.setVisibility(View.VISIBLE);
        } else {
            tvRegistrationNotAllowed.setVisibility(View.VISIBLE);
            btCreateTeam.setVisibility(View.GONE);
            btJoinTeam.setVisibility(View.GONE);
        }

        for (int i = 0; i < tournamentData.rules.size(); i++) {
            TextView tv = new TextView(this);
            tv.setGravity(CENTER);
            tv.setTextAppearance(R.style.HeaderRegularStyleRent);
            tv.setText(tournamentData.rules.get(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 30, 0, 0);
            tv.setLayoutParams(params);
            tv.setTextColor(Color.WHITE);

            llRules.addView(tv);
        }
    }

    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, this, null);
    }
}