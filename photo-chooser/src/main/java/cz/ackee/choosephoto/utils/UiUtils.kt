package cz.ackee.choosephoto.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.util.TypedValue
import android.view.WindowManager

//  Utilities for UI related things like device size or conversion from dp to px

fun Context.getWindowSize(): IntArray {
    val screenWidth: Int
    val screenHeight: Int
    val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        val point = Point()
        display.getSize(point)
        screenWidth = point.x
        screenHeight = point.y
    } else {
        screenWidth = display.width
        screenHeight = display.height
    }
    return intArrayOf(screenWidth, screenHeight)
}

fun Context.getColorAttribute(attribute: Int): Int {
    val typedValue = TypedValue()
    val colorAttr = intArrayOf(attribute)
    val indexOfAttr = 0
    val a = obtainStyledAttributes(typedValue.data, colorAttr)
    val color = a.getColor(indexOfAttr, Color.WHITE)
    a.recycle()
    return color
}
