package com.example.bestfit

import android.os.Bundle
import android.view.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.model.SizeFormatDTO
import com.example.bestfit.util.InitData
import com.example.bestfit.viewmodel.AddItemActivityViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add_item_second.view.*
import kotlinx.android.synthetic.main.fragment_add_item_third.view.*

class AddItemThirdFragment  : Fragment() {
    private lateinit var viewModel: AddItemActivityViewModel

    lateinit var fragmentView: View
    var selectedSizeFormatId: String? = null
    var selectedSizeId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_add_item_third, container, false)

        initSelectedSizeTable(fragmentView)

        fragmentView.fragment_add_item_third_group_size_review.addOnButtonCheckedListener { group, _, isChecked ->
            fragmentView.fragment_add_item_third_error_size_review.visibility = View.GONE

            if (!isChecked)
                return@addOnButtonCheckedListener

            fragmentView.fragment_add_item_third_group_size_review.tag = when (group.checkedButtonId) {
                fragmentView.fragment_add_item_third_btn_s.id -> 0
                fragmentView.fragment_add_item_third_btn_m.id -> 1
                fragmentView.fragment_add_item_third_btn_l.id -> 2
                else -> -1
            }
        }

        fragmentView.fragment_add_item_third_btn_submit.setOnClickListener {
            submitAddItem()
        }

        return fragmentView
    }

    private fun initSelectedSizeTable(view: View) {
        val sizeFormats = InitData.sizeFormatDTOs

        for ((idx, format) in sizeFormats.withIndex()) {
            val formatButton = MaterialButton(view.context)
            formatButton.id = idx
            formatButton.text = format.format

            view.fragment_add_item_third_group_format.addView(formatButton, -1, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (40 * resources.displayMetrics.density).toInt()))
        }

        view.fragment_add_item_third_group_format.addOnButtonCheckedListener { group, _, isChecked ->
            if (selectedSizeFormatId == null )
                view.fragment_add_item_third_error_size.visibility = View.GONE

            if (group.checkedButtonId == -1) {
                view.fragment_add_item_third_layout_selected_size.clearFocus()
                view.fragment_add_item_third_layout_divider.setBackgroundColor(resources.getColor(R.color.colorHintTransparent))

                view.fragment_add_item_third_group_size.removeAllViews()

                selectedSizeFormatId = null
                selectedSizeId = null

                return@addOnButtonCheckedListener
            }

            if (!isChecked)
                return@addOnButtonCheckedListener

            view.fragment_add_item_third_layout_selected_size.requestFocus()
            view.fragment_add_item_third_layout_divider.setBackgroundColor(resources.getColor(R.color.colorPrimaryTransparent))

            view.fragment_add_item_third_group_size.removeAllViews()
            val format = sizeFormats[group.checkedButtonId]

            for ((idx, size) in format.list.withIndex()) {
                val sizeButton = MaterialButton(view.context)
                sizeButton.id = idx
                sizeButton.tag = format.listId[idx]
                sizeButton.text = size
                sizeButton.isCheckable = true // 이걸 해야 checked 된 상태로 add 되지 않음. (이유는 모름)
                sizeButton.isChecked = false

                view.fragment_add_item_third_group_size.addView(sizeButton, -1, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (40 * resources.displayMetrics.density).toInt()))
            }

            selectedSizeFormatId = format.id

            if (selectedSizeFormatId == sizeFormats[0].id)
                selectedSizeId = selectedSizeFormatId
            else
                selectedSizeId = null
        }

        view.fragment_add_item_third_group_size.addOnButtonCheckedListener { group, _, isChecked ->
            view.fragment_add_item_third_error_size.visibility = View.GONE

            if (group.checkedButtonId == -1) {
                selectedSizeId = null

                return@addOnButtonCheckedListener
            }

            if (!isChecked)
                return@addOnButtonCheckedListener

            selectedSizeId = group[group.checkedButtonId].tag as String
        }

        view.fragment_add_item_third_layout_selected_size.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                view.fragment_add_item_third_layout_divider.setBackgroundColor(resources.getColor(R.color.colorHintTransparent))
        }

        initViewModel(view)
    }

    private fun initViewModel(view: View) {
        viewModel = ViewModelProvider(requireActivity()).get(AddItemActivityViewModel::class.java)

        val tempItemDTOObserver = Observer<ItemDTO> { tempItemDTO ->
            initTempCategory(view, tempItemDTO)
        }

        viewModel.tempItemDTO.observe(viewLifecycleOwner, tempItemDTOObserver)
    }

    private fun initTempCategory(view: View, tempItemDTO: ItemDTO) {
        view.fragment_add_item_third_group_format.check(InitData.getSizeFormatIndex(tempItemDTO.sizeFormatId!!))
        view.fragment_add_item_third_group_size.check(InitData.getSizeIndex(tempItemDTO.sizeFormatId!!, tempItemDTO.sizeId!!))

        val sizeReviewId = when (tempItemDTO.sizeReview) {
            0 -> R.id.fragment_add_item_third_btn_s
            1 -> R.id.fragment_add_item_third_btn_m
            2 -> R.id.fragment_add_item_third_btn_l
            else -> -1
        }

        view.fragment_add_item_third_group_size_review.check(sizeReviewId)
    }

    private fun submitAddItem() {
        val addItemActivity = activity as AddItemActivity
        addItemActivity.changeViewPage(false)
    }
}