package com.app.itaptv.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.BuildConfig;
import com.app.itaptv.R;
import com.app.itaptv.activity.BrowserActivity;
import com.app.itaptv.activity.BuyDetailActivity;
import com.app.itaptv.activity.CouponRedemptionActivity;
import com.app.itaptv.activity.DownloadActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.activity.LanguageSettingsActivity;
import com.app.itaptv.activity.LoginActivity;
import com.app.itaptv.activity.MyWatchList;
import com.app.itaptv.activity.PremiumActivity;
import com.app.itaptv.activity.PurchasesActivity;
import com.app.itaptv.activity.ReferAndEarnActivity;
import com.app.itaptv.custom_interface.NavigationMenuCallback;
import com.app.itaptv.holder.MoreHolder;
import com.app.itaptv.interfaces.KeyEventListener;
import com.app.itaptv.structure.MoreMenuData;
import com.app.itaptv.structure.User;
import com.app.itaptv.tvControllers.MoreTvAdapter;
import com.app.itaptv.tv_fragment.ViewAllTvFragment;
import com.app.itaptv.utils.AnalyticsTracker;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.SpacingItemDecoration;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
/*import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;*/

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.app.itaptv.API.APIManager.TOKEN_EXPIRED;
import static com.app.itaptv.fragment.ProfileFragment.MY_PERMISSIONS_REQUEST_CODE;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MoreFragment extends Fragment {

    View view;
    RecyclerView rvMoreMenu;
    KRecyclerViewAdapter MoreMenuAdapter;
    MoreTvAdapter moreTvAdapter;
    ProgressBar llprogressbar;
    ImageView civUser, ivEditImage;
    TextView tvUserName, tvUserMobileNo, tveditprofile, versionTv;
    ConstraintLayout profileConstraint;
    ArrayList<MoreMenuData> moreManuList = new ArrayList<>();
    private boolean shouldOpenProfile = false;
    private int RC_SIGN_IN = 428;
    User user;
    private String userChoosenTask;
    String[] arrPermissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private File pic = null;
    File file;
    private Uri picUri;
    private int LOAD_IMAGE_CAMERA = 33;
    private int LOAD_IMAGE_GALLARY = 4;
    public static String image_path = "";

    private boolean isRemoveImage = false;
    boolean isSuccess = false;
    HomeActivity homeActivity;
    public NavigationMenuCallback navigationMenuCallback;
    int lastSelectedIndex = 0;
    RecyclerView.SmoothScroller smoothScroller;
    LinearLayoutManager linearLayoutManager;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // showAd();
        }
    }

    private void showAd(String id) {
        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            homeActivity = (HomeActivity) getActivity();
            if (homeActivity != null) {
                homeActivity.showAd(id);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_more, container, false);
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserData();
        if (getActivity() != null) {
            ((HomeActivity) getActivity()).showToolbarIcon();
            ((HomeActivity) getActivity()).showToolbarTitle(false);
            if (Utility.isTelevision()) {
                ((HomeActivity) getActivity()).showToolbar(false);
            }
        }
        if (shouldOpenProfile) {
            shouldOpenProfile = false;
            ProfileFragment profileFragment = new ProfileFragment();
            openFragment(profileFragment);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void init() {
        rvMoreMenu = view.findViewById(R.id.rvMoreMenu);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserMobileNo = view.findViewById(R.id.tvUserMobileNo);
        tveditprofile = view.findViewById(R.id.tveditprofile);
        civUser = view.findViewById(R.id.civUser);
        ivEditImage = view.findViewById(R.id.ivEditImage);
        profileConstraint = view.findViewById(R.id.constraint_profile);
        llprogressbar = view.findViewById(R.id.progressBar);
        versionTv = view.findViewById(R.id.versionTv);

        user = LocalStorage.getUserData();
        if (Utility.isTelevision()) {
            profileConstraint.setVisibility(View.GONE);
        } else {
            profileConstraint.setVisibility(View.VISIBLE);
        }


        rvMoreMenu.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DPAD_LEFT) {
                    navigationMenuCallback.navMenuToggle(true);
                }
                return false;
            }
        });


        ivEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), image_permissions, MY_PERMISSIONS_REQUEST_IMG);
                } else {*/
                final CharSequence[] options = {
                        getString(R.string.action_take_photo),
                        getString(R.string.action_choose_from_gallery),
                        getString(R.string.action_select_avatar),
                        getString(R.string.action_remove_profile_photo)};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.label_set_profile_image);
                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (options[item].equals(getString(R.string.action_take_photo))) {
                            userChoosenTask = getString(R.string.action_take_photo);
                            if (Utility.checkPermission(getActivity(), arrPermissions)) {
                                camera();
                            } else {
                                Utility.requestPermission(getActivity(), MY_PERMISSIONS_REQUEST_CODE, arrPermissions);
                            }

                        } else if (options[item].equals(getString(R.string.action_choose_from_gallery))) {
                            userChoosenTask = getString(R.string.action_take_photo);
                            gallery();
                        } else if (options[item].equals(getString(R.string.action_select_avatar))) {
                            userChoosenTask = getString(R.string.action_select_avatar);
                            openWebView();
                        } else if (options[item].equals(getString(R.string.action_remove_profile_photo))) {
                            userChoosenTask = getString(R.string.action_remove_profile_photo);
                            isRemoveImage = true;
                            new ImageUpload().execute();
                        }
                    }
                });
                builder.show();
            }
        });

        profileConstraint.setOnClickListener(this::setOpenEditProfile);
        setUserData();
        if (moreManuList.size() == 0) {
            setData();
        } else {
            setUpRecyclerView();
        }
        versionTv.setText("v " + BuildConfig.VERSION_NAME);
    }

    private void updateProfile() {
        String username = tvUserName.getText().toString();
        if (username.equals("")) {
            return;
        }
        updateProfileAPI(username);
    }

    private void updateProfileAPI(String username) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("display_name", username);
            Log.d("params", params.toString());
            APIRequest apiRequest = new APIRequest(Url.UPDATE_PROFILE, Request.Method.POST, params, null, getActivity());
            //apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleUpdateProfileResponse(response, error);
                }
            });
        } catch (Exception e) {

        }
    }

    private void handleUpdateProfileResponse(String response, Exception error) {
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        //String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        String message = jsonObjectResponse.has("msg") ? Utility.getMessage(getContext(), jsonObjectResponse.get("msg").toString()) : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                        if (jsonObject.length() > 0) {
                            JSONObject jsonObjectuser = jsonObject.has("user") ? jsonObject.getJSONObject("user") : new JSONObject();
                            if (jsonObjectuser.length() > 0) {
                                User user = new User(jsonObjectuser);
                                LocalStorage.setUserData(user);
                                setUserImage();
                                // getFragmentManager().popBackStack();
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {

        }
    }

    private void setUserData() {
        Gson gson = new Gson();
        String json = LocalStorage.getValue(LocalStorage.KEY_USER_DATA, "", String.class);
        User obj = gson.fromJson(json, User.class);
        if (obj != null) {
            if (obj.displayName == null || obj.displayName.isEmpty()) {
                tvUserMobileNo.setText(Utility.checkNullStringForHyphen(obj.mobile));
                tvUserName.setVisibility(View.INVISIBLE);
            } else if (obj.mobile == null || obj.mobile.isEmpty()) {
                tvUserName.setText(Utility.checkNullStringForHyphen(obj.displayName));
                tvUserMobileNo.setVisibility(View.INVISIBLE);
            } else {
                tvUserName.setText(Utility.checkNullStringForHyphen(obj.displayName));
                tvUserMobileNo.setText(Utility.checkNullStringForHyphen(obj.mobile));
            }
            updateProfile();
            //tvUserMobileNo.setText(Utility.checkNullStringForHyphen(obj.mobile));
            //loadProfileImage(obj.img);
        }
    }

    private void setUserImage() {
        Gson gson = new Gson();
        String json = LocalStorage.getValue(LocalStorage.KEY_USER_DATA, "", String.class);
        User obj = gson.fromJson(json, User.class);
        if (obj != null) {
            loadProfileImage(obj.img);
        }

    }

    private void loadProfileImage(String imageUrl) {
        if (getActivity() == null) return;

        Gson gson = new Gson();
        String json = LocalStorage.getValue(LocalStorage.KEY_USER_DATA, "", String.class);
        User obj = gson.fromJson(json, User.class);

        RequestOptions options = new RequestOptions()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.user);

        if (!imageUrl.isEmpty()) {
            Glide.with(getActivity())
                    .load(imageUrl)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(civUser);
        }
    }

    /**
     * Add Menu Data from String Array
     */
    private void setData() {
        String[] menuNames = getResources().getStringArray(R.array.arra_more_menu_name);
        String[] menuId = getResources().getStringArray(R.array.arra_more_menu_id);
        TypedArray testArrayIcon = getResources().obtainTypedArray(R.array.arra_more_menu_icon);
        for (int i = 0; i < menuNames.length; i++) {
            MoreMenuData moreMenuData = new MoreMenuData(menuId[i], menuNames[i], testArrayIcon.getResourceId(i, -1));
            moreManuList.add(moreMenuData);
        }
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        if (Utility.isTelevision()) {
            for (int i = 0; i < moreManuList.size(); i++) {
                if (moreManuList.get(i).MenuName.equalsIgnoreCase(getResources().getString(R.string.label_downloads)) || moreManuList.get(i).MenuName.equalsIgnoreCase(getResources().getString(R.string.label_redeem_coupon)) || moreManuList.get(i).MenuName.equalsIgnoreCase(getResources().getString(R.string.label_contact_us))) {
                    moreManuList.remove(i);
                }
            }
            if (!moreManuList.isEmpty()) {
                moreManuList.get(0).isFocused = true;
            }
            LinearLayout.LayoutParams parameter = (LinearLayout.LayoutParams) rvMoreMenu.getLayoutParams();
            rvMoreMenu.setLayoutParams(parameter);
        }
        rvMoreMenu.setPadding(15, 15, 10, 10);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvMoreMenu.setLayoutManager(linearLayoutManager);
        if (Utility.isTelevision()) {
            smoothScroller =
                    new ViewAllTvFragment.CenterSmoothScroller(rvMoreMenu.getContext());
            moreTvAdapter = new MoreTvAdapter(requireContext(), moreManuList, new KeyEventListener() {
                @Override
                public void onKeyEvent(int position, Object item, View view, boolean isFirstRow) {
                    if (isFirstRow) {
                        navigationMenuCallback.navMenuToggle(true);
                    } else {
                        lastSelectedIndex = position;
                        smoothScroller.setTargetPosition(position);
                        linearLayoutManager.startSmoothScroll(smoothScroller);
                    }
                    if (item instanceof MoreMenuData) {
                        OpenPages((MoreMenuData) item);
                    }
                }
            });
            rvMoreMenu.setAdapter(moreTvAdapter);
            moreTvAdapter.notifyDataSetChanged();
        } else {
            MoreMenuAdapter = new KRecyclerViewAdapter(getContext(),
                    moreManuList, (viewGroup, i) -> {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_more_menu, viewGroup, false);
                return new MoreHolder(view, this);
            }, (kRecyclerViewHolder, o, i) -> {
                if (o instanceof MoreMenuData) {
                    OpenPages((MoreMenuData) o);
                }
            });
            rvMoreMenu.setAdapter(MoreMenuAdapter);
            rvMoreMenu.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING));
            MoreMenuAdapter.notifyDataSetChanged();
        }
    }

    private void OpenPages(MoreMenuData moreMenuData) {
        /*if (i == 1) {
            startActivity(new Intent(getContext(), DownloadsActivity.class));
            return;
        }
        if (i > 1) i -= 1;*/
        switch (moreMenuData.MenuName) {
            case "Manage Subscriptions":
                startActivity(new Intent(getContext(), PremiumActivity.class).putExtra("title", getResources().getString(R.string.label_premium)));
                break;
            case "Purchases":
                startActivity(new Intent(getContext(), PurchasesActivity.class).putExtra("title", getResources().getString(R.string.label_purchases)));
                break;
            case "Language Settings":
                startActivity(new Intent(getContext(), LanguageSettingsActivity.class).putExtra("title", getResources().getString(R.string.label_language_setting)));
                break;
            case "My Watchlist":
                ((HomeActivity) getContext()).startActivityForResult(new Intent(getContext(), MyWatchList.class), BuyDetailActivity.REQUEST_CODE);
                break;
            case "Downloads":
                startActivity(new Intent(getContext(), DownloadActivity.class).putExtra("title", getResources().getString(R.string.label_downloads)));
                break;
            case "Redeem Voucher":
                startActivity(new Intent(getContext(), CouponRedemptionActivity.class).putExtra("title", getResources().getString(R.string.label_redeem_coupon)));
                break;
            case "Refer & Earn":
                startActivity(new Intent(getContext(), ReferAndEarnActivity.class));
                break;
            case "Contact Us":
                startActivity(new Intent(getContext(), BrowserActivity.class).putExtra("title", getResources().getString(R.string.label_contact_us)).putExtra("posturl", Url.CONTACT_US));
                break;
            case "Terms of Use":
                startActivity(new Intent(getContext(), BrowserActivity.class).putExtra("title", getResources().getString(R.string.label_terms_of_uses)).putExtra("posturl", Url.TERMS_OF_USE));
                break;
            case "Privacy Policy":
                startActivity(new Intent(getContext(), BrowserActivity.class).putExtra("title", getResources().getString(R.string.label_privacy_policy)).putExtra("posturl", Url.PRIVACY_POLICY));
                break;
            case "Refunds & Cancellation":
                startActivity(new Intent(getContext(), BrowserActivity.class).putExtra("title", getResources().getString(R.string.label_refund_cancellation)).putExtra("posturl", Url.REFUNDS_AND_CANCELLATION));
                break;
            case "About":
                startActivity(new Intent(getContext(), BrowserActivity.class).putExtra("title", getResources().getString(R.string.label_about)).putExtra("posturl", Url.ABOUT));
                break;
            case "Logout":
                showLogoutDialog();
                break;
            default:
                break;
        }
        //showAd(moreManuList.get(i).id);
    }

    private void startManageSubscriptions() {
        llprogressbar.setVisibility(View.VISIBLE);
        String userId = LocalStorage.getUserId();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", userId);
            APIRequest apiRequest = new APIRequest(Url.FETCH_SUBSCRIPTIONS_URL, Request.Method.POST, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    llprogressbar.setVisibility(View.GONE);
                    if (error != null) {
                        showError(error.getMessage());
                    } else {
                        try {
                            if (response != null) {
                                Log.e("response", response);
                                JSONObject jsonObjectResponse = new JSONObject(response);
                                String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                                if (type.equalsIgnoreCase("error")) {
                                    String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                    //AlertUtils.showToast(message, 1, getActivity());
                                } else if (type.equalsIgnoreCase("ok")) {
                                    JSONObject jsonObjMsg = jsonObjectResponse.getJSONObject("msg");
                                    String subscriptionsUrl = jsonObjMsg.getString("url");
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(subscriptionsUrl));
                                    startActivity(browserIntent);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.note))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (getActivity() != null && getActivity() instanceof HomeActivity) {
                        homeActivity = (HomeActivity) getActivity();
                        if (homeActivity != null) {
                            homeActivity.deletePlaylist();
                        }
                    }
                    AnalyticsTracker.stopDurationUser();
                    LocalStorage.logout();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setAction(TOKEN_EXPIRED);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    AnalyticsTracker.startDurationUser();
                    startActivity(intent);
                }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .setNegativeButton(android.R.string.no, null)
                .setIcon(getActivity().getResources().getDrawable(R.drawable.ic_new_alert))
                .show();
    }

    public void showRedeemCoinsDialog(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_redeem_coins_offer, null);

        ImageView iv_go = (ImageView) view.findViewById(R.id.iv_go);
        final Dialog alertDialogBuilder = new Dialog(context);
        alertDialogBuilder.setContentView(view);
        alertDialogBuilder.show();

        iv_go.setOnClickListener(view1 -> {
            callForOffer();
            alertDialogBuilder.dismiss();
        });
    }

    public void callForOffer() {
        llprogressbar.setVisibility(View.VISIBLE);
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(Url.REDEEM_COINS_FOR_CASH + "&userId=" + LocalStorage.getUserId(), Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    llprogressbar.setVisibility(View.GONE);
                    handleFeedResponse(response, error);
                }
            });
        } catch (Exception e) {

        }
    }

    /**
     * This method handles feed response
     *
     * @param response api response. Can be null
     * @param error    error message in case of error. Can be null
     */

    private void handleFeedResponse(@Nullable String response, @Nullable Exception error) {
        //arrayListFeedData.clear();
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showRedeemCoinsExpiredDialog(getContext(), message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        String message = jsonArrayMsg.getString("message");
                        int cash = jsonArrayMsg.getInt("cash");
                        showRedeemCoinsSuccessDialog(getContext(), String.valueOf(cash));
                    }
                }
            }
        } catch (JSONException e) {
            showError(error.getMessage());
        }
    }

    /**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     */
    private void showError(@Nullable String errorMessage) {
        if (getContext() != null) {
            if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
            //AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, getContext(), null);
        }
    }

    public void showRedeemCoinsSuccessDialog(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_redeem_coins_success, null);

        ImageView iv_go = (ImageView) view.findViewById(R.id.iv_got_it);
        TextView tv_cash_won = (TextView) view.findViewById(R.id.tv_cash_won);
        final Dialog alertDialogBuilder = new Dialog(context);
        alertDialogBuilder.setContentView(view);
        alertDialogBuilder.show();
        tv_cash_won.setText(message);

        iv_go.setOnClickListener(view1 -> alertDialogBuilder.dismiss());
    }

    public void showRedeemCoinsExpiredDialog(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_redeem_coins_expired, null);

        ImageView iv_go = (ImageView) view.findViewById(R.id.iv_got_it);
        TextView tv_error_message = (TextView) view.findViewById(R.id.tv_error_message);
        final Dialog alertDialogBuilder = new Dialog(context);
        alertDialogBuilder.setContentView(view);
        alertDialogBuilder.show();
        tv_error_message.setText(message);
        iv_go.setOnClickListener(view1 -> alertDialogBuilder.dismiss());
    }

    public void setOpenEditProfile(View view1) {
        if (view1 instanceof ConstraintLayout) {
            ProfileFragment profileFragment = new ProfileFragment();
            openFragment(profileFragment);
        }
    }

    /**
     * Open specific fragment on BottomNavigationView item selected
     *
     * @param fragment Fragment object to display the Fragment
     */
    private void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public boolean isShouldOpenProfile() {
        return shouldOpenProfile;
    }

    public void setShouldOpenProfile(boolean shouldOpenProfile) {
        this.shouldOpenProfile = shouldOpenProfile;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == LOAD_IMAGE_CAMERA && resultCode == RESULT_OK) {
                cropImage();
            } else if (requestCode == LOAD_IMAGE_GALLARY) {
                if (data != null) {
                    picUri = data.getData();
                    android.util.Log.e("PIC URL", "" + picUri);
                    cropImage();
                }
            } /*else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    file = new File(resultUri.getPath());
                    image_path = file.getAbsolutePath();
                    new ImageUpload().execute();
                    if (pic != null) {
                        // To delete original image taken by camera
                        if (pic.delete()) ;
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }*/
        }
    }

    void camera() {
        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pic = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)),
                        "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            } else {
                pic = new File(Environment.getExternalStorageDirectory(),
                        "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            }

            //  picUri = Uri.fromFile(pic);
            picUri = FileProvider.getUriForFile(getActivity(),
                    "com.app.itap.fileprovider",
                    pic);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
            cameraIntent.putExtra("return-data", true);
            startActivityForResult(cameraIntent, LOAD_IMAGE_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    void gallery() {
        pic = new File(Environment.getExternalStorageDirectory(),
                "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), LOAD_IMAGE_GALLARY);
    }

    protected void cropImage() {
      /*  Intent intent = CropImage.activity(picUri)
                .setAspectRatio(1, 1)
                .setRequestedSize(150, 150, CropImageView.RequestSizeOptions.RESIZE_EXACT)
                .getIntent(getContext());
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);*/
    }

    public void openWebView() {
        if (Utility.checkPermission(getContext(), arrPermissions)) {
            startActivity(new Intent(getContext(), BrowserActivity.class).putExtra("title", "Ready Player Me").putExtra("posturl", Url.READY_PLAYER));
        } else {
            Utility.requestPermission(getContext(), BrowserActivity.MY_PERMISSIONS_REQUEST_CODE, arrPermissions);
        }
    }

    private class ImageUpload extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = new ProgressDialog(getActivity());
            //dialog.setCancelable(false);
            //dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String token = LocalStorage.getToken();
            String appVersion = LocalStorage.getAppVersion();
            if (token == null) {
                token = "";
            }
            if (appVersion == null) {
                appVersion = "";
            }

            final MediaType MEDIA_TYPE = image_path.endsWith("png") ?
                    MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            OkHttpClient client = new OkHttpClient();
            RequestBody body;
            if (!isRemoveImage) {
                RequestBody file_body = RequestBody.create(MEDIA_TYPE, file);
                body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", isRemoveImage ? "" : image_path.substring(image_path.lastIndexOf("/") + 1), file_body)
                        .addFormDataPart("remove_image", "false")
                        .build();
            } else {
                body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("remove_image", "true")
                        .build();
            }

            Log.i("body", "" + body);
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(Url.UPDATE_PROFILE_IMAGE)
                    .header("X-AUTH-TOKEN", token)
                    .header("X-APP-VERSION", appVersion)
                    .post(body)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonObjectResponse = null;
            if (response != null && response.isSuccessful()) {
                Log.d("Response: ", String.valueOf(response));
                try {
                    String jsonData = response.body().string();
                    try {
                        jsonObjectResponse = new JSONObject(jsonData);
                        String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                        if (type.equalsIgnoreCase("error")) {
                            isSuccess = false;
                            String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                            showError(message);
                        } else if (type.equalsIgnoreCase("ok")) {
                            JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                            if (jsonObject.length() > 0) {
                                JSONObject jsonObjectuser = jsonObject.has("user") ? jsonObject.getJSONObject("user") : new JSONObject();
                                if (jsonObjectuser.length() > 0) {
                                    isSuccess = true;
                                    User user = new User(jsonObjectuser);
                                    LocalStorage.setUserData(user);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Response: ", jsonData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!isSuccess) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (getActivity() != null) {
                        Glide.with(getActivity())
                                .load(isRemoveImage ? R.drawable.user : file)
                                .thumbnail(0.1f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.user)
                                .into(civUser);
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (getActivity() != null) {
                        Glide.with(getActivity())
                                .load(isRemoveImage ? R.drawable.user : file)
                                .thumbnail(0.1f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.user)
                                .into(civUser);
                    }
                });
            }
        }
    }

    public void setNavigationMenuCallbackMore(NavigationMenuCallback callback) {
        this.navigationMenuCallback = callback;
    }

    public void restorePosition() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                smoothScroller.setTargetPosition(0);
                linearLayoutManager.startSmoothScroll(smoothScroller);
                Objects.requireNonNull(moreManuList.get(0)).isFocused = true;
                moreTvAdapter.setLastSelectedIndex(0);
                moreTvAdapter.notifyDataSetChanged();
            }
        }, 10);
    }

}
