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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add_item_second.view.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.view.*
import kotlin.math.max

class SetProfileSecondFragment : Fragment() {
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_set_profile_second, container, false)

        initNumberPicker(fragmentView)

        fragmentView.fragment_set_profile_second_text_height.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                var defaultValue = 165

                if (fragmentView.fragment_set_profile_second_text_height.text!!.isDigitsOnly())
                    defaultValue = fragmentView.fragment_set_profile_second_text_height.text.toString().toInt()

                initDialogPicker(fragmentView.fragment_set_profile_second_text_height, 110, 230, defaultValue)
                v.clearFocus()
            }
        }

//        fragmentView.fragment_set_profile_second_btn_submit.setOnClickListener {
//            submitSetProfile()
//        }

        return fragmentView
    }

    private fun initDialogPicker(view: EditText, minValue: Int, maxValue: Int, defaultValue: Int) {
        val dialog = MaterialAlertDialogBuilder(context!!)
        val picker = NumberPicker(context)

//        picker.layoutParams = ViewGroup.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT)
        picker.minValue = minValue
        picker.maxValue = maxValue
        picker.value = defaultValue
        picker.wrapSelectorWheel = false
        picker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        dialog.setNegativeButton("취소", DialogInterface.OnClickListener { _, _ ->  })
        dialog.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
            view.setText(picker.value.toString())
        })
        dialog.setView(picker)
        dialog.show()
    }

    private fun initNumberPicker(view: View) {
//        view.fragment_set_profile_second_np_height.minValue = 120
//        view.fragment_set_profile_second_np_height.maxValue = 220
//        view.fragment_set_profile_second_np_height.value = 165
//        view.fragment_set_profile_second_np_height.wrapSelectorWheel = false
//        view.fragment_set_profile_second_np_height.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
//
//        view.fragment_set_profile_second_np_weight.minValue = 10
//        view.fragment_set_profile_second_np_weight.maxValue = 250
//        view.fragment_set_profile_second_np_weight.value = 50
//        view.fragment_set_profile_second_np_weight.wrapSelectorWheel = false
//        view.fragment_set_profile_second_np_weight.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }

    private fun submitSetProfile() {
        val setProfileActivity = activity as SetProfileActivity
        setProfileActivity.changeViewPage(false)
    }
}