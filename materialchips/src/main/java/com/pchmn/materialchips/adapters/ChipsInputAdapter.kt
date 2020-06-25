package com.pchmn.materialchips.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.*
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.pchmn.materialchips.R
import com.pchmn.materialchips.extensions.applyAttributes
import com.pchmn.materialchips.extensions.fillInfo
import com.pchmn.materialchips.extensions.init
import com.pchmn.materialchips.models.ChipDataInterface
import com.pchmn.materialchips.models.ChipsInputAttributes
import com.pchmn.materialchips.utils.ViewUtils
import com.pchmn.materialchips.views.ExpandedChip
import com.skydoves.transformationlayout.TransformationLayout
import java.util.*


class ChipsInputAdapter(private val mContext: Context, private val mChipList: ArrayList<ChipDataInterface>, private val mAttributes: ChipsInputAttributes) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var chipsInputAdapterListener: ChipsInputAdapterListener? = null
    var editText: AppCompatEditText
    private var overlayView: View? = null

    init {
        editText = createEditText()
    }

    companion object {
        private const val EDIT_TEXT_TYPE = 0
        private const val CHIP_TYPE = 1
    }

    class ChipViewHolder(val chip: Chip) : RecyclerView.ViewHolder(chip)

    class EditTextViewHolder(val editText: AppCompatEditText) : RecyclerView.ViewHolder(editText)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecyclerView.ViewHolder {

        if (viewType == EDIT_TEXT_TYPE) {
            return EditTextViewHolder(editText)
        }
        val chip = Chip(parent.context)
        chip
        // Create chip according to style choose
        val drawable = ChipDrawable.createFromAttributes(mContext, null, 0, mAttributes.chipStyle)
        chip.setChipDrawable(drawable)
        // Apply attributes
        chip.applyAttributes(mAttributes)
        return ChipViewHolder(chip)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChipViewHolder) {
            binChipView(holder.chip, position)
        } else if (holder is EditTextViewHolder) {
            holder.editText.hint = "Enter text"
        }
    }

    private fun binChipView(chip: Chip, position: Int) {
        val chipData = mChipList[position]
        // Fill info
        chip.fillInfo(chipData, mAttributes)

        // Stock original colors to put them back when unchecked
        val originalBackgroundColor = chip.chipBackgroundColor
        val originalTextColor = chip.textColors
        chip.setOnCheckedChangeListener { _, isChecked ->
            // Notify listener
            chipsInputAdapterListener?.chipOnCheckedChange(chip, chipData, position, isChecked)
            if (isChecked) {
                if (mAttributes.chipBackgroundColorChecked != null) chip.chipBackgroundColor = mAttributes.chipBackgroundColorChecked
                if (mAttributes.chipTextColorChecked != null) chip.setTextColor(mAttributes.chipTextColorChecked)
            } else {
                chip.chipBackgroundColor = originalBackgroundColor
                chip.setTextColor(originalTextColor)
            }
        }

        chip.setOnClickListener {
            // Get chip position
            val coord = IntArray(2)
            it.getLocationInWindow(coord)

            val expandedChip = ExpandedChip(mContext)
            expandedChip.id = position
            setExpandedChipViewPosition(expandedChip, coord, chip.rootView as ViewGroup, chip)

            // Notify listener
            chipsInputAdapterListener?.chipOnClick(chip, chipData, position)
        }

        chip.setOnCloseIconClickListener {
            // Notify listener
            chipsInputAdapterListener?.chipOnRemoveClick(chip, chipData, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == mChipList.size) {
            return EDIT_TEXT_TYPE
        }
        return CHIP_TYPE
    }

    /**
     * We return chip list size + 1 because the edit text is in recyclerview but is not part of the chip list
     */
    override fun getItemCount() = mChipList.size + 1

    private fun createEditText(): AppCompatEditText {
        val editText = AppCompatEditText(mContext)
        editText.init()
        editText.applyAttributes(mAttributes)
        return editText
    }

    private fun setExpandedChipViewPosition(expandedChip: ExpandedChip, coord: IntArray, rootViewGroup: ViewGroup, chip: Chip) {
        // window width
        val windowWidth: Int = ViewUtils.getWindowWidth(mContext)

        // chip size
        val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            ViewUtils.dpToPx(325).toInt(),
            ViewUtils.dpToPx(100).toInt()
        )
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START)

        // align left window
        if (coord[0] <= 0) {
            layoutParams.leftMargin = 0
            layoutParams.topMargin = coord[1] - ViewUtils.dpToPx(5).toInt()
        } else if (coord[0] + ViewUtils.dpToPx(325) > windowWidth + ViewUtils.dpToPx(13)) {
            layoutParams.leftMargin = windowWidth - ViewUtils.dpToPx(325).toInt()
            layoutParams.topMargin = coord[1] - ViewUtils.dpToPx(5).toInt()
        } else {
            layoutParams.leftMargin = coord[0] - ViewUtils.dpToPx(13).toInt()
            layoutParams.topMargin = coord[1] - ViewUtils.dpToPx(5).toInt()
        }

        rootViewGroup.id = R.id.layout2
        rootViewGroup.addView(expandedChip, layoutParams)
        expandedChip.fadeIn(coord)
    }

    interface ChipsInputAdapterListener {
        fun chipOnRemoveClick(chip: Chip, chipData: ChipDataInterface, position: Int)
        fun chipOnClick(chip: Chip, chipData: ChipDataInterface, position: Int)
        fun chipOnCheckedChange(chip: Chip, chipData: ChipDataInterface, position: Int, isChecked: Boolean)
    }

    class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View,
                                    parent: RecyclerView, state: RecyclerView.State) {
            with(outRect) {
                if (view is Chip) {
                    right = spaceHeight
                }
            }
        }
    }
}