package com.app.itaptv.activity;

import androidx.annotation.Nullable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.structure.ParticipantData;
import com.app.itaptv.structure.TournamentData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateTeamActivity extends BaseActivity {

    EditText etUID;
    EditText etTeamName;
    EditText etLeaderName;
    EditText etLeaderMobileNumber;
    Button btCreateTeam;

    //payment
    LinearLayout paymentRoot;
    RelativeLayout rlPayTm, rlRazor, rlCoins;
    TextView tvCoins;

    ParticipantData participantData;
    boolean isRegistered = false;

    TournamentData tournamentData;
    String requiredCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        if (getIntent() != null) {
            requiredCoins = getIntent().getStringExtra("requiredcoins");
        }
        tournamentData = LocalStorage.getEsportsData();
        init();
    }

    public void init() {
        etUID = findViewById(R.id.etLeaderUID);
        etTeamName = findViewById(R.id.etTeamName);
        etLeaderName = findViewById(R.id.etLeaderName);
        etLeaderMobileNumber = findViewById(R.id.etLeaderMobileNumber);
        btCreateTeam = findViewById(R.id.btCreateTeam);

        setUserDetails();

        btCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                createTeam();
            }
        });

        paymentRoot = findViewById(R.id.llPayment);
        rlRazor = findViewById(R.id.rlRazorPay);
        rlPayTm = findViewById(R.id.rlPayTm);
        rlCoins = findViewById(R.id.rlCoinsPay);
        tvCoins = findViewById(R.id.tvCoins);

        rlRazor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (participantData != null) {
                    Intent intent = new Intent(CreateTeamActivity.this, TournamentSummaryActivity.class);
                    intent.putExtra("paymentMode", "razorpay");
                    intent.putExtra("participantId", participantData.id);
                    startActivity(intent);
                    finish();
                    //razorpayPrepayment(participantData.id);
                }
            }
        });

        rlPayTm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (participantData != null) {
                    Intent intent = new Intent(CreateTeamActivity.this, TournamentSummaryActivity.class);
                    intent.putExtra("paymentMode", "paytm");
                    intent.putExtra("participantId", participantData.id);
                    startActivity(intent);
                    finish();
                    //paytmPrepayment(participantData.id);
                }
            }
        });

        rlCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (participantData != null) {
                    Intent intent = new Intent(CreateTeamActivity.this, TournamentSummaryActivity.class);
                    intent.putExtra("paymentMode", "coins");
                    intent.putExtra("participantId", participantData.id);
                    startActivity(intent);
                    finish();
                    //paytmPrepayment(participantData.id);
                }
            }
        });
    }

    public void setUserDetails() {
        etLeaderName.setText(LocalStorage.getUserData().displayName);
        etLeaderMobileNumber.setText(LocalStorage.getUserData().mobile);
    }

    public void createTeam() {
        try {
            String uid = etUID.getText().toString();
            String teamName = etTeamName.getText().toString();
            String userName = etLeaderName.getText().toString();
            String mobileNumber = etLeaderMobileNumber.getText().toString();

            if (uid.isEmpty()) {
                showError(getString(R.string.error_enter_team_leader_UID));
                return;
            }
            if (teamName.isEmpty()) {
                showError(getString(R.string.error_enter_team_name));
                return;
            }
            if (userName.isEmpty()) {
                showError(getString(R.string.error_enter_leader_name));
                return;
            }

            if (mobileNumber.equals("")) {
                showError(getString(R.string.error_msg_enter_mobile_no));
                return;
            }

            if (!Utility.isValidMobile(mobileNumber)) {
                showError(getString(R.string.please_enter_a_valid_mobile_number));
                return;
            }

            Map<String, String> params = new HashMap<>();
            params.put("type", Constant.CREATE);
            params.put("tournament_id", tournamentData.id);
            params.put("game_id", uid);
            params.put("team_name", teamName);
            params.put("username", userName);
            params.put("mobile_no", mobileNumber);
            APIRequest apiRequest = new APIRequest(Url.PARTICIPATING_IN_TOURNAMENT,
                    Request.Method.POST, params, null, this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                handleCreateTeamResponse(response, error);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCreateTeamResponse(@Nullable String response, @Nullable Exception error) {
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
                        TournamentData tournamentData = new TournamentData(jsonArrayTournament);

                        isRegistered = true;

                        JSONArray jsonArrayParticipant = jsonArrayMsg.getJSONArray("participant");
                        for (int i = 0; i < jsonArrayParticipant.length(); i++) {
                            JSONObject jsonObjectParticipant = (JSONObject) jsonArrayParticipant.get(i);
                            ParticipantData participant = new ParticipantData(jsonObjectParticipant);
                            //participantDataList.add(participantData);
                            if (participant.user_id.equals(LocalStorage.getUserId())) {
                                participantData = participant;
                            }
                        }
                        //updateUI(tournamentData, participantDataList);
                        if (LocalStorage.getEsportsData().tournament_type.equalsIgnoreCase("paid")) {
                            showScreen(participantData);
                        }else if (LocalStorage.getEsportsData().tournament_type.equalsIgnoreCase("free")){
                            startActivity(new Intent(CreateTeamActivity.this, TournamentSummaryActivity.class).putExtra(TournamentSummaryActivity.MESSAGE,jsonArrayMsg.getString("message")));
                            finish();
                        }
                    }
                }
            }
        } catch (JSONException e) {

        }
    }

    public void showScreen(ParticipantData participant) {
        if (participant.isPaid) {
            startActivity(new Intent(CreateTeamActivity.this, TournamentSummaryActivity.class));
            finish();
        } else {
            btCreateTeam.setVisibility(View.GONE);
            paymentRoot.setVisibility(View.VISIBLE);

            long walletBalance = 0;
            long coinsForEntry = 0;
            if (HomeActivity.toolbarWalletBalance != null) {
                walletBalance = LocalStorage.getValue(LocalStorage.KEY_WALLET_BALANCE,0,Long.class);
            }
            if (requiredCoins != null) {
                coinsForEntry = Integer.parseInt(requiredCoins);
            }
            if (coinsForEntry <= walletBalance) {
                rlCoins.setVisibility(View.VISIBLE);
                tvCoins.setText(requiredCoins + " " + getString(R.string.icoins));
            } else {
                rlCoins.setVisibility(View.GONE);
            }
        }
    }

    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, this, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isRegistered) {
            startActivity(new Intent(CreateTeamActivity.this, TournamentSummaryActivity.class));
            finish();
        } else {
            startActivity(new Intent(CreateTeamActivity.this, TournamentRegistrationFormActivity.class));
            finish();
        }
    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}