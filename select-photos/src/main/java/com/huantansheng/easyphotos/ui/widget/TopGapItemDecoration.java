package com.huantansheng.easyphotos.ui.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class TopGapItemDecoration extends RecyclerView.ItemDecoration {
    private int gap = 0;

    public TopGapItemDecoration(float gap) {
        super();
        this.gap = new Float(gap).intValue();
    }

    public TopGapItemDecoration() {
        super();
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent) {
        super.onDraw(canvas, parent);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent) {
        super.onDrawOver(canvas, parent);
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        super.getItemOffsets(outRect, itemPosition, parent);
        if (itemPosition == 0) {
            if (this.gap == 0) {
                outRect.top = 20;
            } else {
                outRect.top = gap;
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }
}

