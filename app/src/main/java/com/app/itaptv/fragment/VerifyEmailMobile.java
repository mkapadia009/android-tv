package com.app.itaptv.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.HomeActivity;
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
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.itaptv.activity.GiveAwayActivity.GIVE_AWAY_CODE;

public class VerifyEmailMobile extends Fragment implements View.OnClickListener {

    View view;
    LinearLayout llVerifyMobile, llVerifyEmail, llbtnVerify;
    EditText edtMobile, edtEmail, edtOtp;
    TextView tvResendOtp, titleText;
    LinearLayout llVerifyBox;
    Button btnVerify, btSendOtp;
    Spinner spinnerCountry;
    String editpage = "";
    boolean isVerificationActivity = false;
    boolean isGiveAwayActivity = false;
    String mobileNumber = "";
    String email = "";
    ImageButton backButton;

    CountryAdapter countryAdapter;
    List<CountryData> countryDataList = new ArrayList<>();

    Boolean isGame = false;
    private static final int MY_PERMISSIONS_REQUEST_SMS = 100;

    private FragmentClick fragmentClick;

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
                        edtOtp.setText(otp);
                    }
                }
            }
        }
    };

    public interface FragmentClick {
        void onClick();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_verify, container, false);
        backButton = view.findViewById(R.id.iv_back);
        titleText = view.findViewById(R.id.title_text);

        editpage = getArguments().getString("edit");
        isVerificationActivity = getArguments().getBoolean("VerificationActivity");
        isGiveAwayActivity = getArguments().getBoolean("GiveAwayActivity");
        init();
        setupSmsRetriever();
        return view;
    }

    private void init() {
        if (!isVerificationActivity) {
            if (!isGame) {
                ((HomeActivity) getActivity()).setToolbar(true);
                ((HomeActivity) requireActivity()).showToolbarBackButton(R.drawable.white_arrow);
                ((HomeActivity) getActivity()).showToolbarTitle(true);
                ((HomeActivity) getActivity()).setCustomizedTitle(0);
            }
        } else {
            if (editpage.equalsIgnoreCase("mobile")) {
                titleText.setText(getString(R.string.verify_phone_number));
            } else {
                titleText.setText(getString(R.string.verify_email_address));
            }

            backButton.setOnClickListener(view -> {
                if (isVerificationActivity) {
                    getFragmentManager().popBackStack();
                    getActivity().finish();
                } else if (isGiveAwayActivity) {
                    getFragmentManager().popBackStack();
                    getActivity().setResult(GIVE_AWAY_CODE);
                    getActivity().finish();
                } else {
                    getFragmentManager().popBackStack();
                }
            });
        }

        if (!isVerificationActivity && !isGiveAwayActivity) {
            backButton.setVisibility(View.GONE);
        }

        setHasOptionsMenu(false);
        llVerifyEmail = view.findViewById(R.id.llVerifyEmail);
        llVerifyMobile = view.findViewById(R.id.llVerifyMobile);
        edtMobile = view.findViewById(R.id.etMobileNumber);
        edtEmail = view.findViewById(R.id.etemail);
        edtOtp = view.findViewById(R.id.etotp);
        btnVerify = view.findViewById(R.id.btVerify);
        llbtnVerify = view.findViewById(R.id.llbtnVerify);
        btSendOtp = view.findViewById(R.id.btSendOtp);
        tvResendOtp = view.findViewById(R.id.tvResendOtp);
        llVerifyBox = view.findViewById(R.id.llVerifyBox);
        spinnerCountry = view.findViewById(R.id.spinnerCountry);
        Utility.setShadeBackground(llbtnVerify);

        llVerifyBox.setVisibility(View.GONE);

        if (editpage.equalsIgnoreCase("mobile")) {
            llVerifyEmail.setVisibility(View.GONE);
        } else {
            llVerifyMobile.setVisibility(View.GONE);
        }

        //Validator.phoneNumberValidator(edtMobile);
        edtMobile.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                SendOtp();
            }
            return false;
        });

        edtEmail.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                SendOtp();
            }
            return false;
        });

        edtOtp.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                VerifyOtp();
            }
            return false;
        });

        btSendOtp.setOnClickListener(this);
        tvResendOtp.setOnClickListener(this);
        btnVerify.setOnClickListener(this);

        countryAdapter = new CountryAdapter(getContext(), countryDataList);
        spinnerCountry.setAdapter(countryAdapter);
        getCountryData();

    }

    private void VerifyOtp() {
        if (editpage.equalsIgnoreCase("mobile")) {
            mobileNumber = edtMobile.getText().toString().trim();
            if (mobileNumber.equals("")) {
                showError(null, getString(R.string.error_msg_enter_mobile_no));
            }if (!Utility.isValidMobileNumber(mobileNumber, Integer.parseInt(countryDataList.get(spinnerCountry.getSelectedItemPosition()).numberLimit))) {
                showError(null,getString(R.string.please_enter_a_valid_mobile_number));
            }else {
                otpVerifyAPI("mobile_no", mobileNumber);
            }
        } else {
            email = edtEmail.getText().toString().trim();
            if (email.equals("")) {
                showError(null, getString(R.string.please_enter_your_email));
            } else {
                otpVerifyAPI("user_email", email);
            }
        }
    }

    private void SendOtp() {
        if (editpage.equalsIgnoreCase("mobile")) {
            mobileNumber = edtMobile.getText().toString();
            if (mobileNumber.equals("")) {
                showError(null, getString(R.string.error_msg_enter_mobile_no));
            } else if (!Utility.isValidMobileNumber(mobileNumber,Integer.parseInt(countryDataList.get(spinnerCountry.getSelectedItemPosition()).numberLimit))) {
                showError(null, getString(R.string.please_enter_a_valid_mobile_number));

            } else {
                initiateOtpAPI("mobile_no", mobileNumber);
            }
        } else {
            email = edtEmail.getText().toString();
            if (email.equals("")) {
                showError(null, getString(R.string.please_enter_your_email));
                return;
            }
            initiateOtpAPI("user_email", email);
        }
    }

    /**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     */
    private void showError(@Nullable String title, @Nullable String errorMessage) {
        if (title == null) title = APIManager.ALERT;
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(title, errorMessage, null, getContext(), null);
    }

    /**
     * Initialize process of receiving OTP through SMS
     *
     * @param key   :String
     * @param value :String
     */
    private void initiateOtpAPI(String key, String value) {

        AppSignatureHelper signatureHelper = new AppSignatureHelper(requireContext());
        Log.v("RespectiveStore Key HAsh:", signatureHelper.getAppSignatures().get(0));

        try {
            startVerification();
            Map<String, String> params = new HashMap<>();
            params.put(key, value);
            if (editpage.equalsIgnoreCase("mobile")) {
                params.put("calling_code", countryDataList.get(spinnerCountry.getSelectedItemPosition()).countryCode);
            }
            params.put("android_hash_key", signatureHelper.getAppSignatures().get(0));
            APIRequest apiRequest = new APIRequest(Url.SEND_VERFICATION_OTP, Request.Method.POST, params, null, getActivity());
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                handleOtpResponse(response, error);
                //startVerification();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleOtpResponse(String response, Exception error) {
        try {
            if (error != null) {
                showError(null, error.getMessage());
                llVerifyBox.setVisibility(View.GONE);
                btSendOtp.setVisibility(View.VISIBLE);
            } else {
                if (response != null) {
                    Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String title = jsonObjectResponse.has("title") ? jsonObjectResponse.getString("title") : null;
                        String message = jsonObjectResponse.has("Description") ? jsonObjectResponse.getString("Description") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(title, message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        llVerifyBox.setVisibility(View.VISIBLE);
                        edtOtp.requestFocus();
                        btSendOtp.setVisibility(View.GONE);
                        JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                        String message = jsonObject.has("message") ? jsonObject.getString("message") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        //AlertUtils.showAlert(getString(R.string.label_success), message, null, getContext(), null);
                        AlertUtils.showToast(message, 1, getActivity());
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method handles otp sign in
     *
     * @param response api response. Can be null
     * @param error    error message in case of error. Can be null
     * @param key
     * @param value
     */

    private void handleVerifyResponse(@Nullable String response, @Nullable Exception error, String key, String value) {
        try {
            if (error != null) {
                showError(null, error.getMessage());
            } else {
                if (response != null) {
                    Log.e("response", response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(null, message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                        if (jsonObject.length() > 0) {
                            JSONObject jsonObjectuser = jsonObject.has("user") ? jsonObject.getJSONObject("user") : new JSONObject();
                            if (jsonObjectuser.length() > 0) {
                                User user = new User(jsonObjectuser);
                                LocalStorage.setUserData(user);
                                if (isGame) {
                                    if (fragmentClick != null) {
                                        fragmentClick.onClick();
                                    }
                                } else {
                                    if (isVerificationActivity) {
                                        getFragmentManager().popBackStack();
                                        getActivity().finish();
                                    } else if (isGiveAwayActivity) {
                                        getFragmentManager().popBackStack();
                                        getActivity().setResult(GIVE_AWAY_CODE);
                                        getActivity().finish();
                                    } else {
                                        getFragmentManager().popBackStack();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {

        }
    }

    /**
     * Sign in with otp
     * on successful sign in you will get an auth_token.
     * You should provide this token at headers X-AUTH-TOKEN in order to validate user.
     *
     * @param key
     * @param value
     */
    private void otpVerifyAPI(String key, String value) {
        try {
            String otp = edtOtp.getText().toString();
            if (otp.equals("")) {
                showError(null, getString(R.string.please_enter_otp));
                llVerifyBox.setVisibility(View.VISIBLE);
                btSendOtp.setVisibility(View.GONE);
                return;
            }
            Map<String, String> params = new HashMap<>();
            params.put(key, value);
            if (editpage.equalsIgnoreCase("mobile")) {
                params.put("calling_code", countryDataList.get(spinnerCountry.getSelectedItemPosition()).countryCode);
            }
            params.put("otp", otp);
            APIRequest apiRequest = new APIRequest(Url.VERFICATION_OTP, Request.Method.POST, params, null, getActivity());
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleVerifyResponse(response, error, key, value);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String SMS_RETRIEVER_TAG = "SmsRetriever";
    private SmsRetrieverClient smsRetrieverClient;

    private void setupSmsRetriever() {
        smsRetrieverClient = SmsRetriever.getClient(getActivity());
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

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(Constant.OTP_MESSAGE);
        getActivity().registerReceiver(otpMessageObserver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            getActivity().unregisterReceiver(otpMessageObserver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSendOtp:
            case R.id.tvResendOtp:
                SendOtp();
                break;

            case R.id.btVerify:
                VerifyOtp();
                break;
            default:
                break;
        }
    }

    public Boolean getGame() {
        return isGame;
    }

    public void setGame(Boolean game) {
        isGame = game;
    }

    public FragmentClick getFragmentClick() {
        return fragmentClick;
    }

    public void setFragmentClick(FragmentClick fragmentClick) {
        this.fragmentClick = fragmentClick;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if ((HomeActivity) getActivity() != null) {
            ((HomeActivity) getActivity()).showToolbarIcon();
            ((HomeActivity) getActivity()).showToolbarTitle(false);
        }*/
    }

    private void getCountryData() {
        if (LocalStorage.getCountriesList(LocalStorage.KEY_COUNTRIES_LIST, getContext()) != null) {
            countryDataList.addAll(LocalStorage.getCountriesList(LocalStorage.KEY_COUNTRIES_LIST, getContext()));
            for (int i = 0; i < countryDataList.size(); i++) {
                if (countryDataList.get(i).countryCode.contains("91")) {
                    spinnerCountry.setSelection(i);
                }
            }
            countryAdapter.notifyDataSetChanged();
        }
    }
}
