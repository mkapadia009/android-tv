package com.app.itaptv.activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.app.itaptv.LocaleHelper;
import com.app.itaptv.R;
import com.app.itaptv.structure.LanguagesData;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class LanguageSettingsActivity extends BaseActivity {

    Button btChooseLanguage;
    //TextView tvLanguage;
    RadioGroup rgLanguages;
    int position = 0;
    private RadioButton[] rbLanguages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_settings);
        setTheme(R.style.Theme_Leanback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        init();
    }

    private void init() {
        setToolbar(true);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(true);
        setCustomizedTitle(0);
        setToolbarTitle(getString(R.string.label_language_setting));

        btChooseLanguage = findViewById(R.id.btChooseLanguage);
        //tvLanguage = findViewById(R.id.tvLanguage);
        rgLanguages = findViewById(R.id.rgLanguages);
        getLanguagesData();
    }

    private void getLanguagesData() {
        if (LocalStorage.getLanguagesList(LocalStorage.KEY_LANGUAGES_LIST, LanguageSettingsActivity.this) != null) {
            List<LanguagesData> languagesDataArrayList = LocalStorage.getLanguagesList(LocalStorage.KEY_LANGUAGES_LIST, LanguageSettingsActivity.this);
            ArrayList<String> languages = new ArrayList<>();
            for (int i = 0; i < languagesDataArrayList.size(); i++) {
                languages.add(languagesDataArrayList.get(i).language + " " + getString(R.string.opening_bracket) + languagesDataArrayList.get(i).short_code.toUpperCase() + getString(R.string.closing_bracket));
            }
            createRadioButton(languages, languagesDataArrayList);
            if (languagesDataArrayList.size() > 1) {
                btChooseLanguage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseLanguage(languagesDataArrayList.get(rgLanguages.getCheckedRadioButtonId()).short_code);
                    }
                });
            } else {
                btChooseLanguage.setClickable(false);
            }
        }
    }

    private void createRadioButton(ArrayList<String> languages, List<LanguagesData> languagesDataArrayList) {
        int pos = 0;
        rbLanguages = new RadioButton[languages.size()];
        rgLanguages.setOrientation(RadioGroup.VERTICAL);
        for (int i = 0; i < languages.size(); i++) {
            rbLanguages[i] = new RadioButton(this);
            rbLanguages[i].setText(languages.get(i));
            rbLanguages[i].setId(i);
            rbLanguages[i].setTextColor(getResources().getColor(R.color.white));
            rbLanguages[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            ColorStateList colorStateList = new ColorStateList(
                    new int[][]
                            {
                                    new int[]{-android.R.attr.state_enabled}, // Disabled
                                    new int[]{android.R.attr.state_enabled}   // Enabled
                            },
                    new int[]
                            {
                                    getColor(R.color.colorAccent), // disabled
                                    getColor(R.color.colorAccent) // enabled
                            }
            );

            rbLanguages[i].setButtonTintList(colorStateList); // set the color tint list
            rbLanguages[i].setPadding(20, 70, 20, 70);

            rgLanguages.addView(rbLanguages[i]);

            if (i < languages.size() - 1) {
                View v = new View(this);
                v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                v.setBackgroundColor(getColor(R.color.tab_text_grey));
                rgLanguages.addView(v);
            }
            if (LocalStorage.getSelectedLanguage(LanguageSettingsActivity.this).equalsIgnoreCase("")) {
                if (languagesDataArrayList.get(i).short_code.equalsIgnoreCase("en")) {
                    pos = i;
                }
            } else {
                if (languagesDataArrayList.get(i).short_code.equalsIgnoreCase(LocalStorage.getSelectedLanguage(LanguageSettingsActivity.this))) {
                    pos = i;
                }
            }
        }
        rbLanguages[pos].setChecked(true);
    }

    private void chooseLanguage(String selectedLanguage) {
        switch (selectedLanguage) {
            case "hi":
                //setLocale("hi");
                LocaleHelper.setLocale(LanguageSettingsActivity.this, "hi");
                restart();
                break;
            default:
                LocaleHelper.setLocale(LanguageSettingsActivity.this, "en");
                restart();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    public void restart() {
        Intent intent = new Intent(LanguageSettingsActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Constant.ACTION_RESTART = "restart";
        finish();
    }
}