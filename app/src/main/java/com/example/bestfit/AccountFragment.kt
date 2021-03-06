package com.example.bestfit

import android.app.AppComponentFactory
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.util.InitData
import com.example.bestfit.viewmodel.AccountFragmentViewModel
import com.example.bestfit.viewmodel.DataViewModel
import com.example.bestfit.viewmodel.SingleLiveEvent
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*
import kotlin.math.abs

class AccountFragment : Fragment() {
    private val dataViewModel: DataViewModel by activityViewModels()
    private lateinit var viewModel: AccountFragmentViewModel

    private val args: AccountFragmentArgs by navArgs()
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.currentUser!!.uid

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

        if (args.uid != currentUid)
            initFollowingObserver(args.uid)

        initAccountDTOObserver(view)

    }

    private fun initAccountDTOObserver(view: View) {
        val accountDTOObserver = Observer<AccountDTO> { accountDTO ->
            initAccountFragment(view, accountDTO)
        }

        if (args.uid == currentUid)
            dataViewModel.accountDTO.observe(viewLifecycleOwner, accountDTOObserver)
        else
            viewModel.accountDTO.observe(viewLifecycleOwner, accountDTOObserver)
    }

    private fun initFollowingObserver(uid: String) {
        val followingAccountDTOsObserver = Observer<ArrayList<AccountDTO>> {
            val accountDTO = dataViewModel.getAccountDTO()

            if (accountDTO.following!!.contains(uid)) {
                viewModel.addFollower(accountDTO)
            } else{
                viewModel.removeFollower(accountDTO)
            }

        }
        dataViewModel.followingAccountDTOs.observe(viewLifecycleOwner, followingAccountDTOsObserver)
    }

    private fun initToolbar(view: View) {
        view.fragment_account_appbarlayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
            if (abs(p1) - p0.totalScrollRange == 0) {
                if (view.fragment_account_toolbar_title.visibility == View.GONE) {
                    view.fragment_account_toolbar_title.visibility = View.VISIBLE
                    viewModel.setExpanded(false)
                }
            }
            else {
                if (view.fragment_account_toolbar_title.visibility == View.VISIBLE) {
                    view.fragment_account_toolbar_title.visibility = View.GONE
                    viewModel.setExpanded(true)
                }
            }
        })

        view.fragment_account_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        view.fragment_account_toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        if (!viewModel.isExpanded())
            view.fragment_account_appbarlayout.setExpanded(false)
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

                    bundle.putBoolean("isMyAccount", args.uid == currentUid)

                    fragment.arguments = bundle
                    return fragment
                }
                1 -> Fragment()
                else -> Fragment()
            }
        }
    }

    private fun checkFollowState(view: View, isFollowing: Boolean, accountDTO: AccountDTO) {
        if (!accountDTO.follower.isNullOrEmpty()) {
            view.fragment_account_btn_follow.isChecked = isFollowing
            view.fragment_account_btn_follow.setText(if (isFollowing) "팔로잉" else "팔로우")
       }
    }

    private fun initAccountFragment(view: View, accountDTO: AccountDTO) {
        if (accountDTO.photo.isNullOrEmpty())
            view.fragment_account_iv_profile.setImageResource(R.drawable.ic_profile_120)
        else
            Glide.with(view).load(accountDTO.photo).apply(RequestOptions().centerCrop()).into(view.fragment_account_iv_profile)

        view.fragment_account_toolbar_title.text = "${accountDTO.nickname}님의 프로필"
        view.fragment_account_tv_nickname.text = accountDTO.nickname
        view.fragment_account_tv_user_size.text = "${accountDTO.height} cm . ${accountDTO.weight} kg"
        view.fragment_account_tv_message.text = accountDTO.message
        view.fragment_account_tv_items.text = accountDTO.items!!.size.toString()
        view.fragment_account_tv_follower.text = accountDTO.follower!!.size.toString()
        view.fragment_account_tv_following.text = accountDTO.following!!.size.toString()
        view.fragment_account_tv_user_info.text = if (accountDTO.sex!!) "남자" else "여자" //나이 추가..

        if (args.uid != currentUid) {
            view.fragment_account_btn_follow.visibility = View.VISIBLE

            checkFollowState(view, accountDTO.follower!!.contains(currentUid), accountDTO)

            view.fragment_account_btn_follow.setOnClickListener {

                if (view.fragment_account_btn_follow.isChecked) {
                    println("btn"+view.fragment_account_btn_follow.isChecked)
                    viewModel.addFollower(args.uid)
                    dataViewModel.addFollowing(args.uid, accountDTO)
                    view.fragment_account_btn_follow.setText("팔로잉")
                } else {
                    println("btn"+view.fragment_account_btn_follow.isChecked)
                    viewModel.removeFollower(args.uid)
                    dataViewModel.removeFollowing(args.uid, accountDTO)
                    view.fragment_account_btn_follow.setText("팔로우")
                }

            }
        } else {
            view.fragment_account_btn_follow.visibility = View.GONE
        }

        view.fragment_account_layout_follower.setOnClickListener {
            val action = AccountFragmentDirections.actionToFollowFragment(accountDTO)
            findNavController().navigate(action)
        }

        view.fragment_account_layout_following.setOnClickListener {
            val action = AccountFragmentDirections.actionToFollowFragment(accountDTO)
            findNavController().navigate(action)
        }
    }
}