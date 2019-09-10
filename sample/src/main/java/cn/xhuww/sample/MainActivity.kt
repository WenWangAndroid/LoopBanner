package cn.xhuww.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        recyclerView.layoutManager = HorizontalLayoutManager()
        recyclerView.adapter = imageAdapter
        ViewPagerSnapHelper().attachToRecyclerView(recyclerView)
    }
}
