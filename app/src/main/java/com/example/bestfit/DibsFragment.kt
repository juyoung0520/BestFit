package com.example.bestfit

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bestfit.viewmodel.DataViewModel
import com.example.bestfit.viewmodel.DibsFragmentViewModel
import com.example.bestfit.viewmodel.DressroomFragmentViewModel
import kotlinx.android.synthetic.main.fragment_dibs.view.*
import kotlinx.android.synthetic.main.fragment_dressroom.view.*

class DibsFragment : Fragment() {
    private val dataViewModel: DataViewModel by activityViewModels()
    private lateinit var viewModel: DibsFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        val view = inflater.inflate(R.layout.fragment_dibs, container, false)

        initViewModel()
        initToolbar(view)
        view.fragment_dibs_viewPager.adapter = ViewPagerAdapter()

        return view
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(DibsFragmentViewModel::class.java)
    }

    private fun initToolbar(view: View, isEditMode: Boolean = false) {
        view.fragment_dibs_toolbar.menu.clear()

        if (isEditMode) {
            viewModel.clearSelectionItems()
            viewModel.lastClickItem = null
        }

        viewModel.setEditMode(isEditMode)

        if (isEditMode) {
            view.fragment_dibs_toolbar.title = "해제할 아이템을 선택하세요"
            view.fragment_dibs_toolbar.setNavigationIcon(R.drawable.ic_close)
            view.fragment_dibs_toolbar.setNavigationOnClickListener {
                dataViewModel.setRemoveState(DataViewModel.REMOVE_CANCEL)
                initToolbar(view)
            }
            view.fragment_dibs_toolbar.inflateMenu(R.menu.menu_edit_mode)
            view.fragment_dibs_toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit_mode_remove -> {
                        dataViewModel.setRemoveState(DataViewModel.REMOVE_START)
                        println("state = ${dataViewModel.getRemoveState()}")
                        initToolbar(view)

                        true
                    }
                    else -> false
                }
            }

            return
        }

        view.fragment_dibs_toolbar.title = "찜한 목록"
        view.fragment_dibs_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        view.fragment_dibs_toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        view.fragment_dibs_toolbar.inflateMenu(R.menu.menu_fragment_dibs)
        view.fragment_dibs_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_fragment_dibs_edit -> {
                    initToolbar(view, true)

                    true
                }
                else -> false
            }
        }
    }

    inner class ViewPagerAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return 1
        }

        override fun createFragment(position: Int): Fragment {
            dataViewModel.getDibsItemDTOs()
            return DressroomCategoryFragment()
        }
    }
}