package com.pchmn.materialchips.model;


import android.graphics.drawable.Drawable;

public interface ChipInterface {

    Object getId();

    String getAvatarUri();

    Drawable getAvatarDrawable();

    String getLabel();

    String getInfo();
}
