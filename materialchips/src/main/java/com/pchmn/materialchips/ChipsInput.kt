package com.pchmn.materialchips
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.core.widget.NestedScrollView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.pchmn.materialchips.databinding.ChipsInputBinding
import com.pchmn.materialchips.extensions.applyAttributes
import com.pchmn.materialchips.extensions.fillInfo
import com.pchmn.materialchips.extensions.margin
import com.pchmn.materialchips.models.ChipData
import com.pchmn.materialchips.models.ChipDataInterface
import com.pchmn.materialchips.models.ChipsInputAttributes

class ChipsInput @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    // ViewBinding
    private var binding: ChipsInputBinding
    private val mChipsListeners: MutableList<ChipsListener> = mutableListOf()
    lateinit var editText: AppCompatEditText
    // Attributes
    private var mAttributes: ChipsInputAttributes = ChipsInputAttributes()
    // Bind every Chip view with a ChipInterface
    private val mMapChipWithChipData = hashMapOf<Chip, ChipDataInterface?>()

    init {
        LayoutInflater.from(context).inflate(R.layout.chips_input, this)
        binding = ChipsInputBinding.bind(this)
        applyAttributes(attrs)
        initEditText()
    }

    private fun applyAttributes(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ChipsInput,
            0, 0).apply {

            try {
                mAttributes.hint = getString(R.styleable.ChipsInput_hint)
                mAttributes.hintColor = getColorStateList(R.styleable.ChipsInput_hintColor)
                mAttributes.textColor = getColorStateList(R.styleable.ChipsInput_textColor)
                mAttributes.chipStyle = getResourceId(R.styleable.ChipsInput_chipStyle, R.style.Widget_MaterialComponents_Chip_Action)
                mAttributes.chipBackgroundColor = getColorStateList(R.styleable.ChipsInput_chipBackgroundColor)
                mAttributes.chipBackgroundColorChecked = getColorStateList(R.styleable.ChipsInput_chipBackgroundColorChecked)
                mAttributes.chipTextColor = getColorStateList(R.styleable.ChipsInput_chipTextColor)
                mAttributes.chipTextColorChecked = getColorStateList(R.styleable.ChipsInput_chipTextColorChecked)
                mAttributes.chipIconVisible = getBoolean(R.styleable.ChipsInput_chipIconVisible, false)
                mAttributes.chipIconTint = getColorStateList(R.styleable.ChipsInput_chipIconTint)
                mAttributes.chipDefaultIcon = getDrawable(R.styleable.ChipsInput_chipDefaultIcon)
                mAttributes.chipIconSize = getDimension(R.styleable.ChipsInput_chipIconSize, ChipsInputAttributes.NOT_SPECIFIED)
                mAttributes.chipLetterTileAsIcon = getBoolean(R.styleable.ChipsInput_chipLetterTileAsIcon, false)
                if (mAttributes.chipDefaultIcon == null) {
                    mAttributes.chipDefaultIcon = ContextCompat.getDrawable(context, R.drawable.ic_account_circle_white_24dp)
                }
                mAttributes.chipCloseIconVisible = getBoolean(R.styleable.ChipsInput_chipCloseIconVisible, isEntryStyle(mAttributes.chipStyle))
                mAttributes.chipCloseIconTint = getColorStateList(R.styleable.ChipsInput_chipCloseIconTint)
                mAttributes.chipCloseIconSize = getDimension(R.styleable.ChipsInput_chipCloseIconSize, ChipsInputAttributes.NOT_SPECIFIED)
                mAttributes.chipCheckable = getBoolean(R.styleable.ChipsInput_chipCheckable, isChoiceOrFilterStyle(mAttributes.chipStyle))
                mAttributes.chipCheckedIconVisible = getBoolean(R.styleable.ChipsInput_chipCheckedIconVisible, isChoiceOrFilterStyle(mAttributes.chipStyle))
                mAttributes.chipCheckedIcon = getDrawable(R.styleable.ChipsInput_chipCheckedIcon)
                mAttributes.chipStrokeColor = getColorStateList(R.styleable.ChipsInput_chipStrokeColor)
                mAttributes.chipStrokeWidth = getDimension(R.styleable.ChipsInput_chipStrokeWidth, ChipsInputAttributes.ZERO)
            } finally {
                recycle()
            }
        }
    }

    private fun isChoiceOrFilterStyle(styleRes: Int): Boolean {
        return styleRes == R.style.Widget_MaterialComponents_Chip_Choice || styleRes == R.style.Widget_MaterialComponents_Chip_Filter
    }

    private fun isEntryStyle(styleRes: Int): Boolean {
        return styleRes == R.style.Widget_MaterialComponents_Chip_Entry
    }

    private fun initEditText() {
        editText = binding.editText
        editText.applyAttributes(mAttributes)

        editText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && binding.flexboxLayout.size > 1 && editText.text.toString().isEmpty()) {
                binding.flexboxLayout.removeViewAt(binding.flexboxLayout.size - 2)
            }
            false
        }
    }

    fun addChip(chipData: ChipDataInterface) {
        val chip = attachChipView(chipData)

        chip.fillInfo(chipData, mAttributes)
    }

    fun addChip(id: Any?, iconDrawable: Drawable?, text: String, info: String?) {
        val chipData = ChipData(id = id, iconDrawable = iconDrawable, text = text, info = info)
        val chip = attachChipView(chipData)
        chip.fillInfo(chipData, mAttributes)
    }

    fun addChip(id: Any?, iconUri: Uri?, text: String, info: String?) {
        val chipData = ChipData(id = id, iconUri = iconUri, text = text, info = info)
        val chip = attachChipView(chipData)
        chip.fillInfo(chipData, mAttributes)
    }

    fun addChip(iconDrawable: Drawable?, text: String, info: String?) {
        val chipData = ChipData(iconDrawable = iconDrawable, text = text, info = info)
        val chip = attachChipView(chipData)
        chip.fillInfo(chipData, mAttributes)
    }

    fun addChip(iconUri: Uri?, text: String, info: String?) {
        val chipData = ChipData(iconUri = iconUri, text = text, info = info)
        val chip = attachChipView(chipData)
        chip.fillInfo(chipData, mAttributes)
    }

    fun addChip(text: String, info: String?) {
        val chipData = ChipData(text = text, info = info)
        val chip = attachChipView(chipData)
        chip.fillInfo(chipData, mAttributes)
    }

    fun addChip(text: String) {
        val chipData = ChipData(text = text)
        val chip = attachChipView(chipData)
        chip.fillInfo(chipData, mAttributes)
    }

    private fun attachChipView(chipData: ChipDataInterface?): Chip {
        val chip = Chip(context)
        // Add link between chip view and chip data
        mMapChipWithChipData[chip] = chipData

        // Create chip according to style choose
        val drawable = ChipDrawable.createFromAttributes(context, null, 0, mAttributes.chipStyle)
        chip.setChipDrawable(drawable)
        // Apply own attributes
        chip.applyAttributes(mAttributes)

        binding.flexboxLayout.addView(chip, binding.flexboxLayout.childCount - 1)
        // Notify listeners
        mChipsListeners.forEach { listener -> listener.onChipAdded(chip, mMapChipWithChipData[chip], binding.flexboxLayout.size - 1) }
        // Set margin after the view is added
        chip.margin(right = 10F)

        // Stock original colors to put them back when unchecked
        val originalBackgroundColor = chip.chipBackgroundColor
        val originalTextColor = chip.textColors
        chip.setOnCheckedChangeListener { _, isChecked ->
            // Notify listeners
            mChipsListeners.forEach { listener -> listener.onCheckedChanged(chip, mMapChipWithChipData[chip], binding.flexboxLayout.indexOfChild(chip), isChecked) }
            if (isChecked) {
                if (mAttributes.chipBackgroundColorChecked != null) chip.chipBackgroundColor = mAttributes.chipBackgroundColorChecked
                if (mAttributes.chipTextColorChecked != null) chip.setTextColor(mAttributes.chipTextColorChecked)
            } else {
                chip.chipBackgroundColor = originalBackgroundColor
                chip.setTextColor(originalTextColor)
            }
        }

        chip.setOnClickListener {
            // Notify listeners
            mChipsListeners.forEach { listener -> listener.onClick(chip, mMapChipWithChipData[chip], binding.flexboxLayout.indexOfChild(chip)) }
        }

        chip.setOnCloseIconClickListener {
            binding.flexboxLayout.removeView(chip)
            // Notify listeners
            mChipsListeners.forEach { listener -> listener.onChipRemoved(chip, mMapChipWithChipData[chip], binding.flexboxLayout.size - 1) }
            // Remove link between chip view and chip data
            mMapChipWithChipData.remove(chip)
        }

        return chip
    }

    fun addChipsListener(chipsListener: ChipsListener) {
        mChipsListeners.add(chipsListener)
    }

    interface ChipsListener {
        fun onCheckedChanged(view: Chip, chipData: ChipDataInterface?, position: Int, isChecked: Boolean)
        fun onClick(view: Chip, chipData: ChipDataInterface?, position: Int)
        fun onChipAdded(view: Chip, chipData: ChipDataInterface?, newSize: Int)
        fun onChipRemoved(view: Chip, chipData: ChipDataInterface?, newSize: Int)
    }
}

//inline fun ChipsInput.doOnTextChanged(
//    crossinline action: (
//        text: CharSequence?,
//        start: Int,
//        before: Int,
//        count: Int
//    ) -> Unit
//) = addChipsListener(onTextChanged = action)

//inline fun ChipsInput.addChipsListener(
//    crossinline onTextChanged: (
//        text: CharSequence?,
//        start: Int,
//        before: Int,
//        count: Int
//    ) -> Unit = { _, _, _, _ -> }): ChipsInput.ChipsListener  {
//    val chipsListener = object : ChipsInput.ChipsListener {
//        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
//            onTextChanged.invoke(text, start, before, count)
//        }
//    }
//    addChipsListener(chipsListener)
//
//    return chipsListener
//}