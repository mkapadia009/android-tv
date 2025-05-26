/*
package com.app.itap.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.itap.API.APIManager;
import com.app.itap.API.APIRequest;
import com.app.itap.API.Url;
import com.app.itap.R;
import com.app.itap.holder.MessageHolder;
import com.app.itap.structure.LiveNowData;
import com.app.itap.structure.MessageData;
import com.app.itap.structure.User;
import com.app.itap.utils.AlertUtils;
import com.app.itap.utils.ExoUtil;
import com.app.itap.utils.LocalStorage;
import com.app.itap.utils.Utility;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.util.HttpAuthorizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LiveStreamActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    public static final String LIVE_SESSION_DATA = "live_session_data";
    LiveNowData liveNowData;

    int feedId = 0;
    String liveUrl = "";

    LinearLayout llChatBox;
    LinearLayout llRightBottomOptions;

    RecyclerView rvComments;
    LinearLayoutManager mLinearLayoutManager;

    Pusher pusher;
    Channel channel, viewCountChannel;

    EditText etChatBox;
    JSONObject jsonObjectResponse;
    MessageData messageData;
    KRecyclerViewAdapter adapter;

    ProgressBar progressBar;

    ImageButton closeButton, askAQuestion, shareButton;

    TextView tvNoOfViewers, streamStatus;

    WebView playerContainer;

    boolean isPresenterLoggedin;
    ArrayList<MessageData> arrayListMessageData = new ArrayList<>();

    String channelUser, channelComment, messageType, status;

    String getSendMessageUrl = "";
    String resumedUrl = "";

    private SimpleExoPlayer player;
    private PlayerView playerView;
    Button reactionHeart, reactionClap, reactionLaugh, reactionWow;

    private final String CLAPS = "👏🏻";
    private final String HEART = "❤️";
    private final String LAUGH = "🤣";
    private final String WOW = "😮";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.activity_live_stream);
        init();

        if (getIntent().getAction() != null && getIntent().getData() != null) {
            Intent appLinkIntent = getIntent();
            String appLinkAction = appLinkIntent.getAction();
            Uri appLinkData = appLinkIntent.getData();
            Uri uri = Uri.parse(String.valueOf(appLinkData));
            String streamId = String.valueOf(Integer.parseInt(uri.getQueryParameter("live_sesson")));
        }
    }

    private void init() {
        if (getIntent().getExtras().getBundle("Bundle") != null){
            liveNowData = getIntent().getExtras().getBundle("Bundle").getParcelable(LIVE_SESSION_DATA);
        }
        llChatBox = findViewById(R.id.llChatBox);
        llRightBottomOptions = findViewById(R.id.llRightBottomOptions);
        rvComments = findViewById(R.id.rvComments);
        etChatBox = findViewById(R.id.etChatBox);
        etChatBox.setImeOptions(EditorInfo.IME_ACTION_SEND);
        etChatBox.setRawInputType(InputType.TYPE_CLASS_TEXT);
        playerView = findViewById(R.id.video_container);
        streamStatus = findViewById(R.id.stream_status_text);
        //playerContainer = findViewById(R.id.video_container);
        closeButton = findViewById(R.id.close_live_button);
        askAQuestion = findViewById(R.id.question_button);
        shareButton = findViewById(R.id.share_button);
        progressBar = findViewById(R.id.progressBar);
        reactionHeart = findViewById(R.id.button_reaction_love);
        reactionClap = findViewById(R.id.button_reaction_clap);
        reactionLaugh = findViewById(R.id.button_reaction_laugh);
        reactionWow = findViewById(R.id.button_reaction_wow);
        tvNoOfViewers = findViewById(R.id.tv_number_of_viewers_subscriber);

        progressBar.setVisibility(View.INVISIBLE);
        streamStatus.setVisibility(View.INVISIBLE);

        */
/*playerContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        playerContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        playerContainer.setHapticFeedbackEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }*//*


        askAQuestion.setOnClickListener(view -> showAskAQuestionDialog(LiveStreamActivity.this, liveNowData.title, liveNowData.id));

        */
