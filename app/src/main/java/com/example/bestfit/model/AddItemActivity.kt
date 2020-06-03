
package com.example.bestfit.model

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.bestfit.R
import kotlinx.android.synthetic.main.activity_add_item.*

class AddItemActivity : AppCompatActivity() {
    val items = arrayOf("아우터", "상의", "바지", "치마", "원피스/세트")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item, items)

        activity_add_item_spinner.adapter = spinnerAdapter
        activity_add_item_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                when(p2) {
                    0 -> {

                    }

                    else -> {

                    }
                }
            }

        }

    }
}