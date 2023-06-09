package app.dating.appsuccessor.ui.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class NonSlidingViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        // Disable touch event interception
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Disable touch events
        return false
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        // Disable horizontal scrolling
        return false
    }
}