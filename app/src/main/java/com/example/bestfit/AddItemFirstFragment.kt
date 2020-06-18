package com.example.bestfit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.bestfit.util.InitData
import kotlinx.android.synthetic.main.fragment_add_item_first.view.*


class AddItemFirstFragment  : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_item_first, container, false)

        initCategory(view)
        initBrand(view)

        return view
    }

//    accountDTO.skip = true

    fun initCategory(view: View) {
        val categoryDTOs = InitData.categoryDTOs.subList(1, InitData.categoryDTOs.lastIndex + 1)
        val categories = InitData.categories.subList(1, InitData.categories.lastIndex + 1)
        val categoryAdapter = ArrayAdapter(context!!, R.layout.item_dropdown, categories)

        view.fragment_add_item_first_actv_category.setAdapter(categoryAdapter)
        view.fragment_add_item_first_actv_category.keyListener = null
        view.fragment_add_item_first_actv_category.setOnTouchListener { v, event ->
            (v as AutoCompleteTextView).showDropDown()
            false
        }
        view.fragment_add_item_first_actv_category.setOnItemClickListener { parent, _, position, id ->
            if (view.fragment_add_item_first_layout_divider_category.visibility == View.GONE) {
                view.fragment_add_item_first_layout_divider_category.visibility = View.VISIBLE
                view.fragment_add_item_first_layout_sub_category.visibility = View.VISIBLE
            }

            view.fragment_add_item_first_actv_sub_category.text = null
            initSubCategory(view, categoryDTOs[position].sub!!)
        }
    }

    fun initSubCategory(view: View, subCategories: ArrayList<String>) {
        val categoryAdapter = ArrayAdapter(context!!, R.layout.item_dropdown, subCategories)

        view.fragment_add_item_first_actv_sub_category.setAdapter(categoryAdapter)
        view.fragment_add_item_first_actv_sub_category.keyListener = null
        view.fragment_add_item_first_actv_sub_category.setOnTouchListener { v, event ->
            (v as AutoCompleteTextView).showDropDown()
            false
        }
    }

    fun initBrand(view: View) {
        val brands = InitData.brands
        val categoryAdapter = ArrayAdapter(context!!, R.layout.item_dropdown, brands)

        view.fragment_add_item_first_actv_brand.setAdapter(categoryAdapter)
        view.fragment_add_item_first_actv_brand.setOnTouchListener { v, event ->
            (v as AutoCompleteTextView).text = null
            false
        }
        view.fragment_add_item_first_actv_brand.setOnItemClickListener { parent, _, position, id ->
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}