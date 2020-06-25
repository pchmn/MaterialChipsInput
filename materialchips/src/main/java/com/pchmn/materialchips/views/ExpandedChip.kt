package com.pchmn.materialchips.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.RelativeLayout
import com.pchmn.materialchips.R
import com.pchmn.materialchips.databinding.ExpandedChipBinding


class ExpandedChip @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    // ViewBinding
    private var binding: ExpandedChipBinding

    init {
        LayoutInflater.from(context).inflate(R.layout.expanded_chip, this)
        binding = ExpandedChipBinding.bind(this)
        // Hide on first
        visibility = View.GONE
        // Hide on touch outside
        hideOnTouchOutside()
    }

    /**
     * Hide the view on touch outside of it
     */
    private fun hideOnTouchOutside() {
        // set focusable
        isFocusable = true
        isFocusableInTouchMode = true
        isClickable = true
    }

    /**
     * Fade in
     */
    fun fadeIn(coord: IntArray) {
//        val anim = AlphaAnimation(0.0f, 1.0f)
//        anim.duration = 200
//        startAnimation(anim)
        val anim: Animation = ScaleAnimation(
            0.5f, 1f,  // Start and end values for the X axis scaling
            0.5f, 1f,  // Start and end values for the Y axis scaling
            Animation.ABSOLUTE, coord[0].toFloat(),  // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 0f
        ) // Pivot point of Y scaling
        anim.fillAfter = true // Needed to keep the result of the animation
        anim.duration = 200
        startAnimation(anim)
        visibility = View.VISIBLE
        // focus on the view
        requestFocus()
    }

    /**
     * Fade out
     */
    fun fadeOut() {
        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        startAnimation(anim)
        visibility = View.GONE
        // fix onclick issue
        clearFocus()
        isClickable = false
    }
}