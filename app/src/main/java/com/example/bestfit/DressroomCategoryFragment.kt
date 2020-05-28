package com.example.bestfit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_dressroom_category.view.*

class DressroomCategoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dressroom_category, container, false)

        setTabOfCategory(view)

        return view
    }

    fun setTabOfCategory(view: View) {
        val category = arrayListOf<String>()
        category.add("가디건")
        category.add("자켓")
        category.add("코트")
        category.add("점퍼")
        category.add("야상")
        category.add("패딩")

        for (title in category)
            view.fragment_dressroom_category_tab.addTab(view.fragment_dressroom_category_tab.newTab())

        view.fragment_dressroom_category_viewpager.adapter = TabOfCategoryPagerAdapter(childFragmentManager, category)
        view.fragment_dressroom_category_tab.setupWithViewPager(view.fragment_dressroom_category_viewpager)
    }

    inner class TabOfCategoryPagerAdapter(fm: FragmentManager, private val catergory: ArrayList<String>) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            println("getItem $position")
            val fragment = DressroomCategoryItemFragment()
            return fragment
        }

        override fun getCount(): Int {
            return catergory.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return catergory[position]
        }
    }
}