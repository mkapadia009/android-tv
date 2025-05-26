package com.app.itaptv.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIMethods;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.BuildConfig;
import com.app.itaptv.LocaleHelper;
import com.app.itaptv.MyApp;
import com.app.itaptv.R;
import com.app.itaptv.adapter.CountryAdapter;
import com.app.itaptv.structure.CountryData;
import com.app.itaptv.structure.LanguagesData;
import com.app.itaptv.structure.SocialLoginData;
import com.app.itaptv.structure.User;
import com.app.itaptv.trillbit.CheckoutActivity;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.AppSignatureHelper;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.CustomClickableSpan;
import com.app.itaptv.utils.FirebaseAnalyticsLogs;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.user.UserService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.app.itaptv.utils.Constant.APP_MAINTENANCE;
import static com.app.itaptv.utils.Constant.KEY_APP_URL;
import static com.app.itaptv.utils.Constant.KEY_DESCRIPTION;
import static com.app.itaptv.utils.Constant.KEY_FCM_TOKEN;
import static com.app.itaptv.utils.Constant.KEY_IMAGE;
import static com.app.itaptv.utils.Constant.KEY_INFO_TYPE;
import static com.app.itaptv.utils.Constant.KEY_TITLE;
import static com.app.itaptv.utils.Constant.MAINTENANCE_GENERAL;
import static com.app.itaptv.utils.Constant.MAINTENANCE_SYSTEM_DOWN;
import static com.app.itaptv.utils.LocalStorage.STATUS_ACTIVE;

/**
 * Note: For Facebook App Registration we have used the same package name as previous iTap App i.e.
 * com.infini.itap (Since it has been mentioned that we can use same package name for multiple apps.
 * Package name does not matter for simple facebook sign in process) So we have simply added hash
 * key for this app
 */

/*@RequiresApi(api = Build.VERSION_CODES.P)*/
/*@RegisterPermission(permissions = {Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS})*/
public class LoginActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, TextWatcher, View.OnKeyListener {

    private static final int OTP_INIT = 1;
    private static final int OTP_SIGN_IN = 2;
    private static final int TOTAL_OTP_DIGITS = 6;
    private static final int MY_PERMISSIONS_REQUEST_SMS = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    public static final int REQUEST_PERMISSION_REFERRAL = 108;
    public static final int REQUEST_REFERRAL = 109;
    public static final int REQUEST_REFERRAL_COINS_RECEIVED = 110;
    public static final int REQUEST_GIVEAWAY = 111;
    public static final int REQUEST_CONTEST_WINNERS = 112;
    public static final int REQUEST_QUESTIONNAIRE = 113;
    public static final int REQUEST_LANGUAGE_PREFS = 114;
    public static final int REQUEST_BONUS = 115;
    public static final int REQUEST_WALLET = 116;
    LinearLayout llOtp;
    LinearLayout llOtpDigits;
    LinearLayout llSpinner;
    LinearLayout llCountry;
    ConstraintLayout clOffer;
    EditText etOtpDig1;
    EditText etOtp;
    TextView etCountry;
    Context mContext = LoginActivity.this;
    public Boolean callSettings = true;
    private Button checkout;

    private AppUpdateManager appUpdateManager;
    private int REQUEST_CODE_UPDATE = 898;

    String postType = "";
    String postID = "";
    int countryPos = 0;
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
    TextView tvResendOtp;
    Spinner spinnerCountry;
    EditText etMobileNumber;
    Button btSendOtp;
    Button btLetsGo;
    CheckBox cbTermsNPolicy;
    IntentFilter filter;
    TextView tvLanguage;
    TextView tvRegisterCoins;
    boolean isOtpInitialized = false;
    String mobileNumber = "";
    EditText[] etOtpDigits = new EditText[TOTAL_OTP_DIGITS];
    private Dialog alertDialogTermsNPolicies;

    CountryAdapter countryAdapter;
    List<CountryData> countryDataList = new ArrayList<>();

    private int RC_SIGN_IN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        LocalStorage.setLoginScreenShown(true);
        init();
        if (LocalStorage.getIsFirstInstall(LoginActivity.this) && !LocalStorage.getTermsNPoliciesText().isEmpty()) {
            showTermsandConditionsDialog();
        }
        //setupSmsRetriever();
        //showGoogleKeyHash();
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        clOffer.setVisibility(View.GONE);

