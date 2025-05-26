package com.app.itaptv.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Base64;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.structure.User;
import com.app.itaptv.utils.AnalyticsTracker;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AvatarWebActivity extends BaseActivity {

    public static String AVATARURL = "AvatarUrl";

    private String arModel;

    Button btSave, btCreateNew, btCancel;
    LinearLayout llShareAvatar, llDownloadAvatar, llBottom;
    WebView webView;
    private String image_path = "";
    File file;
    private String image_path_body = "";
    File fileBody;
    ProgressBar progressBar, avatarProgressBar;
    DownloadManager downloadManager;
    Bitmap fullBodyAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_web);

        setToolbar(true);
        showToolbar(true);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(false);

        if (getIntent() != null) {
            arModel = getIntent().getStringExtra(AVATARURL);
        }

        init();
    }

    @SuppressLint({"SetJavaScriptEnabled", "ResourceAsColor"})
    public void init() {
        btSave = findViewById(R.id.btSave);
        btCreateNew = findViewById(R.id.btCreateNew);
        btCancel = findViewById(R.id.btCancel);
        llShareAvatar = findViewById(R.id.llShareAvatar);
        llDownloadAvatar = findViewById(R.id.llDownloadAvatar);
        llBottom = findViewById(R.id.llBottom);
        avatarProgressBar = findViewById(R.id.avatarProgressBar);
        webView = findViewById(R.id.webViewAvatar);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        llBottom.setVisibility(View.GONE);
        avatarProgressBar.setVisibility(View.GONE);

        webView.setBackgroundColor(R.color.avatar_background);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!image_path_body.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    new ImageUpload().execute();
                }
            }
        });

        btCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AvatarWebActivity.this, BrowserActivity.class).putExtra("title", "Ready Player Me").putExtra("posturl", Url.READY_PLAYER).putExtra(BrowserActivity.CREATENEW, true));
                finish();
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        llShareAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAvatar();
            }
        });

        llDownloadAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadAvatar();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.i("Image", String.valueOf(consoleMessage));
                if (consoleMessage.message().contains("data:application/")) {
                    avatarProgressBar.setVisibility(View.VISIBLE);
                    String cleanImage = consoleMessage.message().replace("data:application/octet-stream;base64,", "");
                    byte[] decodedString = Base64.decode(cleanImage, Base64.DEFAULT);
                    // Bitmap Image
                    fullBodyAvatar = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    String filename = getString(R.string.full_body_avatar) + System.currentTimeMillis() + ".png";
                    File file1;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        file1 = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
                    } else {
                        file1 = new File(Environment.getExternalStorageDirectory() + "/" + "Pictures");
                    }
                    File dest = new File(file1, filename);

                    try {
                        FileOutputStream out = new FileOutputStream(dest);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.i("Image", String.valueOf(dest));
                    image_path_body = String.valueOf(dest);
                    fileBody = dest;
                    avatarProgressBar.setVisibility(View.GONE);
                    llBottom.setVisibility(View.VISIBLE);
                } /*else if (consoleMessage.message().contains("data:image/")) {
                    String cleanImage = consoleMessage.message().replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,", "");
                    //File file= Utility.saveImage(AvatarWebActivity.this,cleanImage);
                    byte[] decodedString = Base64.decode(cleanImage, Base64.DEFAULT);
                    // Bitmap Image
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    String filename = "Avatar"+ System.currentTimeMillis() + ".png";
                    File file1;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        file1 = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
                    } else {
                        file1 = new File(Environment.getExternalStorageDirectory() + "/" + "Pictures");
                    }
                    File dest = new File(file1, filename);

                    try {
                        FileOutputStream out = new FileOutputStream(dest);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.i("Image", String.valueOf(dest));
                    image_path = String.valueOf(dest);
                    file = dest;
                    avatarProgressBar.setVisibility(View.GONE);
                    llBottom.setVisibility(View.VISIBLE);
                }*/
                return super.onConsoleMessage(consoleMessage);
            }
        });

        load3DModel();
    }

    private void load3DModel() {
        String content = LocalStorage.getAvatarViewScript().replace("@@glbsrc@@", arModel).replace("@@loadersrc@@", LocalStorage.getAvatarLoader());
        webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);

        // new Handler(Looper.getMainLooper()).post(this::saveImage);
    }

    private class ImageUpload extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

            /*final MediaType MEDIA_TYPE = image_path.endsWith("png") ?
                    MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }*/
            final MediaType MEDIA_TYPE_BODY = image_path_body.endsWith("png") ?
                    MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            RequestBody body;
            //RequestBody file_body = RequestBody.create(MEDIA_TYPE, file);
            RequestBody file_full_body = RequestBody.create(MEDIA_TYPE_BODY, fileBody);
            body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("avatar", arModel)
                    //.addFormDataPart("image", image_path.substring(image_path.lastIndexOf("/") + 1), file_body)
                    .addFormDataPart("body", image_path_body.substring(image_path_body.lastIndexOf("/") + 1), file_full_body)
                    .addFormDataPart("remove_image", "false")
                    .build();

            Log.i("body", "" + body);
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(Url.UPDATE_PROFILE)
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
                            //String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                            String message = jsonObjectResponse.has("msg") ? Utility.getMessage(AvatarWebActivity.this, jsonObjectResponse.get("msg").toString()) : APIManager.GENERIC_API_ERROR_MESSAGE;
                        } else if (type.equalsIgnoreCase("ok")) {
                            JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                            if (jsonObject.length() > 0) {
                                JSONObject jsonObjectuser = jsonObject.has("user") ? jsonObject.getJSONObject("user") : new JSONObject();
                                if (jsonObjectuser.length() > 0) {
                                    User user = new User(jsonObjectuser);
                                    LocalStorage.setUserData(user);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            onBackPressed();
                                        }
                                    });
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

        }
    }

    public void shareAvatar() {
        if (fullBodyAvatar != null && arModel != null && !arModel.isEmpty()) {
            try {
                AnalyticsTracker.pauseTimer(AnalyticsTracker.AVATAR);
                String appLink = Constant.APP_SHARE_LINK;
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(Intent.EXTRA_TITLE, "iTap");
                i.putExtra(Intent.EXTRA_STREAM, HomeActivity.getLocalBitmapUri(fullBodyAvatar, AvatarWebActivity.this));
                i.putExtra(Intent.EXTRA_TEXT, LocalStorage.getAvatarShareMessage() + "\n" + "https://www.itap.online?" + Utility.encryptData("playstore=true&type=activity_avatar", Constant.getSecretKey()));
                i.setType("*/*");
                startActivity(Intent.createChooser(i, getString(R.string.share_content)));
            } catch (Exception e) {

            }
        }
    }

    public void downloadAvatar() {
        if (arModel != null && !arModel.isEmpty()) {
            Toast.makeText(AvatarWebActivity.this, getString(R.string.downloading), Toast.LENGTH_SHORT).show();
            downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uriGlb = Uri.parse(arModel);
            //Uri uriPng = Uri.parse(image_path_body);

            DownloadManager.Request requestGlb = new DownloadManager.Request(uriGlb);
            //DownloadManager.Request requestPng = new DownloadManager.Request(uriPng);

            requestGlb.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getString(R.string.avatar) + System.currentTimeMillis() + ".glb");

            requestGlb.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            //requestPng.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            long referenceGlb = downloadManager.enqueue(requestGlb);
            //long referencePng = downloadManager.enqueue(requestPng);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnalyticsTracker.stopDurationAvatar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsTracker.startDurationAvatar();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
