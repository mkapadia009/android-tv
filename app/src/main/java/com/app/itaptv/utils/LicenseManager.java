package com.app.itaptv.utils;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.app.itaptv.structure.FeedContentData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LicenseManager {

    private static LicenseManager instance;

    private LicenseManager() {
    }

    synchronized public static LicenseManager getInstance() {
        if (instance == null) {
            instance = new LicenseManager();
        }
        return instance;
    }

    private int totalLicences = 0;
    public Map<String, String> licences = new HashMap<>();
    private String TAG = LicenseManager.class.getName();

    private void reset() {
        log("Clearing any previous licences");
        totalLicences = 0;
        licences.clear();
    }

    public void downloadLicences(@NonNull ArrayList<FeedContentData> feedList,
                                 @NonNull LicenceDownloadListener listener, Context context) {
        reset();
        log("Downloading licences");
        for (FeedContentData data : feedList) {
            if (data.postDrmProtected.equalsIgnoreCase(Constant.YES)) {
                if (data.getMediaUrl() != null && !data.getMediaUrl().isEmpty()) {
                    Uri uri = Uri.parse(data.getMediaUrl());
                    if (uri != null) {
                        /*String[] paths = uri.getPath().split("/");
                        String id = paths[paths.length - 2];*/
                        if (data.attachmentData.wv_content_id != null && data.attachmentData.wv_hls_url != null) {
                            String id = data.attachmentData.wv_content_id;
                            totalLicences++;
                            ExoUtil.getLicense(id, LocalStorage.getUserId(), data.postId, url -> {
                                if (url != null && !url.isEmpty()) {
                                    licences.put(data.postId, url);
                                    log("license downloaded for: " + id);
                                    if (licences.size() == totalLicences) {
                                        listener.licencesDownloaded(licences);
                                        log(totalLicences + " licences downloaded successfully. All Licences: " + licences);
                                    }
                                }
                            },context);
                        }
                    }
                }
            }
        }
        if (totalLicences == 0) {
            listener.licencesDownloaded(licences);
            log("No licence to download");
        }
    }

    private void log(@NonNull String message) {
        Log.i(TAG, message);
    }

    public interface LicenceDownloadListener {
        void licencesDownloaded(Map<String, String> licences);
    }

}
