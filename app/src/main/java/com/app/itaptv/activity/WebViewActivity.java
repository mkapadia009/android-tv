package com.app.itaptv.activity;

import static com.app.itaptv.utils.Analyticals.GAME_ACTIVITY_TYPE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.itaptv.R;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;

public class WebViewActivity extends BaseActivity {

    // UI
    WebView webView;
    String url;
    String title = "";
    // VARIABLES
    public static String GAME_URL = "gameurl";
    public static String GAME_TITLE = "gametitle";
    private static final String REDIRECT_APP = "/static/games.html";
    public static int GAME_REDIRECT_REQUEST_CODE = 8;
    public static String GAME_REDIRECT_KEY = "gameRedirectKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = null;
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (getIntent().getExtras() != null) {
            url = getIntent().getExtras().getString(WebViewActivity.GAME_URL);
            title = getIntent().getExtras().getString(WebViewActivity.GAME_TITLE);
        }
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void init() throws Exception {
        if (url != null && !url.isEmpty()) {
            if (webView == null) {
                webView = findViewById(R.id.webViewGames);
            }
            String token = LocalStorage.getToken();
            String userId = LocalStorage.getUserId();
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setWebViewClient(new WebClient());
            if (!token.isEmpty()) {
                Analyticals.CallPlayedActivity(GAME_ACTIVITY_TYPE, url, "", "", this, "", new Analyticals.AnalyticsResult() {
                    @Override
                    public void onResult(boolean success, String activity_id, @Nullable String error) {
                        if (success) {
                            try {
                                Utility.customEventsTracking(Constant.GamePlay, title);
                                if (url.contains("gamezop")) {
                                    webView.loadUrl(url + "&sub=" + Utility.encryptData(userId, Constant.getSecretKeyDateTime(), Constant.getIvParameterDateTime()));
                                    Log.i("Bluff", url + "&sub=" + Utility.encryptData(userId, Constant.getSecretKeyDateTime(), Constant.getIvParameterDateTime()));
                                } else {
                                    webView.loadUrl(url + "&authToken=" + token + "&userid=" + Utility.encryptData(userId, Constant.getSecretKeyDateTime(), Constant.getIvParameterDateTime()));
                                    Log.i("Bluff", url + "&authToken=" + token + "&userid=" + Utility.encryptData(userId, Constant.getSecretKeyDateTime(), Constant.getIvParameterDateTime()));
                                    System.out.println("User TOkEn: " + token);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {

                        }
                    }
                });
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                Toast.makeText(this, getString(R.string.user_token_expired), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.url_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private class WebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (url.contains(REDIRECT_APP)) {
                Intent intent = new Intent();
                intent.putExtra(GAME_REDIRECT_KEY, "");
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i("WebViewActivity", url);
            if (url.startsWith("whatsapp://")) {
                view.stopLoading();
                try {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");

                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, view.getUrl() + "  - From iTAP ");

                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {

                    String MakeShortText = getString(R.string.whatsapp_has_not_been_installed);

                    Toast.makeText(WebViewActivity.this, MakeShortText, Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //if (LocalStorage.getUserDisplayName().mobile.isEmpty()) {
        if (getWindow() != null) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        Intent intent = new Intent();
        intent.putExtra(GAME_REDIRECT_KEY, "");
        setResult(RESULT_OK, intent);
        finish();
        //}
    }
}
