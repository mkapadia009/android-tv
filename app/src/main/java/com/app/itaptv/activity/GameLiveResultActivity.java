package com.app.itaptv.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.custom_interface.WalletCallback;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.GameDateValidation;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Wallet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.app.itaptv.activity.WalletActivity.KEY_WALLET_BALANCE;
import static com.app.itaptv.activity.WalletActivity.REQUEST_CODE;

public class GameLiveResultActivity extends BaseActivity {
    public static String GAME_DATA = "gameData";
    public static String TOTAL_GAME_COINS = "totalGameCoins";
    boolean GAME_Status = false;
    GameData gameData;
    public static String TAG = GameLiveResultActivity.class.getName();

    TextView tvWonCoins;
    TextView tvBalance;
    //TextView tvThankplay;
    //TextView tvLuckydraw;
    ProgressBar pbWalletBalance;
    ImageView ivWinLoseImage;
    LinearLayout llBalance;
    LinearLayout llParent;
    LinearLayout llWalletBalance;
    RelativeLayout rlScore;
    //LinearLayout llLiveGameWinnerText;
    TableLayout tblLayout;
    TableRow trRow1;
    Button btLeaderboard;
    Button btShare;
    Button btRedeemCoins;
    Button btExit;


    long walletBalance = 0L;
    long totalGameCoins = 0L;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);
        init();
    }

    /**
     * Initialize All Values
     */
    private void init() {
        gameData = (GameData) getIntent().getExtras().getBundle("Bundle").getParcelable(GAME_DATA);
        totalGameCoins = Long.parseLong(getIntent().getExtras().getBundle("Bundle").getString(TOTAL_GAME_COINS));
        tvWonCoins = findViewById(R.id.tvWonCoins);
        tvBalance = findViewById(R.id.tvBalance);
        //tvThankplay = findViewById(R.id.tvThankplay);
        //tvLuckydraw = findViewById(R.id.tvLuckydraw);
        pbWalletBalance = findViewById(R.id.pbWalletBalance);
        ivWinLoseImage = findViewById(R.id.ivWinLoseImage);
        llBalance = findViewById(R.id.llBalance);
        llParent = findViewById(R.id.llParent);
        llWalletBalance = findViewById(R.id.llWalletBalance);
        rlScore = findViewById(R.id.rlScore);
        //llLiveGameWinnerText = findViewById(R.id.llLiveGameWinnerText);
        tblLayout = findViewById(R.id.tblLayout);
        trRow1 = findViewById(R.id.trRow1);
        btLeaderboard = findViewById(R.id.btLeaderboard);
        btShare = findViewById(R.id.btShare);
        btRedeemCoins = findViewById(R.id.btRedeemCoins);
        btExit = findViewById(R.id.btExit);

        btLeaderboard.setOnClickListener(this::showLeaderBoard);
        btShare.setOnClickListener(this::shareGame);
        btRedeemCoins.setOnClickListener(this::redeemGame);
        btExit.setOnClickListener(this::exitGame);


        setImageSize();
        setGameResult();
    }

    private void setImageSize() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int imageHeight = (int) (displaymetrics.heightPixels / 3.5);
        int layoutMargin = (int) (imageHeight / 1.1);
        int layoutHeight = (int) (imageHeight / 1.4);
        int layoutWidth = (int) (displaymetrics.widthPixels / 1.5);
        ivWinLoseImage.getLayoutParams().height = imageHeight;
        llBalance.getLayoutParams().height = layoutHeight;
        llBalance.getLayoutParams().width = layoutWidth;
        tblLayout.getLayoutParams().width = layoutWidth;


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llParent.getLayoutParams();
        params.setMargins(0, layoutMargin, 0, 0);
        llParent.setLayoutParams(params);
    }


    private void setGameResult() {
        trRow1.setVisibility(View.GONE);
        rlScore.setVisibility(View.GONE);

        if (totalGameCoins == 0) {
            ivWinLoseImage.setImageResource(R.drawable.you_lose);
            //llLiveGameWinnerText.setVisibility(View.GONE);
        } else {
            ivWinLoseImage.setImageResource(R.drawable.you_won);
            //llLiveGameWinnerText.setVisibility(View.VISIBLE);
            tvWonCoins.setText(String.valueOf(totalGameCoins));
            if (!gameData.announceWinnerAt.equalsIgnoreCase("")) {
                String announcedate = GameDateValidation.getDateTimeLabelAT(this, gameData.announceWinnerAt);
                //tvLuckydraw.setText(String.format(getString(R.string.text_lucky_draw), announcedate));
            } else {
                //tvLuckydraw.setVisibility(View.GONE);
            }
        }
        tvWonCoins.setText(String.valueOf(totalGameCoins));
        JSONObject object = new JSONObject();
        try {
            String activity_id = LocalStorage.getValue(LocalStorage.KEY_ACTIVITY_ID, "", String.class);
            object.put("completed", "completed");
            object.put("activity_id", activity_id);
            Analyticals.CallPlayedActivity(Analyticals.GAME_ACTIVITY_TYPE, gameData.id, "", "", this, object.toString(), new Analyticals.AnalyticsResult() {
                @Override
                public void onResult(boolean success, String acitivity_id, @Nullable String error) {
                    if (success) {
                        gameData.playedGame = true;
                        GAME_Status = true;
                        addWalletCoins();
                        /*if (totalGameCoins != 0) {
                            addWalletCoins();
                        } else {
                            llWalletBalance.setVisibility(View.INVISIBLE);
                            pbWalletBalance.setVisibility(View.GONE);
                        }*/

                    } else {
                        GAME_Status = false;
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            GAME_Status = false;
        }

    }

    private void addWalletCoins() {
        totalGameCoins = totalGameCoins;
        pbWalletBalance.setVisibility(View.VISIBLE);
        Wallet.earnCoins(this, Integer.parseInt(gameData.id), gameData.postTitle, Wallet.FLAG_LIVE_GAME, totalGameCoins, new WalletCallback() {
            @Override
            public boolean onResult(boolean success, @Nullable String error, long coins, long diamonds, long creditedCoins, JSONArray historyData, int historyCount) {
                handleApp42Response(success, error, gameData.id, totalGameCoins);
                return success;
            }
        });

    }

    private void handleApp42Response(boolean success, String error, String id, long totalCoinsWon) {

        if (success) {
            Wallet.getWalletBalance(this, new WalletCallback() {
                @Override
                public boolean onResult(boolean success, @Nullable String error, long coins, long diamonds, long creditedCoins, JSONArray historyData, int historyCount) {
                    handleApp42Response(success, error, coins);
                    return success;
                }
            });

        } else {
            String errorMessage = (error == null) ? getString(R.string.GENERIC_API_ERROR_MESSAGE) : error;
            AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, this, null);
        }
    }

    private void handleApp42Response(boolean success, String error, long coins) {
        if (success) {
            pbWalletBalance.setVisibility(View.GONE);
            String totalCoins = String.valueOf(coins);
            llWalletBalance.setVisibility(View.VISIBLE);
            tvBalance.setText(Html.fromHtml(totalCoins));

            walletBalance = coins;

        } else {
            pbWalletBalance.setVisibility(View.GONE);
            String errorMessage = error == null ? getString(R.string.GENERIC_API_ERROR_MESSAGE) : error;
            AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, GameLiveResultActivity.this, null);
        }
    }


    public void exitGame(View view) {
        Intent intent = new Intent();
        intent.putExtra("GameStatus", GAME_Status);
        setResult(GameData.REQUEST_CODE_GAME, intent);
        finish();
    }

    public void shareGame(View view) {
        String appLink = "";
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(Intent.EXTRA_TITLE, getString(R.string.label_itap));
        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.i_won) + tvWonCoins + getString(R.string.iCoins_by_playing) + gameData.postTitle + getString(R.string.on_iTap) + "\n" +
                getString(R.string.play) + gameData.postTitle + getString(R.string.on_iTap) + "\n" +
                getString(R.string.download_iTap_now) + appLink);
        i.setType("*/*");
        startActivity(Intent.createChooser(i, getString(R.string.share_game_result)));
    }

    public void redeemGame(View view) {
        startActivityForResult(new Intent(this, RedeemCoinsActivity.class).putExtra(KEY_WALLET_BALANCE, walletBalance), REQUEST_CODE);
        finish();

    }

    public void showLeaderBoard(View view) {
        startActivity(new Intent(this, LeaderBoardActivity.class));
        finish();
    }
}
