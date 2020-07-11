package com.example.bestfit

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

class ClearEditText : AppCompatEditText, TextWatcher, OnTouchListener, OnFocusChangeListener {

    private var clearDrawable: Drawable? = null

    constructor(context : Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int) : super(context, attrs, defStyleAttr)

    init {

        val tempDrawable = ContextCompat.getDrawable(context, R.drawable.abc_ic_clear_material)
        clearDrawable = DrawableCompat.wrap(tempDrawable!!)
        DrawableCompat.setTint(clearDrawable!!,resources.getColor(R.color.colorBlack))
        clearDrawable!!.setBounds(0, 0, clearDrawable!!.getIntrinsicWidth(), clearDrawable!!.getIntrinsicHeight()
        )

        setClearIconVisible(false)
        super.setOnTouchListener(this)
        super.setOnFocusChangeListener(this)
        addTextChangedListener(this)
    }

    private fun setClearIconVisible(visible: Boolean) {
        clearDrawable?.setVisible(visible, false)
        setCompoundDrawables(null, null, if (visible) clearDrawable else null, null)
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (isFocused) {
            setClearIconVisible(s.length > 0)
        }
    }

    override fun afterTextChanged(s: Editable?) {
//        if (s.toString().contains("#")) {
//            view.fragment_signin_layout_email.error = "#은 안돼용"
//        } else {
//            view.fragment_signin_layout_email.error = null
//        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val x = event!!.x.toInt()
        if (clearDrawable!!.isVisible && x > width - paddingRight - clearDrawable!!.intrinsicWidth) {
            if (event.action == MotionEvent.ACTION_UP) {
                error = null
                setText(null)
            }
            return true
        }

       return false
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            setClearIconVisible(text!!.length > 0)
        } else {
            setClearIconVisible(false)
        }
    }
}