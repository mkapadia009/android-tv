package com.app.itaptv.activity;

import static com.app.itaptv.activity.LoginActivity.REQUEST_BONUS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.adapter.CountryAdapter;
import com.app.itaptv.structure.CountryData;
import com.app.itaptv.structure.User;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.AppSignatureHelper;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobileVerificationActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, TextWatcher {

    private static final int OTP_INIT = 1;
    private static final int OTP_SIGN_IN = 2;
    private static final int TOTAL_OTP_DIGITS = 6;
    int newUser, isNewUser;

    /**
     * Receiver to catch when otp message arrives.
     */
    private final BroadcastReceiver otpMessageObserver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null && intent.getAction().equals(Constant.OTP_MESSAGE)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String otp = bundle.getString("otpMessage");
                    if (otp != null && !otp.isEmpty()) {
                        // Auto fill otp
                        setOtp(otp);
                    }
                }
            }
        }
    };

    LinearLayout llOtp;
    LinearLayout llOtpDigits;
    TextView tvResendOtp;
    EditText etMobileNumber;
    EditText etOtpDig1;
    Button btSendOtp;
    Button btLetsGo;
    Spinner spinnerCountry;
    IntentFilter filter;
    boolean isOtpInitialized = false;
    String mobileNumber = "";
    EditText[] etOtpDigits = new EditText[TOTAL_OTP_DIGITS];

    String type = "";
    String postID = "";

    CountryAdapter countryAdapter;
    List<CountryData> countryDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);

        if (getIntent() != null) {
            type = getIntent().getStringExtra(Constant.TYPE); // you will get the value "value1" from application 1
            postID = getIntent().getStringExtra(Constant.POSTID);
        }

        init();
        setupSmsRetriever();
    }

    private void init() {
        llOtp = findViewById(R.id.llOtp);
        llOtpDigits = findViewById(R.id.llOtpDigits);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        btLetsGo = findViewById(R.id.btLetsGo);
        btSendOtp = findViewById(R.id.btSendOtp);
        etOtpDig1 = findViewById(R.id.etOtpDig1);
        spinnerCountry = findViewById(R.id.spinnerCountry);

        btLetsGo.setOnClickListener(this);
        btSendOtp.setOnClickListener(this);
        tvResendOtp.setOnClickListener(this);

        if (getIntent() != null) {
            newUser = getIntent().getIntExtra("newuser", 0);
            isNewUser = getIntent().getIntExtra("isnewuser", 0);
        }

        showOtpView(false);
        initOtpEditText();

        etMobileNumber.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                    (actionId == EditorInfo.IME_ACTION_DONE)) {
                SendOtp();
            }
            return false;
        });

        countryAdapter = new CountryAdapter(MobileVerificationActivity.this, countryDataList);
        spinnerCountry.setAdapter(countryAdapter);
        getCountryData();
    }


    private void showOtpView(boolean apiSuccess) {
        if (apiSuccess) {
            llOtp.setVisibility(View.VISIBLE);
            tvResendOtp.setVisibility(View.VISIBLE);
            btSendOtp.setVisibility(View.GONE);
            btLetsGo.setVisibility(View.VISIBLE);
            etOtpDig1.requestFocus();
            return;
        } else {
            llOtp.setVisibility(View.GONE);
            tvResendOtp.setVisibility(View.GONE);
            btSendOtp.setVisibility(View.VISIBLE);
            btLetsGo.setVisibility(View.GONE);
        }
    }

    /**
     * Dynamically initialize Edit fields of Otp and store each edit text object in EditText Array
     */
    private void initOtpEditText() {
        for (int i = 0; i < etOtpDigits.length; i++) {
            EditText etDigit = (EditText) llOtpDigits.getChildAt(i);
            etOtpDigits[i] = etDigit;
            etDigit.addTextChangedListener(this);

        }
    }

    private void SendOtp() {
        mobileNumber = etMobileNumber.getText().toString();
        if (mobileNumber.equals("")) {
            showError(getString(R.string.error_msg_enter_mobile_no));
            return;
        }

        if (!Utility.isValidMobileNumber(mobileNumber, Integer.parseInt(countryDataList.get(spinnerCountry.getSelectedItemPosition()).numberLimit))) {
            showError(getString(R.string.please_enter_a_valid_mobile_number));
            return;
        }

        initiateOtpSignInAPI();
    }

    /**
     * Initialize process of receiving OTP through SMS
     */
    private void initiateOtpSignInAPI() {

        AppSignatureHelper signatureHelper = new AppSignatureHelper(MobileVerificationActivity.this);
        Log.v("RespectiveStore Key HAsh:", signatureHelper.getAppSignatures().get(0));
        //Toast.makeText(signatureHelper, signatureHelper.getAppSignatures().get(0), Toast.LENGTH_SHORT).show();

        try {
            Map<String, String> params = new HashMap<>();
            params.put("mobile_no", mobileNumber);
            params.put("calling_code", countryDataList.get(spinnerCountry.getSelectedItemPosition()).countryCode);
            params.put("android_hash_key", signatureHelper.getAppSignatures().get(0));
            params.put("registration_id", LocalStorage.getFcmToken());
            APIRequest apiRequest = new APIRequest(Url.INITIATE_OTP_VERIFICATION, Request.Method.POST, params, null, MobileVerificationActivity.this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                handleSignInResponse(response, error, OTP_INIT);
                startVerification();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method handles otp sign in
     *
     * @param response api response. Can be null
     * @param error    error message in case of error. Can be null
     */
    private void handleSignInResponse(@Nullable String response, @Nullable Exception error, int actionType) {
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    Log.e("response", response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse
                                .getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        //showError(message);
                        showErrorDialog(message);

                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObjectMsg = jsonObjectResponse.getJSONObject("msg");

                        String message = jsonObjectMsg.has("message")
                                ? jsonObjectMsg.getString("message") : "";

                        switch (actionType) {
                            case OTP_INIT:
                                showOtpView(true);
                                isOtpInitialized = true;
                                if (!message.toLowerCase().contains("otp")) {
                                    AlertUtils.showAlert("", message, null,
                                            this, null);
                                } else {
                                    //AlertUtils.showToast(message, 1, this);
                                    Log.d("OtP init Message:", message);
                                }
                                break;

                            case OTP_SIGN_IN:
                                User user = LocalStorage.getUserData();
                                user.mobile = etMobileNumber.getText().toString();
                                LocalStorage.setUserData(user);
                                LocalStorage.setLoggedInStatus(true, false);
                                if (newUser == 1 && isNewUser == 1) {
                                    if (LocalStorage.getValue(LocalStorage.KEY_LANG_PREFER, 0, Integer.class) == 0) {
                                        Intent i = new Intent(this, CoinsActivity.class);
                                        LocalStorage.putValue(0, LocalStorage.KEY_LANG_PREFER);
                                        startActivityForResult(i, LoginActivity.REQUEST_REFERRAL);
                                    }
                                } else if (newUser == 0 && isNewUser == 1) {
                                    // Show on-boarding coins earned screen
                                    if (LocalStorage.getValue(LocalStorage.KEY_LANG_PREFER, 0, Integer.class) == 0) {
                                        Intent i = new Intent(this, CoinsActivity.class);
                                        LocalStorage.putValue(0, LocalStorage.KEY_LANG_PREFER);
                                        startActivityForResult(i, REQUEST_BONUS);
                                    }
                                } else if (newUser == 0 && isNewUser == 0) {
                                    startActivity(new Intent(MobileVerificationActivity.this,
                                            HomeActivity.class).setAction(getString(R.string.no_action)).putExtra(Constant.TYPE, type).putExtra(Constant.POSTID, postID));
                                    finish();
                                }
                                break;
                        }
                    } else {
                        AlertUtils.showAlert(getString(R.string.something_went_wrong), "", null, this, null);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static final String SMS_RETRIEVER_TAG = "SmsRetriever";
    private SmsRetrieverClient smsRetrieverClient;

    private void setupSmsRetriever() {
        smsRetrieverClient = SmsRetriever.getClient(this);
    }

    private void startVerification() {
        Task<Void> task = smsRetrieverClient.startSmsRetriever();
        task.addOnSuccessListener(aVoid -> {
            Log.i(SMS_RETRIEVER_TAG, "Success");
        });
        task.addOnFailureListener(e -> {
            Log.e(SMS_RETRIEVER_TAG, "Failed to start. Error: " + e.getMessage());
        });
    }

    /**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     */
    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, this, null);
    }

    private void showErrorDialog(@Nullable String error) {
        LayoutInflater inflater = (LayoutInflater) MobileVerificationActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_popup, null);

        TextView tv_offer_message = view.findViewById(R.id.tv_message);
        Button bt_positive = view.findViewById(R.id.bt_positive);
        Button bt_negative = view.findViewById(R.id.bt_negative);

        tv_offer_message.setText(error);

        Dialog alertDialogBuilder = new Dialog(MobileVerificationActivity.this);
        alertDialogBuilder.setContentView(view);
        alertDialogBuilder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialogBuilder.show();

        if (LocalStorage.isUserLoggedIn()) {
            bt_negative.setVisibility(View.VISIBLE);
        } else {
            bt_negative.setVisibility(View.GONE);
        }

        bt_negative.setOnClickListener(v -> {
            LocalStorage.logout();
            startActivity(new Intent(this, LoginActivity.class).setAction(getString(R.string.action_splash_login)));
            alertDialogBuilder.dismiss();
            finish();
        });

        bt_positive.setOnClickListener(v -> {
            alertDialogBuilder.dismiss();
        });
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        for (int i = 0; i < etOtpDigits.length; i++) {
            if (etOtpDigits[i].getText().hashCode() == editable.hashCode()) {
                // If Block - After entering digit in currently focused EditText cursor will move to next EditText
                if (editable.length() > 0 && i < etOtpDigits.length - 1) {
                    etOtpDigits[i].clearFocus();
                    etOtpDigits[i + 1].requestFocus();
                    etOtpDigits[i + 1].setCursorVisible(true);
                }

                // Else Block - After removing digit in currently focused EditText cursor will move to previous EditText
                else if (editable.length() == 0 && (i > 0 && i <= etOtpDigits.length - 1)) {
                    etOtpDigits[i].clearFocus();
                    etOtpDigits[i - 1].requestFocus();
                    etOtpDigits[i - 1].setCursorVisible(true);
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btSendOtp:
            case R.id.tvResendOtp:
                // Call API to initialize process of OTP
                SendOtp();
                break;

            case R.id.btLetsGo:
                // Call API to verify mobile with otp
                otpVerificationInAPI();
                break;
        }
    }

    private void otpVerificationInAPI() {
        try {
            String mobileNumber = etMobileNumber.getText().toString();
            if (mobileNumber.isEmpty()) {
                showError(getString(R.string.error_msg_enter_mobile_no));
                return;
            }

            String otp = getOtp();
            if (otp.isEmpty()) {
                showError(getString(R.string.enter_the_otp_received));
                return;
            }

            if (!Utility.isValidMobileNumber(mobileNumber, Integer.parseInt(countryDataList.get(spinnerCountry.getSelectedItemPosition()).numberLimit))) {
                showError(getString(R.string.please_enter_a_valid_mobile_number));
                return;
            }

            Map<String, String> params = new HashMap<>();
            params.put("mobile_no", mobileNumber);
            params.put("calling_code", countryDataList.get(spinnerCountry.getSelectedItemPosition()).countryCode);
            params.put("otp", otp);
            params.put("registration_id", LocalStorage.getFcmToken());

            APIRequest apiRequest = new APIRequest(Url.VERIFY_MOBILE_NUMBER, Request.Method.POST, params,
                    null, MobileVerificationActivity.this);
            apiRequest.showLoader = true;

            APIManager.request(apiRequest, (response, error, headers, statusCode) ->
                    handleSignInResponse(response, error, OTP_SIGN_IN));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {

    }


    /**
     * This method appends all otp digits
     *
     * @return otp number by appending all entered digits
     */
    private String getOtp() {
        StringBuilder sbOtp = new StringBuilder();
        // LinearLayout llOtpDigits = findViewById(R.id.llOtpDigits);
        for (int i = 0; i < TOTAL_OTP_DIGITS; i++) {
            EditText etOtp = (EditText) llOtpDigits.getChildAt(i);
            String otpDigit = etOtp.getText().toString();
            sbOtp.append(otpDigit);
        }
        return sbOtp.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginActivity.REQUEST_REFERRAL) {
            startActivityForResult(new Intent(MobileVerificationActivity.this, ReferCodeActivity.class), LoginActivity.REQUEST_GIVEAWAY);
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        }
        if (requestCode == LoginActivity.REQUEST_GIVEAWAY) {
            Intent i = new Intent(this, GiveAwayActivity.class);
            startActivityForResult(i, LoginActivity.REQUEST_CONTEST_WINNERS);
        }

        if (requestCode == LoginActivity.REQUEST_CONTEST_WINNERS) {
            Intent i = new Intent(this, PastContestWinners.class);
            langPrefs();
        }

        if (requestCode == LoginActivity.REQUEST_LANGUAGE_PREFS) {
            startActivityForResult(new Intent(MobileVerificationActivity.this, LanguagePrefActivity.class), 1);
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        }

        if (requestCode == LoginActivity.REQUEST_QUESTIONNAIRE) {
            startActivityForResult(new Intent(MobileVerificationActivity.this, QuestionnaireActivity.class), 1);
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        }

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getExtras().getInt("USER_REFER");
                if (result == 0) {
                    langPrefs();
                } else if (result == 1) {
                    Intent i = new Intent(this, ReferralCoinsActivity.class);
                    LocalStorage.putValue(0, LocalStorage.KEY_LANG_PREFER);

                    startActivityForResult(i, LoginActivity.REQUEST_PERMISSION_REFERRAL);
                }
            }
        }
    }

    public void langPrefs() {
        int languagePref = LocalStorage.getValue(
                LocalStorage.KEY_LANG_PREFER, 0, Integer.class);
        startActivity(new Intent(MobileVerificationActivity.this,
                HomeActivity.class).setAction(getString(R.string.no_action)).putExtra(Constant.TYPE, type).putExtra(Constant.POSTID, postID));
        finish();
    }

    /**
     * This method set all otp digits
     *
     * @param strOtp : Otp number
     */
    private void setOtp(String strOtp) {
        char[] stringToCharArray = strOtp.toCharArray();
        for (int i = 0; i < TOTAL_OTP_DIGITS; i++) {
            EditText etOtp = (EditText) llOtpDigits.getChildAt(i);
            etOtp.setText(Character.toString(stringToCharArray[i]));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        filter = new IntentFilter(Constant.OTP_MESSAGE);
        registerReceiver(otpMessageObserver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(otpMessageObserver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!LocalStorage.isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class).setAction(getString(R.string.action_splash_login)));
            finish();
        }
    }

    private void getCountryData() {
        if (LocalStorage.getCountriesList(LocalStorage.KEY_COUNTRIES_LIST, MobileVerificationActivity.this) != null) {
            countryDataList.addAll(LocalStorage.getCountriesList(LocalStorage.KEY_COUNTRIES_LIST, MobileVerificationActivity.this));
            for (int i = 0; i < countryDataList.size(); i++) {
                if (countryDataList.get(i).countryCode.contains("91")) {
                    spinnerCountry.setSelection(i);
                }
            }
            countryAdapter.notifyDataSetChanged();
        }
    }
}
