package com.pchmn.materialchips.extensions

import android.content.res.TypedArray

inline fun <reified T : Enum<T>> TypedArray.getEnum(index: Int, default: T) =
    getInt(index, -1).let { if (it >= 0) enumValues<T>()[it] else default }
