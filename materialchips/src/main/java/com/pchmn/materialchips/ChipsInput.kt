package com.pchmn.materialchips
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.chip.Chip
import com.pchmn.materialchips.adapters.ChipsInputAdapter
import com.pchmn.materialchips.databinding.ChipsInputBinding
import com.pchmn.materialchips.extensions.getEnum
import com.pchmn.materialchips.models.ChipData
import com.pchmn.materialchips.models.ChipDataInterface
import com.pchmn.materialchips.models.ChipsInputAttributes
import com.pchmn.materialchips.utils.SafeFlexboxLayoutManager
import com.pchmn.materialchips.utils.ViewUtils
import com.pchmn.materialchips.utils.WindowCallback
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import java.util.*

class ChipsInput @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

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
        loadAttributes(attrs)
        initRecyclerView()
        initEditText()
        configureWindowCallback()
    }

    /**
     * Load attributes passes by xml
     *
     * @param attrs attributes in the xml
     */
    private fun loadAttributes(attrs: AttributeSet?) {
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

    /**
     * Check if style is Chip.Filter or Chip.Choice
     *
     * @param styleRes the style to check
     * @return true if it is, false either
     */
    private fun isChoiceOrFilterStyle(styleRes: Int): Boolean {
        return styleRes == R.style.Widget_MaterialComponents_Chip_Choice || styleRes == R.style.Widget_MaterialComponents_Chip_Filter
    }

    /**
     * Check if style is Chip.Entry
     *
     * @param styleRes the style to check
     * @return true if it is, false either
     */
    private fun isEntryStyle(styleRes: Int): Boolean {
        return styleRes == R.style.Widget_MaterialComponents_Chip_Entry
    }

    /**
     * Init RecyclerView
     */
    private fun initRecyclerView() {
        mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mFlexboxLayoutManager = SafeFlexboxLayoutManager(context)
        mFlexboxLayoutManager.flexDirection = FlexDirection.ROW
        mChipsInputAdapter = ChipsInputAdapter(context, chipList, mAttributes)

        // Configure RecyclerView
        mRecyclerView = binding.recyclerView.apply {
            // If vertical use FlexboxLayoutManager, if horizontal use LayoutManager
            layoutManager = if (mAttributes.placement == ChipsInputAttributes.Placement.VERTICAL) mFlexboxLayoutManager else mLayoutManager
            adapter = mChipsInputAdapter
            // 10dp margin between items
            addItemDecoration(ChipsInputAdapter.MarginItemDecoration(ViewUtils.dpToPx(10).toInt()))
            // Scale Animation
            itemAnimator = ScaleInAnimator().apply {
                addDuration = 25
                removeDuration = 50
            }
        }

        // Set ChipsInputAdapterListener
        mChipsInputAdapter.chipsInputAdapterListener = object: ChipsInputAdapter.ChipsInputAdapterListener {
            override fun chipOnRemoveClick(chip: Chip, chipData: ChipDataInterface, position: Int) {
                // Remove chip
                removeChip(position)
            }

            override fun chipOnClick(chip: Chip, chipData: ChipDataInterface, position: Int) {
                // Notify listeners
                mChipsListeners.forEach { listener -> listener.onChipClick(chip, chipData, position) }
            }

            override fun chipOnCheckedChange(chip: Chip, chipData: ChipDataInterface, position: Int, isChecked: Boolean) {
                // Notify listeners
                mChipsListeners.forEach { listener -> listener.onChipCheckedChanged(chip, chipData, position, isChecked) }
            }
        }
    }

    fun configureWindowCallback() {
        val activity = getActivityFromContext(context)
        val callback = activity.window.callback
        activity.window.callback = WindowCallback(callback, activity)
    }

    private fun getActivityFromContext(context: Context): Activity {
        return when (context) {
            is Activity -> context
            is ContextWrapper -> getActivityFromContext(
                context.baseContext
            )
            else -> throw ClassCastException("android.view.Context cannot be cast to android.app.Activity")
        }

    }

    /**
     * Init EditText
     */
    private fun initEditText() {
        // Get EditText from adapter
        editText = mChipsInputAdapter.editText

        // When EditText is empty and user presses backspace, remove last chip
        editText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && chipList.size > 0 && editText.text.toString().isEmpty()) {
                removeChip(chipList.size - 1)
            }
            false
        }
    }

    /**
     * Add a chip (ChipDataInterface) to the chip list (ArrayList<ChipDataInterface>)
     * The chip will be represented by a Chip view in the RecyclerView
     *
     * @param chipData the chip to add
     */
    fun addChip(chipData: ChipDataInterface) {
        chipList.add(chipData)
        mChipsInputAdapter.notifyItemInserted(chipList.size - 1)
        // Notify listeners
        mChipsListeners.forEach { listener -> listener.onChipAdded(chipData, chipList.size - 1, chipList.size) }
    }

    /**
     * Add a chip to the chip list (ArrayList<ChipDataInterface>)
     * A ChipData object will be created, based on entry params, and added to the list
     *
     * @param id the id of ChipData object
     * @param iconDrawable the Drawable icon of the ChipData object
     * @param text the text of the ChipData object
     * @param info the info of the ChipData object
     */
    fun addChip(id: Any?, iconDrawable: Drawable?, text: String, info: String?) {
        val chipData = ChipData(id = id, iconDrawable = iconDrawable, text = text, info = info)
        addChip(chipData)
    }

    /**
     * Add a chip to the chip list (ArrayList<ChipDataInterface>)
     * A ChipData object will be created, based on entry params, and added to the list
     *
     * @param id the id of ChipData object
     * @param iconUri the URI icon of the ChipData object
     * @param text the text of the ChipData object
     * @param info the info of the ChipData object
     */
    fun addChip(id: Any?, iconUri: Uri?, text: String, info: String?) {
        val chipData = ChipData(id = id, iconUri = iconUri, text = text, info = info)
        addChip(chipData)
    }

    /**
     * Add a chip to the chip list (ArrayList<ChipDataInterface>)
     * A ChipData object will be created, based on entry params, and added to the list
     *
     * @param iconDrawable the Drawable icon of the ChipData object
     * @param text the text of the ChipData object
     * @param info the info of the ChipData object
     */
    fun addChip(iconDrawable: Drawable?, text: String, info: String?) {
        val chipData = ChipData(iconDrawable = iconDrawable, text = text, info = info)
        addChip(chipData)
    }

    /**
     * Add a chip to the chip list (ArrayList<ChipDataInterface>)
     * A ChipData object will be created, based on entry params, and added to the list
     *
     * @param iconUri the URI icon of the ChipData object
     * @param text the text of the ChipData object
     * @param info the info of the ChipData object
     */
    fun addChip(iconUri: Uri?, text: String, info: String?) {
        val chipData = ChipData(iconUri = iconUri, text = text, info = info)
        addChip(chipData)
    }

    /**
     * Add a chip to the chip list (ArrayList<ChipDataInterface>)
     * A ChipData object will be created, based on entry params, and added to the list
     *
     * @param text the text of the ChipData object
     * @param info the info of the ChipData object
     */
    fun addChip(text: String, info: String?) {
        val chipData = ChipData(text = text, info = info)
        addChip(chipData)
    }

    /**
     * Add a chip to the chip list (ArrayList<ChipDataInterface>)
     * A ChipData object will be created, based on text, and added to the list
     *
     * @param text the text of the ChipData object
     */
    fun addChip(text: String) {
        val chipData = ChipData(text = text)
        addChip(chipData)
    }

    /**
     * Remove the chip at the specified position from the chip list (ArrayList<ChipDataInterface>)
     *
     * @param position the position of the chip to be removed
     */
    fun removeChip(position: Int) {
        val chipData = chipList[position]
        chipList.removeAt(position)
        mChipsInputAdapter.notifyItemRemoved(position)
        // Notify listeners
        mChipsListeners.forEach { listener -> listener.onChipRemoved(chipData, position, chipList.size) }
    }

    /**
     * Remove the chip (ChipDataInterface) from the chip list (ArrayList<ChipDataInterface>)
     *
     * @param chipData the chip to be removed
     */
    fun removeChip(chipData: ChipDataInterface) {
        val position = chipList.indexOf(chipData)
        removeChip(position)
    }

    /**
     * Add a listener (ChipsListener)
     *
     * @param chipsListener the listener to add
     */
    fun addChipsListener(chipsListener: ChipsListener) {
        mChipsListeners.add(chipsListener)
    }

    interface ChipsListener {
        /**
         * Called when a chip view is checked or unchecked
         *
         * @param view the chip view
         * @param chipData the chip data representing the chip view
         * @param position the position of the chip in the chip list
         * @param isChecked whether the chip view is checked or unchecked
         */
        fun onChipCheckedChanged(view: Chip?, chipData: ChipDataInterface?, position: Int, isChecked: Boolean)

        /**
         * Called when a chip view is clicked (also called when a chip view is checked/unchecked)
         *
         * @param view the chip view
         * @param chipData the chip data representing the chip view
         * @param position the position of the chip in the chip list
         */
        fun onChipClick(view: Chip?, chipData: ChipDataInterface?, position: Int)

        /**
         * Called when a chip (ChipDataInterface) is added to the chip list (ArrayList<ChipDataInterface>)
         *
         * @param chipData the chip added
         * @param position the position where the chip was added
         * @param newSize the new size of the chip list
         */
        fun onChipAdded(chipData: ChipDataInterface?, position: Int, newSize: Int)

        /**
         * Called when a chip (ChipDataInterface) is removed from the chip list (ArrayList<ChipDataInterface>)
         *
         * @param chipData the chip removed
         * @param position the position where the chip was removed
         * @param newSize the new size of the chip list
         */
        fun onChipRemoved(chipData: ChipDataInterface?, position: Int, newSize: Int)
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