/**
         * Share link through intent.
         *//*

        shareButton.setOnClickListener(view -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hello there!");
                String shareMessage = "Join this Live Session with me.\n\n";
                shareMessage = shareMessage + liveNowData.shareUrl;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share Link"));
            } catch (Exception e) {
                //e.toString();
            }
        });

        isPresenterLoggedin = LocalStorage.getValue(LocalStorage.IS_PRESENTER_LOGGED_IN, false, Boolean.class);
        etChatBox.setOnEditorActionListener(this);
        //setLayout();
        getSessionData();
        channelConnect();
        initializeRecyclerView();
        windowFullScreen();

        closeButton.setOnClickListener(view -> showCloseSessionDialog(LiveStreamActivity.this));

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

    private void setLayout() {
        if (isPresenterLoggedin) {
            llChatBox.setVisibility(View.GONE);
            llRightBottomOptions.setVisibility(View.GONE);
        }
    }

    private void showAskAQuestionDialog(Context context, String title, int id) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_ask_a_question, null);

        final TextView tv_message = (TextView) view.findViewById(R.id.tvtitle);
        final EditText et_remark = (EditText) view.findViewById(R.id.et_remark);
        Button bt_cancel = (Button) view.findViewById(R.id.btcancel);
        Button bt_send = (Button) view.findViewById(R.id.btsend);

        tv_message.setText(title);
        final Dialog alertDialogBuilder = new Dialog(context);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setContentView(view);
        //final Dialog alertDialog_decline = alertDialogBuilder.create();
        alertDialogBuilder.show();

        bt_cancel.setOnClickListener(view1 -> alertDialogBuilder.dismiss());

        bt_send.setOnClickListener(view12 -> {
            String s_remark = et_remark.getText().toString();
            sendQuestion(context, s_remark, id);
            alertDialogBuilder.dismiss();
        });
    }

    private void sendQuestion(Context context, String remark, int id) {
        Map<String, String> params = new HashMap<>();
        params.put("message", remark);
        params.put("ID", String.valueOf(id));
        com.app.itap.utils.Log.d("params", params.toString());
        APIRequest apiRequest = new APIRequest(Url.ASK_QUESTION, Request.Method.POST, params, null, context);
        apiRequest.showLoader = false;
        APIManager.request(apiRequest, (response, error, headers, statusCode) -> handleAskQuestionResponse(response, error, statusCode));
    }

    private void handleAskQuestionResponse(String response, Exception error, int statusCode) {
        try {
            if (error != null) {
                // showError(error.getMessage());
            } else {
                if (response != null) {
                    com.app.itap.utils.Log.e("response", response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        Log.d("Ask a question Message:", message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        Log.d("Ask a question Message:", String.valueOf(jsonArrayMsg));
                        //AlertUtils.showAlert("", getString(R.string.ask_a_q_desc), null, this, null);
                        showDarkDialog(LiveStreamActivity.this);
                        windowFullScreen();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showDarkDialog(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_dark, null);

        Button bt_ok = (Button) view.findViewById(R.id.btnOk);
        final Dialog alertDialogBuilder = new Dialog(context);
        alertDialogBuilder.setContentView(view);
        alertDialogBuilder.show();

        bt_ok.setOnClickListener(view1 -> alertDialogBuilder.dismiss());

        */
/*AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(getResources().getString(R.string.ask_a_q_desc))
                .setNegativeButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    windowFullScreen();
                })
                .show();*//*

    }

    public void getSessionData() {
        if (liveNowData != null) {
            feedId = liveNowData.id;
            liveUrl = liveNowData.liveStreamUrl;
            if (!liveUrl.isEmpty()) {
                play(liveUrl);
                reactionHeart.setText(String.valueOf(liveNowData.reaction_HEART));
                reactionLaugh.setText(String.valueOf(liveNowData.reaction_LAUGH));
                reactionClap.setText(String.valueOf(liveNowData.reaction_CLAPS));
                reactionWow.setText(String.valueOf(liveNowData.reaction_WOW));
                tvNoOfViewers.setText(String.valueOf(liveNowData.viewers));
                */
/*try {
                    InputStream is = getAssets().open("video_loader.html");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    String str = new String(buffer);
                    str = str.replace("load_my_string", liveUrl);
                    is.close();
                    playerContainer.loadUrl("file:///android_asset/video_loader.html");
                } catch (IOException e) {
                    e.printStackTrace();
                }*//*

            }
            Log.d("Logged URL:", liveUrl);
        }
    }

    private String encode(String message) {
        byte[] data = new byte[0];
        try {
            data = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return Base64.encodeToString(data, Base64.DEFAULT);
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

    private void initializeRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        //mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        rvComments.setLayoutManager(mLinearLayoutManager);
        adapter = new KRecyclerViewAdapter(this, arrayListMessageData, (parent, i) -> {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_received, parent, false);
            return new MessageHolder(layoutView);
        }, (kRecyclerViewHolder, o, i) -> {
            //No click handler needed for comments method = comment
        });
        rvComments.setAdapter(adapter);
        getSessionMessages();
    }

    public void getSessionMessages() {
        Map<String, String> params = new HashMap<>();
        APIRequest apiRequest = new APIRequest(Url.GET_SESSION_MESSAGES + "&ID=" + liveNowData.id, Request.Method.GET, params, null, this);
        apiRequest.showLoader = false;
        APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
            handleSessionMessageAPIResponse(response, error);
        });
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
                rvComments.smoothScrollToPosition(arrayListMessageData.size() - 1);
            }
        }, 300);
        */
