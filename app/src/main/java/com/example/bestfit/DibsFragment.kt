package com.example.bestfit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bestfit.viewmodel.DataViewModel
import kotlinx.android.synthetic.main.fragment_dibs.view.*

class DibsFragment : Fragment() {
    private val dataViewModel: DataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        val view = inflater.inflate(R.layout.fragment_dibs, container, false)

        initToolbar(view)
        view.fragment_dibs_viewPager.adapter = ViewPagerAdapter()

        return view
    }

    private fun initToolbar(view: View) {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }

        view.fragment_dibs_toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        view.fragment_dibs_toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
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