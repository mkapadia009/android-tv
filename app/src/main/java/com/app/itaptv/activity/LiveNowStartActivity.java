/*
package com.app.itap.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.app.itap.API.APIManager;
import com.app.itap.API.APIRequest;
import com.app.itap.API.Url;
import com.app.itap.R;
import com.app.itap.holder.MessageHolder;
import com.app.itap.opentok.CustomVideoCapturer;
import com.app.itap.structure.LiveNowData;
import com.app.itap.structure.MessageData;
import com.app.itap.utils.AlertUtils;
import com.app.itap.utils.GameDateValidation;
import com.app.itap.utils.LocalStorage;
import com.app.itap.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

*/
/**
 * Created by poonam on 8/2/19.
 *//*

public class LiveNowStartActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks,
        Session.SessionListener,
        PublisherKit.PublisherListener {

    public static final String LIVE_SESSION_DATA = "live_session_data";
    private String sourceDate = "yyyy-MM-dd HH:mm:ss";
    private String destinationDate = "EEEE MMM dd, hh:mm a";
    LiveNowData liveNowData;

    LinearLayout llParent;
    CardView cvCardView;
    TextView tvScheduledTime;
    TextView tvTitle;
    TextView tvCountDownTime, tvAllQuestions, tvNoOfViewers, pauseText, pauseButtonText;
    Button startButton;
    ImageButton presenterLiveCloseButton, questionButton, swapCameraButton, pauseButton, resumeButton, micOnButton, micOffButton;
    ImageView ivImage, ivLiveImage;

    RecyclerView recyclerComments;
    LinearLayoutManager mLinearLayoutManager;

    KRecyclerViewAdapter adapter;
    JSONObject jsonObjectResponse;
    ArrayList<MessageData> arrayListMessageData = new ArrayList<>();

    Session mSession;
    Publisher mPublisher;

    FrameLayout publisherFrame;

    CountDownTimer countDownTimer;
    SimpleDateFormat sourceDateFormat = new SimpleDateFormat(sourceDate);

    String channelUser, channelComment, timeLeft, status;
    MessageData messageData;

    ProgressBar progressBar;

    int feedSelectedId;
    String getSessionDataUrl = "";
    String getGoLiveUrl = "";
    String getPauseLiveUrl = "";
    String getStopLiveUrl = "";

    private static String API_KEY = "";
    private static String SESSION_ID = "";
    private static String TOKEN = "";
    private static String ARCHIVE = "";
    private static String BROADCAST = "";
    private static String HLSBROADCASTURL = "";
    private static boolean IS_BROADCASTED = false;
    private static final String LOG_TAG = LiveNowStartActivity.class.getSimpleName();
    private static final int RC_VIDEO_APP_PERM = 124;

    Button reactionHeart, reactionClap, reactionLaugh, reactionWow;
    LinearLayout linearLayoutReactions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_now_start);
        init();
    }

    private void init() {
        liveNowData = getIntent().getExtras().getBundle("Bundle").getParcelable(LIVE_SESSION_DATA);

        llParent = findViewById(R.id.llParent);
        cvCardView = findViewById(R.id.cvCardView);
        tvScheduledTime = findViewById(R.id.tvScheduledTime);
        tvAllQuestions = findViewById(R.id.view_all_questions_text);
        tvTitle = findViewById(R.id.tvStoreName);
        pauseText = findViewById(R.id.tv_pause_text);
        tvCountDownTime = findViewById(R.id.tvCountDownTime);
        startButton = findViewById(R.id.btStartNow);
        presenterLiveCloseButton = findViewById(R.id.presenter_close_live_button);
        pauseButton = findViewById(R.id.pause_live_button);
        resumeButton = findViewById(R.id.resume_live_button);
        pauseButtonText = findViewById(R.id.pause_live_session_text);
        swapCameraButton = findViewById(R.id.presenter_camera_swap_button);
        questionButton = findViewById(R.id.presenter_questions_button);
        progressBar = findViewById(R.id.progressBar);
        publisherFrame = findViewById(R.id.publisher_container);
        ivImage = findViewById(R.id.ivImage);
        ivLiveImage = findViewById(R.id.iv_live_image);
        tvNoOfViewers = findViewById(R.id.tv_number_of_viewers);
        micOnButton = findViewById(R.id.ic_mic);
        micOffButton = findViewById(R.id.ic_mic_off);

        reactionHeart = findViewById(R.id.button_reaction_love);
        reactionClap = findViewById(R.id.button_reaction_clap);
        reactionLaugh = findViewById(R.id.button_reaction_laugh);
        reactionWow = findViewById(R.id.button_reaction_wow);
        linearLayoutReactions = findViewById(R.id.linear_reactions);

        recyclerComments = findViewById(R.id.recycler_view_comments);

        startButton.setVisibility(View.INVISIBLE);
        recyclerComments.setVisibility(View.INVISIBLE);
        presenterLiveCloseButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        resumeButton.setVisibility(View.INVISIBLE);
        pauseButtonText.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        publisherFrame.setVisibility(View.INVISIBLE);
        questionButton.setVisibility(View.INVISIBLE);
        swapCameraButton.setVisibility(View.INVISIBLE);
        tvAllQuestions.setVisibility(View.INVISIBLE);
        tvNoOfViewers.setVisibility(View.INVISIBLE);
        ivLiveImage.setVisibility(View.INVISIBLE);
        linearLayoutReactions.setVisibility(View.INVISIBLE);
        pauseText.setVisibility(View.INVISIBLE);
        micOnButton.setVisibility(View.INVISIBLE);
        micOffButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        setData();
        initializeRecyclerView();

        presenterLiveCloseButton.setOnClickListener(view -> showCloseSessionDialog(LiveNowStartActivity.this));

        pauseButton.setOnClickListener(view -> showPauseSessionDialog(LiveNowStartActivity.this));

        resumeButton.setOnClickListener(view -> resumeSession());

        micOnButton.setOnClickListener(view -> {
            if (mPublisher != null) {
                mPublisher.setPublishAudio(false);
                micOnButton.setVisibility(View.INVISIBLE);
                micOffButton.setVisibility(View.VISIBLE);
            }
        });

        micOffButton.setOnClickListener(view -> {
            if (mPublisher != null) {
                mPublisher.setPublishAudio(true);
                micOffButton.setVisibility(View.INVISIBLE);
                micOnButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setData() {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //if you need fix imageview height
        int devicewidth = (int) (displaymetrics.widthPixels);
        int deviceheight = (int) (devicewidth / 2);
        ivImage.getLayoutParams().height = deviceheight;

        //boolean isValid = isValidDate(liveNowData.time);
        boolean isValidDate = isValidDate(liveNowData.time);
        String formattedDateTime = Utility.formatDate(sourceDate, destinationDate, liveNowData.time)
                .replace("AM", "am")
                .replace("PM", "pm");
        String scheduledTime = String.format(getString(R.string.scheduled_time), formattedDateTime, liveNowData.duration);

        Glide.with(this)
                .load(liveNowData.imageUrl)
                .thumbnail(0.1f)
                .apply(new RequestOptions().centerCrop().dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.ALL).override(devicewidth, deviceheight))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivImage);

        tvTitle.setText(liveNowData.title);
        tvScheduledTime.setText(scheduledTime);

        if (isValidDate) {
            getSessionData();
        } else {
            tvScheduledTime.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.INVISIBLE);
            startCountDown();
        }
    }

    private boolean isValidDate(String date) {
        String sourceDate = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat(sourceDate);
        Calendar calendar = Calendar.getInstance();
        String currentDate = sourceDateFormat.format(calendar.getTime());

        try {
            Date dtScheduledDate = sourceDateFormat.parse(date);
            Date dtCurrentDate = sourceDateFormat.parse(currentDate);
            if (dtScheduledDate.equals(dtCurrentDate) || dtScheduledDate.before(dtCurrentDate)) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getSessionData() {
        if (liveNowData != null) {
            feedSelectedId = liveNowData.id;
            reactionHeart.setText(String.valueOf(liveNowData.reaction_HEART));
            reactionLaugh.setText(String.valueOf(liveNowData.reaction_LAUGH));
            reactionClap.setText(String.valueOf(liveNowData.reaction_CLAPS));
            reactionWow.setText(String.valueOf(liveNowData.reaction_WOW));
            tvNoOfViewers.setText(String.valueOf(liveNowData.viewers));
            Log.d("Feed Listing ID: ", String.valueOf(feedSelectedId));
            getSessionDataUrl = Url.GET_LIVE_SESSION_DETAILS + feedSelectedId;
            callPrepareSessionAPI();
            channelConnect();
        }
    }

    private void callPrepareSessionAPI() {
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(getSessionDataUrl, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> handlePrepareSessionAPIResponse(response, error));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePrepareSessionAPIResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    String type = jsonObject.has("type") ? jsonObject.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObject.has("msg") ? jsonObject.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObjectMessage = jsonObject.getJSONObject("msg");
                        SESSION_ID = jsonObjectMessage.getString("session_id");
                        TOKEN = jsonObjectMessage.getString("presenter_token_id");
                        API_KEY = jsonObjectMessage.getString("api_key");
                        Log.d("Session Data Values", SESSION_ID + " " + TOKEN + " " + API_KEY);
                        requestPermissions();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    */
