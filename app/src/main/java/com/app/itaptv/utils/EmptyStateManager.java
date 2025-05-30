package com.app.itaptv.utils;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.itaptv.R;
import com.app.itaptv.font_awesome.FontAwesome;

import org.jetbrains.annotations.Nullable;

public class EmptyStateManager implements View.OnClickListener {

    AppCompatActivity activity = null;
    ActionBtnCallBack actionBtnCallBack = null;
    TextView tvMsg, tvMsg2, tvIcon;
    ImageView ivIcon;
    Button btn;

    private String message;
    private String actionButton;
    private String iconFA;
    private int iconRes;
    public static final String ACTION_LOGIN = "Login";
    public static final String ACTION_RETRY = "Retry";
    public static final String GO_TO_BUY = "Check Out Store";
    public static final String GO_TO_FEEDS_LISTING = "Browse Videos";

    private EmptyStateManager(AppCompatActivity activity, ActionBtnCallBack actionBtnCallBack) {
        this.activity = activity;
        this.actionBtnCallBack = actionBtnCallBack;

        tvMsg = activity.findViewById(R.id.tvMsg);
        tvMsg2 = activity.findViewById(R.id.tvMsg2);
        tvIcon = activity.findViewById(R.id.tvIcon);
        ivIcon = activity.findViewById(R.id.ivIcon);
        btn = activity.findViewById(R.id.btn);

        btn.setOnClickListener(this);
    }

    private EmptyStateManager(View view, AppCompatActivity activity, ActionBtnCallBack actionBtnCallBack) {
        this.activity = activity;
        this.actionBtnCallBack = actionBtnCallBack;

        tvMsg = view.findViewById(R.id.tvMsg);
        tvMsg2 = view.findViewById(R.id.tvMsg2);
        tvIcon = view.findViewById(R.id.tvIcon);
        ivIcon = view.findViewById(R.id.ivIcon);
        btn = view.findViewById(R.id.btn);

        btn.setOnClickListener(this);
    }

    public static EmptyStateManager setUpInActivity(AppCompatActivity activity,
                                                    EmptyStateManager.ActionBtnCallBack actionBtnCallBack) {
        return new EmptyStateManager(activity, actionBtnCallBack);
    }

    public static EmptyStateManager setUpInFragment(View view, AppCompatActivity activity,
                                                    EmptyStateManager.ActionBtnCallBack actionBtnCallBack) {
        return new EmptyStateManager(view, activity, actionBtnCallBack);
    }

    @Override
    public void onClick(View v) {
        if (actionBtnCallBack != null) actionBtnCallBack.onBtnClick(actionButton);
    }

    /**
     * Sets the secondary message to the empty state view.
     *
     * @param secondaryMessage Message to be shown. Pass null to hide.
     */
    public void setSecondaryMsg(@Nullable String secondaryMessage) {
        tvMsg2.setText(secondaryMessage);
        if (secondaryMessage == null || secondaryMessage.isEmpty()) {
            tvMsg2.setVisibility(View.GONE);
        } else {
            tvMsg2.setVisibility(View.VISIBLE);
        }
    }

    public final void setImgAndMsg(String msg, String imgSrc) {
        this.setMessage(msg);
        this.setIconFA(imgSrc);
        this.setIconRes(0);
        this.setActionButton(null);
    }

    public final void setImgAndMsg(String msg, int imgSrc) {
        this.setMessage(msg);
        this.setIconRes(imgSrc);
        this.setIconFA(null);
        this.setActionButton(null);
    }

    public final void showNoConnectionState() {
        this.setMessage(activity.getString(R.string.no_internet_connection));
        // this.setActionButton("Retry");
        this.setIconRes(0);
    }

    public final void setColor(int col) {
        if (this.tvIcon != null) {
            tvIcon.setTextColor(col);
        }

        if (this.tvMsg != null) {
            tvMsg.setTextColor(col);
        }
    }

    public final void hide() {
        this.setIconFA(null);
        this.setIconRes(0);
        this.setMessage(null);
        this.setActionButton(null);
        this.setSecondaryMsg(null);
    }

    public final String getMessage() {
        return this.message;
    }

    public final void setMessage(String value) {
        this.message = value;
        tvMsg.setText(value);
        if (value == null) {
            tvMsg.setVisibility(View.GONE);
        } else {
            tvMsg.setVisibility(View.VISIBLE);
        }
    }

    public final String getIconFA() {
        return this.iconFA;
    }

    public final void setIconFA(String value) {
        this.iconFA = value;
        if (value != null) {
            FontAwesome.setIcon(activity, value, tvIcon);
            tvIcon.setVisibility(View.VISIBLE);
            this.setIconRes(0);
        } else {
            tvIcon.setText("");
            tvIcon.setVisibility(View.GONE);
        }

    }

    public final int getIconRes() {
        return this.iconRes;
    }

    public final void setIconRes(int value) {
        this.iconRes = value;
        if (value > 0) {
            ivIcon.setImageResource(value);
            ivIcon.setVisibility(View.VISIBLE);
            this.setIconFA(null);
        } else {
            ivIcon.setImageResource(value);
            ivIcon.setVisibility(View.GONE);
        }
    }

    public final String getActionButton() {
        return this.actionButton;
    }

    public final void setActionButton(String value) {
        this.actionButton = value;
        btn.setText(value);
        if (value == null) {
            btn.setVisibility(View.GONE);
        } else {
            btn.setVisibility(View.VISIBLE);
        }
    }

    public interface ActionBtnCallBack {
        void onBtnClick(String action);
    }
}
