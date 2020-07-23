package com.example.bestfit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.bestfit.model.SizeFormatDTO
import com.example.bestfit.util.InitData
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add_item_third.view.*

class AddItemThirdFragment  : Fragment() {
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_add_item_third, container, false)

        fragmentView.fragment_add_item_third_text_review.setTextInputLayout(fragmentView.fragment_add_item_third_layout_text_review)

        setHasOptionsMenu(true)

        initSelectedSizeTable(fragmentView)

        return fragmentView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_add_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_item_action_add -> {
                submitAddItem()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun submitAddItem() {
        val addItemActivity = activity as AddItemActivity
        addItemActivity.submitAddItem()
    }

    private fun initSelectedSizeTable(view: View) {
        val sizeFormats = InitData.sizeFormatDTOs

        for ((idx, format) in sizeFormats.withIndex()) {
            val formatButton = MaterialButton(view.context)
            formatButton.id = idx
            formatButton.text = format.format

            view.fragment_add_item_third_group_format.addView(formatButton, -1, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (40 * resources.displayMetrics.density).toInt()))
        }

        view.fragment_add_item_third_group_format.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (group.checkedButtonId == -1) {
                view.fragment_add_item_third_layout_selected_size.clearFocus()
                view.fragment_add_item_third_layout_divider.setBackgroundColor(resources.getColor(R.color.colorHintTransparent))

                view.fragment_add_item_third_group_size.removeAllViews()

                return@addOnButtonCheckedListener
            }

            if (!isChecked)
                return@addOnButtonCheckedListener

            view.fragment_add_item_third_layout_selected_size.requestFocus()
            view.fragment_add_item_third_layout_divider.setBackgroundColor(resources.getColor(R.color.colorPrimaryTransparent))

            view.fragment_add_item_third_group_size.removeAllViews()
            val format = sizeFormats[group.checkedButtonId]

            for (size in format.list) {
                val sizeButton = MaterialButton(view.context)
                sizeButton.text = size
                sizeButton.isCheckable = true // 이걸 해야 checked 된 상태로 add 되지 않음. (이유는 모름)
                sizeButton.isChecked = false

                view.fragment_add_item_third_group_size.addView(sizeButton, -1, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (40 * resources.displayMetrics.density).toInt()))
            }
        }

        view.fragment_add_item_third_layout_selected_size.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                view.fragment_add_item_third_layout_divider.setBackgroundColor(resources.getColor(R.color.colorHintTransparent))
        }
    }
}