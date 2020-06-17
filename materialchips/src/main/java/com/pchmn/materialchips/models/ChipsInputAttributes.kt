package com.pchmn.materialchips.models

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import com.pchmn.materialchips.R

class ChipsInputAttributes {

    companion object {
        const val ZERO = 0f
        const val NOT_SPECIFIED = -1f
    }

    var hint: String? = null
    var hintColor: ColorStateList? = null
    var textColor: ColorStateList? = null
    var chipStyle: Int = R.style.Widget_MaterialComponents_Chip_Action
    var chipBackgroundColor: ColorStateList? = null
    var chipBackgroundColorChecked: ColorStateList? = null
    var chipTextColor: ColorStateList? = null
    var chipTextColorChecked: ColorStateList? = null
    var chipIconVisible = false
    var chipIconTint: ColorStateList? = null
    var chipDefaultIcon: Drawable? = null
    var chipIconSize: Float = NOT_SPECIFIED
    var chipLetterTileAsIcon = false
    var chipCloseIconVisible = false
    var chipCloseIconTint: ColorStateList? = null
    var chipCloseIconSize: Float = NOT_SPECIFIED
    var chipCheckable = false
    var chipCheckedIconVisible = false
    var chipCheckedIcon: Drawable? = null
    var chipStrokeColor: ColorStateList? = null
    var chipStrokeWidth: Float = ZERO
}