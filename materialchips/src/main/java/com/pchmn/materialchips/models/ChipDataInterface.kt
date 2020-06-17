package com.pchmn.materialchips.models

import android.graphics.drawable.Drawable
import android.net.Uri

interface ChipDataInterface {

    fun getId(): Any?
    fun getIconUri(): Uri?
    fun getIconDrawable(): Drawable?
    fun getText(): String?
    fun getInfo(): String?
}