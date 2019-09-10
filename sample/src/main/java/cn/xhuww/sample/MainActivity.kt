package cn.xhuww.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import cn.xhuww.loopbanner.HorizontalLayoutManager
import cn.xhuww.loopbanner.ViewPagerSnapHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageAdapter = ImageAdapter().apply {
            items = arrayListOf(
                R.mipmap.image_page_1,
                R.mipmap.image_page_2,
                R.mipmap.image_page_3,
                R.mipmap.image_page_4
            )
        }

        recyclerView.adapter = imageAdapter
        recyclerView.layoutManager = HorizontalLayoutManager()
        val snapHelper = ViewPagerSnapHelper().apply {
            attachToRecyclerView(recyclerView)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    //The RecyclerView is not currently scrolling.
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        val view = snapHelper.findSnapView(recyclerView.layoutManager) ?: return
                        val position = recyclerView.getChildAdapterPosition(view)
                        Log.i("TAG", "-----------position$position")
                    }
                    //The RecyclerView is currently being dragged
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                    }
                    //The RecyclerView is currently animating
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                    }
                }
            }
        })
    }
}
