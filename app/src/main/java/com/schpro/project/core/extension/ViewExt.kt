package com.schpro.project.core.extension

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.schpro.project.R
import com.schpro.project.core.HARUS_ISI

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.disable() {
    isEnabled = false
}

fun View.enabled() {
    isEnabled = true
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Fragment.toast(msg: String?) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
}

fun TextInputLayout.harusIsi(setError: Boolean = true) {
    error = "${editText?.hint} $HARUS_ISI"
}

fun Activity.changeStatusBarColor(@ColorInt color: Int, useLightContent: Boolean? = null) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val insetsController = window.insetsController
        val shouldUseLightContent = useLightContent ?: (ColorUtils.calculateLuminance(color) < 0.5)

        if (shouldUseLightContent) {
            insetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    } else {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
            useLightContent ?: true
    }

    window.statusBarColor = color
}

fun View.isEventWithinViewBounds(event: MotionEvent): Boolean {
    val rect = Rect()
    getGlobalVisibleRect(rect)
    return rect.contains(
        event.rawX.toInt(),
        event.rawY.toInt()
    )
}

fun Menu.setIconColor(context: Context, menuId: Int, color: Int = R.color.white) {
    val editAction = findItem(R.id.update_project)
    editAction.icon?.run {
        val icon = DrawableCompat.wrap(this)
        val colorSelector = ResourcesCompat.getColorStateList(
            context.resources,
            color,
            context.theme
        )
        DrawableCompat.setTintList(icon, colorSelector)
    }
}