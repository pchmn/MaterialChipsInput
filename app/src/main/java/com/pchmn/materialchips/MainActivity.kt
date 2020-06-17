package com.pchmn.materialchips

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.pchmn.materialchips.models.ChipDataInterface

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(topAppBar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

//        chipsInput.editText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
//            Log.d("lol", "test log")
//            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
//                //Perform Code
//                return@OnKeyListener true
//            }
//            false
//        })

        chipsInput.editText.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                chipsInput.addChip(chipsInput.editText.text.toString())
                true
            } else {
                false
            }
        }

        chipsInput.addChipsListener(object: ChipsInput.ChipsListener {
            override fun onCheckedChanged(view: Chip, chipData: ChipDataInterface?, position: Int, isChecked: Boolean) {
                Toast.makeText(this@MainActivity, "Chip at position $position checked:$isChecked", Toast.LENGTH_SHORT).show()
            }

            override fun onClick(view: Chip, chipData: ChipDataInterface?, position: Int) {
                Toast.makeText(this@MainActivity, "Chip at position $position is clicked", Toast.LENGTH_SHORT).show()
            }

            override fun onChipAdded(view: Chip, chipData: ChipDataInterface?, newSize: Int) {
                Toast.makeText(this@MainActivity, "Chip added, new size is $newSize", Toast.LENGTH_SHORT).show()
            }

            override fun onChipRemoved(view: Chip, chipData: ChipDataInterface?, newSize: Int) {
                Toast.makeText(this@MainActivity, "Chip removed, new size is $newSize", Toast.LENGTH_SHORT).show()
            }

        })

        chipsInput.addChip("petit test")
    }

    fun getContext(): Context {
        return this
    }

}
