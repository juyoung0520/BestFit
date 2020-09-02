package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.example.bestfit.viewmodel.AddItemActivityViewModel
import kotlinx.android.synthetic.main.fragment_add_item_first.view.*
import kotlinx.android.synthetic.main.fragment_add_item_second.view.*


class AddItemSecondFragment  : Fragment() {
    private lateinit var viewModel: AddItemActivityViewModel

    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_add_item_second, container, false)

        initViewModel(fragmentView)

        fragmentView.fragment_add_item_second_text_item_name.setOnFocusChangeListener { view, b ->
            if (fragmentView.fragment_add_item_second_error_item_name.visibility == View.VISIBLE)
                fragmentView.fragment_add_item_second_error_item_name.visibility = View.GONE
        }

        fragmentView.fragment_add_item_second_text_item_name.doAfterTextChanged {
            viewModel.tempItemDTO.value!!.name = fragmentView.fragment_add_item_second_text_item_name.text.toString()
        }

        fragmentView.fragment_add_item_second_btn_submit.setOnClickListener {
            submitAddItem()
        }

        return fragmentView
    }

    private fun initViewModel(view: View) {
        viewModel = ViewModelProvider(requireActivity()).get(AddItemActivityViewModel::class.java)

        val tempItemDTOObserver = Observer<ItemDTO> {
            initBrandAdapter(view)
        }

        viewModel.tempItemDTO.observe(viewLifecycleOwner, tempItemDTOObserver)
    }

    private fun initViewFromTempItemDTO(view: View) {
        val tempItemDTO = viewModel.tempItemDTO.value!!

        if (tempItemDTO.name != null)
            view.fragment_add_item_second_text_item_name.setText(tempItemDTO.name)
    }

    private fun initBrandAdapter(view: View) {
        val brands = InitData.brands
        val categoryAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, brands)

        view.fragment_add_item_second_actv_brand.setAdapter(categoryAdapter)
        view.fragment_add_item_second_actv_brand.setOnFocusChangeListener { _, _ ->
            if (view.fragment_add_item_second_error_brand.visibility == View.VISIBLE)
                view.fragment_add_item_second_error_brand.visibility = View.GONE
        }
        view.fragment_add_item_second_actv_brand.setOnItemClickListener { _, _, _, _ ->
            view.fragment_add_item_second_text_item_name.requestFocus()
        }

        initViewFromTempItemDTO(view)
    }

    private fun submitAddItem() {
        val addItemActivity = activity as AddItemActivity
        addItemActivity.changeViewPage(false)
    }
}