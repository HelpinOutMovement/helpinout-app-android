package com.triline.billionlights.view.view

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * Adds interior dividers to a RecyclerView with a LinearLayoutManager or its
 * subclass.
 */
class DividerItemDecoration : androidx.recyclerview.widget.RecyclerView.ItemDecoration {

    private var dividerDrawable: Drawable? = null
    private var orientation: Int = 0
    private val leftInset: Int
    private val rightInset: Int

    /**
     * Sole constructor. Takes in a [Drawable] to be used as the interior
     * divider.
     *
     * @param divider A divider `Drawable` to be drawn on the RecyclerView
     */
    constructor(divider: Drawable) {
        dividerDrawable = divider
        leftInset = 0
        rightInset = 0
    }

    /**
     * Sole constructor. Takes in a [Drawable] to be used as the interior
     * divider.
     *
     * @param divider    A divider `Drawable` to be drawn on the RecyclerView
     * @param leftInset  Left inset of a divider
     * @param rightInset Right inset of a divider
     */
    constructor(divider: Drawable, leftInset: Int, rightInset: Int) {
        dividerDrawable = divider
        this.leftInset = leftInset
        this.rightInset = rightInset
    }

    /**
     * Draws horizontal or vertical dividers onto the parent RecyclerView.
     *
     * @param canvas The [Canvas] onto which dividers will be drawn
     * @param parent The RecyclerView onto which dividers are being added
     * @param state  The current RecyclerView.State of the RecyclerView
     */
    override fun onDraw(
        canvas: Canvas,
        parent: androidx.recyclerview.widget.RecyclerView,
        state: androidx.recyclerview.widget.RecyclerView.State
    ) {
        if (orientation == androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL) {
            drawHorizontalDividers(canvas, parent)
        } else if (orientation == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
            drawVerticalDividers(canvas, parent)
        }
    }

    /**
     * Determines the size and location of offsets between items in the parent
     * RecyclerView.
     *
     * @param outRect The [Rect] of offsets to be added around the child
     * view
     * @param view    The child view to be decorated with an offset
     * @param parent  The RecyclerView onto which dividers are being added
     * @param state   The current RecyclerView.State of the RecyclerView
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: androidx.recyclerview.widget.RecyclerView,
        state: androidx.recyclerview.widget.RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildAdapterPosition(view) == 0) {
            return
        }
        orientation =
            (parent.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).orientation
        if (orientation == androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL) {
            outRect.left = dividerDrawable!!.intrinsicWidth
        } else if (orientation == androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
            outRect.top = dividerDrawable!!.intrinsicHeight
        }
    }

    /**
     * Adds dividers to a RecyclerView with a LinearLayoutManager or its
     * subclass oriented horizontally.
     *
     * @param canvas The [Canvas] onto which horizontal dividers will be
     * drawn
     * @param parent The RecyclerView onto which horizontal dividers are being
     * added
     */
    private fun drawHorizontalDividers(
        canvas: Canvas,
        parent: androidx.recyclerview.widget.RecyclerView
    ) {
        val parentTop = parent.paddingTop
        val parentBottom = parent.height - parent.paddingBottom

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)

            val params =
                child.layoutParams as androidx.recyclerview.widget.RecyclerView.LayoutParams

            val parentLeft = leftInset + child.right + params.rightMargin
            val parentRight = rightInset + parentLeft + dividerDrawable!!.intrinsicWidth

            dividerDrawable!!.setBounds(parentLeft, parentTop, parentRight, parentBottom)
            dividerDrawable!!.draw(canvas)
        }
    }

    /**
     * Adds dividers to a RecyclerView with a LinearLayoutManager or its
     * subclass oriented vertically.
     *
     * @param canvas The [Canvas] onto which vertical dividers will be
     * drawn
     * @param parent The RecyclerView onto which vertical dividers are being
     * added
     */
    private fun drawVerticalDividers(
        canvas: Canvas,
        parent: androidx.recyclerview.widget.RecyclerView
    ) {
        val parentLeft = leftInset + parent.paddingLeft
        val parentRight = rightInset + parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)

            val params =
                child.layoutParams as androidx.recyclerview.widget.RecyclerView.LayoutParams

            val parentTop = child.bottom + params.bottomMargin
            val parentBottom = parentTop + dividerDrawable!!.intrinsicHeight

            dividerDrawable!!.setBounds(parentLeft, parentTop, parentRight, parentBottom)
            dividerDrawable!!.draw(canvas)
        }
    }
}