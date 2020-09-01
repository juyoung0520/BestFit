package com.example.bestfit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.viewmodel.SetProfileActivityViewModel
import kotlinx.android.synthetic.main.fragment_set_profile_first.view.*
import kotlinx.android.synthetic.main.fragment_set_profile_second.view.*
import kotlinx.android.synthetic.main.fragment_signin.*

class SetProfileFirstFragment  : Fragment() {
    private lateinit var viewModel: SetProfileActivityViewModel

    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_set_profile_first, container, false)

        initViewModel(fragmentView)
        initNickname(fragmentView)
        initSex(fragmentView)

        fragmentView.fragment_set_profile_first_btn_submit.setOnClickListener {
            submitSetProfile()
        }

        return fragmentView
    }

    private fun initViewModel(view: View) {
        viewModel = ViewModelProvider(requireActivity()).get(SetProfileActivityViewModel::class.java)

        val tempAccountDTOObserver = Observer<AccountDTO> { tempAccountDTO ->
            initBirth(view)
        }

        viewModel.tempAccountDTO.observe(viewLifecycleOwner, tempAccountDTOObserver)
    }

    private fun initViewFromAccountDTO (view: View) {
        val accountDTO = viewModel.getTempAccountDTO()!!

        if (accountDTO.nickname != null)
            view.fragment_set_profile_first_text_nickname.setText(accountDTO.nickname)

        if (accountDTO.sex != null) {
            if (accountDTO.sex!!)
                view.fragment_set_profile_first_btn_male.isChecked = true
            else
                view.fragment_set_profile_first_btn_female.isChecked = true
        }

        if (accountDTO.birth != null) {
            view.fragment_set_profile_first_actv_birth.setText(accountDTO.birth!!.toString(), false)
        }
    }

    private fun initNickname(view: View) {
        view.fragment_set_profile_first_text_nickname.setOnFocusChangeListener { _, b ->
            if (view.fragment_set_profile_first_error_nickname.visibility == View.VISIBLE)
                view.fragment_set_profile_first_error_nickname.visibility = View.GONE

            if (!b) {
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        view.fragment_set_profile_first_text_nickname.doAfterTextChanged {
            viewModel.tempAccountDTO.value!!.nickname = view.fragment_set_profile_first_text_nickname.text.toString()
        }
    }

    private fun initSex(view: View) {
        view.fragment_set_profile_first_group_gender.addOnButtonCheckedListener { _, _, _ ->
            if (view.fragment_set_profile_first_error_group_gender.visibility == View.VISIBLE)
                view.fragment_set_profile_first_error_group_gender.visibility = View.GONE
            view.fragment_set_profile_first_text_nickname.clearFocus()
            viewModel.tempAccountDTO.value!!.sex = fragmentView.fragment_set_profile_first_group_gender.checkedButtonId == view.fragment_set_profile_first_btn_male.id
        }

    }

    private fun initBirth(view: View) {
        val years : ArrayList<Int> = arrayListOf()

        for (year in 2020 downTo 1920)
            years.add(year)

        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, years)
        view.fragment_set_profile_first_actv_birth.setAdapter(adapter)

        view.fragment_set_profile_first_actv_birth.setOnFocusChangeListener{ _, b ->
            if (view.fragment_set_profile_first_error_birth.visibility == View.VISIBLE)
                view.fragment_set_profile_first_error_birth.visibility = View.GONE
        }

        view.fragment_set_profile_first_actv_birth.setOnItemClickListener { adapterView, v, i, l ->
            viewModel.tempAccountDTO.value!!.birth = view.fragment_set_profile_first_actv_birth.text.toString().toInt()
        }

        initViewFromAccountDTO(view)
    }

    private fun submitSetProfile() {
        val setProfileActivity = activity as SetProfileActivity
        setProfileActivity.changeViewPage(false)
    }
}