
package com.example.bestfit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter

class AddItemActivity : AppCompatActivity() {
    val items = arrayOf("아우터", "상의", "바지", "치마", "원피스/세트")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item, items)

//        activity_add_item_spinner_category.adapter = spinnerAdapter
//        activity_add_item_spinner_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//            }
//
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//
//                when(p2) {
//                    0 -> {
//
//                    }
//
//                    else -> {
//
//                    }
//                }
//            }
//
//        }

    }
}