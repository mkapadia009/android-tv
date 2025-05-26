package com.app.itaptv.utils;

import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by poonam on 4/9/18.
 */

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;
    private int displayMode;
    private int edge;

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int GRID = 2;

    public static final int LEFT = 10;
    public static final int RIGHT = 11;
    public static final int TOP = 12;
    public static final int BOTTOM = 13;

    public SpacingItemDecoration(int spacing, int edge) {
        this(spacing, -1, edge);
    }
    public SpacingItemDecoration(int spacing) {
        this(spacing, -1, -1);
    }
    public SpacingItemDecoration(int spacing, int displayMode, int edge) {
        this.spacing = spacing;
        this.displayMode = displayMode;
        this.edge = edge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildViewHolder(view).getAdapterPosition();
        int itemCount = state.getItemCount();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        setSpacingForDirection(outRect, layoutManager, position, itemCount);
    }

    private void setSpacingForDirection(Rect outRect,
                                        RecyclerView.LayoutManager layoutManager,
                                        int position,
                                        int itemCount) {

        // Resolve display mode automatically
        if (displayMode == -1) {
            displayMode = resolveDisplayMode(layoutManager);
        }

        switch (displayMode) {
            case HORIZONTAL:
                outRect.left = edge == LEFT ? spacing : 0;
                outRect.right = position == itemCount - 1 ? edge == RIGHT ? spacing : 0 : 0;
                outRect.top = edge == TOP ? spacing : 0;
                outRect.bottom = edge == BOTTOM ? spacing : 0;
                break;
            case VERTICAL:
                outRect.left = edge == LEFT ? spacing:0;
                outRect.right = edge == RIGHT ? spacing:0;
                outRect.top = edge == TOP ? spacing:0;
                outRect.bottom = position == itemCount - 1 ? edge == BOTTOM ? 0 : spacing  : spacing;
                break;
            case GRID:
                if (layoutManager instanceof GridLayoutManager) {
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                    int cols = gridLayoutManager.getSpanCount();
                    int rows = itemCount / cols;

                    outRect.left = spacing;
                    outRect.right = position % cols == cols - 1 ? spacing : 0;
                    outRect.top = spacing;
                    outRect.bottom = position / cols == rows - 1 ? spacing : 0;
                }
                break;
        }
    }

    private int resolveDisplayMode(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) return GRID;
        if (layoutManager.canScrollHorizontally()) return HORIZONTAL;
        return VERTICAL;
    }
}

