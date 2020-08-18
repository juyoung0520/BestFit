package com.example.bestfit

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.util.InitData
import com.example.bestfit.viewmodel.DataViewModel
import com.example.bestfit.viewmodel.DetailFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dressroom.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {
    private val dataViewModel: DataViewModel by activityViewModels()
    private lateinit var viewModel: DetailFragmentViewModel

    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        initViewModel(view)
        initToolbar(view)

        view.fragment_settings_tv_signOut.setOnClickListener {
            auth.signOut()

            requireActivity().startActivity(Intent(requireActivity(), SignInActivity::class.java))
            requireActivity().finish()
        }

        return view
    }

    private fun initViewModel(view: View) {
        viewModel = ViewModelProvider(this).get(DetailFragmentViewModel::class.java)

        if (!viewModel.isInitialized()) {
            viewModel.setInitializedState(true)

            viewModel.getAccountDTO(currentUid)
        }

        val accountDTOObserver = Observer<AccountDTO> { accountDTO ->
            initMenuFragment(view, accountDTO)
        }

        viewModel.accountDTO.observe(viewLifecycleOwner, accountDTOObserver)

        val dataInitObserver = Observer<Boolean> { isInit ->
            println("hihi $isInit")
        }

        dataViewModel.isInitialized.observe(viewLifecycleOwner, dataInitObserver)
    }

    private fun initToolbar(view: View) {
        view.fragment_settings_toolbar.inflateMenu(R.menu.menu_fragment_settings)
        view.fragment_settings_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_fragment_settings_modify -> {

                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun initMenuFragment(view: View, accountDTO: AccountDTO) {
        if (accountDTO.photo.isNullOrEmpty())
            view.fragment_settings_iv_profile.setImageResource(R.drawable.ic_profile_photo)
        else
            Glide.with(view).load(accountDTO.photo).apply(RequestOptions().centerCrop()).into(view.fragment_settings_iv_profile)

          view.fragment_settings_tv_nickname.text = accountDTO.nickname
//                view.fragment_settings_tv_user_height.text = accountDTO.height.toString() + " cm"
//                view.fragment_settings_tv_user_weight.text = accountDTO.weight.toString() + " kg"
          view.fragment_settings_tv_user_size.text = accountDTO.height.toString() + " cm / " + accountDTO.weight.toString() + " kg"

        val top = dataViewModel.getSizeString("01", accountDTO.topId!!)
        val bottom = dataViewModel.getSizeString("03", accountDTO.bottomId!!)
        val shoes = dataViewModel.getSizeString("04", accountDTO.shoesId!!)
        view.fragment_settings_tv_user_detail_size.text = "TOP " + top + " / BOTTOM "+ bottom + " / SHOES " + shoes

        view.fragment_settings_tv_message.text = accountDTO.message
    }
}