        View view;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.activity_login, null);
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.hasFocus()) {
                    view.setBackground(getDrawable(R.drawable.highlight_text_field));
                } else {
                    view.setBackground(null);
                }
            }
        });

        MyApp application = (MyApp) getApplication();
        application.isShowNotification = true;

        String deviceId = Settings.Secure.getString(MyApp.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        storeDownload(deviceId);

        if (getIntent() != null) {
            postType = getIntent().getStringExtra(Constant.TYPE); // you will get the value "value1" from application 1
            postID = getIntent().getStringExtra(Constant.POSTID);
        }

        //View view=findViewById(R.layout.activity_login);

    }

    private void showGoogleKeyHash() {
        AppSignatureHelper signatureHelper = new AppSignatureHelper(LoginActivity.this);
        Log.v("RespectiveStore Key HAsh:", signatureHelper.getAppSignatures().get(0));
        Toast.makeText(signatureHelper, signatureHelper.getAppSignatures().get(0), Toast.LENGTH_SHORT).show();
    }

    private void getFirebaseInstanceId() {
        if (Utility.isConnectingToInternet(LoginActivity.this)) {
            try {
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(LoginActivity.this, instanceIdResult -> {
                    String newToken = instanceIdResult.getResult();
                    LocalStorage.putValue(KEY_FCM_TOKEN, newToken);
                    Log.e("newToken", newToken);
                });
            } catch (Exception e) {

            }
        }
    }

    private void storeDownload(String deviceId) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("device_id", deviceId);
            APIRequest apiRequest = new APIRequest(Url.STORE_DOWNLOAD_DETAILS, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                try {
                    if (error != null) {
                        //AlertUtils.showToast(error.getMessage(), 1, this);
                    } else {
                        if (response != null) {
                            //Log.e("response", response);
                            JSONObject jsonObjectResponse = new JSONObject(response);
                            String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                            if (type.equalsIgnoreCase("error")) {
                                String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                //AlertUtils.showToast(message, 1, this);
                            } else if (type.equalsIgnoreCase("ok")) {
                                // updated successfully
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalStorage.setLoginScreenShown(false);
        hideKeyboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (callSettings) {
            globalSettingApiCall();
        }
        APIMethods.getGlobalSetting(LoginActivity.this, new APIMethods.GlobalSettingCallBack() {
            @Override
            public void settingResponse(JSONObject msg) {
                tvRegisterCoins.setText(LocalStorage.getRegisterBonus().toString() + " iCoins");
            }
        });


        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // If an in-app update is already running, resume the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, REQUEST_CODE_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideKeyboard();
    }

  /*  private static final String SMS_RETRIEVER_TAG = "SmsRetriever";
    private SmsRetrieverClient smsRetrieverClient;*/

   /* private void setupSmsRetriever() {
        smsRetrieverClient = SmsRetriever.getClient(this);
    }*/

   /* private void startVerification() {
        Task<Void> task = smsRetrieverClient.startSmsRetriever();
        task.addOnSuccessListener(aVoid -> {
            Log.i(SMS_RETRIEVER_TAG, "Success");
        });
        task.addOnFailureListener(e -> {
            Log.e(SMS_RETRIEVER_TAG, "Failed to start. Error: " + e.getMessage());
        });
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(otpMessageObserver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Initialize data
     */
    private void init() {
        llOtp = findViewById(R.id.llOtp);
        llOtpDigits = findViewById(R.id.llOtpDigits);
        llSpinner = findViewById(R.id.llSpinner);
        clOffer = findViewById(R.id.clOffer);
        etOtpDig1 = findViewById(R.id.etOtpDig1);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        btLetsGo = findViewById(R.id.btLetsGo);
        btSendOtp = findViewById(R.id.btSendOtp);
        cbTermsNPolicy = findViewById(R.id.cbTermsNPolicy);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvRegisterCoins = findViewById(R.id.tvRegisterCoins);

        btLetsGo.setOnClickListener(this);
        btSendOtp.setOnClickListener(this);
        tvResendOtp.setOnClickListener(this);

        tvRegisterCoins.setText(LocalStorage.getRegisterBonus().toString() + " iCoins");

        if (Utility.isTelevision()) {
            etOtp = findViewById(R.id.etOtp);
            llCountry = findViewById(R.id.llCountry);
            etCountry = findViewById(R.id.etCountry);
            Utility.buttonFocusListener(btSendOtp);
            Utility.buttonFocusListener(btLetsGo);
            etMobileNumber.setBackground(getDrawable(R.drawable.highlight_selector));

            etMobileNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (view.hasFocus()) {

                    } else {
                        hideKeyboard();
                    }
                }
            });

            etOtp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (view.hasFocus()) {

                    } else {
                        hideKeyboard();
                    }
                }
            });

            etCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (view.hasFocus()) {
                        llCountry.setBackground(getDrawable(R.drawable.highlight_text_field));
                    } else {
                        llCountry.setBackground(getDrawable(R.drawable.bg_tab_gray_color));
                    }
                }
            });
        }
        // Set font to checkbox
        cbTermsNPolicy.setTypeface(ResourcesCompat.getFont(this, R.font.rubik_medium));
        showOtpView(false);
        setClickableText();
        initOtpEditText();

        Intent i = getIntent();
        if (Objects.requireNonNull(i.getAction()).equalsIgnoreCase(getString(R.string.action_token_expire))) {
            showTokenExpiredDialog();
        }

        checkout = findViewById(R.id.btnCheckout);
        checkout.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,
                CheckoutActivity.class)));
        etMobileNumber.requestFocus();
        etMobileNumber.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                    (actionId == EditorInfo.IME_ACTION_DONE)) {
                SendOtp();
            }
            return false;
        });

        spinnerCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.hasFocus()) {
                    llSpinner.setBackground(getDrawable(R.drawable.highlight_text_field));
                } else {
                    llSpinner.setBackground(null);
                }
            }
        });

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                etMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(countryDataList.get(position).numberLimit))});
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        updateCheck();

        List<CountryData> countryData = LocalStorage.getCountriesList(LocalStorage.KEY_COUNTRIES_LIST, LoginActivity.this);
        if (countryData!=null) {
            countryDataList.addAll(countryData);
        }
        countryAdapter = new CountryAdapter(LoginActivity.this, countryDataList);
        spinnerCountry.setAdapter(countryAdapter);
        getCountryData();
        getLanguagesData();
    }


    public void updateCheck() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, REQUEST_CODE_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Api calls to get global settings to show and hide presenterLogin
     **/
    public void globalSettingApiCall() {
        //Button btPresenterLogin = findViewById(R.id.btPresenterLogin);
        APIMethods.getGlobalSetting(mContext, (msg) -> {
            Log.e("Result", msg.toString());
            //btPresenterLogin.setVisibility(LocalStorage.getLiveModuleStatus() ? View.VISIBLE : View.INVISIBLE);
            try {
                String title = msg.has("title") ? msg.getString("title") : null;
                String desc = msg.has("description") ? msg.getString("description") : null;
                String image = msg.has("image") ? msg.getString("image") : null;
                String type_of_maintenance = msg.has("type_of_maintenance") ? msg.getString("type_of_maintenance") : null;
                String url = msg.has("url") ? msg.getString("url") : null;
                String isMaintenance = msg.has("is_maintenance") ? msg.getString("is_maintenance") : null;
                if (msg.has("version")) {
                    int version = msg.getInt("version");
                    Intent intent = new Intent(this, AppInfoActivity.class);
                    intent.putExtra(KEY_TITLE, title);
                    intent.putExtra(KEY_DESCRIPTION, desc);
                    intent.putExtra(KEY_IMAGE, image);
                    intent.putExtra(KEY_INFO_TYPE, type_of_maintenance);
                    intent.putExtra(KEY_APP_URL, url);
                    if (version > BuildConfig.VERSION_CODE || type_of_maintenance.equals(MAINTENANCE_SYSTEM_DOWN)) {
                        startActivity(intent);
                        callSettings = true;
                    }
                    if (type_of_maintenance.equals(MAINTENANCE_GENERAL)) {
                        startActivity(intent);
                        callSettings = false;
                    }

                    if (type_of_maintenance.equalsIgnoreCase(APP_MAINTENANCE)) {
                        if (isMaintenance != null) {
                            if (isMaintenance.equalsIgnoreCase("yes")) {
                                startActivity(new Intent(this, MaintenanceActivity.class).putExtra("Message", desc));
                                finish();
                                callSettings = false;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //btPresenterLogin.setVisibility(View.INVISIBLE);
    }

    private boolean isCheckBoxChecked() {
        return cbTermsNPolicy.isChecked();
    }

    public void presenterLogin(View view) {
        //startActivity(new Intent(this, LoginPresenterActivity.class));
        Toast.makeText(this, getString(R.string.feature_disabled), Toast.LENGTH_SHORT).show();
    }

    /**
     * Dynamically initialize Edit fields of Otp and store each edit text object in EditText Array
     */
    private void initOtpEditText() {
        for (int i = 0; i < etOtpDigits.length; i++) {
            EditText etDigit = (EditText) llOtpDigits.getChildAt(i);
            etOtpDigits[i] = etDigit;
            etDigit.addTextChangedListener(this);
            etDigit.setOnKeyListener(this);
        }
    }

    private void setClickableText() {
        SpannableString spannableString = new SpannableString(getString(R.string.label_terms_of_use));
        SpannableString spannableString1 = new SpannableString(getString(R.string.privacy_policy));
        spannableString.setSpan(new CustomClickableSpan(this, 0), 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString1.setSpan(new CustomClickableSpan(this, 1), 0, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView tvCheckTerms = findViewById(R.id.tvCheckTerms);
        TextView tvCheckPolicy = findViewById(R.id.tvCheckPolicy);
        tvCheckTerms.setText(spannableString);
        tvCheckTerms.setMovementMethod(LinkMovementMethod.getInstance());
        tvCheckTerms.setHighlightColor(Color.TRANSPARENT);
        tvCheckPolicy.setText(spannableString1);
        tvCheckPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        tvCheckPolicy.setHighlightColor(Color.TRANSPARENT);
    }

    private void showOtpView(boolean apiSuccess) {
        if (apiSuccess) {
            if (Utility.isTelevision()) {
                llOtp.setVisibility(View.VISIBLE);
                tvResendOtp.setVisibility(View.VISIBLE);
                btSendOtp.setVisibility(View.GONE);
                llOtpDigits.setVisibility(View.GONE);
                btLetsGo.setVisibility(View.VISIBLE);
                etOtp.requestFocus();
                etOtp.setOnEditorActionListener((v, actionId, event) -> {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                            (actionId == EditorInfo.IME_ACTION_DONE)) {
                        btLetsGo.callOnClick();
                    }
                    return false;
                });
            } else {
                llOtp.setVisibility(View.VISIBLE);
                tvResendOtp.setVisibility(View.VISIBLE);
                btSendOtp.setVisibility(View.GONE);
                btLetsGo.setVisibility(View.VISIBLE);
                etOtpDig1.requestFocus();
            }
            //btLetsGo.setText(getString(R.string.label_lets_go));
            return;
        } else {
            llOtp.setVisibility(View.GONE);
            tvResendOtp.setVisibility(View.GONE);
            btSendOtp.setVisibility(View.VISIBLE);
            btLetsGo.setVisibility(View.GONE);
            //btLetsGo.setText(getString(R.string.label_send_otp));
        }
    }

    /**
     * Initialize process of receiving OTP through SMS
     */
    private void initiateOtpSignInAPI() {

        AppSignatureHelper signatureHelper = new AppSignatureHelper(LoginActivity.this);
        Log.v("RespectiveStore Key HAsh:", signatureHelper.getAppSignatures().get(0));
        //Toast.makeText(signatureHelper, signatureHelper.getAppSignatures().get(0), Toast.LENGTH_SHORT).show();
        FirebaseAnalyticsLogs.startDurationOtp();
        FirebaseAnalyticsLogs.startDurationRegister();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("mobile_no", mobileNumber);
            if (Utility.isTelevision()) {
                params.put("calling_code", countryDataList.get(countryPos).countryCode);
                params.put("country_id", String.valueOf(countryDataList.get(countryPos).id));
            } else {
                params.put("calling_code", countryDataList.get(spinnerCountry.getSelectedItemPosition()).countryCode);
            }
            params.put("android_hash_key", signatureHelper.getAppSignatures().get(0));
            params.put("registration_id", LocalStorage.getFcmToken());
            APIRequest apiRequest = new APIRequest(Url.INITIATE_OTP_SIGNIN, Request.Method.POST, params, null, LoginActivity.this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                handleSignInResponse(response, error, OTP_INIT);
                FirebaseAnalyticsLogs.stopDurationOtp(mobileNumber);
                //startVerification();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sign in with otp on successful sign in you will get an auth_token. You should provide this
     * token at headers X-AUTH-TOKEN in order to validate user.
     */
    private void otpSignInAPI() {
        try {

            String mobileNumber = etMobileNumber.getText().toString();
            if (mobileNumber.isEmpty()) {
                showError(getString(R.string.error_msg_enter_mobile_no));
                return;
            }
            String otp = "";
            if (Utility.isTelevision()) {
                if (etCountry.getText().toString().isEmpty()) {
                    showError(getString(R.string.please_enter_country_code));
                    return;
                }
                otp = etOtp.getText().toString();
                if (etCountry.getText().toString().isEmpty()) {
                    showError(getString(R.string.please_enter_country_code));
                    return;
                }
            } else {
                otp = getOtp();
            }
            if (otp.isEmpty()) {
                showError(getString(R.string.enter_the_otp_received));
                return;
            }

            Map<String, String> params = new HashMap<>();
            params.put("mobile_no", mobileNumber);
            if (Utility.isTelevision()) {
                params.put("calling_code", countryDataList.get(countryPos).countryCode);
                params.put("country_id", String.valueOf(countryDataList.get(countryPos).id));
            } else {
                params.put("calling_code", countryDataList.get(spinnerCountry.getSelectedItemPosition()).countryCode);
            }
            params.put("otp", otp);
            params.put("registration_id", LocalStorage.getFcmToken());

            APIRequest apiRequest = new APIRequest(Url.OTP_SIGNIN, Request.Method.POST, params,
                    null, LoginActivity.this);
            apiRequest.showLoader = true;

            APIManager.request(apiRequest, (response, error, headers, statusCode) ->
                    handleSignInResponse(response, error, OTP_SIGN_IN));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Call Login api of App42 SDK to be able to use their services for coin transactions within
     * app
     */
    private void app42Login() {
        String userId = LocalStorage.getValue(LocalStorage.KEY_USER_ID, "", String.class);
        int languagePref = LocalStorage.getValue(LocalStorage.KEY_LANG_PREFER, 0, Integer.class);
        UserService userService = App42API.buildUserService();
        String appWarpUserSessionId = App42API.getUserSessionId();

        if (appWarpUserSessionId == null || appWarpUserSessionId.isEmpty()) {
            userService.authenticate("inspeerodev@gmail.com", "itap", new App42CallBack() {
                @Override
                public void onSuccess(Object o) {
                    com.app.itaptv.utils.Log.i("App42", "Login successful");
                    if (languagePref == 0) {
                        startActivity(new Intent(LoginActivity.this, LanguagePrefActivity.class));
                        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
                    } else {
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class).setAction(getString(R.string.no_action)).putExtra(Constant.TYPE, postType).putExtra(Constant.POSTID, postID));
                    }
                    finish();
                }

                @Override
                public void onException(Exception e) {
                    com.app.itaptv.utils.Log.e("App42", "Login error: " + e.getMessage());
                }
            });
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
                        showError(message);

                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObjectMsg = jsonObjectResponse.getJSONObject("msg");
                        String authToken = jsonObjectMsg.has("auth_token") ?
                                jsonObjectMsg.getString("auth_token") : "";

                        String userId = jsonObjectMsg.has("user_id") ?
                                jsonObjectMsg.getString("user_id") : "";

                        String message = jsonObjectMsg.has("message")
                                ? jsonObjectMsg.getString("message") : "";

                        int lang_prefs = jsonObjectMsg.has("lang_prefs")
                                ? jsonObjectMsg.getJSONArray("lang_prefs").length() : 0;

                        User user = new User(jsonObjectMsg.has("user")
                                ? jsonObjectMsg.getJSONObject("user") : new JSONObject());

                        LocalStorage.setUserData(user);

                        switch (actionType) {
                            case OTP_INIT:
                                showOtpView(true);
                                isOtpInitialized = true;
                                //  String message = jsonObjectMsg.has("message") ? jsonObjectMsg.getString("message") : "";
                                if (!message.toLowerCase().contains("otp")) {
                                    AlertUtils.showAlert("", message, null,
                                            this, null);
                                } else {
                                    //AlertUtils.showToast(message, 1, this);
                                    Log.d("OtP init Message:", message);
                                }
                                break;

                            case OTP_SIGN_IN:
                            case -1:
                                isOtpInitialized = false;

                                LocalStorage.putValue(authToken, LocalStorage.KEY_AUTH_TOKEN);
                                LocalStorage.setUserId(userId);
                                LocalStorage.putValue(lang_prefs, LocalStorage.KEY_LANG_PREFER);

                                //TODO: Calling User Active Subscription API
                                callSubscriptionApi(userId);
                                JSONObject jsonObjectMsg1 = jsonObjectResponse.getJSONObject("msg");
                                int newUser = 0;
                                if (jsonObjectMsg1.has("new_user")) {
                                    newUser = jsonObjectMsg1.getInt("new_user");
                                }

                                int isNewUser = 0;
                                if (jsonObjectMsg1.has("is_new_user")) {
                                    isNewUser = jsonObjectMsg1.getInt("is_new_user");
                                }

                                if (user.mobile.isEmpty()) {
                                    Intent intent = new Intent(LoginActivity.this, MobileVerificationActivity.class);
                                    intent.putExtra("newuser", newUser);
                                    intent.putExtra("isnewuser", isNewUser);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    LocalStorage.setLoggedInStatus(true, false);
                                    //app42Login();


                              /* int languagePref = LocalStorage.getValue(
                                        LocalStorage.KEY_LANG_PREFER, 0, Integer.class);
                                if (languagePref == 0) {
                                    startActivity(new Intent(LoginActivity.this,
                                            LanguagePrefActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(LoginActivity.this,
                                            HomeActivity.class).setAction(getString(R.string.no_action)));
                                    finish();
                                }*/
                                    if (newUser == 1 && isNewUser == 1) {
                                        // Show on-boarding coins earned screen
                                        if (LocalStorage.getValue(LocalStorage.KEY_LANG_PREFER, 0, Integer.class) == 0) {
                                            Intent i = new Intent(this, CoinsActivity.class);
                                            LocalStorage.putValue(0, LocalStorage.KEY_LANG_PREFER);
                                            startActivityForResult(i, REQUEST_REFERRAL);
                                            //startActivityForResult(i, REQUEST_GIVEAWAY);
                                        }
                                    /*startActivityForResult(new Intent(LoginActivity.this, ReferCodeActivity.class), REQUEST_PERMISSION_REFERRAL);
                                    overridePendingTransition(R.anim.slide_in_up, R.anim.stay);*/
                                    } else if (newUser == 0 && isNewUser == 1) {
                                        // Show on-boarding coins earned screen
                                        if (LocalStorage.getValue(LocalStorage.KEY_LANG_PREFER, 0, Integer.class) == 0) {
                                            Intent i = new Intent(this, CoinsActivity.class);
                                            LocalStorage.putValue(0, LocalStorage.KEY_LANG_PREFER);
                                            startActivityForResult(i, REQUEST_BONUS);
                                        }
                                    } else if (newUser == 0 && isNewUser == 0) {
                                        int languagePref = LocalStorage.getValue(
                                                LocalStorage.KEY_LANG_PREFER, 0, Integer.class);
                                    /*if (languagePref == 0) {
                                        startActivity(new Intent(LoginActivity.this,
                                                LanguagePrefActivity.class));
                                        finish();
                                    } else {*/
                                        FirebaseAnalyticsLogs.stopDurationRegister();
                                        startActivity(new Intent(LoginActivity.this,
                                                HomeActivity.class).setAction(getString(R.string.no_action)).putExtra(Constant.TYPE, postType).putExtra(Constant.POSTID, postID));
                                        finish();
                                        //}
                                    }
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

    public void callSubscriptionApi(String userId) {

        try {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", userId);
            APIRequest apiRequest = new APIRequest(Url.CHECK_ACTIVE_SUBSCRIPTION, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    JSONObject jsonObjectResponse = null;
                    try {
                        jsonObjectResponse = new JSONObject(response);
                        String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                        if (type.equalsIgnoreCase("error")) {
                            LocalStorage.setUserPremium(false);
                            String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        } else if (type.equalsIgnoreCase("ok")) {
                            JSONObject okResponse = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                            if (okResponse.getString("status").equals(STATUS_ACTIVE)) {
                                LocalStorage.setUserPremium(true);
                                LocalStorage.setSubStartDate(okResponse.getString("start_date"));
                                LocalStorage.setSubEndDate(okResponse.getString("expiry_date"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * This method returns the request parameters to pass to the Social Signin API in Map format
     *
     * @param socialLoginData user data in JSON format
     * @param accessToken     user access token
     * @return Map which contains all request parameters
     */
    private Map<String, String> getSignInParams(SocialLoginData socialLoginData, String accessToken) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", socialLoginData.id);
        params.put("first_name", socialLoginData.firstName);
        params.put("last_name", socialLoginData.lastName);
        params.put("user_email", socialLoginData.email);
        params.put("imageUrl", socialLoginData.profileImage);
        params.put("token", accessToken);
        params.put("source", socialLoginData.loginSource.getValue());
        params.put("registration_id", LocalStorage.getFcmToken());

        Log.e("PARAMS", params.toString());

        return params;
    }

    //<#> Your ExampleApp code is: 1234
    // sKkq3SV6LjC

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

    /**
     * This method gets called to display token expired message dialog
     */
    private void showTokenExpiredDialog() {
        AlertUtils.showAlert(getString(R.string.token_expired), getString(R.string.your_token_has_been_expired),
                null, this, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSendOtp:
            case R.id.tvResendOtp:
                // Call API to initialize process of OTP
                SendOtp();
                break;

            case R.id.btLetsGo:
                // Call API to Sign In
                if (!isCheckBoxChecked()) {
                    showError(getString(R.string.accept_terms_of_use_and_privacy_policy));
                    return;
                }
                otpSignInAPI();
                break;
        }
    }

    private void SendOtp() {
        mobileNumber = etMobileNumber.getText().toString();
        if (mobileNumber.equals("")) {
            showError(getString(R.string.error_msg_enter_mobile_no));
            return;
        }

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                    MY_PERMISSIONS_REQUEST_SMS);
        } else {*/
        if (Utility.isTelevision()) {
            if (etCountry != null) {
                if (etCountry.getText().toString().isEmpty()) {
                    showError(getString(R.string.please_enter_country_code));
                    return;
                }
                String country = etCountry.getText().toString();
                boolean isValid = false;
                for (int i = 0; i < countryDataList.size(); i++) {
                    if (country.equals(countryDataList.get(i).countryCode)) {
                        isValid = true;
                        if (!Utility.isValidMobileNumber(mobileNumber, Integer.parseInt(countryDataList.get(i).numberLimit))) {
                            showError(getString(R.string.please_enter_a_valid_mobile_number));
                            return;
                        }
                    }
                }
                if (!isValid) {
                    showError(getString(R.string.country_code_error_message));
                    return;
                }
            }
        } else {
            if (countryDataList.size() > spinnerCountry.getSelectedItemPosition()) {
                if (!Utility.isValidMobileNumber(mobileNumber, Integer.parseInt(countryDataList.get(spinnerCountry.getSelectedItemPosition()).numberLimit))) {
                    showError(getString(R.string.please_enter_a_valid_mobile_number));
                    return;
                }
            } else {
                showError(getString(R.string.please_enter_a_valid_mobile_number));
                return;
            }
        }

        initiateOtpSignInAPI();
        //}.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GIVEAWAY) {
            //startActivityForResult(new Intent(LoginActivity.this, GiveAwayActivity.class), 1);
            //overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
            Intent i = new Intent(this, GiveAwayActivity.class);
            startActivityForResult(i, REQUEST_CONTEST_WINNERS);
            //overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        }

        if (requestCode == REQUEST_CONTEST_WINNERS) {
            Intent i = new Intent(this, PastContestWinners.class);

            /*
             * Start Activity as required.. Current usage is language prefs.
             * Language is for setting up languages
             * Questionnaire is for normal survey
             * If not both, directly call langPrefs() so that it redirects user directly to the home page
             */
            //startActivityForResult(i, REQUEST_LANGUAGE_PREFS);
            //startActivityForResult(i, REQUEST_QUESTIONNAIRE);
            //overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
            langPrefs();
        }

        if (requestCode == REQUEST_LANGUAGE_PREFS) {
            startActivityForResult(new Intent(LoginActivity.this, LanguagePrefActivity.class), 1);
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        }

        if (requestCode == REQUEST_QUESTIONNAIRE) {
            startActivityForResult(new Intent(LoginActivity.this, QuestionnaireActivity.class), 1);
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        }

        if (requestCode == REQUEST_REFERRAL) {
            startActivityForResult(new Intent(LoginActivity.this, ReferCodeActivity.class), REQUEST_GIVEAWAY);
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        }

        if (requestCode == REQUEST_BONUS) {
            FirebaseAnalyticsLogs.stopDurationRegister();
            startActivity(new Intent(LoginActivity.this,
                    HomeActivity.class).setAction(getString(R.string.no_action)).putExtra(Constant.TYPE, postType).putExtra(Constant.POSTID, postID));
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
            finish();
        }

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getExtras().getInt("USER_REFER");
                if (result == 0) {
                    langPrefs();
                } else if (result == 1) {
                    Intent i = new Intent(this, ReferralCoinsActivity.class);
                    LocalStorage.putValue(0, LocalStorage.KEY_LANG_PREFER);

                    startActivityForResult(i, REQUEST_PERMISSION_REFERRAL);
                }
            }
        }

        if (requestCode == REQUEST_PERMISSION_REFERRAL) {
            langPrefs();
        }

        if (requestCode == REQUEST_CODE_UPDATE) {
            if (resultCode != RESULT_OK) {
                FirebaseCrashlytics.getInstance().log("Update flow failed! Result code: " + resultCode);
                // If the update is cancelled
            } else if (resultCode == RESULT_CANCELED) {
                FirebaseCrashlytics.getInstance().log("Update cancelled! Result code: " + resultCode);
            } else if (resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
                FirebaseCrashlytics.getInstance().log("Update failed! Result code: " + resultCode);

            }
        }
    }

    public void langPrefs() {
        int languagePref = LocalStorage.getValue(
                LocalStorage.KEY_LANG_PREFER, 0, Integer.class);
        /*if (languagePref == 0) {
            startActivity(new Intent(LoginActivity.this,
                    LanguagePrefActivity.class));
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        } else {*/
        startActivity(new Intent(LoginActivity.this,
                HomeActivity.class).setAction(getString(R.string.no_action)).putExtra(Constant.TYPE, postType).putExtra(Constant.POSTID, postID));
        //}
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        filter = new IntentFilter(Constant.OTP_MESSAGE);
        registerReceiver(otpMessageObserver, filter);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.i(TAG, "beforeTextChanged");
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.i(TAG, "OnTextChanged");
    }

    @Override
    public void afterTextChanged(Editable s) {
        for (int i = 0; i < etOtpDigits.length; i++) {
            if (etOtpDigits[i].getText().hashCode() == s.hashCode()) {
                // If Block - After entering digit in currently focused EditText cursor will move to next EditText
                if (s.length() > 0 && i < etOtpDigits.length - 1) {
                    etOtpDigits[i].clearFocus();
                    etOtpDigits[i + 1].requestFocus();
                    etOtpDigits[i + 1].setCursorVisible(true);
                }

                // Else Block - After removing digit in currently focused EditText cursor will move to previous EditText
                else if (s.length() == 0 && (i > 0 && i <= etOtpDigits.length - 1)) {
                    etOtpDigits[i].clearFocus();
                    etOtpDigits[i - 1].requestFocus();
                    etOtpDigits[i - 1].setCursorVisible(true);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SMS: {
                /*if (grantResults.length == 2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //initiateOtpSignInAPI();

                } else {
                    for (int i = 0; i < permissions.length; i++) {
                        boolean showRationale = shouldShowRequestPermissionRationale(permissions[i]);
                        if (!showRationale) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            break;
                        }
                    }

                }*/
                initiateOtpSignInAPI();
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN)
            return true;

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            Utility.showKeyboard(LoginActivity.this);
        }

        //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            //this is for backspace
            for (int i = 0; i < etOtpDigits.length; i++) {
                if (etOtpDigits[i].getId() == v.getId()) {
                    if (etOtpDigits[i].getText().toString().equals("")) {
                        if (i > 0) {
                            etOtpDigits[i - 1].setText("");
                            etOtpDigits[i - 1].requestFocus();
                        } else {
                            etOtpDigits[0].setText("");
                            etOtpDigits[0].requestFocus();
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < etOtpDigits.length; i++) {
                if (etOtpDigits[i].getId() == v.getId()) {
                    String str = etOtpDigits[i].getText().toString();
                    if (!etOtpDigits[i].getText().toString().isEmpty() && etOtpDigits[etOtpDigits.length - 1].getText().toString().isEmpty()) {
                        if (i < etOtpDigits.length - 1) {
                            setOtpText((i + 1), keyCode);
                            etOtpDigits[i + 1].requestFocus();
                            etOtpDigits[i + 1].setSelection(etOtpDigits[i + 1].getText().length());
                        } else {
                            setOtpText((etOtpDigits.length - 1), keyCode);
                            etOtpDigits[etOtpDigits.length - 1].requestFocus();
                            etOtpDigits[etOtpDigits.length - 1].setSelection(etOtpDigits[etOtpDigits.length - 1].getText().length());
                        }
                    }
                }
            }
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == EditorInfo.IME_ACTION_DONE) {
            btLetsGo.performClick();
        }

        return false;
    }

    void setOtpText(int pos, int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                etOtpDigits[pos].setText("0");
                break;
            case KeyEvent.KEYCODE_1:
                etOtpDigits[pos].setText("1");
                break;
            case KeyEvent.KEYCODE_2:
                etOtpDigits[pos].setText("2");
                break;
            case KeyEvent.KEYCODE_3:
                etOtpDigits[pos].setText("3");
                break;
            case KeyEvent.KEYCODE_4:
                etOtpDigits[pos].setText("4");
                break;
            case KeyEvent.KEYCODE_5:
                etOtpDigits[pos].setText("5");
                break;
            case KeyEvent.KEYCODE_6:
                etOtpDigits[pos].setText("6");
                break;
            case KeyEvent.KEYCODE_7:
                etOtpDigits[pos].setText("7");
                break;
            case KeyEvent.KEYCODE_8:
                etOtpDigits[pos].setText("8");
                break;
            case KeyEvent.KEYCODE_9:
                etOtpDigits[pos].setText("9");
                break;
        }
    }

    private void getCountryData() {
        countryPos = 0;
        if (LocalStorage.getCountriesList(LocalStorage.KEY_COUNTRIES_LIST, LoginActivity.this) != null) {
            etCountry.setVisibility(View.VISIBLE);
            ArrayList<String> countries = new ArrayList<>();
            for (int i = 0; i < countryDataList.size(); i++) {
                if (countryDataList.get(i).countryCode.equalsIgnoreCase(Constant.INDIA) && countryDataList.get(i).countryName.equalsIgnoreCase("india")) {
                    etCountry.setText(countryDataList.get(i).countryCode);
                    etMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(countryDataList.get(i).numberLimit))});
                    countryPos = i;
                }
                countries.add(countryDataList.get(i).countryName + " (" + countryDataList.get(i).countryCode + ")");
            }
            if (countryDataList.size() > 1) {
                etCountry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertUtils.showCountryDialog(LoginActivity.this, countryPos, countries, new AlertUtils.AlertCallback() {
                            @Override
                            public void alertCallback(String selectedLanguage, int pos) {
                                Log.i("country", "" + pos);
                                etCountry.setText(countryDataList.get(pos).countryCode);
                                etMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(countryDataList.get(pos).numberLimit))});
                                countryPos = pos;
                            }
                        });
                    }
                });
            }
           /* countryDataList.addAll(LocalStorage.getCountriesList(LocalStorage.KEY_COUNTRIES_LIST, LoginActivity.this));
            for (int i = 0; i < countryDataList.size(); i++) {
                if (countryDataList.get(i).countryCode.contains("91")) {
                    spinnerCountry.setSelection(i);
                }
            }
            countryAdapter.notifyDataSetChanged();*/
        }
    }

    private void getLanguagesData() {
        int pos = 0;
        if (LocalStorage.getLanguagesList(LocalStorage.KEY_LANGUAGES_LIST, LoginActivity.this) != null) {
            tvLanguage.setVisibility(View.VISIBLE);
            ArrayList<String> languages = new ArrayList<>();
            List<LanguagesData> languagesDataArrayList = LocalStorage.getLanguagesList(LocalStorage.KEY_LANGUAGES_LIST, LoginActivity.this);
            for (int i = 0; i < languagesDataArrayList.size(); i++) {
                languages.add(languagesDataArrayList.get(i).language + " " + getString(R.string.opening_bracket) + languagesDataArrayList.get(i).short_code.toUpperCase() + getString(R.string.closing_bracket));
            }
            for (int i = 0; i < languages.size(); i++) {
                if (LocalStorage.getSelectedLanguage(LoginActivity.this).equalsIgnoreCase("")) {
                    if (languagesDataArrayList.get(i).short_code.equalsIgnoreCase("en")) {
                        tvLanguage.setText(languages.get(i));
                        pos = i;
                    }
                } else {
                    if (languagesDataArrayList.get(i).short_code.equalsIgnoreCase(LocalStorage.getSelectedLanguage(LoginActivity.this))) {
                        tvLanguage.setText(languages.get(i));
                        pos = i;
                    }
                }
            }
            if (languagesDataArrayList.size() > 1) {
                int finalPos = pos;
                tvLanguage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertUtils.showAlert(LoginActivity.this, finalPos, languages, new AlertUtils.AlertCallback() {
                            @Override
                            public void alertCallback(String selectedLanguage, int pos) {
                                tvLanguage.setText(selectedLanguage);
                                chooseLanguage(languagesDataArrayList.get(pos).short_code);
                            }
                        });
                    }
                });
            } else {
                tvLanguage.setVisibility(View.GONE);
            }
        }
    }

    private void chooseLanguage(String selectedLanguage) {
        switch (selectedLanguage) {
            case "hi":
                LocaleHelper.setLocale(LoginActivity.this, "hi");
                if (getIntent() != null) {
                    finish();
                    startActivity(getIntent());
                }
                break;
            default:
                LocaleHelper.setLocale(LoginActivity.this, "en");
                if (getIntent() != null) {
                    finish();
                    startActivity(getIntent());
                }
        }
    }

    public void showTermsandConditionsDialog() {
        LayoutInflater inflater = (LayoutInflater) LoginActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_terms_policies, null);

        TextView tvTearmsNPolicies = view.findViewById(R.id.tvTermsNPolicies);
        Button bt_agree = view.findViewById(R.id.btAgree);
        Button bt_disagree = view.findViewById(R.id.btDisagree);
        Utility.focusListener(bt_agree);
        Utility.focusListener(bt_disagree);
        tvTearmsNPolicies.setText(Html.fromHtml(LocalStorage.getTermsNPoliciesText()));
        tvTearmsNPolicies.setMovementMethod(new ScrollingMovementMethod());
        bt_agree.requestFocus();

        alertDialogTermsNPolicies = new Dialog(LoginActivity.this);
        alertDialogTermsNPolicies.setContentView(view);
        alertDialogTermsNPolicies.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialogTermsNPolicies.show();
        alertDialogTermsNPolicies.setCancelable(false);

        bt_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogTermsNPolicies.dismiss();
                LocalStorage.setIsFirstInstall(false, LoginActivity.this);
                if (!Utility.isTelevision()) {
                    Utility.getLocationPermission(LoginActivity.this);
                }
            }
        });

        bt_disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogTermsNPolicies.dismiss();
                LocalStorage.setIsFirstInstall(false, LoginActivity.this);
            }
        });
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
