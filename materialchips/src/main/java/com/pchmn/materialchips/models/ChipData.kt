package com.pchmn.materialchips.models

import android.graphics.drawable.Drawable
import android.net.Uri
import java.util.*

class ChipData : ChipDataInterface {

    private var id: Any? = null
    private var iconUri: Uri? = null
    private var iconDrawable: Drawable? = null
    private var text: String? = null
    private var info: String? = null

    constructor(
        id: Any? = null,
        iconUri: Uri? = null,
        iconDrawable: Drawable? = null,
        text: String? = null,
        info: String? = null
    ) {
        this. id = id ?: UUID.randomUUID().toString()
        this.iconUri = iconUri
        this.iconDrawable = iconDrawable
        this.text = text
        this.info = info
    }

    override fun getId(): Any? {
        return id
    }

    override fun getIconUri(): Uri? {
        return iconUri
    }

    override fun getIconDrawable(): Drawable? {
        return iconDrawable
    }

    override fun getText(): String? {
        return text
    }

    override fun getInfo(): String? {
        return info
    }
}