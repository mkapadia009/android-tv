package com.app.itaptv.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.itaptv.R;

import java.util.ArrayList;

/**
 * Created by kalpesh on 07/11/17.
 */

public class AlertUtils {

    /**
     * Present alert with one button.
     *
     * @param title              = title for the alert dialog.
     * @param message            = message body for the alert dialog.
     * @param positiveActionText = text for positive action button. If null, takes the default value 'OK'.
     * @param context            = context to create alert dialog.
     * @param handler            = handler to get onclick event of action button.
     */
    public static void showAlert(String title, String message, String positiveActionText, Context context, final AlertClickHandler handler) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogBlack);
            if (null != title) {
                builder.setTitle(title);
            }
            if (null != message) {
                builder.setMessage(message);
            }
            String posText = context.getResources().getString(android.R.string.ok);
            if (positiveActionText != null) {
                posText = positiveActionText;
            }
            builder.setPositiveButton(posText, (dialog, which) -> {
                if (null != handler) {
                    handler.alertButtonAction(true);
                }
            });
            builder.setCancelable(false);
            if (!((Activity) context).isFinishing()) {
                builder.show();
            }
        }
    }

    /**
     * Present alert with one button.
     *
     * @param title              = title for the alert dialog.
     * @param message            = message body for the alert dialog.
     * @param positiveActionText = text for positive action button. If null, takes the default value 'OK'.
     * @param negativeActionText = text for negative action button. If null, takes the default value 'Cancel'.
     * @param context            = context to create alert dialog.
     * @param handler            = handler to get onclick event of action button.
     */
    public static void showAlert(String title, String message, String positiveActionText, String negativeActionText, Context context, final AlertClickHandler handler) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogBlack);
        if (null != title) {
            builder.setTitle(title);
        }
        if (null != message) {
            builder.setMessage(message);
        }
        String posText = context.getResources().getString(android.R.string.ok);
        if (positiveActionText != null) {
            posText = positiveActionText;
        }
        String negText = context.getResources().getString(android.R.string.cancel);
        if (negativeActionText != null) {
            negText = negativeActionText;
        }
        builder.setPositiveButton(posText, (dialog, which) -> {
            if (null != handler) {
                handler.alertButtonAction(true);
            }
        });
        builder.setNegativeButton(negText, (dialog, which) -> {
            if (null != handler) {
                handler.alertButtonAction(false);
            }
        });
        builder.setCancelable(false);
        //builder.setIcon(android.R.drawable.ic_dialog_alert);
        if (!((Activity) context).isFinishing()) {
            builder.show();
        }
    }

    /**
     * Show toast message.
     *
     * @param message  = message to show in the toast.
     * @param duration = time for the toast to flash.
     * @param context  = context to create the toast.
     */
    public static void showToast(String message, int duration, Context context) {
        Toast.makeText(context, message, duration).show();
    }

    /*public static void showErrorToast(VolleyError error, int duration, Context context) {
        String message = "";
        if (error instanceof NoConnectionError) {
            message = context.getString(R.string.error_msg_check_internet_connection);
        } else {
            message = context.getString(R.string.error_msg_some_error_occured);
        }

        Toast.makeText(context, message, duration).show();
    }*/

    public static void showAlert(Context context, int pos, ArrayList<String> languages, AlertCallback alertCallback) {
        final int[] position = {0};
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.Base_Theme_AppCompat_Dialog);
        builder.setTitle("Choose Language");
        builder.setSingleChoiceItems(languages.toArray(new String[languages.size()]), pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                position[0] = which;
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertCallback.alertCallback(languages.get(position[0]), position[0]);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    public static void showCountryDialog(Context context, int pos, ArrayList<String> countries, AlertCallback alertCallback) {
        final int[] position = {0};
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.Base_Theme_AppCompat_Dialog);
        builder.setTitle("Select Country");
        builder.setSingleChoiceItems(countries.toArray(new String[0]), pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                position[0] = which;
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertCallback.alertCallback(countries.get(position[0]), position[0]);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Alert button action listener.
     */
    public interface AlertClickHandler {
        /**
         * Called when the action buttons on the alert dialog are clicked.
         *
         * @param isPositiveAction = if true, positive action button is clicked else negative action button is clicked.
         */
        void alertButtonAction(boolean isPositiveAction);
    }

    public interface AlertCallback {
        void alertCallback(String selectedLanguage, int pos);
    }

    public static void showErrorDialog(Context mContext, String message) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.connection_dialog, null);
        TextView tvMessage = view.findViewById(R.id.tvMsg);
        tvMessage.setText(message);
        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        if (Utility.isTelevision()) {
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        dialog.dismiss();
                        ((Activity) mContext).finish();
                        System.exit(0);
                    }
                    return true;
                }
            });
        }
    }
}
