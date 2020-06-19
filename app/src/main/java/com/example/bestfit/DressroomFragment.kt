package com.example.bestfit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.bestfit.model.CategoryDTO
import com.example.bestfit.util.InitData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dressroom.view.*


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
        val categories = InitData.categories
        println(categories)
        for (i in categories) {
            view.fragment_dressroom_tab.addTab(view.fragment_dressroom_tab.newTab())
        }

        view.fragment_dressroom_viewpager.adapter = TabOfCategoryPagerAdapter(childFragmentManager, categories)
        view.fragment_dressroom_tab.setupWithViewPager(view.fragment_dressroom_viewpager)
    }

    inner class TabOfCategoryPagerAdapter(fm: FragmentManager, private val categories: ArrayList<String>) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            val fragment = DressroomCategoryFragment()
            return fragment
        }

        override fun getCount(): Int {
            return categories.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return categories[position]
        }
    }
}