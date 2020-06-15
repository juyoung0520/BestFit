package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
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

        initCategorySpinner(view)

        test(view)

        return view
    }

    // 스피너 커스텀
    // https://www.it-swarm.dev/ko/android/android%EC%97%90%EC%84%9C-spinner%EB%A5%BC-%EB%A7%9E%EC%B6%A4-%EC%84%A4%EC%A0%95%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95/1073376298/

//    accountDTO.skip = true

    fun test(view: View) {
        var categoryAdapter = object : ArrayAdapter<String>(context!!, R.layout.spinner_item) {
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

        val categoryDTOs = InitData.categoryDTOs
        categoryDTOs.removeAt(0)

        val categories = InitData.categories
        categories.removeAt(0)

        categoryAdapter.addAll(categories)
        categoryAdapter.add("대분류")

        view.fragment_add_item_first_actv_category.setAdapter(categoryAdapter)
        view.fragment_add_item_first_actv_category.keyListener = null
        view.fragment_add_item_first_actv_category.setOnTouchListener { v, event ->
            (v as AutoCompleteTextView).showDropDown()
            false
        }

//        view.fragment_add_item_first_actv_category.setSelection(categoryAdapter.count)
    }

    fun initCategorySpinner(view: View) {
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

        val categoryDTOs = InitData.categoryDTOs
        categoryDTOs.removeAt(0)

        val categories = InitData.categories
        categories.removeAt(0)

        categoryAdapter.addAll(categories)
        categoryAdapter.add("대분류")

        view.fragment_add_item_first_spinner_category.adapter = categoryAdapter
        view.fragment_add_item_first_spinner_category.setSelection(categoryAdapter.count)
        view.fragment_add_item_first_spinner_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view1: View?,
                position: Int,
                id: Long
            ) {
                if (position != categoryAdapter.count) {
                    if (view.fragment_add_item_first_layout_divider_category.visibility == View.GONE) {
                        view.fragment_add_item_first_layout_divider_category.visibility = View.VISIBLE
                        view.fragment_add_item_first_layout_sub_category.visibility = View.VISIBLE
                    }

                    initSubCategorySpinner(view, categoryDTOs[position].sub!!)
                }
            }
        }
    }

    fun initSubCategorySpinner(view: View, subCategories: ArrayList<String>) {
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

        categoryAdapter.addAll(subCategories)
        categoryAdapter.add("소분류")

        view.fragment_add_item_first_spinner_sub_category.adapter = categoryAdapter
        view.fragment_add_item_first_spinner_sub_category.setSelection(categoryAdapter.count)
    }
}