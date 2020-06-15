package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*
import kotlinx.android.synthetic.main.spinner_item.view.*

class AddItemFirstFragment  : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_item, container, false)

        initSpinner(view)

        return view
    }

//    accountDTO.skip = true

    fun initSpinner(view: View) {
        // 카테고리 처음 실행했을 때 불러와서 변수에 저장해두기?
        val items = resources.getStringArray(R.array.대분류)
        val categoryAdapter = object : ArrayAdapter<String>(context!!, R.layout.spinner_item) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)

                if (position == count) {
                    v.spinner_item_tv.text = ""
                    v.spinner_item_tv.hint = getItem(count)
                }

                return v
            }

            override fun getCount(): Int {
                return super.getCount() - 1
            }
        }

        categoryAdapter.addAll(items.toMutableList())
        categoryAdapter.add("대분류")

        view.fragment_add_item_spinner_category.adapter = categoryAdapter
        view.fragment_add_item_spinner_category.setSelection(categoryAdapter.count)
    }
}