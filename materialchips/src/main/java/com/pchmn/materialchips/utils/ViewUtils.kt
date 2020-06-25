package com.pchmn.materialchips.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources

object ViewUtils {

    private var windowWidthPortrait = 0
    private var windowWidthLandscape = 0

    fun dpToPx(dp: Int): Float {
        return (dp * Resources.getSystem().displayMetrics.density).toFloat()
    }

    fun pxToDp(px: Int): Float {
        return (px / Resources.getSystem().displayMetrics.density).toFloat()
    }

    fun getWindowWidth(context: Context): Int {
        return if (context.resources
                .configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        ) {
            getWindowWidthPortrait(context)
        } else {
            getWindowWidthLandscape(context)
        }
    }

    private fun getWindowWidthPortrait(context: Context): Int {
        if (windowWidthPortrait == 0) {
            val metrics = context.resources.displayMetrics
            windowWidthPortrait = metrics.widthPixels
        }
        return windowWidthPortrait
    }

    private fun getWindowWidthLandscape(context: Context): Int {
        if (windowWidthLandscape == 0) {
            val metrics = context.resources.displayMetrics
            windowWidthLandscape = metrics.widthPixels
        }
        return windowWidthLandscape
    }
}