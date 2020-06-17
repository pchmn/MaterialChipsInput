package com.pchmn.materialchips.utils

import android.R.attr.src
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextPaint
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.pchmn.materialchips.R


/**
 * Used to create a [Bitmap] that contains a letter used in the English
 * alphabet or digit, if there is no letter or digit available, a default image
 * is shown instead
 */
class LetterTileProvider(context: Context) {
    /** The [TextPaint] used to draw the letter onto the tile  */
    private val mPaint = TextPaint()

    /** The bounds that enclose the letter  */
    private val mBounds = Rect()

    /** The [Canvas] to draw on  */
    private val mCanvas = Canvas()

    /** The first char of the name being displayed  */
    private val mFirstChar = CharArray(1)

    /** The background chip_text_color of the tile  */
    private val mColors: TypedArray

    /** The font size used to display the letter  */
    private val mTileLetterFontSize: Int

    /** Width  */
    private val mWidth: Int

    /** Height  */
    private val mHeight: Int

    private val mResources: Resources = context.resources

    /**
     * Constructor for `LetterTileProvider`
     *
     * @param context The [Context] to use
     */
    init {
        mPaint.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        mPaint.color = Color.WHITE
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.isAntiAlias = true
        mColors = mResources.obtainTypedArray(R.array.letter_tile_colors)
        mTileLetterFontSize = mResources.getDimensionPixelSize(R.dimen.tile_letter_font_size)

        mWidth = mResources.getDimensionPixelSize(R.dimen.letter_tile_size)
        mHeight = mResources.getDimensionPixelSize(R.dimen.letter_tile_size)
    }

    /**
     * @param displayName The name used to create the letter for the tile
     * @return A [Bitmap] that contains a letter used in the English
     * alphabet or digit, if there is no letter or digit available, a
     * default image is shown instead
     */
    fun getLetterTile(displayName: String?): BitmapDrawable? {
        // workaround
        if (displayName == null || displayName.isEmpty()) return null
        val bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        val firstChar = displayName[0]
        val c = mCanvas
        c.setBitmap(bitmap)
        c.drawColor(pickColor(displayName))
        if (isLetterOrDigit(firstChar)) {
            mFirstChar[0] = Character.toUpperCase(firstChar)
            mPaint.textSize = mTileLetterFontSize.toFloat()
            mPaint.getTextBounds(mFirstChar, 0, 1, mBounds)
            c.drawText(
                mFirstChar, 0, 1, mWidth / 2.toFloat(), (mHeight / 2
                        + (mBounds.bottom - mBounds.top) / 2).toFloat(), mPaint
            )
        } else {
            return null
        }
        return BitmapDrawable(mResources, bitmap)
    }

    /**
     * @param displayName The name used to create the letter for the tile
     * @return A circular [Bitmap] that contains a letter used in the English
     * alphabet or digit, if there is no letter or digit available, a
     * default image is shown instead
     */
    fun getCircularLetterTile(displayName: String?): RoundedBitmapDrawable? {
        if (displayName == null || displayName.isEmpty()) return null
        val bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        val firstChar = displayName[0]
        val c = mCanvas
        c.setBitmap(bitmap)
        c.drawColor(pickColor(displayName))

        if (isLetterOrDigit(firstChar)) {
            mFirstChar[0] = Character.toUpperCase(firstChar)
            mPaint.textSize = mTileLetterFontSize.toFloat()
            mPaint.getTextBounds(mFirstChar, 0, 1, mBounds)
            c.drawText(
                mFirstChar, 0, 1, mWidth / 2.toFloat(), (mHeight / 2
                        + (mBounds.bottom - mBounds.top) / 2).toFloat(), mPaint
            )
        } else {
            return null
        }
        val dr: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mResources, bitmap)
        dr.isCircular = true
        return dr
    }

    private fun getRoundedBitmap(bitmap: Bitmap): Bitmap? {
        val resultBitmap: Bitmap
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val r: Float
        if (originalWidth > originalHeight) {
            resultBitmap = Bitmap.createBitmap(
                originalHeight, originalHeight,
                Bitmap.Config.ARGB_8888
            )
            r = originalHeight / 2.toFloat()
        } else {
            resultBitmap = Bitmap.createBitmap(
                originalWidth, originalWidth,
                Bitmap.Config.ARGB_8888
            )
            r = originalWidth / 2.toFloat()
        }
        val canvas = Canvas(resultBitmap)
        val paint = Paint()
        val rect = Rect(
            0,
            0, originalWidth, originalHeight
        )
        paint.isAntiAlias = true
        canvas.drawARGB(
            0, 0,
            0, 0
        )
        canvas.drawCircle(r, r, r, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return resultBitmap
    }

    /**
     * @param key The key used to generate the tile color
     * @return A new or previously chosen color for `key` used as the
     * tile background color
     */
    private fun pickColor(key: String): Int {
        // String.hashCode() is not supposed to change across java versions, so
        // this should guarantee the same key always maps to the same color
        val color =
            Math.abs(key.hashCode()) % NUM_OF_TILE_COLORS
        return try {
            mColors.getColor(color, Color.BLACK)
        } finally {
            // bug with recycler view
            //mColors.recycle();
        }
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val output: Bitmap = if (bitmap.width > bitmap.height) {
            Bitmap.createBitmap(bitmap.height, bitmap.height, Bitmap.Config.ARGB_8888)
        } else {
            Bitmap.createBitmap(bitmap.width, bitmap.width, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(output)
        val color = pickColor("test")
        val paint = Paint()
        val rect =
            Rect(0, 0, bitmap.width, bitmap.height)
        var r = 0f
        r = if (bitmap.width > bitmap.height) {
            bitmap.height / 2.toFloat()
        } else {
            bitmap.width / 2.toFloat()
        }
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawCircle(r, r, r, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    companion object {
        /** The number of available tile chip_text_color (see R.array.letter_tile_colors)  */
        private const val NUM_OF_TILE_COLORS = 8

        private var mInstance: LetterTileProvider? = null

        /**
         * @param c The char to check
         * @return True if `c` is in the English alphabet or is a digit,
         * false otherwise
         */
        private fun isLetterOrDigit(c: Char): Boolean {
            //return 'A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || '0' <= c && c <= '9';
            return Character.isLetterOrDigit(c)
        }

        fun drawableToBitmap(drawable: Drawable?): Bitmap? {
            if (drawable == null) {
                return null
            }
            var bitmap: Bitmap? = null
            if (drawable is BitmapDrawable) {
                if (drawable.bitmap != null) {
                    return drawable.bitmap
                }
            }
            bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                Bitmap.createBitmap(
                    1,
                    1,
                    Bitmap.Config.ARGB_8888
                ) // Single color bitmap will be created of 1x1 pixel
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        fun getInstance(context: Context): LetterTileProvider {
            if (mInstance == null) {
                mInstance = LetterTileProvider(context)
            }
            return mInstance as LetterTileProvider
        }
    }

}
