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

        fragmentView.fragment_set_profile_first_text_nickname.setTextInputLayout(fragmentView.fragment_set_profile_first_layout_text_nickname)

        fragmentView.fragment_set_profile_first_btn_submit.setOnClickListener {
            submitSetProfile()
        }

        initBirth(fragmentView)

        return fragmentView
    }

    private fun initBirth(view: View) {
        val years : ArrayList<Int> = arrayListOf()

        for (year in 2020 downTo 1920)
            years.add(year)

        val adapter = ArrayAdapter(context!!, R.layout.item_dropdown, years)
        view.fragment_set_profile_first_actv_birth.setAdapter(adapter)
    }

    private fun submitSetProfile() {
        val setProfileActivity = activity as SetProfileActivity
        setProfileActivity.changeViewPage(false)
    }
}