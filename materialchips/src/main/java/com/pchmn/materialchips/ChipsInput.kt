package com.pchmn.materialchips
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.pchmn.materialchips.adapters.ChipsInputAdapter
import com.pchmn.materialchips.databinding.ChipsInputBinding
import com.pchmn.materialchips.extensions.applyAttributes
import com.pchmn.materialchips.extensions.fillInfo
import com.pchmn.materialchips.extensions.getEnum
import com.pchmn.materialchips.extensions.margin
import com.pchmn.materialchips.models.ChipData
import com.pchmn.materialchips.models.ChipDataInterface
import com.pchmn.materialchips.models.ChipsInputAttributes
import com.pchmn.materialchips.utils.SafeFlexboxLayoutManager
import com.pchmn.materialchips.utils.ViewUtils
import java.util.ArrayList

class ChipsInput @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    // ViewBinding
    private var binding: ChipsInputBinding
    // Listeners
    private val mChipsListeners: ArrayList<ChipsListener> = arrayListOf()
    lateinit var editText: AppCompatEditText
    // Attributes
    private var mAttributes: ChipsInputAttributes = ChipsInputAttributes()
    // Bind every Chip view with a ChipInterface
    private val mMapChipWithChipData = hashMapOf<Chip, ChipDataInterface?>()
    // RecyclerView
    var chipList: ArrayList<ChipDataInterface> = arrayListOf()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mChipsInputAdapter: ChipsInputAdapter
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var mFlexboxLayoutManager: FlexboxLayoutManager

    init {
        LayoutInflater.from(context).inflate(R.layout.chips_input, this)
        binding = ChipsInputBinding.bind(this)
        getAttributes(attrs)
        initRecyclerView()
        initEditText()
    }

    private fun getAttributes(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ChipsInput,
            0, 0).apply {

            try {
                mAttributes.placement = getEnum(R.styleable.ChipsInput_placement, ChipsInputAttributes.Placement.VERTICAL)
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

    private fun initRecyclerView() {
        mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mFlexboxLayoutManager = SafeFlexboxLayoutManager(context)
        mFlexboxLayoutManager.flexDirection = FlexDirection.ROW
        mChipsInputAdapter = ChipsInputAdapter(context, chipList, mAttributes)

        mChipsInputAdapter.chipsInputAdapterListener = object: ChipsInputAdapter.ChipsInputAdapterListener {
            override fun chipOnRemoveClick(chip: Chip, chipData: ChipDataInterface, position: Int) {
                chipList.remove(chipData)
                mChipsInputAdapter.notifyItemRemoved(position)
                mChipsListeners.forEach { listener -> listener.onChipRemoved(chip, chipData, position) }
            }

            override fun chipOnClick(chip: Chip, chipData: ChipDataInterface, position: Int) {
                mChipsListeners.forEach { listener -> listener.onClick(chip, chipData, position) }
            }

            override fun chipOnCheckedChange(chip: Chip, chipData: ChipDataInterface, position: Int, isChecked: Boolean) {
                // Notify listeners
                mChipsListeners.forEach { listener -> listener.onCheckedChanged(chip, chipData, position, isChecked) }
            }
        }

        mRecyclerView = binding.recyclerView.apply {
            layoutManager = if (mAttributes.placement == ChipsInputAttributes.Placement.VERTICAL) mFlexboxLayoutManager else mLayoutManager
            adapter = mChipsInputAdapter
            addItemDecoration(ChipsInputAdapter.MarginItemDecoration(ViewUtils.dpToPx(10).toInt()))
        }
    }

    private fun initEditText() {
        editText = mChipsInputAdapter.editText

        editText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && chipList.size > 0 && editText.text.toString().isEmpty()) {
                // Remove last chip
                removeChip(chipList.size - 1)
            }
            false
        }
    }

    fun addChip(chipData: ChipDataInterface) {
        chipList.add(chipData)
        mChipsInputAdapter.notifyItemInserted(chipList.size - 1)
    }

    fun addChip(id: Any?, iconDrawable: Drawable?, text: String, info: String?) {
        val chipData = ChipData(id = id, iconDrawable = iconDrawable, text = text, info = info)
        addChip(chipData)
    }

    fun addChip(id: Any?, iconUri: Uri?, text: String, info: String?) {
        val chipData = ChipData(id = id, iconUri = iconUri, text = text, info = info)
        addChip(chipData)
    }

    fun addChip(iconDrawable: Drawable?, text: String, info: String?) {
        val chipData = ChipData(iconDrawable = iconDrawable, text = text, info = info)
        addChip(chipData)
    }

    fun addChip(iconUri: Uri?, text: String, info: String?) {
        val chipData = ChipData(iconUri = iconUri, text = text, info = info)
        addChip(chipData)
    }

    fun addChip(text: String, info: String?) {
        val chipData = ChipData(text = text, info = info)
        addChip(chipData)
    }

    fun addChip(text: String) {
        val chipData = ChipData(text = text)
        addChip(chipData)
    }

    fun removeChip(position: Int) {
        chipList.removeAt(position)
        mChipsInputAdapter.notifyItemRemoved(position)
    }

    fun removeChip(chipData: ChipDataInterface) {
        val position = chipList.indexOf(chipData)
        removeChip(position)
    }

    fun addChipsListener(chipsListener: ChipsListener) {
        mChipsListeners.add(chipsListener)
    }

    interface ChipsListener {
        fun onCheckedChanged(view: Chip?, chipData: ChipDataInterface?, position: Int, isChecked: Boolean)
        fun onClick(view: Chip?, chipData: ChipDataInterface?, position: Int)
        fun onChipAdded(view: Chip?, chipData: ChipDataInterface?, newSize: Int)
        fun onChipRemoved(view: Chip?, chipData: ChipDataInterface?, newSize: Int)
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