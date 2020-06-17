package com.pchmn.materialchips.extensions

import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.chip.Chip
import com.pchmn.materialchips.models.ChipDataInterface
import com.pchmn.materialchips.models.ChipsInputAttributes
import com.pchmn.materialchips.utils.LetterTileProvider

fun Chip.applyAttributes(attributes: ChipsInputAttributes) {
    // Text
    if (attributes.chipTextColor != null) {
        setTextColor(attributes.chipTextColor)
    }

    // Checked icon
    isCheckable = attributes.chipCheckable
    isCheckedIconVisible = attributes.chipCheckedIconVisible
    if (attributes.chipCheckedIcon != null) {
        checkedIcon = attributes.chipCheckedIcon
    }

    // Chip icon
    isChipIconVisible = attributes.chipIconVisible
    if (isChipIconVisible && attributes.chipIconTint != null) {
        chipIconTint = attributes.chipIconTint
    }
    if (attributes.chipIconSize != ChipsInputAttributes.NOT_SPECIFIED) {
        chipIconSize = attributes.chipIconSize
    }

    // Close icon
    isCloseIconVisible = attributes.chipCloseIconVisible
    if (isCheckedIconVisible && attributes.chipCloseIconTint != null) {
        closeIconTint = attributes.chipCloseIconTint
    }
    if (attributes.chipCloseIconSize != ChipsInputAttributes.NOT_SPECIFIED) {
        chipIconSize = attributes.chipCloseIconSize
    }

    // Background
    if (attributes.chipBackgroundColor != null) {
        chipBackgroundColor = attributes.chipBackgroundColor
    }

    // Border
    if (attributes.chipStrokeWidth != 0F) {
        chipStrokeWidth = attributes.chipStrokeWidth
        if (attributes.chipStrokeColor != null) {
            chipStrokeColor = attributes.chipStrokeColor
        }
    }
}

fun Chip.fillInfo(chipData: ChipDataInterface, attributes: ChipsInputAttributes) {
    text = chipData.getText()

    // Icon
    if (attributes.chipIconVisible) {
        inflateIcon(chipData, attributes)
    }

}

fun Chip.inflateIcon(chipData: ChipDataInterface, attributes: ChipsInputAttributes) {

    when {
        // If there is a drawable we use it directly
        chipData.getIconDrawable() != null -> {
            chipIcon = chipData.getIconDrawable()
        }
        // Else if there is an uri, we load it and get a drawable with Glide
        // If it fails, we use default icon
        chipData.getIconUri() != null -> {
            Glide.with(this)
                .load(chipData.getIconUri())
                .circleCrop()
                .addListener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        chipIcon = attributes.chipDefaultIcon
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        chipIcon = resource
                        return false
                    }
                })
        }
        // Else we get a tile from chip text or default icon
        else -> {
            chipIcon = if (attributes.chipLetterTileAsIcon) {
                LetterTileProvider.getInstance(context).getCircularLetterTile(chipData.getText()) ?: attributes.chipDefaultIcon
            } else {
                attributes.chipDefaultIcon
            }
        }
    }
}