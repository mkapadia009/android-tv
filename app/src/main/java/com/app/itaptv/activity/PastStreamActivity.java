package com.app.itaptv.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.holder.MessageHolder;
import com.app.itaptv.structure.MessageData;
import com.app.itaptv.structure.PastLiveData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.ExoUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PastStreamActivity extends AppCompatActivity {

    public static final String PAST_SESSION_DATA = "past_session_data";

    private ExoPlayer player;
    private PlayerView playerView;

    PastLiveData pastLiveData;

    ImageButton closeButton;
    RecyclerView rvComments;
    LinearLayoutManager mLinearLayoutManager;
    KRecyclerViewAdapter adapter;
    MessageData messageData;
    JSONObject jsonObjectResponse;
    ArrayList<MessageData> arrayListMessageData = new ArrayList<>();

    TextView tvNoOfViewers;

    int feedId = 0;
    String pastUrl = "";

    Button reactionHeart, reactionClap, reactionLaugh, reactionWow;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_stream);
        init();
    }

    public void init() {
        pastLiveData = getIntent().getExtras().getBundle("Bundle").getParcelable(PAST_SESSION_DATA);
        rvComments = findViewById(R.id.past_recycler_view_comments);

        closeButton = findViewById(R.id.past_close_live_button);
        playerView = findViewById(R.id.past_video_container);
        progressBar = findViewById(R.id.progressBar);
        reactionHeart = findViewById(R.id.past_button_reaction_love);
        reactionClap = findViewById(R.id.past_button_reaction_clap);
        reactionLaugh = findViewById(R.id.past_button_reaction_laugh);
        reactionWow = findViewById(R.id.past_button_reaction_wow);
        tvNoOfViewers = findViewById(R.id.past_tv_number_of_viewers_subscriber);

        progressBar.setVisibility(View.INVISIBLE);

        closeButton.setOnClickListener(view -> showCloseSessionDialog(PastStreamActivity.this));

        getSessionData();
        initializeRecyclerView();
        windowFullScreen();
    }

    public void getSessionData() {
        if (pastLiveData != null) {
            feedId = pastLiveData.id;
            pastUrl = pastLiveData.pastLiveStreamUrl;
            if (!pastUrl.isEmpty()) {
                //play(pastUrl);
                reactionHeart.setText(String.valueOf(pastLiveData.reaction_HEART));
                reactionLaugh.setText(String.valueOf(pastLiveData.reaction_LAUGH));
                reactionClap.setText(String.valueOf(pastLiveData.reaction_CLAPS));
                reactionWow.setText(String.valueOf(pastLiveData.reaction_WOW));
                tvNoOfViewers.setText(String.valueOf(pastLiveData.viewers));
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
                }*/
            }
            Log.d("Logged URL:", pastUrl);
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
        APIRequest apiRequest = new APIRequest(Url.GET_SESSION_MESSAGES + "&ID=" + pastLiveData.id, Request.Method.GET, params, null, this);
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

    private void scrollToBottom() {
        new Handler().postDelayed(() -> {
            if (arrayListMessageData.size() > 0) {
                rvComments.smoothScrollToPosition(arrayListMessageData.size() - 1);
            }
        }, 300);
        /*if (arrayListMessageData.size() > 0) {
            rvComments.smoothScrollToPosition(arrayListMessageData.size() - 1);
        }*/
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
     * This method sets the window to fullscreen (i.e. immersive view)
     */
    public void windowFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    /*
     * ExoPlayer
     */
    private void play(String url) {
        // Data source
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DefaultTrackSelector adaptiveTrackSelection = new DefaultTrackSelector(PastStreamActivity.this);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);
        // TODO: - Build drm session manager here...
        MediaSource mediaSource = ExoUtil.buildMediaSource(Uri.parse(url), dataSourceFactory, null);

        // Player
        player = new ExoPlayer.Builder(PastStreamActivity.this).setTrackSelector(adaptiveTrackSelection).build();

        player.addListener(new Player.Listener() {
            @Override
            public void onLoadingChanged(boolean isLoading) {
                if (isLoading) {
                    //progressBar.setVisibility(View.VISIBLE);
                } else {
                    //progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                if (error != null && error.getCause() instanceof BehindLiveWindowException) {
                    play(pastUrl);
                }
                Log.e("LiveStream", "Unable to play the video. Error: "
                        + error.getMessage());
                if (error.getMessage().equals("com.google.android.exoplayer2.upstream.HttpDataSource$InvalidResponseCodeException: Response code: 404")) {
                    Toast.makeText(PastStreamActivity.this, getString(R.string.live_stream_was_finished), Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(() -> {
                        playerView.setKeepScreenOn(false);
                        finish();
                    }, 2000);
                }
            }
        });

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady && playbackState == Player.STATE_ENDED) {
                    // media ended
                    playerView.setKeepScreenOn(false);
                    finish();
                    Toast.makeText(PastStreamActivity.this, getString(R.string.live_stream_was_finished), Toast.LENGTH_LONG).show();
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
            player.setPlayWhenReady(false);
            player.stop(true);
            player.clearVideoSurface();
            player.release();
            player = null;
        }
    }

    private void showCloseSessionDialog(Context context) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(getResources().getString(R.string.past_dialog_title))
                .setMessage(getResources().getString(R.string.past_dialog_description))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Do nay handling/prompt here
                    cleanup();
                    finish();
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                    windowFullScreen();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                windowFullScreen();
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
        }
        return false;
    }*/

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showCloseSessionDialog(PastStreamActivity.this);
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
            play(pastUrl);
        }
        //playerContainer.loadUrl(liveUrl);
        //playerContainer.loadData(tmpVideo, null, null);
    }

    @Override
    protected void onDestroy() {
        cleanup();
        super.onDestroy();
    }

}
