package com.app.itaptv.custom_widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import androidx.annotation.NonNull;

import android.view.Window;

import com.app.itaptv.R;

public class GameStateIndicatorDialog extends Dialog implements Dialog.OnDismissListener {

    private static final int DISMISS_DURATION = 1200;

    final Handler handler = new Handler();
    final Runnable runnable = () -> {
        if (isShowing()) {
            dismiss();
        }
    };

    public GameStateIndicatorDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        setCancelable(false);
        //setCanceledOnTouchOutside(false);
        int width = (int)(context.getResources().getDisplayMetrics().widthPixels);
        int height = (int)(context.getResources().getDisplayMetrics().heightPixels);
        getWindow().setLayout(width, height);

    }

    @Override
    public void show() {
        super.show();
        handler.postDelayed(runnable, DISMISS_DURATION);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        handler.removeCallbacks(runnable);
    }


}
