package com.app.itaptv.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.app.itaptv.R;
import com.app.itaptv.structure.User;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Utility;

public class ReferAndEarnActivity extends BaseActivity {

    // Variable
    Context mContext = ReferAndEarnActivity.this;
    // UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_and_earn);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setupToolBar();
        setupUI();
    }

    void setupToolBar() {
        if (Utility.isTelevision()) {
            View topLayout = findViewById(R.id.topLayout);
            topLayout.setVisibility(View.GONE);
            setToolbar(false);
            showToolbarTitle(false);
        } else {
            setToolbar(true);
            showToolbarBackButton(R.drawable.white_arrow);
            showToolbarTitle(true);
            setCustomizedTitle(0);
            setToolbarTitle(getString(R.string.invite_a_friend));
            toolbar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
        }
    }

    void text(String referrerCoins, String totalCoins) {
        //String text = String.format("Invite a friend and you'll both\nearn %s iCoins which will be added\nin your wallet.", totalCoins);
        String text = String.format(getString(R.string.refer_messsage), referrerCoins)
                .concat(String.format(getString(R.string.friend_refer), totalCoins));

        TextView tvInviteText = findViewById(R.id.tvInviteText);
        tvInviteText.setText(text);
    }

    @SuppressLint("SetTextI18n")
    void setupUI() {
        TextView tvCode = findViewById(R.id.etCode);
        TextView tvCoins = findViewById(R.id.tvCoins);
        Button btShare = findViewById(R.id.btContinue);
        buttonFocusListener(btShare);

        //setData
        User user = LocalStorage.getUserData();
        if (user != null) {
            tvCode.setText(user.referralCode);
        }
        //tvCoins.setText((LocalStorage.getValue(LocalStorage.KEY_REFERRAL_POINTS, "", String.class).equals("0") ? "50" : "50") + " iCoins");
        tvCoins.setText(LocalStorage.getReferrerPoints() + getString(R.string.icoins));

        /*text(LocalStorage.getValue(LocalStorage.KEY_REFERRAL_POINTS, "", String.class).equals("50") ? "50" : "50",
                LocalStorage.getValue(LocalStorage.KEY_REFERRER_POINTS, "", String.class).equals("250") ? "250" : "250");*/
        text(LocalStorage.getReferrerPoints(), LocalStorage.getReferralPoints());

        btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.download_itap_message) + "\n" + getString(R.string.using_referral_code) + user.referralCode + "\n" + "https://play.google.com/store/apps/details?id=com.app.itap&hl=en");
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "iTap");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
            }
        });

        Button btCopy = findViewById(R.id.btCopy);
        buttonFocusListener(btCopy);
        btCopy.requestFocus();
        btCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.copied), user.referralCode);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    AlertUtils.showToast(getString(R.string.copied_invite_code) + user.referralCode, Toast.LENGTH_LONG, mContext);
                }
            }
        });
    }

    void buttonFocusListener(View view) {
        view.setBackground(getResources().getDrawable(R.drawable.bg_button_grey));
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.hasFocus()) {
                    view.setBackground(getResources().getDrawable(R.drawable.bg_accent));
                } else {
                    view.setBackground(getResources().getDrawable(R.drawable.bg_button_grey));
                }
            }
        });
    }
}
