/*
package com.app.itap.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.app.itap.R;
import com.vav.cn.directhelper.VavDirectGenerator;
import com.vav.cn.listener.OnCloseFragmentListener;
import com.vav.cn.listener.OnLoginCallbackSuccess;

public class VAVActivity extends AppCompatActivity implements OnLoginCallbackSuccess, OnCloseFragmentListener {

    private Fragment currentFragment;
    private boolean isAudioPlaying = false;
    public static int REQUEST_CODE = 333;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vav);
    }

    */
/**
     * ImageView click action.
     *//*

    public void clickAction(View view) {
        if (view.getId() == R.id.logo) {
            listenAudio();
        } else if (view.getId() == R.id.savedVouchersBtn) {
            showSavedVouchers();
        }
    }

    */
/**
     * Show saved vouchers.
     *//*

    private void showSavedVouchers() {
        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            FragmentTransaction transaction = fm.beginTransaction();
            if (transaction != null) {
                showComponens(false);
                currentFragment = VavDirectGenerator.getInstance()
                        .goToVoucherBook(transaction, R.id.frame_home_member_content, this);
            }
        }
    }

    */
/**
     * Listen to audio.
     *//*

    private void listenAudio() {
        VavDirectGenerator.getInstance().goToVavingBox(this);
        Log.i("VAV SDK", "Kalpesh started playing");
        isAudioPlaying = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isAudioPlaying = false;
                Log.i("VAV SDK", "Kalpesh stopped playing");
            }
        }, 8000);
    }

    */
/**
     * OnCloseFragmentListener
     * Called when close button clicked on actionbar.
     *//*

    @Override
    public void onCloseFragment() {
        Log.i("VAV SDK", "onCloseFragment");
        onBackPressed();
    }

    */
/**
     * OnLoginCallbackSuccess
     *//*

    @Override
    public void onLoginCallback(boolean b, String s) {
        Log.i("VAV SDK", "onLoginCallback: Boolean: " + b + " String: " + s);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.i("VAV SDK", "onPointerCaptureChanged: hasCapture: " + hasCapture);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (currentFragment != null) {
            currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        if (currentFragment != null) {
            FragmentManager fm = getFragmentManager();
            if (fm != null) {
                FragmentTransaction transaction = fm.beginTransaction();
                if (transaction != null) {
                    transaction.remove(currentFragment);
                    transaction.commit();
                    currentFragment = null;
                    showComponens(true);
                    return;
                }
            }
        }

        if (isAudioPlaying) return;
        Intent returnIntent = new Intent();
        returnIntent.putExtra("ok", "0k");
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    private void showComponens(boolean show) {
        findViewById(R.id.logo).setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.infoLbl).setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.savedVouchersBtn).setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.llvavchannel).setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.info).setVisibility(show ? View.VISIBLE : View.GONE);
    }

}
*/
