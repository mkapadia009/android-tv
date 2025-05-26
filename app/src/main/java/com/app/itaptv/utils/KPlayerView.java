package com.app.itaptv.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * A custom player view build using ViewPager.
 * The component has one mediaplayer view and the ad player view.
 */
public class KPlayerView extends ViewPager {

    /**
     * Constructor.
     *
     * @param context Context required.
     */
    public KPlayerView(@NonNull Context context) {
        super(context);
    }

    /**
     * Constructor.
     *
     * @param context Context required.
     * @param attrs   Attributes.
     */
    public KPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*int mode = MeasureSpec.getMode(heightMeasureSpec);
        // Unspecified means that the ViewPager is in a ScrollView WRAP_CONTENT.
        // At Most means that the ViewPager is not in a ScrollView WRAP_CONTENT.
        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
            // super has to be called in the beginning so the child views can be initialized.
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int height = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int childMeasuredHeight = child.getMeasuredHeight();
                if (childMeasuredHeight > height) {
                    height = childMeasuredHeight;
                }
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }*/
        // super has to be called again so the new specs are treated as exact measurements
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * SHow ad player view.
     *
     * @param animated Animates if true.
     */
    public void showMediaPlayer(boolean animated) {
        setCurrentItem(0, animated);
    }

    /**
     * SHow ad player view.
     *
     * @param animated Animates if true.
     */
    public void showAdPlayer(boolean animated) {
        setCurrentItem(1, animated);
    }

}
