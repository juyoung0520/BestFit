package com.example.bestfit.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.google.android.material.textfield.TextInputLayout


class EditText : com.google.android.material.textfield.TextInputEditText, View.OnFocusChangeListener {
    private var textInputLayout : TextInputLayout? = null

    constructor(context : Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int) : super(context, attrs, defStyleAttr)

    init {
        super.setOnFocusChangeListener(this)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            this.hint = "성공"
        }
    }

    fun setTextInputLayout(textInputLayout: TextInputLayout) {
        this.textInputLayout = textInputLayout



    }
}