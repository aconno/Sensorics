package com.aconno.sensorics.adapter.viewpager2

import android.view.MotionEvent
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * @author Serhat Surguvec
 *
 * Helper that helps ViewPager content to be scrolled vertically.
 */
class ViewPager2TouchHelper {

    companion object {
        const val DRAG_TRESHOLD = 10
    }

    private var downX = 0
    private var downY = 0

    @MainThread
    fun setViewPager(viewPager2: ViewPager2) {
        val field = viewPager2::class.java.getDeclaredField("mRecyclerView")
        field.isAccessible = true
        val mRecyclerView = field.get(viewPager2) as RecyclerView
        setTouchListener(mRecyclerView)
    }

    /**
     * Disallow RecyclerView to scroll vertically
     * @param mRecyclerView RecyclerView
     */
    private fun setTouchListener(mRecyclerView: RecyclerView) {
        mRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                //No-Op
            }

            override fun onInterceptTouchEvent(
                rv: RecyclerView,
                event: MotionEvent
            ): Boolean {
                when (event.actionMasked) {

                    MotionEvent.ACTION_DOWN -> {
                        downX = event.rawX.toInt()
                        downY = event.rawY.toInt()
                        return false
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val distanceX = Math.abs(event.rawX.toInt() - downX)
                        val distanceY = Math.abs(event.rawY.toInt() - downY)

                        return if (distanceY > distanceX && distanceY > DRAG_TRESHOLD) {
                            rv.requestDisallowInterceptTouchEvent(true)
                            false
                        } else if (distanceX > distanceY && distanceX > DRAG_TRESHOLD) {
                            false
                        } else {
                            false
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        return false
                    }
                }

                return false
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                //No-Op
            }
        })
    }
}