/**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     *//*

    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, this, null);
    }

    private void initializeRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        recyclerComments.setLayoutManager(mLinearLayoutManager);
        adapter = new KRecyclerViewAdapter(this, arrayListMessageData, (parent, i) -> {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_received, parent, false);
            return new MessageHolder(layoutView);
        }, (kRecyclerViewHolder, o, i) -> {
            // Handle item click (since comments, nothing needed).
        });
        recyclerComments.setAdapter(adapter);
        getSessionMessages();

        */
/*if (arrayListMessageData.size() != 0) {
            scrollToBottom();
        }*//*

        recyclerComments.scrollToPosition(arrayListMessageData.size() - 1);
    }

    public void getSessionMessages() {
        Map<String, String> params = new HashMap<>();
        APIRequest apiRequest = new APIRequest(Url.GET_SESSION_MESSAGES + "&ID=" + liveNowData.id, Request.Method.GET, params, null, this);
        apiRequest.showLoader = false;
        APIManager.request(apiRequest, (response, error, headers, statusCode) -> handleSessionMessageAPIResponse(response, error));
    }

    private void handleSessionMessageAPIResponse(String response, Exception error) {
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    String type = jsonObject.has("type") ? jsonObject.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObject.has("msg") ? jsonObject.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObjectMessage = jsonObject.getJSONObject("msg");
                        JSONArray jsonArray = jsonObjectMessage.getJSONArray("contents");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String userName = jsonObject1.getString("name");
                            String message = jsonObject1.getString("comment");
                            generateMessageJSON(userName, message);
                        }
                        new Handler(Looper.getMainLooper()).post(() -> {
                            adapter.notifyDataSetChanged();
                            scrollToBottom();
                        });
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void scrollToBottom() {
        new Handler().postDelayed(() -> {
            if (arrayListMessageData.size() > 0) {
                recyclerComments.smoothScrollToPosition(arrayListMessageData.size() - 1);
            }
        }, 300);
        */
