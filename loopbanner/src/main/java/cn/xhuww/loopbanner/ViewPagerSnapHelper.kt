package cn.xhuww.loopbanner

import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class ViewPagerSnapHelper : PagerSnapHelper() {

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        val position = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
        return if (position >= layoutManager.itemCount) 0 else position
    }
}