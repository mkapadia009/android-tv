package com.app.itaptv.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.MyApp;
import com.app.itaptv.R;
import com.app.itaptv.service.OfflineDownloadService;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.DrmSessionEventListener;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManagerProvider;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.MediaDrmCallback;
import com.google.android.exoplayer2.drm.OfflineLicenseHelper;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.app.itaptv.activity.HomeActivity.DOWNLOADING_BROADCAST_KEY;
import static com.app.itaptv.activity.HomeActivity.IS_GO_TO_DOWNLOAD;
import static com.app.itaptv.activity.HomeActivity.getInstance;

public class ExoUtil {

    /**
     * Builds MediaSource.
     *
     * @param uri               Uri of the media.
     * @param dataSourceFactory DataSource.Factory instance.
     * @param drmSessionManager DRMSessionManager.
     * @return MediaSource instance object.
     */
    public static @Nullable
    MediaSource buildMediaSource(@NonNull Uri uri, @NonNull DataSource.Factory dataSourceFactory,
                                 @Nullable DrmSessionManager drmSessionManager) {
        /*uri = Uri.parse("https://d14adce467ft2z.cloudfront.net/dash/05032020124602537913/playlist.mpd");
        String invalidLic = "https://widevine-proxy.appspot.com/proxy";
        String validLic = "https://proxy.uat.widevine.com/proxy?video_id=d286538032258a1c&provider=widevine_test";
        validLic = "https://license.vdocipher.com/auth/wv/eyJjb250ZW50QXV0aCI6ImV5SmpiMjUwWlc1MFNXUWlPaUl6TURNMU16QXpNek15TXpBek1qTXdNekV6TWpNME16WXpNRE15TXpVek16TTNNemt6TVRNeklpd2laWGh3YVhKbGN5STZNVFU0TXpReE16Z3pOSDA9Iiwic2lnbmF0dXJlIjoid0d3NUtYaHVScFBkNVVHTDoyMDIwMDMwNVQxODI1MzRaOkJSUElHVDZ4b3VpS0x1bUNOV3RQbUpUMXFZbi1hX0ZlRHZlUTh4dlY5LWc9In0=";
        drmSessionManager = getDrmSessionManager(validLic,
                null, UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed"), false);*/
        DrmSessionManagerProvider drmSessionManagerProvider = null;
        if (drmSessionManager != null) {
            final DrmSessionManager finalDrmSessionManager = drmSessionManager;
            drmSessionManagerProvider = new DrmSessionManagerProvider() {
                @Override
                public DrmSessionManager get(MediaItem mediaItem) {
                    return finalDrmSessionManager;
                }
            };
        }
        @C.ContentType int type = Util.inferContentType(uri);
        if (type == C.CONTENT_TYPE_HLS) {
            HlsMediaSource.Factory factory = new HlsMediaSource.Factory(dataSourceFactory);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (drmSessionManagerProvider != null) {
                    factory.setDrmSessionManagerProvider(drmSessionManagerProvider);
                }
            }
            return factory.createMediaSource(MediaItem.fromUri(uri));
        } else if (type == C.CONTENT_TYPE_DASH) {
            DashMediaSource.Factory factory = new DashMediaSource.Factory(dataSourceFactory);
            if (drmSessionManagerProvider != null) {
                factory.setDrmSessionManagerProvider(drmSessionManagerProvider);
            }
            return factory.createMediaSource(MediaItem.fromUri(uri));
        } else if (type == C.CONTENT_TYPE_OTHER) {
            return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
        }
        return null;
    }


    public static boolean isDash(Uri uri) {
        @C.ContentType int type = Util.inferContentType(uri);
        return type == C.TYPE_DASH;
    }

    public static DrmSessionManager
    getDrmSessionManager(@NonNull String licenseUrl, String[] keyProperties,
                         @NonNull UUID uuid, boolean multiSession) {
        if (licenseUrl.isEmpty()) {
            return DrmSessionManager.getDummyDrmSessionManager();
        }

        MediaDrmCallback mediaDrmCallback =
                createMediaDrmCallback(licenseUrl, keyProperties);
        DefaultDrmSessionManager drmSessionManager = new DefaultDrmSessionManager.Builder()
                .setUuidAndExoMediaDrmProvider(uuid, FrameworkMediaDrm.DEFAULT_PROVIDER)
                .setMultiSession(multiSession)
                .build(mediaDrmCallback);
        //drmSessionManager.setMode(DefaultDrmSessionManager.MODE_DOWNLOAD, null);
        return drmSessionManager;
    }

    public static DrmSessionManager
    getOfflineDrmSessionManager(@NonNull String postId, @NonNull String licenseUrl,
                                String[] keyProperties, @NonNull UUID uuid, boolean multiSession) {
        /*if (licenseUrl.isEmpty()) {
            return DrmSessionManager.getDummyDrmSessionManager();
        }*/

        MediaDrmCallback mediaDrmCallback =
                createMediaDrmCallback(licenseUrl, keyProperties);

        byte[] offlineLicenceKeySetId = getLicence(postId);

        DefaultDrmSessionManager drmManager = new DefaultDrmSessionManager.Builder()
                .setUuidAndExoMediaDrmProvider(uuid, FrameworkMediaDrm.DEFAULT_PROVIDER)
                .setMultiSession(multiSession)
                .build(mediaDrmCallback);
        drmManager.setMode(DefaultDrmSessionManager.MODE_PLAYBACK, offlineLicenceKeySetId);
        return drmManager;
    }

    private static byte[] getLicence(String id) {
        String key = LocalStorage.getLicence(id);
        return Base64.decode(key, Base64.DEFAULT);
    }

    public static boolean isValidLicence(String id) {
        return validateLicence(getLicence(id));
    }

    public static boolean validateLicence(byte[] offlineLicenceKeySetId) {
        try {
            DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory().setUserAgent(MyApp.getInstance().userAgent);

            OfflineLicenseHelper offlineLicenseHelper = OfflineLicenseHelper
                    .newWidevineInstance("", httpDataSourceFactory, new DrmSessionEventListener.EventDispatcher());

            Pair<Long, Long> p = offlineLicenseHelper.getLicenseDurationRemainingSec(offlineLicenceKeySetId);
            Log.i("Offline Licence", "Remaining duration: " + p.toString());
            return p.second > 0 && p.first > 0;

        } catch (DrmSession.DrmSessionException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static HttpMediaDrmCallback createMediaDrmCallback(
            String licenseUrl, String[] keyRequestPropertiesArray) {
        HttpDataSource.Factory licenseDataSourceFactory = buildHttpDataSourceFactory();
        HttpMediaDrmCallback drmCallback =
                new HttpMediaDrmCallback(licenseUrl, licenseDataSourceFactory);
        if (keyRequestPropertiesArray != null) {
            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],
                        keyRequestPropertiesArray[i + 1]);
            }
        }
        return drmCallback;
    }

    /**
     * Returns a {@link HttpDataSource.Factory}.
     */
    private static HttpDataSource.Factory buildHttpDataSourceFactory() {
        String agent = Util.getUserAgent(MyApp.sharedContext(), "iTap");
        return new DefaultHttpDataSource.Factory().setUserAgent(agent);
    }

    public static void getLicense(@NonNull String id, @NonNull String userId, @NonNull String postId, @NonNull LicenceListener listener, @NonNull Context context) {
        try {
            Map<String, String> params = new HashMap<>();
            Map<String, String> headers = new HashMap<>();
            String time = Utility.getCurrentDateTimeInMillis();
            String android_id = Settings.Secure.getString(MyApp.getAppContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            String uniqueId = android_id + "." + time;
            headers.put("request_id",Utility.openssl_encrypt(uniqueId, Constant.getSecretKeyDateTime(), Constant.getIvParameterDateTime()));
            String url = Url.getDomain() + "packager/license2.php/?id=" + id;
            APIRequest apiRequest = new APIRequest(url, Request.Method.GET, params, headers, context);

            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    if (error != null) {
                        //showError(error.getMessage());
                    } else {
                        if (response != null) {
                            Log.e("response", response);
                            listener.licenceUrl(response);
                        }
                    }
                }
            });
        } catch (Exception e) {

        }
