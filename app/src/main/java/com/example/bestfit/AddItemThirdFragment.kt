package com.example.bestfit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.bestfit.model.SizeFormatDTO
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_add_item_third.view.*

class AddItemThirdFragment  : Fragment() {
    lateinit var fragmentView: View

    // arrayListOf("FREE", "S/M/L", "90/95/100", "26/28/30", "240/250/260", "1/2/3", "48/50/52", "75A/80B/85C")
    private val sizeFormats: ArrayList<SizeFormatDTO> = arrayListOf(
        SizeFormatDTO("FREE", arrayListOf()),
        SizeFormatDTO("S/M/L", arrayListOf("XXXS", "XXS", "XS", "S", "M", "L", "XL", "XXL", "XXXL")),
        SizeFormatDTO("90/95/100", arrayListOf("75", "80", "85", "90", "95", "100", "105", "110", "115")),
        SizeFormatDTO("26/28/30", arrayListOf("23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36")),
        SizeFormatDTO("240/250/260", arrayListOf("200", "205", "210", "215", "220", "225", "230", "235", "240", "245", "250", "255", "260", "265", "270", "275", "280", "285", "290", "295", "300")),
        SizeFormatDTO("1/2/3", arrayListOf("1", "2", "3", "4")),
        SizeFormatDTO("48/50/52", arrayListOf("46", "48", "50", "52", "54")),
        SizeFormatDTO("75A/80B/85C", arrayListOf("60AA", "60A", "60B", "60C", "60D", "60E", "60F", "60G", "65AA", "65A", "65B", "65C", "65D", "65E", "65F", "65G", "70AA", "70A", "70B", "70C", "70D", "70E", "70F", "70G", "75AA", "75A", "75B", "75C", "75D", "75E", "75F", "75G", "80AA", "80A", "80B", "80C", "80D", "80E", "80F", "80G", "85AA", "85A", "85B", "85C", "85D", "85E", "85F", "85G", "90AA", "90A", "90B", "90C", "90D", "90E", "90F", "90G"))
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_add_item_third, container, false)

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
        for ((idx, format) in sizeFormats.withIndex()) {
            val formatButton = MaterialButton(view.context)
            formatButton.id = idx
            formatButton.text = format.name

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

            for (size in format.lists) {
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