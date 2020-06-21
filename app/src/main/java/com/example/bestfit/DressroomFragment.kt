package com.example.bestfit

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.bestfit.model.CategoryDTO
import com.example.bestfit.util.InitData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dressroom.view.*
import kotlin.concurrent.timer


class DressroomFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dressroom, container, false)

        setHasOptionsMenu(true)
        initToolbar(view)

        setTabOfCategory(view)

        return view
    }

    private fun initToolbar(view: View) {
        val mainActivity: MainActivity = activity!! as MainActivity
        mainActivity.setToolbar(view.fragment_dressroom_toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_dressroom, menu)
    }

    private fun setTabOfCategory(view: View) {
        timer(period = 300) {
            if (InitData.initialization) {
                val categoryDTOs = InitData.categoryDTOs

                activity!!.runOnUiThread {
                    for (i in categoryDTOs) {
                        view.fragment_dressroom_tab.addTab(view.fragment_dressroom_tab.newTab())
                    }

                    view.fragment_dressroom_viewpager.adapter = TabOfCategoryPagerAdapter(childFragmentManager, categoryDTOs)
                    view.fragment_dressroom_tab.setupWithViewPager(view.fragment_dressroom_viewpager)

                    cancel()
                }
            }
        }
    }

    inner class TabOfCategoryPagerAdapter(fm: FragmentManager, private val categoryDTOs: ArrayList<CategoryDTO>) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            val fragment = DressroomCategoryFragment()

            return fragment
        }

        override fun getCount(): Int {
            return categoryDTOs.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return categoryDTOs[position].name!!
        }
    }
}