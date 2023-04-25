package com.example.coffee_shop_app.utils.styles;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewGapDecoration extends RecyclerView.ItemDecoration {
    private int verticalSpace;
    private int horizontalSpace;

    public RecyclerViewGapDecoration(int verticalSpace) {
        this.verticalSpace = verticalSpace;
        horizontalSpace = 0;
    }

    public RecyclerViewGapDecoration(int verticalSpace, int horizontalSpace) {
        this.verticalSpace = verticalSpace;
        this.horizontalSpace = horizontalSpace;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = 0;
            outRect.left = 0;
        } else {
            outRect.top = verticalSpace;
            outRect.left = horizontalSpace;
        }
    }
}
