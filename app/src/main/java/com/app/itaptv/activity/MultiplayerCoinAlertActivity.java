package com.app.itaptv.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.app.itaptv.R;

public class MultiplayerCoinAlertActivity extends AppCompatActivity {

    TextView tvWinsCoins, tvUsingCoins;
    public static String KEY_WINNER_COINS = "winnerCoins";
    public static String KEY_USING_COINS = "usingCoins";
    public static int REQUEST_CODE = 1724;
    public static String SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiiplayer_coin_alert_actvity);
        setup();
        getExtraData();
    }

    private void setup() {
        tvWinsCoins = findViewById(R.id.tvWinnerCoins);
        tvUsingCoins = findViewById(R.id.tvUsingCoins);
    }

    private void getExtraData() {
        if (getIntent() != null) {
            String winnerCoins = getIntent().getStringExtra(KEY_WINNER_COINS);
            String usingCoins = getIntent().getStringExtra(KEY_USING_COINS);
            tvWinsCoins.setText(winnerCoins);
            tvUsingCoins.setText(usingCoins + getString(R.string.icoins));
        }
    }

    public void play(View view) {
        setResult(REQUEST_CODE,new Intent().putExtra(SUCCESS,"Success"));
        finish();
    }


}
