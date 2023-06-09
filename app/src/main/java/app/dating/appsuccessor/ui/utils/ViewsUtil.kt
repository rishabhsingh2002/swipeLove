package app.dating.appsuccessor.ui.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import app.dating.appsuccessor.R
import app.dating.appsuccessor.databinding.CustomToastBinding

fun View.clickTo(
    pressEffect: Boolean = true,
    debounceIntervalMs: Int = 700,
    listener: (view: View?) -> Unit
) {
    var lastTapTimestamp: Long = 0
    this.setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTapTimestamp > debounceIntervalMs) {
            lastTapTimestamp = currentTime
            listener(it)
        }
    }
    if (pressEffect) {
        addPressEffect()
    }
}

fun View.addPressEffect() {
    val alphaAnimator: ValueAnimator = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0.7f).apply {
        duration = 200
    }

    fun isMotionEventInsideView(view: View, event: MotionEvent): Boolean {
        val viewRect = Rect(
            view.left,
            view.top,
            view.right,
            view.bottom
        )
        return viewRect.contains(
            view.left + event.x.toInt(),
            view.top + event.y.toInt()
        )
    }
    setOnTouchListener(View.OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                alphaAnimator.start()
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_press))
                return@OnTouchListener true
            }

            MotionEvent.ACTION_UP -> {
                alphaAnimator.reverse()
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_release))
                if (isMotionEventInsideView(v, event)) {
                    performClick()
                }
                return@OnTouchListener true
            }
        }
        false
    })
}

fun Activity.setStatusBarColor(color: Int) {
    var flags = window?.decorView?.systemUiVisibility // get current flag
    if (flags != null) {
        if (isColorDark(color)) {
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            window?.decorView?.systemUiVisibility = flags
        } else {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window?.decorView?.systemUiVisibility = flags
        }
    }
    window?.statusBarColor = color
}

fun Activity.isColorDark(color: Int): Boolean {
    val darkness =
        1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
    return darkness >= 0.5
}

fun showToast(context: Context, message: String) {
    val customView = CustomToastBinding.inflate(LayoutInflater.from(context))
    customView.message.text = message
    val toast = Toast(context.applicationContext)

    val marginBottomPx = (100 * context.resources.displayMetrics.density).toInt()
    toast.setGravity(Gravity.BOTTOM, 0, marginBottomPx)

    toast.duration = Toast.LENGTH_SHORT
    toast.view = customView.root
    toast.show()
}
