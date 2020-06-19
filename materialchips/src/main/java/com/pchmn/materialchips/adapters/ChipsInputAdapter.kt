package com.pchmn.materialchips.adapters

import android.content.Context
import android.graphics.Rect
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.pchmn.materialchips.extensions.applyAttributes
import com.pchmn.materialchips.extensions.fillInfo
import com.pchmn.materialchips.extensions.init
import com.pchmn.materialchips.models.ChipDataInterface
import com.pchmn.materialchips.models.ChipsInputAttributes
import java.util.ArrayList

class ChipsInputAdapter(private val mContext: Context, private val mChipList: ArrayList<ChipDataInterface>, private val mAttributes: ChipsInputAttributes) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var chipsInputAdapterListener: ChipsInputAdapterListener? = null
    private val mChipsMap = hashMapOf<Int, Chip>()
    lateinit var editText: AppCompatEditText

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
            holder.editText?.hint = "Enter text"
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