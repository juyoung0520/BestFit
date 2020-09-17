package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bestfit.model.AccountDTO
import com.example.bestfit.viewmodel.DataViewModel
import com.example.bestfit.viewmodel.FollowFramgentViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_follow.view.*

class FollowFragment : Fragment() {
    private lateinit var viewModel: FollowFramgentViewModel

    private val args: FollowFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_follow, container, false)

        initViewModel(view)
        initToolbar(view)
        initTapAdapter(view)

        return view
    }

    private fun initViewModel(view: View) {
        viewModel = ViewModelProvider(this).get(FollowFramgentViewModel::class.java)
        viewModel.setAccountDTO(args.accountDTO)

        val accountDTOObserver = Observer<AccountDTO> {
            viewModel.getFollowerAccountDTOs()
            viewModel.getFollowingAccountDTOs()
        }

        viewModel.accountDTO.observe(viewLifecycleOwner, accountDTOObserver)
    }

    private fun initToolbar(view: View) {
        view.fragment_follow_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        view.fragment_follow_toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initTapAdapter(view: View) {
        val tabArray = arrayListOf("팔로워", "팔로잉")

        view.fragment_follow_viewpager.adapter = TabPagerAdapter()
        TabLayoutMediator(view.fragment_follow_tab, view.fragment_follow_viewpager) { tab, position ->
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
                    val fragment = FollowRecyclerViewFragment()
                    val bundle = Bundle()
                    bundle.putString("follow", "er")
                    fragment.arguments = bundle

                    return fragment
                }
                1 -> {
                    val fragment = FollowRecyclerViewFragment()
                    val bundle = Bundle()
                    bundle.putString("follow", "ing")
                    fragment.arguments = bundle

                    return fragment
                }
                else -> Fragment()
            }
        }

    }
}