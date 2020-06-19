package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.example.bestfit.model.AccountDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add_item_second.view.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.view.*

class SetProfileSecondFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_set_profile_second, container, false)

        initNumberPicker(view)

        view.fragment_set_profile_second_btn_submit.setOnClickListener {
            submitSetProfile()
        }

        return view
    }

    private fun initNumberPicker(view: View) {
        view.fragment_set_profile_second_np_height.minValue = 120
        view.fragment_set_profile_second_np_height.maxValue = 220
        view.fragment_set_profile_second_np_height.value = 165
        view.fragment_set_profile_second_np_height.wrapSelectorWheel = false
        view.fragment_set_profile_second_np_height.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        view.fragment_set_profile_second_np_weight.minValue = 10
        view.fragment_set_profile_second_np_weight.maxValue = 250
        view.fragment_set_profile_second_np_weight.value = 50
        view.fragment_set_profile_second_np_weight.wrapSelectorWheel = false
        view.fragment_set_profile_second_np_weight.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }

    private fun submitSetProfile() {
        val setProfileActivity = activity as SetProfileActivity
        setProfileActivity.changeViewPage(false)
    }
}