/*if (arrayListMessageData.size() > 0) {
            recyclerComments.smoothScrollToPosition(arrayListMessageData.size() - 1);
        }*//*

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize and connect to the session
            // new Handler().postDelayed(() -> connectToSession(), 2000);
            connectToSession();
        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls",
                    RC_VIDEO_APP_PERM, perms);
        }
    }

    public void connectToSession() {
        mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
        mSession.setSessionListener(this);
        mSession.connect(TOKEN);
    }

    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG, "Session Connected");
        startToPublish();
    }

    public void startToPublish() {

        */
/*mPublisher = new Publisher.Builder(this)
                .build();
        mPublisher.setPublisherListener(this);
        publisherFrame.addView(mPublisher.getView());
        mPublisher = new Publisher.Builder(this)
                .name("Star")
                .audioTrack(true)
                .frameRate(Publisher.CameraCaptureFrameRate.FPS_15)
                .resolution(Publisher.CameraCaptureResolution.HIGH)
                .videoTrack(true)
                .build();

        mPublisher.startPreview();*//*


        mPublisher = new Publisher.Builder(this)
                .capturer(new CustomVideoCapturer(this, Publisher.CameraCaptureResolution.HIGH, Publisher.CameraCaptureFrameRate.FPS_30))
                .name(LocalStorage.getUserId())
                .build();
        mPublisher.setPublisherListener(this);

        // set publisher video style to fill view
        mPublisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);
        mSession.publish(mPublisher);

        progressBar.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
    }

    public void startLiveNow(View view) {
        startButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        callGoLiveAPI();
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Session Disconnected");
        mPublisher.getCapturer().stopCapture();
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received");
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "Session error: " + opentokError.getMessage());
        progressBar.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
        startButton.setClickable(false);
        startButton.setBackground(getResources().getDrawable(android.R.color.transparent));
        startButton.setText(R.string.cannot_publish);

    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamCreated");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamDestroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "Publisher error: " + opentokError.getMessage());
        progressBar.setVisibility(View.GONE);
        */
