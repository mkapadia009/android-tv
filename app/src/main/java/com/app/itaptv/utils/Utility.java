package com.app.itaptv.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Spannable;
import android.text.style.AbsoluteSizeSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.app.itaptv.API.APIManager;
import com.app.itaptv.BuildConfig;
import com.app.itaptv.MyApp;
import com.app.itaptv.R;
import com.app.itaptv.structure.AdMobData;
import com.app.itaptv.structure.CountryData;
import com.app.itaptv.structure.DeviceDetailsData;
import com.app.itaptv.structure.LanguagesData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.trackier.sdk.TrackierEvent;
import com.trackier.sdk.TrackierSDK;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.os.Build.VERSION.SDK_INT;
import static com.app.itaptv.utils.Constant.INVALID_DATE;
import static com.app.itaptv.utils.Constant.VIEW_LANDSCAPE;
import static com.app.itaptv.utils.Constant.VIEW_PORTRAIT;
import static com.app.itaptv.utils.Constant.VIEW_UNSPECIFIED;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by poonam on 28/8/18.
 */

public class Utility {
    //for couponDunia
    String timeStamp = "";

    /**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     */
    public static void showError(@Nullable String errorMessage, Context context) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(context.getString(R.string.label_alert), errorMessage, null, context, null);
    }

    //Format Date
    public static String formatDate(String sourceFormat, String destFormat, String date) {
        if (date.equalsIgnoreCase(INVALID_DATE))
            return "";
        SimpleDateFormat sourceTimeFormat = new SimpleDateFormat(sourceFormat, Locale.ENGLISH);
        SimpleDateFormat destinationSimpleDateFormat = new SimpleDateFormat(destFormat, Locale.ENGLISH);
        String formattedDate = "";
        try {
            formattedDate = destinationSimpleDateFormat.format(sourceTimeFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public static String urlFix(String url) {
        if (url.contains("hls")) {
            if (!url.contains("/hls")) {
                url = url.replace("hls", "/hls");
            }
        }
        return url;
    }

    public static void setBackgroundColor(Object object, String color) {
        if (color == null || color.equals(""))
            return;
        if (object instanceof Button) {
            Button button = (Button) object;
            GradientDrawable bgShape = (GradientDrawable) button.getBackground();
            bgShape.setColor(Color.parseColor(color));
        }
    }

    public static void setCompoundDrawableColor(Button object, String color) {
        if (color == null || color.equals(""))
            return;
        if (object instanceof Button) {
            Button button = object;
            for (Drawable drawable : button.getCompoundDrawablesRelative()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN));
                }
            }
        }
    }

    public static Boolean isMediaEnding(Long lastDuration, Long totalDuration) {
        long minusValue = totalDuration - 2000;
        return lastDuration >= minusValue;
    }

    public static void setBackgroundShadeColor(Object object, String startColor, String endColor, GradientDrawable.Orientation gradientOrientation) {
        if (startColor == null || endColor == null || startColor.equals("") || endColor.equals(""))
            return;
        if (object instanceof Button) {
            Button button = (Button) object;
            int[] colors = {Color.parseColor(startColor), Color.parseColor(endColor)};

            //create a new gradient color
            GradientDrawable gd = new GradientDrawable(
                    gradientOrientation, colors);

            gd.setCornerRadius(0f);
            //apply the button background to newly created drawable gradient
            button.setBackground(gd);
        }
    }

    public static void setBorderColor(Object object, String color) {
        if (color == null || color.equals(""))
            return;
        if (object instanceof Button) {
            Button button = (Button) object;
            GradientDrawable bgShape = (GradientDrawable) button.getBackground();
            bgShape.setStroke(1, Color.parseColor(color));
        }
    }

    public static void setTextColor(Object object, String color) {
        if (color == null || color.equals(""))
            return;
        if (object instanceof Button) {
            Button button = (Button) object;
            button.setTextColor(Color.parseColor(color));
        } else if (object instanceof TextView) {
            TextView textView = (TextView) object;
            textView.setTextColor(Color.parseColor(color));
        }
    }

    public static int getImageViewHeight(Context context, double originalHeight, double originalWidth) {
        int deviceWidthInDp = getDeviceWidthHeight(context, "w");
        return calculateHeightWithAspectRatio(originalHeight, originalWidth, deviceWidthInDp);
    }

    public static int getPercentPadding(Context context, int percent) {
        int device_width = getDeviceWidthHeight(context, "w");
        return (device_width / 100) * percent;
    }

    public static int getDeviceWidthHeight(Context context, String hw) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int device_height = displaymetrics.heightPixels;
        int device_width = displaymetrics.widthPixels;
        int heightInDP = Math.round(displaymetrics.heightPixels / displaymetrics.density);
        int widthInDP = Math.round(displaymetrics.widthPixels / displaymetrics.density);
        switch (hw) {
            case "h":
                return device_height;
            case "w":
                return device_width;
        }
        return 0;
    }

    public static int getDevicePixelsFromPixels(Context context, int px) {
        /*DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        //return Math.round(px / (displaymetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return Math.round(px / displaymetrics.density);*/

        float dp = px / context.getResources().getDisplayMetrics().density;
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    // Calculate Height keeping Aspect Ratio
    public static int calculateHeightWithAspectRatio(double originalHeight, double originalWidth, double deviceWidth) {
        Double newHeight = Double.valueOf((originalHeight / originalWidth));
        newHeight = newHeight * deviceWidth;
        return newHeight.intValue();
    }

    public static void CustomNotification(Context context, String description) {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.custom_notification);

        int NOTIFICATION_ID = (int) System.currentTimeMillis();
        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(context, NotificationView.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("title", context.getString(R.string.app_name));
        intent.putExtra("text", description);
        PendingIntent pIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder notificationBuilder;
        Bitmap bitmap = null;
        if (bitmap == null) {
            notificationBuilder = new NotificationCompat.Builder(context, description)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.itap_logo))
                    .setSmallIcon(R.mipmap.itap_logo)
                    .setAutoCancel(true)
                    //.setStyle(inboxStyle)
                    .setChannelId(CHANNEL_ID)
                    .setContentIntent(pIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContent(remoteViews);
        } else {
            notificationBuilder = new NotificationCompat.Builder(context, description)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.itap_logo))
                    .setSmallIcon(R.mipmap.itap_logo)
                    .setAutoCancel(true)
                    //.setStyle(inboxStyle)
                    .setChannelId(CHANNEL_ID)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pIntent)
                    .setContent(remoteViews);
        }

        remoteViews.setImageViewResource(R.id.imagenotileft, R.mipmap.itap_logo);
        remoteViews.setTextViewText(R.id.title, context.getString(R.string.app_name));
        remoteViews.setTextViewText(R.id.text, description);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        CharSequence name = "iTap";
        NotificationChannel mChannel = null;
        if (SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }

        /*// Send data to NotificationView Class
        intent.putExtra("title", context.getString(R.string.app_name));
        intent.putExtra("description", description);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationChannel mChannel = null;
        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder notificationBuilder;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // you must create a notification channel for API 26 and Above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.app_name), importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                // Set Icon
                .setSmallIcon(R.mipmap.itap_logo)
                // Dismiss Notification
                .setAutoCancel(true)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Set RemoteViews into Notification
                .setContent(remoteViews);

        // Locate and set the Image into customnotificationtext.xml ImageViews
        remoteViews.setImageViewResource(R.id.imagenotileft, R.mipmap.itap_logo);

        // Locate and set the Text into customnotificationtext.xml TextViews
        remoteViews.setTextViewText(R.id.title, context.getString(R.string.app_name));
        remoteViews.setTextViewText(R.id.description, description);

        // Create Notification Manager
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationManager.notify(0, builder.build());*/
    }

    public static String getMobileStrikeOut(String mobile) {
        if (mobile.length() > 0) {
            String mobile1 = mobile.substring(0, 3);
            String mobile2 = mobile.substring(mobile.length() - 2, mobile.length());
            mobile = mobile1 + "XXXXX" + mobile2;
        }
        return mobile;
    }

    public static String getMobileStrikeOutMiddle(String mobile) {
        String newnumber = "";
        if (mobile.length() > 0) {
            newnumber = mobile.substring(0, 3);
            newnumber = newnumber + "****";
            newnumber = newnumber + mobile.substring(7, mobile.length());
        }
        return newnumber;
    }


    public static Drawable getIcon(Context context, int icon, int state) {
        Drawable mDrawable = context.getResources().getDrawable(icon, null).getConstantState().newDrawable().mutate();
        if (state > 0) {
            mDrawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP));
        } else {
            mDrawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP));
        }
        return mDrawable;
    }

    public static boolean isConnectingToInternet(Context _context) {
        if (_context != null) {
            ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }

            }
        }
        return false;
    }

    /**
     * Set Multiple colors to view
     *
     * @param v : View
     */
    public static void setShadeBackground(View v) {
        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                LinearGradient linearGradient = new LinearGradient(0, 0, width, height,
                        new int[]{
                                0xFF9913e6,
                                0xFF4914a0,
                                0xFF1d3ddd,
                                0xFF019a4a,
                                0xFFfde000,
                                0xFFff5a00,
                                0xFFd10e1c}, //substitute the correct colors for these
                        new float[]{
                                0, 0.17f, 0.34f, 0.51f, 0.68f, 0.85f, 1},
                        Shader.TileMode.CLAMP);
                return linearGradient;
            }
        };
        PaintDrawable paint = new PaintDrawable();
        paint.setShape(new RectShape());
        paint.setShaderFactory(shaderFactory);
        paint.setCornerRadius(7f);
        v.setBackgroundDrawable((Drawable) paint);

    }

    /**
     * Convert Integer to Ordinal
     *
     * @param i
     * @return
     */
    public static String ordinal(int i) {
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:

                return i + "th";
            default:
                return i + sufixes[i % 10];

        }
    }

    /**
     * Increase some text size of TextView
     *
     * @param spannable       : Spannable String
     * @param path            : String to be increase size
     * @param fontSizeInPixel : Integer Value to increase size
     */
    public static void setFontSizeForPath(Spannable spannable, String path, int fontSizeInPixel) {
        int startIndexOfPath = spannable.toString().indexOf(path);
        spannable.setSpan(new AbsoluteSizeSpan(fontSizeInPixel), startIndexOfPath,
                startIndexOfPath + path.length(), 0);
    }

    /**
     * Check Passing String value if null or blank then send Hyphen
     *
     * @param s_data_string : String Value
     * @return
     */
    public static String checkNullStringForHyphen(String s_data_string) {
        if (s_data_string.equals(null) || s_data_string.equals("null") || s_data_string.equals("")) {
            if (s_data_string.length() >= 5) {
                if (s_data_string.substring(0, 5).equals("User_")) {
                    return "";
                }
            } else {
                return "";
            }
            return "";
        }
        return s_data_string;
    }

    public static void requestPermission(Context context, int request_code, String... permissions) {

        //below android 11
        if (SDK_INT >= Build.VERSION_CODES.M) {
            ((Activity) context).requestPermissions(permissions,
                    request_code);
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((Activity) context).requestPermissions(permissions,
                    request_code);
        }*/
    }

    // Methods to Check and Ask for Permissions
    public static boolean checkPermission(Context context, String... s_permissions) {
        if (context != null && s_permissions != null) {
            for (String permission : s_permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getBase64Image(String encodedImage, String image_path, String fileBase64, Context context, ImageView imageView, Bitmap photo) {
        String fileBase64Image = fileBase64;
        File file = new File(image_path);
        long length = file.length();
        length = length / 1024;
        Log.d("size", length + "");
        if (length > 1000) {
            //fileBase64Image = "";
            Toast.makeText(context, context.getString(R.string.error_file_size), Toast.LENGTH_LONG).show();
            int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
            Log.d("file_size", "" + file_size);

            File compressedImage = new File(image_path);

            int file_size1 = Integer.parseInt(String.valueOf(compressedImage.length() / 1024));
            Log.d("file_size1", "" + file_size1);
            long lengthsize = compressedImage.length();
            lengthsize = lengthsize / 1024;
            Log.d("file_size1", "" + lengthsize);
            if (lengthsize > 1000) {
                Toast.makeText(context, context.getString(R.string.error_file_size), Toast.LENGTH_LONG).show();
            } else {
                String file_name = compressedImage.getName();
                String type = MimeTypeMap.getFileExtensionFromUrl(file_name);
                //android.util.Log.e("EXTENSION", type);
                if (imageView != null) {
                    Glide.with(context).asBitmap()
                            .load(compressedImage.getAbsoluteFile())
                            .thumbnail(0.1f)
                            .apply(new RequestOptions().dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.NONE).placeholder(R.drawable.user).skipMemoryCache(true))
                            .into(imageView);

                }
                if (type != null) {
                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(type);
                    android.util.Log.e("TYPE", type);
                } else {
                    type = "";
                }
                String image_path1 = compressedImage.getAbsolutePath();
                // imageView.setImageBitmap(BitmapFactory.decodeFile(image_path1));
                String encodedImage1 = image(image_path1);
                fileBase64Image = "data:" + type + ";base64," + encodedImage1;
            }


        } else {
            //ivLogo.setImageBitmap(photo);
            //ivLogo.setBackgroundColor(getResources().getColor(android.R.color.white));

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 80, stream);
            if (imageView != null) {
                Glide.with(context).asBitmap()
                        .load(stream.toByteArray())
                        .thumbnail(0.1f)
                        .apply(new RequestOptions().dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.NONE).placeholder(R.drawable.user).skipMemoryCache(true))
                        .into(imageView);
            }

            String file_name = file.getName();
            String type = MimeTypeMap.getFileExtensionFromUrl(file_name);
            android.util.Log.e("EXTENSION1", type);
            if (type != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(type);
                android.util.Log.e("TYPE1", type);
            } else {
                type = "";
            }
            fileBase64Image = "data:" + type + ";base64," + encodedImage;
        }
        return fileBase64Image;
    }

    public static String image(String image_path) {
        Bitmap bm = BitmapFactory.decodeFile(image_path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] byteArrayImage = baos.toByteArray();
        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String numberFormat(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        DecimalFormat format = new DecimalFormat("0.#");
        String value = format.format(count / Math.pow(1000, exp));
        return String.format("%s%c", value, "kMBTPE".charAt(exp - 1));
    }

    public static boolean isValidMobile(String phone) {
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() > 6 && phone.length() <= 13;
        }
        return false;
    }

    public static boolean isValidMobileNumber(String phone, int numberLimit) {
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() == numberLimit;
        }
        return false;
    }

    public static void lockOrientation(Activity activity, String oType) {
        switch (oType) {
            case VIEW_PORTRAIT:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case VIEW_LANDSCAPE:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case VIEW_UNSPECIFIED:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                break;
        }
    }

    public static int compareVersionNames(String oldVersionName, String newVersionName) {
        int res = 0;

        String[] oldNumbers = oldVersionName.split("\\.");
        String[] newNumbers = newVersionName.split("\\.");

        // To avoid IndexOutOfBounds
        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i++) {
            int oldVersionPart = Integer.valueOf(oldNumbers[i]);
            int newVersionPart = Integer.valueOf(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = 1;
                break;
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length) ? 1 : -1;
        }

        return res;
    }

    public static boolean isAppUpdateRequired(String oldVersionName, String newVersionName) {
        int version = compareVersionNames(oldVersionName, newVersionName);
        if (version == 1) {
            return true;
        }
        return false;
    }

    public static String openssl_encrypt(String data, String strKey, String strIv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(strKey.getBytes(), "UTF-8");
        IvParameterSpec iv = new IvParameterSpec(strIv.getBytes(), 0, cipher.getBlockSize());

        // Encrypt
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedCipherBytes = cipher.doFinal(data.getBytes());

        return Base64.encodeToString(encryptedCipherBytes, Base64.DEFAULT);
    }

    public static String encryptData(String data, String strKey, String strIv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(strKey.getBytes(), "UTF-8");
        IvParameterSpec iv = new IvParameterSpec(strIv.getBytes(), 0, cipher.getBlockSize());

        // Encrypt
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedCipherBytes = cipher.doFinal(data.getBytes());

        String hex = "";

        // Iterating through each byte in the array
        for (byte i : encryptedCipherBytes) {
            hex += String.format("%02X", i);
        }

        return hex;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encryptData(String message, String secret) throws Exception {
        /* Encrypt the message. */

        byte[] decoded = java.util.Base64.getDecoder().decode(secret.getBytes());
        Key key = new SecretKeySpec(decoded, "AES/ECB/PKCS5Padding");

        Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");

        c.init(Cipher.ENCRYPT_MODE, key);

        byte[] encVal = c.doFinal(message.getBytes());

        String encryptedValue = java.util.Base64.getEncoder().encodeToString(encVal);
        return encryptedValue;
       /* Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(message.getBytes());
        String str=Base64.encodeToString(cipherText, Base64.DEFAULT);
        return str;*/
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decryptData(String cipherText, String secret) throws Exception {
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        byte[] decoded = java.util.Base64.getDecoder().decode(secret.getBytes());
        Key key = new SecretKeySpec(decoded, "AES/ECB/PKCS5Padding");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] bytes = java.util.Base64.getDecoder().decode(cipherText.getBytes());

        String decryptString = new String(cipher.doFinal(bytes), "UTF-8");

        return decryptString;

        /*Cipher cipher = null;
        byte[] bytes = Base64.decode(cipherText, Base64.DEFAULT);
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "UTF-8");
        cipher.init(Cipher.DECRYPT_MODE, key);
        String decryptString = new String(cipher.doFinal(bytes), "UTF-8");
        return decryptString;*/
    }

    public static String getCurrentDateTimeInMillis() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        long time = System.currentTimeMillis();
        return String.valueOf(time);
    }

    public static boolean isValidForOnClickAD(String id, Context mContext) {
        List<AdMobData> list = LocalStorage.getOnClickAdMobList(LocalStorage.KEY_ONCLICK_AD_MOB, mContext);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).id.equals(id)) {
                    if (list.get(i).show.equalsIgnoreCase("true")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getOnClickAdType(String id, Context mContext) {
        List<AdMobData> list = LocalStorage.getOnClickAdMobList(LocalStorage.KEY_ONCLICK_AD_MOB, mContext);
        if (Utility.isValidForOnClickAD(id, mContext)) {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).id.equals(id)) {
                        if (list.get(i).type.equalsIgnoreCase(Constant.ADMOB)) {
                            return list.get(i).type;
                        } else if (list.get(i).type.equalsIgnoreCase(Constant.CUSTOM)) {
                            return list.get(i).type;
                        }
                    }
                }
            }

        }
        return "";
    }

    public static boolean isValidForBannerAD(String id, Context mContext) {
        List<AdMobData> list = LocalStorage.getBannerAdMobList(LocalStorage.KEY_BANNER_AD_MOB, mContext);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).id.equals(id)) {
                    if (list.get(i).show.equalsIgnoreCase("true")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getBannerAdType(String id, Context mContext) {
        List<AdMobData> list = LocalStorage.getBannerAdMobList(LocalStorage.KEY_BANNER_AD_MOB, mContext);
        if (Utility.isValidForBannerAD(id, mContext)) {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).id.equals(id)) {
                        if (list.get(i).type.equalsIgnoreCase(Constant.ADMOB)) {
                            return list.get(i).type;
                        } else if (list.get(i).type.equalsIgnoreCase(Constant.CUSTOM)) {
                            return list.get(i).type;
                        }
                    }
                }
            }

        }
        return "";
    }

    public static File saveImage(final Context context, final String imageData) throws IOException {
        final byte[] imgBytesData = android.util.Base64.decode(imageData,
                android.util.Base64.DEFAULT);

        final File file = File.createTempFile("image", null, context.getCacheDir());
        final FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                fileOutputStream);
        try {
            bufferedOutputStream.write(imgBytesData);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static boolean isJson(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            Log.i("JSONObject", jsonObject.toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void openCustomTab(Activity activity, CustomTabsIntent customTabsIntent, Uri uri) {
        // package name is the default package
        // for our custom chrome tab
        String packageName = "com.android.chrome";
        if (packageName != null) {

            // we are checking if the package name is not null
            // if package name is not null then we are calling
            // that custom chrome tab with intent by passing its
            // package name.
            customTabsIntent.intent.setPackage(packageName);

            // in that custom tab intent we are passing
            // our url which we have to browse.
            customTabsIntent.launchUrl(activity, uri);
        } else {
            // if the custom tabs fails to load then we are simply
            // redirecting our user to users device default browser.
            activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    @SuppressLint("HardwareIds")
    public static DeviceDetailsData getSystemDetail() {
        return new DeviceDetailsData(Build.BRAND,
                Settings.Secure.getString(MyApp.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID),
                Build.MODEL, Build.ID,
                Build.VERSION.SDK_INT,
                Build.MANUFACTURER,
                Build.USER,
                Build.TYPE,
                Build.VERSION_CODES.BASE,
                Build.VERSION.INCREMENTAL,
                Build.BOARD,
                Build.HOST,
                Build.FINGERPRINT,
                Build.VERSION.RELEASE);
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        return currentDateTime;
    }

    public static boolean isHtml(String string) {
        Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
        boolean isHTML = htmlPattern.matcher(string).matches();
        return isHTML;
    }

    public static String getMessage(Context mContext, String data) {
        Object json = null;
        try {
            json = new JSONTokener(data).nextValue();
            if (json instanceof JSONObject) {
                JSONObject jsonObject = new JSONObject(data);
                String lang = LocalStorage.getSelectedLanguage(mContext);
                Log.i("lang", lang);
                List<LanguagesData> languagesData = LocalStorage.getLanguagesList(LocalStorage.KEY_LANGUAGES_LIST, mContext);
                if (languagesData != null && !languagesData.isEmpty() && !lang.isEmpty()) {
                    for (int i = 0; i < languagesData.size(); i++) {
                        Log.i("shortcode", (languagesData.get(i).short_code));
                        if (languagesData.get(i).short_code.equalsIgnoreCase(lang)) {
                            if (jsonObject.has(lang)) {
                                Log.i("has", jsonObject.getString(lang));
                                return jsonObject.getString(lang);
                            } else {
                                Log.i("not has", jsonObject.getString("en"));
                                return jsonObject.getString("en");
                            }
                        }
                    }
                } else {
                    Log.i("all empty", jsonObject.getString("en"));
                    return jsonObject.getString("en");
                }
                Log.i("ISJSON", data);
                return data;
            } else {
                Log.i("ISJSON", data);
                return data;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getUserCountry(Context mContext, String callingCode) {
        try {
            if (LocalStorage.getCountriesList(LocalStorage.KEY_COUNTRIES_LIST, mContext) != null) {
                List<CountryData> countryDataList = LocalStorage.getCountriesList(LocalStorage.KEY_COUNTRIES_LIST, mContext);
                for (int i = 0; i < countryDataList.size(); i++) {
                    if (countryDataList.get(i).countryCode.equalsIgnoreCase(callingCode)) {
                        return String.valueOf(countryDataList.get(i).id);
                    }
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public static String getLanguagePref(Context mContext) {
        try {
            if (LocalStorage.getLanguagesList(LocalStorage.KEY_LANGUAGES_LIST, mContext) != null) {
                List<LanguagesData> languages = LocalStorage.getLanguagesList(LocalStorage.KEY_LANGUAGES_LIST, mContext);
                for (int i = 0; i < languages.size(); i++) {
                    if (LocalStorage.getSelectedLanguage(mContext).equalsIgnoreCase("")) {
                        if (languages.get(i).short_code.equalsIgnoreCase(Constant.DEFAULT_LANGUAGE)) {
                            return languages.get(i).short_code;
                        }
                    } else {
                        if (languages.get(i).short_code.equalsIgnoreCase(LocalStorage.getSelectedLanguage(mContext))) {
                            return languages.get(i).short_code;
                        }
                    }
                }
            } else {
                return Constant.DEFAULT_LANGUAGE;
            }
            return Constant.DEFAULT_LANGUAGE;
        } catch (Exception e) {
            return Constant.DEFAULT_LANGUAGE;
        }
    }

    public static void customEventsTracking(String eventId, String title) {
        try {
            if (!BuildConfig.DEBUG) {
                // The arguments - Events id
                TrackierEvent event = new TrackierEvent(eventId);
                event.param1 = LocalStorage.getUserData().mobile;

                TrackierSDK.setUserId(LocalStorage.getUserId()); // Pass the UserId values here

                // Passing the extra data through customs params
                HashMap<String, Object> eventCustomParams = new HashMap<>();
                if (LocalStorage.getLocationData() != null) {
                    eventCustomParams.put("State", LocalStorage.getLocationData().getState());
                    eventCustomParams.put("City", LocalStorage.getLocationData().getCity());
                } else {
                    eventCustomParams.put("State", "");
                    eventCustomParams.put("City", "");
                }
                eventCustomParams.put("Brand", Utility.getSystemDetail().getBrand());
                eventCustomParams.put("Model", Utility.getSystemDetail().getModel());
                if (!title.isEmpty() && !eventId.equals(Constant.Subscription)) {
                    eventCustomParams.put("Title", title);
                }
                if (eventId.equals(Constant.Subscription)) {
                    event.revenue = Double.parseDouble(title);
                    event.currency = LocalStorage.getUserData().currencyCode;
                }
                event.ev = eventCustomParams; // Pass the reference to the ev
                TrackierSDK.trackEvent(event);
                Log.d("TAG", "onClick: event_track ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isTelevision() {
        boolean isTelevision = MyApp.getAppContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK);
        return true;
    }

    public static void getLocationPermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(activity
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION}
                    , 234);
        }
    }

    public static void showKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void focusListener(View view) {
        Drawable btAgreeBg = view.getBackground();
        view.setPadding(10, 10, 10, 10);
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.hasFocus()) {
                    view.setBackground(view.getContext().getDrawable(R.drawable.highlight_border));
                } else {
                    view.setBackground(btAgreeBg);
                }
            }
        });
    }

    public static void textFocusListener(View view) {
        Drawable btAgreeBg = view.getBackground();
        view.setPadding(10, 5, 10, 5);
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.hasFocus()) {
                    view.setBackground(view.getContext().getDrawable(R.drawable.highlight_text_field));
                } else {
                    view.setBackground(btAgreeBg);
                }
            }
        });
    }

    public static void buttonFocusListener(View view) {
        if (Utility.isTelevision()) {
            view.setBackground(view.getContext().getDrawable(R.drawable.bg_button_grey));
            view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (view.hasFocus()) {
                        view.setBackground(view.getContext().getDrawable(R.drawable.bg_accent));
                    } else {
                        view.setBackground(view.getContext().getDrawable(R.drawable.bg_button_grey));
                    }
                }
            });
        }
    }
}
