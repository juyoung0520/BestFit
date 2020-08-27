package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.model.ItemDTO
import com.example.bestfit.util.InitData
import com.example.bestfit.viewmodel.AccountFragmentViewModel
import com.example.bestfit.viewmodel.DressroomFragmentViewModel
import com.example.bestfit.viewmodel.SettingsFragmentViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlin.math.abs

class AccountFragment : Fragment() {
    private lateinit var viewModel: AccountFragmentViewModel

    private val args: AccountFragmentArgs by navArgs()
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        initViewModel(view)
        initToolbar(view)
        initTabAdapter(view)

        return view
    }

    private fun initViewModel(view: View) {
        viewModel = ViewModelProvider(this, AccountFragmentViewModel.Factory(args.uid)).get(AccountFragmentViewModel::class.java)

        val accountDTOObserver = Observer<AccountDTO> { accountDTO ->
            initAccountFragment(view, accountDTO)
        }

        viewModel.accountDTO.observe(viewLifecycleOwner, accountDTOObserver)
    }

    private fun initToolbar(view: View) {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }

        view.fragment_account_appbarlayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
            if (abs(p1) - p0.totalScrollRange == 0) {
                if (view.fragment_account_toolbar_title.visibility == View.GONE)
                    view.fragment_account_toolbar_title.visibility = View.VISIBLE
            }
            else {
                if (view.fragment_account_toolbar_title.visibility == View.VISIBLE)
                    view.fragment_account_toolbar_title.visibility = View.GONE
            }
        })

        view.fragment_account_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        view.fragment_account_toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initTabAdapter(view: View) {
        val tabArray = arrayListOf("드레스룸", "게시글")

        view.fragment_account_viewpager.adapter = TabPagerAdapter()
        TabLayoutMediator(view.fragment_account_tab, view.fragment_account_viewpager) { tab, position ->
            tab.text = tabArray[position]
        }.attach()
    }

    inner class TabPagerAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    val fragment = DressroomCategoryFragment()
                    val bundle = Bundle()

                    bundle.putInt("position", -1)

                    fragment.arguments = bundle
                    fragment
                }
                1 -> {
                    Fragment()
                }
                else -> Fragment()
            }
        }
    }

    private fun initAccountFragment(view: View, accountDTO: AccountDTO) {
        if (accountDTO.photo.isNullOrEmpty())
            view.fragment_account_iv_profile.setImageResource(R.drawable.ic_profile_photo)
        else
            Glide.with(view).load(accountDTO.photo).apply(RequestOptions().centerCrop()).into(view.fragment_settings_iv_profile)

        view.fragment_account_toolbar_title.text = "${accountDTO.nickname}님의 프로필"
        view.fragment_account_tv_nickname.text = accountDTO.nickname
        view.fragment_account_tv_user_size.text = "${accountDTO.height} cm / ${accountDTO.weight} kg"

        val top = InitData.getSizeString("01", accountDTO.topId!!)
        val bottom = InitData.getSizeString("03", accountDTO.bottomId!!)
        val shoes = InitData.getSizeString("04", accountDTO.shoesId!!)
        view.fragment_account_tv_user_detail_size.text = "TOP " + top + " / BOTTOM "+ bottom + " / SHOES " + shoes

        view.fragment_account_tv_message.text = accountDTO.message
    }
}