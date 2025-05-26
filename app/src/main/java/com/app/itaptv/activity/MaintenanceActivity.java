package com.app.itaptv.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.app.itaptv.R;

public class MaintenanceActivity extends BaseActivity {

    TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        init();
    }

    public void init() {
        tvMessage = findViewById(R.id.tvMessage);
        if (getIntent() != null) {
            tvMessage.setText(getIntent().getStringExtra("Message"));
        }
    }
}