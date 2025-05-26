package com.app.itaptv.activity;

import static com.app.itaptv.activity.AvatarWebActivity.AVATARURL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.utils.AnalyticsTracker;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.google.android.material.appbar.AppBarLayout;

import java.io.File;

public class BrowserActivity extends BaseActivity {

    public static String CREATENEW = "isCreateNew";
    WebView webView;
    ProgressBar progressBar;
    AppBarLayout appBarLayout;

    String title;
    String postUrl = "";

    private File pic = null;
    File file;
    private Uri picUri;

    private int LOAD_IMAGE_CAMERA = 13;
    private int LOAD_IMAGE_GALLARY = 14;

    boolean isFirstLoading = false;
    boolean isCreateNew = false;
    String[] arrPermissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ValueCallback<Uri[]> filePathCallBack;
    public final static int MY_PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        appBarLayout = findViewById(R.id.lltoolbar);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (getIntent() != null) {
            title = getIntent().getStringExtra("title");
            postUrl = getIntent().getStringExtra("posturl");
            isCreateNew = getIntent().getBooleanExtra(CREATENEW, false);
        }

        if (title.equalsIgnoreCase(getString(R.string.stripe))) {
            appBarLayout.setVisibility(View.GONE);
        } else {
            appBarLayout.setVisibility(View.VISIBLE);
            setToolbar(true);
            showToolbar(true);
            showToolbarBackButton(R.drawable.white_arrow);
            showToolbarTitle(true);
        }

        setTitle(title);
        setCustomizedTitle(0);

        init();
        AnalyticsTracker.secondsPlayedAvatar = 0;
    }

    @SuppressLint("SetJavaScriptEnabled")
    void init() {
        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        webView.getSettings().setJavaScriptEnabled(true);
        //webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);

        webView.getSettings().setGeolocationEnabled(true);

        if (isCreateNew) {
            webView.clearHistory();
            webView.clearFormData();
            webView.clearCache(true);
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().removeSessionCookies(null);
            CookieManager.getInstance().flush();
            WebStorage.getInstance().deleteAllData();
        }

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.i("Avatar", consoleMessage.message());
                String message = consoleMessage.message();
                if (message.startsWith("https") && message.endsWith(".glb")) {
                    if (!isFirstLoading) {
                        isFirstLoading = true;
                        startActivity(new Intent(BrowserActivity.this, AvatarWebActivity.class).putExtra(AVATARURL, message));
                        finish();
                    }
                }
                if (Utility.isJson(consoleMessage.message())) {
                    Log.i("Avatar", "Redirected to app");
                    Intent intent = new Intent();
                    intent.putExtra("status", true);
                    setResult(RESULT_OK, intent);
                    finish();
                    //  startActivityForResult(new Intent(BrowserActivity.this,BuyDetailActivity.class),BuyDetailActivity.REQUEST_CODE_PREMIUM);
                }
                if (message.equalsIgnoreCase("paid")) {
                    Log.i("Avatar", "Redirected to app");
                    Intent intent = new Intent();
                    intent.putExtra("status", true);
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (consoleMessage.message().equalsIgnoreCase("unpaid")) {
                    Log.i("Avatar", "Redirected to app");
                    Intent intent = new Intent();
                    intent.putExtra("status", false);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                filePathCallBack = filePathCallback;
                if (fileChooserParams.isCaptureEnabled()) {
                    if (Utility.checkPermission(BrowserActivity.this, arrPermissions)) {
                        camera();
                    } else {
                        Utility.requestPermission(BrowserActivity.this, MY_PERMISSIONS_REQUEST_CODE, arrPermissions);
                    }
                } else {
                    gallery();
                }
                return true;
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(new String[]{Manifest.permission.CAMERA});
                super.onPermissionRequest(request);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                loadJs();
                super.onPageFinished(view, url);
            }

            /* @Override
            public void onReceivedSslError(WebView view, SslErrorHandler
                    handler, SslError error) {
                handler.proceed();
            }*/
        });

        webView.loadUrl(postUrl);

        // webView.loadUrl(postUrl);
        webView.setHorizontalScrollBarEnabled(false);
    }

    @Override
    public void onBackPressed() {
        if (postUrl.contains(Url.READY_PLAYER)) {
            AnalyticsTracker.stopDurationAvatar();
        }
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public void loadJs() {
        if (LocalStorage.getAvatarScript() != null) {
            webView.evaluateJavascript(LocalStorage.getAvatarScript(), null);
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
            picUri = FileProvider.getUriForFile(BrowserActivity.this,
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == LOAD_IMAGE_CAMERA && resultCode == RESULT_OK) {
                Log.e("PIC URL", "" + picUri);
                if (picUri != null) {
                    filePathCallBack.onReceiveValue(new Uri[]{picUri});
                }
            } else if (requestCode == LOAD_IMAGE_GALLARY) {
                if (data != null) {
                    picUri = data.getData();
                    Log.e("PIC URL", "" + picUri);
                    filePathCallBack.onReceiveValue(new Uri[]{picUri});
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CODE && grantResults.length > 0 && (grantResults[0] + grantResults[1]
                == PackageManager.PERMISSION_GRANTED)) {

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (postUrl.contains(Url.READY_PLAYER)) {
            AnalyticsTracker.startDurationAvatar();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
