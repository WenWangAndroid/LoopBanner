package cn.xhuww.loopbanner

import android.graphics.PointF
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView

class HorizontalLayoutManager : RecyclerView.LayoutManager(),
    RecyclerView.SmoothScroller.ScrollVectorProvider {
    private val orientationHelper = OrientationHelper.createHorizontalHelper(this)

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        if (childCount == 0) {
            return null
        }

        val firstChildPos = getPosition(getChildAt(0)!!)
        val direction = if (targetPosition < firstChildPos) -1 else 1
        return PointF(direction.toFloat(), 0f)
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        detachAndScrapAttachedViews(recycler)

        if (itemCount == 0) return
        var start = orientationHelper.startAfterPadding
        for (i in 0 until itemCount) {
            val child = recycler.getViewForPosition(i)
            layoutChild(child, start, forward = true)
            start = orientationHelper.getDecoratedEnd(child)
            if (start > orientationHelper.endAfterPadding) {
                break
            }
        }
    }

    private fun layoutChild(view: View, start: Int, forward: Boolean) {
        measureChildWithMargins(view, 0, 0)
        val childWidth = orientationHelper.getDecoratedMeasurement(view)
        val childHeight = orientationHelper.getDecoratedMeasurementInOther(view)

        val left: Int
        val right: Int
        val top = paddingTop
        val bottom = top + childHeight
        if (forward) {
            addView(view)
            left = start
            right = start + childWidth
        } else {
            addView(view, 0)
            left = start - childWidth
            right = start
        }
        layoutDecoratedWithMargins(view, left, top, right, bottom)
        orientationHelper.onLayoutComplete()
    }

    override fun canScrollHorizontally(): Boolean = true

    override fun scrollHorizontallyBy(
        dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State
    ): Int {
        if (childCount == 0 || dx == 0) return 0
        recycleViews(dx, recycler)
        fill(dx, recycler)
        orientationHelper.offsetChildren(-1 * dx)
        return dx
    }

    private fun fill(dx: Int, recycler: RecyclerView.Recycler) {
        while (true) {
            var start: Int
            var currentView: View
            val forward = dx > 0

            if (forward) {
                val lastVisibleView = getChildAt(childCount - 1) ?: break
                start = orientationHelper.getDecoratedEnd(lastVisibleView)
                if (start - dx > orientationHelper.endAfterPadding) break

                currentView = lastVisibleView
            } else {
                val firstVisibleView = getChildAt(0) ?: break
                start = orientationHelper.getDecoratedStart(firstVisibleView)
                if (start - dx < orientationHelper.startAfterPadding) break

                currentView = firstVisibleView
            }

            val nextView = nextView(currentView, forward, recycler) ?: break
            layoutChild(nextView, start, forward)
        }
    }

    private fun nextView(
        currentView: View, forward: Boolean, recycler: RecyclerView.Recycler
    ): View? {
        val endPosition = itemCount - 1
        val currentPosition = getPosition(currentView)
        val nextViewPosition: Int = if (forward) {
            if (currentPosition == endPosition) 0 else currentPosition + 1
        } else {
            if (currentPosition == 0) endPosition else currentPosition - 1
        }
        return recycler.getViewForPosition(nextViewPosition)
    }

    private fun recycleViews(dx: Int, recycler: RecyclerView.Recycler) {
        for (i in 0 until itemCount) {
            val childView = getChildAt(i) ?: return
            if (dx > 0) {
                if (orientationHelper.getDecoratedEnd(childView) - dx <
                    orientationHelper.startAfterPadding
                ) {
                    removeAndRecycleViewAt(i, recycler)
                }
            } else {
                if (orientationHelper.getDecoratedStart(childView) - dx >
                    orientationHelper.endAfterPadding
                ) {
                    removeAndRecycleViewAt(i, recycler)
                }
            }
        }
    }

    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
        var remeasureWidthSpec = widthSpec
        var remeasureHeightSpec = heightSpec

        val widthMode = View.MeasureSpec.getMode(widthSpec)
        val heightMode = View.MeasureSpec.getMode(heightSpec)

        if (widthMode == View.MeasureSpec.AT_MOST) {
            var measureWidth = View.MeasureSpec.getSize(widthSpec)
            val itemWidthSum = (0 until itemCount)
                .mapNotNull { recycler.getViewForPosition(it) }
                .map { measureChildView(it, widthSpec, heightSpec).first }
                .sum()

            if (itemWidthSum < measureWidth) {
                measureWidth = itemWidthSum
            }
            remeasureWidthSpec =
                View.MeasureSpec.makeMeasureSpec(measureWidth, View.MeasureSpec.EXACTLY)
        }

        if (heightMode == View.MeasureSpec.AT_MOST) {
            var measureHeight = View.MeasureSpec.getSize(heightSpec)
            val itemMaxHeight = (0 until itemCount)
                .mapNotNull { recycler.getViewForPosition(it) }
                .map { measureChildView(it, widthSpec, heightSpec).second }
                .max() ?: 0

            if (itemMaxHeight < measureHeight) {
                measureHeight = itemMaxHeight
            }
            remeasureHeightSpec =
                View.MeasureSpec.makeMeasureSpec(measureHeight, View.MeasureSpec.EXACTLY)
        }
        super.onMeasure(recycler, state, remeasureWidthSpec, remeasureHeightSpec)
    }

    private fun measureChildView(childView: View, widthSpec: Int, heightSpec: Int): Pair<Int, Int> {
        val layoutParams = childView.layoutParams as RecyclerView.LayoutParams
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec, paddingTop + paddingBottom, layoutParams.height
        )
        childView.measure(widthSpec, childHeightSpec)

        val width = childView.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
        val height = childView.measuredHeight + layoutParams.bottomMargin + layoutParams.topMargin

        return Pair(width, height)
    }
}
