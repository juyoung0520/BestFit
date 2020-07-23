package com.example.bestfit.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.textfield.TextInputLayout

class EditText : com.google.android.material.textfield.TextInputEditText, View.OnFocusChangeListener {
    private var textInputLayout: TextInputLayout? = null
    private var hint: String? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int) : super(context, attrs, defStyleAttr)

    init {
        super.setOnFocusChangeListener(this)
    }

    fun setTextInputLayout(textInputLayout: TextInputLayout) {
        this.textInputLayout = textInputLayout
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        super.setOnFocusChangeListener(this)
    }

    override fun onFocusChange(p0: View?, p1: Boolean) {
        if (textInputLayout != null) {
            if (p1) {
                if (hint.isNullOrEmpty())
                    hint = textInputLayout!!.hint.toString()

                textInputLayout!!.hint = ""

                println("focus true $hint")
            }
            else if (text.isNullOrEmpty()) {
                textInputLayout!!.hint = hint
                hint = null

                println("focus false")
            }
        }
    }
}