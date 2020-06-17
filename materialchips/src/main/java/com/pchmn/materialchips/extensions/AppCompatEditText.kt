package com.pchmn.materialchips.extensions

import androidx.appcompat.widget.AppCompatEditText
import com.pchmn.materialchips.models.ChipsInputAttributes

fun AppCompatEditText.applyAttributes(attributes: ChipsInputAttributes) {
    hint = attributes.hint
    if (attributes.hintColor != null) {
        setHintTextColor(attributes.hintColor )
    }
    if (attributes.textColor != null) {
        setTextColor(attributes.textColor)
    }
}