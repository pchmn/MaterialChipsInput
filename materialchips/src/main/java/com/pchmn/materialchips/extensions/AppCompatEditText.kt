package com.pchmn.materialchips.extensions

import android.text.InputType
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatEditText
import com.pchmn.materialchips.models.ChipsInputAttributes

fun AppCompatEditText.init() {
    layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    setBackgroundResource(android.R.color.transparent)
    imeOptions = EditorInfo.IME_ACTION_DONE
    privateImeOptions = "nm"
    // No suggestion
    inputType = InputType.TYPE_TEXT_VARIATION_FILTER or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
}

fun AppCompatEditText.applyAttributes(attributes: ChipsInputAttributes) {
    hint = attributes.hint
    if (attributes.hintColor != null) {
        setHintTextColor(attributes.hintColor )
    }
    if (attributes.textColor != null) {
        setTextColor(attributes.textColor)
    }
}