package com.app.itaptv.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.itaptv.R;
import com.app.itaptv.structure.QuestionData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DownloadUtil {

    private static Cache cache;
    private static DownloadManager downloadManager;
    ExoDatabaseProvider databaseProvider;

    void download() {
        databaseProvider = new ExoDatabaseProvider(context);
        cache = new SimpleCache(getDownloadsDirectory(), new NoOpCacheEvictor(), databaseProvider);

        String agent = Util.getUserAgent(context, context.getString(R.string.app_name));
        DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory().setUserAgent(agent);

        DownloadManager downloadManager = new DownloadManager(context, databaseProvider, cache, dataSourceFactory);
    }

    File getDownloadsDirectory() {
        File downloadsDirectory = new File(context.getFilesDir(), "offline");
        if (!downloadsDirectory.exists()) downloadsDirectory.mkdir();
        return downloadsDirectory;
    }

    public static synchronized Cache getCache(Context context) {
        if (cache == null) {
            File cacheDirectory = new File(context.getExternalFilesDir(null), "downloads");
            cache = new SimpleCache(cacheDirectory, new NoOpCacheEvictor());
        }
        return cache;
    }

    /*public static synchronized com.google.android.exoplayer2.offline.DownloadManager getDownloadManager(Context context) {
        if (downloadManager == null) {
            File actionFile = new File(context.getExternalCacheDir(), "actions");
            downloadManager = new com.google.android.exoplayer2.offline.DownloadManager(
                    getCache(context),
                    new DefaultDataSourceFactory(context,
                            Util.getUserAgent(context, context.getString(R.string.app_name))),
                    actionFile, ProgressiveDownloadAction.DESERIALIZER);
        }
        return downloadManager;
    }*/

    // Caching...

    /**
     * Instance variable.
     */
    private static DownloadUtil instance;

    /**
     * Private constructor.
     */
    public DownloadUtil() {
    }

    /**
     * Singleton class - get instance.
     *
     * @return Singleton instance of the class DownloadUtil.
     */
    public static synchronized DownloadUtil getInstance() {
        if (instance == null) {
            instance = new DownloadUtil();
        }
        return instance;
    }

    /**
     * Context.
     */
    private Context context;

    /**
     * Holds total attachments to be downloaded.
     */
    private int totalAttachments = 0;

    /**
     * Holds total attachments that are downloaded.
     */
    private int downloadedAttachments = 0;

    /**
     * Holds list of items for which the attachment is to be downloaded.
     */
    private ArrayList<QuestionData> items = new ArrayList<>();

    /**
     * Holds the bitmaps with their respective urls.
     */
    private HashMap<String, Bitmap> bitmaps = new HashMap<>();

    /**
     * Holds the file uris with their respective urls.
     */
    private HashMap<String, String> fileUris = new HashMap<>();

    /**
     * Variable stores the downloading status.
     */
    private boolean isDownloading = false;

    /**
     * Listener. Called when all attachments are downloaded.
     * In case of error, downloads are stopped and listener is called with the error.
     */
    @Nullable
    private DownloadListener listener;

    /**
     * Download all the attachments.
     *
     * @param context  Context required.
     * @param items    Items to be downloaded.
     * @param listener Called after download is finished.
     */
    public void downloadItems(@NonNull Context context, @NonNull ArrayList<QuestionData> items, @NonNull DownloadListener listener) {
        this.context = context;
        this.items.addAll(items);
        this.listener = listener;
        startDownload();
    }

    /**
     * Start the download process.
     */
    private void startDownload() {
        log("startDownload called");
        isDownloading = true;
        for (QuestionData data : items) {
            if (data.attachmentData != null) {
                // Increment total attachments
                totalAttachments += 1;

                // Download the attachments
                if (data.attachmentData.type.equalsIgnoreCase("image")) {
                    log("downloading image");
                    // Download image...
                    Glide.with(context)
                            .asBitmap()
                            .load(Uri.parse(data.attachmentData.url))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    log("image downloaded");
                                    downloadedAttachments += 1;
                                    bitmaps.put(data.attachmentData.url, resource);
                                    checkDownloads();
                                }
                            });

                } else if (data.attachmentData.type.equalsIgnoreCase("audio")) {
                    // download audio...
                    log("Download audio");
                    new DownloadTask(context, data).execute();
                    //downloadFile(data.attachmentData);

                } else {
                    // download file...
                    log("Unsupported file. ignoring");
                    totalAttachments -= 1;
                    //downloadFile(data.attachmentData);
                }
            }
        }
        checkDownloads();
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private QuestionData question;
        private String filePath;

        public DownloadTask(Context context, QuestionData question) {
            log("init download task");
            this.context = context;
            this.question = question;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            log("Started file downloaded");
            int count;
            try {
                URL url = new URL(question.attachmentData.url);
                //URL url = new URL("https://sample-videos.com/audio/mp3/crowd-cheering.mp3");
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String fileName = question.attachmentData.name + "_" + System.currentTimeMillis();
                filePath = new File(context.getFilesDir(), fileName).getPath();
                // Output stream to write file
                OutputStream output = new FileOutputStream(filePath);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress((int) (total * 100 / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                totalAttachments -= 1;
                return e.toString();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            log(String.format("Download progress: %d of 100", progress[0]));
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                log("File download failed. Error: " + result);
            } else {
                log("File downloaded");
                downloadedAttachments += 1;
                fileUris.put(question.attachmentData.url, filePath);
            }
            checkDownloads();
        }

    }

    private void checkDownloads() {
        if (totalAttachments <= 0) {
            log("Game data downloaded with errors");
            isDownloading = false;
            if (listener != null) {
                listener.downloadsCompleted(false, context.getString(R.string.no_attachments_to_download));
            }
            return;
        }
        if (downloadedAttachments == totalAttachments) {
            log("Game data downloaded");
            isDownloading = false;
            if (listener != null) {
                listener.downloadsCompleted(true, null);
            }
        }
    }

    public Bitmap getImage(@NonNull String url) {
        return bitmaps.get(url);
    }

    public String getAudioPath(@NonNull String url) {
        return fileUris.get(url);
    }

    /**
     * Calling this method will delete all cached data.
     */
    public void clear() {
        bitmaps.clear();
        for (Map.Entry<String, String> pair : fileUris.entrySet()) {
            File f = new File(pair.getValue());
            if (f.exists()) {
                f.delete();
            }
        }
        fileUris.clear();
    }

    /**
     * Interface to notify when the downloads are completed.
     */
    public interface DownloadListener {
        /**
         * Called when all the downloads are completed.
         *
         * @param success True if all downloads complete else false.
         * @param error   Error in case the downloads fail.
         */
        void downloadsCompleted(boolean success, @Nullable String error);
    }

    /**
     * Private log method.
     *
     * @param message Message to print.
     */
    private void log(String message) {
        Log.i("DownloadUtil", message);
    }

}
