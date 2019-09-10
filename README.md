# LoopBanner

LoopBanner是通过自定义RecyclerView.LayoutManager以及PagerSnapHelper配合RecyclerView实现的可横向无限循环滑动的banner

## 添加依赖
1. project 的 build.gradle
```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
2. module 的 build.gradle
```gradle
dependencies {
    implementation 'com.github.WenWangAndroid:LoopBanner:1.0'
}
```

## 基本使用
```kotlin
recyclerView.adapter = imageAdapter
recyclerView.layoutManager = HorizontalLayoutManager()
val snapHelper = ViewPagerSnapHelper().apply {
    attachToRecyclerView(recyclerView)
}
```
## RecyclerView滑动监听
```kotlin
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
```