/*startButton.setVisibility(View.VISIBLE);
        startButton.setClickable(false);
        startButton.setBackground(getResources().getDrawable(android.R.color.transparent));
        startButton.setText(R.string.cannot_publish);*//*

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                windowFullScreen();
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
        }
        return false;
    }

    private void callGoLiveAPI() {
        getGoLiveUrl = Url.GET_GO_LIVE_SESSION + feedSelectedId;
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(getGoLiveUrl, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> handleGoLiveAPIResponse(response, error));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGoLiveAPIResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    String type = jsonObject.has("type") ? jsonObject.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObject.has("msg") ? jsonObject.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                        progressBar.setVisibility(View.INVISIBLE);
                    } else if (type.equalsIgnoreCase("ok")) {
                        progressBar.setVisibility(View.INVISIBLE);
                        llParent.setVisibility(View.INVISIBLE);
                        recyclerComments.setVisibility(View.VISIBLE);
                        publisherFrame.setVisibility(View.VISIBLE);
                        questionButton.setVisibility(View.VISIBLE);
                        swapCameraButton.setVisibility(View.VISIBLE);
                        ivLiveImage.setVisibility(View.VISIBLE);
                        linearLayoutReactions.setVisibility(View.VISIBLE);
                        tvAllQuestions.setVisibility(View.VISIBLE);
                        tvNoOfViewers.setVisibility(View.VISIBLE);
                        publisherFrame.addView(mPublisher.getView());
                        if (mPublisher.getView() instanceof GLSurfaceView) {
                            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(false);
                        }

                        windowFullScreen();

                        JSONObject jsonObjectMessage = jsonObject.getJSONObject("msg");
                        SESSION_ID = jsonObjectMessage.getString("session_id");
                        TOKEN = jsonObjectMessage.getString("presenter_token_id");
                        API_KEY = jsonObjectMessage.getString("api_key");
                        ARCHIVE = jsonObjectMessage.getString("archive");
                        BROADCAST = jsonObjectMessage.getString("broadcast");
                        HLSBROADCASTURL = jsonObjectMessage.getString("hlsbroadcasturl");
                        IS_BROADCASTED = jsonObjectMessage.getBoolean("broadcasted");
                        Log.d("Session Data Values", String.valueOf(IS_BROADCASTED));
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showPauseSessionDialog(Context context) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(getResources().getString(R.string.pause_dialog_title))
                .setMessage(getResources().getString(R.string.pause_dialog_description))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (mPublisher != null && mSession != null) {
                        publisherFrame.removeAllViews();
                        progressBar.setVisibility(View.VISIBLE);
                        callPauseLiveSessionApi();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                    windowFullScreen();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void callPauseLiveSessionApi() {
        getPauseLiveUrl = Url.GET_PAUSE_LIVE_SESSION + feedSelectedId;
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(getPauseLiveUrl, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> handlePauseLiveAPIResponse(response, error));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePauseLiveAPIResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    String type = jsonObject.has("type") ? jsonObject.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObject.has("msg") ? jsonObject.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObjectMessage = jsonObject.getJSONObject("msg");
                        IS_BROADCASTED = jsonObjectMessage.getBoolean("broadcasted");
                        if (!IS_BROADCASTED) {
                            mSession.onPause();
                        }
                        Log.d("Session Data Values", SESSION_ID + " " + TOKEN + " " + API_KEY + " " +
                                "\n" + ARCHIVE + " " + BROADCAST + " " + HLSBROADCASTURL + " " + IS_BROADCASTED);
                    }
                }
            }
            progressBar.setVisibility(View.INVISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void resumeSession() {
        if (mSession != null) {
            mSession.onResume();
            callGoLiveAPI();
        }
    }

    public void channelConnect() {

        PusherOptions options = new PusherOptions();
        options.setCluster("ap2");
        Pusher pusher = new Pusher("21e75ea486e129bb3f36", options);

        Channel channel = pusher.subscribe("channel_publish_" + feedSelectedId);

        channel.bind("comment", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                System.out.println(data + " " + "This was a Comment");
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    channelUser = jsonObject.getString("name");
                    if (channelUser.matches("[0-9]+") && channelUser.length() > 5) {
                        channelUser = Utility.getMobileStrikeOutMiddle(channelUser);
                    }
                    channelComment = jsonObject.getString("comment");
                    Log.d("Comment Json String: ", channelComment);
                    updateMessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //abcd.setText(data + " " + "THis was a comment");
            }
        });

        channel.bind("question", (channelName, eventName, data) -> {
                    System.out.println(data);
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        channelUser = jsonObject.getString("name");
                        if (channelUser.matches("[0-9]+") && channelUser.length() > 5) {
                            channelUser = Utility.getMobileStrikeOutMiddle(channelUser);
                        }
                        channelComment = jsonObject.getString("message");
                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), channelComment, Snackbar.LENGTH_INDEFINITE)
                                .setAction("Dismiss", view ->
                                        Snackbar.make(view, "Action!", Snackbar.LENGTH_SHORT)
                                                .dismiss()).setActionTextColor(getResources().getColor(R.color.colorAccent));
                        View view = snack.getView();
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                        params.gravity = Gravity.TOP;
                        view.setLayoutParams(params);
                        snack.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        );

        channel.bind("info", (channelName, eventName, data) -> {
                    System.out.println(data);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.has("stats")) {
                                JSONObject jsonObject1 = jsonObject.getJSONObject("stats");
                                tvNoOfViewers.setText(jsonObject1.getString("views"));
                                reactionHeart.setText(jsonObject1.getString("reaction_HEART"));
                                reactionLaugh.setText(jsonObject1.getString("reaction_LAUGH"));
                                reactionClap.setText(jsonObject1.getString("reaction_CLAPS"));
                                reactionWow.setText(jsonObject1.getString("reaction_WOW"));
                            } else if (jsonObject.has("status")) {
                                status = jsonObject.getString("status");
                                sessionStatus(status);
                            }
                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }
                    });
                }
        );
        pusher.connect();

    }

    public void sessionStatus(String status) {
        switch (status) {
            case "PAUSED":
                new Handler(Looper.getMainLooper()).post(() -> {
                    pauseButton.setVisibility(View.INVISIBLE);
                    resumeButton.setVisibility(View.VISIBLE);
                    pauseButtonText.setVisibility(View.VISIBLE);
                    micOffButton.setVisibility(View.INVISIBLE);
                    micOnButton.setVisibility(View.INVISIBLE);
                    pauseButtonText.setText(getResources().getString(R.string.resume_live_session));
                    windowFullScreen();
                });
                break;
            case "STARTED":
                new Handler(Looper.getMainLooper()).post(() -> {
                    pauseButton.setVisibility(View.VISIBLE);
                    resumeButton.setVisibility(View.INVISIBLE);
                    pauseButtonText.setVisibility(View.VISIBLE);
                    presenterLiveCloseButton.setVisibility(View.VISIBLE);
                    micOnButton.setVisibility(View.VISIBLE);
                    pauseButtonText.setText(getResources().getString(R.string.pause_live_session));

                    windowFullScreen();
                });
                break;
            case "COMPLETED":
                new Handler(Looper.getMainLooper()).post(this::finish);
                break;
        }
    }

        //The reaction event shall come in handy when animations shall be added.
        */
