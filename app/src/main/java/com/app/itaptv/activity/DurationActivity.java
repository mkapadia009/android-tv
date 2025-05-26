package com.app.itaptv.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.PriceData;

import java.util.ArrayList;

/**
 * Created by poonam on 11/12/18.
 */

public class DurationActivity extends AppCompatActivity {
    public static String PRICE_DATA = "PriceData";
    public static String SELECTED_DURATION = "selectedDuration";
    public static String POSITION = "position";


    LinearLayout llDuration;
    ImageView ivClose;

    String selectedDuration = "";
    ArrayList<PriceData> arrayListPriceData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duration);
        init();
    }

    private void init() {
        if (getIntent() != null) {
            arrayListPriceData = getIntent().getParcelableArrayListExtra(PRICE_DATA);
            selectedDuration = getIntent().getStringExtra(SELECTED_DURATION);
        }

        llDuration = findViewById(R.id.llDuration);
        ivClose = findViewById(R.id.ivClose);
        setDurationData();
    }

    public void finishActivity(View view) {
        finish();
    }

    public void selectDuration(View view) {
        int position = view.getId();
        Intent intent = getIntent();
        intent.putExtra(POSITION, position);
        setResult(RESULT_OK, intent);
        finish();

    }

    private void setDurationData() {
        if (arrayListPriceData != null) {
            for (int i = 0; i < arrayListPriceData.size(); i++) {
                PriceData priceData = arrayListPriceData.get(i);
                View view = getLayoutInflater().inflate(R.layout.row_spinner, null);
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(layoutParams);
                llDuration.addView(view);

                TextView tvSpinnerData = view.findViewById(R.id.tvSpinnerData);

                // Get Data
                String duration = priceData.duration;
                String durationType = priceData.durationType;
                durationType = durationType.substring(0, 1).toUpperCase() + durationType.substring(1);
                String totalDuration = String.format(getString(R.string.total_duration), duration, durationType);

                tvSpinnerData.setText(totalDuration);
                tvSpinnerData.setId(i);
                setTextViewStyle(tvSpinnerData, totalDuration);
            }
        }
    }

    private void setTextViewStyle(TextView textView, String currentDuration) {
        int textSize_9 = getResources().getDimensionPixelSize(R.dimen.sp_9);
        int textSize_12 = getResources().getDimensionPixelSize(R.dimen.sp_12);
        if (selectedDuration.equalsIgnoreCase(currentDuration)) {
            textView.setTextSize(textSize_12);
            textView.setTypeface(ResourcesCompat.getFont(this, R.font.rubik_medium));
            textView.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            textView.setTextSize(textSize_9);
            textView.setTypeface(ResourcesCompat.getFont(this, R.font.rubik_regular));

        }
    }
}
