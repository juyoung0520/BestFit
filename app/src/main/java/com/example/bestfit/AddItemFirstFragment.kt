package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.example.bestfit.model.CategoryDTO
import kotlinx.android.synthetic.main.fragment_add_item_first.view.*
import kotlinx.android.synthetic.main.spinner_item.view.*
import com.example.bestfit.util.InitData

class AddItemFirstFragment  : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_item_first, container, false)

        initCategory(view)

        return view
    }

//    accountDTO.skip = true

    fun initCategory(view: View) {
        println("hi")
        val categoryDTOs = InitData.categoryDTOs.subList(1, InitData.categoryDTOs.lastIndex + 1)
        val categories = InitData.categories.subList(1, InitData.categories.lastIndex + 1)

        println(categoryDTOs)

        val categoryAdapter = ArrayAdapter(context!!, R.layout.spinner_item, categories)

        view.fragment_add_item_first_actv_category.setAdapter(categoryAdapter)
        view.fragment_add_item_first_actv_category.keyListener = null
        view.fragment_add_item_first_actv_category.setOnTouchListener { v, event ->
            (v as AutoCompleteTextView).showDropDown()
            false
        }
        view.fragment_add_item_first_actv_category.setOnItemClickListener { parent, _, position, id ->
            println("position = $position, count = ${categoryAdapter.count}")
            if (view.fragment_add_item_first_layout_divider_category.visibility == View.GONE) {
                view.fragment_add_item_first_layout_divider_category.visibility = View.VISIBLE
                view.fragment_add_item_first_layout_sub_category.visibility = View.VISIBLE
            }

            view.fragment_add_item_first_actv_sub_category.text = null
            initSubCategory(view, categoryDTOs[position].sub!!)
        }
    }

    fun initSubCategory(view: View, subCategories: ArrayList<String>) {
        val categoryAdapter = ArrayAdapter(context!!, R.layout.spinner_item, subCategories)

        view.fragment_add_item_first_actv_sub_category.setAdapter(categoryAdapter)
        view.fragment_add_item_first_actv_sub_category.keyListener = null
        view.fragment_add_item_first_actv_sub_category.setOnTouchListener { v, event ->
            (v as AutoCompleteTextView).showDropDown()
            false
        }
    }
}