package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_set_profile_first.view.*

class SetProfileFirstFragment  : Fragment() {
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_set_profile_first, container, false)

//        initNumberPicker(fragmentView)
        initList(fragmentView)


        fragmentView.fragment_set_profile_first_btn_submit.setOnClickListener {
            submitSetProfile()
        }

        return fragmentView
    }

    private fun initList(view: View) {
        val list : ArrayList<Int> = arrayListOf()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_list, list)

        for (birth in 2020 .. 1920) {
            list.add(birth)
        }
        view.fragment_set_profile_first_actv_birth.setAdapter(adapter)
    }

//    private fun initNumberPicker(view: View) {
//        view.fragment_set_profile_first_np_birth.minValue = 1920
//        view.fragment_set_profile_first_np_birth.maxValue = 2020
//        view.fragment_set_profile_first_np_birth.value = 2001
//        view.fragment_set_profile_first_np_birth.wrapSelectorWheel = false
//        view.fragment_set_profile_first_np_birth.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
//    }

    private fun submitSetProfile() {
        val setProfileActivity = activity as SetProfileActivity
        setProfileActivity.changeViewPage(false)
    }

//    fun getData(): Bundle {
//        val bundle = Bundle()
//        bundle.putString("nickname", view.)
//    }
}