/*if (arrayListMessageData.size() > 0) {
            rvComments.smoothScrollToPosition(arrayListMessageData.size() - 1);
        }*//*

    }

    public void channelConnect() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-AUTH-TOKEN", LocalStorage.getToken());
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Accept", "application/json");

        HttpAuthorizer authorizer = new HttpAuthorizer(Url.AUTHENTICATE_SUBSCRIBER);
        authorizer.setHeaders(headers);
        PusherOptions options = new PusherOptions().setAuthorizer(authorizer);
        options.setCluster("ap2");
        pusher = new Pusher("21e75ea486e129bb3f36", options);

        channel = pusher.subscribe("channel_publish_" + feedId);

        viewCountChannel = pusher.subscribePrivate("private-mypresence-" + feedId + "-" + LocalStorage.getUserId(), new PrivateChannelEventListener() {
            @Override
            public void onAuthenticationFailure(String message, Exception e) {
                Log.d("Failure", message);
            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {
                Log.d("onSubscriptionSucceeded", channelName);
            }

            @Override
            public void onEvent(String channelName, String eventName, String data) {
                Log.d("onEvent", channelName + " " + eventName + " " + data);
            }
        });

        channel.bind("comment", (channelName, eventName, data) -> {
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
        });

        channel.bind("info", (channelName, eventName, data) -> {
            System.out.println(data);
            new Handler(Looper.getMainLooper()).post(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.has("stats")) {
                                JSONObject jsonObject1 = jsonObject.getJSONObject("stats");
                                reactionHeart.setText(jsonObject1.getString("reaction_HEART"));
                                reactionLaugh.setText(jsonObject1.getString("reaction_LAUGH"));
                                reactionClap.setText(jsonObject1.getString("reaction_CLAPS"));
                                reactionWow.setText(jsonObject1.getString("reaction_WOW"));
                                tvNoOfViewers.setText(jsonObject1.getString("views"));
                            } else if (jsonObject.has("status")) {
                                status = jsonObject.getString("status");
                                resumedUrl = jsonObject.getString("url").replace("\\", "");
                                sessionStatus(status);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //abcd.setText(data + " " + "This was a question");
                    }
            );
        });

        //The reaction event shall come in handy when animations shall be added.
        */
/*
         *Commented as all reactions are handled in comment event itself.
         *//*

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


        pusher.connect();
    }

    public void sessionStatus(String status) {
        switch (status) {
            case "COMPLETED":
                */
/*new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(LiveStreamActivity.this, "This Live Stream has ended.", Toast.LENGTH_LONG).show();
                });
                finish();*//*

                break;
            case "PAUSED":
                // Do Pause Functionality
                */
/*playerView.setVisibility(View.INVISIBLE);
                streamStatus.setVisibility(View.VISIBLE);
                windowFullScreen();*//*

                break;
            case "STARTED":
                playerView.setVisibility(View.VISIBLE);
                streamStatus.setVisibility(View.INVISIBLE);
                windowFullScreen();
                play(resumedUrl);
                break;
        }
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

    private void updateMessage() {
        Gson gson = new Gson();
        String userdata = LocalStorage.getValue(LocalStorage.KEY_USER_DATA, "", String.class);
        User user = gson.fromJson(userdata, User.class);
        if (user != null) {
            String userName = channelUser;
            String message = channelComment;
            generateMessageJSON(userName, message);
            new Handler(Looper.getMainLooper()).post(() -> {
                adapter.notifyDataSetChanged();
                scrollToBottom();
            });
        }
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
                    // Do nay handling/prompt here
                    cleanup();
                    pusher.unsubscribe("private-mypresence-" + feedId + "-" + LocalStorage.getUserId());
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                    windowFullScreen();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        switch (actionId) {
            case EditorInfo.IME_ACTION_SEND:
                if (!etChatBox.getText().toString().isEmpty()) {
                    String encoded = encode(etChatBox.getText().toString());
                    messageType = "text";
                    if (encoded != null) {
                        sendMessageViaAPI(encoded, messageType);
                    }
                } else {
                    Toast.makeText(this, "Comment Box can't be left empty.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return false;
    }

    public void sendMessageViaAPI(String message, String type) {
        getSendMessageUrl = Url.SEND_PUSHER_MESSAGE;
        try {
            Map<String, String> params = new HashMap<>();
            params.put("ID", String.valueOf(feedId));
            params.put("comment", message);
            params.put("type", type);
            APIRequest apiRequest = new APIRequest(getSendMessageUrl, Request.Method.POST, params, null, this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> {
                handleSendPusherMessageAPIResponse(response, error);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSendPusherMessageAPIResponse(@Nullable String response, @Nullable Exception error) {
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
                        int id = jsonObjectMessage.getInt("sent");
                        String receivedMessage = jsonObjectMessage.getString("msg");
                        if (id == 1) {
                            //Toast.makeText(this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                            etChatBox.setText("");
                            windowFullScreen();
                        } else {
                            showError("Something went Wrong");
                        }
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

    @Override
    protected void onPause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
        } else {
            play(liveUrl);
        }
    }

    @Override
    protected void onDestroy() {
        cleanup();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        showCloseSessionDialog(LiveStreamActivity.this);
        //super.onBackPressed();
    }

    */
