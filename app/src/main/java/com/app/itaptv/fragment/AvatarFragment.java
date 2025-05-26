package com.app.itaptv.fragment;

import static com.app.itaptv.activity.HomeActivity.getLocalBitmapUri;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.BrowserActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.structure.User;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Utility;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class AvatarFragment extends Fragment {

    View view;
    Button btCreateNew;
    ImageView ivPlaceHolderAvatar;
    TextView tvDescriptionAvatar;
    WebView webViewAvatar;
    User user;
    DownloadManager downloadManager;
    LinearLayout llShareAvatar, llDownloadAvatar;

    String[] arrPermissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_avatar, container, false);
        init();
        return view;
    }

    public static AvatarFragment newInstance() {
        return new AvatarFragment();
    }

    @SuppressLint({"ResourceAsColor", "SetJavaScriptEnabled"})
    private void init() {
        btCreateNew = view.findViewById(R.id.btCreateNewAvatar);
        ivPlaceHolderAvatar = view.findViewById(R.id.placeHolderAvatar);
        tvDescriptionAvatar = view.findViewById(R.id.descriptionAvatar);
        webViewAvatar = view.findViewById(R.id.webViewAvatar);
        llShareAvatar = view.findViewById(R.id.llShareAvatar);
        llDownloadAvatar = view.findViewById(R.id.llDownloadAvatar);

        webViewAvatar.getSettings().setJavaScriptEnabled(true);
        //webViewAvatar.getSettings().setAppCacheEnabled(true);
        webViewAvatar.getSettings().setDatabaseEnabled(true);
        webViewAvatar.getSettings().setDomStorageEnabled(true);
        webViewAvatar.getSettings().setSupportZoom(true);
        webViewAvatar.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webViewAvatar.getSettings().setBuiltInZoomControls(true);

        setAvatar();

        btCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.checkPermission(getContext(), arrPermissions)) {
                    startActivity(new Intent(getContext(), BrowserActivity.class).putExtra("title", "Ready Player Me").putExtra("posturl", Url.READY_PLAYER).putExtra(BrowserActivity.CREATENEW, true));
                } else {
                    Utility.requestPermission(getContext(), BrowserActivity.MY_PERMISSIONS_REQUEST_CODE, arrPermissions);
                }
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((HomeActivity) getActivity()).showToolbarIcon();
            ((HomeActivity) getActivity()).showToolbarTitle(false);
        }
        setAvatar();
    }

    private void setAvatar() {
        user = LocalStorage.getUserData();
        if (user.avatar.equalsIgnoreCase("null")) {
            ivPlaceHolderAvatar.setVisibility(View.VISIBLE);
            tvDescriptionAvatar.setVisibility(View.VISIBLE);
            webViewAvatar.setVisibility(View.GONE);
            llDownloadAvatar.setVisibility(View.GONE);
            llShareAvatar.setVisibility(View.GONE);
        } else if (user.avatar.isEmpty()) {
            ivPlaceHolderAvatar.setVisibility(View.VISIBLE);
            tvDescriptionAvatar.setVisibility(View.VISIBLE);
            webViewAvatar.setVisibility(View.GONE);
            llDownloadAvatar.setVisibility(View.GONE);
            llShareAvatar.setVisibility(View.GONE);
        } else {
            ivPlaceHolderAvatar.setVisibility(View.GONE);
            tvDescriptionAvatar.setVisibility(View.GONE);
            webViewAvatar.setVisibility(View.VISIBLE);
            llDownloadAvatar.setVisibility(View.VISIBLE);
            llShareAvatar.setVisibility(View.VISIBLE);

            String content = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<body style=\"background-color:#13001f;\">\n" +
                    "<!-- Import the component -->\n" +
                    "<script type=\"module\" src=\"https://unpkg.com/@google/model-viewer/dist/model-viewer.min.js\"></script>\n" +
                    "<script src=\"https://unpkg.com/focus-visible@5.0.2/dist/focus-visible.js\" defer></script>\n" +
                    "<style>\n" +
                    "      html {\n" +
                    "        background-color: #13001f;\n" +
                    "      }\n" +
                    "      model-viewer#reveal {\n" +
                    "        --poster-color: #13001f;\n" +
                    "      }\n" +
                    "      body {\n" +
                    "        background-color: #13001f;\n" +
                    "        margin: 0;\n" +
                    "        padding: 0;\n" +
                    "        width: 100vw;\n" +
                    "        height: 100vh;\n" +
                    "        display: flex;\n" +
                    "        justify-content: center;\n" +
                    "        align-items: center;\n" +
                    "        flex-direction: column;\n" +
                    "      }\n" +
                    "\n" +
                    "      canvas {\n" +
                    "        position: absolute;\n" +
                    "        top: 0;\n" +
                    "        left: 0;\n" +
                    "      }\n" +
                    "    </style>" +
                    "<model-viewer\n" +
                    "        id=\"reveal\"\n" +
                    "        loading=\"eager\"\n" +
                    "        camera-controls=\"true\"\n" +
                    "        auto-rotate=\"true\"\n" +
                    "        poster=" + '"' + LocalStorage.getAvatarLoader() + "\"\n" +
                    "        src=" + '"' + user.avatar + '"' + "alt=\"Bold [3D]\"\n" +
                    "        ar-status=\"not-presenting\"\n" +
                    "        style=\"width: 100%; height: 100%; position: absolute; left: 0px; top: 0px; z-index: 0; display: block; background-color:#13001f;\">\n" +
                    "</model-viewer>\n" +
                    "</body>\n" +
                    "</html>";
            webViewAvatar.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
        }

        if (user.avatarBodyPicture.equalsIgnoreCase("null")) {
            llDownloadAvatar.setVisibility(View.GONE);
            llShareAvatar.setVisibility(View.GONE);
        } else if (user.avatarBodyPicture.isEmpty()) {
            llDownloadAvatar.setVisibility(View.GONE);
            llShareAvatar.setVisibility(View.GONE);
        } else {
            llDownloadAvatar.setVisibility(View.VISIBLE);
            llShareAvatar.setVisibility(View.VISIBLE);
        }
    }

    public void shareAvatar() {

        if (user.avatarBodyPicture != null && !user.avatarBodyPicture.equals("")) {
            Picasso.get().load(user.avatarBodyPicture).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    try {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra(Intent.EXTRA_TITLE, "iTap");
                        i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, view.getContext()));
                        i.putExtra(Intent.EXTRA_TEXT, LocalStorage.getAvatarShareMessage() + "\n" + "https://www.itap.online?" + Utility.encryptData("playstore=true&type=activity_avatar", Constant.getSecretKey()));
                        i.setType("*/*");
                        startActivity(Intent.createChooser(i, getString(R.string.share_content)));
                    }catch (Exception e){
                        
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }

    public void downloadAvatar() {
        if (!user.avatar.isEmpty() && !user.avatarBodyPicture.isEmpty()) {
            if (!user.avatar.equalsIgnoreCase("null") && !user.avatarBodyPicture.equalsIgnoreCase("null")) {
                Toast.makeText(getContext(), getString(R.string.downloading), Toast.LENGTH_SHORT).show();
                downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(user.avatar);
                Uri uriPng = Uri.parse(user.avatarBodyPicture);

                DownloadManager.Request request = new DownloadManager.Request(uri);
                DownloadManager.Request requestPng = new DownloadManager.Request(uriPng);

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getString(R.string.avatar) + System.currentTimeMillis() + ".glb");
                requestPng.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getString(R.string.full_body_avatar) + System.currentTimeMillis() + ".png");

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                requestPng.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                long reference = downloadManager.enqueue(request);
                long referencePng = downloadManager.enqueue(requestPng);
            }
        }
    }
}