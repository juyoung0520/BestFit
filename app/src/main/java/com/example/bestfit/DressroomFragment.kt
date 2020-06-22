package com.example.bestfit

import android.app.Activity
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
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_dressroom, container, false)

        setHasOptionsMenu(true)
        initToolbar(fragmentView)

        setTabOfCategory(fragmentView)

        return fragmentView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            initAdapter()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_dressroom, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_dressroom_action_add -> {
                startActivityForResult(Intent(activity, AddItemActivity::class.java), 1)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar(view: View) {
        val mainActivity: MainActivity = activity!! as MainActivity
        mainActivity.setToolbar(view.fragment_dressroom_toolbar)
    }

    private fun setTabOfCategory(view: View) {
        timer(period = 300) {
            if (InitData.initialization) {
                val categoryDTOs = InitData.categoryDTOs

                activity!!.runOnUiThread {
                    for (i in categoryDTOs) {
                        view.fragment_dressroom_tab.addTab(view.fragment_dressroom_tab.newTab())
                    }

                    initAdapter()

                    cancel()
                }
            }
        }
    }

    private fun initAdapter() {
        fragmentView.fragment_dressroom_viewpager.adapter = TabOfCategoryPagerAdapter(childFragmentManager, InitData.categoryDTOs)
        fragmentView.fragment_dressroom_tab.setupWithViewPager(fragmentView.fragment_dressroom_viewpager)
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