/*
     * ExoPlayer
     *//*

    private void play(String url) {
        // Data source
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        AdaptiveTrackSelection.Factory adaptiveTrackSelection = new AdaptiveTrackSelection.Factory();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);
        // TODO: - Build drm session manager here...
        MediaSource mediaSource = ExoUtil.buildMediaSource(Uri.parse(url), dataSourceFactory, null);

        // Player
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector(adaptiveTrackSelection));
        player.addListener(new Player.EventListener() {
            @Override
            public void onLoadingChanged(boolean isLoading) {
                if (isLoading) {
                    //progressBar.setVisibility(View.VISIBLE);
                } else {
                    //progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if (error != null && error.getCause() instanceof BehindLiveWindowException) {
                    play(liveUrl);
                }
                Log.e("LiveStream", "Unable to play the video. Error: "
                        + error.getMessage());
                if (error.getMessage().equals("com.google.android.exoplayer2.upstream.HttpDataSource$InvalidResponseCodeException: Response code: 404")) {
                    if (status != null) {
                        switch (status) {
                            case "PAUSED":
                                // Do Pause functionality
                                playerView.setVisibility(View.INVISIBLE);
                                streamStatus.setVisibility(View.VISIBLE);
                                windowFullScreen();
                                break;
                            case "STARTED":
                                play(resumedUrl);
                                break;
                            case "COMPLETED":
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    playerView.setKeepScreenOn(false);
                                    Toast.makeText(LiveStreamActivity.this, "This Live Stream was ended.", Toast.LENGTH_LONG).show();
                                });
                                finish();
                                break;
                            default:
                                Toast.makeText(LiveStreamActivity.this, "This Live Stream was ended.", Toast.LENGTH_LONG).show();
                                new Handler().postDelayed(() -> finish(), 2000);
                                break;
                        }
                    }
                }

                */
/*try {
                    AlertUtils.showAlert("Error", "Unable to play the video. Error: "
                                    + error.getMessage(), null,
                            LiveStreamActivity.this, isPositiveAction -> {
                                //finish();
                            });
                } catch (Exception e) {
                    Log.e("LiveStream", "Error: " + e.getMessage());
                }*//*


            }
        });

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady && playbackState == Player.STATE_ENDED) {
                    // media ended
                    playerView.setKeepScreenOn(false);
                    finish();
                    Toast.makeText(LiveStreamActivity.this, "This Live Stream was finished.", Toast.LENGTH_LONG).show();
                } else if (playWhenReady && playbackState == Player.STATE_BUFFERING || playWhenReady && playbackState == Player.STATE_READY) {
                    playerView.setKeepScreenOn(true);
                }
            }
        });

        // Prepare player with media source
        player.prepare(mediaSource);
        // Start playing as the player is prepared
        player.setPlayWhenReady(true);
        // Set player
        playerView.setPlayer(player);
    }

    private void cleanup() {
        playerView.setPlayer(null);
        if (player != null) {
            player.stop(true);
            player.clearVideoSurface();
            player.release();
            player = null;
        }
    }

    public void sendReaction(View view) {
        String reaction = "";
        String type = "";
        int id = view.getId();
        if (id == R.id.button_reaction_clap) {
            reaction = CLAPS;
            type = "CLAPS";
        } else if (id == R.id.button_reaction_love) {
            reaction = HEART;
            type = "HEART";
        } else if (id == R.id.button_reaction_laugh) {
            reaction = LAUGH;
            type = "LAUGH";
        } else if (id == R.id.button_reaction_wow) {
            reaction = WOW;
            type = "WOW";
        }
        if (reaction.isEmpty()) return;
        String encoded = encode(reaction);
        if (encoded != null) {
            sendMessageViaAPI(encoded, type);
        }
    }
}
*/
