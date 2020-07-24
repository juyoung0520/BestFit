package com.example.bestfit

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import androidx.annotation.Nullable
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.util.InitData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add_item_second.view.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.view.*
import kotlinx.android.synthetic.main.item_dialogpicker.*
import kotlinx.android.synthetic.main.item_dialogpicker.view.*
import kotlin.math.max

class SetProfileSecondFragment : Fragment() {
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_set_profile_second, container, false)

        fragmentView.fragment_set_profile_second_text_height.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                var defaultValue = 165

                if (fragmentView.fragment_set_profile_second_text_height.text!!.isDigitsOnly())
                    defaultValue = fragmentView.fragment_set_profile_second_text_height.text.toString().toInt()

                initDialogNumberPicker(fragmentView.fragment_set_profile_second_text_height, 110, 230, defaultValue, "cm")
                v.clearFocus()
            }
        }

        fragmentView.fragment_set_profile_second_text_weight.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                var defaultValue = 50

                if (fragmentView.fragment_set_profile_second_text_weight.text!!.isDigitsOnly())
                    defaultValue = fragmentView.fragment_set_profile_second_text_weight.text.toString().toInt()

                initDialogNumberPicker(fragmentView.fragment_set_profile_second_text_weight, 10, 300, defaultValue, "kg")
                v.clearFocus()
            }
        }

        fragmentView.fragment_set_profile_second_text_top.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val sizeFormatId = "01"
                var defaultSizeId = "0104" // M

                if (fragmentView.fragment_set_profile_second_text_top.text!!.isNotEmpty())
                    defaultSizeId = InitData.getSizeId(sizeFormatId, fragmentView.fragment_set_profile_second_text_top.text.toString()) ?: defaultSizeId

                initDialogPicker(fragmentView.fragment_set_profile_second_text_top, sizeFormatId, defaultSizeId)
                v.clearFocus()
            }
        }

        fragmentView.fragment_set_profile_second_text_bottom.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val sizeFormatId = "03"
                var defaultSizeId = "0305" // 28

                if (fragmentView.fragment_set_profile_second_text_bottom.text!!.isNotEmpty())
                    defaultSizeId = InitData.getSizeId(sizeFormatId, fragmentView.fragment_set_profile_second_text_bottom.text.toString()) ?: defaultSizeId

                initDialogPicker(fragmentView.fragment_set_profile_second_text_bottom, sizeFormatId, defaultSizeId)
                v.clearFocus()
            }
        }

        fragmentView.fragment_set_profile_second_text_shoes.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val sizeFormatId = "04"
                var defaultSizeId = "0410" // 250

                if (fragmentView.fragment_set_profile_second_text_shoes.text!!.isNotEmpty())
                    defaultSizeId = InitData.getSizeId(sizeFormatId, fragmentView.fragment_set_profile_second_text_shoes.text.toString()) ?: defaultSizeId

                initDialogPicker(fragmentView.fragment_set_profile_second_text_shoes, sizeFormatId, defaultSizeId)
                v.clearFocus()
            }
        }

        fragmentView.fragment_set_profile_second_btn_submit.setOnClickListener {
            submitSetProfile()
        }

        return fragmentView
    }

    private fun initDialogNumberPicker(view: EditText, minValue: Int, maxValue: Int, defaultValue: Int, suffix: String) {
        val dialog = MaterialAlertDialogBuilder(context!!)
        val dialogPickerView = LayoutInflater.from(context).inflate(R.layout.item_dialogpicker, null)
        val picker = dialogPickerView.item_dialogpicker_np

        picker.minValue = minValue
        picker.maxValue = maxValue
        picker.value = defaultValue
        picker.wrapSelectorWheel = false
        picker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        dialogPickerView.item_dialogpicker_tv_suffix.text = suffix

        dialog.setNegativeButton("취소", DialogInterface.OnClickListener { _, _ ->  })
        dialog.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
            view.setText(picker.value.toString())
        })
        dialog.setView(dialogPickerView)
        dialog.show()
    }

    private fun initDialogPicker(view: EditText, sizeFormatId: String, defaultSizeId: String) {
        val dialog = MaterialAlertDialogBuilder(context!!)
        val dialogPickerView = LayoutInflater.from(context).inflate(R.layout.item_dialogpicker, null)
        val picker = dialogPickerView.item_dialogpicker_np
        val displayedArray = InitData.sizeFormatDTOs[InitData.getSizeFormatIndex(sizeFormatId)].list.toTypedArray()

        picker.minValue = 0
        picker.maxValue = displayedArray.size - 1
        picker.displayedValues = displayedArray
        picker.value = InitData.getSizeIndex(sizeFormatId, defaultSizeId)
        picker.wrapSelectorWheel = false
        picker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        dialog.setNegativeButton("취소", DialogInterface.OnClickListener { _, _ ->  })
        dialog.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
            view.setText(displayedArray[picker.value])
        })
        dialog.setView(dialogPickerView)
        dialog.show()
    }

    private fun submitSetProfile() {
        val setProfileActivity = activity as SetProfileActivity
        setProfileActivity.changeViewPage(false)
    }
}