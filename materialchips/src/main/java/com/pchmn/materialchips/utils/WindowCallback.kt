package com.pchmn.materialchips.utils

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.pchmn.materialchips.views.ExpandedChip

class WindowCallback(private val mLocalCallback: Window.Callback, private val mActivity: Activity): Window.Callback {

    init {
        Log.d("WindowCallback", "init")
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        Log.d("WindowCallback", "in dispatch")
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val v: View? = mActivity.currentFocus
            if (v is ExpandedChip) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.fadeOut()
                }
            }
        }
        return mLocalCallback.dispatchTouchEvent(event)
    }

    override fun onActionModeFinished(mode: ActionMode?) {
        mLocalCallback.onActionModeFinished(mode)
    }

    override fun onCreatePanelView(featureId: Int): View? {
        return mLocalCallback.onCreatePanelView(featureId)
    }

    override fun onCreatePanelMenu(featureId: Int, menu: Menu): Boolean {
        return mLocalCallback.onCreatePanelMenu(featureId, menu)
    }

    override fun onWindowStartingActionMode(callback: ActionMode.Callback?): ActionMode? {
        return mLocalCallback.onWindowStartingActionMode(callback)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onWindowStartingActionMode(
        callback: ActionMode.Callback?,
        type: Int
    ): ActionMode? {
        return mLocalCallback.onWindowStartingActionMode(callback, type)
    }

    override fun onAttachedToWindow() {
        Log.d("WindowCallback", "onAttachedToWindow")
        mLocalCallback.onAttachedToWindow()
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent?): Boolean {
        return mLocalCallback.dispatchGenericMotionEvent(event)
    }

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent?): Boolean {
        return mLocalCallback.dispatchPopulateAccessibilityEvent(event)
    }

    override fun dispatchTrackballEvent(event: MotionEvent?): Boolean {
        return mLocalCallback.dispatchTrackballEvent(event)
    }

    override fun dispatchKeyShortcutEvent(event: KeyEvent?): Boolean {
        return mLocalCallback.dispatchKeyShortcutEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return mLocalCallback.dispatchKeyEvent(event)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        return mLocalCallback.onMenuOpened(featureId, menu)
    }

    override fun onPanelClosed(featureId: Int, menu: Menu) {
        mLocalCallback.onPanelClosed(featureId, menu)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        return mLocalCallback.onMenuItemSelected(featureId, item)
    }

    override fun onDetachedFromWindow() {
        mLocalCallback.onDetachedFromWindow()
    }

    override fun onPreparePanel(featureId: Int, view: View?, menu: Menu): Boolean {
        return mLocalCallback.onPreparePanel(featureId, view, menu)
    }

    override fun onWindowAttributesChanged(attrs: WindowManager.LayoutParams?) {
        mLocalCallback.onWindowAttributesChanged(attrs)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        mLocalCallback.onWindowFocusChanged(hasFocus)
    }

    override fun onContentChanged() {
        mLocalCallback.onContentChanged()
    }

    override fun onSearchRequested(): Boolean {
        return mLocalCallback.onSearchRequested()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onSearchRequested(searchEvent: SearchEvent?): Boolean {
        return mLocalCallback.onSearchRequested(searchEvent)
    }

    override fun onActionModeStarted(mode: ActionMode?) {
        mLocalCallback.onActionModeStarted(mode)
    }
}