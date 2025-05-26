package com.app.itaptv.activity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.itaptv.R;
import com.app.itaptv.fragment.VerifyEmailMobile;

public class VerificationActivity extends BaseActivity {

    String key;
    public static final String TYPE_KEY = "type";
    public static final String TYPE_EMAIL = "email";
    public static final String TYPE_MOBILE = "mobile";
    public static final String GIVE_AWAY_TYPE_MOBILE = "mobile1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        init();

    }

    public void init() {
        if (getIntent().getExtras() != null) {
            key = getIntent().getExtras().getString(TYPE_KEY);
        }

        if (key != null) {
            switch (key) {
                case TYPE_EMAIL:
                    setOpenEditEmail();
                    break;
                case TYPE_MOBILE:
                    setOpenEditMobile(false);
                    break;
                case GIVE_AWAY_TYPE_MOBILE:
                    setOpenEditMobile(true);
                    break;
            }
        }
    }

    public void setOpenEditMobile(boolean isGiveAway) {
        VerifyEmailMobile verifyEmailMobile = new VerifyEmailMobile();
        Bundle bundle = new Bundle();
        bundle.putString("edit", "mobile");
        if (isGiveAway) {
            bundle.putBoolean("GiveAwayActivity", true);
        } else {
            bundle.putBoolean("VerificationActivity", true);
        }
        verifyEmailMobile.setArguments(bundle);
        openFragment(verifyEmailMobile);
    }

    public void setOpenEditEmail() {
        VerifyEmailMobile verifyEmailMobile = new VerifyEmailMobile();
        Bundle bundle = new Bundle();
        bundle.putString("edit", "email");
        bundle.putBoolean("VerificationActivity", true);
        verifyEmailMobile.setArguments(bundle);
        openFragment(verifyEmailMobile);
    }

    /**
     * Open specific fragment on BottomNavigationView item selected
     *
     * @param fragment Fragment object to display the Fragment
     */
    private void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
