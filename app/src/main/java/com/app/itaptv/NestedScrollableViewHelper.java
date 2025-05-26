package com.app.itaptv;

import androidx.core.widget.NestedScrollView;
import android.view.View;

import com.sothree.slidinguppanel.ScrollableViewHelper;

/**
 * Created by poonam on 22/11/18.
 */

public class NestedScrollableViewHelper extends ScrollableViewHelper{
    public int getScrollableViewScrollPosition(View scrollableView, boolean isSlidingUp) {
        if (scrollableView instanceof NestedScrollView) {
            if(isSlidingUp){
                return scrollableView.getScrollY();
            } else {
                NestedScrollView nsv = ((NestedScrollView) scrollableView);
                View child = nsv.getChildAt(0);
                return (child.getBottom() - (nsv.getHeight() + nsv.getScrollY()));
            }
        } else {
            return 0;
        }
    }
}
