package com.example.bestfit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
            if (b) {
//                println("top ${view.fragment_add_item_first_tv_brand.top}")
//                view.fragment_add_item_scrollview.scrollTo(0, view.fragment_add_item_first_tv_brand.bottom)
                view.fragment_add_item_second_actv_brand.text = null
            }
//            else {
//                view.fragment_add_item_first_actv_brand.error = "직접 입력한 브랜드/쇼핑몰은 검색 결과에 제대로 노출되지 않을 수 있습니다."
//            }
        }
        view.fragment_add_item_second_actv_brand.setOnItemClickListener { parent, _, position, id ->
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            view.fragment_add_item_second_actv_brand.clearFocus()
        }
    }

    private fun submitAddItem() {
        val addItemActivity = activity as AddItemActivity
        addItemActivity.changeViewPage(false)
    }
}