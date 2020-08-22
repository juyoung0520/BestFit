package com.example.bestfit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_set_profile_first.view.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.view.*

class SetProfileFirstFragment  : Fragment() {
    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_set_profile_first, container, false)

        fragmentView.fragment_set_profile_first_text_nickname.setOnFocusChangeListener { _, b ->
            fragmentView.fragment_set_profile_first_error_nickname.visibility = View.GONE

            if (!b) {
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(fragmentView.windowToken, 0)
            }
        }

        fragmentView.fragment_set_profile_first_group_gender.addOnButtonCheckedListener { _, _, _ ->
            fragmentView.fragment_set_profile_first_error_group_gender.visibility = View.GONE
            fragmentView.fragment_set_profile_first_text_nickname.clearFocus()
        }

        fragmentView.fragment_set_profile_first_actv_birth.setOnFocusChangeListener{ _, b ->
            fragmentView.fragment_set_profile_first_error_birth.visibility = View.GONE
        }

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

        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, years)
        view.fragment_set_profile_first_actv_birth.setAdapter(adapter)
    }

    private fun submitSetProfile() {
        val setProfileActivity = activity as SetProfileActivity
        setProfileActivity.changeViewPage(false)
    }
}