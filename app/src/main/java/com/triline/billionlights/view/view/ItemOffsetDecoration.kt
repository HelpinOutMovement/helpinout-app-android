package com.triline.billionlights.view.view

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes

class ItemOffsetDecoration(private val mItemOffset: Int) :
    androidx.recyclerview.widget.RecyclerView.ItemDecoration() {


    constructor(
        context: Context,
        @DimenRes itemOffsetId: Int
    ) : this(context.resources.getDimensionPixelSize(itemOffsetId)) {

    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: androidx.recyclerview.widget.RecyclerView,
        state: androidx.recyclerview.widget.RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset)
    }
}