package com.example.bestfit

import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.viewmodel.SetProfileActivityViewModel
import com.google.android.gms.dynamic.IFragmentWrapper
import kotlinx.android.synthetic.main.fragment_set_profile_third.view.*

class SetProfileThirdFragment : Fragment() {
    private lateinit var viewModel: SetProfileActivityViewModel

    lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_set_profile_third, container, false)

        initViewModel()
        fragmentView.fragment_set_profile_third_text_message.doAfterTextChanged {
            viewModel.tempAccountDTO.value!!.message = fragmentView.fragment_set_profile_third_text_message.text.toString()
        }

        return fragmentView
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity()).get(SetProfileActivityViewModel::class.java)

        val tempAccountDTOObserver = Observer<AccountDTO> { tempAccountDTO ->
            initViewFromAccountDTO()
        }

        viewModel.tempAccountDTO.observe(viewLifecycleOwner, tempAccountDTOObserver)
    }

    private fun initViewFromAccountDTO() {
        val accountDTO = viewModel.getTempAccountDTO()!!
        if (accountDTO.message != null)
            fragmentView.fragment_set_profile_third_text_message.setText(accountDTO.message)
    }

}