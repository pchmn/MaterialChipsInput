package com.pchmn.materialchips.utils

import android.content.res.Resources

object ViewUtils {
    fun dpToPx(dp: Int): Float {
        return (dp * Resources.getSystem().displayMetrics.density).toFloat()
    }

    fun pxToDp(px: Int): Float {
        return (px / Resources.getSystem().displayMetrics.density).toFloat()
    }
}