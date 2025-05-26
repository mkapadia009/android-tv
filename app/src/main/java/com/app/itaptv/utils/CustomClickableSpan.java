package com.app.itaptv.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.BrowserActivity;

/**
 * Created by poonam on 11/9/18.
 */

public class CustomClickableSpan extends ClickableSpan {
    Context context;
    int position;

    public CustomClickableSpan(Context context, int position) {
        this.position = position;
        this.context = context;
    }

    @Override
    public void onClick(View widget) {

        switch (position) {
            case 0:
                context.startActivity(new Intent(context, BrowserActivity.class).putExtra("title", context.getResources().getString(R.string.label_terms_of_uses)).putExtra("posturl", Url.TERMS_OF_USE));
                break;
            case 1:
                context.startActivity(new Intent(context, BrowserActivity.class).putExtra("title", context.getResources().getString(R.string.label_privacy_policy)).putExtra("posturl", Url.PRIVACY_POLICY));
                break;
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(true);
        ds.setColor(context.getResources().getColor(R.color.colorGray));
    }
}
