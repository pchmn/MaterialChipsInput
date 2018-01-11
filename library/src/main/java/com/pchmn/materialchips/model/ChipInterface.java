package com.pchmn.materialchips.model;


import android.graphics.drawable.Drawable;
import android.net.Uri;

public interface ChipInterface {

    Object getId();
    String getAvatarUri();
    Drawable getAvatarDrawable();
    String getLabel();
    String getInfo();
}
