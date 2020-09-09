package com.example.bestfit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.util.InitData
import com.example.bestfit.viewmodel.DataViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*

class MyPageFragment : Fragment() {
    private val dataViewModel: DataViewModel by activityViewModels()

    private val auth = FirebaseAuth.getInstance()

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val accountDTO = result.data!!.getParcelableExtra<AccountDTO>("accountDTO")!!
            dataViewModel.setAccountDTO(accountDTO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)

        initViewModel(view)
        initToolbar(view)

        view.fragment_mypage_tv_signOut.setOnClickListener {
            auth.signOut()

            requireActivity().startActivity(Intent(requireActivity(), SignInActivity::class.java))
            requireActivity().finish()
        }

        return view
    }

    private fun initViewModel(view: View) {
        val accountDTOObserver = Observer<AccountDTO> { accountDTO ->
            initMyPageFragment(view, accountDTO)
        }

        dataViewModel.accountDTO.observe(viewLifecycleOwner, accountDTOObserver)
    }

    private fun initToolbar(view: View) {
        view.fragment_mypage_toolbar.inflateMenu(R.menu.menu_fragment_mypage)
        view.fragment_mypage_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_fragment_mypage_modify -> {
                    val intent = Intent(context, SetProfileActivity::class.java).putExtra("accountDTO", dataViewModel.accountDTO.value)
                    startForResult.launch(intent)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun initMyPageFragment(view: View, accountDTO: AccountDTO) {
        if (accountDTO.photo.isNullOrEmpty())
            view.fragment_mypage_iv_profile.setImageResource(R.drawable.ic_profile_120)
        else
            Glide.with(view).load(accountDTO.photo).apply(RequestOptions().centerCrop()).into(view.fragment_mypage_iv_profile)

        view.fragment_mypage_tv_nickname.text = accountDTO.nickname
        view.fragment_mypage_tv_user_size.text = "${accountDTO.height} cm . ${accountDTO.weight} kg"
        view.fragment_mypage_tv_user_info.text = if (accountDTO.sex!!) "남자" else "여자" //나이 추가..
        //view.fragment_account_tv_items.text = 아이템 수 ...
        view.fragment_mypage_tv_follower.text = accountDTO.follower!!.size.toString()
        view.fragment_mypage_tv_following.text = accountDTO.following!!.size.toString()
        view.fragment_mypage_tv_message.text = accountDTO.message

        view.fragment_mypage_layout_dibs.setOnClickListener {
            val action = MyPageFragmentDirections.actionToDibsFragment()
            findNavController().navigate(action)
        }
    }

}