/*channel.bind("reaction", (channelName, eventName, data) -> {
                    System.out.println(data);
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        String abc = jsonObject.getString("reaction");
                        Log.d("Reaction Json String: ", abc);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //abcd.setText(data + " " + "THis was a reaction");
                }
        );*//*


    private void updateMessage() {
        String userName = channelUser;
        String message = channelComment;
        generateMessageJSON(userName, message);
        new Handler(Looper.getMainLooper()).post(() -> {
            adapter.notifyDataSetChanged();
            scrollToBottom();
        });
    }

    private void generateMessageJSON(String senderName, String message) {
        String decoded = decode(message);
        if (decoded == null) return;
        try {
            jsonObjectResponse = new JSONObject();
            jsonObjectResponse.put("senderName", senderName);
            jsonObjectResponse.put("message", decoded);
            messageData = new MessageData(jsonObjectResponse);
            arrayListMessageData.add(messageData);
            //Collections.reverse(arrayListMessageData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String decode(String message) {
        // Receiving side
        try {
            byte[] data = Base64.decode(message, Base64.DEFAULT);
            return new String(data, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return message;
        }
    }

    @Override
    public void onBackPressed() {
        if (status != null) {
            if (status.equals("PREPARED")) {
                if (mPublisher != null && mSession != null) {
                    mSession.onPause();
                    mPublisher.getCapturer().stopCapture();
                    mSession.unpublish(mPublisher);
                    mPublisher.destroy();
                    mSession.disconnect();
                }
                finish();
            } else if (status.equals("STARTED")) {
                showCloseSessionDialog(LiveNowStartActivity.this);
            } else if (status.equals("PAUSED")) {
                showCloseSessionDialog(LiveNowStartActivity.this);
            } else if (mPublisher != null) {
                showCloseSessionDialog(LiveNowStartActivity.this);
            }
        }
        //super.onBackPressed();
    }

    private void showCloseSessionDialog(Context context) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(getResources().getString(R.string.close_dialog_title))
                .setMessage(getResources().getString(R.string.close_dialog_description))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (mPublisher != null && mSession != null) {
                        mSession.onPause();
                        mPublisher.getCapturer().stopCapture();
                        mSession.unpublish(mPublisher);
                        mPublisher.destroy();
                        mSession.disconnect();
                        publisherFrame.removeAllViews();
                        progressBar.setVisibility(View.VISIBLE);
                        pauseButton.setVisibility(View.INVISIBLE);
                        pauseButtonText.setVisibility(View.INVISIBLE);
                        swapCameraButton.setVisibility(View.INVISIBLE);
                        questionButton.setVisibility(View.INVISIBLE);
                        tvAllQuestions.setVisibility(View.INVISIBLE);
                        micOnButton.setVisibility(View.INVISIBLE);
                        micOffButton.setVisibility(View.INVISIBLE);
                        recyclerComments.setVisibility(View.GONE);
                        stopLiveStreamAPI();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                    windowFullScreen();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void stopLiveStreamAPI() {
        getStopLiveUrl = Url.GET_STOP_LIVE_SESSION + feedSelectedId;
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(getStopLiveUrl, Request.Method.GET, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                handleStopLiveAPIResponse(response, error);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleStopLiveAPIResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    JSONObject jsonObject = new JSONObject(response);
                    String type = jsonObject.has("type") ? jsonObject.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObject.has("msg") ? jsonObject.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObjectMessage = jsonObject.getJSONObject("msg");
                        finish();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void startCountDown() {
        try {
            Date formattedGivenDateTime = sourceDateFormat.parse(liveNowData.time);
            long gameExpireMilliseconds = formattedGivenDateTime.getTime();
            long currentTimeMilliseconds = System.currentTimeMillis();
            long difference = gameExpireMilliseconds - currentTimeMilliseconds;

            countDownTimer = new CountDownTimer(difference, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeft = GameDateValidation.getTimeLeft(millisUntilFinished);
                    Log.e("Start Time", timeLeft);
                    tvCountDownTime.setText(timeLeft);
                }

                @Override
                public void onFinish() {
                    tvCountDownTime.setVisibility(View.INVISIBLE);
                    getSessionData();

                }
            }.start();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void cancelCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    */
/**
     * This method sets the window to fullscreen (i.e. immersive view)
     *//*

    public void windowFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public void swapCamera(View view) {
        mPublisher.cycleCamera();
    }

    public void openQuestionsPage(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("StreamId", String.valueOf(feedSelectedId));
        startActivity(new Intent(this, ViewAllQuestionsActivity.class).putExtra("LiveStreamId", bundle));
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        */
/*if (mSession != null) {
            mSession.onPause();
        }*//*

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelCountDown();
        if (mPublisher != null) {
            mPublisher.getCapturer().stopCapture();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (status != null) {
            sessionStatus(status);
        }
    }
}*/
