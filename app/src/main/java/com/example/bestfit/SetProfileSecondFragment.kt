package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_set_profile_second.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.view.*

class SetProfileSecondFragment : Fragment() {
    var fragment : View ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragment = inflater.inflate(R.layout.fragment_set_profile_second, container, false)

        setProfileHeight()
        setProfileWeight()

        return fragment
    }

    private fun setProfileHeight() {
        fragment?.fragment_set_profile_second_np_height?.minValue = 130
        fragment?.fragment_set_profile_second_np_height?.maxValue = 200
        fragment?.fragment_set_profile_second_np_height?.wrapSelectorWheel = false
        fragment?.fragment_set_profile_second_np_height?.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }

    private fun setProfileWeight() {
        fragment?.fragment_set_profile_second_np_weight?.minValue = 30
        fragment?.fragment_set_profile_second_np_weight?.maxValue = 150
        fragment?.fragment_set_profile_second_np_weight?.wrapSelectorWheel = false
        fragment?.fragment_set_profile_second_np_weight?.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }
}