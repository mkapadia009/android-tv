package com.app.itaptv.builder;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.itaptv.R;

/**
 * Created by poonam on 4/6/18.
 */


public class CustomProgressBar {
    Context context;
    View view;
    Dialog alertDialog;
    boolean dialogCancelable;
    int dialogType;
    int dialogBackgroundDrawable;
    int dialogHeight;
    int dialogWidth;

    private CustomProgressBar(ProgressDialogBuilder builder) {
        this.context = builder.context;
        this.view = builder.view;
        this.dialogCancelable = builder.dialogCancelable;
        this.dialogBackgroundDrawable = builder.dialogBackgroundDrawable;
        this.dialogWidth = builder.dialogWidth;
        this.dialogHeight = builder.dialogHeight;
        this.dialogType = builder.dialogType;
    }

    public void show() {

        alertDialog = new Dialog(context);
        alertDialog.setCancelable(dialogCancelable);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.loader, null);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(dialogBackgroundDrawable));
        alertDialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        alertDialog.show();
    }

    public void dismiss() {
        if(alertDialog!=null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public boolean isShowing() {
        if (alertDialog == null)
            return false;
        return alertDialog.isShowing();
    }


    public static class ProgressDialogBuilder {

        Context context;
        View view;
        boolean dialogCancelable;
        int dialogType;
        int dialogBackgroundDrawable;
        int dialogHeight;
        int dialogWidth;

        public static int DIALOG_WITH_PROGRESS = 1;

        public ProgressDialogBuilder(Context context) {
            this.context = context;

        }

        public ProgressDialogBuilder(Context context, View view) {
            this.context = context;
            this.view = view;

        }

        public ProgressDialogBuilder dialogType(int dialogType) {
            this.dialogType = dialogType;
            return this;
        }

        public ProgressDialogBuilder cancelable(boolean dialogCancelable) {
            this.dialogCancelable = dialogCancelable;
            return this;
        }

        public ProgressDialogBuilder backgroundDrawable(int dialogBackgroundDrawable) {
            this.dialogBackgroundDrawable = dialogBackgroundDrawable;
            return this;
        }

        public ProgressDialogBuilder dialogHeight(int dialogHeight) {
            this.dialogHeight = dialogHeight;
            return this;
        }

        public ProgressDialogBuilder setDialogWidth(int dialogWidth) {
            this.dialogWidth = dialogWidth;
            return this;
        }

        public CustomProgressBar build() {
            return new CustomProgressBar(this);
        }
    }
}

