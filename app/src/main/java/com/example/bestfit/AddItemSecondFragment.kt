package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.bestfit.util.InitData
import kotlinx.android.synthetic.main.fragment_add_item_second.view.*


class AddItemSecondFragment  : Fragment() {
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_add_item_second, container, false)

        fragmentView.fragment_add_item_second_text_item_name.setTextInputLayout(fragmentView.fragment_add_item_second_layout_text_item_name)

        initBrand(fragmentView)

        fragmentView.fragment_add_item_second_btn_submit.setOnClickListener {
            submitAddItem()
        }

        return fragmentView
    }

    private fun initBrand(view: View) {
        val brands = InitData.brands
        val categoryAdapter = ArrayAdapter(context!!, R.layout.item_dropdown, brands)

        view.fragment_add_item_second_actv_brand.setAdapter(categoryAdapter)
        view.fragment_add_item_second_actv_brand.setOnFocusChangeListener { _, b ->
            println("focus")
            if (b) {
                view.fragment_add_item_second_layout_actv_brand.hint = ""
                view.fragment_add_item_second_actv_brand.text = null
            }
            else if (view.fragment_add_item_second_actv_brand.text.isNullOrEmpty())
                view.fragment_add_item_second_layout_actv_brand.hint = "브랜드/쇼핑몰"
        }
        view.fragment_add_item_second_actv_brand.setOnItemClickListener { _, _, _, _ ->
            println("test")
            view.fragment_add_item_second_text_item_name.requestFocus()
        }
    }

    private fun submitAddItem() {
        val addItemActivity = activity as AddItemActivity
        addItemActivity.changeViewPage(false)
    }
}