//        RequestQueue queue = Volley.newRequestQueue(MyApp.getAppContext());
//        String url = Url.getDomain() + "packager/license2.php/?id=" + id;
//        APIRequest request = new APIRequest(url, Request.Method.GET, null, null, null);
//        APIManager.request(request, (response, error, headers, statusCode) -> listener.licenceUrl(response));

//        StringRequest getLicenseRequest = new StringRequest(Request.Method.GET, url,
//                response -> {
//                    Log.d("License Response: ", response.trim());
//                    listener.licenceUrl(response);
//                },
//                error -> Log.d("Error.Response", error.toString())
//        );
//        queue.add(getLicenseRequest);
    }

    public static void getOfflineLicense(@NonNull String userId, @NonNull String id, @NonNull String postId, @NonNull LicenceListener listener) {
        // TODO: - If in future licence rental duration is required as an input from client side, pass those parameters in query in the below api.
        RequestQueue queue = Volley.newRequestQueue(MyApp.getAppContext());
        String url = Url.getDomain() + "packager/offlinelicense2.php/?id=" + id;

        /*APIRequest request = new APIRequest(url, Request.Method.GET, null, null, null);
        APIManager.request(request, (response, error, headers, statusCode) -> listener.licenceUrl(response));*/

        StringRequest getLicenseRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("License Response: ", response);
                    if (response.equalsIgnoreCase("\n\n\ninactive")) {
                        AlertUtils.showToast(getInstance().getString(R.string.subscribe_to_use_the_download_feature), 3, MyApp.getAppContext());
                        Intent downloadInf = new Intent(DOWNLOADING_BROADCAST_KEY);
                        downloadInf.putExtra(IS_GO_TO_DOWNLOAD, true);
                        LocalBroadcastManager.getInstance(MyApp.getAppContext()).sendBroadcast(downloadInf);
                    } else {
                        listener.licenceUrl(response.trim());
                    }
                },
                error -> {
                    listener.licenceUrl(null);
                    Log.d("Error.Response", error.toString());
                }
        );
        queue.add(getLicenseRequest);
    }

    public interface LicenceListener {
        void licenceUrl(String url);
    }

    public static void download(Context context, String id, Uri contentUri) {
        // if (isDash(contentUri)) {
        boolean shouldDownload = false;
        try {
            Download download = MyApp.getInstance().getDownloadManager()
                    .getDownloadIndex().getDownload(id);
            if (download == null || download.state != Download.STATE_COMPLETED) {
                shouldDownload = true; // download not found or download not completed
            }
        } catch (Exception ignored) {
            shouldDownload = true; // file not found
        }
        if (shouldDownload) {
                /*DownloadRequest downloadRequest = new DownloadRequest(id, DownloadRequest.TYPE_DASH,
                        contentUri, Collections.emptyList(), null, null);
                DownloadService.sendAddDownload(context, OfflineDownloadService.class, downloadRequest,
                        false);*/
            DownloadHelper downloadHelper = DownloadHelper.forDash(context, contentUri,
                    buildHttpDataSourceFactory(), new DefaultRenderersFactory(context));
            downloadHelper.prepare(new DownloadHelper.Callback() {
                @Override
                public void onPrepared(@NotNull DownloadHelper helper) {
                    Log.i("DownloadHelper", "Prepared");
                    // Preparation completes. Now other DownloadHelper methods can be called.
                    ArrayList trackKeys = new ArrayList();
                    for (int i = 0; i < downloadHelper.getPeriodCount(); i++) {
                        TrackGroupArray trackGroups = downloadHelper.getTrackGroups(i);
                        for (int j = 0; j < trackGroups.length; j++) {
                            TrackGroup trackGroup = trackGroups.get(j);
                            for (int k = 0; k < trackGroup.length; k++) {
                                Format track = trackGroup.getFormat(k);
                                    /*if (shouldDownload(track)) {
                                        trackKeys.add(new TrackKey(i, j, k));
                                    }*/
                            }
                        }
                    }
                    DownloadRequest request = helper.getDownloadRequest(id, null);
                    DownloadService.sendAddDownload(context, OfflineDownloadService.class, request,
                            false);
                }

                @Override
                public void onPrepareError(@NotNull DownloadHelper helper, IOException e) {
                    Log.e("DownloadHelper", "Error preparing: " + e.getMessage());
                }
            });
        } else {
            AlertUtils.showToast(context.getString(R.string.file_already_downloaded), 1, context);
        }
        //} else {
        //  AlertUtils.showToast(context.getString(R.string.download_not_supported), 1, context);
        // }
    }

    public static void delete(String id) {
        DownloadService.sendRemoveDownload(MyApp.sharedContext(), OfflineDownloadService.class, id,
                false);
    }

    public static void deleteAll() {
        DownloadService.sendRemoveAllDownloads(MyApp.sharedContext(), OfflineDownloadService.class,
                false);
    }

    public static boolean isDownloaded(String id) {
        try {
            Download download = MyApp.getInstance().getDownloadManager()
                    .getDownloadIndex().getDownload(id);
            return (download != null && download.state == Download.STATE_COMPLETED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
